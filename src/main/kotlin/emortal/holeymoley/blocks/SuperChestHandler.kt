package emortal.holeymoley.blocks

import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.utils.NamespaceID

class SuperChestHandler : BlockHandler {
    override fun getNamespaceId(): NamespaceID = Block.ENDER_CHEST.namespace()

    val inventory: Inventory = Inventory(InventoryType.CHEST_3_ROW, "Super Chest")

    companion object {
        fun create(): Block = Block.ENDER_CHEST.withHandler(SuperChestHandler())
    }
}