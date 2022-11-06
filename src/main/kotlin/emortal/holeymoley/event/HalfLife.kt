package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import net.minestom.server.entity.GameMode
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect

object HalfLife : Event("Half Life") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.players
            .filter { it.gameMode == GameMode.SURVIVAL }
            .forEach {
                it.health = it.health / 2
                it.addEffect(Potion(PotionEffect.REGENERATION, 1, 4 * 20))
            }
    }
}