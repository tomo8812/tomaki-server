package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

/**
 * Non-destructive generation adoption. Only entries whose serialization contract
 * can be proven are copied into the current generation; every source is retained.
 */
final class CacheGenerationMigration {
    static final String MIGRATION_CONTRACT = "entry-migration-v1";
    private static final Pattern CACHE_FILE = Pattern.compile("c\\.(-?\\d+)\\.(-?\\d+)\\.nbt\\.gz");

    private CacheGenerationMigration() {}

    static Stats copyReusableEntriesPreservingSource(Path source, Path target, String expectedDimension) throws IOException {
        Files.createDirectories(target);
        List<Path> files;
        try (Stream<Path> stream = Files.walk(source)) {
            files = stream
                .filter(Files::isRegularFile)
                .sorted(Comparator.comparing(Path::toString))
                .toList();
        }
        if (files.stream().anyMatch(path -> path.getFileName().toString().endsWith(".in-use"))) {
            throw new IOException("Recover interrupted promotion ownership before adopting " + source);
        }
        files = files.stream().filter(path -> path.getFileName().toString().endsWith(".nbt.gz")).toList();

        long adopted = 0L;
        long duplicates = 0L;
        long promotionOnly = 0L;
        long unproven = 0L;
        long invalid = 0L;
        FingerprintManifest.Manifest sourceManifest =
            FingerprintManifest.readIfValid(source.resolve(FingerprintManifest.FILE_NAME));

        for (Path path : files) {
            CompoundTag envelope;
            try {
                envelope = NbtIo.readCompressed(path, NbtAccounter.create(64L << 20));
            } catch (Throwable unreadable) {
                invalid++;
                continue;
            }

            CacheEnvelope.Reuse reuse = classify(path, envelope, sourceManifest, expectedDimension);
            if (reuse == CacheEnvelope.Reuse.LEGACY_UNPROVEN) {
                unproven++;
                continue;
            }
            if (reuse == CacheEnvelope.Reuse.INVALID) {
                invalid++;
                continue;
            }

            Path destination = target.resolve(source.relativize(path));
            Files.createDirectories(destination.getParent());
            if (Files.exists(destination)) {
                duplicates++;
                continue;
            }

            // Remove any stale receipt before a promotion-only payload can become visible.
            if (reuse != CacheEnvelope.Reuse.GENERATED_COMPLETE) CacheManager.clearGeneratedComplete(destination);
            Files.copy(path, destination, StandardCopyOption.COPY_ATTRIBUTES);
            if (reuse == CacheEnvelope.Reuse.GENERATED_COMPLETE) {
                CacheManager.markGeneratedComplete(destination);
            } else {
                CacheManager.clearGeneratedComplete(destination);
                promotionOnly++;
            }
            adopted++;
        }

        return new Stats(files.size(), adopted, duplicates, promotionOnly, unproven, invalid);
    }

    private static CacheEnvelope.Reuse classify(
        Path path,
        CompoundTag envelope,
        FingerprintManifest.Manifest sourceManifest,
        String expectedDimension
    ) {
        CacheEnvelope.Reuse reuse = CacheEnvelope.reuse(envelope);
        if (reuse == CacheEnvelope.Reuse.INVALID || reuse == CacheEnvelope.Reuse.LEGACY_UNPROVEN) return reuse;
        if (sourceManifest == null) return CacheEnvelope.Reuse.LEGACY_UNPROVEN;
        if (!expectedDimension.equals(envelope.getString("dimension"))) return CacheEnvelope.Reuse.INVALID;
        String storedFingerprint = envelope.getString("fingerprint");
        if (!sourceManifest.fingerprint().equals(storedFingerprint)
            && !sourceManifest.storedFingerprints().contains(storedFingerprint)) {
            return CacheEnvelope.Reuse.INVALID;
        }
        if (!envelope.contains("chunk")
            || NormalChunkStorage.fromTag(envelope.getCompound("chunk")) != NormalChunkStorage.State.FULL) {
            return CacheEnvelope.Reuse.INVALID;
        }

        Matcher matcher = CACHE_FILE.matcher(path.getFileName().toString());
        if (!matcher.matches()) return CacheEnvelope.Reuse.INVALID;
        try {
            int x = Integer.parseInt(matcher.group(1));
            int z = Integer.parseInt(matcher.group(2));
            if (envelope.getInt("x") != x || envelope.getInt("z") != z) return CacheEnvelope.Reuse.INVALID;
        } catch (RuntimeException invalidCoordinate) {
            return CacheEnvelope.Reuse.INVALID;
        }
        return reuse;
    }

    record Stats(
        long sourceEntries,
        long adoptedEntries,
        long duplicateEntries,
        long promotionOnlyEntries,
        long retainedUnprovenEntries,
        long retainedInvalidEntries
    ) {}
}
