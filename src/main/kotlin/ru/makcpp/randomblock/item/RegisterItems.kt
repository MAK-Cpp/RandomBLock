package ru.makcpp.randomblock.item

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item.Properties
import ru.makcpp.randomblock.RandomBlock
import ru.makcpp.randomblock.util.camelToSnakeCase

private inline fun <reified T : ModItem> generateItemId(): Identifier =
    T::class
        .simpleName
        ?.let { Identifier.fromNamespaceAndPath(RandomBlock.MOD_ID, it.camelToSnakeCase()) }
        ?: throw IllegalArgumentException("No class name for ModItem")

private inline fun <reified T : ModItem> registerItem(itemFactory: (Properties) -> T, settings: Properties): T {
    // Create the item key.
    val itemKey = ResourceKey.create(Registries.ITEM, generateItemId<T>())
    // Create the item instance.
    val item = itemFactory(settings.setId(itemKey))
    // Register the item.
    Registry.register(BuiltInRegistries.ITEM, itemKey, item)

    return item
}

val RANDOM_BLOCK_PLACER_ITEM = registerItem(::RandomBlockPlacerItem, Properties().stacksTo(1))

fun registerItems() {
    CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register {
        with(it) {
            accept(RANDOM_BLOCK_PLACER_ITEM)
        }
    }
}
