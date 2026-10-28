package ru.makcpp.randomblock.network.payload

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry

fun registerPayloads() {
    // Packets, which can be sent from client to server
    with(PayloadTypeRegistry.serverboundPlay()) {
        register(PlayerBlocksListsPayload.ID, PlayerBlocksListsPayload.CODEC)
    }

    // Packets, which can be sent from server to client
    with(PayloadTypeRegistry.clientboundPlay()) {
        register(PlayerBlocksListsPayload.ID, PlayerBlocksListsPayload.CODEC)
    }
}
