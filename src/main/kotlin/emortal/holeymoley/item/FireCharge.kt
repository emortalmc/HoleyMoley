package emortal.holeymoley.item

import net.minestom.server.item.Material
import java.util.concurrent.ThreadLocalRandom

object FireCharge : Item("firecharge", Material.FIRE_CHARGE, Rarity.COMMON, { it.amount(ThreadLocalRandom.current().nextInt(1, 3)) })