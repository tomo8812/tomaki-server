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
import java.util.function.Predicate;
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

    static boolean reconcile(ServerLevel level, Fingerprint.Snapshot snapshot, String trigger) {
        Path root = dimensionRoot(level);
        try {
            RecoveryClaims.initialize(root);
            Files.createDirectories(root);
            CacheDiskBudget migrationBudget = refreshMigrationBudget(level);
            adoptPriorCacheDirectories(level, root, snapshot, trigger, migrationBudget);
            recoverPreviousQuarantines(level, root, snapshot, trigger, migrationBudget);
            quarantineRejectedRemainders(root, snapshot, trigger);
            Path active = root.resolve(snapshot.fingerprint());
            Files.createDirectories(active);
            FingerprintManifest.Manifest existing = FingerprintManifest.readIfValid(active.resolve(FingerprintManifest.FILE_NAME));
            Set<String> accepted = new LinkedHashSet<>();
            accepted.add(snapshot.fingerprint());
            if (existing != null && existing.fingerprint().equals(snapshot.fingerprint())) accepted.addAll(existing.storedFingerprints());
            FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME), snapshot.manifest(accepted));
            // The old current-manifest snapshot described mod/seed/config
            // changes. No such monitoring data is needed after adoption.
            Files.deleteIfExists(root.resolve(FingerprintManifest.CURRENT_FILE_NAME));
            pruneQuarantine(root, snapshot.policy());
            return true;
        } catch (Throwable error) {
            LOG.warn("Could not reconcile chunk-cache generations for {} trigger={}", CacheManager.dimensionKey(level), trigger, error);
            return false;
        }
    }

    static void prune(ServerLevel level, CacheRetentionPolicy policy) {
        try {
            pruneQuarantine(dimensionRoot(level), policy);
        } catch (Throwable error) {
            LOG.warn("Could not prune chunk-cache quarantine for {}", CacheManager.dimensionKey(level), error);
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
            FingerprintManifest.Manifest old = historicalIdentity(source);
            if (old == null) return "restore refused: invalid historical cache identity";
            CacheDiskBudget budget = refreshMigrationBudget(level);
            Path active = dimensionRoot(level).resolve(snapshot.fingerprint());
            FingerprintManifest.Manifest current = snapshot.manifest(Set.of(snapshot.fingerprint()));
            FingerprintManifest.Manifest existing =
                FingerprintManifest.readIfValid(active.resolve(FingerprintManifest.FILE_NAME));
            CacheDirectoryMerge.MergeStats merge = copyValidFulls(
                level, old, current, source, active, budget);
            Set<String> accepted = acceptedIdentities(snapshot, old, existing);
            FingerprintManifest.writeAtomic(
                active.resolve(FingerprintManifest.FILE_NAME), snapshot.manifest(accepted));

            // Finish cleanup only after the acceptance manifest is durable.
            long cleaned = CacheDirectoryMerge.cleanupCopiedSources(
                source, active, cacheValidator(level, old),
                cacheValidator(level, validationManifest(current, active, old)));
            long remaining = stats(source).entries();
            if (remaining == 0) {
                deleteTree(source);
            } else {
                writeRecoveryReceipt(source, old.fingerprint(), snapshot.fingerprint(),
                    merge, "manual-restore");
            }
            LOG.info("Restored valid FULLs from quarantine {} for {}; adopted={} duplicates={} rejected={} cleaned={} retained={}",
                id, CacheManager.dimensionKey(level), merge.adoptedEntries(),
                merge.duplicateEntries(), merge.rejectedEntries(), cleaned, remaining);
            return "restored " + id + " (adopted=" + merge.adoptedEntries()
                + ", duplicates=" + merge.duplicateEntries()
                + ", rejected=" + merge.rejectedEntries() + ", retained=" + remaining + ")";
        } catch (Throwable error) {
            LOG.warn("Could not restore quarantined FULL cache {}", id, error);
            return "restore failed: " + error.getClass().getSimpleName();
        }
    }

    /**
     * Adopt every previously saved FULL-cache directory into the stable
     * schema-3 namespace. Worldgen versions and settings are not inspected.
     * Every individual FULL must pass the envelope and coordinate checks.
     */
    private static void adoptPriorCacheDirectories(
        ServerLevel level, Path root, Fingerprint.Snapshot snapshot,
        String trigger, CacheDiskBudget budget
    ) throws IOException {
        List<Path> candidates;
        try (Stream<Path> stream = Files.list(root)) {
            candidates = stream.filter(Files::isDirectory)
                .filter(path -> path.getFileName().toString().matches("[0-9a-f]{24}"))
                .filter(path -> !path.getFileName().toString().equals(snapshot.fingerprint()))
                .sorted().toList();
        }
        Path active = root.resolve(snapshot.fingerprint());
        FingerprintManifest.Manifest current = snapshot.manifest(Set.of(snapshot.fingerprint()));
        for (Path source : candidates) {
            FingerprintManifest.Manifest old = historicalIdentity(source);
            if (old == null) {
                LOG.warn("Keeping historical cache with invalid owner metadata {}; recovery will preserve it", source);
                continue;
            }
            Stats before = stats(source);
            FingerprintManifest.Manifest existing =
                FingerprintManifest.readIfValid(active.resolve(FingerprintManifest.FILE_NAME));
            CacheDirectoryMerge.MergeStats copied =
                copyValidFulls(level, old, current, source, active, budget);
            FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME),
                snapshot.manifest(acceptedIdentities(snapshot, old, existing)));
            CacheDirectoryMerge.cleanupCopiedSources(
                source, active, cacheValidator(level, old),
                cacheValidator(level, validationManifest(current, active, old)));
            if (stats(source).entries() == 0) {
                deleteTree(source);
                budget.deleted(before.bytes());
            }
            LOG.info("Adopted historical FULL cache {} -> {}; checked={} adopted={} duplicates={} rejected={} trigger={}",
                old.fingerprint(), snapshot.fingerprint(), copied.sourceEntries(),
                copied.adoptedEntries(), copied.duplicateEntries(), copied.rejectedEntries(), trigger);
        }
    }

    private static void recoverPreviousQuarantines(
        ServerLevel level, Path root, Fingerprint.Snapshot snapshot, String trigger,
        CacheDiskBudget migrationBudget
    ) throws IOException {
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
            CacheDirectoryMerge.MergeStats merge = mergeForCompatibility(
                level, old, current, candidate, active, true, migrationBudget);

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

    private static void quarantineRejectedRemainders(Path root, Fingerprint.Snapshot snapshot, String trigger) throws IOException {
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

    private static void pruneQuarantine(Path root, CacheRetentionPolicy policy) throws IOException {
        RecoveryClaims.initialize(root);
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

    private static CacheDiskBudget refreshMigrationBudget(ServerLevel level) {
        Path cacheRoot = level.getServer().getWorldPath(LevelResource.ROOT)
            .resolve("chunksmith_chunk_cache");
        CacheDiskBudget budget = CacheDiskBudget.at(cacheRoot);
        budget.refresh(level.getServer().getServerDirectory()
            .resolve("config/maki-chunksmith-chunk-cache.properties"));
        return budget;
    }

    private static Predicate<Path> cacheValidator(
        ServerLevel level, FingerprintManifest.Manifest acceptedManifest
    ) {
        int dataVersion = WorldVersionBridge.getDataVersion(
            net.minecraft.SharedConstants.getCurrentVersion()).getVersion();
        String dimension = level.dimension().location().toString();
        return file -> {
            String rejected = CacheFormatValidator.rejectReason(
                file, acceptedManifest, dimension, dataVersion);
            if (rejected != null) {
                LOG.warn("Historical FULL cache failed migration validation {}: {}", file, rejected);
            }
            return rejected == null;
        };
    }

    private static FingerprintManifest.Manifest validationManifest(
        FingerprintManifest.Manifest current, Path active,
        FingerprintManifest.Manifest historical
    ) {
        Set<String> accepted = new LinkedHashSet<>(current.storedFingerprints());
        accepted.add(current.fingerprint());
        accepted.add(historical.fingerprint());
        accepted.addAll(historical.storedFingerprints());
        FingerprintManifest.Manifest existing = FingerprintManifest.readIfValid(
            active.resolve(FingerprintManifest.FILE_NAME));
        if (existing != null && existing.fingerprint().equals(current.fingerprint())) {
            accepted.addAll(existing.storedFingerprints());
        }
        return new FingerprintManifest.Manifest(
            current.fingerprint(), current.createdAtMillis(), Set.copyOf(accepted), current.entries());
    }

    /**
     * A different FULL generation is adopted only after semantic environment
     * equivalence and per-file envelope validation. Old files stay until the
     * accepted-fingerprint manifest is committed.
     */
    private static CacheDirectoryMerge.MergeStats mergeForCompatibility(
        ServerLevel level,
        FingerprintManifest.Manifest historical,
        FingerprintManifest.Manifest current,
        Path source, Path active,
        boolean preserveSource,
        CacheDiskBudget migrationBudget
    ) throws IOException {
        Predicate<Path> sourceVerified = cacheValidator(level, historical);
        Predicate<Path> targetVerified = cacheValidator(
            level, validationManifest(current, active, historical));
        return CacheDirectoryMerge.copyValidatedPreservingSource(
            source, active, sourceVerified, targetVerified, migrationBudget);
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

