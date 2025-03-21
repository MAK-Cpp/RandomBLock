package ru.makcpp.randomblock.item

import kotlin.random.Random
import net.minecraft.block.Block
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import ru.makcpp.randomblock.RandomBlock
import ru.makcpp.randomblock.isServer


class RandomBlockPlacerItem(settings: Settings) : Item(settings) {
    private val blockItems: List<BlockItem> = listOf(Items.STONE, Items.GRASS_BLOCK).map { it as BlockItem }

    private fun tryPlaceBlock(world: World, block: Block, pos: BlockPos): ActionResult {
        if (world.canPlace(block.defaultState, pos, ShapeContext.absent())) {
            if (world.isServer()) {
                world.setBlockState(pos, block.defaultState)
                val sound = block.defaultState.soundGroup.placeSound
                world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F)
            }
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

    private fun ItemUsageContext.useOnBlockInCreative(pos: BlockPos): ActionResult {
        RandomBlock.LOGGER.info("player is in creative")
        val block = blockItems[Random.nextInt(blockItems.size)].block
        return tryPlaceBlock(world, block, pos)
    }

    private fun ItemUsageContext.useOnBlock(pos: BlockPos, player: PlayerEntity): ActionResult {
        RandomBlock.LOGGER.info("player isn't in creative")
        val playersItemStacks = with(player.inventory) { main + offHand }.filter { blockItems.contains(it.item) }
        if (playersItemStacks.isEmpty()) {
            return ActionResult.PASS
        }
        val playersItemStack = playersItemStacks[Random.nextInt(playersItemStacks.size)]
        val block = (playersItemStack.item as BlockItem).block
        return tryPlaceBlock(world, block, pos).also {
            if (it == ActionResult.SUCCESS && world.isServer()) {
                playersItemStack.decrement(1)
            }
        }
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        // Определяем позицию размещения: рядом с выбранным блоком
        val pos = context.blockPos.offset(context.side)
        val player = context.player ?: return ActionResult.PASS
        RandomBlock.LOGGER.info("starting placing random block")
        return with(context) { if (player.isCreative) useOnBlockInCreative(pos) else useOnBlock(pos, player) }
    }
}