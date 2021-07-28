package emortal.holeymoley.map

import net.minestom.server.instance.Instance
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.batch.RelativeBlockBatch
import net.minestom.server.instance.block.Block
import net.minestom.server.tag.Tag
import world.cepi.kstom.Manager
import java.time.Duration

object MapCreator {

    val possibleBlocks = listOf(Block.DIRT, Block.COARSE_DIRT, Block.DIRT, Block.COARSE_DIRT, Block.GRAVEL)

    val mapSizeTag = Tag.Integer("mapSize")

    fun init() {

    }

    fun create(mapSize: Int): Instance {
        val instance = Manager.instance.createInstanceContainer()

        instance.setTag(mapSizeTag, mapSize)

        instance.loadChunk(0, 0)
        instance.loadChunk(0, 1)
        instance.loadChunk(1, 0)
        instance.loadChunk(1, 1)

        val batch = AbsoluteBlockBatch()

        for (x in 0..mapSize) {
            for (y in 0..mapSize) {
                for (z in 0..mapSize) {

                    if ((x == 0 || x == mapSize) || (y == 0 || y == mapSize) || (z == 0 || z == mapSize)) {
                        batch.setBlock(x, y, z, Block.BEDROCK)
                        continue
                    }
                    batch.setBlock(x, y, z, possibleBlocks.random())
                }
            }
        }

        // TODO: Why does this have to be delayed smh
        Manager.scheduler.buildTask {

            batch.apply(instance) {
                println("Map created for HoleyMoley")
            }
        }.delay(Duration.ofSeconds(4)).schedule()

        return instance
    }

}