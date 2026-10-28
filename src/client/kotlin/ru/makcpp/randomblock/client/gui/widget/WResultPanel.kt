package ru.makcpp.randomblock.client.gui.widget

import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItem
import io.github.cottonmc.cotton.gui.widget.WText
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import ru.makcpp.randomblock.serialization.BlocksPage
import ru.makcpp.randomblock.util.ValueRef

class WResultPanel(private val playerPageRef: ValueRef<BlocksPage>) : WGridPanel() {
    init {
        createGui()
    }

    private fun createGui() {
        @Suppress("MagicNumber")
        add(WText(Component.translatable("gui.text.result_panel")), 0, 0, 3, 1)
        playerPageRef.value.uniqueBlocksWithProbabilityPercents.forEachIndexed { i, (blockItem, probability) ->
            val row = i + 1
            add(WItem(ItemStack(blockItem)), 0, row)
            with(WText(Component.literal(probability))) {
                horizontalAlignment = HorizontalAlignment.RIGHT
                verticalAlignment = VerticalAlignment.CENTER
                add(this, 1, row, 2, 1)
            }
        }
    }

    override fun tick() {
        children.clear()
        createGui()
        super.tick()
    }
}
