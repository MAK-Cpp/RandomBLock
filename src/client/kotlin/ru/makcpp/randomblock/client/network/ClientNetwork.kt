package ru.makcpp.randomblock.client.network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import ru.makcpp.randomblock.client.RandomBlockClient
import ru.makcpp.randomblock.network.payload.PlayerBlocksListsPayload

fun RandomBlockClient.registryClientNetwork() {
    ClientPlayNetworking.registerGlobalReceiver(PlayerBlocksListsPayload.ID) { payload, context ->
        this@registryClientNetwork.playerPages = payload.playerPages
    }
}
