package emortal.holeymoley.map

import io.github.bloepiloepi.pvp.explosion.PvpExplosionSupplier
import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import world.cepi.kstom.Manager

object MapCreator {

    val mapSizeTag = Tag.Integer("mapSize")

    fun create(mapSize: Int): Instance {
        val instance = Manager.instance.createInstanceContainer()

        instance.setTag(mapSizeTag, mapSize)
        instance.explosionSupplier = PvpExplosionSupplier.INSTANCE

        instance.setChunkGenerator(TempChunkGen(mapSize))

        return instance
    }

}