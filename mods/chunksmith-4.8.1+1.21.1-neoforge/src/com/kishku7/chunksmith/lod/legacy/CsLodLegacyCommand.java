package com.kishku7.chunksmith.lod.legacy;

import com.kishku7.chunksmith.lod.net.CsLodServerNet;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class CsLodLegacyCommand {
   private CsLodLegacyCommand() {
   }

   public static LiteralArgumentBuilder<CommandSourceStack> build() {
      LiteralArgumentBuilder<CommandSourceStack> root = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("cslod")
         .requires(CsLodLegacyCommand::outOfDateClient);
      root.executes(context -> tell((CommandSourceStack)context.getSource()));
      RequiredArgumentBuilder<CommandSourceStack, String> rest = RequiredArgumentBuilder.argument("rest", StringArgumentType.greedyString());
      rest.executes(context -> tell((CommandSourceStack)context.getSource()));
      root.then(rest);
      return root;
   }

   private static boolean outOfDateClient(CommandSourceStack source) {
      return source.getEntity() instanceof ServerPlayer player && CsLodServerNet.isLegacyClient(player);
   }

   private static int tell(CommandSourceStack source) {
      source.sendFailure(
         Component.literal(
            "[chunksmith] /cslod is gone in Chunksmith 4. Update your Chunksmith client: the LOD commands you want are /csclient, and they run on your machine."
         )
      );
      return 0;
   }
}
