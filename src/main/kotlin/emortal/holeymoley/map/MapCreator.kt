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

        instance.chunkGenerator = HoleyMoleyGenerator(mapSize)

        return instance
    }

}