package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/** Cached disk observations only on the caller path; inventory and FileStore reads run off-thread. */
final class CacheDiskBudget {
    private static final long DEFAULT_MAX = 15L << 30;
    private static final long DEFAULT_RESERVE = 2L << 30;
    private static final Map<Path, CacheDiskBudget> ROOTS = new ConcurrentHashMap<>();
    private final Path root;
    private long observedAt;
    private long inventoriedAt;
    private long activeBytes;
    private long lodBytes;
    private long usableBytes;
    private long cacheUsableBytes;
    private long lodUsableBytes;
    private boolean sharedFileStore=true;
    private long reservedBytes;
    private long additions;
    private long lodAdditions;
    private long cachePublications;
    private long lodPublications;
    private long inventoryEpoch;
    private long maxBytes = DEFAULT_MAX;
    private long minimumFree = DEFAULT_RESERVE;
    private String observationFailure = "cache-disk-warming-up";

    private CacheDiskBudget(Path root) { this.root = root; }
    static CacheDiskBudget at(Path root) { return ROOTS.computeIfAbsent(root.toAbsolutePath().normalize(), CacheDiskBudget::new); }

    void refresh(Path config) {
        try {
            Properties properties = new Properties();
            if (Files.isRegularFile(config)) try (var input = Files.newInputStream(config)) { properties.load(input); }
            long max = positive(properties, "cache.max-active-bytes", DEFAULT_MAX);
            long reserve = positive(properties, "cache.minimum-free-bytes", DEFAULT_RESERVE);
            Files.createDirectories(root);
            Path lodRoot=root.getParent().resolve("chunksmith").resolve("lod");
            Files.createDirectories(lodRoot);
            Path realRoot=root.toRealPath();
            Path realLodRoot=lodRoot.toRealPath();
            FileStore cacheStore=Files.getFileStore(realRoot);
            FileStore lodStore=Files.getFileStore(realLodRoot);
            boolean sameStore=samePhysicalStore(realRoot,cacheStore,realLodRoot,lodStore);
            long cachePublicationsBefore,lodPublicationsBefore;
            synchronized(this){
                cachePublicationsBefore=cachePublications;
                lodPublicationsBefore=lodPublications;
            }
            long freeCache=cacheStore.getUsableSpace();
            long freeLod=lodStore.getUsableSpace();
            long now=System.nanoTime();
            boolean inventory;
            long additionsBefore,lodAdditionsBefore;
            long epoch;
            synchronized(this){
                inventory=inventoriedAt==0||now-inventoriedAt>900_000_000_000L;
                additionsBefore=additions;
                lodAdditionsBefore=lodAdditions;
                epoch=inventoryEpoch;
            }
            long bytes=0;
            if(inventory){
                try(var walk=Files.walk(realRoot)){
                    var files=walk.filter(Files::isRegularFile).iterator();
                    while(files.hasNext()){
                        Path file=files.next();
                        boolean quarantine=false;
                        for(Path segment:realRoot.relativize(file))
                            if(segment.toString().equals(".quarantine")||segment.toString().equals(".corrupt")){quarantine=true;break;}
                        if(!quarantine)bytes=Math.addExact(bytes,Files.size(file));
                    }
                }
            }
            long observedLodBytes=inventory?treeBytes(realLodRoot):0L;
            synchronized (this) {
                maxBytes = max;
                minimumFree = reserve;
                // Preserve publications racing a slow FileStore probe. Counting a
                // write twice when already in the probe is conservative.
                long cacheDelta=Math.max(0L,cachePublications-cachePublicationsBefore);
                long lodDelta=Math.max(0L,lodPublications-lodPublicationsBefore);
                sharedFileStore=sameStore;
                if(sameStore){
                    long shared=Math.max(0L,Math.min(freeCache,freeLod)-cacheDelta-lodDelta);
                    cacheUsableBytes=shared;
                    lodUsableBytes=shared;
                }else{
                    cacheUsableBytes=Math.max(0L,freeCache-cacheDelta);
                    lodUsableBytes=Math.max(0L,freeLod-lodDelta);
                }
                usableBytes=Math.min(cacheUsableBytes,lodUsableBytes);
                // Inventory is not a filesystem snapshot. Concurrent additions may already be in
                // it, so count them again conservatively rather than underestimating occupancy.
                if(inventory){
                    activeBytes=Math.addExact(bytes,additions-additionsBefore);
                    lodBytes=Math.addExact(observedLodBytes,lodAdditions-lodAdditionsBefore);
                    inventoriedAt=inventoryEpoch==epoch?now:0L;
                }
                observedAt = now;
                observationFailure = null;
            }
        } catch (Exception error) {
            synchronized (this) { observationFailure = "cache-disk-unavailable"; }
        }
    }

    private static boolean samePhysicalStore(Path cacheRoot,FileStore cacheStore,Path lodRoot,FileStore lodStore) {
        if(cacheStore.equals(lodStore))return true;
        try{
            Object cacheDev=Files.getAttribute(cacheRoot,"unix:dev");
            Object lodDev=Files.getAttribute(lodRoot,"unix:dev");
            if(cacheDev!=null&&lodDev!=null)return cacheDev.equals(lodDev);
        }catch(Exception ignored){}
        return cacheStore.name().equals(lodStore.name())&&cacheStore.type().equals(lodStore.type());
    }

    private static long treeBytes(Path tree) throws IOException {
        if (!Files.isDirectory(tree)) return 0L;
        long total=0L;
        try (var walk=Files.walk(tree)) {
            var files=walk.filter(Files::isRegularFile).iterator();
            while(files.hasNext()) total=Math.addExact(total,Files.size(files.next()));
        }
        return total;
    }

    private static long positive(Properties properties, String key, long fallback) {
        String value = properties.getProperty(key);
        if (value == null) return fallback;
        long parsed = Long.parseLong(value.trim());
        if (parsed <= 0) throw new IllegalArgumentException(key + " must be positive");
        return parsed;
    }

    synchronized String pressure(long additional) {
        if(observationFailure!=null)return observationFailure;
        if(System.nanoTime()-observedAt>60_000_000_000L)return "cache-disk-measurement-stale";
        long lodProjection;
        try{lodProjection=Math.multiplyExact((long)PregenFlowController.snapshot().lodOutstanding(),32L<<20);}
        catch(ArithmeticException overflow){lodProjection=Long.MAX_VALUE;}
        long committed;
        try{committed=Math.addExact(Math.addExact(activeBytes,lodBytes),Math.addExact(reservedBytes,lodProjection));}
        catch(ArithmeticException overflow){return "pregen-combined-capacity";}
        if(additional>maxBytes-committed)return "pregen-combined-capacity";
        try{
            long fullDebt=Math.addExact(reservedBytes,additional);
            if(sharedFileStore){
                long sharedDebt=Math.addExact(fullDebt,lodProjection);
                if(sharedDebt>Math.max(0L,cacheUsableBytes-minimumFree))return "cache-disk-reserve";
            }else{
                if(fullDebt>Math.max(0L,cacheUsableBytes-minimumFree))return "cache-disk-reserve";
                if(lodProjection>Math.max(0L,lodUsableBytes-minimumFree))return "lod-disk-reserve";
            }
        }catch(ArithmeticException overflow){return "cache-disk-reserve";}
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

    synchronized void lodPublished(long bytes) {
        if(bytes<=0)return;
        lodBytes=Math.max(0,lodBytes+bytes);
        lodAdditions+=bytes;
        lodPublications+=bytes;
        lodUsableBytes=Math.max(0,lodUsableBytes-bytes);
        if(sharedFileStore)cacheUsableBytes=Math.max(0,cacheUsableBytes-bytes);
        usableBytes=Math.min(cacheUsableBytes,lodUsableBytes);
    }
    synchronized void deleted(long bytes) { activeBytes = Math.max(0, activeBytes - bytes); }
    synchronized void invalidateInventory() { inventoryEpoch++; inventoriedAt=0L; }
    synchronized long activeBytes() { return Math.max(0L, activeBytes + lodBytes); }
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
                long published=Math.max(0,addedBytes);
                additions+=published;
                cachePublications+=published;
                cacheUsableBytes=Math.max(0,cacheUsableBytes-published);
                if(sharedFileStore)lodUsableBytes=Math.max(0,lodUsableBytes-published);
                usableBytes=Math.min(cacheUsableBytes,lodUsableBytes);
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
