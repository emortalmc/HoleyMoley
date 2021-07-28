package emortal.holeymoley.game

import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag
import java.util.concurrent.ConcurrentHashMap

val Player.mole: MolePlayer
    get() = MolePlayer.from(this)

class MolePlayer(val player: Player) {

    companion object {
        val lastHeightOnGroundTag = Tag.Double("lastHeightOnGround")
        val killsTag = Tag.Integer("kills")
        val deadTag = Tag.Byte("dead")
        val canBeHitTag = Tag.Byte("canBeHit")

        private val molePlayerMap: ConcurrentHashMap<Player, MolePlayer> = ConcurrentHashMap()

        fun from(player: Player): MolePlayer {
            molePlayerMap.computeIfAbsent(player, ::MolePlayer);

            return molePlayerMap[player]!!
        }

        fun removeFrom(player: Player) {
            molePlayerMap.remove(player)
        }
    }

    var kills: Int
        get() = player.getTag(killsTag)!!
        set(value) = player.setTag(killsTag, value)
    var dead: Boolean
        get() = player.getTag(deadTag)!!.toInt() == 1
        set(value) = player.setTag(deadTag, if (value) 1 else 0)
    var canBeHit: Boolean
        get() = player.getTag(canBeHitTag)!!.toInt() == 1
        set(value) = player.setTag(canBeHitTag, if (value) 1 else 0)

    var lastHeightOnGround: Double
        get() = player.getTag(lastHeightOnGroundTag)!!
        set(value) = player.setTag(lastHeightOnGroundTag, value)

    init {
        player.setTag(killsTag, 0)
        player.setTag(deadTag, 0)
        player.setTag(canBeHitTag, 1)
        player.setTag(lastHeightOnGroundTag, player.position.y())
    }

}