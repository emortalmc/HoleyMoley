package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.game.mole

object OneHit : Event("OneHit") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.players
            .filter { !it.mole.dead }
            .forEach {
                it.health = 0.5f
            }
    }
}