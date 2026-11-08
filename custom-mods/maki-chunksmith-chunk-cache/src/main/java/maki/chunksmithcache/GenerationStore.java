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
    private static final Set<String> GENERATION_COMPATIBILITY_EXCLUSIONS = Set.of("policy:compatibility", "cache:envelope-schema");

    private GenerationStore() {}

    static boolean reconcile(ServerLevel level, Fingerprint.Snapshot snapshot, String trigger) {
        Path root = dimensionRoot(level);
        try {
            RecoveryClaims.initialize(root);
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
            retainIncompatible(root, snapshot, trigger);
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
            inspectRetainedData(root, snapshot.policy());
            return true;
        } catch (Throwable error) {
            LOG.warn("Could not reconcile chunk-cache generations for {} trigger={}", CacheManager.dimensionKey(level), trigger, error);
            return false;
        }
    }

    static void prune(ServerLevel level, FingerprintPolicy policy) {
        try {
            inspectRetainedData(dimensionRoot(level), policy);
        } catch (Throwable error) {
            LOG.warn("Could not inspect retained chunk-cache data for {}", CacheManager.dimensionKey(level), error);
        }
    }

    static boolean acceptsStoredFingerprint(Path generationDirectory, String expectedFingerprint, String storedFingerprint) {
        return StoredFingerprintAcceptance.accepts(generationDirectory, expectedFingerprint, storedFingerprint);
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
            RecoveryClaims.initialize(dimensionRoot(level));
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
            RecoveryClaims.initialize(dimensionRoot(level));
            Path source = quarantineTarget(level, id);
            if (!Files.isDirectory(source)) return "quarantine not found: " + id;
            FingerprintManifest.Manifest manifest = FingerprintManifest.readIfValid(source.resolve(FingerprintManifest.FILE_NAME));
            if (manifest == null) return "restore refused: quarantine has no valid compatibility manifest";
            FingerprintManifest.Manifest current = snapshot.manifest(Set.of(snapshot.fingerprint()));
            boolean exact = manifest.fingerprint().equals(snapshot.fingerprint());
            boolean semantic = !exact && FingerprintManifest.sameCompatibilityInputsExcept(
                manifest, current, GENERATION_COMPATIBILITY_EXCLUSIONS, source
            );
            if (!exact && !semantic) {
                List<String> changed = FingerprintManifest.changedCompatibilityInputs(
                    manifest, current, GENERATION_COMPATIBILITY_EXCLUSIONS, 12, source
                );
                return "restore refused: quarantined inputs are not proven generation-equivalent; current="
                    + snapshot.fingerprint() + " quarantined=" + manifest.fingerprint() + " changed=" + changed;
            }
            Path active = dimensionRoot(level).resolve(snapshot.fingerprint());
            FingerprintManifest.Manifest activeManifest = FingerprintManifest.readIfValid(active.resolve(FingerprintManifest.FILE_NAME));
            List<String> proofDetails = exact ? List.of() : FingerprintManifest.compatibilityProofs(
                manifest, current, GENERATION_COMPATIBILITY_EXCLUSIONS, 12, source
            );
            CacheGenerationMigration.Stats migration = CacheGenerationMigration.copyReusableEntriesPreservingSource(source, active);
            Set<String> accepted = new LinkedHashSet<>();
            accepted.add(snapshot.fingerprint());
            if (migration.adoptedEntries() > 0L) {
                accepted.add(manifest.fingerprint());
                accepted.addAll(manifest.storedFingerprints());
            }
            if (activeManifest != null) accepted.addAll(activeManifest.storedFingerprints());
            FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME), snapshot.manifest(accepted));
            long activeEntries = stats(active).entries();
            String proof = exact ? "exact generation fingerprint" : "audited generation compatibility";
            writeRecoveryInfo(source, manifest.fingerprint(), snapshot.fingerprint(), proof, proofDetails, migration, activeEntries, "manual-restore");
            LOG.info(
                "Re-adopted retained chunk-cache generation {} for {}; proof={}; sourceEntries={} adoptedEntries={} duplicateEntries={} promotionOnly={} retainedUnproven={} retainedInvalid={} activeEntriesAfter={} sourcePreserved=true",
                id, CacheManager.dimensionKey(level), proof, migration.sourceEntries(), migration.adoptedEntries(),
                migration.duplicateEntries(), migration.promotionOnlyEntries(), migration.retainedUnprovenEntries(),
                migration.retainedInvalidEntries(), activeEntries
            );
            return "re-adopted " + id + " to active fingerprint " + snapshot.fingerprint() + " after " + proof
                + " (source=" + migration.sourceEntries() + ", adopted=" + migration.adoptedEntries()
                + ", duplicates=" + migration.duplicateEntries() + ", promotionOnly=" + migration.promotionOnlyEntries()
                + ", retainedUnproven=" + migration.retainedUnprovenEntries()
                + ", retainedInvalid=" + migration.retainedInvalidEntries() + ", active=" + activeEntries
                + ", sourcePreserved=true)";
        } catch (Throwable error) {
            LOG.warn("Could not re-adopt retained chunk-cache generation {}", id, error);
            return "restore failed: " + error.getClass().getSimpleName();
        }
    }

    private static void migrateExactLegacy(Path root, Fingerprint.Snapshot snapshot, String trigger) throws IOException {
        if (snapshot.legacyFingerprint().equals(snapshot.fingerprint())) return;
        Path legacy = root.resolve(snapshot.legacyFingerprint());
        if (!Files.isDirectory(legacy)) return;
        if (FingerprintManifest.readIfValid(legacy.resolve(FingerprintManifest.FILE_NAME)) != null) return;
        Path active = root.resolve(snapshot.fingerprint());
        FingerprintManifest.Manifest existing = FingerprintManifest.readIfValid(active.resolve(FingerprintManifest.FILE_NAME));
        if (alreadyRecovered(legacy, snapshot.fingerprint(), snapshot.legacyFingerprint(), existing)) return;

        Stats before = stats(legacy);
        CacheGenerationMigration.Stats migration = CacheGenerationMigration.copyReusableEntriesPreservingSource(legacy, active);
        Set<String> accepted = new LinkedHashSet<>();
        accepted.add(snapshot.fingerprint());
        if (migration.adoptedEntries() > 0L) accepted.add(snapshot.legacyFingerprint());
        if (existing != null) accepted.addAll(existing.storedFingerprints());
        FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME), snapshot.manifest(accepted));
        long activeEntries = stats(active).entries();
        writeRecoveryInfo(legacy, snapshot.legacyFingerprint(), snapshot.fingerprint(), "exact legacy generation inputs",
            List.of(), migration, activeEntries, trigger);
        LOG.info(
            "Inspected exact-legacy chunk-cache generation {} for {}; sourceEntries={} adoptedEntries={} duplicateEntries={} promotionOnly={} retainedUnproven={} retainedInvalid={} bytes={} sourcePreserved=true trigger={}",
            snapshot.legacyFingerprint(), root.getFileName(), migration.sourceEntries(), migration.adoptedEntries(),
            migration.duplicateEntries(), migration.promotionOnlyEntries(), migration.retainedUnprovenEntries(),
            migration.retainedInvalidEntries(), before.bytes(), trigger
        );
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
        Path active = root.resolve(snapshot.fingerprint());
        for (Path candidate : candidates) {
            FingerprintManifest.Manifest old = FingerprintManifest.readIfValid(candidate.resolve(FingerprintManifest.FILE_NAME));
            if (old == null || !FingerprintManifest.sameCompatibilityInputsExcept(
                old, current, GENERATION_COMPATIBILITY_EXCLUSIONS, candidate
            )) continue;

            String oldFingerprint = candidate.getFileName().toString();
            FingerprintManifest.Manifest existing = FingerprintManifest.readIfValid(active.resolve(FingerprintManifest.FILE_NAME));
            if (alreadyRecovered(candidate, snapshot.fingerprint(), oldFingerprint, existing)) continue;
            Stats before = stats(candidate);
            List<String> proofDetails = FingerprintManifest.compatibilityProofs(
                old, current, GENERATION_COMPATIBILITY_EXCLUSIONS, 12, candidate
            );
            CacheGenerationMigration.Stats migration = CacheGenerationMigration.copyReusableEntriesPreservingSource(candidate, active);

            Set<String> accepted = new LinkedHashSet<>();
            accepted.add(snapshot.fingerprint());
            if (migration.adoptedEntries() > 0L) {
                accepted.add(oldFingerprint);
                accepted.addAll(old.storedFingerprints());
            }
            if (existing != null) accepted.addAll(existing.storedFingerprints());
            FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME), snapshot.manifest(accepted));
            long activeEntries = stats(active).entries();
            writeRecoveryInfo(candidate, oldFingerprint, snapshot.fingerprint(), "audited generation compatibility",
                proofDetails, migration, activeEntries, trigger);
            LOG.info(
                "Re-adopted generation-equivalent chunk-cache {} as {} for {}; sourceEntries={} adoptedEntries={} duplicateEntries={} promotionOnly={} retainedUnproven={} retainedInvalid={} activeEntriesAfter={} bytes={} proofDetails={} sourcePreserved=true trigger={}",
                oldFingerprint, snapshot.fingerprint(), root.getFileName(), migration.sourceEntries(),
                migration.adoptedEntries(), migration.duplicateEntries(), migration.promotionOnlyEntries(),
                migration.retainedUnprovenEntries(), migration.retainedInvalidEntries(), activeEntries,
                before.bytes(), proofDetails, trigger
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
                if (manifest != null) {
                    candidates.add(new QuarantineCandidate(candidate, manifest));
                } else {
                    Stats retained = stats(candidate);
                    LOG.warn(
                        "Retaining quarantined chunk-cache {} for {}; re-adoption refused because no valid generation manifest is available; sourceEntries={} adoptedEntries=0 duplicateEntries=0 retainedUnprovenEntries={} sourcePreserved=true trigger={}",
                        candidate.getFileName(), root.getFileName(), retained.entries(), retained.entries(), trigger
                    );
                }
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
                old, current, GENERATION_COMPATIBILITY_EXCLUSIONS, candidate
            );
            if (!exact && !semantic) {
                List<String> changed = FingerprintManifest.changedCompatibilityInputs(
                    old, current, GENERATION_COMPATIBILITY_EXCLUSIONS, 12, candidate
                );
                LOG.info(
                    "Kept retained chunk-cache generation {} for {}; re-adoption refused; changedInputs={} sourcePreserved=true trigger={}",
                    old.fingerprint(), root.getFileName(), changed, trigger
                );
                continue;
            }

            Stats before = stats(candidate);
            List<String> proofDetails = exact ? List.of() : FingerprintManifest.compatibilityProofs(
                old, current, GENERATION_COMPATIBILITY_EXCLUSIONS, 12, candidate
            );
            CacheGenerationMigration.Stats migration = CacheGenerationMigration.copyReusableEntriesPreservingSource(candidate, active);

            Set<String> accepted = new LinkedHashSet<>();
            accepted.add(snapshot.fingerprint());
            if (migration.adoptedEntries() > 0L) {
                accepted.add(old.fingerprint());
                accepted.addAll(old.storedFingerprints());
            }
            if (existing != null) accepted.addAll(existing.storedFingerprints());
            FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME), snapshot.manifest(accepted));

            long activeEntries = stats(active).entries();
            String proof = exact ? "exact generation fingerprint" : "audited generation compatibility";
            writeRecoveryInfo(candidate, old.fingerprint(), snapshot.fingerprint(), proof, proofDetails, migration, activeEntries, trigger);
            LOG.info(
                "Re-adopted retained chunk-cache generation {} as {} for {}; proof={}; sourceEntries={} adoptedEntries={} duplicateEntries={} promotionOnly={} retainedUnproven={} retainedInvalid={} activeEntriesAfter={} bytes={} proofDetails={} sourcePreserved=true trigger={}",
                old.fingerprint(), snapshot.fingerprint(), root.getFileName(), proof, migration.sourceEntries(),
                migration.adoptedEntries(), migration.duplicateEntries(), migration.promotionOnlyEntries(),
                migration.retainedUnprovenEntries(), migration.retainedInvalidEntries(), activeEntries,
                before.bytes(), proofDetails, trigger
            );
        }
    }

    private static void retainIncompatible(Path root, Fingerprint.Snapshot snapshot, String trigger) throws IOException {
        List<Path> generations;
        try (Stream<Path> stream = Files.list(root)) {
            generations = stream.filter(Files::isDirectory)
                .filter(path -> path.getFileName().toString().matches("[0-9a-f]{24}"))
                .filter(path -> !path.getFileName().toString().equals(snapshot.fingerprint()))
                .toList();
        }
        FingerprintManifest.Manifest current = snapshot.manifest(Set.of(snapshot.fingerprint()));
        for (Path generation : generations) {
            FingerprintManifest.Manifest old = FingerprintManifest.readIfValid(generation.resolve(FingerprintManifest.FILE_NAME));
            if (old != null && FingerprintManifest.sameCompatibilityInputsExcept(
                old, current, GENERATION_COMPATIBILITY_EXCLUSIONS, generation
            )) continue;
            Stats generationStats = stats(generation);
            List<String> changed = old == null
                ? List.of("unknown: no compatibility manifest")
                : FingerprintManifest.changedCompatibilityInputs(
                    old, current, GENERATION_COMPATIBILITY_EXCLUSIONS, 12, generation
                );
            LOG.warn(
                "Retaining inactive chunk-cache generation {} for {}; entries={} bytes={} changedInputs={} automatic generation-wide quarantine/deletion disabled; trigger={}",
                generation.getFileName(), root.getFileName(), generationStats.entries(), generationStats.bytes(), changed, trigger
            );
        }
    }

    static void inspectRetainedData(Path root, FingerprintPolicy policy) throws IOException {
        RecoveryClaims.initialize(root);
        Path quarantine = root.resolve(QUARANTINE_DIR);
        if (!Files.isDirectory(quarantine)) return;
        List<QuarantineEntry> entries = quarantineEntries(quarantine);
        long bytes = entries.stream().mapToLong(value -> value.stats().bytes()).sum();
        if (entries.size() > policy.warnGenerations() || bytes > policy.warnBytes()) {
            LOG.warn(
                "Retained chunk-cache quarantine exceeds warning threshold: generations={} bytes={} warnGenerations={} warnBytes={}; no automatic deletion will occur. New cache writes remain bounded by active capacity and the normal-world free-space reserve; discard requires an explicit administrator action.",
                entries.size(), bytes, policy.warnGenerations(), policy.warnBytes()
            );
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

    private static void writeRecoveryInfo(
        Path source,
        String sourceFingerprint,
        String targetFingerprint,
        String proof,
        List<String> proofDetails,
        CacheGenerationMigration.Stats migration,
        long activeEntries,
        String trigger
    ) throws IOException {
        Files.writeString(
            source.resolve(RECOVERY_INFO),
            "sourceFingerprint=" + sourceFingerprint + "\n"
                + "targetFingerprint=" + targetFingerprint + "\n"
                + "migrationContract=" + CacheGenerationMigration.MIGRATION_CONTRACT + "\n"
                + "proof=" + proof + "\n"
                + "proofDetails=" + proofDetails + "\n"
                + "sourceEntries=" + migration.sourceEntries() + "\n"
                + "adoptedEntries=" + migration.adoptedEntries() + "\n"
                + "duplicateEntries=" + migration.duplicateEntries() + "\n"
                + "promotionOnlyEntries=" + migration.promotionOnlyEntries() + "\n"
                + "retainedUnprovenEntries=" + migration.retainedUnprovenEntries() + "\n"
                + "retainedInvalidEntries=" + migration.retainedInvalidEntries() + "\n"
                + "activeEntriesAfter=" + activeEntries + "\n"
                + "sourcePreserved=true\n"
                + "trigger=" + trigger + "\n"
        );
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

    private static boolean alreadyRecovered(
        Path source,
        String targetFingerprint,
        String sourceFingerprint,
        FingerprintManifest.Manifest activeManifest
    ) {
        if (activeManifest == null || !targetFingerprint.equals(activeManifest.fingerprint())) return false;
        Path info = source.resolve(RECOVERY_INFO);
        if (!Files.isRegularFile(info)) return false;
        try {
            String text = Files.readString(info);
            boolean currentContract =
                text.contains("migrationContract=" + CacheGenerationMigration.MIGRATION_CONTRACT + "\n");
            boolean matchingSource = text.contains("sourceFingerprint=" + sourceFingerprint + "\n");
            boolean matchingTarget = text.contains("targetFingerprint=" + targetFingerprint + "\n");
            boolean preserved = text.contains("sourcePreserved=true\n");
            boolean copiedNothing = text.contains("adoptedEntries=0\n");
            boolean copiedSourceAccepted = activeManifest.storedFingerprints().contains(sourceFingerprint);
            return currentContract && matchingSource && matchingTarget && preserved
                && (copiedNothing || copiedSourceAccepted);
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

