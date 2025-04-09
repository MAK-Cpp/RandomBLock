package ru.makcpp.randomblock

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.world.World
import ru.makcpp.randomblock.command.registerCommands
import ru.makcpp.randomblock.item.RANDOM_BLOCK_PLACER_ITEM
import ru.makcpp.randomblock.item.registerItems
import ru.makcpp.randomblock.network.payload.registerPayloads
import ru.makcpp.randomblock.network.registryServerNetwork

fun World.isServer(): Boolean = !isClient

class RandomBlock : ModInitializer {
    companion object {
        const val MOD_ID = "randomblock"
    }

    override fun onInitialize() {
        registerItems()
        registerCommands()
        registerPayloads()
        registryServerNetwork()

        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            server.execute {
                RANDOM_BLOCK_PLACER_ITEM.disconnectPlayer(handler.player.uuid)
            }
        }
    }
}
