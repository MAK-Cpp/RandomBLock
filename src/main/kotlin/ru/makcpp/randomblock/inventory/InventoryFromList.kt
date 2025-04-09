package ru.makcpp.randomblock.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import ru.makcpp.randomblock.serialization.BlocksPage
import ru.makcpp.randomblock.util.LoggerDelegator
import ru.makcpp.randomblock.util.ValueRef

class InventoryFromList(private val pageRef: ValueRef<BlocksPage>) : Inventory {
    companion object {
        private val LOGGER by LoggerDelegator
    }

    private val blockItems
        get() = pageRef.value.blocks

    private val blockItemsAsStacks
        get() =
            blockItems
                .map { if (it.value != null) ItemStack(it.value) else ItemStack.EMPTY }
                .toMutableList()

    override fun size(): Int = blockItems.size

    override fun isEmpty(): Boolean = blockItems.all { it.value == null }

    override fun getStack(slot: Int): ItemStack {
        LOGGER.debug("Get stack in slot $slot")
        return blockItemsAsStacks[slot]
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack = removeStack(slot)

    override fun removeStack(slot: Int): ItemStack {
        LOGGER.debug("Removing $slot")
        blockItemsAsStacks[slot] = ItemStack.EMPTY
        blockItems[slot].value = null
        // В этом инвентаре все предметы - фантомные, поэтому когда мы достаем стэк, он просто исчезает
        return ItemStack.EMPTY
    }

    override fun setStack(slot: Int, stack: ItemStack) {
        LOGGER.debug("Set stack $slot")
        val blockItem = stack.item as? BlockItem ?: return
        blockItems[slot].value = blockItem
        blockItemsAsStacks[slot] = ItemStack(blockItem)
    }

    override fun markDirty() {
        return
    }

    override fun canPlayerUse(player: PlayerEntity): Boolean = true

    override fun clear() {
        for (i in blockItems.indices) {
            blockItems[i].value = null
            blockItemsAsStacks[i] = ItemStack.EMPTY
        }
    }
}
