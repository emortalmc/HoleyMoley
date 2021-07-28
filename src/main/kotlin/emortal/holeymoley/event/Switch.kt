package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.game.mole

object Switch : Event("Switch") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.players
            .filter { !it.mole.dead }
            .forEach {
                val randomPlayer = game.players.filter { player -> !player.mole.dead }.random()
                val oldPos = it.position
                it.teleport(randomPlayer.position)
                randomPlayer.teleport(oldPos)
            }
    }
}