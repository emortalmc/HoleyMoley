package emortal.holeymoley.item

import net.minestom.server.item.Material
import java.util.concurrent.ThreadLocalRandom

object Snowball : Item("snowball", Material.SNOWBALL, Rarity.COMMON, { it.amount(ThreadLocalRandom.current().nextInt(1, 17)) })