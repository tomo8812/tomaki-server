package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

final class CacheDirectoryMerge {
    private CacheDirectoryMerge() {}
    static final class CapacityDeferred extends IOException {
        CapacityDeferred(Path source,long bytes){super("Chunk-cache recovery deferred by capacity for "+source+" bytes="+bytes);}
    }
    static MergeStats tryRecoverValidated(Path source,Path target,Predicate<Path> sourceVerified,
        Predicate<Path> targetVerified,CacheDiskBudget budget)throws IOException{
        try{return copyValidatedPreservingSource(source,target,sourceVerified,targetVerified,budget);}
        catch(CapacityDeferred deferred){return null;}
    }

    static MergeStats merge(Path source, Path target) throws IOException {
        return transferCacheEntries(source, target, false, null, null, null);
    }

    static MergeStats copyCacheEntriesPreservingSource(Path source, Path target) throws IOException {
        return copyCacheEntriesPreservingSource(source, target, null);
    }

    static MergeStats copyCacheEntriesPreservingSource(
        Path source, Path target, CacheDiskBudget budget
    ) throws IOException {
        return transferCacheEntries(source, target, true, null, null, budget);
    }

    static MergeStats mergeValidated(Path source, Path target, Predicate<Path> verified) throws IOException {
        return transferCacheEntries(source, target, false, verified, verified, null);
    }

    static MergeStats copyValidatedPreservingSource(
        Path source, Path target, Predicate<Path> verified
    ) throws IOException {
        return copyValidatedPreservingSource(source, target, verified, verified, null);
    }

    static MergeStats copyValidatedPreservingSource(
        Path source,
        Path target,
        Predicate<Path> sourceVerified,
        Predicate<Path> targetVerified,
        CacheDiskBudget budget
    ) throws IOException {
        return transferCacheEntries(source, target, true, sourceVerified, targetVerified, budget);
    }

    private static MergeStats transferCacheEntries(
        Path source,
        Path target,
        boolean preserveSource,
        Predicate<Path> sourceVerified,
        Predicate<Path> targetVerified,
        CacheDiskBudget budget
    ) throws IOException {
        Files.createDirectories(target);
        List<Path> files;
        try (Stream<Path> stream = Files.walk(source)) {
            files = stream
                .filter(Files::isRegularFile)
                .sorted(Comparator.comparing(Path::toString))
                .toList();
        }
        if (files.stream().anyMatch(path -> path.getFileName().toString().endsWith(".in-use"))) {
            throw new IOException("Recover interrupted promotion ownership before merging " + source);
        }
        files = files.stream()
            .filter(path -> path.getFileName().toString().endsWith(".nbt.gz"))
            .toList();

        long adopted = 0L;
        long duplicates = 0L;
        long rejected = 0L;
        for (Path path : files) {
            if (sourceVerified != null && !sourceVerified.test(path)) {
                rejected++;
                continue;
            }

            Path destination = target.resolve(source.relativize(path));
            Files.createDirectories(destination.getParent());
            boolean destinationExists = Files.exists(destination);
            if (destinationExists
                && (targetVerified == null || targetVerified.test(destination))) {
                duplicates++;
                continue;
            }

            long sourceBytes = Files.size(path);
            long existingBytes = Files.isRegularFile(destination) ? Files.size(destination) : 0L;
            if (preserveSource) {
                CacheDiskBudget.Lease lease = budget == null ? null : budget.tryAcquire(sourceBytes);
                if (budget != null && lease == null) {
                    throw new CapacityDeferred(path,sourceBytes);
                }
                try {
                    copyAtomically(path, destination);
                    if (targetVerified != null && !targetVerified.test(destination)) {
                        Files.deleteIfExists(destination);
                        throw new IOException(
                            "Copied cache entry failed destination validation: " + destination);
                    }
                    if (lease != null) {
                        lease.published(Math.max(0L, sourceBytes - existingBytes));
                    }
                } finally {
                    if (lease != null) lease.close();
                }
            } else if (destinationExists && sourceVerified != null) {
                moveReplacing(path, destination);
            } else {
                move(path, destination);
            }
            adopted++;
        }

        if (!preserveSource) {
            if (rejected == 0L) deleteTree(source);
            else deleteEmptyDirectories(source);
        }
        return new MergeStats(files.size(), adopted, duplicates, rejected);
    }

    /** Same-filesystem adoption has no second FULL-sized allocation. The caller
     * must durably accept the historical identity before moving any payload. */
    static MergeStats adoptValidatedInPlace(Path source,Path target,Predicate<Path> sourceVerified,
        Predicate<Path> targetVerified)throws IOException{
        Files.createDirectories(target);
        List<Path> files;
        try(var walk=Files.walk(source)){files=walk.filter(Files::isRegularFile).sorted().toList();}
        if(files.stream().anyMatch(path->path.getFileName().toString().endsWith(".in-use")))
            throw new IOException("Recover promotion ownership before adoption: "+source);
        long checked=0,adopted=0,duplicates=0,rejected=0;
        for(Path file:files){
            if(!file.getFileName().toString().endsWith(".nbt.gz"))continue;
            checked++;
            if(!sourceVerified.test(file)){rejected++;continue;}
            Path destination=target.resolve(source.relativize(file));
            Files.createDirectories(destination.getParent());
            if(Files.isRegularFile(destination)&&targetVerified.test(destination)){
                if(Files.mismatch(file,destination)==-1){Files.delete(file);duplicates++;}
                else rejected++; // A different valid FULL remains preserved at its source.
                continue;
            }
            // Atomic rename either preserves the source or installs the complete payload.
            // Never fall back to a full-size copy that can deadlock the active quota.
            Files.move(file,destination,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);
            adopted++;
        }
        deleteEmptyDirectories(source);
        return new MergeStats(checked,adopted,duplicates,rejected);
    }

    static long cleanupCopiedSources(
        Path source,
        Path target,
        Predicate<Path> sourceVerified,
        Predicate<Path> targetVerified
    ) throws IOException {
        if (!Files.isDirectory(source)) return 0L;
        List<Path> files;
        try (Stream<Path> stream = Files.walk(source)) {
            files = stream
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".nbt.gz"))
                .sorted(Comparator.comparing(Path::toString))
                .toList();
        }

        long removed = 0L;
        for (Path path : files) {
            if (sourceVerified != null && !sourceVerified.test(path)) continue;
            Path destination = target.resolve(source.relativize(path));
            if (!Files.isRegularFile(destination)) continue;
            if (targetVerified != null && !targetVerified.test(destination)) continue;
            if (Files.size(path) != Files.size(destination)
                || Files.mismatch(path, destination) != -1L) continue;
            Files.deleteIfExists(path);
            removed++;
        }
        deleteEmptyDirectories(source);
        return removed;
    }

    private static void copyAtomically(Path source, Path target) throws IOException {
        Files.createDirectories(target.getParent());
        Path temporary = Files.createTempFile(
            target.getParent(), "." + target.getFileName() + ".", ".migration");
        try {
            Files.copy(
                source,
                temporary,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
            );
            if (Files.size(source) != Files.size(temporary)
                || Files.mismatch(source, temporary) != -1L) {
                throw new IOException("Incomplete chunk-cache migration copy: " + source);
            }
            moveReplacing(temporary, target);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static void move(Path source, Path target) throws IOException {
        Files.createDirectories(target.getParent());
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source, target);
        }
    }

    private static void moveReplacing(Path source, Path target) throws IOException {
        Files.createDirectories(target.getParent());
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) return;
        try (Stream<Path> stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private static void deleteEmptyDirectories(Path root) throws IOException {
        if (!Files.exists(root)) return;
        try (Stream<Path> stream = Files.walk(root)) {
            for (Path directory : stream.sorted(Comparator.reverseOrder())
                .filter(Files::isDirectory).toList()) {
                try (Stream<Path> contents = Files.list(directory)) {
                    if (contents.findAny().isEmpty()) Files.deleteIfExists(directory);
                }
            }
        }
    }

    record MergeStats(long sourceEntries, long adoptedEntries, long duplicateEntries, long rejectedEntries) {}
}
