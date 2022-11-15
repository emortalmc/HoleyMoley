package emortal.holeymoley.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import world.cepi.kstom.adventure.asMini

enum class Rarity(val component: Component, val weight: Int, val quality: Int) {
    COMMON(Component.text("COMMON", NamedTextColor.GRAY, TextDecoration.BOLD), 35, 1),
    UNCOMMON(Component.text("UNCOMMON", NamedTextColor.GREEN, TextDecoration.BOLD), 26, 2),
    RARE(Component.text("RARE", NamedTextColor.AQUA, TextDecoration.BOLD), 12, 3),
    EPIC(Component.text("EPIC", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD), 8, 4),
    LEGENDARY("<bold><gradient:light_purple:gold>LEGENDARY".asMini(), 1, 5),

    IMPOSSIBLE(Component.empty(), 0, 0);

    fun findWeight(luck: Int): Int {
        return weight + luck * quality
    }
}