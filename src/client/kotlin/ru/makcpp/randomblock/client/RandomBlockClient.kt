package ru.makcpp.randomblock.client

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreens
import org.slf4j.LoggerFactory
import ru.makcpp.randomblock.RandomBlock
import ru.makcpp.randomblock.client.gui.RandomBlockPlacerItemScreen
import ru.makcpp.randomblock.client.keybind.registryKeybinds
import ru.makcpp.randomblock.client.network.registryClientNetwork
import ru.makcpp.randomblock.gui.RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER
import ru.makcpp.randomblock.gui.RandomBlockPlacerItemGuiDescription
import ru.makcpp.randomblock.network.payload.PlayerBlocksListsPayload
import ru.makcpp.randomblock.serialization.BlockItemWithProbability
import ru.makcpp.randomblock.serialization.BlockItemWithProbabilityList
import ru.makcpp.randomblock.serialization.PlayerBlocksLists
import ru.makcpp.randomblock.serialization.PlayerList
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class RandomBlockClient : ClientModInitializer {
    companion object {
        val LOGGER = LoggerFactory.getLogger(RandomBlockClient::class.java)!!

        @OptIn(ExperimentalSerializationApi::class)
        val PRETTY_JSON =
            Json {
                prettyPrint = true
                prettyPrintIndent = "  "
            }

        val DEFAULT_CONFIG_FILE_CONTENT =
            PlayerBlocksLists(
                number = 0,
                lists =
                    mutableListOf(
                        BlockItemWithProbabilityList(
                            name = "new list 1",
                            blocksWithProbabilities = PlayerList { BlockItemWithProbability() },
                        ),
                    ),
            )

        private val CONFIG_FILE_PATH: Path =
            MinecraftClient
                .getInstance()
                .runDirectory
                .toPath()
                .resolve("config")
                .resolve("${RandomBlock.MOD_ID}_config.json")
    }

    var blockItems: PlayerBlocksLists =
        if (CONFIG_FILE_PATH.notExists()) {
            LOGGER.debug("there is no config file, creating new one.")

            CONFIG_FILE_PATH.parent.createDirectories()
            CONFIG_FILE_PATH.createFile()

            DEFAULT_CONFIG_FILE_CONTENT
        } else {
            val config = CONFIG_FILE_PATH.readText()
            try {
                PRETTY_JSON.decodeFromString<PlayerBlocksLists>(config)
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
        ClientProxy.sendToServer = { payload ->
            LOGGER.info("Sending to server: $payload")
            ClientPlayNetworking.send(payload)
        }
        ClientProxy.updateBlockListsInClient = { playerBlocksLists ->
            LOGGER.info("Updating player blocks lists")
            blockItems = playerBlocksLists
        }
    }

    private fun loadConfig() {
        ClientPlayNetworking.send(PlayerBlocksListsPayload(blockItems))
    }

    private fun saveConfig() {
        CONFIG_FILE_PATH.writeText(PRETTY_JSON.encodeToString<PlayerBlocksLists>(blockItems))
    }
}
