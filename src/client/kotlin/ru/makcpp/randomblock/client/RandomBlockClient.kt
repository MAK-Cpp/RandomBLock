package ru.makcpp.randomblock.client

import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlinx.serialization.json.Json
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.item.BlockItem
import org.slf4j.LoggerFactory
import ru.makcpp.randomblock.RandomBlock
import ru.makcpp.randomblock.client.gui.RandomBlockPlacerItemScreen
import ru.makcpp.randomblock.client.json.BLOCK_ITEMS_LIST_SERIALIZER
import ru.makcpp.randomblock.gui.RandomBlockPlacerItemGuiDescription
import ru.makcpp.randomblock.gui.RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER
import ru.makcpp.randomblock.item.RandomBlockPlacerItem
import ru.makcpp.randomblock.item.getItem

class RandomBlockClient : ClientModInitializer {
    companion object {
        val LOGGER = LoggerFactory.getLogger(RandomBlockClient::class.java)!!
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

        val randomBlockPlacerItem = getItem<RandomBlockPlacerItem>()
        val blockItems: MutableList<BlockItem?> = if (configFilePath.notExists()) {
            LOGGER.debug("there is no config file, creating new one.")

            configFilePath.parent.createDirectories()
            configFilePath.createFile()

            MutableList(9) { null }
        } else {
            val config = configFilePath.readText()
            Json.decodeFromString(BLOCK_ITEMS_LIST_SERIALIZER, config).toMutableList()
        }

        LOGGER.debug("config file loaded: {}", configFilePath)

        randomBlockPlacerItem.joinPlayer(playerUUID, blockItems)
    }

    private fun saveConfig(playerUUID: UUID) {
        LOGGER.debug("saving config for player with uuid {}", playerUUID)

        val randomBlockPlacerItem = getItem<RandomBlockPlacerItem>()
        val blockItems = randomBlockPlacerItem.disconnectPlayer(playerUUID)
        configFilePath.writeText(Json.encodeToString(BLOCK_ITEMS_LIST_SERIALIZER, blockItems))
    }
}
