package maki.chunksmithcache;

import java.nio.file.Path;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/** In-memory ACK correlation; the .in-use rename is the restart-safe ownership boundary. */
public final class PromotionLedger {
    private record Key(ServerLevel level, long pos) {}
    private static final ConcurrentHashMap<Key, PromotionClaim> ACTIVE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Key,Path> BLOCKED = new ConcurrentHashMap<>();
    private PromotionLedger() {}

    static boolean claim(ServerLevel level, ChunkPos pos, Path file, boolean hasEntities) throws IOException {
        Key key = new Key(level, pos.toLong());
        if (ACTIVE.containsKey(key)) return false;
        PromotionClaim claim = PromotionClaim.take(file, hasEntities);
        if (claim == null) return false;
        ACTIVE.put(key, claim);
        return true;
    }

    static boolean adoptLive(ServerLevel level, ChunkPos pos, Path file, CompletableFuture<Boolean> pendingCapture) throws IOException {
        Key key=new Key(level,pos.toLong());
        PromotionClaim claim=ACTIVE.get(key);
        if(claim!=null) {
            if(PregenBorrowLedger.needsPoiWrite(level,pos)) claim.requirePoiWrite();
            holdUntilCaptureEnds(key,claim,pendingCapture);
            return true;
        }
        claim=PromotionClaim.adoptLive(file,pendingCapture!=null);
        if(claim!=null) {
            if(PregenBorrowLedger.needsPoiWrite(level,pos)) claim.requirePoiWrite();
            ACTIVE.put(key,claim);
            classify(key,claim);
            holdUntilCaptureEnds(key,claim,pendingCapture);
            return true;
        }
        return false;
    }

    private static void holdUntilCaptureEnds(Key key, PromotionClaim claim, CompletableFuture<Boolean> pendingCapture) {
        if(pendingCapture==null) return;
        claim.publicationPending();
        pendingCapture.whenComplete((ignored,error) -> {
            claim.publicationResolved();
            retire(key,claim);
        });
    }

    public static boolean consume(ServerLevel level, ChunkPos pos) {
        PromotionClaim claim = ACTIVE.get(new Key(level, pos.toLong()));
        return claim != null && claim.loaded();
    }

    static boolean replayBlocked(ServerLevel level, ChunkPos pos) { return BLOCKED.containsKey(new Key(level,pos.toLong())); }

    static void blockReplay(ServerLevel level, ChunkPos pos, Path payload) { BLOCKED.put(new Key(level,pos.toLong()),payload); }
    static void handoffRecovered(ServerLevel level,ChunkPos pos,Path payload) { BLOCKED.remove(new Key(level,pos.toLong()),payload); }
    static void normalFullPresent(ServerLevel level, ChunkPos pos) { BLOCKED.remove(new Key(level,pos.toLong())); }
    static boolean owns(ServerLevel level, ChunkPos pos) {
        Key key=new Key(level,pos.toLong());
        return ACTIVE.containsKey(key)||BLOCKED.containsKey(key);
    }

    public static void requireUnambiguousLoad(ServerLevel level, ChunkPos pos) {
        Path payload=BLOCKED.remove(new Key(level,pos.toLong()));
        if (payload!=null) {
            throw new IllegalStateException("Interrupted chunk-cache promotion at " + level.dimension().location() + " " + pos
                + "; normal storage is incomplete; payload retained at " + payload
                + "; inspect/recover this payload before reloading this coordinate; refusing automatic replay or replacement generation");
        }
    }

    public static void discard(ServerLevel level, ChunkPos pos) {
        PromotionClaim claim = ACTIVE.get(new Key(level, pos.toLong()));
        if (claim != null) claim.failed(); // Never put a possibly modified snapshot back on offer.
    }

    static boolean observeChunkWrite(ServerLevel level, ChunkPos pos, CompletableFuture<Void> written) {
        Key key = new Key(level, pos.toLong());
        PromotionClaim claim = ACTIVE.get(key);
        if (claim == null) return false;
        written.whenComplete((ignored, error) -> {
            claim.chunkWrite(error == null);
            retire(key, claim);
        });
        return true;
    }

    public static void entitiesRestored(ServerLevel level, ChunkPos pos) {
        PromotionClaim claim = ACTIVE.get(new Key(level, pos.toLong()));
        if (claim != null) claim.entitiesRestored();
    }

    public static void observeEntityWrite(ServerLevel level, ChunkPos pos, CompletableFuture<Void> written) {
        Key key = new Key(level, pos.toLong());
        PromotionClaim claim = ACTIVE.get(key);
        // Correlate at invocation, not completion: an older outstanding write is not an ACK
        // for entities restored from this claim.
        if (claim == null || !claim.acceptsEntityWrite()) return;
        written.whenComplete((ignored, error) -> {
            claim.entityWrite(error == null);
            retire(key, claim);
        });
    }

    /** Observe the actual normal POI region write, not just dirty restoration. */
    public static void observePoiWrite(Object poiManager, ChunkPos pos, CompletableFuture<Void> written) {
        if(poiManager==null||pos==null||written==null)return;
        // The borrow may have unloaded before the POI Future completes; correlate
        // against the live promotion claim instead of the transient borrow ledger.
        ACTIVE.forEach((key,claim) -> {
            if(key.pos()!=pos.toLong() || key.level().getPoiManager()!=poiManager
               || !claim.requiresPoiWrite())return;
            written.whenComplete((ignored,error) -> {
                claim.poiWrite(error==null);
                retire(key,claim);
            });
        });
    }

    private static void retire(Key key, PromotionClaim claim) {
        if (!claim.beginRetirement()) return;
        boolean queued=CacheManager.retireClaim(
            claim.path(),
            () -> ACTIVE.remove(key, claim),
            claim::retirementRejected
        );
        if(!queued) claim.retirementRejected();
    }

    private static void classify(Key key,PromotionClaim claim) {
        if(!claim.beginClassification()) return;
        if(!CacheManager.classifyLiveClaim(claim,() -> retire(key,claim))) claim.classificationRejected();
    }

    static void classifyPending(MinecraftServer server) {
        ACTIVE.forEach((key,claim) -> {
            if(key.level().getServer()!=server) return;
            // ACK completion can race a full WRITE_IO queue. Retry any fully
            // acknowledged claim here; beginRetirement prevents duplicate queued deletes.
            retire(key,claim);
            classify(key,claim);
        });
    }

    static void clear(MinecraftServer server) {
        ACTIVE.keySet().removeIf(key -> key.level().getServer() == server);
        BLOCKED.keySet().removeIf(key -> key.level().getServer() == server);
        // Claimed payloads remain on disk, unavailable for automatic replay after restart.
    }
}
