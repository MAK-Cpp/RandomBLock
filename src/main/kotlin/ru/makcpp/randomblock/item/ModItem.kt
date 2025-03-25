package ru.makcpp.randomblock.item

import net.minecraft.item.Item
import net.minecraft.registry.Registries

/**
 * Абстракция над Item для того, чтобы отделить обычные предметы от предметов мода.
 *
 * (?) Проблема: не совместимо с BlockItem, т.к. это тоже наследник от Item
 */
abstract class ModItem(settings: Settings) : Item(settings) {
}

inline fun <reified T : ModItem> getItem(): T = Registries.ITEM.get(getItemId<T>()) as T