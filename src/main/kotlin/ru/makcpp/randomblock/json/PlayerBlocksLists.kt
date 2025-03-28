package ru.makcpp.randomblock.json

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerBlocksLists(
    @Required
    @SerialName("current_list_number")
    var currentListNumber: Int,

    @SerialName("lists")
    @Required
    val lists: MutableList<BlockItemWithProbabilityList>,
) {
    val currentList: BlockItemWithProbabilityList
        get() = lists[currentListNumber]
}