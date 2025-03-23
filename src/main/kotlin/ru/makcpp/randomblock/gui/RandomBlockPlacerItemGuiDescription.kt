package ru.makcpp.randomblock.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import ru.makcpp.randomblock.RandomBlock

val RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER: ScreenHandlerType<RandomBlockPlacerItemGuiDescription> = Registry.register(
    Registries.SCREEN_HANDLER,
    Identifier.of(RandomBlock.MOD_ID, "random_block_placer"),
    ScreenHandlerType(
        { syncId, inventory -> RandomBlockPlacerItemGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY) },
        FeatureFlags.VANILLA_FEATURES
    )
)

class RandomBlockPlacerItemGuiDescription(syncId: Int, inventory: PlayerInventory, context: ScreenHandlerContext) :
    SyncedGuiDescription(
        RANDOM_BLOCK_PLACER_ITEM_SCREEN_HANDLER,
        syncId,
        inventory,
        getBlockInventory(context, INVENTORY_SIZE),
        getBlockPropertyDelegate(context)
    ) {
    companion object {
        const val INVENTORY_SIZE = 1
    }

    init {
        val root = WGridPanel().also(::setRootPanel)

        with(root) {
            val gui = this@RandomBlockPlacerItemGuiDescription
            // setSize(300, 300)
            setInsets(Insets.ROOT_PANEL)

            add(gui.createPlayerInventoryPanel(), 0, 3);

            validate(gui)
        }
    }
}