package emortal.holeymoley.event

import emortal.holeymoley.blocks.SingleChestHandler
import emortal.holeymoley.game.HoleyMoleyGame

object ChestRefill : Event("Chest Refill") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.uncoveredChests.forEach {
            val inv = (it.handler() as SingleChestHandler).inventory
            inv.clear()
            game.addRandomChestItems(inv)
        }
    }
}