package ru.makcpp.randomblock.network.payload

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import ru.makcpp.randomblock.RandomBlock

class OpenGuiPayload : CustomPacketPayload {
    companion object {
        val PACKET_ID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(RandomBlock.MOD_ID, "opengui_packet")
        val ID = CustomPacketPayload.Type<OpenGuiPayload>(PACKET_ID)
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, OpenGuiPayload> =
            StreamCodec.ofMember(
                { value, buf -> buf.writeResourceLocation(PACKET_ID) },
                { buf ->
                    require(buf.readResourceLocation() == PACKET_ID) { "Invalid ID received" }
                    OpenGuiPayload()
                },
            )
    }

    override fun type() = ID
}
