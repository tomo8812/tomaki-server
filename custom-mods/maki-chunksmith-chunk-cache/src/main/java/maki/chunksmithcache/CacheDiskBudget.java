package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/** Cached disk observations only on the caller path; inventory and FileStore reads run off-thread. */
final class CacheDiskBudget {
    private static final long DEFAULT_MAX = 20L << 30;
    private static final long DEFAULT_RESERVE = 2L << 30;
    private static final Map<Path, CacheDiskBudget> ROOTS = new ConcurrentHashMap<>();
    private final Path root;
    private long observedAt;
    private long inventoriedAt;
    private long activeBytes;
    private long usableBytes;
    private long reservedBytes;
    private long additions;
    private long inventoryEpoch;
    private long maxBytes = DEFAULT_MAX;
    private long minimumFree = DEFAULT_RESERVE;
    private String observationFailure = "cache-disk-warming-up";

    private CacheDiskBudget(Path root) { this.root = root; }
    static CacheDiskBudget at(Path root) { return ROOTS.computeIfAbsent(root.toAbsolutePath().normalize(), CacheDiskBudget::new); }

    void refresh(Path config) {
        refresh(config, null, null);
    }

    void refreshForMigration(Path config, Path dimension, String targetFingerprint) {
        refresh(config, dimension.toAbsolutePath().normalize(), targetFingerprint);
    }

    private void refresh(Path config, Path migrationDimension, String migrationTargetFingerprint) {
        try {
            Properties properties = new Properties();
            if (Files.isRegularFile(config)) try (var input = Files.newInputStream(config)) { properties.load(input); }
            long max = positive(properties, "cache.max-active-bytes", DEFAULT_MAX);
            long reserve = positive(properties, "cache.minimum-free-bytes", DEFAULT_RESERVE);
            Files.createDirectories(root);
            long free = Files.getFileStore(root).getUsableSpace();
            long now = System.nanoTime();
            boolean inventory;
            long additionsBefore;
            long epoch;
            synchronized (this) {
                inventory = inventoriedAt == 0 || now - inventoriedAt > 900_000_000_000L;
                additionsBefore = additions;
                epoch = inventoryEpoch;
            }
            long bytes = inventory ? inventoriedActiveBytes(migrationDimension, migrationTargetFingerprint) : 0L;
            synchronized (this) {
                maxBytes = max;
                minimumFree = reserve;
                usableBytes = free;
                // Inventory is not a filesystem snapshot. Concurrent additions may already be in
                // it, so count them again conservatively rather than underestimating occupancy.
                if (inventory) { activeBytes = Math.addExact(bytes, additions-additionsBefore); inventoriedAt = inventoryEpoch==epoch?now:0L; }
                observedAt = now;
                observationFailure = null;
            }
        } catch (Exception error) {
            synchronized (this) { observationFailure = "cache-disk-unavailable"; }
        }
    }

    /**
     * The active quota is for writable/current cache state, not retained historical
     * generations. Retained generations are monitored separately by GenerationStore.
     *
     * A root can be either the global chunksmith_chunk_cache directory (production)
     * or a single dimension directory (self-tests/maintenance helpers). A valid
     * current manifest gives us an exact active fingerprint. If a manifest is absent
     * or unreadable we fail closed and count the unknown subtree rather than
     * underestimating storage.
     */
    private long inventoriedActiveBytes(Path migrationDimension, String migrationTargetFingerprint) throws IOException {
        FingerprintManifest.Manifest direct =
            FingerprintManifest.readIfValid(root.resolve(FingerprintManifest.CURRENT_FILE_NAME));
        if (migrationDimension != null && root.equals(migrationDimension)) {
            return dimensionActiveBytes(root, migrationTargetFingerprint);
        }
        if (direct != null) return dimensionActiveBytes(root, direct.fingerprint());

        long bytes = 0L;
        try (var children = Files.list(root)) {
            for (Path child : children.toList()) {
                if (Files.isRegularFile(child)) {
                    bytes = Math.addExact(bytes, Files.size(child));
                    continue;
                }
                if (!Files.isDirectory(child)) continue;
                String name = child.getFileName().toString();
                if (".quarantine".equals(name) || ".corrupt".equals(name)) continue;

                Path normalizedChild = child.toAbsolutePath().normalize();
                if (migrationDimension != null && normalizedChild.equals(migrationDimension)) {
                    bytes = Math.addExact(bytes, dimensionActiveBytes(child, migrationTargetFingerprint));
                    continue;
                }
                FingerprintManifest.Manifest current =
                    FingerprintManifest.readIfValid(child.resolve(FingerprintManifest.CURRENT_FILE_NAME));
                bytes = Math.addExact(
                    bytes,
                    current == null ? subtreeBytesFailClosed(child) : dimensionActiveBytes(child, current.fingerprint())
                );
            }
        }
        return bytes;
    }

    private static long dimensionActiveBytes(
        Path dimension,
        String activeFingerprint
    ) throws IOException {
        long bytes = 0L;
        Path currentManifest = dimension.resolve(FingerprintManifest.CURRENT_FILE_NAME);
        if (Files.isRegularFile(currentManifest)) bytes = Math.addExact(bytes, Files.size(currentManifest));

        Path active = dimension.resolve(activeFingerprint);
        if (Files.isDirectory(active)) bytes = Math.addExact(bytes, subtreeBytesFailClosed(active));

        // Promotion claims are authoritative ownership state outside disposable
        // generations. They still consume cache storage and must remain budgeted.
        Path promotions = RecoveryClaims.root(dimension);
        if (Files.isDirectory(promotions)) bytes = Math.addExact(bytes, subtreeBytesFailClosed(promotions));
        return bytes;
    }

    private static long subtreeBytesFailClosed(Path subtree) throws IOException {
        long bytes = 0L;
        try (var walk = Files.walk(subtree)) {
            for (Path file : walk.filter(Files::isRegularFile).toList()) {
                boolean excluded = false;
                for (Path segment : subtree.relativize(file)) {
                    String name = segment.toString();
                    if (".quarantine".equals(name) || ".corrupt".equals(name)) {
                        excluded = true;
                        break;
                    }
                }
                if (!excluded) bytes = Math.addExact(bytes, Files.size(file));
            }
        }
        return bytes;
    }

    private static long positive(Properties properties, String key, long fallback) {
        String value = properties.getProperty(key);
        if (value == null) return fallback;
        long parsed = Long.parseLong(value.trim());
        if (parsed <= 0) throw new IllegalArgumentException(key + " must be positive");
        return parsed;
    }

    synchronized String pressure(long additional) {
        if (observationFailure != null) return observationFailure;
        if (System.nanoTime() - observedAt > 60_000_000_000L) return "cache-disk-measurement-stale";
        if (additional > maxBytes - activeBytes - reservedBytes) return "cache-active-capacity";
        if (additional > usableBytes - minimumFree - reservedBytes) return "cache-disk-reserve";
        return null;
    }

    synchronized Lease tryAcquire(long estimate) {
        if (estimate <= 0 || pressure(estimate) != null) return null;
        reservedBytes += estimate;
        return new Lease(estimate);
    }

    static String pressure(Collection<Path> roots) {
        if (roots.isEmpty()) return "cache-disk-warming-up";
        for (Path root : roots) {
            CacheDiskBudget budget = at(root);
            String reason = budget.pressure(1L << 20);
            if (reason != null) return reason;
        }
        return null;
    }

    synchronized void deleted(long bytes) { activeBytes = Math.max(0, activeBytes - bytes); }
    synchronized void invalidateInventory() { inventoryEpoch++; inventoriedAt=0L; }
    synchronized long activeBytes() { return activeBytes; }
    synchronized long usableBytes() { return usableBytes; }

    final class Lease implements AutoCloseable {
        private long bytes;
        private boolean closed;
        private Lease(long bytes) { this.bytes = bytes; }
        boolean resize(long next) {
            synchronized (CacheDiskBudget.this) {
                if (closed || next <= 0 || next > bytes && pressure(next - bytes) != null) return false;
                reservedBytes += next - bytes;
                bytes = next;
                return true;
            }
        }
        void published(long addedBytes) {
            synchronized (CacheDiskBudget.this) {
                activeBytes = Math.max(0, activeBytes + addedBytes);
                additions += Math.max(0, addedBytes);
                usableBytes = Math.max(0, usableBytes - Math.max(0, addedBytes));
            }
        }
        public void close() {
            synchronized (CacheDiskBudget.this) {
                if (closed) return;
                closed = true;
                reservedBytes -= bytes;
            }
        }
    }
}
