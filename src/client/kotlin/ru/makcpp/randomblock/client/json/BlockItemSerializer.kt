package ru.makcpp.randomblock.client.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

object BlockItemSerializer : KSerializer<BlockItem> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.minecraft.item.BlockItem", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BlockItem) {
        val itemId = Registries.ITEM.getId(value).toString()
        encoder.encodeString(itemId)
    }

    override fun deserialize(decoder: Decoder): BlockItem {
        val itemId = Identifier.of(decoder.decodeString())
        return Registries.ITEM.get(itemId) as BlockItem
    }
}

val BLOCK_ITEMS_LIST_SERIALIZER = ListSerializer(BlockItemSerializer)