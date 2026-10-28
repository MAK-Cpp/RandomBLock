package ru.makcpp.randomblock.network.payload

import kotlinx.serialization.json.Json
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import ru.makcpp.randomblock.RandomBlock
import ru.makcpp.randomblock.serialization.PlayerPages

data class PlayerBlocksListsPayload(val playerPages: PlayerPages) : CustomPacketPayload {
    companion object {
        val PACKET_ID: Identifier =
            Identifier.fromNamespaceAndPath(RandomBlock.MOD_ID, "player_blocks_lists_packet")
        val ID = CustomPacketPayload.Type<PlayerBlocksListsPayload>(PACKET_ID)
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, PlayerBlocksListsPayload> =
            StreamCodec.composite(
                StreamCodec.ofMember(
                    { value, buf -> buf.writeUtf(Json.encodeToString(value)) },
                    { buf -> Json.decodeFromString<PlayerPages>(buf.readUtf()) },
                ),
                PlayerBlocksListsPayload::playerPages,
                ::PlayerBlocksListsPayload,
            )
    }

    override fun type() = ID
}
