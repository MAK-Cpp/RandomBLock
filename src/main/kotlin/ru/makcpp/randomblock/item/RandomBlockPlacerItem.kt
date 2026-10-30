package ru.makcpp.randomblock.item

import java.util.UUID
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.phys.BlockHitResult
import ru.makcpp.randomblock.exception.ImpossibleBehaviorException
import ru.makcpp.randomblock.serialization.BlockItemWithProbability
import ru.makcpp.randomblock.serialization.PlayerPages
import ru.makcpp.randomblock.util.LoggerDelegator
import ru.makcpp.randomblock.util.PlayersMap
import ru.makcpp.randomblock.util.isServer

/**
 * Предмет для случайного выбора блока и установки его.
 *
 * Позволяет установить случайный блок из набора
 */
class RandomBlockPlacerItem(settings: Properties) : ModItem(settings) {
    companion object {
        private val LOGGER by LoggerDelegator
    }

    private val playersListsOfBlocks: PlayersMap<PlayerPages> = PlayersMap()

    operator fun get(playerUUID: UUID): PlayerPages = playersListsOfBlocks[playerUUID]

    operator fun set(playerUUID: UUID, playerPages: PlayerPages) {
        playersListsOfBlocks[playerUUID] = playerPages
    }

    fun disconnectPlayer(playerUUID: UUID): PlayerPages {
        LOGGER.info("Disconnecting player $playerUUID")
        return playersListsOfBlocks.remove(playerUUID)
    }

    private fun UseOnContext.tryPlaceBlock(
        blockItem: BlockItem,
        stack: ItemStack = ItemStack(blockItem),
    ): InteractionResult = if (level.isServer()) {
        val hitResult = BlockHitResult(clickLocation, clickedFace, clickedPos, isInside)
        val placementContext = UseOnContext(level, player, hand, stack, hitResult)
        blockItem.useOn(placementContext)
    } else {
        InteractionResult.SUCCESS
    }

    fun List<BlockItemWithProbability>.getRandomBlockItem(): BlockItem? {
        // Оставим только те сущности, где есть и блок, и вероятность
        val filteredList = filterNot { it.isEmpty }

        val totalProbability = filteredList.sumOf { it.probability }
        if (totalProbability <= 0) return null

        val randomValue = (0 until totalProbability).random()

        var cumulative = 0
        for ((blockItem, probability) in filteredList) {
            cumulative += probability
            if (randomValue < cumulative) {
                return blockItem
            }
        }
        // Теоретически сюда выполнение не должно доходить
        throw ImpossibleBehaviorException("No probabilities found")
    }

    private fun UseOnContext.useOnInCreative(
        blockItemsWithProbabilities: List<BlockItemWithProbability>,
    ): InteractionResult {
        LOGGER.debug("player is in creative")

        val blockItem = blockItemsWithProbabilities.getRandomBlockItem() ?: return InteractionResult.PASS
        return tryPlaceBlock(blockItem)
    }

    @Suppress("ReturnCount")
    private fun UseOnContext.useOn(
        player: Player,
        blockItemsWithProbabilities: List<BlockItemWithProbability>,
    ): InteractionResult {
        LOGGER.debug("player isn't in creative")

        // 1) Получить все предметы-блоки, которые мы хотим использовать
        val blockItems = blockItemsWithProbabilities.mapNotNull { it.blockItem }

        // 2) Получить все стеки предметов, которые содержат нужные нам предметы
        val playersItemStacks = player.inventory.nonEquipmentItems.filter { blockItems.contains(it.item) }
        if (playersItemStacks.isEmpty()) {
            LOGGER.debug("There is no items in player's inventory")
            return InteractionResult.PASS
        }
        // 3) Узнаем, а какие вообще уникальные предметы есть у игрока
        val playerItems = playersItemStacks.mapNotNull { it.item as BlockItem }.toSet()

        // 4) Из всех блоков с их вероятностями оставим только те, которые присутствуют у игрока
        //    В будущем добавить вариант, что если игрок не содержит хотя бы 1 из предметов, то ничего не ставится
        val containedBlockItemsWithProbabilities =
            blockItemsWithProbabilities.filter { it.blockItem != null && playerItems.contains(it.blockItem) }

        // 5) Случайно выберем блок, который будем ставить
        val randomItem = containedBlockItemsWithProbabilities.getRandomBlockItem() ?: return InteractionResult.PASS

        // 6) Найдем стек у игрока, который содержит наш случайный блок
        val playersItemStack = playersItemStacks.find { it.item == randomItem } ?: return InteractionResult.PASS

        return tryPlaceBlock(randomItem, playersItemStack)
    }

    @Suppress("ReturnCount")
    override fun useOn(context: UseOnContext): InteractionResult {
        if (context.level.isClientSide) {
            return InteractionResult.SUCCESS
        }
        val player = context.player ?: return InteractionResult.PASS

        val playerCurrentBlocksList = get(player.uuid).currentPage.blocksWithProbabilities

        LOGGER.debug("starting placing random block")

        return with(context) {
            if (player.isCreative) {
                useOnInCreative(playerCurrentBlocksList)
            } else {
                useOn(player, playerCurrentBlocksList)
            }
        }
    }
}
