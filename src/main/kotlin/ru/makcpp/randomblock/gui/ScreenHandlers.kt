package ru.makcpp.randomblock.gui

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import ru.makcpp.randomblock.item.RANDOM_BLOCK_PLACER_ITEM
import ru.makcpp.randomblock.network.payload.PlayerBlocksListsPayload
import ru.makcpp.randomblock.util.id

val RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER:
    ExtendedMenuType<RandomBlockPlacerItemGuiDescription, PlayerBlocksListsPayload> =
    Registry.register(
        BuiltInRegistries.MENU,
        RANDOM_BLOCK_PLACER_ITEM.id,
        ExtendedMenuType(
            { syncId, inventory, data ->
                RandomBlockPlacerItemGuiDescription(
                    syncId,
                    inventory,
                    ContainerLevelAccess.NULL,
                    data.playerPages,
                )
            },
            PlayerBlocksListsPayload.CODEC,
        ),
    )

val RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER_FACTORY =
    object : ExtendedMenuProvider<PlayerBlocksListsPayload> {
        override fun getScreenOpeningData(player: ServerPlayer): PlayerBlocksListsPayload =
            PlayerBlocksListsPayload(RANDOM_BLOCK_PLACER_ITEM[player.uuid])

        override fun getDisplayName() = Component.translatable("item.randomblock.random_block_placer_item")

        override fun createMenu(syncId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
            RandomBlockPlacerItemGuiDescription(
                syncId,
                playerInventory,
                ContainerLevelAccess.create(player.level(), player.blockPosition()),
                RANDOM_BLOCK_PLACER_ITEM[player.uuid],
            )
    }
