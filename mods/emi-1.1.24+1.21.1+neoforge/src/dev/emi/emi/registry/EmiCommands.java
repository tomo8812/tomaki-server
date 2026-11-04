package dev.emi.emi.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.emi.emi.network.CommandS2CPacket;
import dev.emi.emi.network.EmiNetwork;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class EmiCommands {
   public static final byte VIEW_RECIPE = 1;
   public static final byte VIEW_TREE = 2;
   public static final byte TREE_GOAL = 17;
   public static final byte TREE_RESOLUTION = 18;

   public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("emi").requires(source -> source.hasPermission(2)))
               .then(
                  ((LiteralArgumentBuilder)Commands.literal("view")
                        .then(Commands.literal("recipe").then(Commands.argument("id", ResourceLocationArgument.id()).executes(context -> {
                           send(
                              ((CommandSourceStack)context.getSource()).getPlayer(),
                              (byte)1,
                              (ResourceLocation)context.getArgument("id", ResourceLocation.class)
                           );
                           return 1;
                        }))))
                     .then(Commands.literal("tree").executes(context -> {
                        send(((CommandSourceStack)context.getSource()).getPlayer(), (byte)2, null);
                        return 1;
                     }))
               ))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("tree")
                     .then(Commands.literal("goal").then(Commands.argument("id", ResourceLocationArgument.id()).executes(context -> {
                        send(
                           ((CommandSourceStack)context.getSource()).getPlayer(), (byte)17, (ResourceLocation)context.getArgument("id", ResourceLocation.class)
                        );
                        return 1;
                     }))))
                  .then(Commands.literal("resolution").then(Commands.argument("id", ResourceLocationArgument.id()).executes(context -> {
                     send(((CommandSourceStack)context.getSource()).getPlayer(), (byte)18, (ResourceLocation)context.getArgument("id", ResourceLocation.class));
                     return 1;
                  })))
            )
      );
   }

   private static void send(ServerPlayer player, byte type, @Nullable ResourceLocation id) {
      EmiNetwork.sendToClient(player, new CommandS2CPacket(type, id));
   }
}
