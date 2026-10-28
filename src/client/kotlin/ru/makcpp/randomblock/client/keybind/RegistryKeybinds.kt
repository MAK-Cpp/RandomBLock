package ru.makcpp.randomblock.client.keybind

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW
import ru.makcpp.randomblock.network.payload.OpenGuiPayload

private val OPENGUI_KEYBIND =
    KeyBindingHelper.registerKeyBinding(
        KeyMapping(
            "key.randomblock.gui", // The translation key of the keybinding's name
            InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_R, // The keycode of the key
            "category.randomblock", // The translation key of the keybinding's category.
        ),
    )

fun registryKeybinds() {
    ClientTickEvents.END_CLIENT_TICK.register { client ->
        while (OPENGUI_KEYBIND.consumeClick()) {
            ClientPlayNetworking.send(OpenGuiPayload())
        }
    }
}
