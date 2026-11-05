package maki.millenairelod;

import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import com.kishku7.chunksmith.lod.HorizonGuard;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.lod.LodSupport;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.millenaire.culture.ModCultures;
import org.millenaire.village.Village;
import org.millenaire.village.VillageSavedData;
import org.millenaire.world.VillageSpawnQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Compat {
    private static final Logger LOG = LoggerFactory.getLogger("MakiMillenaireLOD");
    private static final Map<ServerLevel, Protection> PROTECTION = new WeakHashMap<>();
    private static final Map<Village, Long> VILLAGE_VERSIONS = new WeakHashMap<>();
    private static final Object PROTECTION_LOCK = new Object();
    private static volatile long protectionVersion;
    private static volatile long fullRangeVersion;
    private static boolean refreshFailureLogged;

    private Compat() {
    }

    public static void onChunk(VillageSpawnQueue queue, ChunkPos pos, boolean natural, ServerLevel level) {
        queue.markResolved(pos);
        DeferredData data = DeferredData.get(level);
        long key = pos.toLong();
        if ((natural || data.pending.contains(key)) && !data.queued.contains(key)) {
            BlockPos sample = pos.getMiddleBlockPosition(level.getMaxBuildHeight() - 1);
            Holder<Biome> biome = level.getBiome(sample);
            if (!VillageSpawnQueue.couldHostAnyVillage(ModCultures.getAllVillageTypes().values(), biome::is)) {
                data.consume(key);
            } else if (HorizonGuard.drop(level, pos)) {
                data.remember(key, false);
            } else {
                enqueue(level, queue, pos);
            }
        }
    }

    private static void enqueue(ServerLevel level, VillageSpawnQueue queue, ChunkPos pos) {
        DeferredData data = DeferredData.get(level);
        long key = pos.toLong();
        if (!data.queued.contains(key)) {
            data.remember(key, true);
            queue.enqueue(pos.getMiddleBlockPosition(64));
        }
    }

    public static void replay(ServerLevel level, VillageSpawnQueue queue) {
        ReplayController.replay(level, queue);
    }

    public static boolean beginCandidate(ServerLevel level, BlockPos pos) {
        return ReplayController.beginCandidate(level, pos);
    }

    public static void waiting(ServerLevel level, BlockPos pos, boolean queued) {
        DeferredData.get(level).remember(new ChunkPos(pos).toLong(), queued);
    }

    public static boolean protectedChunk(ServerLevel level, ChunkPos pos) {
        ReservationLifecycle.expire(level);
        if (level == null || pos == null) {
            return false;
        }

        Protection protection = PROTECTION.computeIfAbsent(level, ignored -> new Protection());
        if (protection.syncedVersion != protectionVersion) {
            syncProtection(level, protection);
        }

        long key = pos.toLong();
        return protection.villages.contains(key) || protection.reservations.contains(key);
    }

    private static void syncProtection(ServerLevel level, Protection protection) {
        synchronized (PROTECTION_LOCK) {
            long targetVersion = protectionVersion;
            if (protection.syncedVersion == targetVersion) {
                return;
            }

            long targetFullRangeVersion = fullRangeVersion;
            boolean refreshAllRanges = protection.syncedFullRangeVersion != targetFullRangeVersion;
            Collection<Village> villages = VillageSavedData.get(level).getVillageManager().getAllVillages();
            IdentityHashMap<Village, Boolean> present = new IdentityHashMap<>();

            for (Village village : villages) {
                present.put(village, Boolean.TRUE);
                long villageVersion = VILLAGE_VERSIONS.getOrDefault(village, 0L);
                CachedVillage cached = protection.byVillage.get(village);
                if (cached == null || refreshAllRanges || cached.version != villageVersion) {
                    replaceVillage(protection, village, villageVersion, cached);
                }
            }

            Iterator<Map.Entry<Village, CachedVillage>> iterator = protection.byVillage.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Village, CachedVillage> entry = iterator.next();
                if (!present.containsKey(entry.getKey())) {
                    removeChunks(protection, entry.getValue().chunks);
                    iterator.remove();
                }
            }

            protection.syncedFullRangeVersion = targetFullRangeVersion;
            protection.syncedVersion = targetVersion;
        }
    }

    private static void replaceVillage(Protection protection, Village village, long version, CachedVillage previous) {
        if (previous != null) {
            removeChunks(protection, previous.chunks);
        }

        Set<ChunkPos> chunks = village.computeVillageChunks();
        long[] packed = new long[chunks.size()];
        int index = 0;
        for (ChunkPos chunk : chunks) {
            long key = chunk.toLong();
            packed[index++] = key;
            int refs = protection.references.getOrDefault(key, 0);
            protection.references.put(key, refs + 1);
            if (refs == 0) {
                protection.villages.add(key);
            }
        }
        protection.byVillage.put(village, new CachedVillage(version, packed));
    }

    private static void removeChunks(Protection protection, long[] chunks) {
        for (long key : chunks) {
            int refs = protection.references.getOrDefault(key, 0);
            if (refs <= 1) {
                protection.references.remove(key);
                protection.villages.remove(key);
            } else {
                protection.references.put(key, refs - 1);
            }
        }
    }

    public static void invalidateVillage(Village village) {
        if (village == null) {
            return;
        }
        synchronized (PROTECTION_LOCK) {
            long version = protectionVersion + 1L;
            protectionVersion = version;
            VILLAGE_VERSIONS.put(village, version);
        }
    }

    public static void invalidateVillageMembership() {
        synchronized (PROTECTION_LOCK) {
            protectionVersion++;
        }
    }

    public static void invalidateAllVillageRanges() {
        synchronized (PROTECTION_LOCK) {
            long version = protectionVersion + 1L;
            protectionVersion = version;
            fullRangeVersion = version;
        }
    }

    public static void reserve(ServerLevel level, BlockPos pos, int radiusBlocks) {
        Protection protection = PROTECTION.computeIfAbsent(level, ignored -> new Protection());
        if (!LodHorizon.isArmed(LodSupport.dimensionId(level))) {
            ReservationLifecycle.begin(level, pos, radiusBlocks);
            return;
        }

        ChunkPos center = new ChunkPos(pos);
        int radius = Math.max(0, (radiusBlocks + 15) / 16) + 4;
        ReservationLifecycle.ignoreKeepAround(LodSupport.dimensionId(level), center.x, center.z, radius);
        for (int x = center.x - radius; x <= center.x + radius; x++) {
            for (int z = center.z - radius; z <= center.z + radius; z++) {
                protection.reservations.add(ChunkPos.asLong(x, z));
            }
        }
        ReservationLifecycle.begin(level, pos, radiusBlocks);
    }

    public static void refresh(ServerLevel level, ChunkAccess access) {
        if (!(access instanceof LevelChunk chunk) || !chunk.isUnsaved()) {
            return;
        }
        if (!protectedChunk(level, chunk.getPos()) || !LodSupport.lodEnabled(level.getServer())) {
            return;
        }

        try {
            CsLodPresenceIndex presence =
                LodSupport.presenceIndexFor(level.getServer(), LodSupport.dimensionId(level));
            if (presence != null && presence.hasLod(chunk.getPos().x, chunk.getPos().z)) {
                LodSupport.offer(level, chunk);
            }
        } catch (RuntimeException | LinkageError error) {
            if (!refreshFailureLogged) {
                refreshFailureLogged = true;
                LOG.error("Could not refresh village LOD; real chunk saving will continue", error);
            }
        }
    }

    public static Object maki$protection(Object level) {
        return PROTECTION.get(level);
    }

    public static Object maki$reservations(Object protection) {
        return ((Protection)protection).reservations;
    }

    public static boolean maki$containsReservation(Object reservations, long key) {
        return ((LongOpenHashSet)reservations).contains(key);
    }

    public static void maki$removeReservation(Object reservations, long key) {
        ((LongOpenHashSet)reservations).remove(key);
    }

    public static void maki$invalidateVillageCache(Object protection) {
        ((Protection)protection).syncedVersion = Long.MIN_VALUE;
    }

    public static long maki$gameTime(Object level) {
        return ((ServerLevel)level).getGameTime();
    }

    public static int maki$blockX(Object pos) {
        return ((BlockPos)pos).getX();
    }

    public static int maki$blockZ(Object pos) {
        return ((BlockPos)pos).getZ();
    }

    private static final class CachedVillage {
        final long version;
        final long[] chunks;

        CachedVillage(long version, long[] chunks) {
            this.version = version;
            this.chunks = chunks;
        }
    }

    private static final class Protection {
        long syncedVersion = Long.MIN_VALUE;
        long syncedFullRangeVersion = Long.MIN_VALUE;
        final IdentityHashMap<Village, CachedVillage> byVillage = new IdentityHashMap<>();
        final HashMap<Long, Integer> references = new HashMap<>();
        final LongOpenHashSet villages = new LongOpenHashSet();
        final LongOpenHashSet reservations = new LongOpenHashSet();
    }
}
