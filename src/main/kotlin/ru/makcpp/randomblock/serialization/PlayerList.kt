package ru.makcpp.randomblock.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class PlayerListSerializer<T>(private val dataSerializer: KSerializer<T>) : KSerializer<PlayerList<T>> {
    override val descriptor: SerialDescriptor = ListSerializer(dataSerializer).descriptor

    override fun serialize(encoder: Encoder, value: PlayerList<T>) {
        ListSerializer(dataSerializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PlayerList<T> {
        val list = ListSerializer(dataSerializer).deserialize(decoder)
        return PlayerList(list)
    }
}

@Serializable(with = PlayerListSerializer::class)
class PlayerList<T> private constructor(private val list: MutableList<T>) : MutableList<T> by list {
    companion object {
        operator fun <T> invoke(elements: List<T>): PlayerList<T> {
            require(elements.size == 9) { "Expected 9 elements but got ${elements.size}" }
            return PlayerList(elements.toMutableList())
        }

        operator fun <T> invoke(builder: (Int) -> T): PlayerList<T> = invoke(MutableList(9, builder))
    }
}