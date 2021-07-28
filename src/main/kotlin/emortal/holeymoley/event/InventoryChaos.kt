package emortal.holeymoley.event

import emortal.holeymoley.game.HoleyMoleyGame
import emortal.holeymoley.game.mole

object InventoryChaos : Event("Inventory Chaos") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.players
            .filter { !it.mole.dead }
            .forEach { player ->
                val newItemList = player.inventory.itemStacks.clone()
                newItemList.shuffle()

                var i = 0
                newItemList.forEach {
                    player.inventory.setItemStack(i++, it)
                }
            }
    }
}