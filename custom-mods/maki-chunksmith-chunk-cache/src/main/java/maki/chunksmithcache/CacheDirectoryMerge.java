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

    static MergeStats merge(Path source, Path target) throws IOException {
        return transferCacheEntries(source, target, false, null);
    }

    static MergeStats copyCacheEntriesPreservingSource(Path source, Path target) throws IOException {
        return transferCacheEntries(source, target, true, null);
    }

    static MergeStats mergeValidated(Path source, Path target, Predicate<Path> verified) throws IOException {
        return transferCacheEntries(source, target, false, verified);
    }

    static MergeStats copyValidatedPreservingSource(Path source, Path target, Predicate<Path> verified) throws IOException {
        return transferCacheEntries(source, target, true, verified);
    }

    private static MergeStats transferCacheEntries(Path source, Path target,
                                                     boolean preserveSource, Predicate<Path> verified) throws IOException {
        Files.createDirectories(target);
        List<Path> files;
        try (Stream<Path> stream = Files.walk(source)) {
            files = stream
                .filter(Files::isRegularFile)
                .sorted(Comparator.comparing(Path::toString))
                .toList();
        }
        if(files.stream().anyMatch(path -> path.getFileName().toString().endsWith(".in-use")))
            throw new IOException("Recover interrupted promotion ownership before merging "+source);
        files=files.stream().filter(path -> path.getFileName().toString().endsWith(".nbt.gz")).toList();

        long adopted = 0L;
        long duplicates = 0L;
        long rejected = 0L;
        for (Path path : files) {
            if (verified != null && !verified.test(path)) {
                rejected++;
                continue;
            }
            Path destination = target.resolve(source.relativize(path));
            Files.createDirectories(destination.getParent());
            if (Files.exists(destination)) {
                duplicates++;
                continue;
            }
            if (preserveSource) {
                Files.copy(path, destination, StandardCopyOption.COPY_ATTRIBUTES);
            } else {
                move(path, destination);
            }
            adopted++;
        }

        if (!preserveSource) {
            // A failed format check must never be removed alongside approved
            // files. The caller quarantines the remaining old generation.
            if (rejected == 0L) deleteTree(source);
            else deleteEmptyDirectories(source);
        }
        return new MergeStats(files.size(), adopted, duplicates, rejected);
    }

    private static void move(Path source, Path target) throws IOException {
        Files.createDirectories(target.getParent());
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source, target);
        }
    }

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) return;
        try (Stream<Path> stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
        }
    }

    private static void deleteEmptyDirectories(Path root) throws IOException {
        try (Stream<Path> stream = Files.walk(root)) {
            for (Path directory : stream.sorted(Comparator.reverseOrder()).filter(Files::isDirectory).toList()) {
                try (Stream<Path> contents = Files.list(directory)) {
                    if (contents.findAny().isEmpty()) Files.deleteIfExists(directory);
                }
            }
        }
    }

    record MergeStats(long sourceEntries, long adoptedEntries, long duplicateEntries, long rejectedEntries) {}
}
