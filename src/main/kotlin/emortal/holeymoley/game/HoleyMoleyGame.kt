package emortal.holeymoley.game

import emortal.holeymoley.blocks.SingleChestHandler
import emortal.holeymoley.event.Event
import emortal.holeymoley.item.*
import emortal.holeymoley.item.Item.Companion.getItem
import emortal.holeymoley.item.Item.Companion.heldItem
import emortal.holeymoley.item.ItemUtil.addRandomly
import emortal.holeymoley.item.ItemUtil.count
import emortal.holeymoley.map.MapCreator
import emortal.holeymoley.util.collidingEntities
import emortal.immortal.game.Game
import emortal.immortal.game.GameOptions
import emortal.immortal.game.GameState
import emortal.immortal.util.takeKnockback
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.minestom.server.attribute.Attribute
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import net.minestom.server.entity.damage.EntityDamage
import net.minestom.server.entity.metadata.item.ThrownPotionMeta
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.entity.EntityDamageEvent
import net.minestom.server.event.entity.EntityTickEvent
import net.minestom.server.event.player.*
import net.minestom.server.instance.block.Block
import net.minestom.server.inventory.Inventory
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.minestom.server.sound.SoundEvent
import net.minestom.server.timer.Task
import world.cepi.kstom.Manager
import world.cepi.kstom.adventure.sendMiniMessage
import world.cepi.kstom.event.listenOnly
import world.cepi.kstom.util.eyePosition
import world.cepi.kstom.util.playSound
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.*

class HoleyMoleyGame(gameOptions: GameOptions) : Game(gameOptions) {

    var eventLoopTask: Task? = null

    val maxChests = 0.01 * (instance.getTag(MapCreator.mapSizeTag)!!).toDouble().pow(3)
    var uncoveredChests = mutableListOf<Block>()

    val blocksPlacedByPlayer = mutableListOf<Point>()

    val mini = MiniMessage.get()

    override fun playerJoin(player: Player) {
        player.setInstance(instance)
    }

    override fun playerLeave(player: Player) {
        MolePlayer.removeFrom(player)
    }

    override fun start() {

        eventLoopTask = Manager.scheduler.buildTask {
            println("event")
            val randomEvent = Event.eventList.random()
            randomEvent.performEvent(this)


            playerAudience.showTitle(
                Title.title(
                    mini.parse("<rainbow><bold>${randomEvent.name}"),
                    Component.empty(),
                    Title.Times.of(Duration.ZERO, Duration.ofSeconds(3), Duration.ofSeconds(1))
                )
            )

            playerAudience.sendMiniMessage(" <gold>★</gold> <dark_gray>|</dark_gray> <gray>New event! <rainbow>${randomEvent.name}")

        }.repeat(Duration.ofSeconds(30)).delay(Duration.ofSeconds(30)).schedule()

        players.forEach { respawn(it) }
    }

    override fun postDestroy() {

        eventLoopTask?.cancel()

        players.forEach { MolePlayer.removeFrom(it) }
    }

    override fun respawn(player: Player) {
        player.addEffect(Potion(PotionEffect.NIGHT_VISION, 2, 32767))
        player.inventory.setItemStack(1, Shovel.createItemStack())
        player.inventory.setItemStack(0, WoodenSword.createItemStack())
        player.inventory.helmet = LeatherHelmet.createItemStack()
        player.inventory.chestplate = LeatherChestplate.createItemStack()
        player.inventory.leggings = LeatherLeggings.createItemStack()
        player.inventory.boots = LeatherBoots.createItemStack()
        player.inventory.addItemStack(RegenPotion.createItemStack())

        player.gameMode = GameMode.SURVIVAL

        var isValid = false
        var lastPos = Pos.ZERO
        val random = ThreadLocalRandom.current()
        while (!isValid) {
            val x = random.nextInt(1, instance.getTag(MapCreator.mapSizeTag)!! - 1)
            val y = random.nextInt(1, instance.getTag(MapCreator.mapSizeTag)!! - 1)
            val z = random.nextInt(1, instance.getTag(MapCreator.mapSizeTag)!! - 1)

            lastPos = Pos(x.toDouble(), y.toDouble(), z.toDouble())

            isValid = true
        }

        instance.setBlock(lastPos, Block.AIR)
        instance.setBlock(lastPos.add(0.0, 1.0, 0.0), Block.AIR)
        player.mole.lastHeightOnGround = lastPos.y()
        player.teleport(lastPos)
    }

    override fun playerDied(player: Player, killer: Entity?, deathMessage: () -> Component) {
        if (gameState == GameState.ENDING) return

        val killerPlayer = killer as Player

        player.mole.dead = true

        playerAudience.sendMessage(deathMessage.invoke())

        player.inventory.clear()
        player.heal()
        player.gameMode = GameMode.SPECTATOR
        player.showTitle(
            Title.title(
                Component.text("YOU DIED", NamedTextColor.RED, TextDecoration.BOLD),
                Component.text()
                    .append(Component.text("Killed by ", NamedTextColor.GRAY))
                    .append(Component.text(killerPlayer.username)).build(),
                Title.Times.of(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))
            )
        )

        player.addEffect(Potion(PotionEffect.NIGHT_VISION, 2, 32767))

        // TODO: add spectating items
    }

    override fun registerEvents() {

        // TODO: Chests

        childEventNode.listenOnly<EntityTickEvent> {
            if (entity.entityType == EntityType.SNOWBALL) {
                // Check for snowball collision with block
                val collidingEntities = entity.collidingEntities(instance.players)
                if (collidingEntities.isNotEmpty()) {
                    collidingEntities.forEach {
                        if (it !is Player) return@listenOnly
                        if (!it.mole.canBeHit) return@listenOnly

                        it.velocity = it.velocity.add(Vec(
                            (-sin(entity.position.yaw() * Math.PI / 180.0f) * 0.4f),
                            20.0,
                            (cos(entity.position.yaw() * Math.PI / 180.0f) * 0.4f)
                        ))

                        it.damage(DamageType.fromProjectile(null, entity), 0f)
                    }
                    entity.remove()
                }

                if (entity.velocity.x() == 0.0 || entity.velocity.y() == 0.0 || entity.velocity.z() == 0.0) {
                    entity.remove()
                }
            }
            if (entity.entityType == EntityType.POTION) {
                if (entity.velocity.x() == 0.0 || entity.velocity.y() == 0.0 || entity.velocity.z() == 0.0 || entity.isOnGround || !entity.instance!!.getBlock(entity.position).compare(Block.AIR)) {

                    val collidingEntities = entity.boundingBox.expand(6.0, 6.0, 6.0).collidingEntities(instance.players)

                    collidingEntities.forEach { collideEntity ->
                        (collideEntity as Player).sendMessage("collid")

                        val item = (entity.entityMeta as ThrownPotionMeta).item.getItem
                        collideEntity.sendMessage("a ${item!!.id}")
                        if (item !is PotionItem) return@forEach
                        collideEntity.sendMessage("b")
                        item.potionEffects.forEach { potionEffect -> collideEntity.addEffect(potionEffect) }

                    }
                    entity.instance!!.playSound(Sound.sound(SoundEvent.ENTITY_SPLASH_POTION_BREAK, Sound.Source.AMBIENT, 1f, 0.8f), entity.position)
                    entity.remove()
                }
            }

            if (entity !is Player) return@listenOnly

            val player = entity as Player

            val activeRegenEffects = player.activeEffects.firstOrNull { it.potion.effect == PotionEffect.REGENERATION }
            if (activeRegenEffects != null && player.aliveTicks % (50 / activeRegenEffects.potion.amplifier) == 0L) {
                player.health += 0.5f
            }


            // Fall damage
            if (player.isOnGround && player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
                val lastHeight = player.mole.lastHeightOnGround
                player.mole.lastHeightOnGround = player.position.y()

                val fallDistance = lastHeight - player.position.y()
                val damage = max(0.0, fallDistance - 3).toFloat()
                if (damage == 0f) return@listenOnly

                player.scheduleNextTick {
                    player.damage(DamageType.GRAVITY, damage)
                }
            }
        }

        childEventNode.listenOnly<PlayerUseItemEvent> {
            if (itemStack.material == Material.SNOWBALL) {
                val entity = Entity(EntityType.SNOWBALL)

                player.instance!!.playSound(Sound.sound(SoundEvent.ENTITY_SNOWBALL_THROW, Sound.Source.AMBIENT, 1f, 0.8f), player.position)
                entity.velocity = player.position.direction().normalize().mul(40.0)
                entity.setInstance(player.instance!!, player.eyePosition())

            }
            if (itemStack.material == Material.SPLASH_POTION) {
                val potion = Entity(EntityType.POTION)

                val thrownPotionMeta = potion.entityMeta as ThrownPotionMeta
                thrownPotionMeta.item = itemStack
                player.itemInMainHand.consume(1)

                player.instance!!.playSound(Sound.sound(SoundEvent.ENTITY_SPLASH_POTION_THROW, Sound.Source.AMBIENT, 1f, 0.8f), player.position)
                potion.velocity = player.position.direction().normalize().mul(10.0)
                potion.setInstance(player.instance!!, player.eyePosition())
            }
        }

        childEventNode.listenOnly<EntityDamageEvent> {
            if (entity !is Player) return@listenOnly

            val player = entity as Player

            // if would have killed
            if (0 >= player.health - damage) {
                isCancelled = true

                if (damageType is EntityDamage) {
                    kill(player, (damageType as EntityDamage).source)
                }
            }

        }

        // Handles instant block breaking
        childEventNode.listenOnly<PlayerStartDiggingEvent> {
            if (player.inventory.itemInMainHand.material == Shovel.createItemStack().material && MapCreator.possibleBlocks.contains(block) && player.isOnGround) {
                instance.breakBlock(player, blockPosition)

                // Plays block break sound for other players
                instance.players
                    .filter { it != player }
                    .forEach {
                        it.playSound(Sound.sound(SoundEvent.BLOCK_GRAVEL_BREAK, Sound.Source.BLOCK, 1f, 0.8f), blockPosition)
                    }
            }
        }

        childEventNode.listenOnly<PlayerBlockInteractEvent> {
            if (block.compare(Block.CHEST)) {
                val inventory = (block.handler() as SingleChestHandler).inventory
                player.openInventory(inventory)
                instance.playSound(Sound.sound(SoundEvent.BLOCK_CHEST_OPEN, Sound.Source.BLOCK, 1f, 1f), blockPosition)
            }
        }

        childEventNode.listenOnly<PlayerBlockPlaceEvent> {
            blocksPlacedByPlayer.add(blockPosition)
        }

        childEventNode.listenOnly<PlayerBlockBreakEvent> {
            if (block == Block.BEDROCK) {
                isCancelled = true
                return@listenOnly
            }
            if (block.compare(Block.CHEST)) return@listenOnly

            if (player.inventory.count(Material.DIRT) < 64) {
                player.playSound(Sound.sound(SoundEvent.ENTITY_ITEM_PICKUP, Sound.Source.PLAYER, 0.25f, 1f), player.position)
                player.inventory.addItemStack(ItemStack.of(Material.DIRT))
            }

            if (!blocksPlacedByPlayer.contains(blockPosition)) {
                if (ThreadLocalRandom.current().nextDouble() < 0.001) {
                    player.sendMessage(mini.parse(" <gold>★</gold> <dark_gray>|</dark_gray> <gray>You uncovered a <light_purple>CHEST</light_purple>!"))
                    player.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1f, 1f), blockPosition)

                    val singleChest = SingleChestHandler.create()
                    val handler = singleChest.handler() as SingleChestHandler

                    addRandomChestItems(handler.inventory)

                    isCancelled = true
                    instance.setBlock(blockPosition, singleChest)

                    uncoveredChests.add(singleChest)
                }
            }
        }

        childEventNode.listenOnly<EntityAttackEvent> {
            if (target !is Player || entity !is Player) return@listenOnly

            val attacker = entity as Player
            val victim = target as Player

            if (!victim.mole.canBeHit) return@listenOnly
            if (attacker.gameMode != GameMode.SURVIVAL || victim.gameMode != GameMode.SURVIVAL) return@listenOnly

            victim.takeKnockback(attacker)
            victim.mole.canBeHit = false
            Manager.scheduler.buildTask { victim.mole.canBeHit = true }.delay(Duration.ofMillis(500)).schedule()

            val defence = victim.getAttribute(Attribute.ARMOR).baseValue
            val toughness = victim.getAttribute(Attribute.ARMOR_TOUGHNESS).baseValue

            var damage = (attacker.heldItem?.damage ?: 0.5f) * (1 - (min(20f, max(defence / 5f, defence - (((attacker.heldItem?.damage ?: 0.5f)*4)/(toughness + 8))))/25))
            if (!attacker.isOnGround) damage *= 1.5f

            println("damage $damage")

            victim.damage(DamageType.fromPlayer(attacker), damage)
        }
    }

    fun addRandomChestItems(inventory: Inventory) {
        val alreadyHadItems = mutableSetOf<Item>()
        for (i in 0..15) {
            val newItem = Item.random()
            if (alreadyHadItems.contains(newItem)) continue
            alreadyHadItems.add(newItem)
            inventory.addRandomly(newItem.createItemStack())
        }
    }

}