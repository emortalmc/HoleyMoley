package emortal.holeymoley

import emortal.holeymoley.command.doEvent
import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.item.*
import emortal.holeymoley.map.MapCreator
import emortal.holeymoley.util.SphereUtil
import emortal.immortal.game.GameManager
import emortal.immortal.game.GameOptions
import emortal.immortal.game.GameTypeInfo
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.extensions.Extension
import world.cepi.kstom.command.register

class HoleyMoleyExtension : Extension() {

    companion object {
        val mini = MiniMessage.get()
    }

    override fun initialize() {

        WoodenSword
        LeatherHelmet
        LeatherChestplate
        LeatherLeggings
        LeatherBoots
        RegenPotion

        doEvent.register()

        SphereUtil.init()

        GameManager.registerGame<HoleyMoleyGame>(
            GameTypeInfo(
                eventNode,
                "holeymoley",
                mini.parse("<gradient:gold:yellow><bold>HoleyMoley"),
                true,
                GameOptions(
                    { MapCreator.create(50) },
                    15,
                    2,
                    false
                )
            )
        )

        logger.info("[HoleyMoleyExtension] has been enabled!")
    }

    override fun terminate() {
        logger.info("[HoleyMoleyExtension] has been disabled!")
    }

}