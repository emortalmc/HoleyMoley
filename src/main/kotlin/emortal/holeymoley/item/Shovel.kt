package emortal.holeymoley.item

import net.minestom.server.item.Enchantment
import net.minestom.server.item.Material
import world.cepi.kstom.item.withMeta

object Shovel : Item(
    "shovel",
    Material.STONE_SHOVEL,
    Rarity.IMPOSSIBLE,
    { it.withMeta {
        enchantment(Enchantment.EFFICIENCY, 5)
    } }
)