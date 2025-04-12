package ru.makcpp.randomblock.gui.widget

import io.github.cottonmc.cotton.gui.widget.WItemSlot
import ru.makcpp.randomblock.inventory.InventoryFromList
import ru.makcpp.randomblock.serialization.BlocksPage
import ru.makcpp.randomblock.util.ValueRef

class WBlocksPanel(playerPageRef: ValueRef<BlocksPage>) :
    WItemSlot(InventoryFromList(playerPageRef), 0, BLOCKS_WIDTH, BLOCKS_HEIGHT, false) {
    companion object {
        const val BLOCKS_WIDTH = 3

        const val BLOCKS_HEIGHT = 3
    }
}
