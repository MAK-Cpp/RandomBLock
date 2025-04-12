package ru.makcpp.randomblock.gui.widget

import io.github.cottonmc.cotton.gui.widget.WTextField
import net.minecraft.text.Text
import ru.makcpp.randomblock.util.MutableValueRef

class WIntField(private val valueRef: MutableValueRef<Int>) : WTextField() {
    companion object {
        const val WIDTH = 32
        const val HEIGHT = 16
        const val MAX_NUMBER = 10_000
    }

    init {
        width = WIDTH
        height = HEIGHT
        maxLength = Integer.MAX_VALUE
        suggestion = Text.of { "0" }
        text = valueRef.value.let { if (it == 0) "" else it.toString() }
        setTextPredicate { text ->
            text.isEmpty() ||
                text.toIntOrNull().let { num ->
                    num != null && num in 1 until MAX_NUMBER
                }
        }
        setChangedListener { text ->
            valueRef.value = if (text.isEmpty()) 0 else text.toInt()
        }
    }

    override fun setSize(x: Int, y: Int) {
        return
    }

    override fun canResize() = false

    override fun setMaxLength(max: Int): WIntField = this

    override fun getX(): Int = super.getX() + 1

    override fun getY(): Int = super.getY() + 1

    fun update() {
        val value = valueRef.value
        text = if (value == 0) "" else value.toString()
    }
}
