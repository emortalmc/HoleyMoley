package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.game.mole
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect

object Blindness : Event("Blindness") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.players
            .filter { !it.mole.dead }
            .forEach {
                it.addEffect(Potion(PotionEffect.BLINDNESS, 2, 10*20))
            }
    }
}