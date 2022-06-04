package emortal.holeymoley.map

import net.minestom.server.instance.Chunk
import net.minestom.server.instance.ChunkGenerator
import net.minestom.server.instance.ChunkPopulator
import net.minestom.server.instance.batch.ChunkBatch
import net.minestom.server.instance.block.Block
import kotlin.math.ceil

class TempChunkGen(private val mapSize: Int) : ChunkGenerator {

    val ceiledChunks = ceil(mapSize.toDouble() / 16.0).toInt()

    override fun generateChunkData(batch: ChunkBatch, chunkX: Int, chunkZ: Int) {
        if (0 > chunkX || 0 > chunkZ || chunkX > ceiledChunks || chunkZ > ceiledChunks) return

        for (xInChunk in 0 until Chunk.CHUNK_SIZE_X) {
            for (y in 0..mapSize) {
                for (zInChunk in 0 until Chunk.CHUNK_SIZE_Z) {

                    val x = (chunkX * 16) + xInChunk
                    val z = (chunkZ * 16) + zInChunk
                    if (x > mapSize || z > mapSize) continue

                    if ((x == 0 || x == mapSize) || (y == 0 || y == mapSize) || (z == 0 || z == mapSize)) {
                        batch.setBlock(xInChunk, y, zInChunk, Block.BEDROCK)
                        continue
                    }

                    batch.setBlock(xInChunk, y, zInChunk, Block.DIRT)

                }
            }
        }
    }

    override fun getPopulators(): MutableList<ChunkPopulator>? = null
}