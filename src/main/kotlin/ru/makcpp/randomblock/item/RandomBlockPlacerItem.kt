package ru.makcpp.randomblock.item

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set
import kotlin.random.Random
import net.minecraft.block.Block
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.sound.SoundCategory
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.makcpp.randomblock.isServer

/**
 * Предмет для случайного выбора блока и установки его.
 *
 * Позволяет установить случайный блок из набора
 */
class RandomBlockPlacerItem(settings: Settings) : Item(settings) {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(RandomBlockPlacerItem::class.java)
    }

    private val playersBlockItems: MutableMap<UUID, MutableList<BlockItem>> = ConcurrentHashMap()

    fun joinPlayer(playerUUID: UUID, blockItems: MutableList<BlockItem>) {
        playersBlockItems[playerUUID] = blockItems
    }

    fun disconnectPlayer(playerUUID: UUID): List<BlockItem> = requireNotNull(playersBlockItems.remove(playerUUID)) {
        "Player $playerUUID not found"
    }

    private fun tryPlaceBlock(world: World, block: Block, pos: BlockPos): ActionResult {
        LOGGER.debug("Placing random item {}", block)

        if (world.canPlace(block.defaultState, pos, ShapeContext.absent())) {
            if (world.isServer()) {
                world.setBlockState(pos, block.defaultState)
                val sound = block.defaultState.soundGroup.placeSound
                world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F)
            }

            LOGGER.debug("Success")

            return ActionResult.SUCCESS
        }

        LOGGER.debug("Pass")

        return ActionResult.PASS
    }

    private fun ItemUsageContext.useOnBlockInCreative(pos: BlockPos, blockItems: List<BlockItem>): ActionResult {
        LOGGER.debug("player is in creative")

        val block = blockItems[Random.nextInt(blockItems.size)].block
        return tryPlaceBlock(world, block, pos)
    }

    private fun ItemUsageContext.useOnBlock(
        pos: BlockPos,
        player: PlayerEntity,
        blockItems: List<BlockItem>
    ): ActionResult {
        LOGGER.debug("player isn't in creative")

        val playersItemStacks = with(player.inventory) { main + offHand }.filter { blockItems.contains(it.item) }
        if (playersItemStacks.isEmpty()) {
            LOGGER.debug("There is no items in player's inventory")
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
        val pos = context.blockPos.offset(context.side)
        val player = context.player ?: return ActionResult.PASS
        val playerBlockItems = playersBlockItems[player.uuid] ?: return ActionResult.PASS
        if (playerBlockItems.isEmpty()) {
            return ActionResult.PASS
        }

        LOGGER.debug("starting placing random block")

        return with(context) {
            if (player.isCreative) useOnBlockInCreative(pos, playerBlockItems)
            else useOnBlock(pos, player, playerBlockItems)
        }
    }
}