package emortal.holeymoley.game

import emortal.holeymoley.item.Items
import emortal.holeymoley.item.Items.damage
import emortal.holeymoley.map.MapCreator
import emortal.immortal.game.Game
import emortal.immortal.game.GameOptions
import emortal.immortal.util.takeKnockback
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.instance.block.Block
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listenOnly
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom

class HoleyMoleyGame(gameOptions: GameOptions) : Game(gameOptions) {
    override fun playerJoin(player: Player) {
        player.setInstance(instance)
    }

    override fun playerLeave(player: Player) {
        MolePlayer.removeFrom(player)
    }

    override fun start() {

        // TODO: Start random event loop

        players.forEach { respawn(it) }
    }

    override fun postDestroy() {
        players.forEach { MolePlayer.removeFrom(it) }
    }

    override fun respawn(player: Player) {
        player.inventory.setItemStack(0, Items.shovel)

        player.gameMode = GameMode.SURVIVAL

        var isValid = false
        var lastPos = Pos.ZERO
        val random = ThreadLocalRandom.current()
        while (!isValid) {
            val x = random.nextInt(1, MapCreator.mapSize - 1)
            val y = random.nextInt(1, MapCreator.mapSize - 1)
            val z = random.nextInt(1, MapCreator.mapSize - 1)

            lastPos = Pos(x.toDouble(), y.toDouble(), z.toDouble())

            isValid = true
        }

        instance.setBlock(lastPos, Block.AIR)
        instance.setBlock(lastPos.add(0.0, 1.0, 0.0), Block.AIR)
        player.teleport(lastPos)
    }

    override fun kill(player: Player, killer: Player) {

    }

    override fun registerEvents() {

        // TODO: Chests

        childEventNode.listenOnly<PlayerBlockBreakEvent> {
            if (block == Block.BEDROCK) isCancelled = true
        }

        childEventNode.listenOnly<EntityAttackEvent> {
            if (target !is Player || entity !is Player) return@listenOnly

            val attacker = entity as Player
            val victim = target as Player

            if (attacker.gameMode != GameMode.SURVIVAL || victim.gameMode != GameMode.SURVIVAL) return@listenOnly

            // 1.8 kb woo
            victim.takeKnockback(attacker)
            victim.mole.canBeHit = false
            Manager.scheduler.buildTask { victim.mole.canBeHit = true }.delay(Duration.ofMillis(500)).schedule()
            victim.damage(DamageType.fromPlayer(attacker), attacker.inventory.itemInMainHand.damage)
        }
    }

}