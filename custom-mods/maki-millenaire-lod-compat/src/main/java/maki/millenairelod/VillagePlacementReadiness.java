package maki.millenairelod;

import com.kishku7.chunksmith.lod.HorizonGuard;
import maki.chunksmithcache.BackfillGuard;
import maki.chunksmithcache.CacheManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.fml.ModList;
import org.millenaire.world.VillageTerrainMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Admission for actual Millenaire construction, independent of the origin of
 * the FULL chunk (normal generation, disk load, or a restored cold cache).
 *
 * Chunksmith's HorizonGuard is only a remaining write-veto check when its
 * pregeneration horizon happens to be armed. It is not a readiness signal.
 */
public final class VillagePlacementReadiness {
    private static final Logger LOG = LoggerFactory.getLogger("MakiMillenaireLOD");
    private static volatile boolean persistenceErrorLogged;

    private VillagePlacementReadiness() {}

    /** Used by the deferred candidate queue before giving work to Millenaire. */
    public static boolean candidateReady(Object level, Object pos) {
        if (!(level instanceof ServerLevel world) || !(pos instanceof ChunkPos chunk)) return false;
        return hasNearbyPlayer(world, chunk.x, chunk.z) && loadedAndPersistable(world, chunk.x, chunk.z);
    }

    /**
     * SiteHunter 9.1.0's exact placementAreaLoaded rectangle is radius+5,
     * snapped to chunk boundaries and padded by one chunk on every side.
     * Check every FULL chunk in that same area, not only the village center.
     * Native terrain scoring continues independently while this returns false.
     */
    public static boolean areaReady(ServerLevel level, int centerX, int centerZ, int radius) {
        if (level == null || radius < 0 || !level.getServer().isSameThread()) return false;
        if (!hasNearbyPlayer(level, centerX >> 4, centerZ >> 4)) return false;
        if (!VillageTerrainMap.placementAreaLoaded(level, centerX, centerZ, radius)) return false;

        long extent = (long) radius + 5L;
        long minX = (long) centerX - extent, maxX = (long) centerX + extent;
        long minZ = (long) centerZ - extent, maxZ = (long) centerZ + extent;
        if (minX < Integer.MIN_VALUE + 16L || maxX > Integer.MAX_VALUE - 16L
                || minZ < Integer.MIN_VALUE + 16L || maxZ > Integer.MAX_VALUE - 16L) return false;

        // Match VillageTerrainMap.placementBounds + placementAreaLoaded:
        // one extra chunk outside the rounded rectangle in each direction.
        int fromX = ((int) minX >> 4) - 1, toX = ((int) maxX >> 4) + 1;
        int fromZ = ((int) minZ >> 4) - 1, toZ = ((int) maxZ >> 4) + 1;
        for (int x = fromX; x <= toX; x++) {
            for (int z = fromZ; z <= toZ; z++) {
                if (!loadedAndPersistable(level, x, z)) return false;
            }
        }
        return true;
    }

    private static boolean hasNearbyPlayer(ServerLevel level, int x, int z) {
        int view = Math.max(1, level.getServer().getPlayerList().getViewDistance() + 1);
        for (ServerPlayer player : level.players()) {
            ChunkPos at = player.chunkPosition();
            if (Math.abs((long) at.x - x) <= view && Math.abs((long) at.z - z) <= view) return true;
        }
        return false;
    }

    private static boolean loadedAndPersistable(ServerLevel level, int x, int z) {
        // getChunkNow never triggers worldgen or a disk/cache load. A non-null
        // result is a FULL chunk already published by the native load pipeline.
        if (level.getChunkSource().getChunkNow(x, z) == null) return false;
        ChunkPos pos = new ChunkPos(x, z);
        try {
            if (ModList.get().isLoaded("maki_chunksmith_chunk_cache")) {
                // Both APIs agree with the actual chunk/POI write vetoes. The
                // pregen check also performs an owned player handoff when due.
                if (BackfillGuard.suppress(level, pos)
                        || CacheManager.pregenPersistenceSuppressed(level, pos)) return false;
            }
            // A concurrent external Chunksmith task can still veto normal
            // writes. This is a final safety check, not a load/restore gate.
            return !HorizonGuard.drop(level, pos);
        } catch (RuntimeException | LinkageError error) {
            if (!persistenceErrorLogged) {
                persistenceErrorLogged = true;
                LOG.error("Unable to establish safe village persistence; retaining Millenaire candidate", error);
            }
            return false;
        }
    }
}
