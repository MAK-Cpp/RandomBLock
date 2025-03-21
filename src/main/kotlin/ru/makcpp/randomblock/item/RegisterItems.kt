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


fun registerItem(name: String, itemFactory: (Settings) -> Item, settings: Settings): Item {
    // Create the item key.
    val itemKey = RegistryKey.of<Item>(RegistryKeys.ITEM, Identifier.of(RandomBlock.MOD_ID, name))

    // Create the item instance.
    val item: Item = itemFactory(settings.registryKey(itemKey))

    // Register the item.
    Registry.register(Registries.ITEM, itemKey, item)

    return item
}

val testItem = registerItem("test", ::Item, Settings())
val randomBlockPlacerItem = registerItem("random_block_placer", ::RandomBlockPlacerItem, Settings())

fun registerItems() {
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register {
        with(it) {
            add(testItem)
            add(randomBlockPlacerItem)
        }
    }
}