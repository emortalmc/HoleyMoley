package emortal.holeymoley.util

import net.minestom.server.collision.BoundingBox
import net.minestom.server.entity.Entity

fun Entity.collidingEntities(entities: Collection<Entity>): List<Entity> = entities.filter { boundingBox.intersect(it.boundingBox) }
fun BoundingBox.collidingEntities(entities: Collection<Entity>): List<Entity> = entities.filter { intersect(it.boundingBox) }