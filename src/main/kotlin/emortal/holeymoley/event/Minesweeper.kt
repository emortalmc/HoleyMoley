package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import java.time.Duration

object Minesweeper : Event("Minesweeper") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.minesweeper = true
        game.instance!!.scheduler().buildTask {
            game.minesweeper = false
        }.delay(Duration.ofSeconds(10)).schedule()
    }
}