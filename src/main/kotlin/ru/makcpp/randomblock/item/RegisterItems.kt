package ru.makcpp.randomblock.item

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.Item.Settings
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import ru.makcpp.randomblock.RandomBlock
import ru.makcpp.randomblock.util.camelToSnakeCase

inline fun <reified T : ModItem> getItemId(): Identifier = T::class.simpleName
    ?.let { Identifier.of(RandomBlock.MOD_ID, it.camelToSnakeCase()) }
    ?: throw IllegalArgumentException("No class name for ModItem")

inline fun <reified T : ModItem> registerItem(itemFactory: (Settings) -> T, settings: Settings): T {
    // Create the item key.
    val itemKey = RegistryKey.of<Item>(RegistryKeys.ITEM, getItemId<T>())

    // Create the item instance.
    val item: T = itemFactory(settings.registryKey(itemKey))

    // Register the item.
    Registry.register(Registries.ITEM, itemKey, item)

    return item
}

val randomBlockPlacerItem = registerItem(::RandomBlockPlacerItem, Settings().maxCount(1))

fun registerItems() {
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register {
        with(it) {
            add(randomBlockPlacerItem)
        }
    }
}