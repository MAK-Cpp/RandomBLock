package ru.makcpp.randomblock.client

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreens
import ru.makcpp.randomblock.RandomBlock
import ru.makcpp.randomblock.client.gui.RandomBlockPlacerItemScreen
import ru.makcpp.randomblock.client.keybind.registryKeybinds
import ru.makcpp.randomblock.client.network.registryClientNetwork
import ru.makcpp.randomblock.gui.RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER
import ru.makcpp.randomblock.gui.RandomBlockPlacerItemGuiDescription
import ru.makcpp.randomblock.network.payload.PlayerBlocksListsPayload
import ru.makcpp.randomblock.serialization.BlocksPage
import ru.makcpp.randomblock.serialization.PlayerPages
import ru.makcpp.randomblock.util.LoggerDelegator
import ru.makcpp.randomblock.util.reference
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class RandomBlockClient : ClientModInitializer {
    companion object {
        private val LOGGER by LoggerDelegator

        @OptIn(ExperimentalSerializationApi::class)
        val PRETTY_JSON =
            Json {
                prettyPrint = true
                prettyPrintIndent = "  "
            }

        val DEFAULT_CONFIG_FILE_CONTENT =
            PlayerPages(
                number = 0,
                pages = mutableListOf(BlocksPage.newPage(1)),
            )

        private val CONFIG_FILE_PATH: Path =
            MinecraftClient
                .getInstance()
                .runDirectory
                .toPath()
                .resolve("config")
                .resolve("${RandomBlock.MOD_ID}_config.json")
    }

    var playerPages: PlayerPages =
        if (CONFIG_FILE_PATH.notExists()) {
            LOGGER.debug("there is no config file, creating new one.")

            CONFIG_FILE_PATH.parent.createDirectories()
            CONFIG_FILE_PATH.createFile()

            DEFAULT_CONFIG_FILE_CONTENT
        } else {
            val config = CONFIG_FILE_PATH.readText()
            try {
                PRETTY_JSON.decodeFromString<PlayerPages>(config)
            } catch (e: SerializationException) {
                LOGGER.error("Error while loading config file", e)
                LOGGER.debug("Replacing config with default content")
                DEFAULT_CONFIG_FILE_CONTENT
            }
        }

    override fun onInitializeClient() {
        LOGGER.info("config file path: $CONFIG_FILE_PATH")
        // При заходе игрока подгружается его конфиг файл
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            loadConfig()
        }
        // При выходе - выгружается в файл
        ClientPlayConnectionEvents.DISCONNECT.register { handler, client ->
            saveConfig()
        }
        // Экран для предмета, где можно настраивать блоки
        HandledScreens.register<RandomBlockPlacerItemGuiDescription, RandomBlockPlacerItemScreen>(
            RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER,
        ) { gui, inventory, title -> RandomBlockPlacerItemScreen(gui, inventory.player, title) }

        configureClientProxy()
        registryKeybinds()
        registryClientNetwork()
    }

    /**
     * Настраиваем клиентские методы в common коде, которые там не доступны по умолчанию, например, [ClientPlayNetworking.send]
     */
    private fun configureClientProxy() {
        val client = this

        with(ClientProxy.INSTANCE!!) {
            sendToServer = ClientPlayNetworking::send

            playerPagesRef = client::playerPages.reference
        }
    }

    private fun loadConfig() {
        ClientPlayNetworking.send(PlayerBlocksListsPayload(playerPages))
    }

    private fun saveConfig() {
        CONFIG_FILE_PATH.writeText(PRETTY_JSON.encodeToString<PlayerPages>(playerPages))
    }
}
