package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.game.mole
import emortal.holeymoley.util.SphereUtil
import net.minestom.server.instance.block.Block

object RainbowDirt : Event("Rainbow Dirt") {

    override fun performEvent(game: HoleyMoleyGame) {
        val blocksInSphere = SphereUtil.getBlocksInSphere(4)

        game.players
            .filter { !it.mole.dead }
            .forEach {
                blocksInSphere.forEach { pos ->
                    if (!it.instance!!.getBlock(pos).compare(Block.DIRT)) return
                    it.instance?.setBlock(pos.add(it.position), SphereUtil.rainbowBlocks.random())
                }
            }
    }

}
