package ru.makcpp.randomblock.client

import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreens
import org.slf4j.LoggerFactory
import ru.makcpp.randomblock.RandomBlock
import ru.makcpp.randomblock.client.gui.RandomBlockPlacerItemScreen
import ru.makcpp.randomblock.gui.RandomBlockPlacerItemGuiDescription
import ru.makcpp.randomblock.gui.RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER
import ru.makcpp.randomblock.serialization.BlockItemWithProbability
import ru.makcpp.randomblock.item.RANDOM_BLOCK_PLACER_ITEM
import ru.makcpp.randomblock.serialization.BlockItemWithProbabilityList
import ru.makcpp.randomblock.serialization.PlayerBlocksLists
import ru.makcpp.randomblock.serialization.PlayerList

class RandomBlockClient : ClientModInitializer {
    companion object {
        val LOGGER = LoggerFactory.getLogger(RandomBlockClient::class.java)!!

        @OptIn(ExperimentalSerializationApi::class)
        val PRETTY_JSON = Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }

        val DEFAULT_CONFIG_FILE_CONTENT = PlayerBlocksLists(
            currentListNumber = 0,
            lists = mutableListOf(
                BlockItemWithProbabilityList(
                    name = "new list 1",
                    blocksWithProbabilities = PlayerList { BlockItemWithProbability() }
                )
            )
        )
    }

    private val configFilePath: Path =
        MinecraftClient.getInstance().runDirectory.toPath().resolve("config")
            .resolve("${RandomBlock.MOD_ID}_config.json")

    override fun onInitializeClient() {
        LOGGER.info("config file path: $configFilePath")
        // При заходе игрока подгружается его конфиг файл
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            loadConfig(requireNotNull(client.player).uuid)
        }
        // При выходе - выгружается в файл
        ClientPlayConnectionEvents.DISCONNECT.register { handler, client ->
            saveConfig(requireNotNull(client.player).uuid)
        }
        // Экран для предмета, где можно настраивать блоки
        HandledScreens.register<RandomBlockPlacerItemGuiDescription, RandomBlockPlacerItemScreen>(
            RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER
        )
        { gui, inventory, title -> RandomBlockPlacerItemScreen(gui, inventory.player, title) }
    }

    private fun loadConfig(playerUUID: UUID) {
        LOGGER.debug("loading config for player with uuid {}", playerUUID)

        val blockItems: PlayerBlocksLists = if (configFilePath.notExists()) {
            LOGGER.debug("there is no config file, creating new one.")

            configFilePath.parent.createDirectories()
            configFilePath.createFile()

            DEFAULT_CONFIG_FILE_CONTENT
        } else {
            val config = configFilePath.readText()
            try {
                PRETTY_JSON.decodeFromString<PlayerBlocksLists>(config)
            } catch (e: Exception) {
                LOGGER.error("Error while loading config file", e)
                LOGGER.debug("Replacing config with default content")
                DEFAULT_CONFIG_FILE_CONTENT
            }
        }

        LOGGER.debug("config file loaded: {}", configFilePath)

        RANDOM_BLOCK_PLACER_ITEM.joinPlayer(playerUUID, blockItems)
    }

    private fun saveConfig(playerUUID: UUID) {
        LOGGER.debug("saving config for player with uuid {}", playerUUID)

        val blockItems = RANDOM_BLOCK_PLACER_ITEM.disconnectPlayer(playerUUID)
        configFilePath.writeText(PRETTY_JSON.encodeToString<PlayerBlocksLists>(blockItems))
    }
}
