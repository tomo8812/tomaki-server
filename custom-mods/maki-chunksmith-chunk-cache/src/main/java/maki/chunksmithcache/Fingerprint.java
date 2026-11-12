package maki.chunksmithcache;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

/**
 * Stable identity of the FULL cache envelope, independent of server versions,
 * mods, world seed, generator options, configuration and data packs.
 *
 * The old 24-hex directory format is retained so already saved caches can be
 * adopted without renaming the contents of their NBT envelopes. This value is
 * a cache storage namespace, never a compatibility probe.
 */
final class Fingerprint {
    static final String CACHE_ID = "f00dcace0000000000000003";
    private static final Map<MinecraftServer, CompletableFuture<CacheRetentionPolicy>> POLICIES = new WeakHashMap<>();
    private static final Map<ServerLevel, Snapshot> LEVEL_SNAPSHOTS = new WeakHashMap<>();

    private Fingerprint() {}

    static synchronized void kick(MinecraftServer server) {
        POLICIES.computeIfAbsent(server, value ->
            CompletableFuture.supplyAsync(() -> CacheRetentionPolicy.load(value.getServerDirectory())));
    }

    static String forLevel(ServerLevel level) {
        Snapshot snapshot = snapshotForLevel(level);
        return snapshot == null ? null : snapshot.fingerprint();
    }

    static CompletableFuture<Snapshot> whenReady(ServerLevel level) {
        kick(level.getServer());
        CompletableFuture<CacheRetentionPolicy> policy;
        synchronized (Fingerprint.class) {
            policy = POLICIES.get(level.getServer());
        }
        return policy.thenApply(ignored -> snapshotForLevel(level));
    }

    static Snapshot snapshotForLevel(ServerLevel level) {
        kick(level.getServer());
        CompletableFuture<CacheRetentionPolicy> ready;
        synchronized (Fingerprint.class) {
            Snapshot cached = LEVEL_SNAPSHOTS.get(level);
            if (cached != null) return cached;
            ready = POLICIES.get(level.getServer());
        }
        if (ready == null || !ready.isDone() || ready.isCompletedExceptionally()) return null;
        try {
            Snapshot snapshot = new Snapshot(CACHE_ID, ready.join());
            synchronized (Fingerprint.class) {
                return LEVEL_SNAPSHOTS.computeIfAbsent(level, unused -> snapshot);
            }
        } catch (Throwable ignored) {
            return null;
        }
    }

    record Snapshot(String fingerprint, CacheRetentionPolicy policy) {
        FingerprintManifest.Manifest manifest(Set<String> storedFingerprints) {
            return new FingerprintManifest.Manifest(
                fingerprint, System.currentTimeMillis(), storedFingerprints);
        }
    }
}
