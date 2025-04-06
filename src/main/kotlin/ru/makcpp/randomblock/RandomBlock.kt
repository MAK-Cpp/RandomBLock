package ru.makcpp.randomblock

import net.fabricmc.api.ModInitializer
import net.minecraft.world.World
import org.slf4j.LoggerFactory
import ru.makcpp.randomblock.command.registerCommands
import ru.makcpp.randomblock.item.registerItems
import ru.makcpp.randomblock.network.payload.registerPayloads
import ru.makcpp.randomblock.network.registryServerNetwork

fun World.isServer(): Boolean = !isClient

class RandomBlock : ModInitializer {
    companion object {
        const val MOD_ID = "randomblock"
        val LOGGER = LoggerFactory.getLogger(MOD_ID)!!
    }

    override fun onInitialize() {
        registerItems()
        registerCommands()
        registerPayloads()
        registryServerNetwork()
    }
}
