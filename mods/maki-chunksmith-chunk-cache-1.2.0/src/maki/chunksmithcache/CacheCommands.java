package maki.chunksmithcache;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

final class CacheCommands {
   private CacheCommands() {
   }

   static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("makichunkcache")
                  .requires(source -> source.hasPermission(2)))
               .then(Commands.literal("status").executes(context -> status((CommandSourceStack)context.getSource()))))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("quarantine")
                        .then(Commands.literal("list").executes(context -> list((CommandSourceStack)context.getSource()))))
                     .then(
                        Commands.literal("discard")
                           .then(
                              Commands.argument("id", StringArgumentType.word())
                                 .executes(context -> discard((CommandSourceStack)context.getSource(), StringArgumentType.getString(context, "id")))
                           )
                     ))
                  .then(
                     Commands.literal("restore")
                        .then(
                           Commands.argument("id", StringArgumentType.word())
                              .executes(context -> restore((CommandSourceStack)context.getSource(), StringArgumentType.getString(context, "id")))
                        )
                  )
            )
      );
   }

   private static int status(CommandSourceStack source) {
      Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(source.getLevel());
      if (snapshot == null) {
         source.sendFailure(Component.literal("Chunk-cache fingerprint is still being computed or failed closed."));
         return 0;
      } else {
         FingerprintPolicy policy = snapshot.policy();
         source.sendSuccess(
            () -> Component.literal(
               "Chunk-cache fingerprint="
                  + snapshot.fingerprint()
                  + ", legacy="
                  + snapshot.legacyFingerprint()
                  + ", quarantine retention="
                  + policy.retentionDays()
                  + "d, maxGenerations="
                  + policy.maxGenerations()
                  + ", maxBytes="
                  + policy.maxBytes()
                  + ", minProtection="
                  + policy.minProtectionHours()
                  + "h"
            ),
            false
         );
         return 1;
      }
   }

   private static int list(CommandSourceStack source) {
      ServerLevel level = source.getLevel();
      boolean queued = CacheManager.submitMaintenance(() -> {
         try {
            List<GenerationStore.QuarantineInfo> entries = GenerationStore.listQuarantines(level);
            source.getServer().execute(() -> {
               source.sendSuccess(() -> Component.literal("Chunk-cache quarantines for " + CacheManager.dimensionKey(level) + ": " + entries.size()), false);

               for (GenerationStore.QuarantineInfo entry : entries.stream().limit(20L).toList()) {
                  source.sendSuccess(() -> Component.literal("- " + entry.id() + " entries=" + entry.entries() + " bytes=" + entry.bytes()), false);
               }

               if (entries.size() > 20) {
                  source.sendSuccess(() -> Component.literal("- ... " + (entries.size() - 20) + " more"), false);
               }
            });
         } catch (Throwable error) {
            source.getServer().execute(() -> source.sendFailure(Component.literal("Could not list quarantines: " + error.getClass().getSimpleName())));
         }
      });
      if (!queued) {
         source.sendFailure(Component.literal("Chunk-cache maintenance queue is full."));
         return 0;
      } else {
         source.sendSuccess(() -> Component.literal("Queued chunk-cache quarantine listing."), false);
         return 1;
      }
   }

   private static int discard(CommandSourceStack source, String id) {
      ServerLevel level = source.getLevel();
      boolean queued = CacheManager.submitMaintenance(() -> {
         String result = GenerationStore.discardQuarantine(level, id);
         source.getServer().execute(() -> source.sendSuccess(() -> Component.literal(result), false));
      });
      if (!queued) {
         source.sendFailure(Component.literal("Chunk-cache maintenance queue is full."));
         return 0;
      } else {
         source.sendSuccess(() -> Component.literal("Queued quarantine discard for " + id + "."), false);
         return 1;
      }
   }

   private static int restore(CommandSourceStack source, String id) {
      ServerLevel level = source.getLevel();
      Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(level);
      if (snapshot == null) {
         source.sendFailure(Component.literal("Restore refused: current compatibility fingerprint is unavailable."));
         return 0;
      } else {
         boolean queued = CacheManager.submitMaintenance(() -> {
            String result = GenerationStore.restoreQuarantine(level, id, snapshot);
            source.getServer().execute(() -> source.sendSuccess(() -> Component.literal(result), false));
         });
         if (!queued) {
            source.sendFailure(Component.literal("Chunk-cache maintenance queue is full."));
            return 0;
         } else {
            source.sendSuccess(() -> Component.literal("Queued quarantine restore check for " + id + "."), false);
            return 1;
         }
      }
   }
}
