package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.util.SphereUtil
import net.minestom.server.entity.GameMode
import net.minestom.server.instance.block.Block

object Boxed : Event("Boxed") {

    override fun performEvent(game: HoleyMoleyGame) {
        val blocksInSphere = SphereUtil.getBlocksInSphere(3)

        game.players
            .filter { it.gameMode == GameMode.SURVIVAL }
            .forEach {
                blocksInSphere.forEach { pos ->
                    if (!it.instance!!.getBlock(pos.add(it.position)).compare(Block.DIRT)) return@forEach
                    if (pos.distanceSquared(it.position) > 1.8*1.8) return@forEach
                    it.instance?.setBlock(pos.add(it.position), Block.OAK_PLANKS)
                }
            }
    }

}
