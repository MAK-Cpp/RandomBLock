package ru.makcpp.randomblock.client.gui.widget

import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.WItem
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WPlainPanel

/**
 * Предмет с текстурой слота на заднем плане
 */
class WSlotItem(icon: WItem) : WPlainPanel() {
    companion object {
        const val WIDTH = 18
        const val HEIGHT = 18
        const val SLOT_TEXTURE_SIZE = 64
    }

    init {
        setBackgroundPainter { context, x, y, _ ->
            ScreenDrawing.texturedRect(
                context,
                x, y,
                WIDTH, HEIGHT,
                WItemSlot.SLOT_TEXTURE,
                0f, 0f,
                WIDTH.toFloat() / SLOT_TEXTURE_SIZE, HEIGHT.toFloat() / SLOT_TEXTURE_SIZE,
                -1,
            )
        }
        add(icon, 1, 1, WIDTH - 2, HEIGHT - 2)
    }
}
