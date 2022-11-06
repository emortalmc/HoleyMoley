package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import java.time.Duration

object Drill : Event("Drill") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.shovelBreakRadius = 3
        game.instance!!.scheduler().buildTask {
            game.shovelBreakRadius = 1
        }.delay(Duration.ofSeconds(10)).schedule()
    }
}