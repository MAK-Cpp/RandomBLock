package ru.makcpp.randomblock.client.gui

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import ru.makcpp.randomblock.gui.RandomBlockPlacerItemGuiDescription

class RandomBlockPlacerItemScreen(gui: RandomBlockPlacerItemGuiDescription, player: Player, title: Component) :
    CottonInventoryScreen<RandomBlockPlacerItemGuiDescription>(gui, player, title)
