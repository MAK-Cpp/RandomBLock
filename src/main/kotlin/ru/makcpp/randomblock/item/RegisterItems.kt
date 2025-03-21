package ru.makcpp.randomblock.item

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import ru.makcpp.randomblock.RandomBlock


fun register(name: String, itemFactory: (Item.Settings) -> Item, settings: Item.Settings): Item {
    // Create the item key.
    val itemKey = RegistryKey.of<Item?>(RegistryKeys.ITEM, Identifier.of(RandomBlock.MOD_ID, name))

    // Create the item instance.
    val item: Item = itemFactory(settings.registryKey(itemKey))

    // Register the item.
    Registry.register(Registries.ITEM, itemKey, item)

    return item
}

val testItem = register("test", ::Item, Item.Settings())

fun registerItems() {
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register { it.add(testItem) }
}