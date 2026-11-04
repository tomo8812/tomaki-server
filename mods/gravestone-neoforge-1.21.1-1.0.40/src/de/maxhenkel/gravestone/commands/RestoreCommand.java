package de.maxhenkel.gravestone.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.corelib.death.Death;
import de.maxhenkel.gravestone.corelib.death.DeathManager;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RestoreCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> literalBuilder = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("restore")
         .requires(commandSource -> commandSource.hasPermission(2));
      Command<CommandSourceStack> add = commandSource -> {
         UUID deathID = UuidArgument.getUuid(commandSource, "death_id");
         ServerPlayer player = EntityArgument.getPlayer(commandSource, "target");
         Death death = DeathManager.getDeath(player.serverLevel(), deathID);
         if (death == null) {
            ((CommandSourceStack)commandSource.getSource())
               .sendFailure(Component.translatable("message.gravestone.death_id_not_found", new Object[]{deathID.toString()}));
            return 0;
         }

         for (ItemStack stack : death.getAllItems()) {
            if (!player.getInventory().add(stack)) {
               player.drop(stack, false);
            }
         }

         ((CommandSourceStack)commandSource.getSource())
            .sendSuccess(() -> Component.translatable("message.gravestone.restore.success", new Object[]{player.getDisplayName()}), true);
         return 1;
      };
      Command<CommandSourceStack> replace = commandSource -> {
         UUID deathID = UuidArgument.getUuid(commandSource, "death_id");
         ServerPlayer player = EntityArgument.getPlayer(commandSource, "target");
         Death death = DeathManager.getDeath(player.serverLevel(), deathID);
         if (death == null) {
            ((CommandSourceStack)commandSource.getSource())
               .sendFailure(Component.translatable("message.gravestone.death_id_not_found", new Object[]{deathID.toString()}));
            return 0;
         }

         player.getInventory().clearContent();

         for (ItemStack stack : ((GraveStoneBlock)Main.GRAVESTONE.get()).fillPlayerInventory(player, death)) {
            player.drop(stack, false);
         }

         ((CommandSourceStack)commandSource.getSource())
            .sendSuccess(() -> Component.translatable("message.gravestone.restore.success", new Object[]{player.getDisplayName()}), true);
         return 1;
      };
      literalBuilder.then(
         Commands.argument("target", EntityArgument.player())
            .then(
               ((RequiredArgumentBuilder)Commands.argument("death_id", UuidArgument.uuid()).then(Commands.literal("replace").executes(replace)))
                  .then(Commands.literal("add").executes(add))
            )
      );
      LiteralCommandNode<CommandSourceStack> register = dispatcher.register(literalBuilder);
      LiteralArgumentBuilder<CommandSourceStack> alias = (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)Commands.literal(
               "restoreinventory"
            )
            .requires(commandSource -> commandSource.hasPermission(2)))
         .redirect(register);
      dispatcher.register(alias);
   }
}
