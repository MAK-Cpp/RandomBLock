package ru.makcpp.randomblock.network.payload

import kotlinx.serialization.json.Json
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import ru.makcpp.randomblock.RandomBlock
import ru.makcpp.randomblock.serialization.PlayerBlocksLists

data class PlayerBlocksListsPayload(
    val playerBlocksLists: PlayerBlocksLists,
) : CustomPayload {
    companion object {
        val PACKET_ID: Identifier = Identifier.of(RandomBlock.MOD_ID, "player_blocks_lists_packet")
        val ID = CustomPayload.Id<PlayerBlocksListsPayload>(PACKET_ID)
        val CODEC: PacketCodec<RegistryByteBuf, PlayerBlocksListsPayload> =
            PacketCodec.tuple(
                PacketCodec.of<RegistryByteBuf, PlayerBlocksLists>(
                    { value, buf -> buf.writeString(Json.encodeToString(value)) },
                    { buf -> Json.decodeFromString<PlayerBlocksLists>(buf.readString()) },
                ),
                PlayerBlocksListsPayload::playerBlocksLists,
                ::PlayerBlocksListsPayload,
            )
    }

    override fun getId() = ID
}
