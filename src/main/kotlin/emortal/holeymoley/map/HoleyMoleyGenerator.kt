package emortal.holeymoley.map

import net.minestom.server.instance.Chunk
import net.minestom.server.instance.ChunkGenerator
import net.minestom.server.instance.ChunkPopulator
import net.minestom.server.instance.batch.ChunkBatch
import net.minestom.server.instance.block.Block
import net.minestom.server.world.biomes.Biome
import java.util.*

class HoleyMoleyGenerator(private val mapSize: Int) : ChunkGenerator {

    override fun generateChunkData(batch: ChunkBatch, chunkX: Int, chunkZ: Int) {
        for (xInChunk in 0 until Chunk.CHUNK_SIZE_X) {
            for (y in 0..mapSize) {
                for (zInChunk in 0 until Chunk.CHUNK_SIZE_Z) {

                    val x = (chunkX * 16) + xInChunk
                    val z = (chunkZ * 16) + zInChunk

                    if ((x == 0 || x == mapSize) || (y == 0 || y == mapSize) || (z == 0 || z == mapSize)) {
                        batch.setBlock(x, y, z, Block.BEDROCK)
                        continue
                    }

                    batch.setBlock(x, y, z, Block.DIRT)

                }
            }
        }
    }

    override fun fillBiomes(biomes: Array<out Biome>, chunkX: Int, chunkZ: Int) = Arrays.fill(biomes, Biome.PLAINS)

    override fun getPopulators(): MutableList<ChunkPopulator>? = null
}