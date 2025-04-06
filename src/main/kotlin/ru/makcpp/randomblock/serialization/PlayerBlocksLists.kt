package ru.makcpp.randomblock.serialization

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerBlocksLists(
    @Required
    @SerialName("current_list_number")
    private var number: Int,
    @SerialName("lists")
    @Required
    val lists: MutableList<BlockItemWithProbabilityList>,
) {
    val currentList: BlockItemWithProbabilityList
        get() = lists[number]

    var currentListNumber: Int
        get() = number
        set(value) {
            require(value >= 0) { "Cannot set negate value" }
            if (value > number) {
                /**
                 * Если новый номер больше старого, то новое значение либо меньше кол-ва страниц, либо нет
                 *
                 * Если нет, то добавим недостающие страницы, и тогда currentListNumber будет смотреть на
                 * последнюю страницу
                 */
                for (i in lists.size..value) {
                    lists.add(
                        BlockItemWithProbabilityList(
                            name = "new list ${i + 1}",
                            blocksWithProbabilities = PlayerList { BlockItemWithProbability() },
                        ),
                    )
                }
                number = value
            } else {
                /**
                 * Иначе, убираем все крайние, которые пустые
                 */
                number = value
                for (i in lists.size - 1 downTo value + 1) {
                    if (!lists.last().isEmpty) {
                        break
                    }
                    lists.removeLast()
                }
            }
        }
}
