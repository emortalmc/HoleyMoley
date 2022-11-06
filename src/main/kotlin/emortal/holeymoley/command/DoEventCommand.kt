package emortal.holeymoley.command

import dev.emortal.immortal.game.GameManager.game
import emortal.holeymoley.event.Event
import emortal.holeymoley.game.HoleyMoleyGame
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import world.cepi.kstom.command.arguments.suggest

object DoEventCommand : Command("eventholeymoley") {
    init {
        val eventArg = ArgumentType.StringArray("event").suggest {
            Event.eventList.map { it.name }
        }

        addConditionalSyntax({ sender, str ->
            true
        }, { sender, context ->
            val player = sender as? Player ?: return@addConditionalSyntax

            val event = Event.eventList.firstOrNull { it.name == context.get(eventArg).joinToString(" ") }
            if (event == null) {
                player.sendMessage("invalid event")
                return@addConditionalSyntax
            }

            event.performEvent(player.game!! as HoleyMoleyGame)
        }, eventArg)
    }
}