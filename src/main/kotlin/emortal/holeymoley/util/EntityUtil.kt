package emortal.holeymoley.util

import net.minestom.server.collision.BoundingBox
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Entity

fun Entity.collidingEntities(entities: Collection<Entity>): List<Entity> = entities.filter { this.boundingBox.intersectEntity(this.position, it) }
fun BoundingBox.collidingEntities(relativePos: Point, entities: Collection<Entity>): List<Entity> = entities.filter { this.intersectEntity(relativePos, it) }