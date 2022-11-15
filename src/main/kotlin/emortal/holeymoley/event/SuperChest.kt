package emortal.holeymoley.event

import emortal.holeymoley.blocks.SuperChestHandler
import emortal.holeymoley.game.HoleyMoleyGame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Vec
import net.minestom.server.instance.block.Block
import java.util.concurrent.ThreadLocalRandom

object SuperChest : Event("Super Chest") {
    override fun performEvent(game: HoleyMoleyGame) {
        game.instance?.setBlock(0, 0, 0, Block.DIAMOND_BLOCK)

        val rng = ThreadLocalRandom.current()

        val pos = Vec(
            rng.nextInt(2, HoleyMoleyGame.mapSize - 1).toDouble(),
            rng.nextInt(2, HoleyMoleyGame.mapSize - 1).toDouble(),
            rng.nextInt(2, HoleyMoleyGame.mapSize - 1).toDouble()
        )

        if (game.superChest != null) {
            game.instance?.setBlock(game.superChest!!, game.previousSuperChestBlock!!)
        }

        val block = SuperChestHandler.create()
        game.addChestLoot((block.handler() as SuperChestHandler).inventory, 16, 9)

        game.superChest = pos
        game.previousSuperChestBlock = game.instance?.getBlock(pos)
        game.instance?.setBlock(pos, block)

        game.sendMessage(
            Component.text()
                .append(Component.text("⚠", NamedTextColor.GOLD))
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(Component.text("A super chest has generated at ${pos.blockX()}, ${pos.blockY()}, ${pos.blockZ()}! Loot it before it's gone!", NamedTextColor.GREEN))
        )
    }
}