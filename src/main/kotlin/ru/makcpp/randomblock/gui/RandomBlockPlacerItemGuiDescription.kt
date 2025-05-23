package ru.makcpp.randomblock.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import net.minecraft.util.ClickType
import ru.makcpp.randomblock.client.ClientProxy
import ru.makcpp.randomblock.gui.widget.WBlocksPanel
import ru.makcpp.randomblock.gui.widget.WBlocksPanel.Companion.BLOCKS_HEIGHT
import ru.makcpp.randomblock.gui.widget.WBlocksPanel.Companion.BLOCKS_WIDTH
import ru.makcpp.randomblock.gui.widget.WProbabilitiesPanel
import ru.makcpp.randomblock.gui.widget.WResultPanel
import ru.makcpp.randomblock.inventory.InventoryFromList
import ru.makcpp.randomblock.network.payload.PlayerBlocksListsPayload
import ru.makcpp.randomblock.serialization.PlayerList
import ru.makcpp.randomblock.serialization.PlayerPages
import ru.makcpp.randomblock.util.MutableValueRef
import ru.makcpp.randomblock.util.reference
import kotlin.math.max

class RandomBlockPlacerItemGuiDescription(
    syncId: Int,
    inventory: PlayerInventory,
    context: ScreenHandlerContext,
    private val playerLists: PlayerPages,
) : SyncedGuiDescription(
    RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER,
    syncId,
    inventory,
    getBlockInventory(context, INVENTORY_SIZE),
    getBlockPropertyDelegate(context),
) {
    companion object {
        const val INVENTORY_SIZE = 1
    }

    init {
        val root = WGridPanel().also(::setRootPanel)

        with(root) {
            val gui = this@RandomBlockPlacerItemGuiDescription
            setInsets(Insets.ROOT_PANEL)

            // Текущий лист
            val playerPageRef = playerLists::currentPage.reference
            val currentPageNumberRef = playerLists::currentPageNumber.reference

            // Название текущего списка (в будущем добавить изменение)
            val label = WDynamicLabel { playerPageRef.value.name }
            add(label, 0, 1)

            // Набор блоков, из которых будет рандомно ставиться какой-то случайный
            val blockPanel = WBlocksPanel(playerPageRef)
            add(blockPanel, 0, 2)

            // Вероятность появления какого-то блока (считается как p_i / sum_i p_i)
            val probabilities =
                PlayerList { i ->
                    MutableValueRef(
                        { playerPageRef.value.blocksWithProbabilities[i].probability },
                        { playerPageRef.value.blocksWithProbabilities[i].probability = it },
                    )
                }
            val probabilitiesPanel = WProbabilitiesPanel(probabilities)

            @Suppress("MagicNumber")
            add(probabilitiesPanel, 3, 2)

            // Следующая страница
            val nextPageButton =
                WButton(Text.of { ">" }).setOnClick {
                    currentPageNumberRef.value++
                    probabilitiesPanel.update()
                }
            // Предыдущая страница
            val prevPageButton =
                WButton(Text.of { "<" }).setOnClick {
                    currentPageNumberRef.value = max(currentPageNumberRef.value - 1, 0)
                    probabilitiesPanel.update()
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

            // Инвентарь игрока
            @Suppress("MagicNumber")
            add(gui.createPlayerInventoryPanel(), 0, 7)

            validate(gui)
        }
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity) {
        if (slotIndex in 0 until BLOCKS_WIDTH * BLOCKS_HEIGHT &&
            this.slots[slotIndex].inventory is InventoryFromList &&
            !this.cursorStack.isEmpty
        ) {
            val slot = this.slots[slotIndex]
            val slotItemStack = slot.stack
            val cursorItemStack = this.cursorStack
            val clickType = if (button == 0) ClickType.LEFT else ClickType.RIGHT
            when (actionType) {
                SlotActionType.PICKUP, SlotActionType.QUICK_CRAFT -> {
                    player.onPickupSlotClick(cursorItemStack, slot.stack, clickType)

                    if (!slotItemStack.isEmpty) {
                        slotItemStack.decrement(slotItemStack.count)
                    }
                    val currentCount = cursorItemStack.count
                    slot.insertStack(cursorItemStack, 1)
                    cursorItemStack.count = currentCount

                    slot.markDirty()
                }

                else -> super.onSlotClick(slotIndex, button, actionType, player)
            }
        } else {
            super.onSlotClick(slotIndex, button, actionType, player)
        }
    }

    override fun onClosed(player: PlayerEntity) {
        if (player.world.isClient) {
            with(ClientProxy.INSTANCE!!) {
                sendToServer(PlayerBlocksListsPayload(playerLists))
                playerPagesRef.value = playerLists
            }
        }
        super.onClosed(player)
    }
}
