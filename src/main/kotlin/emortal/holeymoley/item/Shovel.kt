package emortal.holeymoley.item

import net.minestom.server.item.Enchantment
import net.minestom.server.item.Material

object Shovel : Item(
    "shovel",
    Material.STONE_SHOVEL,
    Rarity.IMPOSSIBLE,
    { it.meta {
        it.enchantment(Enchantment.EFFICIENCY, 5)
    } }
)