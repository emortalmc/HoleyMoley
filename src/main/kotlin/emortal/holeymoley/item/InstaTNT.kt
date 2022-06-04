package emortal.holeymoley.item

import net.minestom.server.item.Material
import java.util.concurrent.ThreadLocalRandom

object InstaTNT : Item("instatnt", Material.REDSTONE_BLOCK, Rarity.RARE, { it.amount(ThreadLocalRandom.current().nextInt(1, 3)) })