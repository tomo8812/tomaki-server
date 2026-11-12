package maki.chunksmithcache;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;

/** Saved FULL integrity, stable namespace, and legacy-cache adoption tests. */
public final class FullCacheCompatibilitySelfTest {
    private static final int DATA_VERSION = 3955;
    private static final String OLD_ID = "c11f310d76e16d27d6bc06e9";

    private FullCacheCompatibilitySelfTest() {}

    public static void main(String[] args) throws Exception {
        inPlaceAdoption();
        require(Fingerprint.CACHE_ID.matches("[0-9a-f]{24}"),
            "FULL storage id must remain a stable cache-format namespace");
        require(!Fingerprint.CACHE_ID.equals(OLD_ID),
            "the stable namespace must differ from historical version-dependent names");
        testLegacyManifestRead();
        testPerFileFormatAndSelectiveMigration(
            new FingerprintManifest.Manifest(OLD_ID, 1L, Set.of(OLD_ID)));
        System.out.println("FULL cache: stable identity, legacy manifest parsing, NBT validation and selective migration PASS");
    }

    private static void inPlaceAdoption()throws Exception{
        Path root=Files.createTempDirectory("cache-inplace");
        Path source=Files.createDirectories(root.resolve("old")),target=Files.createDirectories(root.resolve("current"));
        Path config=root.resolveSibling(root.getFileName()+".properties");
        try{
            Files.write(source.resolve("a.nbt.gz"),new byte[100]);
            Files.write(source.resolve("b.nbt.gz"),new byte[100]);
            Files.writeString(config,"cache.max-active-bytes=300\ncache.minimum-free-bytes=1\n");
            CacheDiskBudget budget=CacheDiskBudget.at(root);budget.refresh(config);
            require(budget.tryAcquire(200)==null,"duplicate-copy migration would exceed the quota");
            var result=CacheDirectoryMerge.adoptValidatedInPlace(source,target,Files::isRegularFile,Files::isRegularFile);
            require(result.adoptedEntries()==2&&Files.size(target.resolve("a.nbt.gz"))==100,
                "adoption succeeds without two FULL copies under the same quota");
            // Retrying a partially completed adoption preserves conflicting valid FULLs.
            Files.createDirectories(source);Files.write(source.resolve("a.nbt.gz"),new byte[]{1});
            var retried=CacheDirectoryMerge.adoptValidatedInPlace(source,target,Files::isRegularFile,Files::isRegularFile);
            require(retried.rejectedEntries()==1&&Files.exists(source.resolve("a.nbt.gz")),"valid conflicting source remains preserved");
        }finally{
            try(var walk=Files.walk(root)){for(Path file:walk.sorted(java.util.Comparator.reverseOrder()).toList())Files.delete(file);}
            Files.deleteIfExists(config);
        }
    }

    private static void testLegacyManifestRead() throws Exception {
        String key = Base64.getUrlEncoder().withoutPadding().encodeToString(
            "level:seed".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String reason = Base64.getUrlEncoder().withoutPadding().encodeToString(
            "historical monitored environment".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String entry = "entry\t" + key + "\t3\t" + "a".repeat(64)
            + "\tRELEVANT\t" + reason + "\n";
        String old = "format\t2\nfingerprint\t" + OLD_ID + "\n"
            + "created\t1\nstored\t" + OLD_ID + "\n" + entry;
        FingerprintManifest.Manifest decoded = FingerprintManifest.decode(old);
        require(decoded.fingerprint().equals(OLD_ID), "historical owner id is preserved");
        require(decoded.storedFingerprints().contains(OLD_ID), "historical envelope identity is accepted");
        require(!FingerprintManifest.encode(decoded).contains("entry\t"),
            "old environment monitoring entries must not be written to new manifests");
        boolean bad = false;
        try { FingerprintManifest.decode(old.replace("a".repeat(64), "bad-digest")); }
        catch (java.io.IOException expected) { bad = true; }
        require(bad, "corrupt old owner manifest must fail closed");
    }

    private static void testPerFileFormatAndSelectiveMigration(
        FingerprintManifest.Manifest historical
    ) throws Exception {
        Path directory = Files.createTempDirectory("maki-cache-format-upgrade");
        try {
            Path old = directory.resolve("previous");
            Path current = directory.resolve("new");
            Path valid = old.resolve("r.11.-2/c.383.-61.nbt.gz");
            Path invalid = old.resolve("r.11.-2/c.382.-61.nbt.gz");
            Files.createDirectories(valid.getParent());

            CompoundTag good = envelope(historical.fingerprint(), 383, -61);
            NbtIo.writeCompressed(good, valid);
            require(CacheFormatValidator.rejectReason(
                valid, historical, "minecraft:overworld") == null,
                "a complete v3 FULL with identical DataVersion must pass");

            Path noTerrain = old.resolve("r.11.-2/c.381.-61.nbt.gz");
            CompoundTag missingSections = envelope(historical.fingerprint(), 381, -61);
            missingSections.getCompound("chunk").remove("sections");
            NbtIo.writeCompressed(missingSections, noTerrain);
            require("missing-terrain-sections".equals(CacheFormatValidator.rejectReason(
                noTerrain, historical, "minecraft:overworld")),
                "FULL metadata without serialized terrain sections must remain old/quarantined");
            Files.delete(noTerrain);

            CompoundTag bad = envelope(historical.fingerprint(), 382, -61);
            bad.putBoolean("entitiesComplete", false);
            NbtIo.writeCompressed(bad, invalid);
            require("entities-or-full-incomplete".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld")),
                "incomplete entities must remain old/quarantined");

            CacheDirectoryMerge.MergeStats stats = CacheDirectoryMerge.mergeValidated(
                old, current,
                p -> CacheFormatValidator.rejectReason(
                    p, historical, "minecraft:overworld") == null);
            require(stats.sourceEntries() == 2 && stats.adoptedEntries() == 1
                && stats.rejectedEntries() == 1,
                "only the format-valid FULL should migrate");
            require(Files.isRegularFile(current.resolve("r.11.-2/c.383.-61.nbt.gz")),
                "valid cache moved into the new generation");
            require(Files.isRegularFile(invalid), "invalid FULL must survive for quarantine");
            require(!Files.exists(valid), "validated original was moved");

            // Production historical-cache migrations copy before committing the stored-
            // fingerprint acceptance. The historical source must survive a
            // crash or missing manifest until reconciliation succeeds.
            NbtIo.writeCompressed(good, valid);
            Path afterCopy = directory.resolve("copy-only");
            CacheDirectoryMerge.MergeStats copy = CacheDirectoryMerge.copyValidatedPreservingSource(
                old, afterCopy,
                p -> CacheFormatValidator.rejectReason(
                    p, historical, "minecraft:overworld") == null);
            require(copy.adoptedEntries() == 1 && copy.rejectedEntries() == 1,
                "copy must adopt only the format-compatible entry");
            require(Files.isRegularFile(valid) && Files.isRegularFile(invalid)
                && Files.isRegularFile(afterCopy.resolve("r.11.-2/c.383.-61.nbt.gz")),
                "copy-first migration must preserve every original until manifest commit");

            Path retryTarget = directory.resolve("retry");
            Path retryValid = retryTarget.resolve("r.11.-2/c.383.-61.nbt.gz");
            Files.createDirectories(retryValid.getParent());
            Files.writeString(retryValid, "partial-copy");
            CacheDirectoryMerge.MergeStats retried = CacheDirectoryMerge.copyValidatedPreservingSource(
                old, retryTarget,
                p -> CacheFormatValidator.rejectReason(
                    p, historical, "minecraft:overworld") == null);
            require(retried.adoptedEntries() == 1 && retried.rejectedEntries() == 1,
                "an invalid existing destination must be replaced from the validated source");
            require(CacheFormatValidator.rejectReason(
                retryValid, historical, "minecraft:overworld") == null,
                "retry must leave a complete validated destination, not a partial duplicate");

            long cleaned = CacheDirectoryMerge.cleanupCopiedSources(
                old, afterCopy,
                p -> CacheFormatValidator.rejectReason(
                    p, historical, "minecraft:overworld") == null,
                p -> CacheFormatValidator.rejectReason(
                    p, historical, "minecraft:overworld") == null);
            require(cleaned == 1 && !Files.exists(valid) && Files.isRegularFile(invalid),
                "post-commit cleanup removes only byte-identical validated sources");

            Path budgetSource = directory.resolve("budget-source/r.11.-2/c.383.-61.nbt.gz");
            Files.createDirectories(budgetSource.getParent());
            NbtIo.writeCompressed(good, budgetSource);
            Path budgetRoot = directory.resolve("budget-root");
            Path budgetConfig = directory.resolve("budget.properties");
            Files.writeString(
                budgetConfig,
                "cache.max-active-bytes=1\ncache.minimum-free-bytes=1\n");
            CacheDiskBudget budget = CacheDiskBudget.at(budgetRoot);
            budget.refresh(budgetConfig);
            boolean budgetBlocked = false;
            try {
                CacheDirectoryMerge.copyValidatedPreservingSource(
                    budgetSource,
                    budgetRoot.resolve("minecraft_overworld/current"),
                    p -> CacheFormatValidator.rejectReason(
                        p, historical, "minecraft:overworld") == null,
                    p -> CacheFormatValidator.rejectReason(
                        p, historical, "minecraft:overworld") == null,
                    budget);
            } catch (java.io.IOException expected) {
                budgetBlocked = true;
            }
            require(budgetBlocked && Files.isRegularFile(budgetSource),
                "migration must defer without consuming the source when disk reserve/capacity is unavailable");

            CompoundTag checked = envelope(historical.fingerprint(), 382, -61);
            checked.getCompound("chunk").putInt("DataVersion", DATA_VERSION + 1);
            NbtIo.writeCompressed(checked, invalid);
            require(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld") == null,
                "server DataVersion changes must not quarantine readable FULLs");

            checked.getCompound("chunk").remove("DataVersion");
            NbtIo.writeCompressed(checked, invalid);
            require("missing-chunk-data-version".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld")),
                "malformed chunk metadata must still be rejected");

            checked = envelope(historical.fingerprint(), 382, -61);
            checked.putInt("schema", CacheEnvelope.SCHEMA+1);
            NbtIo.writeCompressed(checked, invalid);
            require("cache-schema".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld")),
                "new/unsupported serialization must reject");

            checked = envelope("aaaaaaaaaaaaaaaaaaaaaaaa", 382, -61);
            NbtIo.writeCompressed(checked, invalid);
            require("unaccepted-stored-fingerprint".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld")),
                "untrusted stored generation marker must reject");

            checked = envelope(historical.fingerprint(), 381, -61);
            NbtIo.writeCompressed(checked, invalid);
            require("coordinates-mismatch".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld")),
                "mismatched coordinates must reject");

            checked = envelope(historical.fingerprint(), 382, -61);
            checked.putBoolean("lodCommitted", false);
            NbtIo.writeCompressed(checked, invalid);
            require("entities-or-full-incomplete".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld")),
                "incomplete LOD must reject");

            Files.writeString(invalid, "not-gzip");
            require(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld") != null,
                "corrupt GZIP/NBT must reject");
        } finally {
            try (var files = Files.walk(directory)) {
                for (Path path : files.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    private static CompoundTag envelope(String fingerprint, int x, int z) {
        CompoundTag e = new CompoundTag();
        e.putInt("schema", CacheEnvelope.SCHEMA);
        e.putBoolean("entitiesComplete", true);
        e.putBoolean("lodCommitted", true);
        e.putString("fingerprint", fingerprint);
        e.putString("dimension", "minecraft:overworld");
        e.putInt("x", x);
        e.putInt("z", z);
        CompoundTag chunk = new CompoundTag();
        chunk.putString("Status", "minecraft:full");
        chunk.putInt("DataVersion", DATA_VERSION);
        chunk.putInt("xPos", x);
        chunk.putInt("zPos", z);
        chunk.put("entities", new ListTag());

        CompoundTag section = new CompoundTag();
        section.putByte("Y", (byte)0);
        CompoundTag blockStates = new CompoundTag();
        ListTag blockPalette = new ListTag();
        CompoundTag stone = new CompoundTag();
        stone.putString("Name", "minecraft:stone");
        blockPalette.add(stone);
        blockStates.put("palette", blockPalette);
        section.put("block_states", blockStates);

        CompoundTag biomes = new CompoundTag();
        ListTag biomePalette = new ListTag();
        biomePalette.add(StringTag.valueOf("minecraft:plains"));
        biomes.put("palette", biomePalette);
        section.put("biomes", biomes);

        ListTag sections = new ListTag();
        sections.add(section);
        chunk.put("sections", sections);
        e.put("chunk", chunk);
        return e;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
