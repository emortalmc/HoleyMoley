package emortal.holeymoley.game

import dev.emortal.immortal.game.GameState
import dev.emortal.immortal.game.PvpGame
import dev.emortal.immortal.util.MinestomRunnable
import dev.emortal.immortal.util.reset
import emortal.holeymoley.blocks.SingleChestHandler
import emortal.holeymoley.event.Event
import emortal.holeymoley.item.Item
import emortal.holeymoley.item.Item.Companion.randomItem
import emortal.holeymoley.item.addRandomly
import emortal.holeymoley.item.count
import emortal.holeymoley.map.MapCreator
import emortal.holeymoley.util.SphereUtil
import io.github.bloepiloepi.pvp.PvpExtension
import io.github.bloepiloepi.pvp.damage.CustomDamageType
import io.github.bloepiloepi.pvp.damage.CustomEntityDamage
import io.github.bloepiloepi.pvp.events.*
import io.github.bloepiloepi.pvp.explosion.ExplosionListener
import io.github.bloepiloepi.pvp.explosion.TntEntity
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.ItemEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerStartDiggingEvent
import net.minestom.server.event.trait.InstanceEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.inventory.Inventory
import net.minestom.server.item.Enchantment
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.minestom.server.scoreboard.Sidebar
import net.minestom.server.sound.SoundEvent
import net.minestom.server.utils.time.TimeUnit
import world.cepi.kstom.adventure.asMini
import world.cepi.kstom.event.listenOnly
import world.cepi.kstom.util.playSound
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class HoleyMoleyGame : PvpGame() {

    override val maxPlayers: Int = 15
    override val minPlayers: Int = 2
    override val countdownSeconds: Int = 15
    override val canJoinDuringGame: Boolean = false
    override val showScoreboard: Boolean = false
    override val showsJoinLeaveMessages: Boolean = true
    override val allowsSpectators: Boolean = false

    companion object {

        var spawnPosition = Pos(0.5, 60.0, 0.5)
    }

    override fun getSpawnPosition(player: Player, spectator: Boolean): Pos = spawnPosition

    var uncoveredChests: MutableSet<Block> = ConcurrentHashMap.newKeySet()
    val blocksPlacedByPlayer: MutableSet<Point> = ConcurrentHashMap.newKeySet()

    // events
    var shovelBreakRadius = 1
    var minesweeper = false


    override fun gameCreated() {
        val eventNode = instance!!.eventNode()

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
        object : MinestomRunnable(delay = Duration.ofSeconds(13), repeat = Duration.ofSeconds(40), group = runnableGroup) {
            override fun run() {

                object : MinestomRunnable(delay = Duration.ofSeconds(4), repeat = Duration.ofSeconds(1), iterations = 3, group = runnableGroup) {
                    override fun run() {
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

                        object : MinestomRunnable(delay = Duration.ofSeconds(5), group = runnableGroup) {
                            override fun run() {
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
        object : MinestomRunnable(delay = Duration.ofSeconds(30), repeat = Duration.ofSeconds(42), group = runnableGroup) {
            override fun run() {
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

    override fun gameEnded() {

        players.forEach {
            it.stopSpectating()
            it.cleanup()
        }
    }

    override fun respawn(player: Player) {
        player.reset()
        player.addEffect(Potion(PotionEffect.NIGHT_VISION, 0, 32767))

        player.inventory.setItemStack(1, ItemStack.builder(Material.STONE_SHOVEL).meta { it.enchantment(Enchantment.EFFICIENCY, 5) }.build())
        player.inventory.setItemStack(0, ItemStack.of(Material.WOODEN_SWORD))
        player.inventory.itemInOffHand = ItemStack.of(Material.SHIELD)
        player.inventory.chestplate = ItemStack.of(Material.LEATHER_CHESTPLATE)

        player.canBeHit = true

        player.gameMode = GameMode.SURVIVAL

        var isValid = false
        var lastPos = Pos.ZERO
        val random = ThreadLocalRandom.current()
        while (!isValid) {
            val x = random.nextInt(1, instance!!.getTag(MapCreator.mapSizeTag)!! - 1)
            val y = random.nextInt(2, instance!!.getTag(MapCreator.mapSizeTag)!! - 3)
            val z = random.nextInt(1, instance!!.getTag(MapCreator.mapSizeTag)!! - 1)

            lastPos = Pos(x.toDouble(), y.toDouble(), z.toDouble())

            isValid = true
        }

        instance!!.setBlock(lastPos, Block.AIR)
        instance!!.setBlock(lastPos.add(0.0, 1.0, 0.0), Block.AIR)
        player.teleport(lastPos.add(0.5, 0.0, 0.5))
    }

    override fun playerDied(player: Player, killer: Entity?) {
        if (gameState == GameState.ENDING) return

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

        val rand = ThreadLocalRandom.current()
        player.inventory.itemStacks.forEach {
            val angle = rand.nextDouble(PI * 2)
            val strength = rand.nextDouble(3.0, 6.0)
            val x = cos(angle) * strength
            val z = sin(angle) * strength

            val itemEntity = ItemEntity(it)
            itemEntity.setPickupDelay(500, TimeUnit.MILLISECOND)
            itemEntity.velocity = Vec(x, rand.nextDouble(3.0, 7.0), z)
            itemEntity.setInstance(player.instance!!, player.position.add(0.0, 1.5, 0.0))
        }

        player.inventory.clear()

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

    override fun registerEvents(eventNode: EventNode<InstanceEvent>) {

        eventNode.addChild(PvpExtension.events())

        eventNode.listenOnly<PlayerSpectateEvent> {
            isCancelled = true
        }


        eventNode.listenOnly<PlayerExhaustEvent> {
            this.amount = amount / 1.5f
        }

        eventNode.listenOnly<EntityPreDeathEvent> {
            if (entity !is Player) return@listenOnly

            val player = entity as Player
            this.isCancelled = true

            if (damageType is CustomEntityDamage) kill(player, (damageType as CustomEntityDamage).entity)
            else kill(player)

        }

        // Handles instant block breaking
        val breakableBlocks = listOf<Block>(
            Block.DIRT,

        ) + SphereUtil.rainbowBlocks
        eventNode.listenOnly<PlayerStartDiggingEvent> {
            if (player.inventory.itemInMainHand.material() == Material.STONE_SHOVEL && breakableBlocks.contains(block) && player.isOnGround) {
                if (shovelBreakRadius == 1) {
                    instance.breakBlock(player, blockPosition)
                } else {
                    for (x in -1..1) {
                        for (y in -1..1) {
                            for (z in -1..1) {
                                val pos = blockPosition.add(x.toDouble(), y.toDouble(), z.toDouble())
                                if (instance.getBlock(pos).compare(Block.CHEST)) continue
                                instance.breakBlock(player, pos)
                            }
                        }
                    }
                }

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
            if (block.compare(Block.TNT)) {

                isCancelled = true
                ExplosionListener.primeTnt(instance, blockPosition, player, 50)
                player.itemInMainHand = if (player.itemInMainHand.amount() == 1) ItemStack.AIR else  player.itemInMainHand.withAmount(player.itemInMainHand.amount() - 1)

                return@listenOnly
            }
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

            // replace some blocks broken with TNT
            if (minesweeper) {
                if (ThreadLocalRandom.current().nextDouble() < 0.1) {
                    val tnt = TntEntity(null)
                    tnt.setInstance(instance, blockPosition.add(0.5, 0.0, 0.5))
                }
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
            val newItem = randomItem()
            if (alreadyHadItems.contains(newItem)) continue
            alreadyHadItems.add(newItem)

            inventory.addRandomly(newItem.createItemStack())
        }
    }

    override fun instanceCreate(): CompletableFuture<Instance> {
        return CompletableFuture.completedFuture(MapCreator.create(52))
    }

}