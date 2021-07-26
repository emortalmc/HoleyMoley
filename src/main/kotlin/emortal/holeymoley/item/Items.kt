package emortal.holeymoley.item

import net.minestom.server.item.Enchantment
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag

object Items {

    val damageTag = Tag.Float("damage")

    val ItemStack.damage: Float get() =
        (getTag(damageTag) ?: 0f) + (0.5f + ((meta.enchantmentMap[Enchantment.SHARPNESS] ?: -0.5f).toFloat() * 0.5f))

    val shovel = ItemStack.builder(Material.WOODEN_SHOVEL)
        .meta { meta ->
            meta.setTag(damageTag, 2f)
            meta.enchantment(Enchantment.SHARPNESS, 1)
            meta.enchantment(Enchantment.EFFICIENCY, 5)
        }
        .build()

}