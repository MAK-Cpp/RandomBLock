package ru.makcpp.randomblock.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.packet.CustomPayload
import ru.makcpp.randomblock.serialization.PlayerPages
import ru.makcpp.randomblock.util.MutableValueRef

@Environment(EnvType.CLIENT)
class ClientProxy private constructor() {
    companion object {
        val INSTANCE: ClientProxy? =
            if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) ClientProxy() else null
    }

    @Environment(EnvType.CLIENT)
    lateinit var sendToServer: (payload: CustomPayload) -> Unit

    @Environment(EnvType.CLIENT)
    lateinit var playerPagesRef: MutableValueRef<PlayerPages>
}
