package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class GenerationStore {
    private static final Logger LOG = LoggerFactory.getLogger("MakiChunkCache");
    private static final String QUARANTINE_DIR = ".quarantine";
    private static final String QUARANTINE_INFO = "quarantine-info.txt";
    private static final String RECOVERY_INFO = "recovery-info.txt";

    private GenerationStore() {}

    static void reconcile(ServerLevel level, Fingerprint.Snapshot snapshot, String trigger) {
        Path root = dimensionRoot(level);
        try {
            Files.createDirectories(root);
            FingerprintManifest.Manifest currentState = snapshot.manifest(Set.of(snapshot.fingerprint()));
            FingerprintManifest.Manifest previousState = FingerprintManifest.readIfValid(root.resolve(FingerprintManifest.CURRENT_FILE_NAME));
            if (previousState != null && previousState.fingerprint().equals(snapshot.fingerprint())) {
                List<String> ignoredChanges = FingerprintManifest.changedInputs(previousState, currentState, FingerprintManifest.Impact.IGNORED, 12);
                if (!ignoredChanges.isEmpty()) {
                    LOG.info("Keeping chunk-cache generation {} for {}; compatibility fingerprint unchanged; ignored changes={} trigger={}",
                        snapshot.fingerprint(), CacheManager.dimensionKey(level), ignoredChanges, trigger);
                }
            }

            migrateExactLegacy(root, snapshot, trigger);
            migratePolicyEquivalent(root, snapshot, trigger);
            migrateEquivalentQuarantines(root, snapshot, trigger);
            quarantineIncompatible(root, snapshot, trigger);
            Path active = root.resolve(snapshot.fingerprint());
            Files.createDirectories(active);
            FingerprintManifest.Manifest existing = FingerprintManifest.readIfValid(active.resolve(FingerprintManifest.FILE_NAME));
            Set<String> accepted = new LinkedHashSet<>();
            accepted.add(snapshot.fingerprint());
            if (existing != null && existing.fingerprint().equals(snapshot.fingerprint())) accepted.addAll(existing.storedFingerprints());
            FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME), snapshot.manifest(accepted));
            CompatibilityInput.persistHistoricalEvidence(
                active,
                level.getServer().getServerDirectory(),
                snapshot.entries()
            );
            FingerprintManifest.writeAtomic(root.resolve(FingerprintManifest.CURRENT_FILE_NAME), currentState);
            pruneQuarantine(root, snapshot.policy());
        } catch (Throwable error) {
            LOG.warn("Could not reconcile chunk-cache generations for {} trigger={}", CacheManager.dimensionKey(level), trigger, error);
        }
    }

    static void prune(ServerLevel level, FingerprintPolicy policy) {
        try {
            pruneQuarantine(dimensionRoot(level), policy);
        } catch (Throwable error) {
            LOG.warn("Could not prune chunk-cache quarantine for {}", CacheManager.dimensionKey(level), error);
        }
    }

    static boolean acceptsStoredFingerprint(Path generationDirectory, String expectedFingerprint, String storedFingerprint) {
        if (expectedFingerprint.equals(storedFingerprint)) return true;
        FingerprintManifest.Manifest manifest = FingerprintManifest.readIfValid(generationDirectory.resolve(FingerprintManifest.FILE_NAME));
        return manifest != null
            && expectedFingerprint.equals(manifest.fingerprint())
            && manifest.storedFingerprints().contains(storedFingerprint);
    }

    static Path quarantineCorrupt(Path cacheFile) {
        try {
            Path generation = generationDirectory(cacheFile);
            Path corrupt = generation.resolve(".corrupt");
            Files.createDirectories(corrupt);
            String name = cacheFile.getFileName() + "." + System.currentTimeMillis() + ".corrupt";
            Path target = unique(corrupt.resolve(name));
            move(cacheFile, target);
            return target;
        } catch (Throwable ignored) {
            return null;
        }
    }

    static List<QuarantineInfo> listQuarantines(ServerLevel level) throws IOException {
        Path quarantine = dimensionRoot(level).resolve(QUARANTINE_DIR);
        if (!Files.isDirectory(quarantine)) return List.of();
        List<QuarantineInfo> result = new ArrayList<>();
        try (Stream<Path> stream = Files.list(quarantine)) {
            for (Path directory : stream.filter(Files::isDirectory).toList()) {
                Stats stats = stats(directory);
                result.add(new QuarantineInfo(directory.getFileName().toString(), stats.entries(), stats.bytes(), Files.getLastModifiedTime(directory).toMillis()));
            }
        }
        result.sort(Comparator.comparingLong(QuarantineInfo::modifiedAtMillis).reversed());
        return List.copyOf(result);
    }

    static String discardQuarantine(ServerLevel level, String id) {
        try {
            Path target = quarantineTarget(level, id);
            if (!Files.isDirectory(target)) return "quarantine not found: " + id;
            Stats stats = stats(target);
            deleteTree(target);
            LOG.info("Discarded quarantined chunk-cache generation {} for {} by administrator; entries={} bytes={}",
                id, CacheManager.dimensionKey(level), stats.entries(), stats.bytes());
            return "discarded " + id + " (" + stats.entries() + " cache entries, " + stats.bytes() + " bytes)";
        } catch (Throwable error) {
            LOG.warn("Could not discard quarantined chunk-cache generation {}", id, error);
            return "discard failed: " + error.getClass().getSimpleName();
        }
    }

    static String restoreQuarantine(ServerLevel level, String id, Fingerprint.Snapshot snapshot) {
        try {
            Path source = quarantineTarget(level, id);
            if (!Files.isDirectory(source)) return "quarantine not found: " + id;
            FingerprintManifest.Manifest manifest = FingerprintManifest.readIfValid(source.resolve(FingerprintManifest.FILE_NAME));
            if (manifest == null) return "restore refused: quarantine has no valid compatibility manifest";
            FingerprintManifest.Manifest current = snapshot.manifest(Set.of(snapshot.fingerprint()));
            boolean exact = manifest.fingerprint().equals(snapshot.fingerprint());
            boolean semantic = !exact && FingerprintManifest.sameCompatibilityInputsExcept(
                manifest,
                current,
                Set.of("policy:compatibility"),
                source
            );
            if (!exact && !semantic) {
                List<String> changed = FingerprintManifest.changedCompatibilityInputs(
                    manifest, current, Set.of("policy:compatibility"), 12, source
                );
                return "restore refused: quarantined inputs are not proven compatibility-equivalent; current="
                    + snapshot.fingerprint() + " quarantined=" + manifest.fingerprint() + " changed=" + changed;
            }
            Path active = dimensionRoot(level).resolve(snapshot.fingerprint());
            FingerprintManifest.Manifest activeManifest = FingerprintManifest.readIfValid(active.resolve(FingerprintManifest.FILE_NAME));
            List<String> proofDetails = exact ? List.of() : FingerprintManifest.compatibilityProofs(
                manifest, current, Set.of("policy:compatibility"), 12, source
            );
            CacheDirectoryMerge.MergeStats merge = mergeDirectory(source, active);
            Set<String> accepted = new LinkedHashSet<>(manifest.storedFingerprints());
            accepted.add(manifest.fingerprint());
            accepted.add(snapshot.fingerprint());
            if (activeManifest != null) accepted.addAll(activeManifest.storedFingerprints());
            FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME), snapshot.manifest(accepted));
            String proof = exact ? "exact fingerprint match" : "audited semantic compatibility";
            long activeEntries = stats(active).entries();
            LOG.info(
                "Restored quarantined chunk-cache generation {} for {} after {}; sourceEntries={} adoptedEntries={} duplicateEntries={} activeEntriesAfter={} proofDetails={}",
                id, CacheManager.dimensionKey(level), proof, merge.sourceEntries(), merge.adoptedEntries(),
                merge.duplicateEntries(), activeEntries, proofDetails
            );
            return "restored " + id + " to active fingerprint " + snapshot.fingerprint() + " after " + proof
                + " (source=" + merge.sourceEntries() + ", adopted=" + merge.adoptedEntries()
                + ", duplicates=" + merge.duplicateEntries() + ", active=" + activeEntries + ")";
        } catch (Throwable error) {
            LOG.warn("Could not restore quarantined chunk-cache generation {}", id, error);
            return "restore failed: " + error.getClass().getSimpleName();
        }
    }

    private static void migrateExactLegacy(Path root, Fingerprint.Snapshot snapshot, String trigger) throws IOException {
        if (snapshot.legacyFingerprint().equals(snapshot.fingerprint())) return;
        Path legacy = root.resolve(snapshot.legacyFingerprint());
        if (!Files.isDirectory(legacy)) return;
        if (FingerprintManifest.readIfValid(legacy.resolve(FingerprintManifest.FILE_NAME)) != null) return;
        Stats before = stats(legacy);
        Path active = root.resolve(snapshot.fingerprint());
        mergeDirectory(legacy, active);
        FingerprintManifest.Manifest existing = FingerprintManifest.readIfValid(active.resolve(FingerprintManifest.FILE_NAME));
        Set<String> accepted = new LinkedHashSet<>();
        accepted.add(snapshot.fingerprint());
        accepted.add(snapshot.legacyFingerprint());
        if (existing != null) accepted.addAll(existing.storedFingerprints());
        FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME), snapshot.manifest(accepted));
        LOG.info("Adopted legacy chunk-cache generation {} as {} for {}; exact legacy fingerprint matches current inputs; entries={} bytes={} trigger={}",
            snapshot.legacyFingerprint(), snapshot.fingerprint(), root.getFileName(), before.entries(), before.bytes(), trigger);
    }

    private static void migratePolicyEquivalent(Path root, Fingerprint.Snapshot snapshot, String trigger) throws IOException {
        FingerprintManifest.Manifest current = snapshot.manifest(Set.of(snapshot.fingerprint()));
        List<Path> candidates;
        try (Stream<Path> stream = Files.list(root)) {
            candidates = stream.filter(Files::isDirectory)
                .filter(path -> path.getFileName().toString().matches("[0-9a-f]{24}"))
                .filter(path -> !path.getFileName().toString().equals(snapshot.fingerprint()))
                .toList();
        }
        for (Path candidate : candidates) {
            FingerprintManifest.Manifest old = FingerprintManifest.readIfValid(candidate.resolve(FingerprintManifest.FILE_NAME));
            if (old == null || !FingerprintManifest.sameCompatibilityInputsExcept(
                old, current, Set.of("policy:compatibility"), candidate
            )) continue;

            String oldFingerprint = candidate.getFileName().toString();
            Stats before = stats(candidate);
            Path active = root.resolve(snapshot.fingerprint());
            FingerprintManifest.Manifest existing = FingerprintManifest.readIfValid(active.resolve(FingerprintManifest.FILE_NAME));
            List<String> proofDetails = FingerprintManifest.compatibilityProofs(
                old, current, Set.of("policy:compatibility"), 12, candidate
            );
            CacheDirectoryMerge.MergeStats merge = mergeDirectory(candidate, active);

            Set<String> accepted = new LinkedHashSet<>();
            accepted.add(snapshot.fingerprint());
            accepted.add(oldFingerprint);
            accepted.addAll(old.storedFingerprints());
            if (existing != null) accepted.addAll(existing.storedFingerprints());
            FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME), snapshot.manifest(accepted));
            long activeEntries = stats(active).entries();
            LOG.info(
                "Adopted compatibility-equivalent chunk-cache generation {} as {} for {}; sourceEntries={} adoptedEntries={} duplicateEntries={} activeEntriesAfter={} bytes={} proofDetails={} trigger={}",
                oldFingerprint, snapshot.fingerprint(), root.getFileName(), merge.sourceEntries(),
                merge.adoptedEntries(), merge.duplicateEntries(), activeEntries, before.bytes(), proofDetails, trigger
            );
        }
    }

    private static void migrateEquivalentQuarantines(Path root, Fingerprint.Snapshot snapshot, String trigger) throws IOException {
        Path quarantineRoot = root.resolve(QUARANTINE_DIR);
        if (!Files.isDirectory(quarantineRoot)) return;

        FingerprintManifest.Manifest current = snapshot.manifest(Set.of(snapshot.fingerprint()));
        List<QuarantineCandidate> candidates = new ArrayList<>();
        try (Stream<Path> stream = Files.list(quarantineRoot)) {
            for (Path candidate : stream.filter(Files::isDirectory).toList()) {
                FingerprintManifest.Manifest manifest = FingerprintManifest.readIfValid(candidate.resolve(FingerprintManifest.FILE_NAME));
                if (manifest != null) candidates.add(new QuarantineCandidate(candidate, manifest));
            }
        }
        candidates.sort(Comparator.comparingLong((QuarantineCandidate value) -> value.manifest().createdAtMillis()).reversed());

        Path active = root.resolve(snapshot.fingerprint());
        for (QuarantineCandidate candidateValue : candidates) {
            Path candidate = candidateValue.path();
            FingerprintManifest.Manifest old = candidateValue.manifest();
            FingerprintManifest.Manifest existing = FingerprintManifest.readIfValid(active.resolve(FingerprintManifest.FILE_NAME));
            if (alreadyRecovered(candidate, snapshot.fingerprint(), old.fingerprint(), existing)) continue;

            boolean exact = old.fingerprint().equals(snapshot.fingerprint());
            boolean semantic = !exact && FingerprintManifest.sameCompatibilityInputsExcept(
                old,
                current,
                Set.of("policy:compatibility"),
                candidate
            );
            if (!exact && !semantic) {
                List<String> changed = FingerprintManifest.changedCompatibilityInputs(
                    old, current, Set.of("policy:compatibility"), 12, candidate
                );
                LOG.info(
                    "Kept quarantined chunk-cache generation {} for {}; migration refused; reason=compatibility proof missing or inputs changed; changedInputs={} trigger={}",
                    old.fingerprint(), root.getFileName(), changed, trigger
                );
                continue;
            }

            Stats before = stats(candidate);
            List<String> proofDetails = exact ? List.of() : FingerprintManifest.compatibilityProofs(
                old, current, Set.of("policy:compatibility"), 12, candidate
            );
            CacheDirectoryMerge.MergeStats merge = CacheDirectoryMerge.copyCacheEntriesPreservingSource(candidate, active);

            Set<String> accepted = new LinkedHashSet<>();
            accepted.add(snapshot.fingerprint());
            accepted.add(old.fingerprint());
            accepted.addAll(old.storedFingerprints());
            if (existing != null) accepted.addAll(existing.storedFingerprints());
            FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME), snapshot.manifest(accepted));

            long activeEntries = stats(active).entries();
            String proof = exact ? "exact fingerprint match" : "audited semantic compatibility";
            Files.writeString(
                candidate.resolve(RECOVERY_INFO),
                "sourceFingerprint=" + old.fingerprint() + "\n"
                    + "targetFingerprint=" + snapshot.fingerprint() + "\n"
                    + "proof=" + proof + "\n"
                    + "proofDetails=" + proofDetails + "\n"
                    + "sourceEntries=" + merge.sourceEntries() + "\n"
                    + "adoptedEntries=" + merge.adoptedEntries() + "\n"
                    + "duplicateEntries=" + merge.duplicateEntries() + "\n"
                    + "activeEntriesAfter=" + activeEntries + "\n"
                    + "sourcePreserved=true\n"
                    + "trigger=" + trigger + "\n"
            );
            Files.setLastModifiedTime(candidate, FileTime.fromMillis(System.currentTimeMillis()));
            LOG.info(
                "Re-adopted quarantined chunk-cache generation {} as {} for {}; proof={}; sourceEntries={} adoptedEntries={} duplicateEntries={} activeEntriesAfter={} bytes={} proofDetails={}; quarantine source preserved until retention/capacity pruning; trigger={}",
                old.fingerprint(), snapshot.fingerprint(), root.getFileName(), proof, merge.sourceEntries(),
                merge.adoptedEntries(), merge.duplicateEntries(), activeEntries, before.bytes(), proofDetails, trigger
            );
        }
    }

    private static void quarantineIncompatible(Path root, Fingerprint.Snapshot snapshot, String trigger) throws IOException {
        List<Path> generations;
        try (Stream<Path> stream = Files.list(root)) {
            generations = stream.filter(Files::isDirectory)
                .filter(path -> path.getFileName().toString().matches("[0-9a-f]{24}"))
                .filter(path -> !path.getFileName().toString().equals(snapshot.fingerprint()))
                .toList();
        }
        for (Path generation : generations) {
            String oldFingerprint = generation.getFileName().toString();
            FingerprintManifest.Manifest oldManifest = FingerprintManifest.readIfValid(generation.resolve(FingerprintManifest.FILE_NAME));
            Stats stats = stats(generation);
            String reason;
            List<String> changed;
            if (oldManifest == null) {
                reason = "legacy-or-missing-manifest; compatibility cannot be proven";
                changed = List.of("unknown: no v2 manifest");
            } else {
                FingerprintManifest.Manifest current = snapshot.manifest(Set.of(snapshot.fingerprint()));
                changed = FingerprintManifest.changedCompatibilityInputs(
                    oldManifest, current, Set.of("policy:compatibility"), 12, generation
                );
                reason = changed.isEmpty()
                    ? "fingerprint mismatch with no explainable manifest diff; fail-closed"
                    : "compatibility inputs changed or historical migration proof is missing";
            }
            Path quarantine = root.resolve(QUARANTINE_DIR);
            Files.createDirectories(quarantine);
            Path target = unique(quarantine.resolve(oldFingerprint + "-" + System.currentTimeMillis()));
            move(generation, target);
            Files.setLastModifiedTime(target, FileTime.fromMillis(System.currentTimeMillis()));
            Files.writeString(target.resolve(QUARANTINE_INFO),
                "oldFingerprint=" + oldFingerprint + "\nnewFingerprint=" + snapshot.fingerprint() + "\ntrigger=" + trigger + "\nreason=" + reason + "\nchangedInputs=" + changed + "\nentries=" + stats.entries() + "\nbytes=" + stats.bytes() + "\n");
            LOG.warn("Quarantined incompatible chunk-cache generation {} -> {} for {}; entries={} bytes={} reason={} changedInputs={} trigger={}",
                oldFingerprint, snapshot.fingerprint(), root.getFileName(), stats.entries(), stats.bytes(), reason, changed, trigger);
        }
    }

    private static void pruneQuarantine(Path root, FingerprintPolicy policy) throws IOException {
        Path quarantine = root.resolve(QUARANTINE_DIR);
        if (!Files.isDirectory(quarantine)) return;
        long now = System.currentTimeMillis();
        long minimumAge = Duration.ofHours(policy.minProtectionHours()).toMillis();
        long retention = Duration.ofDays(policy.retentionDays()).toMillis();
        List<QuarantineEntry> entries = quarantineEntries(quarantine);

        for (QuarantineEntry entry : new ArrayList<>(entries)) {
            long age = Math.max(0L, now - entry.modifiedAtMillis());
            if (age >= retention && age >= minimumAge) {
                deleteQuarantine(entry, "retention-days=" + policy.retentionDays());
                entries.remove(entry);
            }
        }

        entries.sort(Comparator.comparingLong(QuarantineEntry::modifiedAtMillis));
        long bytes = entries.stream().mapToLong(value -> value.stats().bytes()).sum();
        while ((entries.size() > policy.maxGenerations() || bytes > policy.maxBytes()) && !entries.isEmpty()) {
            QuarantineEntry candidate = entries.getFirst();
            long age = Math.max(0L, now - candidate.modifiedAtMillis());
            if (age < minimumAge) {
                LOG.warn("Chunk-cache quarantine is above configured capacity but newest protected data will not be discarded yet: generations={} bytes={} maxGenerations={} maxBytes={} minimumProtectionHours={}",
                    entries.size(), bytes, policy.maxGenerations(), policy.maxBytes(), policy.minProtectionHours());
                break;
            }
            String reason = entries.size() > policy.maxGenerations() ? "max-generations=" + policy.maxGenerations() : "max-bytes=" + policy.maxBytes();
            deleteQuarantine(candidate, reason);
            entries.removeFirst();
            bytes -= candidate.stats().bytes();
        }
    }

    private static List<QuarantineEntry> quarantineEntries(Path quarantine) throws IOException {
        List<QuarantineEntry> entries = new ArrayList<>();
        try (Stream<Path> stream = Files.list(quarantine)) {
            for (Path path : stream.filter(Files::isDirectory).toList()) {
                entries.add(new QuarantineEntry(path, stats(path), Files.getLastModifiedTime(path).toMillis()));
            }
        }
        return entries;
    }

    private static void deleteQuarantine(QuarantineEntry entry, String reason) throws IOException {
        deleteTree(entry.path());
        LOG.info("Pruned quarantined chunk-cache generation {}; entries={} bytes={} reason={}",
            entry.path().getFileName(), entry.stats().entries(), entry.stats().bytes(), reason);
    }

    private static Path quarantineTarget(ServerLevel level, String id) throws IOException {
        if (id == null || !id.matches("[0-9a-f]{24}-[0-9]+(?:-[0-9]+)?")) throw new IOException("invalid quarantine id");
        Path root = dimensionRoot(level).resolve(QUARANTINE_DIR).toAbsolutePath().normalize();
        Path target = root.resolve(id).toAbsolutePath().normalize();
        if (!target.startsWith(root)) throw new IOException("invalid quarantine path");
        return target;
    }

    private static Path generationDirectory(Path cacheFile) {
        Path current = cacheFile.toAbsolutePath().normalize();
        while (current != null && !current.getFileName().toString().matches("[0-9a-f]{24}")) current = current.getParent();
        return current == null ? cacheFile.getParent() : current;
    }

    private static Path dimensionRoot(ServerLevel level) {
        return level.getServer().getWorldPath(LevelResource.ROOT)
            .resolve("chunksmith_chunk_cache")
            .resolve(CacheManager.dimensionKey(level));
    }

    private static Stats stats(Path root) throws IOException {
        long entries = 0L;
        long bytes = 0L;
        try (Stream<Path> stream = Files.walk(root)) {
            for (Path path : stream.filter(Files::isRegularFile).toList()) {
                long size = Files.size(path);
                bytes += size;
                if (path.getFileName().toString().endsWith(".nbt.gz")) entries++;
            }
        }
        return new Stats(entries, bytes);
    }

    private static CacheDirectoryMerge.MergeStats mergeDirectory(Path source, Path target) throws IOException {
        return CacheDirectoryMerge.merge(source, target);
    }

    private static boolean alreadyRecovered(
        Path quarantine,
        String targetFingerprint,
        String sourceFingerprint,
        FingerprintManifest.Manifest activeManifest
    ) {
        if (activeManifest == null
            || !targetFingerprint.equals(activeManifest.fingerprint())
            || !activeManifest.storedFingerprints().contains(sourceFingerprint)) {
            return false;
        }
        Path info = quarantine.resolve(RECOVERY_INFO);
        if (!Files.isRegularFile(info)) return false;
        try {
            String text = Files.readString(info);
            return text.contains("sourceFingerprint=" + sourceFingerprint + "\n")
                && text.contains("targetFingerprint=" + targetFingerprint + "\n")
                && text.contains("sourcePreserved=true\n");
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Path unique(Path target) {
        if (!Files.exists(target)) return target;
        int suffix = 1;
        Path candidate;
        do {
            candidate = target.resolveSibling(target.getFileName() + "-" + suffix++);
        } while (Files.exists(candidate));
        return candidate;
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
        try (Stream<Path> stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
        }
    }

    record QuarantineInfo(String id, long entries, long bytes, long modifiedAtMillis) {}
    private record Stats(long entries, long bytes) {}
    private record QuarantineEntry(Path path, Stats stats, long modifiedAtMillis) {}
    private record QuarantineCandidate(Path path, FingerprintManifest.Manifest manifest) {}
}

