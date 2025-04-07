package ru.makcpp.randomblock.gui

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import ru.makcpp.randomblock.item.RANDOM_BLOCK_PLACER_ITEM
import ru.makcpp.randomblock.item.id
import ru.makcpp.randomblock.network.payload.PlayerBlocksListsPayload

val RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER:
    ExtendedScreenHandlerType<RandomBlockPlacerItemGuiDescription, PlayerBlocksListsPayload> =
    Registry.register(
        Registries.SCREEN_HANDLER,
        RANDOM_BLOCK_PLACER_ITEM.id,
        ExtendedScreenHandlerType(
            { syncId, inventory, data ->
                RandomBlockPlacerItemGuiDescription(
                    syncId,
                    inventory,
                    ScreenHandlerContext.EMPTY,
                    data.playerBlocksLists,
                )
            },
            PlayerBlocksListsPayload.CODEC,
        ),
    )

val RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER_FACTORY =
    object : ExtendedScreenHandlerFactory<PlayerBlocksListsPayload> {
        override fun getScreenOpeningData(player: ServerPlayerEntity): PlayerBlocksListsPayload =
            PlayerBlocksListsPayload(RANDOM_BLOCK_PLACER_ITEM[player.uuid])

        override fun getDisplayName() = Text.translatable("item.randomblock.random_block_placer_item")

        override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): ScreenHandler =
            RandomBlockPlacerItemGuiDescription(
                syncId,
                playerInventory,
                ScreenHandlerContext.create(player.world, player.blockPos),
                RANDOM_BLOCK_PLACER_ITEM[player.uuid],
            )
    }
