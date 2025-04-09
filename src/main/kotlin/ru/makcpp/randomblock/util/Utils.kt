package ru.makcpp.randomblock.util

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.world.World

fun World.isServer(): Boolean = !isClient

fun String.camelToSnakeCase(): String = buildString {
    this@camelToSnakeCase.forEach { c ->
        val lowerC = c.lowercase()
        append(if (c.isUpperCase() && this.isNotEmpty()) "_$lowerC" else lowerC)
    }
}

val Item.id: Identifier
    get() =
        Registries.ITEM
            .getKey(this)
            .get()
            .registry
