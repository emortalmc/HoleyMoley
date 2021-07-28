package emortal.holeymoley.blocks

import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.utils.NamespaceID

class SingleChestHandler : BlockHandler {
    override fun getNamespaceId(): NamespaceID = Block.CHEST.namespace()

    val inventory: Inventory = Inventory(InventoryType.CHEST_3_ROW, "Chest")

    companion object {
        fun create(): Block = Block.CHEST.withHandler(SingleChestHandler())
    }
}