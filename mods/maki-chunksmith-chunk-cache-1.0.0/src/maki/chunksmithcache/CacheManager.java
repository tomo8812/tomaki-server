package maki.chunksmithcache;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.lod.HorizonGuard;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CacheManager {
   private static final Logger LOG = LoggerFactory.getLogger("MakiChunkCache");
   private static final int SCHEMA = 1;
   private static final AtomicLong LAST_QUEUE_WARNING = new AtomicLong();
   private static final Set<String> ROTATED = ConcurrentHashMap.newKeySet();
   private static final ThreadPoolExecutor WRITE_IO = new ThreadPoolExecutor(
      1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128), daemonFactory("maki-chunk-cache-write"), new AbortPolicy()
   );
   private static final ThreadPoolExecutor READ_IO = new ThreadPoolExecutor(
      1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(32), daemonFactory("maki-chunk-cache-read"), new AbortPolicy()
   );

   private CacheManager() {
   }

   public static void captureFromChunksmith(ServerLevel var0, LevelChunk var1) {
      if (var0 != null && var1 != null && !BackfillGuard.active(var0, var1.getPos())) {
         if (HorizonGuard.drop(var0, var1.getPos())) {
            capture(var0, var1, false);
         }
      }
   }

   static CompletableFuture<Boolean> capture(ServerLevel var0, LevelChunk var1, boolean var2) {
      String var3 = Fingerprint.forLevel(var0);
      if (var3 == null) {
         return CompletableFuture.completedFuture(false);
      }

      CompoundTag var4;
      try {
         var4 = ChunkSerializer.write(var0, var1);
      } catch (Throwable var14) {
         LOG.warn("Could not serialize chunk {} for cache; normal regeneration remains the fallback", new Object[]{var1.getPos(), var14});
         return CompletableFuture.completedFuture(false);
      }

      CompoundTag var5 = new CompoundTag();
      var5.putInt("schema", 1);
      var5.putString("fingerprint", var3);
      var5.putString("dimension", var0.dimension().location().toString());
      var5.putInt("x", var1.getPos().x);
      var5.putInt("z", var1.getPos().z);
      CompoundTagBridge.put(var5, "chunk", var4);
      rotateGenerations(var0, var3);
      Path var6 = path(var0, var1.getPos(), var3);
      CompletableFuture var7 = new CompletableFuture();
      Runnable var8 = () -> {
         try {
            Files.createDirectories(var6.getParent());
            Path var3x = var6.resolveSibling(var6.getFileName().toString() + ".tmp-" + Long.toUnsignedString(System.nanoTime()));
            NbtIo.writeCompressed(var5, var3x);
            moveAtomically(var3x, var6);
            var7.complete(true);
         } catch (Throwable var4x) {
            LOG.warn("Could not write chunk cache {}", new Object[]{var6, var4x});
            var7.complete(false);
         }
      };

      try {
         WRITE_IO.execute(var8);
      } catch (RejectedExecutionException var15) {
         long var10 = System.currentTimeMillis();
         long var12 = LAST_QUEUE_WARNING.get();
         if (var10 - var12 > 30000L && LAST_QUEUE_WARNING.compareAndSet(var12, var10)) {
            LOG.warn("Chunk cache write queue is full; skipping cache entries until it drains", new Object[0]);
         }

         var7.complete(false);
      }

      return var7;
   }

   public static CompletableFuture<Optional<CompoundTag>> loadIfPresent(ServerLevel var0, ChunkPos var1) {
      if (!BackfillGuard.active(var0, var1) && promotionAllowed(var0, var1)) {
         String var2 = Fingerprint.forLevel(var0);
         if (var2 == null) {
            return CompletableFuture.completedFuture(Optional.empty());
         }

         rotateGenerations(var0, var2);
         Path var3 = path(var0, var1, var2);
         if (!Files.isRegularFile(var3)) {
            return CompletableFuture.completedFuture(Optional.empty());
         }

         try {
            return CompletableFuture.supplyAsync(
               () -> {
                  try {
                     CompoundTag var4 = NbtIo.readCompressed(var3, NbtAccounter.unlimitedHeap());
                     if (var4.getInt("schema") == 1
                        && var2.equals(var4.getString("fingerprint"))
                        && var0.dimension().location().toString().equals(var4.getString("dimension"))
                        && var4.getInt("x") == var1.x
                        && var4.getInt("z") == var1.z
                        && var4.contains("chunk")) {
                        CompoundTag var5x = var4.getCompound("chunk");
                        PromotionLedger.mark(var0, var1);
                        return Optional.of(var5x);
                     } else {
                        return Optional.empty();
                     }
                  } catch (Throwable var6) {
                     quarantine(var3);
                     LOG.warn("Ignoring corrupt chunk cache {}; vanilla worldgen will be used", new Object[]{var3, var6});
                     return Optional.empty();
                  }
               },
               READ_IO
            );
         } catch (RejectedExecutionException var5) {
            return CompletableFuture.completedFuture(Optional.empty());
         }
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   private static boolean promotionAllowed(ServerLevel var0, ChunkPos var1) {
      try {
         if (ChunksmithProvider.isLoaded() && !ChunksmithProvider.get().getGenerationTasks().isEmpty()) {
            int var2 = var0.getServer().getPlayerList().getViewDistance() + 1;

            for (ServerPlayer var4 : var0.players()) {
               ChunkPos var5 = var4.chunkPosition();
               if (Math.abs(var5.x - var1.x) <= var2 && Math.abs(var5.z - var1.z) <= var2) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      } catch (Throwable var6) {
         return false;
      }
   }

   public static void onNormalSave(ServerLevel var0, ChunkAccess var1, boolean var2) {
      if (var2 && var0 != null && var1 != null && !BackfillGuard.active(var0, var1.getPos())) {
         String var3 = Fingerprint.forLevel(var0);
         if (var3 != null) {
            Path var4 = path(var0, var1.getPos(), var3);

            try {
               WRITE_IO.execute(() -> {
                  try {
                     Files.deleteIfExists(var4);
                  } catch (IOException var2x) {
                  }
               });
            } catch (RejectedExecutionException var6) {
            }
         }
      }
   }

   static boolean cacheExists(ServerLevel var0, ChunkPos var1) {
      String var2 = Fingerprint.forLevel(var0);
      return var2 != null && cacheExists(var0, var1, var2);
   }

   static boolean cacheExists(ServerLevel var0, ChunkPos var1, String var2) {
      rotateGenerations(var0, var2);
      return Files.isRegularFile(path(var0, var1, var2));
   }

   static Path lodRoot(ServerLevel var0) {
      return var0.getServer().getWorldPath(LevelResource.ROOT).resolve("chunksmith").resolve("lod").resolve(dimensionKey(var0));
   }

   static Path worldRegionRoot(ServerLevel var0) {
      Path var1 = var0.getServer().getWorldPath(LevelResource.ROOT);
      String var2 = var0.dimension().location().toString();
      if ("minecraft:overworld".equals(var2)) {
         return var1.resolve("region");
      }

      if (var2.startsWith("minecraft:")) {
         String var3 = var2.substring("minecraft:".length());
         if ("the_nether".equals(var3)) {
            return var1.resolve("DIM-1").resolve("region");
         }

         if ("the_end".equals(var3)) {
            return var1.resolve("DIM1").resolve("region");
         }
      }

      String[] var4 = var2.split(":", 2);
      return var4.length == 2 ? var1.resolve("dimensions").resolve(var4[0]).resolve(var4[1]).resolve("region") : var1.resolve("region");
   }

   static Path path(ServerLevel var0, ChunkPos var1, String var2) {
      int var3 = Math.floorDiv(var1.x, 32);
      int var4 = Math.floorDiv(var1.z, 32);
      return var0.getServer()
         .getWorldPath(LevelResource.ROOT)
         .resolve("chunksmith_chunk_cache")
         .resolve(dimensionKey(var0))
         .resolve(var2)
         .resolve("r." + var3 + "." + var4)
         .resolve("c." + var1.x + "." + var1.z + ".nbt.gz");
   }

   static String dimensionKey(ServerLevel var0) {
      return var0.dimension().location().toString().replace(':', '_').replace('/', '_');
   }

   private static void rotateGenerations(ServerLevel var0, String var1) {
      String var2 = dimensionKey(var0) + "|" + var1;
      if (ROTATED.add(var2)) {
         Path var3 = var0.getServer().getWorldPath(LevelResource.ROOT).resolve("chunksmith_chunk_cache").resolve(dimensionKey(var0));

         try {
            WRITE_IO.execute(() -> {
               try {
                  if (!Files.isDirectory(var3)) {
                     return;
                  }

                  try (Stream var3x = Files.list(var3)) {
                     for (Path var5x : var3x.toList()) {
                        String var6 = var5x.getFileName().toString();
                        if (Files.isDirectory(var5x) && !var6.equals(var1) && var6.matches("[0-9a-f]{24}")) {
                           deleteTree(var5x);
                           LOG.info("Removed stale chunk-cache generation {} for {}", new Object[]{var6, dimensionKey(var0)});
                        }
                     }
                  }
               } catch (Throwable var9) {
                  LOG.warn("Could not rotate stale chunk-cache generations for {}", new Object[]{dimensionKey(var0), var9});
               }
            });
         } catch (RejectedExecutionException var5) {
            ROTATED.remove(var2);
         }
      }
   }

   private static void deleteTree(Path var0) throws IOException {
      try (Stream var1 = Files.walk(var0)) {
         for (Path var3 : var1.sorted(Comparator.reverseOrder()).toList()) {
            Files.deleteIfExists(var3);
         }
      }
   }

   private static void quarantine(Path var0) {
      try {
         Files.move(var0, var0.resolveSibling(var0.getFileName() + ".corrupt"), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException var2) {
      }
   }

   private static void moveAtomically(Path var0, Path var1) throws IOException {
      try {
         Files.move(var0, var1, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException var3) {
         Files.move(var0, var1, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   private static ThreadFactory daemonFactory(String var0) {
      return var1 -> {
         Thread var2 = new Thread(var1, var0);
         var2.setDaemon(true);
         return var2;
      };
   }
}
