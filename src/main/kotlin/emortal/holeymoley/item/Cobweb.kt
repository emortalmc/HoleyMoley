package emortal.holeymoley.item

import net.minestom.server.item.Material
import java.util.concurrent.ThreadLocalRandom

object Cobweb : Item("cobweb", Material.COBWEB, Rarity.COMMON, { it.amount(ThreadLocalRandom.current().nextInt(1, 7)) })