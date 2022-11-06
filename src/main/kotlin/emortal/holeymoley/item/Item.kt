package emortal.holeymoley.item

import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.metadata.PotionMeta
import net.minestom.server.potion.PotionType
import world.cepi.kstom.adventure.noItalic
import java.util.concurrent.ThreadLocalRandom

class Item(val material: Material, val rarity: Rarity, val itemCreate: (ItemStack.Builder) -> Unit = { }) {

    companion object {
        val items = listOf(
//            Item(Material.LEATHER_HELMET, Rarity.IMPOSSIBLE),
//            Item(Material.LEATHER_CHESTPLATE, Rarity.IMPOSSIBLE),
//            Item(Material.LEATHER_LEGGINGS, Rarity.IMPOSSIBLE),
//            Item(Material.LEATHER_BOOTS, Rarity.IMPOSSIBLE),
//            Item(Material.WOODEN_SWORD, Rarity.IMPOSSIBLE),
//            Item(Material.STONE_SHOVEL, Rarity.IMPOSSIBLE) { it.meta { it.enchantment(Enchantment.EFFICIENCY, 5) } },

            Item(Material.TNT, Rarity.RARE) { it.amount(ThreadLocalRandom.current().nextInt(1, 3)) },

            Item(Material.STONE_SWORD, Rarity.UNCOMMON),
            Item(Material.IRON_SWORD, Rarity.RARE),
            Item(Material.DIAMOND_SWORD, Rarity.LEGENDARY),

            Item(Material.STONE_AXE, Rarity.UNCOMMON),
            Item(Material.IRON_AXE, Rarity.RARE),
            Item(Material.DIAMOND_AXE, Rarity.LEGENDARY),

            Item(Material.CHAINMAIL_BOOTS, Rarity.RARE),
            Item(Material.CHAINMAIL_LEGGINGS, Rarity.EPIC),
            Item(Material.LEATHER_LEGGINGS, Rarity.RARE),
            Item(Material.LEATHER_HELMET, Rarity.RARE),
            Item(Material.LEATHER_BOOTS, Rarity.RARE),
            Item(Material.COBWEB, Rarity.COMMON) { it.amount(ThreadLocalRandom.current().nextInt(2, 6)) },
            //Item(Material.FIRE_CHARGE, Rarity.COMMON) { it.amount(ThreadLocalRandom.current().nextInt(1, 3)) },
            Item(Material.IRON_CHESTPLATE, Rarity.RARE),

//            Item(Material.SHIELD, Rarity.RARE),
            Item(Material.SPLASH_POTION, Rarity.EPIC) { it.meta(PotionMeta::class.java) { it.potionType(PotionType.REGENERATION) } },

//            Item(Material.SNOWBALL, Rarity.COMMON) { it.amount(ThreadLocalRandom.current().nextInt(1, 10)) },
            Item(Material.COOKED_BEEF, Rarity.UNCOMMON) { it.amount(ThreadLocalRandom.current().nextInt(2, 3)) }
        )

        fun randomItem(): Item {
            val possibleItems = items.filter { it.rarity != Rarity.IMPOSSIBLE }
            val totalWeight = possibleItems.sumOf { it.rarity.weight }

            var idx = 0

            var r = ThreadLocalRandom.current().nextInt(totalWeight)
            while (idx < possibleItems.size - 1) {
                r -= possibleItems[idx].rarity.weight
                if (r <= 0.0) break
                ++idx
            }

            return possibleItems[idx]
        }
    }

    fun createItemStack(): ItemStack {
        return ItemStack.builder(material)
            .lore(rarity.component.noItalic())
            .also { itemCreate.invoke(it) }
            .build()
    }

}
