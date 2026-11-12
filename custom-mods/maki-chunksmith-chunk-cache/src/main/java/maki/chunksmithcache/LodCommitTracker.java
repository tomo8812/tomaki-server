package maki.chunksmithcache;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.LodSupport;
import java.util.IdentityHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

/** Correlates the actual native record object with its writer acknowledgement. */
public final class LodCommitTracker {
    private static final IdentityHashMap<CsLodChunk, CompletableFuture<Boolean>> PENDING = new IdentityHashMap<>();
    private static final ThreadLocal<Request> CURRENT = new ThreadLocal<>();
    private record Request(int x, int z, CompletableFuture<Boolean> future) {}
    private LodCommitTracker() {}

    public static CompletableFuture<Boolean> offer(ServerLevel level, LevelChunk chunk) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Request previous = CURRENT.get();
        CURRENT.set(new Request(chunk.getPos().x, chunk.getPos().z, result));
        try { LodSupport.offer(level, chunk); }
        catch (Throwable error) { result.completeExceptionally(error); }
        finally {
            if (previous == null) CURRENT.remove(); else CURRENT.set(previous);
        }
        boolean registered;
        synchronized (PENDING) { registered = PENDING.containsValue(result); }
        if (!registered && !result.isDone()) result.complete(false); // disabled/no native sink
        result.orTimeout(30, TimeUnit.SECONDS).whenComplete((ok,error) -> {
            synchronized (PENDING) { PENDING.values().removeIf(value -> value == result); }
            if (error != null || !Boolean.TRUE.equals(ok)) {
                try {
                    var index=LodSupport.presenceIndexFor(level.getServer(), level.dimension().location().toString());
                    if(index!=null)index.invalidate();
                } catch(Throwable ignored) {
                    // A failed repair remains retry debt even if the native presence cache
                    // cannot be invalidated immediately; restart will reread disk state.
                }
            }
        });
        return result;
    }

    public static void offered(Object value) {
        Request request = CURRENT.get();
        if (request != null && value instanceof CsLodChunk record
            && record.getChunkX() == request.x && record.getChunkZ() == request.z) {
            track(record,request.future);
        }
    }

    static void track(CsLodChunk record,CompletableFuture<Boolean> future) {
        synchronized (PENDING) { PENDING.put(record,future); }
    }

    public static void written(CsLodChunk record, Throwable error) {
        CompletableFuture<Boolean> result;
        synchronized (PENDING) { result = PENDING.remove(record); }
        if (result != null) {
            if (error == null) result.complete(true); else result.completeExceptionally(error);
        }
    }
}
