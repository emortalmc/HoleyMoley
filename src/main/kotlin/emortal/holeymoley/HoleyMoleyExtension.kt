package emortal.holeymoley

import dev.emortal.immortal.game.GameManager
import dev.emortal.immortal.game.GameOptions
import dev.emortal.immortal.game.WhenToRegisterEvents
import emortal.holeymoley.command.doEvent
import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.item.*
import net.minestom.server.extensions.Extension
import world.cepi.kstom.adventure.asMini

class HoleyMoleyExtension : Extension() {

    override fun initialize() {

        WoodenSword
        LeatherHelmet
        LeatherChestplate
        LeatherLeggings
        LeatherBoots
        RegenPotion

        doEvent.register()

        GameManager.registerGame<HoleyMoleyGame>(
            eventNode,
            "holeymoley",
            "<gradient:gold:yellow><bold>HoleyMoley".asMini(),
            true,
            true,
            WhenToRegisterEvents.GAME_START,
            GameOptions(
                maxPlayers = 15,
                minPlayers = 2
            )
        )

        logger.info("[HoleyMoleyExtension] has been enabled!")
    }

    override fun terminate() {
        logger.info("[HoleyMoleyExtension] has been disabled!")
    }

}