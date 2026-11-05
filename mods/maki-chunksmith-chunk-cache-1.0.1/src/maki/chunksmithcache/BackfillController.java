package maki.chunksmithcache;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.lod.LodSupport;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BackfillController {
   private static final TicketType<Unit> BACKFILL_TICKET = TicketType.create("maki_chunk_cache_backfill", (var0, var1) -> 0);
   private static final Logger LOG = LoggerFactory.getLogger("MakiChunkCache");
   private static final Map<MinecraftServer, BackfillController.State> STATES = new WeakHashMap<>();
   private static volatile List<ServerLevel> LEVELS = List.of();
   private static final long RESCAN_MILLIS = 600000L;
   private static final ExecutorService SCAN = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "maki-chunk-cache-scan");
      var1.setDaemon(true);
      return var1;
   });

   private BackfillController() {
   }

   public static List<ServerLevel> levels() {
      return LEVELS;
   }

   public static void tick(MinecraftServer var0) {
      Fingerprint.kick(var0);
      LEVELS = copyLevels(var0);
      BackfillController.State var1;
      synchronized (STATES) {
         var1 = STATES.computeIfAbsent(var0, var0x -> new BackfillController.State());
      }

      var1.ticks++;
      if (var1.busy) {
         if (var1.activeLevel != null && var1.activePos != null && playerNear(var1.activeLevel, var1.activePos)) {
            handoffToPlayer(var1);
         }
      } else if (var1.ticks % 20L == 0L
         && !var1.scanning
         && var0.getPlayerCount() == 0
         && !(var0.getAverageTickTimeNanos() / 1000000.0 > 40.0)
         && MillenaireBridge.canBackfillSafely()) {
         try {
            if (ChunksmithProvider.isLoaded() && !ChunksmithProvider.get().getGenerationTasks().isEmpty()) {
               return;
            }
         } catch (Throwable var7) {
            return;
         }

         ArrayList var8 = new ArrayList();

         for (ServerLevel var4 : LEVELS) {
            String var5 = Fingerprint.forLevel(var4);
            if (var5 != null) {
               var8.add(new BackfillController.ScanLevel(var4, var5));
            }
         }

         if (!var8.isEmpty()) {
            var1.scanning = true;
            CompletableFuture.<BackfillController.Candidate>supplyAsync(() -> findCandidate(var8, var1), SCAN)
               .whenComplete((var2, var3) -> var0.execute(() -> {
                  var1.scanning = false;
                  if (var3 == null && var2 != null && !var1.busy && var0.getPlayerCount() == 0 && !(var0.getAverageTickTimeNanos() / 1000000.0 > 40.0)) {
                     try {
                        if (ChunksmithProvider.isLoaded() && !ChunksmithProvider.get().getGenerationTasks().isEmpty()) {
                           return;
                        }
                     } catch (Throwable var7x) {
                        return;
                     }

                     ServerLevel var4x = var2.level();
                     BackfillController.Target var5x = var2.target();
                     ChunkPos var6 = var5x.pos();
                     if (var4x.getChunkSource().hasChunk(var6.x, var6.z)) {
                        advanceCursor(var4x, var1.cursor.get(CacheManager.dimensionKey(var4x)));
                     } else {
                        start(var4x, var5x, var1);
                     }
                  }
               }));
         }
      }
   }

   public static void onUnload(ServerLevel var0, LevelChunk var1) {
      if (BackfillGuard.active(var0, var1.getPos())) {
         BackfillGuard.clear(var0, var1.getPos());
         MinecraftServer var2 = var0.getServer();
         synchronized (STATES) {
            BackfillController.State var4 = STATES.get(var2);
            if (var4 != null && var0 == var4.activeLevel && var1.getPos().toLong() == var4.activePos.toLong()) {
               clearActive(var4);
            }
         }
      }
   }

   private static void start(ServerLevel var0, BackfillController.Target var1, BackfillController.State var2) {
      ChunkPos var3 = var1.pos();
      if (MillenaireBridge.canBackfillSafely()) {
         var2.busy = true;
         var2.activeLevel = var0;
         var2.activePos = var3;
         long var4 = ++var2.token;
         BackfillGuard.arm(var0, var3);
         Boolean var6 = MillenaireBridge.suppressNaturalSpawns(true);
         var2.previousSuppression = var6;
         if (MillenaireBridge.isLoaded() && var6 == null) {
            LOG.warn("Millenaire suppression could not be acquired; skipping chunk-cache backfill to preserve natural-generation semantics", new Object[0]);
            BackfillGuard.clear(var0, var3);
            clearActive(var2);
         } else {
            LOG.debug("Backfilling cached chunk {} in {}", new Object[]{var3, var0.dimension().location()});
            var0.getChunkSource().addRegionTicket(BACKFILL_TICKET, var3, 0, Unit.INSTANCE);
            var0.getChunkSource()
               .getChunkFuture(var3.x, var3.z, ChunkStatus.FULL, true)
               .whenComplete(
                  (var7, var8) -> var0.getServer()
                     .execute(
                        () -> {
                           if (active(var2, var4, var0, var3)) {
                              try {
                                 if (var8 != null || var7 == null) {
                                    fail(var0, var3, var2, var6, var4);
                                    return;
                                 }

                                 LevelChunk[] var9 = new LevelChunk[1];
                                 ChunkResultBridge.ifSuccess(var7, var1xxx -> {
                                    if (var1xxx instanceof LevelChunk var2xxx) {
                                       var9[0] = var2xxx;
                                    }
                                 });
                                 if (var9[0] == null) {
                                    fail(var0, var3, var2, var6, var4);
                                    return;
                                 }

                                 try {
                                    LodSupport.offer(var0, var9[0]);
                                 } catch (Throwable var11) {
                                    LOG.warn("Could not refresh LOD for backfill {}; leaving it pending for retry", new Object[]{var3, var11});
                                    fail(var0, var3, var2, var6, var4);
                                    return;
                                 }

                                 if (!MillenaireBridge.rememberPending(var0, var3)) {
                                    LOG.warn("Could not preserve Millenaire pending state for backfill {}; leaving it pending for retry", new Object[]{var3});
                                    fail(var0, var3, var2, var6, var4);
                                    return;
                                 }

                                 MillenaireBridge.restore(var6);
                                 var2.previousSuppression = null;
                                 LevelChunk var10 = var9[0];
                                 CompletableFuture.<Boolean>supplyAsync(() -> waitForLodRefresh(var1), SCAN)
                                    .whenComplete((var7xx, var8xx) -> var0.getServer().execute(() -> {
                                       if (active(var2, var4, var0, var3)) {
                                          if (!Boolean.TRUE.equals(var7xx)) {
                                             LOG.warn("Backfill LOD refresh was not durably observed for {}; leaving it pending for retry", new Object[]{var3});
                                             fail(var0, var3, var2, var6, var4);
                                          } else {
                                             CacheManager.capture(var0, var10, true).whenComplete((var5xxxx, var6xxxxx) -> var0.getServer().execute(() -> {
                                                if (active(var2, var4, var0, var3)) {
                                                   if (!Boolean.TRUE.equals(var5xxxx)) {
                                                      LOG.warn(
                                                         "Backfill cache write failed for {}; releasing without treating it as complete", new Object[]{var3}
                                                      );
                                                   } else {
                                                      advanceCursor(var0, var2.cursor.get(CacheManager.dimensionKey(var0)));
                                                   }

                                                   var0.getChunkSource().removeRegionTicket(BACKFILL_TICKET, var3, 0, Unit.INSTANCE);
                                                }
                                             }));
                                          }
                                       }
                                    }));
                              } catch (Throwable var12) {
                                 LOG.warn("Backfill failed for {}", new Object[]{var3, var12});
                                 fail(var0, var3, var2, var6, var4);
                              }
                           }
                        }
                     )
               );
         }
      }
   }

   private static void fail(ServerLevel var0, ChunkPos var1, BackfillController.State var2, Boolean var3, long var4) {
      if (active(var2, var4, var0, var1)) {
         MillenaireBridge.restore(var3);
         var2.previousSuppression = null;
         var0.getChunkSource().removeRegionTicket(BACKFILL_TICKET, var1, 0, Unit.INSTANCE);
         var2.token++;
         LOG.warn("Chunk-cache backfill for {} is paused until the guarded chunk unloads or a player takes ownership", new Object[]{var1});
      }
   }

   private static boolean active(BackfillController.State var0, long var1, ServerLevel var3, ChunkPos var4) {
      return var0.busy && var0.token == var1 && var0.activeLevel == var3 && var0.activePos != null && var0.activePos.toLong() == var4.toLong();
   }

   private static boolean playerNear(ServerLevel var0, ChunkPos var1) {
      int var2 = var0.getServer().getPlayerList().getViewDistance() + 1;

      for (ServerPlayer var4 : var0.players()) {
         ChunkPos var5 = var4.chunkPosition();
         if (Math.abs(var5.x - var1.x) <= var2 && Math.abs(var5.z - var1.z) <= var2) {
            return true;
         }
      }

      return false;
   }

   private static void handoffToPlayer(BackfillController.State var0) {
      ServerLevel var1 = var0.activeLevel;
      ChunkPos var2 = var0.activePos;
      if (var1 != null && var2 != null) {
         MillenaireBridge.restore(var0.previousSuppression);
         var0.previousSuppression = null;
         if (!MillenaireBridge.rememberPending(var1, var2)) {
            LOG.warn("Could not pre-mark Millenaire pending state while handing backfill {} to a player", new Object[]{var2});
         }

         var1.getChunkSource().removeRegionTicket(BACKFILL_TICKET, var2, 0, Unit.INSTANCE);
         BackfillGuard.clear(var1, var2);
         LOG.info("Handed chunk-cache backfill {} to normal player loading", new Object[]{var2});
         clearActive(var0);
      }
   }

   private static void clearActive(BackfillController.State var0) {
      var0.busy = false;
      var0.activeLevel = null;
      var0.activePos = null;
      var0.previousSuppression = null;
      var0.token++;
   }

   private static BackfillController.Target nextCandidate(ServerLevel var0, BackfillController.Cursor var1, String var2) {
      Path var3 = CacheManager.lodRoot(var0);
      if (!Files.isDirectory(var3)) {
         return null;
      }

      ArrayList<Path> var4 = new ArrayList<>();

      try (Stream<Path> var5 = Files.list(var3)) {
         var5.filter(var0x -> var0x.getFileName().toString().startsWith("r.") && var0x.getFileName().toString().endsWith(".cslod"))
            .sorted(Comparator.comparing(var0x -> var0x.getFileName().toString()))
            .forEach(var4::add);
      } catch (IOException var18) {
         return null;
      }

      loadCursor(var0, var1, var2);
      if (var1.region >= var4.size()) {
         long var19 = System.currentTimeMillis();
         if (var1.exhaustedAt == 0L) {
            var1.exhaustedAt = var19;
         }

         if (var19 - var1.exhaustedAt < 600000L) {
            return null;
         }

         var1.region = 0;
         var1.slot = 0;
         var1.exhaustedAt = 0L;
         saveCursor(var0, var1, var2);
      }

      for (int var20 = var1.region; var20 < var4.size(); var20++) {
         int var6 = var20 == var1.region ? var1.slot : 0;
         int[] var7 = parseRegion(var4.get(var20).getFileName().toString());
         if (var7 != null) {
            byte[] var8 = readHeader(var4.get(var20));
            if (var8 != null) {
               ByteBuffer var9 = ByteBuffer.wrap(var8).order(ByteOrder.BIG_ENDIAN);

               for (int var10 = var6; var10 < 1024; var10++) {
                  int var11 = var9.getInt(var10 * 8);
                  int var12 = var9.getInt(var10 * 8 + 4);
                  var1.region = var20;
                  var1.slot = var10;
                  if (var11 > 0 && var12 > 0) {
                     int var13 = var7[0] * 32 + var10 % 32;
                     int var14 = var7[1] * 32 + var10 / 32;
                     ChunkPos var15 = new ChunkPos(var13, var14);
                     if (!normalRegionContains(var0, var15) && !CacheManager.cacheExists(var0, var15, var2)) {
                        var1.exhaustedAt = 0L;
                        return new BackfillController.Target(var15, var4.get(var20), var10, var11, var12);
                     }
                  }
               }

               var1.slot = 0;
               var1.region = var20 + 1;
               saveCursor(var0, var1, var2);
            }
         }
      }

      if (var1.exhaustedAt == 0L) {
         var1.exhaustedAt = System.currentTimeMillis();
      }

      return null;
   }

   private static BackfillController.Candidate findCandidate(List<BackfillController.ScanLevel> var0, BackfillController.State var1) {
      for (BackfillController.ScanLevel var3 : var0) {
         ServerLevel var4 = var3.level();
         BackfillController.Cursor var5;
         synchronized (var1.cursor) {
            var5 = var1.cursor.computeIfAbsent(CacheManager.dimensionKey(var4), var0x -> new BackfillController.Cursor());
         }

         BackfillController.Target var9 = nextCandidate(var4, var5, var3.fingerprint());
         if (var9 != null) {
            return new BackfillController.Candidate(var4, var9);
         }
      }

      return null;
   }

   private static boolean waitForLodRefresh(BackfillController.Target var0) {
      long var1 = System.nanoTime() + 10000000000L;

      while (System.nanoTime() < var1) {
         int[] var3 = readSlot(var0.region(), var0.slot());
         if (var3 != null && var3[0] > 0 && var3[1] > 0 && (var3[0] != var0.offset() || var3[1] != var0.length())) {
            return true;
         }

         try {
            Thread.sleep(25L);
         } catch (InterruptedException var5) {
            Thread.currentThread().interrupt();
            return false;
         }
      }

      return false;
   }

   private static int[] readSlot(Path var0, int var1) {
      try (RandomAccessFile var2 = new RandomAccessFile(var0.toFile(), "r")) {
         long var3 = var1 * 8L;
         if (var2.length() < var3 + 8L) {
            return null;
         }

         var2.seek(var3);
         return new int[]{var2.readInt(), var2.readInt()};
      } catch (IOException var8) {
         return null;
      }
   }

   private static boolean normalRegionContains(ServerLevel var0, ChunkPos var1) {
      Path var2 = CacheManager.worldRegionRoot(var0).resolve("r." + Math.floorDiv(var1.x, 32) + "." + Math.floorDiv(var1.z, 32) + ".mca");
      if (!Files.isRegularFile(var2)) {
         return false;
      }

      int var3 = Math.floorMod(var1.x, 32) + Math.floorMod(var1.z, 32) * 32;

      try (RandomAccessFile var4 = new RandomAccessFile(var2.toFile(), "r")) {
         if (var4.length() < var3 * 4L + 4L) {
            return false;
         }

         var4.seek(var3 * 4L);
         return var4.readInt() != 0;
      } catch (IOException var9) {
         return true;
      }
   }

   private static byte[] readHeader(Path var0) {
      try (RandomAccessFile var1 = new RandomAccessFile(var0.toFile(), "r")) {
         int var2 = (int)Math.min(8192L, var1.length());
         if (var2 < 8) {
            return null;
         }

         byte[] var3 = new byte[8192];
         var1.readFully(var3, 0, var2);
         return var3;
      } catch (IOException var7) {
         return null;
      }
   }

   private static int[] parseRegion(String var0) {
      try {
         String[] var1 = var0.substring(2, var0.length() - 6).split("\\.");
         return var1.length == 2 ? new int[]{Integer.parseInt(var1[0]), Integer.parseInt(var1[1])} : null;
      } catch (RuntimeException var2) {
         return null;
      }
   }

   private static void advanceCursor(ServerLevel var0, BackfillController.Cursor var1) {
      if (var1 != null) {
         var1.slot++;
         if (var1.slot >= 1024) {
            var1.slot = 0;
            var1.region++;
         }

         String var2 = Fingerprint.forLevel(var0);
         if (var2 != null) {
            SCAN.execute(() -> saveCursor(var0, var1, var2));
         }
      }
   }

   private static Path cursorPath(ServerLevel var0) {
      return var0.getServer()
         .getWorldPath(LevelResource.ROOT)
         .resolve("chunksmith_chunk_cache")
         .resolve("backfill-" + CacheManager.dimensionKey(var0) + ".cursor");
   }

   private static void loadCursor(ServerLevel var0, BackfillController.Cursor var1, String var2) {
      if (!var1.loaded) {
         var1.loaded = true;
         Path var3 = cursorPath(var0);

         try {
            List var4 = Files.readAllLines(var3);
            if (var4.size() >= 3 && var2.equals(var4.get(0))) {
               var1.region = Integer.parseInt((String)var4.get(1));
               var1.slot = Integer.parseInt((String)var4.get(2));
            }
         } catch (Exception var5) {
         }
      }
   }

   private static void saveCursor(ServerLevel var0, BackfillController.Cursor var1, String var2) {
      Path var3 = cursorPath(var0);

      try {
         Files.createDirectories(var3.getParent());
         Path var4 = var3.resolveSibling(var3.getFileName().toString() + ".tmp");
         Files.writeString(var4, var2 + "\n" + var1.region + "\n" + var1.slot + "\n", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

         try {
            Files.move(var4, var3, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException var6) {
            Files.move(var4, var3, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (IOException var7) {
      }
   }

   private static List<ServerLevel> copyLevels(MinecraftServer var0) {
      ArrayList var1 = new ArrayList();

      for (ServerLevel var3 : var0.getAllLevels()) {
         var1.add(var3);
      }

      return List.copyOf(var1);
   }

   private record Candidate(ServerLevel level, BackfillController.Target target) {
   }

   private static final class Cursor {
      boolean loaded;
      int region;
      int slot;
      long exhaustedAt;
   }

   private record ScanLevel(ServerLevel level, String fingerprint) {
   }

   private static final class State {
      long ticks;
      boolean busy;
      boolean scanning;
      long token;
      ServerLevel activeLevel;
      ChunkPos activePos;
      Boolean previousSuppression;
      final Map<String, BackfillController.Cursor> cursor = new HashMap<>();
   }

   private record Target(ChunkPos pos, Path region, int slot, int offset, int length) {
   }
}
