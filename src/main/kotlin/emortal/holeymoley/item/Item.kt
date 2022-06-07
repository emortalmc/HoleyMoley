package emortal.holeymoley.item

import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import world.cepi.kstom.adventure.noItalic

class Item(val material: Material, val rarity: Rarity, val itemCreate: (ItemStack.Builder) -> Unit = { }) {

    fun createItemStack(): ItemStack {
        return ItemStack.builder(material)
            .lore(rarity.component.noItalic())
            .also { itemCreate.invoke(it) }
            .build()
    }

}
