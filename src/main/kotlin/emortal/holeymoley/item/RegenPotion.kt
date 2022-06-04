package emortal.holeymoley.item

import net.minestom.server.item.Material
import net.minestom.server.item.metadata.PotionMeta
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.minestom.server.potion.PotionType

object RegenPotion : Item("regenpotion", Material.SPLASH_POTION, Rarity.EPIC, { it.meta(PotionMeta::class.java) {
    it.potionType(PotionType.REGENERATION) } }), PotionItem {

    override val potionEffects: List<Potion> = listOf(Potion(PotionEffect.REGENERATION, 2, 10*20))

}