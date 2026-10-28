package ru.makcpp.randomblock.network

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import ru.makcpp.randomblock.item.RANDOM_BLOCK_PLACER_ITEM
import ru.makcpp.randomblock.network.payload.PlayerBlocksListsPayload

fun registryServerNetwork() {
    ServerPlayNetworking.registerGlobalReceiver(PlayerBlocksListsPayload.ID) { payload, context ->
        context.server().execute {
            RANDOM_BLOCK_PLACER_ITEM[context.player().uuid] = payload.playerPages
        }
    }
}
