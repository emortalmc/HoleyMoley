package emortal.holeymoley.map

import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import world.cepi.kstom.Manager

object MapCreator {


    val mapSizeTag = Tag.Integer("mapSize")

    fun init() {

    }

    fun create(mapSize: Int): Instance {
        val instance = Manager.instance.createInstanceContainer()

        instance.setTag(mapSizeTag, mapSize)

        /*val batch = AbsoluteBlockBatch()

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
        }*/

        instance.chunkGenerator = HoleyMoleyGenerator(mapSize)

        return instance
    }

}