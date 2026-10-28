package ru.makcpp.randomblock.command

import com.mojang.brigadier.Command
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.world.item.ItemStack
import ru.makcpp.randomblock.item.RANDOM_BLOCK_PLACER_ITEM

val GET_RBP_ITEM =
    Command<CommandSourceStack> { context ->
        context.source.player?.let { player ->
            if (player.inventory.nonEquipmentItems
                    .map { it.item }
                    .toSet()
                    .contains(RANDOM_BLOCK_PLACER_ITEM)
            ) {
                player.sendSystemMessage(
                    Component.translatable("message.error.rbp_already_in_inventory").withColor(CommonColors.RED),
                )
            } else {
                player.addItem(ItemStack(RANDOM_BLOCK_PLACER_ITEM))
            }
        }
        1
    }

fun registerCommands() = CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
    dispatcher.register(literal("RBP").executes(GET_RBP_ITEM))
}
