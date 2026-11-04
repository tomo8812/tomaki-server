package dev.terminalmc.clientsort.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.terminalmc.clientsort.config.ServerConfig;
import dev.terminalmc.clientsort.util.Localization;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Commands {
   private Commands() {
      throw new UnsupportedOperationException("This class cannot be instantiated.");
   }

   public static <S> void register(CommandDispatcher<S> dispatcher, CommandBuildContext buildCtx) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)net.minecraft.commands.Commands.literal("clientsort")
               .requires(sourceStack -> sourceStack.hasPermission(2)))
            .then(
               net.minecraft.commands.Commands.literal("reload")
                  .executes(
                     ctx -> {
                        MutableComponent msg = Component.empty()
                           .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
                           .append(Component.literal("Client").withStyle(ChatFormatting.AQUA))
                           .append(Component.literal("Sort").withStyle(ChatFormatting.DARK_AQUA))
                           .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))
                           .withStyle(ChatFormatting.GRAY);
                        ServerConfig.reloadAndSave();
                        msg.append(
                           Localization.localized("message", "configReloaded", Component.literal("clientsort-server.json").withStyle(ChatFormatting.GOLD))
                        );
                        ((CommandSourceStack)ctx.getSource()).sendSystemMessage(msg);
                        return 1;
                     }
                  )
            )
      );
   }
}
