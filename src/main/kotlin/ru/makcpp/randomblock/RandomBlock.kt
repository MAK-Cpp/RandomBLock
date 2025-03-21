package ru.makcpp.randomblock

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import ru.makcpp.randomblock.item.registerItems

class RandomBlock : ModInitializer {
    companion object {
        const val MOD_ID = "randomblock"
        val LOGGER = LoggerFactory.getLogger(MOD_ID)!!
    }

    override fun onInitialize() {
        registerItems()
    }
}
