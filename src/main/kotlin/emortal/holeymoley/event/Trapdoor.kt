package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.game.mole
import net.minestom.server.instance.block.Block

object Trapdoor : Event("Trapdoor") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.players
            .filter { !it.mole.dead }
            .forEach {
                for (i in 0..20) {
                    if (it.position.y() - i.toDouble() < 1) break
                    it.instance!!.setBlock(it.position.sub(0.0, i.toDouble(), 0.0), Block.AIR)
                }
            }
    }
}