package ru.makcpp.randomblock.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.network.packet.CustomPayload
import ru.makcpp.randomblock.serialization.PlayerBlocksLists

@Environment(EnvType.CLIENT)
object ClientProxy {
    @Environment(EnvType.CLIENT)
    var sendToServer: (payload: CustomPayload) -> Unit = {}

    @Environment(EnvType.CLIENT)
    var updateBlockListsInClient: (playerBlockLists: PlayerBlocksLists) -> Unit = {}
}
