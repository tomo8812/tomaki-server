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

    static void merge(Path source, Path target) throws IOException {
        Files.createDirectories(target);
        List<Path> paths;
        try (Stream<Path> stream = Files.walk(source)) {
            paths = stream.sorted(Comparator.comparingInt(Path::getNameCount)).toList();
        }
        for (Path path : paths) {
            if (path.equals(source)) continue;
            Path relative = source.relativize(path);
            Path destination = target.resolve(relative);
            if (Files.isDirectory(path)) {
                Files.createDirectories(destination);
            } else if (Files.isRegularFile(path)) {
                Files.createDirectories(destination.getParent());
                if (Files.exists(destination)) {
                    Files.delete(path);
                } else {
                    move(path, destination);
                }
            }
        }
        try (Stream<Path> stream = Files.walk(source)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
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
}
