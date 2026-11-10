package maki.lodcpu;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.GenerationTask;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.Comparator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** One-time reset of only disposable pregen state for the Issue #83 unified pipeline. */
final class UnifiedPregenReset {
    private static final Logger LOG=LoggerFactory.getLogger("MakiLodFollow");
    private static final String WORLD="minecraft:overworld";
    private static MinecraftServer completedServer;
    private static MinecraftServer preparedServer;
    private static boolean storagePrepared;
    private static long retryAfter;

    private UnifiedPregenReset(){}

    /**
     * Clear disposable storage before Chunksmith can create any LOD sink or resume
     * a persisted task. Never retry this destructive phase from a live server tick.
     */
    static synchronized void prepareBeforeLod(MinecraftServer server) {
        Path world=server.getWorldPath(LevelResource.ROOT);
        Path marker=marker(world);
        if(Files.isRegularFile(marker)) {
            preparedServer=completedServer=server;
            storagePrepared=true;
            return;
        }
        preparedServer=server;
        storagePrepared=false;
        try {
            deleteTree(world.resolve("chunksmith").resolve("lod").resolve("minecraft_overworld"));
            resetPregenCacheDimension(world.resolve("chunksmith_chunk_cache").resolve("minecraft_overworld"));
            storagePrepared=true;
            LOG.info("Issue #83 disposable LOD/cache storage was reset before Chunksmith LOD startup; owned task progress will be finalized after Chunksmith initializes.");
        } catch(Exception error) {
            LOG.warn("Issue #83 disposable storage reset failed before Chunksmith LOD startup; unified pregen stays paused for this server session and will retry at the next server start",error);
        }
    }

    static synchronized boolean ensure(MinecraftServer server) {
        if(completedServer==server)return true;
        Path world=server.getWorldPath(LevelResource.ROOT);
        Path marker=marker(world);
        if(Files.isRegularFile(marker)){
            preparedServer=completedServer=server;
            storagePrepared=true;
            return true;
        }
        // The storage phase is intentionally startup-only. Deleting a live .cslod
        // tree can race the native writer's open RandomAccessFile handles.
        if(preparedServer!=server||!storagePrepared)return false;
        long now=System.currentTimeMillis();
        if(now<retryAfter)return false;

        try {
            if(ChunksmithProvider.isLoaded()&&ChunksmithProvider.get()!=null) {
                var chunky=ChunksmithProvider.get();
                GenerationTask active=chunky.getGenerationTasks().get(WORLD);
                if(active!=null) {
                    var owner=TaskOwnership.persistedOwner(chunky,WORLD);
                    if(owner==TaskOwnership.PersistedOwner.CURRENT_SESSION||owner==TaskOwnership.PersistedOwner.PREVIOUS_SESSION) {
                        TaskOwnership.claim(active,"Issue83-reset");
                        active.stop(false);
                        retryAfter=now+1000L;
                        return false;
                    }
                    LOG.warn("Unified pregen reset is waiting for the protected external overworld Chunksmith task to stop.");
                    retryAfter=now+5000L;
                    return false;
                }
            }

            if(ChunksmithProvider.isLoaded()&&ChunksmithProvider.get()!=null) {
                var chunky=ChunksmithProvider.get();
                var owner=TaskOwnership.persistedOwner(chunky,WORLD);
                if(owner!=TaskOwnership.PersistedOwner.EXTERNAL) {
                    Files.deleteIfExists(chunky.getConfig().getDirectory().resolve("tasks").resolve("minecraft").resolve("overworld.properties"));
                }
            }

            Files.createDirectories(marker.getParent());
            Files.writeString(marker,
                "issue=83\nversion=1\nreset=overworld-lod,overworld-pregen-cache,owned-overworld-task\npreserved=promotion-claims,other-dimensions,normal-world-data\n",
                StandardOpenOption.CREATE_NEW,StandardOpenOption.WRITE);
            completedServer=server;
            LOG.info("Issue #83 unified pregen reset complete: old LOD, pregen FULL cache, and owned follow task progress were cleared; normal world/player/Millenaire/VS2 data were untouched.");
            return true;
        } catch(Exception error) {
            retryAfter=now+30_000L;
            LOG.warn("Unified pregen task-state cleanup is incomplete; generation remains paused and this non-destructive phase will retry",error);
            return false;
        }
    }

    static synchronized void stopped(MinecraftServer server){
        if(completedServer==server)completedServer=null;
        if(preparedServer==server){
            preparedServer=null;
            storagePrepared=false;
        }
        retryAfter=0L;
    }

    private static Path marker(Path world) {
        return world.resolve("data/maki_pregen_unified_v1.reset");
    }

    private static void resetPregenCacheDimension(Path dimension) throws IOException {
        if(!Files.isDirectory(dimension))return;
        preservePromotionClaims(dimension);
        try(var children=Files.list(dimension)) {
            for(Path child:children.toList()) {
                if(".promotions".equals(child.getFileName().toString()))continue;
                deleteTree(child);
            }
        }
    }

    /**
     * Promotion payloads are no longer disposable pregen output: after a cached FULL
     * is handed to the normal world they are the restart-safe ownership boundary.
     * Preserve canonical and legacy .in-use payloads before resetting generations.
     */
    private static void preservePromotionClaims(Path dimension) throws IOException {
        Path promotions=dimension.resolve(".promotions");
        Files.createDirectories(promotions);
        try(var walk=Files.walk(dimension)) {
            for(Path source:walk.filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".nbt.gz.in-use"))
                .filter(path -> !path.startsWith(promotions)).toList()) {
                String region=source.getParent().getFileName().toString();
                Path target=promotions.resolve(region).resolve(source.getFileName());
                if(Files.exists(target)) {
                    target=promotions.resolve(".conflicts").resolve(UUID.randomUUID().toString()).resolve(source.getFileName());
                }
                Files.createDirectories(target.getParent());
                try { Files.move(source,target,StandardCopyOption.ATOMIC_MOVE); }
                catch(java.nio.file.AtomicMoveNotSupportedException unsupported) { Files.move(source,target); }
            }
        }
    }

    private static void deleteTree(Path root) throws IOException {
        if(!Files.exists(root))return;
        try(var walk=Files.walk(root)) {
            for(Path path:walk.sorted(Comparator.reverseOrder()).toList())Files.deleteIfExists(path);
        }
    }
}
