package ru.makcpp.randomblock.item

import net.minecraft.item.Item

/**
 * Абстракция над Item для того, чтобы отделить обычные предметы от предметов мода.
 *
 * (?) Проблема: не совместимо с BlockItem, т.к. это тоже наследник от Item
 */
abstract class ModItem(settings: Settings) : Item(settings)
