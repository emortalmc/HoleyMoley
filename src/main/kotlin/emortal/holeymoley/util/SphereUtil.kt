package emortal.holeymoley.util

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.instance.block.Block

object SphereUtil {

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

    fun getBlocksInSphere(radius: Int): List<Point> {
        val list = mutableListOf<Point>()

        for (x in -radius..radius) {
            for (y in -radius..radius) {
                for (z in -radius..radius) {
                    if (x * x + y * y + z * z > radius * radius) continue

                    list.add(Vec(x.toDouble(), y.toDouble(), z.toDouble()))
                }
            }
        }

        return list
    }

}