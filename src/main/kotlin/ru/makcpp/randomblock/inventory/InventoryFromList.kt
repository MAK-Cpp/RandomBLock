package ru.makcpp.randomblock.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InventoryFromList(val blockItems: MutableList<BlockItem?>) : Inventory {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(InventoryFromList::class.java)
    }

    private val blockItemsAsStacks =
        blockItems.map { if (it != null) ItemStack(it) else ItemStack.EMPTY }.toMutableList()

    override fun size(): Int = blockItems.size

    override fun isEmpty(): Boolean = blockItems.all { it == null }

    override fun getStack(slot: Int): ItemStack {
        LOGGER.debug("Get stack in slot $slot")
        return blockItemsAsStacks[slot]
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack = removeStack(slot)

    override fun removeStack(slot: Int): ItemStack {
        LOGGER.debug("Removing $slot")
        blockItemsAsStacks[slot] = ItemStack.EMPTY
        blockItems[slot] = null
        // В этом инвентаре все предметы - фантомные, поэтому когда мы достаем стэк, он просто исчезает
        return ItemStack.EMPTY
    }

    override fun setStack(slot: Int, stack: ItemStack) {
        LOGGER.debug("Set stack $slot")
        val blockItem = stack.item as? BlockItem ?: return
        blockItems[slot] = blockItem
        blockItemsAsStacks[slot] = ItemStack(blockItem)
    }

    override fun markDirty() {
    }

    override fun canPlayerUse(player: PlayerEntity): Boolean = true

    override fun clear() {
        for (i in blockItems.indices) {
            blockItems[i] = null
            blockItemsAsStacks[i] = ItemStack.EMPTY
        }
    }
}