package emortal.holeymoley.game

import dev.emortal.immortal.config.GameOptions
import dev.emortal.immortal.game.GameState
import dev.emortal.immortal.game.PvpGame
import dev.emortal.immortal.util.MinestomRunnable
import dev.emortal.immortal.util.reset
import emortal.holeymoley.blocks.SingleChestHandler
import emortal.holeymoley.event.Event
import emortal.holeymoley.item.*
import emortal.holeymoley.map.MapCreator
import emortal.holeymoley.util.SphereUtil
import io.github.bloepiloepi.pvp.damage.CustomDamageType
import io.github.bloepiloepi.pvp.damage.CustomEntityDamage
import io.github.bloepiloepi.pvp.events.EntityPreDeathEvent
import io.github.bloepiloepi.pvp.events.FinalAttackEvent
import io.github.bloepiloepi.pvp.events.FinalDamageEvent
import io.github.bloepiloepi.pvp.events.PlayerExhaustEvent
import io.github.bloepiloepi.pvp.explosion.TntEntity
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.other.PrimedTntMeta
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerStartDiggingEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.inventory.Inventory
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.minestom.server.scoreboard.Sidebar
import net.minestom.server.sound.SoundEvent
import world.cepi.kstom.adventure.asMini
import world.cepi.kstom.event.listenOnly
import world.cepi.kstom.util.playSound
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

class HoleyMoleyGame(gameOptions: GameOptions) : PvpGame(gameOptions) {

    override var spawnPosition = Pos(0.5, 60.0, 0.5)


    var uncoveredChests: MutableSet<Block> = ConcurrentHashMap.newKeySet()

    val blocksPlacedByPlayer: MutableSet<Point> = ConcurrentHashMap.newKeySet()

    init {
        eventNode.listenOnly<FinalDamageEvent> {
            if (damageType == CustomDamageType.FALL && gameState != GameState.PLAYING) isCancelled = true
        }

        eventNode.listenOnly<FinalAttackEvent> {
            if (gameState != GameState.PLAYING) isCancelled = true
        }
    }

    override fun playerJoin(player: Player) {
    }

    override fun playerLeave(player: Player) {
        player.cleanup()
    }

    override fun gameStarted() {

        scoreboard?.createLine(
            Sidebar.ScoreboardLine(
                "playersLeft",
                Component.text()
                    .append(Component.text("Players left: ", NamedTextColor.GRAY))
                    .append(Component.text(players.size, NamedTextColor.GOLD))
                    .build(),
                1
            )
        )
        scoreboard?.removeLine("infoLine")

        // Regular glow runnable
        object : MinestomRunnable(coroutineScope = coroutineScope, delay = Duration.ofSeconds(13), repeat = Duration.ofSeconds(40)) {
            override suspend fun run() {

                object : MinestomRunnable(coroutineScope = coroutineScope, delay = Duration.ofSeconds(4), repeat = Duration.ofSeconds(1), iterations = 3) {
                    override suspend fun run() {
                        val currentIter = currentIteration.get()

                        sendMessage(
                            Component.text()
                                .append(Component.text("⚠", NamedTextColor.RED))
                                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                                .append(Component.text("Players start glowing in ", NamedTextColor.GRAY))
                                .append(Component.text(iterations - currentIter, NamedTextColor.RED))
                                .append(Component.text(" ${if ((iterations - currentIter) == 1) "second" else "seconds"}", NamedTextColor.GRAY))
                        )

                        if (currentIter == 0) {
                            playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1f, 0.5f))
                        }

                        playSound(Sound.sound(SoundEvent.BLOCK_WOODEN_BUTTON_CLICK_OFF, Sound.Source.MASTER, 1f, 1.2f))
                    }

                    override fun cancelled() {
                        players.filter { it.gameMode == GameMode.SURVIVAL }.forEach {
                            it.isGlowing = true
                        }

                        sendMessage(
                            Component.text()
                                .append(Component.text("⚠", NamedTextColor.RED))
                                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                                .append(Component.text("Players are now glowing!", NamedTextColor.RED))
                        )

                        playSound(Sound.sound(SoundEvent.BLOCK_ANVIL_LAND, Sound.Source.MASTER, 0.7f, 1f))

                        object : MinestomRunnable(coroutineScope = coroutineScope, delay = Duration.ofSeconds(4)) {
                            override suspend fun run() {
                                players.filter { it.gameMode == GameMode.SURVIVAL }.forEach {
                                    it.isGlowing = false
                                }

                                sendMessage(
                                    Component.text()
                                        .append(Component.text("⚠", NamedTextColor.RED))
                                        .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                                        .append(Component.text("Players are no longer glowing!", NamedTextColor.GREEN))
                                )

                                playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1f, 2f))
                            }
                        }
                    }
                }

            }
        }

        // Random event runnable
        object : MinestomRunnable(coroutineScope = coroutineScope, delay = Duration.ofSeconds(30), repeat = Duration.ofSeconds(42)) {
            override suspend fun run() {
                val randomEvent = Event.eventList.random()
                randomEvent.performEvent(this@HoleyMoleyGame)

                showTitle(
                    Title.title(
                        "<rainbow><bold>${randomEvent.name}".asMini(),
                        Component.empty(),
                        Title.Times.times(Duration.ZERO, Duration.ofSeconds(3), Duration.ofSeconds(1))
                    )
                )

                playSound(Sound.sound(SoundEvent.ENTITY_ENDER_DRAGON_GROWL, Sound.Source.MASTER, 0.7f,2f))

                sendMessage(
                    Component.text()
                        .append(Component.text("★", NamedTextColor.GOLD))
                        .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("New event! ", NamedTextColor.GRAY))
                        .append(Component.text(randomEvent.name, NamedTextColor.GOLD))
                )
            }
        }

        players.forEach { respawn(it) }
    }

    override fun gameDestroyed() {

        players.forEach {
            it.cleanup()
        }
    }

    override fun respawn(player: Player) {
        player.addEffect(Potion(PotionEffect.NIGHT_VISION, 0, 32767))
        player.inventory.setItemStack(1, Shovel.createItemStack())
        player.inventory.setItemStack(0, WoodenSword.createItemStack())
        //player.inventory.helmet = LeatherHelmet.createItemStack()
        player.inventory.chestplate = LeatherChestplate.createItemStack()
        //player.inventory.leggings = LeatherLeggings.createItemStack()
        //player.inventory.boots = LeatherBoots.createItemStack()
        //player.inventory.addItemStack(RegenPotion.createItemStack())
        player.inventory.setItemStack(3, ItemStack.of(Material.TNT))

        player.canBeHit = true

        player.gameMode = GameMode.SURVIVAL

        var isValid = false
        var lastPos = Pos.ZERO
        val random = ThreadLocalRandom.current()
        while (!isValid) {
            val x = random.nextInt(1, instance.getTag(MapCreator.mapSizeTag)!! - 1)
            val y = random.nextInt(2, instance.getTag(MapCreator.mapSizeTag)!! - 3)
            val z = random.nextInt(1, instance.getTag(MapCreator.mapSizeTag)!! - 1)

            lastPos = Pos(x.toDouble(), y.toDouble(), z.toDouble())

            isValid = true
        }

        instance.setBlock(lastPos, Block.AIR)
        instance.setBlock(lastPos.add(0.0, 1.0, 0.0), Block.AIR)
        player.teleport(lastPos.add(0.5, 0.0, 0.5))
    }

    override fun playerDied(player: Player, killer: Entity?) {
        if (gameState == GameState.ENDING) return

        player.inventory.clear()
        player.heal()
        player.reset()
        player.gameMode = GameMode.SPECTATOR


        if (killer is Player) {
            player.showTitle(
                Title.title(
                    Component.text("YOU DIED", NamedTextColor.RED, TextDecoration.BOLD),
                    Component.text()
                        .append(Component.text("Killed by ", NamedTextColor.GRAY))
                        .append(Component.text(killer.username)).build(),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))
                )
            )

            sendMessage(
                Component.text()
                    .append(Component.text("☠", NamedTextColor.RED))
                    .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(player.username, NamedTextColor.RED))
                    .append(Component.text(" was slain by ", NamedTextColor.GRAY))
                    .append(Component.text(killer.username, NamedTextColor.WHITE))
            )

        } else {
            player.showTitle(
                Title.title(
                    Component.text("YOU DIED", NamedTextColor.RED, TextDecoration.BOLD),
                    Component.empty(),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))
                )
            )

            sendMessage(
                Component.text()
                    .append(Component.text("☠", NamedTextColor.RED))
                    .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(player.username, NamedTextColor.RED))
                    .append(Component.text(" died", NamedTextColor.GRAY))
            )
        }

        playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1f, 1f), Sound.Emitter.self())

        player.addEffect(Potion(PotionEffect.NIGHT_VISION, 2, Short.MAX_VALUE.toInt()))

        val alivePlayers = players.filter { it.gameMode == GameMode.SURVIVAL }

        scoreboard?.updateLineContent(
            "playersLeft",
            Component.text()
                .append(Component.text("Players left: ", NamedTextColor.GRAY))
                .append(Component.text(alivePlayers.size, NamedTextColor.GOLD))
                .build()
        )

        if (alivePlayers.size == 1) {
            victory(alivePlayers.first())
        }
    }

    override fun registerEvents() {

        eventNode.listenOnly<PlayerExhaustEvent> {
            this.amount = amount / 1.5f
        }

        eventNode.listenOnly<PlayerBlockPlaceEvent> {
            if (this.block == Block.REDSTONE_BLOCK) {
                val tntEntity = TntEntity(player)
                val tntMeta = tntEntity.entityMeta as PrimedTntMeta
                tntMeta.fuseTime = 20
                consumeBlock(true)
                isCancelled = true
            }
        }

        eventNode.listenOnly<EntityPreDeathEvent> {
            if (entity !is Player) return@listenOnly

            val player = entity as Player
            this.isCancelled = true

            kill(player, (damageType as CustomEntityDamage).entity)

        }

        // Handles instant block breaking
        val breakableBlocks = listOf<Block>(
            Block.DIRT,

        ) + SphereUtil.rainbowBlocks
        eventNode.listenOnly<PlayerStartDiggingEvent> {
            if (player.inventory.itemInMainHand.material() == Shovel.createItemStack().material() && breakableBlocks.contains(block) && player.isOnGround) {
                instance.breakBlock(player, blockPosition)

                // Plays block break sound for other players
                instance.players
                    .filter { it != player }
                    .forEach {
                        it.playSound(
                            Sound.sound(SoundEvent.BLOCK_GRAVEL_BREAK, Sound.Source.BLOCK, 1f, 0.8f),
                            blockPosition
                        )
                    }
            }
        }

        eventNode.listenOnly<PlayerBlockInteractEvent> {
            if (player.gameMode != GameMode.SURVIVAL) return@listenOnly
            if (block.compare(Block.CHEST)) {
                val inventory = (block.handler() as SingleChestHandler).inventory
                player.openInventory(inventory)
                instance.playSound(Sound.sound(SoundEvent.BLOCK_CHEST_OPEN, Sound.Source.BLOCK, 1f, 1f), blockPosition)
            }
        }

        eventNode.listenOnly<PlayerBlockPlaceEvent> {
            blocksPlacedByPlayer.add(blockPosition)
        }

        eventNode.listenOnly<PlayerBlockBreakEvent> {
            if (block == Block.BEDROCK) {
                isCancelled = true
                return@listenOnly
            }
            if (block.compare(Block.CHEST)) return@listenOnly

            if (player.inventory.count(Material.DIRT) < 64) {
                player.playSound(
                    Sound.sound(SoundEvent.ENTITY_ITEM_PICKUP, Sound.Source.PLAYER, 0.25f, 1f),
                    player.position
                )
                player.inventory.addItemStack(ItemStack.of(Material.DIRT))
            }

            if (!blocksPlacedByPlayer.contains(blockPosition)) {
                if (ThreadLocalRandom.current().nextDouble() < 0.005) {
                    player.sendMessage(
                        Component.text()
                            .append(Component.text("★", NamedTextColor.GOLD))
                            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("You uncovered a ", NamedTextColor.GRAY))
                            .append(Component.text("CHEST", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
                            .append(Component.text("!", NamedTextColor.GRAY))
                    )

                    player.playSound(
                        Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1f, 1.5f),
                        blockPosition
                    )

                    val singleChest = SingleChestHandler.create()
                    val handler = singleChest.handler() as SingleChestHandler

                    addRandomChestItems(handler.inventory)

                    isCancelled = true
                    instance.setBlock(blockPosition, singleChest)

                    uncoveredChests.add(singleChest)
                }
            }
        }
    }

    fun addRandomChestItems(inventory: Inventory) {
        val alreadyHadItems = mutableSetOf<Item>()
        for (i in 0..7) {
            val newItem = Item.random()
            if (alreadyHadItems.contains(newItem)) continue
            alreadyHadItems.add(newItem)

            inventory.addRandomly(newItem.createItemStack())
        }
    }

    override fun instanceCreate(): Instance {
        return MapCreator.create(45)
    }

}