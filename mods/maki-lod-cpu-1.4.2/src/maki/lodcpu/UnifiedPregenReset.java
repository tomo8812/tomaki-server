package maki.lodcpu;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.GenerationTask;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class UnifiedPregenReset {
   private static final Logger LOG = LoggerFactory.getLogger("MakiLodFollow");
   private static final String WORLD = "minecraft:overworld";
   private static MinecraftServer completedServer;
   private static MinecraftServer preparedServer;
   private static boolean storagePrepared;
   private static long retryAfter;

   private UnifiedPregenReset() {
   }

   static synchronized void prepareBeforeLod(MinecraftServer server) {
      Path world = server.getWorldPath(LevelResource.ROOT);
      Path marker = marker(world);
      if (Files.isRegularFile(marker)) {
         completedServer = server;
         preparedServer = server;
         storagePrepared = true;
      } else {
         preparedServer = server;
         storagePrepared = false;

         try {
            deleteTree(world.resolve("chunksmith").resolve("lod").resolve("minecraft_overworld"));
            resetPregenCacheDimension(world.resolve("chunksmith_chunk_cache").resolve("minecraft_overworld"));
            storagePrepared = true;
            LOG.info(
               "Issue #83 disposable LOD/cache storage was reset before Chunksmith LOD startup; owned task progress will be finalized after Chunksmith initializes."
            );
         } catch (Exception error) {
            LOG.warn(
               "Issue #83 disposable storage reset failed before Chunksmith LOD startup; unified pregen stays paused for this server session and will retry at the next server start",
               error
            );
         }
      }
   }

   static synchronized boolean ensure(MinecraftServer server) {
      if (completedServer == server) {
         return true;
      }

      Path world = server.getWorldPath(LevelResource.ROOT);
      Path marker = marker(world);
      if (Files.isRegularFile(marker)) {
         completedServer = server;
         preparedServer = server;
         storagePrepared = true;
         return true;
      }

      if (preparedServer == server && storagePrepared) {
         long now = System.currentTimeMillis();
         if (now < retryAfter) {
            return false;
         }

         try {
            if (ChunksmithProvider.isLoaded() && ChunksmithProvider.get() != null) {
               Chunksmith chunky = ChunksmithProvider.get();
               GenerationTask active = (GenerationTask)chunky.getGenerationTasks().get("minecraft:overworld");
               if (active != null) {
                  TaskOwnership.PersistedOwner owner = TaskOwnership.persistedOwner(chunky, "minecraft:overworld");
                  if (owner != TaskOwnership.PersistedOwner.CURRENT_SESSION && owner != TaskOwnership.PersistedOwner.PREVIOUS_SESSION) {
                     LOG.warn("Unified pregen reset is waiting for the protected external overworld Chunksmith task to stop.");
                     retryAfter = now + 5000L;
                     return false;
                  }

                  TaskOwnership.claim(active, "Issue83-reset");
                  active.stop(false);
                  retryAfter = now + 1000L;
                  return false;
               }
            }

            if (ChunksmithProvider.isLoaded() && ChunksmithProvider.get() != null) {
               Chunksmith chunky = ChunksmithProvider.get();
               TaskOwnership.PersistedOwner owner = TaskOwnership.persistedOwner(chunky, "minecraft:overworld");
               if (owner != TaskOwnership.PersistedOwner.EXTERNAL) {
                  Files.deleteIfExists(chunky.getConfig().getDirectory().resolve("tasks").resolve("minecraft").resolve("overworld.properties"));
               }
            }

            Files.createDirectories(marker.getParent());
            Files.writeString(
               marker,
               "issue=83\nversion=1\nreset=overworld-lod,overworld-pregen-cache,owned-overworld-task\npreserved=promotion-claims,other-dimensions,normal-world-data\n",
               StandardOpenOption.CREATE_NEW,
               StandardOpenOption.WRITE
            );
            completedServer = server;
            LOG.info(
               "Issue #83 unified pregen reset complete: old LOD, pregen FULL cache, and owned follow task progress were cleared; normal world/player/Millenaire/VS2 data were untouched."
            );
            return true;
         } catch (Exception error) {
            retryAfter = now + 30000L;
            LOG.warn("Unified pregen task-state cleanup is incomplete; generation remains paused and this non-destructive phase will retry", error);
            return false;
         }
      } else {
         return false;
      }
   }

   static synchronized void stopped(MinecraftServer server) {
      if (completedServer == server) {
         completedServer = null;
      }

      if (preparedServer == server) {
         preparedServer = null;
         storagePrepared = false;
      }

      retryAfter = 0L;
   }

   private static Path marker(Path world) {
      return world.resolve("data/maki_pregen_unified_v1.reset");
   }

   private static void resetPregenCacheDimension(Path dimension) throws IOException {
      if (Files.isDirectory(dimension)) {
         preservePromotionClaims(dimension);

         try (Stream<Path> children = Files.list(dimension)) {
            for (Path child : children.toList()) {
               if (!".promotions".equals(child.getFileName().toString())) {
                  deleteTree(child);
               }
            }
         }
      }
   }

   private static void preservePromotionClaims(Path dimension) throws IOException {
      Path promotions = dimension.resolve(".promotions");
      Files.createDirectories(promotions);

      try (Stream<Path> walk = Files.walk(dimension)) {
         for (Path source : walk.filter(x$0 -> Files.isRegularFile(x$0))
            .filter(path -> path.getFileName().toString().endsWith(".nbt.gz.in-use"))
            .filter(path -> !path.startsWith(promotions))
            .toList()) {
            String region = source.getParent().getFileName().toString();
            Path target = promotions.resolve(region).resolve(source.getFileName());
            if (Files.exists(target)) {
               target = promotions.resolve(".conflicts").resolve(UUID.randomUUID().toString()).resolve(source.getFileName());
            }

            Files.createDirectories(target.getParent());

            try {
               Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException unsupported) {
               Files.move(source, target);
            }
         }
      }
   }

   private static void deleteTree(Path root) throws IOException {
      if (Files.exists(root)) {
         try (Stream<Path> walk = Files.walk(root)) {
            for (Path path : walk.sorted(Comparator.reverseOrder()).toList()) {
               Files.deleteIfExists(path);
            }
         }
      }
   }
}
