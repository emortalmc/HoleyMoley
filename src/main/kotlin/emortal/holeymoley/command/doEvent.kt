package emortal.holeymoley.command

import dev.emortal.immortal.game.GameManager.game
import emortal.holeymoley.event.Event
import emortal.holeymoley.game.HoleyMoleyGame
import net.minestom.server.command.builder.arguments.ArgumentType
import world.cepi.kstom.command.arguments.suggest
import world.cepi.kstom.command.kommand.Kommand

object doEvent : Kommand({

    onlyPlayers

    val eventArg = ArgumentType.StringArray("event").suggest {
        Event.eventList.map { it.name }
    }

    syntax(eventArg) {
        val event = Event.eventList.firstOrNull { it.name == context.get(eventArg).joinToString(" ") }
        if (event == null) {
            player.sendMessage("invalid event")
            return@syntax
        }

        event.performEvent(player.game!! as HoleyMoleyGame)
    }
}, "doevent")