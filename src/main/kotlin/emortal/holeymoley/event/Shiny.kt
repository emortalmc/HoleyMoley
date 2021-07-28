package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.game.mole
import world.cepi.kstom.Manager
import java.time.Duration

object Shiny : Event("Shiny") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.players
            .filter { !it.mole.dead }
            .forEach {
                it.isGlowing = true
                Manager.scheduler.buildTask { it.isGlowing = false }.delay(Duration.ofSeconds(5)).schedule()
            }
    }
}