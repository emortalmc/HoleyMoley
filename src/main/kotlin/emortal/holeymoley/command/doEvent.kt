package emortal.holeymoley.command

import emortal.holeymoley.event.Event
import emortal.holeymoley.game.HoleyMoleyGame
import emortal.immortal.game.GameManager.game
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import world.cepi.kstom.command.addSyntax

object doEvent : Command("doevent") {

    init {
        val eventArg = ArgumentType.StringArray("event")

        addSyntax(eventArg) {
            val event = Event.eventList.firstOrNull { it.name == this.context.get(eventArg).joinToString(" ") }
            if (event == null) {
                sender.asPlayer().sendMessage("invalid event")
                return@addSyntax
            }

            event.performEvent(sender.asPlayer().game!! as HoleyMoleyGame)
        }

    }

}