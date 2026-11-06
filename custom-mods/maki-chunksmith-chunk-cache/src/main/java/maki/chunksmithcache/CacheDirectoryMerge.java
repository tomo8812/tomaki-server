package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

final class CacheDirectoryMerge {
    private CacheDirectoryMerge() {}

    static MergeStats merge(Path source, Path target) throws IOException {
        return transferCacheEntries(source, target, false);
    }

    static MergeStats copyCacheEntriesPreservingSource(Path source, Path target) throws IOException {
        return transferCacheEntries(source, target, true);
    }

    private static MergeStats transferCacheEntries(Path source, Path target, boolean preserveSource) throws IOException {
        Files.createDirectories(target);
        List<Path> files;
        try (Stream<Path> stream = Files.walk(source)) {
            files = stream
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".nbt.gz"))
                .sorted(Comparator.comparing(Path::toString))
                .toList();
        }

        long adopted = 0L;
        long duplicates = 0L;
        for (Path path : files) {
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
            deleteTree(source);
        }
        return new MergeStats(files.size(), adopted, duplicates);
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

    record MergeStats(long sourceEntries, long adoptedEntries, long duplicateEntries) {}
}
