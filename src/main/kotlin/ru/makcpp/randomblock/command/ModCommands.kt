package ru.makcpp.randomblock.command

import com.mojang.brigadier.Command
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.item.ItemStack
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Colors
import ru.makcpp.randomblock.item.RANDOM_BLOCK_PLACER_ITEM

val GET_RBP_ITEM =
    Command<ServerCommandSource> { context ->
        context.source.player?.let { player ->
            if (player.inventory.mainStacks
                    .map { it.item }
                    .toSet()
                    .contains(RANDOM_BLOCK_PLACER_ITEM)
            ) {
                player.sendMessage(Text.translatable("message.error.rbp_already_in_inventory").withColor(Colors.RED))
            } else {
                player.giveItemStack(ItemStack(RANDOM_BLOCK_PLACER_ITEM))
            }
        }
        1
    }

fun registerCommands() =
    CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
        dispatcher.register(literal("RBP").executes(GET_RBP_ITEM))
    }
