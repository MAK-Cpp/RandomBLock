package ru.makcpp.randomblock.client.gui

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import kotlin.math.max
import net.minecraft.network.chat.Component
import ru.makcpp.randomblock.client.gui.widget.WBlocksPanel
import ru.makcpp.randomblock.client.gui.widget.WProbabilitiesPanel
import ru.makcpp.randomblock.client.gui.widget.WResultPanel
import ru.makcpp.randomblock.serialization.PlayerList
import ru.makcpp.randomblock.serialization.PlayerPages
import ru.makcpp.randomblock.util.MutableValueRef
import ru.makcpp.randomblock.util.mutableRef
import ru.makcpp.randomblock.util.ref

class RandomBlockPlacerItemGuiDescription(
    private val playerLists: PlayerPages,
    private val onApply: (PlayerPages) -> Unit,
) : LightweightGuiDescription(
) {
    init {
        val root = WGridPanel().also(::setRootPanel)

        with(root) {
            val gui = this@RandomBlockPlacerItemGuiDescription
            setInsets(Insets.ROOT_PANEL)

            // Текущий лист
            val playerPageRef = playerLists::currentPage.ref
            val currentPageNumberRef = playerLists::currentPageNumber.mutableRef

            // Название текущего списка (в будущем добавить изменение)
            val label = WDynamicLabel { playerPageRef.value.name }
            add(label, 0, 1)

            // Набор блоков, из которых будет рандомно ставиться какой-то случайный
            val blockPanel = WBlocksPanel(playerPageRef)
            add(blockPanel, 0, 2)

            // Вероятность появления какого-то блока (считается как p_i / sum_i p_i)
            val probabilities =
                PlayerList { index ->
                    MutableValueRef(
                        { playerPageRef.value.blocksWithProbabilities[index].probability },
                        { value ->
                            playerPageRef.value.blocksWithProbabilities[index].probability = value
                        },
                    )
                }
            val probabilitiesPanel = WProbabilitiesPanel(probabilities)

            @Suppress("MagicNumber")
            add(probabilitiesPanel, 3, 2)

            // Следующая страница
            val nextPageButton =
                WButton(Component.literal(">")).setOnClick {
                    currentPageNumberRef.value++
                    probabilitiesPanel.update()
                    blockPanel.update()
                }
            // Предыдущая страница
            val prevPageButton =
                WButton(Component.literal("<")).setOnClick {
                    currentPageNumberRef.value = max(currentPageNumberRef.value - 1, 0)
                    probabilitiesPanel.update()
                    blockPanel.update()
                }
            @Suppress("MagicNumber")
            add(prevPageButton, 0, 5)
            @Suppress("MagicNumber")
            add(nextPageButton, 1, 5)

            // Отображение итогового результата выбора игрока
            @Suppress("MagicNumber")
            val resultPanel = WResultPanel(playerPageRef)
            @Suppress("MagicNumber")
            add(resultPanel, 9, 1)

            validate(gui)
        }
    }

    fun onClosed() {
        onApply(playerLists)
    }
}
