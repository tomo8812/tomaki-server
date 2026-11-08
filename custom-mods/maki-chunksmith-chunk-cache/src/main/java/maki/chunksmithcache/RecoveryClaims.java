package maki.chunksmithcache;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The payload itself is the coordinate tombstone, outside disposable cache generations. */
final class RecoveryClaims {
    private static final Logger LOG=LoggerFactory.getLogger("MakiChunkCache");
    private static final Set<Path> READY=ConcurrentHashMap.newKeySet();
    private static final ConcurrentHashMap<Path,CompletableFuture<Void>> PREPARING=new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Path,ConcurrentHashMap<String,Set<Path>>> CONFLICT_INDEX=new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Path,Long> NEXT_FAILURE_LOG= new ConcurrentHashMap<>();
    private static final long FAILURE_LOG_INTERVAL_NANOS=TimeUnit.MINUTES.toNanos(1);
    private RecoveryClaims() {}

    static Path root(Path dimension) { return dimension.resolve(".promotions"); }

    /**
     * A promotion claim is ownership state, so an I/O or permission error is not absence.
     * Only NoSuchFileException is a confirmed miss; every other inspection failure propagates.
     */
    static boolean claimExists(Path claim) throws IOException {
        Path key=claim.toAbsolutePath().normalize();
        try {
            BasicFileAttributes attributes=Files.readAttributes(key,BasicFileAttributes.class,LinkOption.NOFOLLOW_LINKS);
            if(!attributes.isRegularFile()) throw new IOException("Promotion claim path is not a regular file: "+key);
            return true;
        } catch(NoSuchFileException missing) {
            return false;
        }
    }

    /**
     * Locate ownership for one coordinate without requiring dimension-wide recovery to succeed.
     *
     * When startup recovery is READY this is the canonical claim lookup only. Before READY, it
     * also checks every legacy location that this MOD has used: active/old generations,
     * quarantined generations, and preserved conflict payloads. A final canonical recheck closes
     * the race where the background recovery moves a legacy payload while this targeted probe runs.
     *
     * Any I/O error on a path that could own this coordinate propagates. Errors elsewhere in the
     * dimension do not prevent a proven miss for this coordinate.
     */
    static Path claimForCoordinate(Path dimension,int x,int z) throws IOException {
        Path key=dimension.toAbsolutePath().normalize();
        String region="r."+Math.floorDiv(x,32)+"."+Math.floorDiv(z,32);
        String file="c."+x+"."+z+".nbt.gz.in-use";
        Path canonical=root(key).resolve(region).resolve(file);

        if(claimExists(canonical)) return canonical;

        // READY means initialize() has indexed every preserved conflict once. Keep the normal
        // load/capture path O(1) instead of listing every UUID conflict bucket per coordinate.
        if(READY.contains(key)) return indexedConflictClaim(key,file);

        Path conflict=findConflictClaim(key,file);
        if(conflict!=null) return conflict;

        Path legacy=findLegacyClaim(key,region,file);
        if(legacy!=null) return legacy;

        // Background initialize() may have moved the exact legacy source after the first checks.
        if(claimExists(canonical)) return canonical;
        return findConflictClaim(key,file);
    }

    private static Path findLegacyClaim(Path dimension,String region,String file) throws IOException {
        BasicFileAttributes dimensionAttributes=attributesIfPresent(dimension);
        if(dimensionAttributes==null) return null;
        if(!dimensionAttributes.isDirectory()) throw new IOException("Chunk-cache dimension path is not a directory: "+dimension);

        try(Stream<Path> children=Files.list(dimension)) {
            for(Path child:children.toList()) {
                BasicFileAttributes attrs=attributesIfPresent(child);
                if(attrs==null || !attrs.isDirectory()) continue;
                String name=child.getFileName().toString();
                if(".promotions".equals(name)) continue;
                if(".quarantine".equals(name)) {
                    Path found=findQuarantinedClaim(child,region,file);
                    if(found!=null) return found;
                    continue;
                }
                // Legacy claims were siblings of cache entries inside generation/r.x.z.
                // Non-hidden directories are treated as possible old generation roots so an
                // unknown historical generation name cannot be mistaken for proven absence.
                if(name.startsWith(".")) continue;
                Path candidate=child.resolve(region).resolve(file);
                if(claimExists(candidate)) return candidate;
            }
        }
        return null;
    }

    private static Path findQuarantinedClaim(Path quarantine,String region,String file) throws IOException {
        BasicFileAttributes attrs=attributesIfPresent(quarantine);
        if(attrs==null) return null;
        if(!attrs.isDirectory()) throw new IOException("Chunk-cache quarantine path is not a directory: "+quarantine);
        try(Stream<Path> generations=Files.list(quarantine)) {
            for(Path generation:generations.toList()) {
                BasicFileAttributes generationAttrs=attributesIfPresent(generation);
                if(generationAttrs==null || !generationAttrs.isDirectory()) continue;
                Path candidate=generation.resolve(region).resolve(file);
                if(claimExists(candidate)) return candidate;
            }
        }
        return null;
    }

    private static Path findConflictClaim(Path dimension,String file) throws IOException {
        Path conflicts=root(dimension).resolve(".conflicts");
        BasicFileAttributes attrs=attributesIfPresent(conflicts);
        if(attrs==null) return null;
        if(!attrs.isDirectory()) throw new IOException("Promotion conflict path is not a directory: "+conflicts);
        try(Stream<Path> buckets=Files.list(conflicts)) {
            for(Path bucket:buckets.toList()) {
                BasicFileAttributes bucketAttrs=attributesIfPresent(bucket);
                if(bucketAttrs==null || !bucketAttrs.isDirectory()) continue;
                Path candidate=bucket.resolve(file);
                if(claimExists(candidate)) {
                    indexConflict(dimension,file,candidate);
                    return candidate;
                }
            }
        }
        return null;
    }

    private static Path indexedConflictClaim(Path dimension,String file) throws IOException {
        ConcurrentHashMap<String,Set<Path>> byFile=CONFLICT_INDEX.get(dimension);
        if(byFile==null) return null;
        Set<Path> candidates=byFile.get(file);
        if(candidates==null) return null;
        for(Path candidate:List.copyOf(candidates)) {
            if(claimExists(candidate)) return candidate;
            candidates.remove(candidate);
        }
        if(candidates.isEmpty()) byFile.remove(file,candidates);
        return null;
    }

    private static void indexConflict(Path dimension,String file,Path claim) {
        CONFLICT_INDEX.computeIfAbsent(dimension,ignored -> new ConcurrentHashMap<>())
            .computeIfAbsent(file,ignored -> ConcurrentHashMap.newKeySet())
            .add(claim.toAbsolutePath().normalize());
    }

    private static void rebuildConflictIndex(Path dimension) throws IOException {
        CONFLICT_INDEX.remove(dimension);
        Path conflicts=root(dimension).resolve(".conflicts");
        BasicFileAttributes attrs=attributesIfPresent(conflicts);
        if(attrs==null) return;
        if(!attrs.isDirectory()) throw new IOException("Promotion conflict path is not a directory: "+conflicts);
        try(Stream<Path> buckets=Files.list(conflicts)) {
            for(Path bucket:buckets.toList()) {
                BasicFileAttributes bucketAttrs=attributesIfPresent(bucket);
                if(bucketAttrs==null || !bucketAttrs.isDirectory()) continue;
                try(Stream<Path> claims=Files.list(bucket)) {
                    for(Path claim:claims.toList()) {
                        BasicFileAttributes claimAttrs=attributesIfPresent(claim);
                        String file=claim.getFileName().toString();
                        if(claimAttrs!=null && claimAttrs.isRegularFile()
                            && file.matches("c\\.-?\\d+\\.-?\\d+\\.nbt\\.gz\\.in-use")) {
                            indexConflict(dimension,file,claim);
                        }
                    }
                }
            }
        }
    }

    private static BasicFileAttributes attributesIfPresent(Path path) throws IOException {
        try {
            return Files.readAttributes(path,BasicFileAttributes.class,LinkOption.NOFOLLOW_LINKS);
        } catch(NoSuchFileException missing) {
            return null;
        }
    }

    static CompletableFuture<Void> prepare(Path dimension, Executor executor) {
        Path key=dimension.toAbsolutePath().normalize();
        CompletableFuture<Void> result=new CompletableFuture<>();
        CompletableFuture<Void> previous=PREPARING.putIfAbsent(key,result);
        if(previous!=null) return previous;
        result.whenComplete((ignored,error) -> { if(error!=null) PREPARING.remove(key,result); });
        try {
            executor.execute(() -> {
                try {
                    initialize(key);
                    NEXT_FAILURE_LOG.remove(key);
                    result.complete(null);
                }
                catch(IOException error) {
                    warnRecoveryFailure(key,error);
                    result.completeExceptionally(new UncheckedIOException("Cannot recover chunk-cache promotion ownership at "+key,error));
                }
                catch(RuntimeException error) {
                    warnRecoveryFailure(key,error);
                    result.completeExceptionally(error);
                }
            });
        } catch(RuntimeException error) {
            warnRecoveryFailure(key,error);
            result.completeExceptionally(error);
        }
        return result;
    }

    private static void warnRecoveryFailure(Path dimension,Throwable error) {
        long now=System.nanoTime();
        Long next=NEXT_FAILURE_LOG.get(dimension);
        if(next!=null && now<next) return;
        NEXT_FAILURE_LOG.put(dimension,now+FAILURE_LOG_INTERVAL_NANOS);
        LOG.warn(
            "Chunk-cache promotion recovery failed for {}; incomplete-normal loads will use coordinate-local ownership proof and fail closed when that proof is unavailable. Repair cache-directory access; recovery remains retryable.",
            dimension,error
        );
    }

    /** Off-thread, once per dimension/session, before any generation merge or deletion. */
    static void initialize(Path dimension) throws IOException {
        Path key=dimension.toAbsolutePath().normalize();
        try(var ignored=CachePathLocks.lock(root(key))) {
            if(READY.contains(key)) return;
            rebuildConflictIndex(key);
            if(Files.isDirectory(key)) Files.walkFileTree(key,new SimpleFileVisitor<>() {
                @Override public FileVisitResult preVisitDirectory(Path directory,BasicFileAttributes attrs) {
                    return directory.equals(root(key)) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
                }
                @Override public FileVisitResult visitFile(Path source,BasicFileAttributes attrs) throws IOException {
                    String name=source.getFileName().toString();
                    if(!attrs.isRegularFile() || !name.matches("c\\.-?\\d+\\.-?\\d+\\.nbt\\.gz\\.in-use")
                        || !source.getParent().getFileName().toString().matches("r\\.-?\\d+\\.-?\\d+")) return FileVisitResult.CONTINUE;
                    Path target=root(key).resolve(source.getParent().getFileName()).resolve(name);
                    try(var claimLock=CachePathLocks.lock(target)) {
                    Files.createDirectories(target.getParent());
                    if(Files.exists(target)) {
                        // Keep every interrupted payload; the canonical one still blocks this coordinate.
                        target=root(key).resolve(".conflicts").resolve(UUID.randomUUID().toString()).resolve(name);
                        Files.createDirectories(target.getParent());
                    }
                    Files.move(source,target,StandardCopyOption.ATOMIC_MOVE);
                    if(target.startsWith(root(key).resolve(".conflicts"))) indexConflict(key,name,target);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            READY.add(key);
        }
    }

    static void clear(Path dimension) {
        Path key=dimension.toAbsolutePath().normalize();
        PREPARING.remove(key);
        READY.remove(key);
        CONFLICT_INDEX.remove(key);
        NEXT_FAILURE_LOG.remove(key);
    }
}
