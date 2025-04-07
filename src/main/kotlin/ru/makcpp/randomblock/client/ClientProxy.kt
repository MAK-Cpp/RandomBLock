package ru.makcpp.randomblock.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.packet.CustomPayload
import ru.makcpp.randomblock.serialization.PlayerBlocksLists
import ru.makcpp.randomblock.util.MutableValueRef

@Environment(EnvType.CLIENT)
class ClientProxy private constructor() {
    // TODO: попробовать заменить на простую запись вида
    /**
     *
     * ```kt
     * val INSTANCE = if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) ClientProxy() else null
     * ```
     */
    companion object {
        private val CLIENT_PROXY_INSTANCE = ClientProxy()

        val INSTANCE: ClientProxy?
            get() = if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) CLIENT_PROXY_INSTANCE else null
    }

    @Environment(EnvType.CLIENT)
    lateinit var sendToServer: (payload: CustomPayload) -> Unit

    @Environment(EnvType.CLIENT)
    lateinit var blockItems: MutableValueRef<PlayerBlocksLists>
}
