package ru.makcpp.randomblock.util

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

fun Level.isServer(): Boolean = !isClientSide

fun String.camelToSnakeCase(): String = buildString {
    this@camelToSnakeCase.forEach { c ->
        val lowerC = c.lowercase()
        append(if (c.isUpperCase() && this.isNotEmpty()) "_$lowerC" else lowerC)
    }
}

val Item.id: ResourceLocation
    get() =
        BuiltInRegistries.ITEM
            .getResourceKey(this)
            .get()
            .registry()
