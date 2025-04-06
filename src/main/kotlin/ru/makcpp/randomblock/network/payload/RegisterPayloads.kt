package ru.makcpp.randomblock.network.payload

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry

fun registerPayloads() {
    // Packets, which can be sent from client to server
    with(PayloadTypeRegistry.playC2S()) {
        register(OpenGuiPayload.ID, OpenGuiPayload.CODEC)
        register(PlayerBlocksListsPayload.ID, PlayerBlocksListsPayload.CODEC)
    }

    // Packets, which can be sent from server to client
    with(PayloadTypeRegistry.playS2C()) {
        register(PlayerBlocksListsPayload.ID, PlayerBlocksListsPayload.CODEC)
    }
}
