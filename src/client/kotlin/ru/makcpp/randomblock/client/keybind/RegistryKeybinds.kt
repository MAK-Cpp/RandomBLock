package ru.makcpp.randomblock.client.keybind

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.KeyMapping
import net.minecraft.resources.Identifier
import ru.makcpp.randomblock.RandomBlock
import ru.makcpp.randomblock.network.payload.OpenGuiPayload

private val OPENGUI_KEYBIND =
    KeyMappingHelper.registerKeyMapping(
        KeyMapping(
            "key.randomblock.gui", // The translation key of the keybinding's name
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_R,
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(RandomBlock.MOD_ID, "main")),
        ),
    )

fun registryKeybinds() {
    ClientTickEvents.END_CLIENT_TICK.register { client ->
        while (OPENGUI_KEYBIND.consumeClick()) {
            ClientPlayNetworking.send(OpenGuiPayload())
        }
    }
}
