package maki.chunksmithcache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * File-format reader for cache ownership metadata.
 *
 * Older manifest-v2.txt files contain snapshots of mods, world settings,
 * seed and datapacks. The legacy entry records are parsed only so old caches
 * can be found; their values are not loaded, compared or monitored.
 *
 * The active manifest stores only the stable cache directory identity and
 * the identities recorded inside previously saved FULL envelopes.
 */
final class FingerprintManifest {
    static final String FILE_NAME = "manifest-v2.txt";
    static final String CURRENT_FILE_NAME = ".current-manifest-v2.txt";
    private static final int FORMAT = 2;

    record Manifest(String fingerprint, long createdAtMillis, Set<String> storedFingerprints) {
        Manifest {
            if (fingerprint == null || !fingerprint.matches("[0-9a-f]{24}")) {
                throw new IllegalArgumentException("invalid cache namespace");
            }
            storedFingerprints = Set.copyOf(storedFingerprints);
        }
    }

    private FingerprintManifest() {}

    static void writeAtomic(Path path, Manifest manifest) throws IOException {
        Files.createDirectories(path.getParent());
        Path temporary = path.resolveSibling(path.getFileName() + ".tmp");
        Files.writeString(temporary, encode(manifest), StandardCharsets.UTF_8);
        try {
            Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
            Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    static Manifest readIfValid(Path path) {
        try {
            return Files.isRegularFile(path) ? decode(Files.readString(path, StandardCharsets.UTF_8)) : null;
        } catch (Throwable invalid) {
            return null;
        }
    }

    static String encode(Manifest manifest) {
        List<String> stored = new ArrayList<>(manifest.storedFingerprints());
        stored.sort(String::compareTo);
        return "format\t" + FORMAT + "\n"
            + "fingerprint\t" + manifest.fingerprint() + "\n"
            + "created\t" + manifest.createdAtMillis() + "\n"
            + "stored\t" + String.join(",", stored) + "\n";
    }

    static Manifest decode(String input) throws IOException {
        String fingerprint = null;
        long created = -1L;
        Set<String> stored = new LinkedHashSet<>();
        boolean hasFormat = false, hasStored = false;
        for (String line : input.split("\\R")) {
            if (line.isBlank()) continue;
            String[] fields = line.split("\\t", -1);
            switch (fields[0]) {
                case "format" -> {
                    if (fields.length != 2 || !fields[1].equals(Integer.toString(FORMAT))
                        || hasFormat) throw new IOException("unsupported or duplicate manifest format");
                    hasFormat = true;
                }
                case "fingerprint" -> {
                    if (fields.length != 2 || fingerprint != null
                        || !fields[1].matches("[0-9a-f]{24}")) throw new IOException("invalid cache identity");
                    fingerprint = fields[1];
                }
                case "created" -> {
                    if (fields.length != 2 || created >= 0) throw new IOException("invalid creation marker");
                    try { created = Long.parseLong(fields[1]); }
                    catch (NumberFormatException invalid) { throw new IOException("invalid creation marker", invalid); }
                }
                case "stored" -> {
                    if (fields.length != 2 || hasStored) throw new IOException("invalid stored identities");
                    hasStored = true;
                    if (!fields[1].isEmpty()) for (String value : fields[1].split(",")) {
                        if (!value.matches("[0-9a-f]{24}")) throw new IOException("invalid saved identity");
                        stored.add(value);
                    }
                }
                case "entry" -> {
                    // Legacy manifest-v2 records are accepted as old-format
                    // metadata, never as active compatibility checks.
                    if (fields.length != 6 || !fields[3].matches("[0-9a-f]{64}")
                        || !fields[4].matches("RELEVANT|IGNORED"))
                        throw new IOException("invalid legacy manifest record");
                    try {
                        Base64.getUrlDecoder().decode(fields[1]);
                        Base64.getUrlDecoder().decode(fields[5]);
                        Long.parseLong(fields[2]);
                    } catch (IllegalArgumentException invalid) {
                        throw new IOException("invalid legacy manifest record", invalid);
                    }
                }
                default -> throw new IOException("unknown cache manifest field");
            }
        }
        if (!hasFormat || fingerprint == null || created < 0) throw new IOException("incomplete cache manifest");
        if (stored.isEmpty()) stored.add(fingerprint);
        return new Manifest(fingerprint, created, stored);
    }
}
