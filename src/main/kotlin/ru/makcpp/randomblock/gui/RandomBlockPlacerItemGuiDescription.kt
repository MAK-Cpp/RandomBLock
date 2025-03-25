package ru.makcpp.randomblock.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.data.Insets
import java.util.function.Consumer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.ClickType
import ru.makcpp.randomblock.inventory.InventoryFromList
import ru.makcpp.randomblock.item.RandomBlockPlacerItem
import ru.makcpp.randomblock.item.getItem
import ru.makcpp.randomblock.item.getItemId

val RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER: ScreenHandlerType<RandomBlockPlacerItemGuiDescription> = Registry.register(
    Registries.SCREEN_HANDLER,
    getItemId<RandomBlockPlacerItem>(),
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
            val randomBlockPlacerItem = getItem<RandomBlockPlacerItem>()
            val blockItemsInventory = randomBlockPlacerItem.playersBlockItemsAsInventory(inventory.player.uuid)
            val blocksSet = WItemSlot(blockItemsInventory, 0, 9, 1, false)

            add(blocksSet, 0, 1)

            add(gui.createPlayerInventoryPanel(), 0, 3)

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
                    slot.insertStack(cursorItemStack, 1)
                    cursorItemStack.increment(1)

                    slot.markDirty()
                }

                else -> super.onSlotClick(slotIndex, button, actionType, player)
            }
        } else {
            super.onSlotClick(slotIndex, button, actionType, player)
        }
    }
}