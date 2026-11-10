package maki.chunksmithcache;

import com.kishku7.chunksmith.GenerationTask;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

final class PregenBorrowLedger {
   private static final ConcurrentHashMap<PregenBorrowLedger.Key, PregenBorrowLedger.Borrow> ACTIVE = new ConcurrentHashMap<>();

   private PregenBorrowLedger() {
   }

   static boolean beginGenerated(ServerLevel level, ChunkPos pos, Path file, GenerationTask sourceTask) {
      return sourceTask != null && begin(level, pos, file, PregenBorrowLedger.Kind.GENERATED, sourceTask);
   }

   static boolean beginBorrowed(ServerLevel level, ChunkPos pos, Path file) {
      return begin(level, pos, file, PregenBorrowLedger.Kind.BORROWED, null);
   }

   private static boolean begin(ServerLevel level, ChunkPos pos, Path file, PregenBorrowLedger.Kind kind, GenerationTask owner) {
      PregenBorrowLedger.Key key = new PregenBorrowLedger.Key(level, pos.toLong());
      PregenBorrowLedger.Borrow existing = ACTIVE.get(key);
      if (existing == null) {
         CompletableFuture<Boolean> lodDone = new CompletableFuture<>();
         lodDone.completeOnTimeout(false, 30L, TimeUnit.SECONDS);
         PregenBorrowLedger.Borrow borrow = new PregenBorrowLedger.Borrow(
            file, kind, owner, lodDone, new AtomicBoolean(), new AtomicBoolean(), new AtomicReference<>(), new HashSet<>(), new AtomicBoolean()
         );
         return ACTIVE.putIfAbsent(key, borrow) == null;
      } else {
         return existing.kind() == kind && existing.file().equals(file) && existing.owner() == owner && !existing.handedOff().get();
      }
   }

   static boolean generatedFullLoaded(ServerLevel level, ChunkPos pos, GenerationTask sourceTask) {
      if (sourceTask == null) {
         return false;
      } else {
         PregenBorrowLedger.Borrow borrow = ACTIVE.get(new PregenBorrowLedger.Key(level, pos.toLong()));
         if (borrow != null && borrow.kind() == PregenBorrowLedger.Kind.GENERATED && borrow.owner() == sourceTask && !borrow.handedOff().get()) {
            borrow.fullLoaded().set(true);
            return true;
         } else {
            return false;
         }
      }
   }

   static PregenBorrowLedger.Kind kind(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(new PregenBorrowLedger.Key(level, pos.toLong()));
      return borrow == null ? null : borrow.kind();
   }

   static CompletableFuture<Boolean> pending(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(new PregenBorrowLedger.Key(level, pos.toLong()));
      return borrow != null && !borrow.lodDone().isDone() ? borrow.lodDone() : null;
   }

   static boolean attachLod(ServerLevel level, ChunkPos pos, CompletableFuture<Boolean> lodCommitted) {
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(new PregenBorrowLedger.Key(level, pos.toLong()));
      if (borrow == null) {
         return false;
      }

      lodCommitted.whenComplete((saved, error) -> borrow.lodDone().complete(error == null && Boolean.TRUE.equals(saved)));
      return true;
   }

   static Path handoffCandidate(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(new PregenBorrowLedger.Key(level, pos.toLong()));
      return borrow != null && !borrow.handedOff().get() && playerNear(level, pos) ? borrow.file() : null;
   }

   static void handedOff(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(new PregenBorrowLedger.Key(level, pos.toLong()));
      if (borrow != null) {
         releasePersistence(level, borrow);
      }
   }

   static boolean releaseGeneratedToNormal(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.Key key = new PregenBorrowLedger.Key(level, pos.toLong());
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(key);
      return borrow == null ? false : releaseOnServerThread(level, key, borrow, false);
   }

   static boolean cancel(ServerLevel level, ChunkPos pos, GenerationTask sourceTask) {
      if (sourceTask == null) {
         return false;
      }

      PregenBorrowLedger.Key key = new PregenBorrowLedger.Key(level, pos.toLong());
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(key);
      return borrow != null && borrow.kind() == PregenBorrowLedger.Kind.GENERATED && borrow.owner() == sourceTask
         ? releaseOnServerThread(level, key, borrow, true)
         : false;
   }

   private static boolean releaseOnServerThread(ServerLevel level, PregenBorrowLedger.Key key, PregenBorrowLedger.Borrow borrow, boolean remove) {
      MinecraftServer server = level.getServer();
      if (server.isSameThread()) {
         return releaseCurrent(level, key, borrow, remove);
      }

      try {
         server.execute(() -> releaseCurrent(level, key, borrow, remove));
         return true;
      } catch (RuntimeException unavailable) {
         return false;
      }
   }

   private static boolean releaseCurrent(ServerLevel level, PregenBorrowLedger.Key key, PregenBorrowLedger.Borrow borrow, boolean remove) {
      PregenBorrowLedger.Borrow active = ACTIVE.get(key);
      if (active != borrow) {
         return false;
      }

      if (remove && borrow.fullLoaded().get()) {
         return false;
      }

      if (remove && !ACTIVE.remove(key, borrow)) {
         return false;
      }

      releasePersistence(level, borrow);
      if (remove && !borrow.lodDone().isDone()) {
         borrow.lodDone().complete(false);
      }

      return true;
   }

   private static void releasePersistence(ServerLevel level, PregenBorrowLedger.Borrow borrow) {
      synchronized (borrow) {
         if (borrow.handedOff().compareAndSet(false, true)) {
            ChunkAccess suppressed = borrow.suppressedChunk().getAndSet(null);
            if (suppressed != null) {
               suppressed.setUnsaved(true);
            }

            if (!borrow.suppressedPoiSections().isEmpty()) {
               long[] sections = borrow.suppressedPoiSections().stream().mapToLong(Long::longValue).toArray();
               borrow.suppressedPoiSections().clear();
               if (level.getPoiManager() instanceof PregenPoiDirtyBridge bridge) {
                  bridge.maki$restorePregenPoiDirty(sections);
               }
            }
         }
      }
   }

   static boolean suppressChunkSave(ServerLevel level, ChunkAccess chunk) {
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(new PregenBorrowLedger.Key(level, chunk.getPos().toLong()));
      if (borrow == null) {
         return false;
      }

      synchronized (borrow) {
         if (borrow.handedOff().get()) {
            return false;
         }

         borrow.suppressedChunk().set(chunk);
         chunk.setUnsaved(false);
         return true;
      }
   }

   static boolean suppressPoiSave(ServerLevel level, ChunkPos pos, Object poiManager) {
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(new PregenBorrowLedger.Key(level, pos.toLong()));
      if (borrow == null) {
         return false;
      }

      synchronized (borrow) {
         if (borrow.handedOff().get()) {
            return false;
         }

         if (poiManager instanceof PregenPoiDirtyBridge bridge) {
            for (long section : bridge.maki$clearPregenPoiDirty(pos)) {
               borrow.suppressedPoiSections().add(section);
               borrow.poiWriteRequired().set(true);
            }
         }

         return true;
      }
   }

   static boolean needsPoiWrite(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(new PregenBorrowLedger.Key(level, pos.toLong()));
      return borrow != null && borrow.poiWriteRequired().get();
   }

   static boolean isHandedOff(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(new PregenBorrowLedger.Key(level, pos.toLong()));
      return borrow != null && borrow.handedOff().get();
   }

   static boolean suppressPersistence(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.Borrow borrow = ACTIVE.get(new PregenBorrowLedger.Key(level, pos.toLong()));
      return borrow != null && !borrow.handedOff().get();
   }

   static ServerLevel levelForPoi(Object poiManager, ChunkPos pos) {
      if (poiManager != null && pos != null) {
         for (PregenBorrowLedger.Key key : ACTIVE.keySet()) {
            if (key.pos() == pos.toLong()) {
               ServerLevel level = key.level();
               if (level.getPoiManager() == poiManager) {
                  return level;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   static void unloaded(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.Borrow borrow = ACTIVE.remove(new PregenBorrowLedger.Key(level, pos.toLong()));
      if (borrow != null && !borrow.lodDone().isDone()) {
         borrow.lodDone().complete(false);
      }
   }

   static void clear(MinecraftServer server) {
      ACTIVE.forEach((key, borrow) -> {
         if (key.level().getServer() == server) {
            ACTIVE.remove(key, borrow);
            borrow.lodDone().complete(false);
         }
      });
   }

   private static boolean playerNear(ServerLevel level, ChunkPos pos) {
      int radius = level.getServer().getPlayerList().getViewDistance() + 1;

      for (ServerPlayer player : level.players()) {
         ChunkPos at = player.chunkPosition();
         if (Math.abs(at.x - pos.x) <= radius && Math.abs(at.z - pos.z) <= radius) {
            return true;
         }
      }

      return false;
   }

   private record Borrow(
      Path file,
      PregenBorrowLedger.Kind kind,
      GenerationTask owner,
      CompletableFuture<Boolean> lodDone,
      AtomicBoolean handedOff,
      AtomicBoolean fullLoaded,
      AtomicReference<ChunkAccess> suppressedChunk,
      Set<Long> suppressedPoiSections,
      AtomicBoolean poiWriteRequired
   ) {
   }

   private record Key(ServerLevel level, long pos) {
   }

   enum Kind {
      GENERATED,
      BORROWED;

      // $VF: synthetic method
      private static PregenBorrowLedger.Kind[] $values() {
         return new PregenBorrowLedger.Kind[]{GENERATED, BORROWED};
      }
   }
}
