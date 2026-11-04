package dev.terminalmc.clientsort.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.terminalmc.clientsort.client.gui.screen.config.ConfigScreenProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;

public class Commands {
   private Commands() {
      throw new UnsupportedOperationException("This class cannot be instantiated.");
   }

   public static <S> void register(CommandDispatcher<S> dispatcher, CommandBuildContext buildCtx) {
      Minecraft mc = Minecraft.getInstance();
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)net.minecraft.commands.Commands.literal("clientsort").executes(ctx -> {
         mc.doRunTask(() -> mc.setScreen(ConfigScreenProvider.getConfigScreen(null)));
         return 1;
      })).then(net.minecraft.commands.Commands.argument("_", StringArgumentType.greedyString()).executes(ctx -> {
         String cmd = StringArgumentType.getString(ctx, "_");
         mc.player.connection.send(new ServerboundChatCommandPacket("clientsort " + cmd));
         return 1;
      })));
   }
}
