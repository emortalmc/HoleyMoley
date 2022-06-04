package emortal.holeymoley.game

import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag

val killsTag = Tag.Integer("kills")
val deadTag = Tag.Boolean("dead")
val canBeHitTag = Tag.Boolean("canBeHit")

var Player.kills: Int
    get() = getTag(killsTag) ?: 0
    set(value) = setTag(killsTag, value)
var Player.dead: Boolean
    get() = getTag(deadTag) ?: false
    set(value) = setTag(deadTag, value)
var Player.canBeHit: Boolean
    get() = getTag(canBeHitTag) ?: true
    set(value) = setTag(canBeHitTag, value)

fun Player.cleanup() {
    removeTag(killsTag)
    removeTag(deadTag)
    removeTag(canBeHitTag)
}