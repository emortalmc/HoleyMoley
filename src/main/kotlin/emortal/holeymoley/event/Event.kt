package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame

sealed class Event(val name: String) {

    companion object {
        val eventList = Event::class.sealedSubclasses.mapNotNull { it.objectInstance }
    }

    abstract fun performEvent(game: HoleyMoleyGame)

}
