package emortal.holeymoley.item

import emortal.holeymoley.HoleyMoleyExtension
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

enum class Rarity(val component: Component, val weight: Int) {
    COMMON(Component.text("COMMON", NamedTextColor.GRAY, TextDecoration.BOLD), 30),
    UNCOMMON(Component.text("UNCOMMON", NamedTextColor.GREEN, TextDecoration.BOLD), 15),
    RARE(Component.text("RARE", NamedTextColor.AQUA, TextDecoration.BOLD), 7),
    EPIC(Component.text("EPIC", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD), 3),
    LEGENDARY(HoleyMoleyExtension.mini.parse("<bold><gradient:light_purple:gold>LEGENDARY"), 1),

    IMPOSSIBLE(Component.empty(), 0)
}