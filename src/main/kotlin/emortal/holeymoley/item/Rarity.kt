package emortal.holeymoley.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import world.cepi.kstom.adventure.asMini

enum class Rarity(val component: Component, val weight: Int) {
    COMMON(Component.text("COMMON", NamedTextColor.GRAY, TextDecoration.BOLD), 35),
    UNCOMMON(Component.text("UNCOMMON", NamedTextColor.GREEN, TextDecoration.BOLD), 26),
    RARE(Component.text("RARE", NamedTextColor.AQUA, TextDecoration.BOLD), 12),
    EPIC(Component.text("EPIC", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD), 8),
    LEGENDARY("<bold><gradient:light_purple:gold>LEGENDARY".asMini(), 1),

    IMPOSSIBLE(Component.empty(), 0)
}