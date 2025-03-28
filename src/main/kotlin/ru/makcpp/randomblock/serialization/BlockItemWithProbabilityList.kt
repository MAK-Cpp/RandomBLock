package ru.makcpp.randomblock.serialization

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.item.BlockItem
import ru.makcpp.randomblock.util.MutableValueRef

@Serializable
class PlayerList<T> private constructor(private val list: MutableList<T>) : MutableList<T> by list {
    companion object {
        operator fun <T> invoke(elements: List<T>): PlayerList<T> {
            require(elements.size == 9) { "Expected 9 elements but got ${elements.size}" }
            return PlayerList(elements.toMutableList())
        }

        operator fun <T> invoke(builder: (Int) -> T): PlayerList<T> = invoke(MutableList(9, builder))
    }
}


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