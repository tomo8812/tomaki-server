package maki.chunksmithcache;

import com.kishku7.chunksmith.GenerationTask;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * Owns the normal-persistence veto for unified pregen targets. A guard is armed
 * either before a newly generated target is requested or before a confirmed
 * cache FULL is borrowed for a Chunksmith load, and remains until unload/player handoff.
 */
final class PregenBorrowLedger {
    enum Kind { GENERATED, BORROWED }
    private record Key(ServerLevel level, long pos) {}
    private record Borrow(Path file, Kind kind, GenerationTask owner, CompletableFuture<Boolean> captureDone, AtomicBoolean captureAttached, AtomicBoolean handedOff, AtomicBoolean fullLoaded,
                          java.util.concurrent.atomic.AtomicReference<ChunkAccess> suppressedChunk,
                          java.util.Set<Long> suppressedPoiSections, AtomicBoolean poiWriteRequired) {}
    private static final ConcurrentHashMap<Key, Borrow> ACTIVE = new ConcurrentHashMap<>();

    private PregenBorrowLedger() {}

    static boolean beginGenerated(ServerLevel level, ChunkPos pos, Path file, GenerationTask sourceTask) {
        return sourceTask != null && begin(level,pos,file,Kind.GENERATED,sourceTask);
    }

    static boolean beginBorrowed(ServerLevel level, ChunkPos pos, Path file,GenerationTask owner) {
        return begin(level,pos,file,Kind.BORROWED,owner);
    }

    private static boolean begin(ServerLevel level, ChunkPos pos, Path file, Kind kind, GenerationTask owner) {
        Key key=new Key(level,pos.toLong());
        Borrow existing=ACTIVE.get(key);
        // Never adopt a guard from an earlier task, even when kind and file match.
        // Its delayed failure/cancellation can still be queued on the server thread.
        if(existing!=null)return existing.kind()==kind && existing.file().equals(file)
            && existing.owner()==owner && !existing.handedOff().get();
        CompletableFuture<Boolean> captureDone=new CompletableFuture<>();
        if(kind==Kind.BORROWED)captureDone.complete(true);
        Borrow borrow=new Borrow(file,kind,owner,captureDone,new AtomicBoolean(),new AtomicBoolean(),new AtomicBoolean(),new java.util.concurrent.atomic.AtomicReference<>(),new java.util.HashSet<>(),new AtomicBoolean());
        return ACTIVE.putIfAbsent(key,borrow)==null;
    }

    /**
     * A successful FULL result can remain in vanilla's chunk manager after its
     * follow-owned request is rejected for stopping/detachment. Retain the
     * persistence veto until unload, player handoff, or durable normal FULL.
     */
    static boolean generatedFullLoaded(ServerLevel level, ChunkPos pos, GenerationTask sourceTask) {
        if(sourceTask==null)return false;
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        if(borrow==null || borrow.owner()!=sourceTask
            || borrow.handedOff().get())return false;
        borrow.fullLoaded().set(true);
        return true;
    }

    static Kind kind(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        return borrow==null?null:borrow.kind();
    }

    /** Blocks a competing normal cache load only while the owned FULL capture is unresolved. */
    static CompletableFuture<Boolean> pending(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        return borrow==null||borrow.captureDone().isDone()?null:borrow.captureDone();
    }

    static boolean attachCapture(ServerLevel level, ChunkPos pos, CompletableFuture<Boolean> captureCommitted) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        if(borrow==null)return false;
        synchronized(borrow) {
            borrow.captureAttached().set(true);
            captureCommitted.whenComplete((saved,error)->borrow.captureDone().complete(error==null&&Boolean.TRUE.equals(saved)));
        }
        return true;
    }

    static void requestFailed(ServerLevel level,ChunkPos pos,GenerationTask owner) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        if(borrow==null||borrow.owner()!=owner)return;
        synchronized(borrow) {
            if(!borrow.captureAttached().get())borrow.captureDone().complete(false);
        }
    }

    static Path handoffCandidate(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        return borrow==null||!borrow.fullLoaded().get()||level.getChunkSource().getChunkNow(pos.x,pos.z)==null||borrow.handedOff().get()||!playerNear(level,pos)?null:borrow.file();
    }

    static void handedOff(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        if(borrow==null)return;
        releasePersistence(level,borrow);
    }

    /**
     * A durable normal FULL may win after either a generated-target guard or a
     * borrowed-cache a Chunksmith load guard was armed. Stop vetoing normal persistence
     * immediately and restore any chunk/POI dirty state suppressed by that guard.
     */
    static boolean releaseGeneratedToNormal(ServerLevel level, ChunkPos pos) {
        Key key=new Key(level,pos.toLong());
        Borrow borrow=ACTIVE.get(key);
        if(borrow==null)return false;
        return releaseOnServerThread(level,key,borrow,false);
    }

    /**
     * A failed request may still have obtained a FULL chunk. Release the exact
     * task's guard only when no successful FULL has been observed; otherwise
     * leave normal chunk/Entity/POI persistence vetoed until unload or handoff.
     */
    static boolean cancel(ServerLevel level, ChunkPos pos, GenerationTask sourceTask) {
        if(sourceTask==null)return false;
        Key key=new Key(level,pos.toLong());
        Borrow borrow=ACTIVE.get(key);
        // Cancellation belongs to the exact request that armed this guard.
        // A late callback must not remove a newer task's guard.
        if(borrow==null || borrow.owner()!=sourceTask)return false;
        return releaseOnServerThread(level,key,borrow,true);
    }

    private static boolean releaseOnServerThread(ServerLevel level, Key key, Borrow borrow, boolean remove) {
        MinecraftServer server=level.getServer();
        if(server.isSameThread())return releaseCurrent(level,key,borrow,remove);
        try {
            // Normal-storage inspections and request completions may finish on I/O
            // workers. Keep ChunkAccess and POI dirty-state mutation on the server thread.
            server.execute(() -> releaseCurrent(level,key,borrow,remove));
            return true;
        } catch(RuntimeException unavailable) {
            // During executor shutdown retain the guard rather than mutating world
            // state from an arbitrary worker. ServerStoppedEvent performs final cleanup.
            return false;
        }
    }

    private static boolean releaseCurrent(ServerLevel level, Key key, Borrow borrow, boolean remove) {
        Borrow active=ACTIVE.get(key);
        if(active!=borrow)return false;
        // Check again on the server thread: a FULL may finish while cancellation
        // is queued, even when the requesting task has already stopped.
        if(remove && borrow.fullLoaded().get())return false;
        if(remove && !ACTIVE.remove(key,borrow))return false;
        releasePersistence(level,borrow);
        if(remove&&!borrow.captureDone().isDone())borrow.captureDone().complete(false);
        return true;
    }

    private static void releasePersistence(ServerLevel level, Borrow borrow) {
        synchronized(borrow) {
            if(!borrow.handedOff().compareAndSet(false,true))return;
            ChunkAccess suppressed=borrow.suppressedChunk().getAndSet(null);
            if(suppressed!=null)suppressed.setUnsaved(true);
            if(!borrow.suppressedPoiSections().isEmpty()) {
                long[] sections=borrow.suppressedPoiSections().stream().mapToLong(Long::longValue).toArray();
                borrow.suppressedPoiSections().clear();
                Object poiManager=level.getPoiManager();
                if(poiManager instanceof PregenPoiDirtyBridge bridge)bridge.maki$restorePregenPoiDirty(sections);
            }
        }
    }

    /**
     * Records the exact loaded chunk whose normal save is being vetoed. The
     * unsaved transition is serialized with handoff so an Entity/POI handoff
     * cannot race between clearing and restoring the chunk dirty bit.
     */
    static boolean suppressChunkSave(ServerLevel level, ChunkAccess chunk) {
        Borrow borrow=ACTIVE.get(new Key(level,chunk.getPos().toLong()));
        if(borrow==null)return false;
        synchronized(borrow) {
            if(borrow.handedOff().get())return false;
            borrow.suppressedChunk().set(chunk);
            chunk.setUnsaved(false);
            return true;
        }
    }

    static boolean suppressPoiSave(ServerLevel level, ChunkPos pos, Object poiManager) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        if(borrow==null)return false;
        synchronized(borrow) {
            if(borrow.handedOff().get())return false;
            if(poiManager instanceof PregenPoiDirtyBridge bridge) {
                for(long section:bridge.maki$clearPregenPoiDirty(pos)) {
                    borrow.suppressedPoiSections().add(section);
                    // Keep this obligation after restoring the temporary dirty keys.
                    borrow.poiWriteRequired().set(true);
                }
            }
            return true;
        }
    }

    static boolean needsPoiWrite(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        return borrow!=null&&borrow.poiWriteRequired().get();
    }

    static boolean isHandedOff(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        return borrow!=null&&borrow.handedOff().get();
    }

    static boolean suppressPersistence(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        return borrow!=null&&!borrow.handedOff().get();
    }

    static ServerLevel levelForPoi(Object poiManager, ChunkPos pos) {
        if(poiManager==null||pos==null)return null;
        for(Key key:ACTIVE.keySet()) {
            if(key.pos()!=pos.toLong())continue;
            ServerLevel level=key.level();
            if(level.getPoiManager()==poiManager)return level;
        }
        return null;
    }

    static void unloaded(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.remove(new Key(level,pos.toLong()));
        if(borrow!=null&&!borrow.captureDone().isDone())borrow.captureDone().complete(false);
    }

    static void clear(MinecraftServer server) {
        ACTIVE.forEach((key,borrow)->{
            if(key.level().getServer()==server) {
                ACTIVE.remove(key,borrow);
                borrow.captureDone().complete(false);
            }
        });
    }

    static void handoffPlayers(MinecraftServer server) {
        for(Key key:ACTIVE.keySet())if(key.level().getServer()==server&&playerNear(key.level(),new ChunkPos(key.pos())))
            CacheManager.pregenPersistenceSuppressed(key.level(),new ChunkPos(key.pos()));
    }

    private static boolean playerNear(ServerLevel level, ChunkPos pos) {
        int radius=level.getServer().getPlayerList().getViewDistance()+1;
        for(ServerPlayer player:level.players()) {
            ChunkPos at=player.chunkPosition();
            if(Math.abs(at.x-pos.x)<=radius&&Math.abs(at.z-pos.z)<=radius)return true;
        }
        return false;
    }
}
