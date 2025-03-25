package ru.makcpp.randomblock.gui.widget

import io.github.cottonmc.cotton.gui.widget.WTextField
import net.minecraft.text.Text

class WIntField : WTextField() {
    companion object {
        @JvmStatic
        private var result = 1
    }

    init {
        println("constructor of WIntField")
        width = 32
        height = 16
        maxLength = Integer.MAX_VALUE
        suggestion = Text.of { "1" }
        text = result.toString()
        setTextPredicate { text ->
            text.isEmpty() || text.toIntOrNull().let { num ->
                num != null && num in 1 until 10_000
            }
        }
        setChangedListener { text ->
            result = if (text.isEmpty()) 1 else text.toInt()
        }
    }

    override fun setSize(x: Int, y: Int) {}

    override fun setMaxLength(max: Int): WIntField {
        return this
    }

    override fun getX(): Int {
        return super.getX() + 1
    }

    override fun getY(): Int {
        return super.getY() + 1
    }
}