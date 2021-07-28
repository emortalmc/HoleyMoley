package emortal.holeymoley.item

import net.minestom.server.item.Material
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect

object RegenPotion : Item("regenpotion", Material.SPLASH_POTION, Rarity.EPIC), PotionItem {

    override val potionEffects: List<Potion> = listOf(Potion(PotionEffect.REGENERATION, 3, 15*20))

}