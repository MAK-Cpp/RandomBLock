package ru.makcpp.randomblock.item

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult
import ru.makcpp.randomblock.serialization.BlockItemWithProbability
import ru.makcpp.randomblock.serialization.PlayerPages
import ru.makcpp.randomblock.util.LoggerDelegator
import ru.makcpp.randomblock.util.PlayersMap
import ru.makcpp.randomblock.util.isServer
import java.util.UUID
import kotlin.collections.set

/**
 * Предмет для случайного выбора блока и установки его.
 *
 * Позволяет установить случайный блок из набора
 */
class RandomBlockPlacerItem(settings: Settings) : ModItem(settings) {
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

    private fun ItemUsageContext.tryPlaceBlock(blockItem: BlockItem): ActionResult = if (world.isServer()) {
        blockItem.useOnBlock(this)
    } else {
        ActionResult.SUCCESS
    }

    fun List<BlockItemWithProbability>.getRandomBlockItem(): BlockItem? {
        // Оставим только те сущности, где есть и блок, и вероятность
        val filteredList = filterNot { it.isEmpty }

        val totalProbability = filteredList.sumOf { it.probability }
        if (totalProbability <= 0) return null

        val randomValue = (0 until totalProbability).random()

        var cumulative = 0
        for (item in filteredList) {
            cumulative += item.probability
            if (randomValue < cumulative) {
                return item.blockItem
            }
        }
        // Теоретически сюда выполнение не должно доходить
        throw IllegalStateException("No probabilities found")
    }

    private fun ItemUsageContext.useOnBlockInCreative(
        blockItemsWithProbabilities: List<BlockItemWithProbability>,
    ): ActionResult {
        LOGGER.debug("player is in creative")

        val blockItem = blockItemsWithProbabilities.getRandomBlockItem() ?: return ActionResult.PASS
        return tryPlaceBlock(blockItem)
    }

    private fun ItemUsageContext.useOnBlock(
        player: PlayerEntity,
        blockItemsWithProbabilities: List<BlockItemWithProbability>,
    ): ActionResult {
        LOGGER.debug("player isn't in creative")

        // 1) Получить все предметы-блоки, которые мы хотим использовать
        val blockItems = blockItemsWithProbabilities.mapNotNull { it.blockItem }

        // 2) Получить все стеки предметов, которые содержат нужные нам предметы
        val playersItemStacks = player.inventory.mainStacks.filter { blockItems.contains(it.item) }
        if (playersItemStacks.isEmpty()) {
            LOGGER.debug("There is no items in player's inventory")
            return ActionResult.PASS
        }
        // 3) Узнаем, а какие вообще уникальные предметы есть у игрока
        val playerItems = playersItemStacks.mapNotNull { it.item as BlockItem }.toSet()

        // 4) Из всех блоков с их вероятностями оставим только те, которые присутствуют у игрока
        //    В будущем добавить вариант, что если игрок не содержит хотя бы 1 из предметов, то ничего не ставится
        val containedBlockItemsWithProbabilities =
            blockItemsWithProbabilities.filter { it.blockItem != null && playerItems.contains(it.blockItem) }

        // 5) Случайно выберем блок, который будем ставить
        val randomItem = containedBlockItemsWithProbabilities.getRandomBlockItem() ?: return ActionResult.PASS

        // 6) Найдем стек у игрока, который содержит наш случайный блок
        val playersItemStack = playersItemStacks.find { it.item == randomItem } ?: return ActionResult.PASS

        return tryPlaceBlock(randomItem).also {
            // Если смогли поставить блок, то на стороне сервера уменьшим стак с этим блоком на один
            if (it == ActionResult.SUCCESS && world.isServer()) {
                playersItemStack.decrement(1)
            }
        }
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (context.world.isClient) {
            return ActionResult.SUCCESS
        }
        val player = context.player ?: return ActionResult.PASS

        val playerCurrentBlocksList = get(player.uuid).currentPage.blocksWithProbabilities

        LOGGER.debug("starting placing random block")

        return with(context) {
            if (player.isCreative) {
                useOnBlockInCreative(playerCurrentBlocksList)
            } else {
                useOnBlock(player, playerCurrentBlocksList)
            }
        }
    }
}
