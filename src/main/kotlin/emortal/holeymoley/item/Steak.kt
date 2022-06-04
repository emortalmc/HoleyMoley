package emortal.holeymoley.item

import net.minestom.server.item.Material
import java.util.concurrent.ThreadLocalRandom

object Steak : Item("steak", Material.COOKED_BEEF, Rarity.UNCOMMON, { it.amount(ThreadLocalRandom.current().nextInt(2, 3)) })