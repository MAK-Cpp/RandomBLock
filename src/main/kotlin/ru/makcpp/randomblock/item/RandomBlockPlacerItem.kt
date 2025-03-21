package ru.makcpp.randomblock.item

import kotlin.random.Random
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import ru.makcpp.randomblock.RandomBlock
import ru.makcpp.randomblock.isServer


class RandomBlockPlacerItem(settings: Settings) : Item(settings) {
    private val blockItems: List<BlockItem> = listOf(Items.STONE, Items.GRASS_BLOCK).map { it as BlockItem }

    private fun useOnBlockInCreative(context: ItemUsageContext, pos: BlockPos, player: PlayerEntity): ActionResult {
        RandomBlock.LOGGER.info("player is in creative")
        val block = blockItems[Random.nextInt(blockItems.size)].block
        if (context.world.canPlace(block.defaultState, pos, ShapeContext.absent())) {
            if (context.world.isServer()) {
                context.world.setBlockState(pos, block.defaultState)
                val sound = block.defaultState.soundGroup.placeSound
                context.world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F)
            }
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

    private fun useOnBlock(context: ItemUsageContext, pos: BlockPos, player: PlayerEntity): ActionResult {
        RandomBlock.LOGGER.info("player is not in creative")
        val playerItems = (player.inventory.main + player.inventory.offHand).filter { blockItems.contains(it.item) }
        if (playerItems.isEmpty()) {
            return ActionResult.PASS
        }
        val playersItemStack = playerItems[Random.nextInt(playerItems.size)]
        val block = (playersItemStack.item as BlockItem).block
        if (context.world.canPlace(block.defaultState, pos, ShapeContext.absent())) {
            if (context.world.isServer()) {
                playersItemStack.decrement(1)
                context.world.setBlockState(pos, block.defaultState)
                val sound = block.defaultState.soundGroup.placeSound
                context.world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F)
            }
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        // Определяем позицию размещения: рядом с выбранным блоком
        val pos = context.blockPos.offset(context.side)
        val player = context.player ?: return ActionResult.PASS
        RandomBlock.LOGGER.info("starting placing random block")
        return when {
            player.isCreative -> useOnBlockInCreative(context, pos, player)
            else -> useOnBlock(context, pos, player)
        }
    }
}