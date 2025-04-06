package ru.makcpp.randomblock.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

object BlockItemSerializer : KSerializer<BlockItem?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.minecraft.item.BlockItem", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BlockItem?) {
        if (value == null) {
            encoder.encodeString("null")
        } else {
            val itemId = Registries.ITEM.getId(value).toString()
            encoder.encodeString(itemId)
        }
    }

    override fun deserialize(decoder: Decoder): BlockItem? {
        val string = decoder.decodeString()
        if (string == "null") {
            return null
        }
        val itemId = Identifier.of(string)
        return Registries.ITEM.get(itemId) as BlockItem
    }
}
