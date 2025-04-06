package ru.makcpp.randomblock.network.payload

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import ru.makcpp.randomblock.RandomBlock

class OpenGuiPayload : CustomPayload {
    companion object {
        val PACKET_ID: Identifier = Identifier.of(RandomBlock.MOD_ID, "opengui_packet")
        val ID = CustomPayload.Id<OpenGuiPayload>(PACKET_ID)
        val CODEC: PacketCodec<RegistryByteBuf, OpenGuiPayload> =
            PacketCodec.of(
                { value, buf -> buf.writeIdentifier(PACKET_ID) },
                { buf ->
                    require(buf.readIdentifier() == PACKET_ID) { "Invalid ID received" }
                    OpenGuiPayload()
                },
            )
    }

    override fun getId() = ID
}
