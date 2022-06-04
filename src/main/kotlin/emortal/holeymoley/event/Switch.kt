package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import net.minestom.server.entity.GameMode

object Switch : Event("Switch") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.players
            .filter { it.gameMode == GameMode.SURVIVAL }
            .forEach {
                val randomPlayer = game.players.filter { player -> player != it && player.gameMode == GameMode.SURVIVAL }.random()
                val oldPos = it.position
                it.teleport(randomPlayer.position).thenRun {
                    it.scheduleNextTick {
                        it.askSynchronization()
                    }
                }
                randomPlayer.teleport(oldPos).thenRun {
                    randomPlayer.scheduleNextTick {
                        randomPlayer.askSynchronization()
                    }
                }


            }
    }
}