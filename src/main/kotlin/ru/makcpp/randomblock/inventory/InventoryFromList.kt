package ru.makcpp.randomblock.inventory

import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import ru.makcpp.randomblock.serialization.BlocksPage
import ru.makcpp.randomblock.util.LoggerDelegator
import ru.makcpp.randomblock.util.ValueRef

class InventoryFromList(private val pageRef: ValueRef<BlocksPage>) : Container {
    companion object {
        private val LOGGER by LoggerDelegator
    }

    private val blockItems
        get() = pageRef.value.blocks

    private val blockItemsAsStacks
        get() =
            blockItems
                .map { ref -> ref.value?.let { ItemStack(it) } ?: ItemStack.EMPTY }
                .toMutableList()

    override fun getContainerSize(): Int = blockItems.size

    override fun isEmpty(): Boolean = blockItems.all { it.value == null }

    override fun getItem(slot: Int): ItemStack {
        LOGGER.debug("Get stack in slot $slot")
        return blockItemsAsStacks[slot]
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack = removeItemNoUpdate(slot)

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        LOGGER.debug("Removing $slot")
        blockItemsAsStacks[slot] = ItemStack.EMPTY
        blockItems[slot].value = null
        // В этом инвентаре все предметы - фантомные, поэтому когда мы достаем стэк, он просто исчезает
        return ItemStack.EMPTY
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        LOGGER.debug("Set stack $slot")
        val blockItem = stack.item as? BlockItem ?: return
        blockItems[slot].value = blockItem
        blockItemsAsStacks[slot] = ItemStack(blockItem)
    }

    override fun setChanged() {
        return
    }

    override fun stillValid(player: Player): Boolean = true

    override fun clearContent() {
        for (i in blockItems.indices) {
            blockItems[i].value = null
            blockItemsAsStacks[i] = ItemStack.EMPTY
        }
    }
}
