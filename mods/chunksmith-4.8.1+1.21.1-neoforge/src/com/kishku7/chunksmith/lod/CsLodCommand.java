package com.kishku7.chunksmith.lod;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.lod.net.CsLodServerNet;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class CsLodCommand {
   private CsLodCommand() {
   }

   public static LiteralArgumentBuilder<CommandSourceStack> buildServerNode() {
      LiteralArgumentBuilder<CommandSourceStack> lod = Commands.literal("lod");
      lod.then(
         Commands.literal("status")
            .executes(
               context -> {
                  CommandSourceStack source = (CommandSourceStack)context.getSource();
                  ServerLevel level = source.getLevel();
                  Path store = LodSupport.storeRoot(level);
                  long bytes = sizeOf(store);

                  long records;
                  try {
                     records = CsLodPresenceIndex.countRecords(store);
                  } catch (IOException e) {
                     records = -1L;
                  }

                  say(source, "dimension:   " + LodSupport.dimensionKey(level));
                  say(source, "world id:    " + orNone(LodSupport.worldId(level.getServer())));
                  say(source, "store:       " + store);
                  say(source, "exists:      " + Files.isDirectory(store));
                  say(source, "records:     " + (records < 0L ? "unreadable" : Long.toString(records)));
                  say(source, "size:        " + bytes / 1024L + " KB");
                  say(source, "decision:    " + LodSupport.describeDecision(level.getServer()));
                  say(source, "backchannel: " + CsLodServerNet.describe());
                  say(
                     source,
                     "explored:    "
                        + ExploredLod.describe(ChunksmithProvider.isLoaded() && ChunksmithProvider.get().getConfig().isLodFromExploredChunksEnabled())
                  );
                  String rendererLine = renderers().replaceFirst("^\\s*\\|\\s*", "");
                  if (!rendererLine.isEmpty()) {
                     say(source, "renderers:   " + rendererLine);
                  }

                  return 1;
               }
            )
      );
      lod.then(
         Commands.literal("token")
            .then(
               Commands.argument("player", EntityArgument.player())
                  .executes(
                     context -> {
                        ServerPlayer target = EntityArgument.getPlayer(context, "player");
                        String token = CsLodServerNet.issueFor(target);
                        if (token == null) {
                           ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("[chunksmith] the LOD backchannel is not running"));
                           return 0;
                        } else {
                           ((CommandSourceStack)context.getSource())
                              .sendSuccess(() -> Component.literal("[chunksmith] token for " + target.getGameProfile().getName() + ": " + token), false);
                           return 1;
                        }
                     }
                  )
            )
      );
      return lod;
   }

   private static void say(CommandSourceStack source, String line) {
      source.sendSuccess(() -> Component.literal("[chunksmith] " + line), false);
   }

   private static String orNone(String value) {
      return value != null && !value.isEmpty() ? value : "(none -- store not writable)";
   }

   private static String renderers() {
      return " | voxy: " + (CsLodVoxyInjector.voxyAvailable() ? "available" : "not available") + " | dh: " + dhStatus();
   }

   private static String dhStatus() {
      if (!LodPlatform.isModLoaded("distanthorizons")) {
         return "not installed";
      }

      try {
         return CsLodDhSupport.describe();
      } catch (LinkageError error) {
         return "incompatible";
      }
   }

   private static long sizeOf(Path dir) {
      if (!Files.isDirectory(dir)) {
         return 0L;
      }

      try (Stream<Path> walk = Files.walk(dir)) {
         return walk.filter(x$0 -> Files.isRegularFile(x$0)).mapToLong(path -> {
            try {
               return Files.size(path);
            } catch (Exception e) {
               return 0L;
            }
         }).sum();
      } catch (Exception e) {
         return 0L;
      }
   }
}
