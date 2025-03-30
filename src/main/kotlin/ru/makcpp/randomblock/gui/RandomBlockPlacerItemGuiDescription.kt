package ru.makcpp.randomblock.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WText
import io.github.cottonmc.cotton.gui.widget.data.Insets
import kotlin.math.max
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import net.minecraft.util.ClickType
import ru.makcpp.randomblock.gui.widget.WIntField
import ru.makcpp.randomblock.inventory.InventoryFromList
import ru.makcpp.randomblock.item.RANDOM_BLOCK_PLACER_ITEM
import ru.makcpp.randomblock.item.id
import ru.makcpp.randomblock.serialization.BlockItemWithProbability
import ru.makcpp.randomblock.serialization.BlockItemWithProbabilityList
import ru.makcpp.randomblock.serialization.PlayerList
import ru.makcpp.randomblock.util.MutableValueRef
import ru.makcpp.randomblock.util.ValueRef

val RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER: ScreenHandlerType<RandomBlockPlacerItemGuiDescription> = Registry.register(
    Registries.SCREEN_HANDLER,
    RANDOM_BLOCK_PLACER_ITEM.id,
    ScreenHandlerType(
        { syncId, inventory -> RandomBlockPlacerItemGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY) },
        FeatureFlags.VANILLA_FEATURES
    )
)

class RandomBlockPlacerItemGuiDescription(syncId: Int, inventory: PlayerInventory, context: ScreenHandlerContext) :
    SyncedGuiDescription(
        RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER,
        syncId,
        inventory,
        getBlockInventory(context, INVENTORY_SIZE),
        getBlockPropertyDelegate(context)
    ) {
    companion object {
        const val INVENTORY_SIZE = 1
    }

    init {
        val root = WGridPanel().also(::setRootPanel)

        with(root) {
            val gui = this@RandomBlockPlacerItemGuiDescription
            setInsets(Insets.ROOT_PANEL)
            val uuid = inventory.player.uuid

            // Текущий лист
            val playerLists = RANDOM_BLOCK_PLACER_ITEM.playerLists(uuid)
            val playerListRef = ValueRef<BlockItemWithProbabilityList> { playerLists.currentList }
            val currentListIndexRef =
                MutableValueRef<Int>({ playerLists.currentListNumber }, { playerLists.currentListNumber = it })

            // Название текущего списка (в будущем добавить изменение)
            val label = WDynamicLabel { playerListRef.get().name }
            add(label, 0, 1)

            // Набор блоков, из которых будет рандомно ставиться какой-то случайный
            val blockItemsInventory = InventoryFromList(playerListRef)
            val blocksSet = WItemSlot(blockItemsInventory, 0, 3, 3, false)
            add(blocksSet, 0, 2)

            // Вероятность появления какого-то блока (считается как p_i / sum_i p_i)
            val probabilities: PlayerList<MutableValueRef<Int>> =
                PlayerList { i ->
                    MutableValueRef(
                        { playerListRef.get().blocksWithProbabilities[i].probability },
                        { playerListRef.get().blocksWithProbabilities[i].probability = it },
                    )
                }
            val intFields = probabilities.map { WIntField(it) }
            repeat(3) { i ->
                repeat(3) { j ->
                    val id = i * 3 + j
                    add(intFields[id], 3 + j * 2, 2 + i)
                }
            }

            // Следующая страница
            val nextListButton = WButton(Text.of { ">" }).setOnClick {
                currentListIndexRef.set(currentListIndexRef.get() + 1)
                intFields.forEach { it.update() }
            }
            // Предыдущая страница
            val prevListButton = WButton(Text.of { "<" }).setOnClick {
                currentListIndexRef.set(max(currentListIndexRef.get() - 1, 0))
                intFields.forEach { it.update() }
            }
            add(prevListButton, 0, 5)
            add(nextListButton, 1, 5)

            // Инвентарь игрока
            add(gui.createPlayerInventoryPanel(), 0, 7)

            validate(gui)
        }
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity) {
        println("${actionType.name} $slotIndex $button")
        if (slotIndex in 0 until 9
            && this.slots[slotIndex].inventory is InventoryFromList
            && !this.cursorStack.isEmpty
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
}