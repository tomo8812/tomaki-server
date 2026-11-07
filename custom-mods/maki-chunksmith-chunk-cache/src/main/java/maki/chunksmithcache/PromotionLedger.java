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

    static void adoptLive(ServerLevel level, ChunkPos pos, Path file, boolean capturePending) throws IOException {
        Key key=new Key(level,pos.toLong());
        if(ACTIVE.containsKey(key)) return;
        PromotionClaim claim=PromotionClaim.adoptLive(file,capturePending);
        if(claim!=null) { ACTIVE.put(key,claim); classify(key,claim); }
    }

    public static boolean consume(ServerLevel level, ChunkPos pos) {
        PromotionClaim claim = ACTIVE.get(new Key(level, pos.toLong()));
        return claim != null && claim.loaded();
    }

    static void blockReplay(ServerLevel level, ChunkPos pos, Path payload) { BLOCKED.put(new Key(level,pos.toLong()),payload); }
    static void normalFullPresent(ServerLevel level, ChunkPos pos) { BLOCKED.remove(new Key(level,pos.toLong())); }

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

    private static void retire(Key key, PromotionClaim claim) {
        if (!claim.canRetire()) return;
        CacheManager.retireClaim(claim.path(), () -> ACTIVE.remove(key, claim));
    }

    private static void classify(Key key,PromotionClaim claim) {
        if(!claim.beginClassification()) return;
        if(!CacheManager.classifyLiveClaim(claim,() -> retire(key,claim))) claim.classificationRejected();
    }

    static void classifyPending(MinecraftServer server) {
        ACTIVE.forEach((key,claim) -> { if(key.level().getServer()==server) classify(key,claim); });
    }

    static void clear(MinecraftServer server) {
        ACTIVE.keySet().removeIf(key -> key.level().getServer() == server);
        BLOCKED.keySet().removeIf(key -> key.level().getServer() == server);
        // Claimed payloads remain on disk, unavailable for automatic replay after restart.
    }
}
