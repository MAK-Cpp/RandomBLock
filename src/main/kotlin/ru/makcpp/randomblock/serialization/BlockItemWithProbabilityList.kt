package ru.makcpp.randomblock.serialization

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.item.BlockItem
import ru.makcpp.randomblock.util.MutableValueRef


@Serializable
data class BlockItemWithProbabilityList(
    @Required
    @SerialName("name")
    var name: String,

    @Required
    @SerialName("blocks")
    val blocksWithProbabilities: PlayerList<BlockItemWithProbability>,
) {
    val blocks: PlayerList<MutableValueRef<BlockItem?>>
        get() = PlayerList.Companion(blocksWithProbabilities.map { blockWithProb ->
            MutableValueRef(
                { blockWithProb.blockItem },
                { blockWithProb.blockItem = it }
            )
        })

    val probabilities: PlayerList<MutableValueRef<Int>>
        get() = PlayerList.Companion(blocksWithProbabilities.map { blockWithProb ->
            MutableValueRef(
                { blockWithProb.probability },
                { blockWithProb.probability = it }
            )
        })

    val isEmpty: Boolean
        get() = blocksWithProbabilities.all { it.isEmpty }
}