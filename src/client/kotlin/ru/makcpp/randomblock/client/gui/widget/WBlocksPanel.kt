package ru.makcpp.randomblock.client.gui.widget

import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItem
import net.minecraft.world.item.ItemStack
import ru.makcpp.randomblock.serialization.BlocksPage
import ru.makcpp.randomblock.util.ValueRef

class WBlocksPanel(private val playerPageRef: ValueRef<BlocksPage>) : WGridPanel() {
    companion object {
        const val BLOCKS_WIDTH = 3
        const val BLOCKS_HEIGHT = 3
    }

    private val icons = List(BLOCKS_WIDTH * BLOCKS_HEIGHT) {
        WItem(ItemStack.EMPTY)
    }

    init {
        icons.forEachIndexed { index, icon ->
            val column = index % BLOCKS_WIDTH
            val row = index / BLOCKS_WIDTH
            add(WSlotItem(icon), column, row)
        }

        update()
    }

    fun update() {
        val blocks = playerPageRef.value.blocksWithProbabilities

        icons.forEachIndexed { index, icon ->
            val block = blocks[index].blockItem
            val stack = block?.let { ItemStack(it) } ?: ItemStack.EMPTY
            icon.setItems(listOf(stack))
        }
    }
}
