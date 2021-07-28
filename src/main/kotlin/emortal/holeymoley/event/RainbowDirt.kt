package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.game.mole
import emortal.holeymoley.map.MapCreator
import emortal.holeymoley.util.SphereUtil
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block

object RainbowDirt : Event("Rainbow Dirt") {

    override fun performEvent(game: HoleyMoleyGame) {
        game.players
            .filter { !it.mole.dead }
            .forEach {
                SphereUtil.rainbowSphere(it.instance!!, it.position)
            }
    }

}
