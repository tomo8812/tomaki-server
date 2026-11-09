package maki.chunksmithcache;

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
 * cache FULL is borrowed for LOD repair, and remains until unload/player handoff.
 */
final class PregenBorrowLedger {
    enum Kind { GENERATED, BORROWED }
    private record Key(ServerLevel level, long pos) {}
    private record Borrow(Path file, Kind kind, CompletableFuture<Boolean> lodDone, AtomicBoolean handedOff,
                          java.util.concurrent.atomic.AtomicReference<ChunkAccess> suppressedChunk,
                          java.util.Set<Long> suppressedPoiSections) {}
    private static final ConcurrentHashMap<Key, Borrow> ACTIVE = new ConcurrentHashMap<>();

    private PregenBorrowLedger() {}

    static boolean beginGenerated(ServerLevel level, ChunkPos pos, Path file) {
        return begin(level,pos,file,Kind.GENERATED);
    }

    static boolean beginBorrowed(ServerLevel level, ChunkPos pos, Path file) {
        return begin(level,pos,file,Kind.BORROWED);
    }

    private static boolean begin(ServerLevel level, ChunkPos pos, Path file, Kind kind) {
        Key key=new Key(level,pos.toLong());
        Borrow existing=ACTIVE.get(key);
        if(existing!=null)return existing.kind()==kind && existing.file().equals(file);
        CompletableFuture<Boolean> lodDone=new CompletableFuture<>();
        lodDone.completeOnTimeout(false,30L,TimeUnit.SECONDS);
        Borrow borrow=new Borrow(file,kind,lodDone,new AtomicBoolean(),new java.util.concurrent.atomic.AtomicReference<>(),new java.util.HashSet<>());
        return ACTIVE.putIfAbsent(key,borrow)==null;
    }

    static Kind kind(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        return borrow==null?null:borrow.kind();
    }

    /** Blocks a competing normal cache load only while the owned LOD write is unresolved. */
    static CompletableFuture<Boolean> pending(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        return borrow==null||borrow.lodDone().isDone()?null:borrow.lodDone();
    }

    static boolean attachLod(ServerLevel level, ChunkPos pos, CompletableFuture<Boolean> lodCommitted) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        if(borrow==null)return false;
        lodCommitted.whenComplete((saved,error)->borrow.lodDone().complete(error==null&&Boolean.TRUE.equals(saved)));
        return true;
    }

    static Path handoffCandidate(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        return borrow==null||borrow.handedOff().get()||!playerNear(level,pos)?null:borrow.file();
    }

    static void handedOff(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.get(new Key(level,pos.toLong()));
        if(borrow==null)return;
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
                for(long section:bridge.maki$clearPregenPoiDirty(pos))borrow.suppressedPoiSections().add(section);
            }
            return true;
        }
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
        if(borrow!=null&&!borrow.lodDone().isDone())borrow.lodDone().complete(false);
    }

    static void cancel(ServerLevel level, ChunkPos pos) {
        Borrow borrow=ACTIVE.remove(new Key(level,pos.toLong()));
        if(borrow!=null)borrow.lodDone().complete(false);
    }

    static void clear(MinecraftServer server) {
        ACTIVE.forEach((key,borrow)->{
            if(key.level().getServer()==server) {
                ACTIVE.remove(key,borrow);
                borrow.lodDone().complete(false);
            }
        });
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
