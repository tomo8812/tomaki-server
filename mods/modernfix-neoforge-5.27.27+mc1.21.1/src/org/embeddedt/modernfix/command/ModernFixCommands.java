package org.embeddedt.modernfix.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.embeddedt.modernfix.duck.IProfilingServerFunctionManager;

public class ModernFixCommands {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("modernfix")
            .then(((LiteralArgumentBuilder)Commands.literal("mcfunctions").requires(source -> source.hasPermission(3))).executes(context -> {
               ServerLevel level = ((CommandSourceStack)context.getSource()).getLevel();
               if (level == null) {
                  ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Couldn't find server level"));
                  return 0;
               } else if (!(level.getServer().getFunctions() instanceof IProfilingServerFunctionManager profiler)) {
                  ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("ModernFix mcfunction profiling is not enabled on this server."));
                  return 0;
               } else {
                  ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal("mcfunction runtime breakdown:"), false);

                  for (String line : profiler.mfix$getProfilingResults().split("\n")) {
                     ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal(line), false);
                  }

                  return 1;
               }
            }))
      );
   }
}
