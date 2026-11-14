package dev.diorama.web;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class WebMapCommand {
   private WebMapCommand() {
   }

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("diorama-web").requires(source -> source.hasPermission(2)))
               .then(
                  Commands.literal("status")
                     .executes(
                        context -> {
                           WebMap map = WebMap.get();
                           ((CommandSourceStack)context.getSource())
                              .sendSuccess(
                                 () -> Component.literal(
                                    map == null ? "The web map is off (web-map in config/diorama-server.properties)" : "Web map: " + map.status()
                                 ),
                                 false
                              );
                           return 1;
                        }
                     )
               ))
            .then(Commands.literal("redraw").executes(context -> {
               WebMap map = WebMap.get();
               if (map == null) {
                  ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("The web map is off (web-map in config/diorama-server.properties)"));
                  return 0;
               } else {
                  map.redrawAll();
                  ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal("Drawing the whole web map again"), true);
                  return 1;
               }
            }))
      );
   }
}
