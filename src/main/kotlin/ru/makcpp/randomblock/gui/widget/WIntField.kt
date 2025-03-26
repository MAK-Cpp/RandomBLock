package ru.makcpp.randomblock.gui.widget

import io.github.cottonmc.cotton.gui.widget.WTextField
import net.minecraft.text.Text

data class MutableValueReference<T>(
    val get: () -> T,
    val set: (T) -> Unit
)

class WIntField(private val valueRef: MutableValueReference<Int>) : WTextField() {

    init {
        println("constructor of WIntField")
        width = 32
        height = 16
        maxLength = Integer.MAX_VALUE
        suggestion = Text.of { "0" }
        text = valueRef.get().toString()
        setTextPredicate { text ->
            text.isEmpty() || text.toIntOrNull().let { num ->
                num != null
                        && num in 0 until 10_000
                        && num.toString() == text
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

    override fun getX(): Int {
        return super.getX() + 1
    }

    override fun getY(): Int {
        return super.getY() + 1
    }
}