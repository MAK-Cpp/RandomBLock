package ru.makcpp.randomblock.gui.widget

import io.github.cottonmc.cotton.gui.widget.WTextField
import net.minecraft.text.Text

data class MutableValueReference<T>(
    val get: () -> T,
    val set: (T) -> Unit
)

class WIntField(private val valueRef: MutableValueReference<Int>) : WTextField() {

    init {
        width = 32
        height = 16
        maxLength = Integer.MAX_VALUE
        suggestion = Text.of { "0" }
        text = valueRef.get().let { if (it == 0) "" else it.toString() }
        setTextPredicate { text ->
            text.isEmpty() || text.toIntOrNull().let { num ->
                num != null && num in 1 until 10_000
            }
        }
        setChangedListener { text ->
            valueRef.set(if (text.isEmpty()) 0 else text.toInt())
        }
    }

    override fun setSize(x: Int, y: Int) {}

    override fun setMaxLength(max: Int): WIntField {
        return this
    }

    override fun getX(): Int = super.getX() + 1

    override fun getY(): Int = super.getY() + 1
}