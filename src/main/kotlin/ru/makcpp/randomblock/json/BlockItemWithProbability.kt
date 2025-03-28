package ru.makcpp.randomblock.json

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.item.BlockItem

@Serializable
data class BlockItemWithProbability(
    @Serializable(with = BlockItemSerializer::class)
    @SerialName("block_item")
    @Required
    var blockItem: BlockItem? = null,

    @Serializable
    @SerialName("probability")
    @Required
    var probability: Int = 0
)