package ru.makcpp.randomblock.client.gui

import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import net.minecraft.network.chat.Component

class RandomBlockPlacerItemScreen(private val gui: RandomBlockPlacerItemGuiDescription) :
    CottonClientScreen(Component.literal("TITLE"), gui) {

    override fun removed() {
        gui.onClosed()
        super.removed()
    }
}
