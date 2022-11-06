package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import net.minestom.server.entity.GameMode
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect

object NearDeath : Event("Near Death") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.players
            .filter { it.gameMode == GameMode.SURVIVAL }
            .forEach {
                it.health = 1f
                it.addEffect(Potion(PotionEffect.REGENERATION, 2, 6 * 20))
            }
    }
}