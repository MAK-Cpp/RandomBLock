package ru.makcpp.randomblock.client.gui

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import ru.makcpp.randomblock.gui.RandomBlockPlacerItemGuiDescription

class RandomBlockPlacerItemScreen(
    gui: RandomBlockPlacerItemGuiDescription,
    player: PlayerEntity,
    title: Text,
) : CottonInventoryScreen<RandomBlockPlacerItemGuiDescription>(gui, player, title)
