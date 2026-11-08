package maki.chunksmithcache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.ArrayList;
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
    static final String MIGRATION_CONTRACT = "entry-migration-v10";
    private static final long MIGRATION_RESERVATION_OVERHEAD = 65_536L;
    private static final Pattern CACHE_FILE = Pattern.compile("c\\.(-?\\d+)\\.(-?\\d+)\\.nbt\\.gz");

    private CacheGenerationMigration() {}

    @FunctionalInterface
    interface SourceAuthorization {
        void authorize(FingerprintManifest.Manifest sourceManifest) throws IOException;
    }

    static Stats copyReusableEntriesPreservingSource(
        Path source,
        Path target,
        String expectedDimension,
        CacheDiskBudget budget,
        SourceAuthorization authorization
    ) throws IOException {
        FingerprintManifest.Manifest currentManifest =
            FingerprintManifest.readIfValid(target.resolve(FingerprintManifest.FILE_NAME));
        return copyReusableEntriesPreservingSource(
            source, target, expectedDimension, currentManifest, budget, authorization
        );
    }

    static Stats copyReusableEntriesPreservingSource(
        Path source,
        Path target,
        String expectedDimension,
        FingerprintManifest.Manifest currentManifest,
        CacheDiskBudget budget,
        SourceAuthorization authorization
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
            throw new IOException("Recover interrupted promotion ownership before adopting " + source);
        }
        files = files.stream().filter(path -> path.getFileName().toString().endsWith(".nbt.gz")).toList();

        long adopted = 0L;
        long recoveredExisting = 0L;
        long duplicates = 0L;
        long promotionOnly = 0L;
        long unproven = 0L;
        long unreadable = 0L;
        long invalid = 0L;
        long deferredCapacity = 0L;
        long legacyReused = 0L;
        long legacyEntityRegenerated = 0L;
        long legacyFullRegeneration = 0L;
        long legacyHeld = 0L;
        long legacyInvalid = 0L;
        long legacyActiveWins = 0L;
        List<String> legacyOutcomes = new ArrayList<>();
        FingerprintManifest.Manifest sourceManifest =
            FingerprintManifest.readIfValid(source.resolve(FingerprintManifest.FILE_NAME));
        boolean sourceAuthorized = false;

        for (int index = 0; index < files.size(); index++) {
            Path path = files.get(index);
            byte[] compressed;
            try {
                // Separate filesystem availability from payload validity. A failure to
                // fetch the bytes may recover on a later pass; once bytes are available,
                // malformed gzip/NBT is a coordinate-local terminal invalid entry and
                // must not force a full-generation retry every minute.
                compressed = Files.readAllBytes(path);
            } catch (IOException readFailure) {
                unreadable++;
                continue;
            }

            CompoundTag envelope;
            try (ByteArrayInputStream input = new ByteArrayInputStream(compressed)) {
                envelope = NbtIo.readCompressed(input, NbtAccounter.create(64L << 20));
            } catch (Throwable malformedPayload) {
                invalid++;
                continue;
            }

            boolean legacySchema1 = envelope.getInt("schema") == 1;
            CacheEnvelope.Reuse reuse = classify(path, envelope, sourceManifest, expectedDimension);
            String legacySourceSha = null;
            String legacyReason = null;
            if (reuse == CacheEnvelope.Reuse.INVALID) {
                invalid++;
                if (legacySchema1) {
                    legacyInvalid++;
                    legacyOutcomes.add(legacyOutcome(envelope, "INVALID", "cache-identity-invalid"));
                }
                continue;
            }
            if (legacySchema1) {
                if (reuse != CacheEnvelope.Reuse.LEGACY_UNPROVEN || sourceManifest == null) {
                    unproven++;
                    legacyHeld++;
                    legacyOutcomes.add(legacyOutcome(envelope, "HOLD", "generation-manifest-unproven"));
                    continue;
                }
                LegacySchema1Recovery.Decision decision =
                    LegacySchema1Recovery.decide(envelope, sourceManifest, currentManifest);
                if (decision.outcome() == LegacySchema1Recovery.Outcome.FULL_REGENERATE) {
                    legacyFullRegeneration++;
                    legacyOutcomes.add(legacyOutcome(envelope, "FULL_REGEN", decision.reason()));
                    continue;
                }
                if (decision.outcome() != LegacySchema1Recovery.Outcome.REUSE_FULL) {
                    unproven++;
                    legacyHeld++;
                    legacyOutcomes.add(legacyOutcome(envelope, "HOLD", decision.reason()));
                    continue;
                }
                legacySourceSha = sha256(compressed);
                legacyReason = decision.reason();
                envelope = LegacySchema1Recovery.upgradedEnvelope(envelope, legacySourceSha);
                reuse = classify(path, envelope, sourceManifest, expectedDimension);
                if (reuse != CacheEnvelope.Reuse.PROMOTION_ONLY) {
                    invalid++;
                    legacyInvalid++;
                    legacyOutcomes.add(legacyOutcome(envelope, "INVALID", "functional-upgrade-contract-invalid"));
                    continue;
                }
            } else if (reuse == CacheEnvelope.Reuse.LEGACY_UNPROVEN) {
                unproven++;
                continue;
            }

            // Generation compatibility is proven by GenerationStore, but readers validate
            // each visible payload against the active generation manifest. Authorize the
            // source generation before the first reusable payload can become visible, then
            // verify the durable manifest actually accepts this entry's stored fingerprint.
            if (!sourceAuthorized) {
                authorization.authorize(sourceManifest);
                sourceAuthorized = true;
            }
            if (!targetAcceptsStoredFingerprint(target, envelope.getString("fingerprint"))) {
                throw new IOException("Migration source fingerprint was not authorized before publication: "
                    + envelope.getString("fingerprint"));
            }

            Path destination = target.resolve(source.relativize(path));
            Files.createDirectories(destination.getParent());
            Path claim = PromotionClaim.claimedPath(destination);
            try (var ignored = CachePathLocks.lock(claim)) {
                if (RecoveryClaims.claimExists(claim)) {
                    duplicates++;
                    continue;
                }
                if (Files.exists(destination)) {
                    if (sameMigratedEntry(path, destination, reuse, sourceManifest, expectedDimension, legacySourceSha)) {
                        restoreCompletionMarker(destination, reuse);
                        recoveredExisting++;
                        if (legacySourceSha != null) {
                            legacyReused++;
                            legacyOutcomes.add(legacyOutcome(envelope, "REUSE_FULL_RECOVERED", legacyReason));
                        }
                        if (reuse == CacheEnvelope.Reuse.PROMOTION_ONLY) promotionOnly++;
                    } else {
                        duplicates++;
                        if (legacySourceSha != null) {
                            legacyActiveWins++;
                            legacyOutcomes.add(legacyOutcome(envelope, "ACTIVE_CACHE_WINS", "existing-active-payload-preserved"));
                        }
                    }
                    continue;
                }

                long estimate = Math.addExact(Files.size(path), MIGRATION_RESERVATION_OVERHEAD);
                CacheDiskBudget.Lease storage = budget.tryAcquire(estimate);
                if (storage == null) {
                    // Stop at the first admission failure. The source is retained and
                    // recovery-info records the unprocessed suffix, so the next retry
                    // resumes from durable source data without thrashing every remaining
                    // NBT while the same capacity limit is still active.
                    deferredCapacity += files.size() - index;
                    break;
                }

                Path temporary = destination.resolveSibling(
                    destination.getFileName() + ".migrate-" + Long.toUnsignedString(System.nanoTime())
                );
                boolean destinationPublished = false;
                boolean accounted = false;
                try {
                    if (legacySourceSha != null) {
                        NbtIo.writeCompressed(envelope, temporary);
                    } else {
                        Files.copy(path, temporary, StandardCopyOption.COPY_ATTRIBUTES);
                    }

                    // Remove a stale sidecar before a promotion-only payload can become visible.
                    if (reuse != CacheEnvelope.Reuse.GENERATED_COMPLETE) {
                        CacheManager.clearGeneratedComplete(destination);
                    }
                    try {
                        Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE);
                    } catch (AtomicMoveNotSupportedException unsupported) {
                        Files.move(temporary, destination);
                    }
                    destinationPublished = true;

                    restoreCompletionMarker(destination, reuse);
                    long added = Files.size(destination);
                    Path marker = CacheManager.generatedCompleteMarker(destination);
                    if (Files.isRegularFile(marker)) added = Math.addExact(added, Files.size(marker));
                    storage.published(added);
                    accounted = true;

                    if (reuse == CacheEnvelope.Reuse.PROMOTION_ONLY) promotionOnly++;
                    if (legacySourceSha != null) {
                        legacyReused++;
                        legacyOutcomes.add(legacyOutcome(envelope, "REUSE_FULL", legacyReason));
                    }
                    adopted++;
                } finally {
                    if (destinationPublished && !accounted) {
                        // Publication succeeded but a later sidecar/accounting operation
                        // failed. Conservatively account whatever is now visible so another
                        // writer cannot consume the normal-world reserve before refresh.
                        long added = Files.isRegularFile(destination) ? Files.size(destination) : 0L;
                        Path marker = CacheManager.generatedCompleteMarker(destination);
                        if (Files.isRegularFile(marker)) added = Math.addExact(added, Files.size(marker));
                        if (added > 0L) storage.published(added);
                    }
                    storage.close();
                    Files.deleteIfExists(temporary);
                }
            }
        }

        writeLegacyOutcomeLedger(source, legacyOutcomes);
        return new Stats(
            files.size(), adopted, recoveredExisting, duplicates, promotionOnly,
            legacyReused, legacyEntityRegenerated, legacyFullRegeneration,
            legacyHeld, legacyInvalid, legacyActiveWins,
            unproven, unreadable, invalid, deferredCapacity
        );
    }

    private static boolean targetAcceptsStoredFingerprint(Path target, String storedFingerprint) {
        FingerprintManifest.Manifest manifest =
            FingerprintManifest.readIfValid(target.resolve(FingerprintManifest.FILE_NAME));
        return manifest != null
            && (manifest.fingerprint().equals(storedFingerprint)
                || manifest.storedFingerprints().contains(storedFingerprint));
    }

    private static boolean sameMigratedEntry(
        Path source,
        Path destination,
        CacheEnvelope.Reuse expectedReuse,
        FingerprintManifest.Manifest sourceManifest,
        String expectedDimension,
        String legacySourceSha
    ) {
        try {
            if (!Files.isRegularFile(destination)) return false;
            if (legacySourceSha == null && Files.mismatch(source, destination) != -1L) return false;
            CompoundTag envelope = NbtIo.readCompressed(destination, NbtAccounter.create(64L << 20));
            if (legacySourceSha != null && !LegacySchema1Recovery.isUpgradeOf(envelope, legacySourceSha)) return false;
            return classify(destination, envelope, sourceManifest, expectedDimension) == expectedReuse;
        } catch (Throwable unreadable) {
            return false;
        }
    }

    private static void restoreCompletionMarker(Path destination, CacheEnvelope.Reuse reuse) throws IOException {
        if (reuse == CacheEnvelope.Reuse.GENERATED_COMPLETE) {
            CacheManager.markGeneratedComplete(destination);
        } else {
            CacheManager.clearGeneratedComplete(destination);
        }
    }

    static CacheEnvelope.Reuse classify(
        Path path,
        CompoundTag envelope,
        FingerprintManifest.Manifest sourceManifest,
        String expectedDimension
    ) {
        CacheEnvelope.Reuse reuse = CacheEnvelope.reuse(envelope);
        if (reuse == CacheEnvelope.Reuse.INVALID) return reuse;
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
            CompoundTag chunk = envelope.getCompound("chunk");
            if ((chunk.contains("xPos") && chunk.getInt("xPos") != x)
                || (chunk.contains("zPos") && chunk.getInt("zPos") != z)) {
                return CacheEnvelope.Reuse.INVALID;
            }
        } catch (RuntimeException invalidCoordinate) {
            return CacheEnvelope.Reuse.INVALID;
        }
        return reuse;
    }

    private static String sha256(byte[] bytes) {
        var digest = CompatibilityFingerprint.digest();
        digest.update(bytes);
        return CompatibilityFingerprint.hex(digest.digest());
    }

    private static String legacyOutcome(CompoundTag envelope, String outcome, String reason) {
        return envelope.getInt("x") + "\t" + envelope.getInt("z") + "\t" + outcome + "\t" + reason;
    }

    private static void writeLegacyOutcomeLedger(Path source, List<String> outcomes) throws IOException {
        if (outcomes.isEmpty()) return;
        outcomes.sort(String::compareTo);
        Path ledger = source.resolve("legacy-schema1-recovery-v1.tsv");
        Path temporary = ledger.resolveSibling(ledger.getFileName() + ".tmp");
        StringBuilder body = new StringBuilder("policy\t").append(LegacySchema1Recovery.POLICY).append('\n');
        body.append("x\tz\toutcome\treason\n");
        for (String outcome : outcomes) body.append(outcome).append('\n');
        Files.writeString(temporary, body.toString());
        try {
            Files.move(temporary, ledger, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException unsupported) {
            Files.move(temporary, ledger, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    record Stats(
        long sourceEntries,
        long adoptedEntries,
        long recoveredExistingEntries,
        long duplicateEntries,
        long promotionOnlyEntries,
        long legacyReusedEntries,
        long legacyEntityRegeneratedEntries,
        long legacyFullRegenerationEntries,
        long legacyHeldEntries,
        long legacyInvalidEntries,
        long legacyActiveWinsEntries,
        long retainedUnprovenEntries,
        long retainedUnreadableEntries,
        long retainedInvalidEntries,
        long deferredCapacityEntries
    ) {
        long acceptedSourceEntries() {
            return adoptedEntries + recoveredExistingEntries;
        }

        boolean completeForCurrentPass() {
            return retainedUnreadableEntries == 0L && deferredCapacityEntries == 0L;
        }
    }
}
