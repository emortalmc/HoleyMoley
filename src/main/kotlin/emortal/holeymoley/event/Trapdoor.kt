package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import net.minestom.server.entity.GameMode

object Trapdoor : Event("Trapdoor") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.players
            .filter { it.gameMode == GameMode.SURVIVAL }
            .forEach {
                for (i in 0..20) {
                    if (it.position.y() - i.toDouble() < 1) break
                    it.instance!!.breakBlock(it, it.position.sub(0.0, i.toDouble(), 0.0))
                    //it.instance!!.setBlock(it.position.sub(0.0, i.toDouble(), 0.0), Block.AIR)
                }
            }
    }
}