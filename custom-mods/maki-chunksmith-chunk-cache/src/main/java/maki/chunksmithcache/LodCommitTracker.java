package maki.chunksmithcache;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.LodSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * A logical timeout cannot cancel native CsLodRegionStore.write. Retain the
 * exact record identity until the physical terminal callback, including
 * synchronous store fallback and late acknowledgements.
 */
public final class LodCommitTracker {
    private static final int MAX_PHYSICAL = 4096;
    private static final IdentityHashMap<CsLodChunk, Physical> PHYSICAL = new IdentityHashMap<>();
    private static final Map<Long, Set<Physical>> BY_COORDINATE = new HashMap<>();
    private static final ThreadLocal<Request> CURRENT = new ThreadLocal<>();
    private static long unknownPhysical;

    private static final class Request {
        final int x, z;
        final CompletableFuture<Boolean> result;
        int registrations;
        Request(int x, int z, CompletableFuture<Boolean> result) {
            this.x=x; this.z=z; this.result=result;
        }
    }
    private static final class Physical {
        final CompletableFuture<Boolean> result;
        final long coordinate, startedNanos;
        boolean collided;
        Physical(CompletableFuture<Boolean> result, long coordinate) {
            this.result=result; this.coordinate=coordinate; this.startedNanos=System.nanoTime();
        }
    }
    public record PhysicalStatus(int inFlight, long oldestMillis, long unknown) {
        public boolean reliable() { return unknown == 0; }
    }
    private LodCommitTracker() {}

    public static CompletableFuture<Boolean> offer(ServerLevel level, LevelChunk chunk) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Request previous = CURRENT.get();
        Request request = new Request(chunk.getPos().x, chunk.getPos().z, result);
        CURRENT.set(request);
        try { LodSupport.offer(level, chunk); }
        catch (Throwable error) { result.completeExceptionally(error); }
        finally {
            if (previous == null) CURRENT.remove(); else CURRENT.set(previous);
        }
        if (request.registrations != 1 && !result.isDone()) result.complete(false);
        // Timeout closes the *logical* promise only, never the physical handle.
        result.orTimeout(30, TimeUnit.SECONDS).whenComplete((ok,error) -> {
            if (error != null || !Boolean.TRUE.equals(ok)) {
                try {
                    var index=LodSupport.presenceIndexFor(level.getServer(), level.dimension().location().toString());
                    if(index!=null)index.invalidate();
                } catch(Throwable ignored) {
                    // Failed writes remain retry debt even when invalidation fails.
                }
            }
        });
        return result;
    }

    public static void offered(Object value) {
        Request request = CURRENT.get();
        if (request != null && value instanceof CsLodChunk record
            && record.getChunkX() == request.x && record.getChunkZ() == request.z) {
            request.registrations++;
            track(record, request.result);
        }
    }

    /** Also used by the exact-record lifecycle regression test. */
    static void track(CsLodChunk record, CompletableFuture<Boolean> future) {
        Set<CompletableFuture<Boolean>> unsafe = new HashSet<>();
        boolean overflow=false, overlap=false;
        synchronized (PHYSICAL) {
            Physical duplicate=PHYSICAL.get(record);
            if (duplicate != null) {
                duplicate.collided=true;
                unsafe.add(future);
                unsafe.add(duplicate.result);
            } else if (PHYSICAL.size() >= MAX_PHYSICAL) {
                // This untracked native write may still complete much later.
                // Keep a sticky unknown indication instead of reporting zero WIP.
                unknownPhysical++;
                overflow=true;
                unsafe.add(future);
            } else {
                long coordinate=((long)record.getChunkX()<<32) | (record.getChunkZ() & 0xffffffffL);
                Physical current=new Physical(future,coordinate);
                Set<Physical> existing=BY_COORDINATE.computeIfAbsent(coordinate,ignored->new HashSet<>());
                if (!existing.isEmpty()) {
                    current.collided=true; overlap=true;
                    for (Physical old:existing) {
                        old.collided=true;
                        unsafe.add(old.result);
                    }
                    unsafe.add(future);
                }
                existing.add(current);
                PHYSICAL.put(record,current);
            }
        }
        // CompletableFuture callbacks can do I/O. Run them outside the map lock.
        for (CompletableFuture<Boolean> result:unsafe) result.complete(false);
        if(overlap) PipelineMetrics.count("lod-physical-coordinate-overlap");
        if(overflow) PipelineMetrics.count("lod-physical-handle-overflow");
    }

    /** Called after actual native write returns, including late completions. */
    public static void written(CsLodChunk record, Throwable error) {
        Physical work;
        synchronized (PHYSICAL) {
            work=PHYSICAL.remove(record);
            if(work!=null) {
                Set<Physical> active=BY_COORDINATE.get(work.coordinate);
                if(active!=null) {
                    active.remove(work);
                    if(active.isEmpty()) BY_COORDINATE.remove(work.coordinate);
                }
            }
        }
        if(work==null) return;
        boolean late=work.result.isDone();
        if(work.collided) work.result.complete(false);
        else if(error==null) work.result.complete(true);
        else work.result.completeExceptionally(error);
        PipelineMetrics.count(error==null ? "lod-physical-write-ack" : "lod-physical-write-failed");
        if(late) PipelineMetrics.count("lod-physical-late-terminal");
    }

    /** Low-frequency snapshot; never scan the physical map on the writer path. */
    public static PhysicalStatus physicalStatus() {
        synchronized (PHYSICAL) {
            long now=System.nanoTime(),oldest=0;
            for(Physical work:PHYSICAL.values()) oldest=Math.max(oldest,Math.max(0,now-work.startedNanos));
            return new PhysicalStatus(PHYSICAL.size(),TimeUnit.NANOSECONDS.toMillis(oldest),unknownPhysical);
        }
    }
}
