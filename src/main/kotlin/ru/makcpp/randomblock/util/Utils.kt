package ru.makcpp.randomblock.util

import net.minecraft.world.World

fun World.isServer(): Boolean = !isClient

fun String.camelToSnakeCase(): String = buildString {
    this@camelToSnakeCase.forEach { c ->
        val lowerC = c.lowercase()
        append(if (c.isUpperCase() && this.isNotEmpty()) "_$lowerC" else lowerC)
    }
}
