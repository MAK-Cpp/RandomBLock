package ru.makcpp.randomblock.item

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set
import net.minecraft.block.Block
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.sound.SoundCategory
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.makcpp.randomblock.gui.RandomBlockPlacerItemGuiDescription
import ru.makcpp.randomblock.inventory.InventoryFromList
import ru.makcpp.randomblock.isServer

private typealias PlayersMap<T> = MutableMap<UUID, T>

private fun <T> PlayersMap<T>.notNull(playerUUID: UUID, func: (UUID) -> T?): T = requireNotNull(func(playerUUID)) {
    "Player $playerUUID not found"
}

private fun <T> PlayersMap<T>.getNotNull(playerUUID: UUID): T = notNull(playerUUID, ::get)

private fun <T> PlayersMap<T>.removeNotNull(playerUUID: UUID): T = notNull(playerUUID, ::remove)

/**
 * Предмет для случайного выбора блока и установки его.
 *
 * Позволяет установить случайный блок из набора
 */
class RandomBlockPlacerItem(settings: Settings) : ModItem(settings) {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RandomBlockPlacerItem::class.java)

        private val DIRECTIONS = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
    }

    private val playersBlockItems: PlayersMap<MutableList<BlockItem?>> = ConcurrentHashMap()
    private val playersProbabilities: PlayersMap<MutableList<Int>> = ConcurrentHashMap()

    fun joinPlayersData(blockItems: List<BlockItem?>, probabilities: List<Int>): List<BlockItemWithProbability> =
        blockItems.mapIndexed { i, item -> BlockItemWithProbability(item, probabilities[i]) }

    fun joinPlayer(playerUUID: UUID, blockItems: List<BlockItemWithProbability>) {
        playersBlockItems[playerUUID] = blockItems.map { it.blockItem }.toMutableList()
        playersProbabilities[playerUUID] = blockItems.map { it.probability }.toMutableList()
    }

    fun disconnectPlayer(playerUUID: UUID): List<BlockItemWithProbability> =
        joinPlayersData(playersBlockItems.removeNotNull(playerUUID), playersProbabilities.removeNotNull(playerUUID))

    fun playersBlockItemsAsInventory(playerUUID: UUID): Inventory =
        InventoryFromList(playersBlockItems.getNotNull(playerUUID))

    fun playersProbabilities(playerUUID: UUID): MutableList<Int> = playersProbabilities.getNotNull(playerUUID)

    private fun ItemUsageContext.tryPlaceBlock(block: Block): ActionResult {
        LOGGER.debug("Placing random item {}", block)
        val placeContext = ItemPlacementContext(this)
        val pos = placeContext.blockPos

        if (world.canPlace(block.defaultState, pos, ShapeContext.absent())) {
            if (world.isServer()) {
                var state = block.defaultState
                for (i in 0 until 4) {
                    if (DIRECTIONS[i] == horizontalPlayerFacing) {
                        break
                    }
                    state = state.rotate(BlockRotation.CLOCKWISE_90)
                }
                world.setBlockState(pos, state)
                val sound = block.defaultState.soundGroup.placeSound
                world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F)
            }

            LOGGER.debug("Success")

            return ActionResult.SUCCESS
        }

        LOGGER.debug("Pass")

        return ActionResult.PASS
    }

    fun List<BlockItemWithProbability>.getRandomBlockItem(): BlockItem? {
        // Оставим только те сущности, где есть и блок, и вероятность
        val filteredList = filter { it.blockItem != null && it.probability != 0 }

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
        blockItemsWithProbabilities: List<BlockItemWithProbability>
    ): ActionResult {
        LOGGER.debug("player is in creative")

        val block = blockItemsWithProbabilities.getRandomBlockItem()?.block ?: return ActionResult.PASS
        return tryPlaceBlock(block)
    }

    private fun ItemUsageContext.useOnBlock(
        player: PlayerEntity,
        blockItemsWithProbabilities: List<BlockItemWithProbability>
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
        val playerItems = playersItemStacks.mapNotNull { it.item }.toSet()

        // 4) Из всех блоков с их вероятностями оставим только те, которые присутствуют у игрока
        //    В будущем добавить вариант, что если игрок не содержит хотя бы 1 из предметов, то ничего не ставится
        val containedBlockItemsWithProbabilities =
            blockItemsWithProbabilities.filter { it.blockItem != null && playerItems.contains(it.blockItem) }

        // 5) Случайно выберем блок, который будем ставить
        val randomItem = containedBlockItemsWithProbabilities.getRandomBlockItem() ?: return ActionResult.PASS

        // 6) Найдем стек у игрока, который содержит наш случайный блок
        val playersItemStack = playersItemStacks.find { it.item == randomItem } ?: return ActionResult.PASS
        val block = randomItem.block

        return tryPlaceBlock(block).also {
            // Если смогли поставить блок, то на стороне сервера уменьшим стак с этим блоком на один
            if (it == ActionResult.SUCCESS && world.isServer()) {
                playersItemStack.decrement(1)
            }
        }
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val player = context.player ?: return ActionResult.PASS

        val playerBlockItems: List<BlockItem?> = playersBlockItems[player.uuid] ?: return ActionResult.PASS
        val playerProbabilities: List<Int> = playersProbabilities[player.uuid] ?: return ActionResult.PASS

        val playerData = joinPlayersData(playerBlockItems, playerProbabilities)

        LOGGER.debug("starting placing random block")

        return with(context) {
            if (player.isCreative) useOnBlockInCreative(playerData)
            else useOnBlock(player, playerData)
        }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        if (user.isSneaking) {
            if (world.isServer()) {
                user.openHandledScreen(
                    SimpleNamedScreenHandlerFactory(
                        { syncId, playerInventory, player ->
                            RandomBlockPlacerItemGuiDescription(
                                syncId,
                                playerInventory,
                                ScreenHandlerContext.create(world, player.blockPos)
                            )
                        },
                        Text.translatable("item.randomblock.random_block_placer_item")
                    )
                )
            }
            return ActionResult.SUCCESS
        }
        return super.use(world, user, hand)
    }
}