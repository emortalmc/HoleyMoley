package emortal.holeymoley

import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.map.MapCreator
import emortal.immortal.game.GameManager
import emortal.immortal.game.GameOptions
import emortal.immortal.game.GameTypeInfo
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.extensions.Extension;
import world.cepi.kstom.adventure.asMini

class HoleyMoleyExtension : Extension() {

    val mini = MiniMessage.get()

    override fun initialize() {

        GameManager.registerGame<HoleyMoleyGame>(
            GameTypeInfo(
                eventNode,
                "holeymoley",
                mini.parse("<gradient:gold:yellow><bold>HoleyMoley"),
                GameOptions(
                    { MapCreator.create() },
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