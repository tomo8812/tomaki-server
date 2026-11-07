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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/** The payload itself is the coordinate tombstone, outside disposable cache generations. */
final class RecoveryClaims {
    private static final Set<Path> READY=ConcurrentHashMap.newKeySet();
    private static final ConcurrentHashMap<Path,CompletableFuture<Void>> PREPARING=new ConcurrentHashMap<>();
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

    static CompletableFuture<Void> prepare(Path dimension, Executor executor) {
        Path key=dimension.toAbsolutePath().normalize();
        CompletableFuture<Void> result=new CompletableFuture<>();
        CompletableFuture<Void> previous=PREPARING.putIfAbsent(key,result);
        if(previous!=null) return previous;
        result.whenComplete((ignored,error) -> { if(error!=null) PREPARING.remove(key,result); });
        try {
            executor.execute(() -> {
                try { initialize(key); result.complete(null); }
                catch(IOException error) { result.completeExceptionally(new UncheckedIOException("Cannot recover chunk-cache promotion ownership at "+key,error)); }
                catch(RuntimeException error) { result.completeExceptionally(error); }
            });
        } catch(RuntimeException error) { result.completeExceptionally(error); }
        return result;
    }

    /** Off-thread, once per dimension/session, before any generation merge or deletion. */
    static void initialize(Path dimension) throws IOException {
        Path key=dimension.toAbsolutePath().normalize();
        try(var ignored=CachePathLocks.lock(root(key))) {
            if(READY.contains(key)) return;
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
    }
}
