package maki.millenairelod;

import com.kishku7.chunksmith.lod.HorizonGuard;
import maki.chunksmithcache.CacheManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.millenaire.world.VillageTerrainMap;

/**
 * Admission for actual Millenaire construction, independent of the origin of
 * the FULL chunk (normal generation, disk load, or a restored cold cache).
 *
 * Chunksmith's HorizonGuard is only a remaining write-veto check when its
 * pregeneration horizon happens to be armed. It is not a readiness signal.
 */
public final class VillagePlacementReadiness {

    private VillagePlacementReadiness() {}

    /** Used by the deferred candidate queue before giving work to Millenaire. */
    public static boolean candidateReady(ServerLevel level, ChunkPos pos) {
        return hasNearbyPlayer(level, pos.x, pos.z) && loadedAndPersistable(level, pos.x, pos.z);
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
        return CacheManager.playerLoadReady(level,pos)&&!HorizonGuard.drop(level,pos);
    }
}
