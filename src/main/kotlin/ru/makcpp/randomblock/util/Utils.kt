package ru.makcpp.randomblock.util

import net.minecraft.world.World

fun World.isServer(): Boolean = !isClient
