package emortal.holeymoley.util

import net.minestom.server.potion.CustomPotionEffect
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect

fun CustomPotionEffect.asPotion(): Potion {
    return Potion(PotionEffect.fromId(id.toInt())!!, amplifier, duration, showParticles(), showIcon(), isAmbient)
}