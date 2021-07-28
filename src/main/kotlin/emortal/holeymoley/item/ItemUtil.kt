package emortal.holeymoley.item

import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.Enchantment
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import java.util.concurrent.ThreadLocalRandom

object ItemUtil {

    fun PlayerInventory.count(material: Material): Int {
        var count = 0
        itemStacks.forEach {
            if (it.material == material) {
                count += it.amount
            }
        }
        return count
    }

    fun Inventory.addRandomly(itemStack: ItemStack) {
        var randomSlot: Int

        var validSlot = false
        while (!validSlot) {
            randomSlot = ThreadLocalRandom.current().nextInt(size)

            if (getItemStack(randomSlot) == ItemStack.AIR) {
                validSlot = true
                setItemStack(randomSlot, itemStack)
            }
        }
    }

    val Item.damage: Float
        get() = damage + (0.5f + ((createItemStack().meta.enchantmentMap[Enchantment.SHARPNESS] ?: -0.5f).toFloat() * 0.5f)) *2

}