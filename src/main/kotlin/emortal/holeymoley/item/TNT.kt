package emortal.holeymoley.item

import net.minestom.server.item.Material
import java.util.concurrent.ThreadLocalRandom

object TNT : Item("tnt", Material.TNT, Rarity.RARE, { it.amount(ThreadLocalRandom.current().nextInt(1, 3)) })