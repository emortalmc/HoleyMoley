package emortal.holeymoley.map

import io.github.bloepiloepi.pvp.explosion.PvpExplosionSupplier
import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import net.minestom.server.utils.NamespaceID
import world.cepi.kstom.Manager

object MapCreator {

    val mapSizeTag = Tag.Integer("mapSize")

    fun create(mapSize: Int): Instance {
        val dimension = Manager.dimensionType.getDimension(NamespaceID.from("fullbright"))!!
        val instance = Manager.instance.createInstanceContainer(dimension)

        instance.setTag(mapSizeTag, mapSize)

        instance.setChunkGenerator(TempChunkGen(mapSize))
        instance.explosionSupplier = PvpExplosionSupplier.INSTANCE

        return instance
    }

}