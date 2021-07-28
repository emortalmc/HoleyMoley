package emortal.holeymoley.item

import net.minestom.server.potion.Potion

interface PotionItem {
    val potionEffects: List<Potion>
}