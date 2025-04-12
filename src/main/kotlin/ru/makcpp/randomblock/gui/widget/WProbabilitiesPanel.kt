package ru.makcpp.randomblock.gui.widget

import io.github.cottonmc.cotton.gui.widget.WGridPanel
import ru.makcpp.randomblock.serialization.PlayerList
import ru.makcpp.randomblock.util.MutableValueRef

/**
 * Одна ячейка будет занимать 2 ячейки предмета, то есть 32 пикселя:
 * ```
 * 2 * column + 1 -----------+
 *                           |
 * 2 * column ---------+     |
 *                     |     |
 *                     v     v
 *                   +----+----+
 * row ------------> |         |
 *                   +----+----+
 * ```
 * Вся панель вероятностей выглядит так:
 * ```
 *                                 2 * PROBABILITIES_WIDTH
 *                                       ---------->
 *
 *                           +----+----+ +----+----+ +----+----+
 *                           | PROB. 1 | | PROB. 2 | | PROB. 3 |
 *                           +----+----+ +----+----+ +----+----+
 *                       |   +----+----+ +----+----+ +----+----+
 *  PROBABILITIES_HEIGHT |   | PROB. 4 | | PROB. 5 | | PROB. 6 |
 *                       v   +----+----+ +----+----+ +----+----+
 *                           +----+----+ +----+----+ +----+----+
 *                           | PROB. 7 | | PROB. 8 | | PROB. 9 |
 *                           +----+----+ +----+----+ +----+----+
 * ```
 */
class WProbabilitiesPanel(probabilities: PlayerList<MutableValueRef<Int>>) : WGridPanel() {
    companion object {
        const val PROBABILITIES_WIDTH = 3

        const val PROBABILITIES_HEIGHT = 3
    }

    val probabilityFields = probabilities.map { WIntField(it) }

    init {
        repeat(PROBABILITIES_HEIGHT) { row ->
            repeat(PROBABILITIES_WIDTH) { column ->
                val id = row * PROBABILITIES_WIDTH + column
                add(probabilityFields[id], column * 2, row)
            }
        }
    }
}
