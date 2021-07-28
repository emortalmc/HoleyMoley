package emortal.holeymoley.util

import emortal.holeymoley.map.MapCreator
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block
import kotlin.math.sqrt

object SphereUtil {

    var spawnPositionList = mutableListOf<Pos>()

    fun init() {
        for (x in -3..3) {
            for (y in -3..3) {
                for (z in -3..3) {
                    if (sqrt(((x * x) + (y * y) + (z * z)).toDouble()) <= 3) {
                        spawnPositionList.add(Pos(x.toDouble(), y.toDouble(), z.toDouble()))
                    }
                }
            }
        }
    }

    val rainbowBlocks = listOf(
        Block.RED_CONCRETE_POWDER,
        Block.ORANGE_CONCRETE_POWDER,
        Block.YELLOW_CONCRETE_POWDER,
        Block.GREEN_CONCRETE_POWDER,
        Block.BLUE_CONCRETE_POWDER,
        Block.CYAN_CONCRETE_POWDER,
        Block.LIGHT_BLUE_CONCRETE_POWDER,
        Block.PURPLE_CONCRETE_POWDER,
        Block.PINK_CONCRETE_POWDER,
        Block.MAGENTA_CONCRETE_POWDER
    )

    fun rainbowSphere(instance: Instance, pos: Pos) {
        val batch = AbsoluteBlockBatch()

        val mapSize = instance.getTag(MapCreator.mapSizeTag)!! - 1

        for (it in spawnPositionList) {
            val blockPos = pos.add(it.x(), it.y(), it.z())
            val block = instance.getBlock(blockPos)

            if (blockPos.x() <= 0 || blockPos.x() > mapSize || blockPos.z() <= 0 || blockPos.z() > mapSize || blockPos.y() <= 0 || blockPos.y() > mapSize) {
                continue
            }

            if (MapCreator.possibleBlocks.contains(block)) {
                batch.setBlock(blockPos, rainbowBlocks.random())

            }

        }

        batch.apply(instance) {}
    }

}