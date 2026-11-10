package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class PromotionLedger {
   private static final ConcurrentHashMap<PromotionLedger.Key, PromotionClaim> ACTIVE = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<PromotionLedger.Key, Path> BLOCKED = new ConcurrentHashMap<>();

   private PromotionLedger() {
   }

   static boolean claim(ServerLevel level, ChunkPos pos, Path file, boolean hasEntities) throws IOException {
      PromotionLedger.Key key = new PromotionLedger.Key(level, pos.toLong());
      if (ACTIVE.containsKey(key)) {
         return false;
      }

      PromotionClaim claim = PromotionClaim.take(file, hasEntities);
      if (claim == null) {
         return false;
      }

      ACTIVE.put(key, claim);
      return true;
   }

   static boolean adoptLive(ServerLevel level, ChunkPos pos, Path file, CompletableFuture<Boolean> pendingCapture) throws IOException {
      PromotionLedger.Key key = new PromotionLedger.Key(level, pos.toLong());
      PromotionClaim claim = ACTIVE.get(key);
      if (claim != null) {
         if (PregenBorrowLedger.needsPoiWrite(level, pos)) {
            claim.requirePoiWrite();
         }

         holdUntilCaptureEnds(key, claim, pendingCapture);
         return true;
      } else {
         claim = PromotionClaim.adoptLive(file, pendingCapture != null);
         if (claim != null) {
            if (PregenBorrowLedger.needsPoiWrite(level, pos)) {
               claim.requirePoiWrite();
            }

            ACTIVE.put(key, claim);
            classify(key, claim);
            holdUntilCaptureEnds(key, claim, pendingCapture);
            return true;
         } else {
            return false;
         }
      }
   }

   private static void holdUntilCaptureEnds(PromotionLedger.Key key, PromotionClaim claim, CompletableFuture<Boolean> pendingCapture) {
      if (pendingCapture != null) {
         claim.publicationPending();
         pendingCapture.whenComplete((ignored, error) -> {
            claim.publicationResolved();
            retire(key, claim);
         });
      }
   }

   public static boolean consume(ServerLevel level, ChunkPos pos) {
      PromotionClaim claim = ACTIVE.get(new PromotionLedger.Key(level, pos.toLong()));
      return claim != null && claim.loaded();
   }

   static void blockReplay(ServerLevel level, ChunkPos pos, Path payload) {
      BLOCKED.put(new PromotionLedger.Key(level, pos.toLong()), payload);
   }

   static void normalFullPresent(ServerLevel level, ChunkPos pos) {
      BLOCKED.remove(new PromotionLedger.Key(level, pos.toLong()));
   }

   static boolean owns(ServerLevel level, ChunkPos pos) {
      PromotionLedger.Key key = new PromotionLedger.Key(level, pos.toLong());
      return ACTIVE.containsKey(key) || BLOCKED.containsKey(key);
   }

   public static void requireUnambiguousLoad(ServerLevel level, ChunkPos pos) {
      Path payload = BLOCKED.remove(new PromotionLedger.Key(level, pos.toLong()));
      if (payload != null) {
         throw new IllegalStateException(
            "Interrupted chunk-cache promotion at "
               + level.dimension().location()
               + " "
               + pos
               + "; normal storage is incomplete; payload retained at "
               + payload
               + "; inspect/recover this payload before reloading this coordinate; refusing automatic replay or replacement generation"
         );
      }
   }

   public static void discard(ServerLevel level, ChunkPos pos) {
      PromotionClaim claim = ACTIVE.get(new PromotionLedger.Key(level, pos.toLong()));
      if (claim != null) {
         claim.failed();
      }
   }

   static boolean observeChunkWrite(ServerLevel level, ChunkPos pos, CompletableFuture<Void> written) {
      PromotionLedger.Key key = new PromotionLedger.Key(level, pos.toLong());
      PromotionClaim claim = ACTIVE.get(key);
      if (claim == null) {
         return false;
      }

      written.whenComplete((ignored, error) -> {
         claim.chunkWrite(error == null);
         retire(key, claim);
      });
      return true;
   }

   public static void entitiesRestored(ServerLevel level, ChunkPos pos) {
      PromotionClaim claim = ACTIVE.get(new PromotionLedger.Key(level, pos.toLong()));
      if (claim != null) {
         claim.entitiesRestored();
      }
   }

   public static void observeEntityWrite(ServerLevel level, ChunkPos pos, CompletableFuture<Void> written) {
      PromotionLedger.Key key = new PromotionLedger.Key(level, pos.toLong());
      PromotionClaim claim = ACTIVE.get(key);
      if (claim != null && claim.acceptsEntityWrite()) {
         written.whenComplete((ignored, error) -> {
            claim.entityWrite(error == null);
            retire(key, claim);
         });
      }
   }

   public static void observePoiWrite(Object poiManager, ChunkPos pos, CompletableFuture<Void> written) {
      if (poiManager != null && pos != null && written != null) {
         ACTIVE.forEach((key, claim) -> {
            if (key.pos() == pos.toLong() && key.level().getPoiManager() == poiManager && claim.requiresPoiWrite()) {
               written.whenComplete((ignored, error) -> {
                  claim.poiWrite(error == null);
                  retire(key, claim);
               });
            }
         });
      }
   }

   private static void retire(PromotionLedger.Key key, PromotionClaim claim) {
      if (claim.beginRetirement()) {
         boolean queued = CacheManager.retireClaim(claim.path(), () -> ACTIVE.remove(key, claim), claim::retirementRejected);
         if (!queued) {
            claim.retirementRejected();
         }
      }
   }

   private static void classify(PromotionLedger.Key key, PromotionClaim claim) {
      if (claim.beginClassification()) {
         if (!CacheManager.classifyLiveClaim(claim, () -> retire(key, claim))) {
            claim.classificationRejected();
         }
      }
   }

   static void classifyPending(MinecraftServer server) {
      ACTIVE.forEach((key, claim) -> {
         if (key.level().getServer() == server) {
            retire(key, claim);
            classify(key, claim);
         }
      });
   }

   static void clear(MinecraftServer server) {
      ACTIVE.keySet().removeIf(key -> key.level().getServer() == server);
      BLOCKED.keySet().removeIf(key -> key.level().getServer() == server);
   }

   private record Key(ServerLevel level, long pos) {
   }
}
