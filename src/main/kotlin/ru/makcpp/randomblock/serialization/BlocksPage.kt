package ru.makcpp.randomblock.serialization

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.item.BlockItem
import ru.makcpp.randomblock.util.MutableValueRef
import ru.makcpp.randomblock.util.reference

@Serializable
data class BlocksPage(
    @Required
    @SerialName("name")
    var name: String,
    @Required
    @SerialName("blocks")
    val blocksWithProbabilities: PlayerList<BlockItemWithProbability>,
) {
    companion object {
        fun empty(name: String) = BlocksPage(name, PlayerList { BlockItemWithProbability() })

        fun newPage(pageNumber: Int) = empty("new page $pageNumber")
    }

    val blocks: PlayerList<MutableValueRef<BlockItem?>>
        get() = PlayerList.Companion(blocksWithProbabilities.map { it::blockItem.reference })

    val probabilities: PlayerList<MutableValueRef<Int>>
        get() = PlayerList.Companion(blocksWithProbabilities.map { it::probability.reference })

    val isEmpty: Boolean
        get() = blocksWithProbabilities.all { it.isEmpty }

    val uniqueBlocksWithProbabilityPercents: List<Pair<BlockItem, String>>
        get() {
            val resultMap = mutableMapOf<BlockItem, Int>()
            blocksWithProbabilities.forEach { (blockItem, probability) ->
                if (blockItem != null) {
                    resultMap[blockItem] = resultMap.getOrDefault(blockItem, 0) + probability
                }
            }
            val probabilitiesSum = resultMap.values.sum()
            return resultMap.map {
                val percent: Double = if (probabilitiesSum == 0) 0.0 else it.value.toDouble() / probabilitiesSum * 100
                it.key to String.format("%.2f", percent)
            }
        }
}
