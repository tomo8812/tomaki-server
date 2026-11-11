package maki.chunksmithcache;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;

/**
 * FULL cache generation-boundary and per-entry integrity regressions.
 * No forward-version or JAR-pair compatibility mechanism is required.
 */
public final class FullCacheCompatibilitySelfTest {
    private static final int DATA_VERSION = 3955;

    private FullCacheCompatibilitySelfTest() {}

    public static void main(String[] args) throws Exception {
        List<FingerprintManifest.Entry> past = baseline();
        List<FingerprintManifest.Entry> current = baseline();

        past.add(value("mods/chunksmith-4.6.0+1.21.1-neoforge.jar", "scheduler-old"));
        current.add(value("mods/chunksmith-4.8.1+1.21.1-neoforge.jar", "scheduler-new"));
        past.add(value("mods/millenaire-9.0.3.jar", "deferred-village-old"));
        current.add(value("mods/millenaire-9.1.0.jar", "deferred-village-new"));
        past.add(value("mods/maki-millenaire-lod-compat-1.0.6.jar", "bridge-old"));
        current.add(value("mods/maki-millenaire-lod-compat-1.0.8.jar", "bridge-new"));
        past.add(value("mods/maki-idle-pregen-1.1.1.jar", "idle-old"));
        current.add(value("mods/maki-idle-pregen-1.1.2.jar", "idle-new"));
        past.add(value("mods/maki-ship-save-1.0.2.jar", "saving-old"));
        current.add(value("mods/maki-ship-save-1.0.3.jar", "saving-new"));
        past.add(value("config/jade-client.toml", "old-ui-setting"));
        current.add(value("config/jade-client.toml", "new-ui-setting"));
        past.add(value("mods/utility-no-worldgen-1.0.jar", "utility-old"));
        current.add(value("mods/utility-no-worldgen-2.0.jar", "utility-new"));

        FingerprintManifest.Manifest old = manifest(past);
        FingerprintManifest.Manifest now = manifest(current);
        require(FingerprintManifest.sameCompatibilityInputsExcept(
            old, now, Set.of("policy:compatibility")),
            "FULL terrain must survive LOD/scheduling/village and UI updates");
        require(FingerprintManifest.changedCompatibilityInputs(
            old, now, Set.of("policy:compatibility"), 30, null).isEmpty(),
            "non-generation changes must not show as incompatibility");

        assertRejected(old, replace(now, "level:seed", "another-seed"), "seed");
        assertRejected(old, replace(now, "level:generator-class", "another-generator"), "generator");
        assertRejected(old, replace(now, "minecraft:data-version", "4080"), "Minecraft DataVersion");
        assertRejected(old, replace(now, "world/datapacks/test/pack.mcmeta", "new-worldgen-datapack"), "datapack");
        assertRejected(old, replace(now, "config/epicterrain.properties", "new-terrain-settings"), "terrain settings");
        assertRejected(old, replace(now, "mods/epicterrain-0.3.0-1.21.1.jar", "new-terrain-jar"), "terrain MOD");
        List<FingerprintManifest.Entry> extra = new ArrayList<>(current);
        extra.add(new FingerprintManifest.Entry(
            "mods/new-worldgen-1.0.jar", 101L, CompatibilityFingerprint.sha256("worldgen"),
            FingerprintManifest.Impact.RELEVANT,
            "JAR contains worldgen/structure/dimension datapack resources"));
        assertRejected(old, manifest(extra), "new generation MOD");

        testPerFileFormatAndSelectiveMigration(old);
        System.out.println("FULL cache boundary: terrain changes rejected, LOD/utility changes reused, NBT validated PASS");
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
                valid, historical, "minecraft:overworld", DATA_VERSION) == null,
                "a complete v3 FULL with identical DataVersion must pass");

            Path noTerrain = old.resolve("r.11.-2/c.381.-61.nbt.gz");
            CompoundTag missingSections = envelope(historical.fingerprint(), 381, -61);
            missingSections.getCompound("chunk").remove("sections");
            NbtIo.writeCompressed(missingSections, noTerrain);
            require("missing-terrain-sections".equals(CacheFormatValidator.rejectReason(
                noTerrain, historical, "minecraft:overworld", DATA_VERSION)),
                "FULL metadata without serialized terrain sections must remain old/quarantined");
            Files.delete(noTerrain);

            CompoundTag bad = envelope(historical.fingerprint(), 382, -61);
            bad.putBoolean("entitiesComplete", false);
            NbtIo.writeCompressed(bad, invalid);
            require("entities-or-lod-incomplete".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION)),
                "incomplete entities must remain old/quarantined");

            CacheDirectoryMerge.MergeStats stats = CacheDirectoryMerge.mergeValidated(
                old, current,
                p -> CacheFormatValidator.rejectReason(
                    p, historical, "minecraft:overworld", DATA_VERSION) == null);
            require(stats.sourceEntries() == 2 && stats.adoptedEntries() == 1
                && stats.rejectedEntries() == 1,
                "only the format-valid FULL should migrate");
            require(Files.isRegularFile(current.resolve("r.11.-2/c.383.-61.nbt.gz")),
                "valid cache moved into the new generation");
            require(Files.isRegularFile(invalid), "invalid FULL must survive for quarantine");
            require(!Files.exists(valid), "validated original was moved");

            // Production semantic migrations copy before committing the stored-
            // fingerprint acceptance. The historical source must survive a
            // crash or missing manifest until reconciliation succeeds.
            NbtIo.writeCompressed(good, valid);
            Path afterCopy = directory.resolve("copy-only");
            CacheDirectoryMerge.MergeStats copy = CacheDirectoryMerge.copyValidatedPreservingSource(
                old, afterCopy,
                p -> CacheFormatValidator.rejectReason(
                    p, historical, "minecraft:overworld", DATA_VERSION) == null);
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
                    p, historical, "minecraft:overworld", DATA_VERSION) == null);
            require(retried.adoptedEntries() == 1 && retried.rejectedEntries() == 1,
                "an invalid existing destination must be replaced from the validated source");
            require(CacheFormatValidator.rejectReason(
                retryValid, historical, "minecraft:overworld", DATA_VERSION) == null,
                "retry must leave a complete validated destination, not a partial duplicate");

            long cleaned = CacheDirectoryMerge.cleanupCopiedSources(
                old, afterCopy,
                p -> CacheFormatValidator.rejectReason(
                    p, historical, "minecraft:overworld", DATA_VERSION) == null,
                p -> CacheFormatValidator.rejectReason(
                    p, historical, "minecraft:overworld", DATA_VERSION) == null);
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
                        p, historical, "minecraft:overworld", DATA_VERSION) == null,
                    p -> CacheFormatValidator.rejectReason(
                        p, historical, "minecraft:overworld", DATA_VERSION) == null,
                    budget);
            } catch (java.io.IOException expected) {
                budgetBlocked = true;
            }
            require(budgetBlocked && Files.isRegularFile(budgetSource),
                "migration must defer without consuming the source when disk reserve/capacity is unavailable");

            CompoundTag checked = envelope(historical.fingerprint(), 382, -61);
            checked.getCompound("chunk").putInt("DataVersion", DATA_VERSION+1);
            NbtIo.writeCompressed(checked, invalid);
            require("data-version-mismatch".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION)),
                "data version mismatch must reject");

            checked = envelope(historical.fingerprint(), 382, -61);
            checked.putInt("schema", CacheEnvelope.SCHEMA+1);
            NbtIo.writeCompressed(checked, invalid);
            require("cache-schema".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION)),
                "new/unsupported serialization must reject");

            checked = envelope("aaaaaaaaaaaaaaaaaaaaaaaa", 382, -61);
            NbtIo.writeCompressed(checked, invalid);
            require("unaccepted-stored-fingerprint".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION)),
                "untrusted stored generation marker must reject");

            checked = envelope(historical.fingerprint(), 381, -61);
            NbtIo.writeCompressed(checked, invalid);
            require("coordinates-mismatch".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION)),
                "mismatched coordinates must reject");

            checked = envelope(historical.fingerprint(), 382, -61);
            checked.putBoolean("lodCommitted", false);
            NbtIo.writeCompressed(checked, invalid);
            require("entities-or-lod-incomplete".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION)),
                "incomplete LOD must reject");

            Files.writeString(invalid, "not-gzip");
            require(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION) != null,
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

    private static void assertRejected(FingerprintManifest.Manifest old,
                                       FingerprintManifest.Manifest current, String message) {
        require(!FingerprintManifest.sameCompatibilityInputsExcept(
            old, current, Set.of("policy:compatibility")),
            message + " must reject migration");
    }

    private static List<FingerprintManifest.Entry> baseline() {
        return new ArrayList<>(List.of(
            value("policy:compatibility", "policy-v9"),
            value("level:seed", "original-seed"),
            value("level:dimension", "minecraft:overworld"),
            value("level:generator-class", "NoiseBasedChunkGenerator"),
            value("minecraft:data-version", Integer.toString(DATA_VERSION)),
            value("level.dat:WorldGenSettings", "worldgen-still-same"),
            value("world/datapacks/test/pack.mcmeta", "datapack-still-same"),
            value("config/epicterrain.properties", "terrain-options"),
            value("mods/epicterrain-0.3.0-1.21.1.jar", "terrain-binary")
        ));
    }

    private static FingerprintManifest.Manifest replace(
        FingerprintManifest.Manifest original, String from, String newKey, String text
    ) {
        List<FingerprintManifest.Entry> entries = new ArrayList<>();
        boolean changed = false;
        for (FingerprintManifest.Entry entry : original.entries()) {
            if (entry.key().equals(from)) {
                entries.add(value(newKey, text));
                changed = true;
            } else entries.add(entry);
        }
        require(changed, "test setup missing " + from);
        return manifest(entries);
    }

    private static FingerprintManifest.Manifest replace(
        FingerprintManifest.Manifest original, String key, String text
    ) { return replace(original, key, key, text); }

    private static FingerprintManifest.Manifest remove(FingerprintManifest.Manifest original, String key) {
        List<FingerprintManifest.Entry> entries = new ArrayList<>(original.entries());
        require(entries.removeIf(e -> e.key().equals(key)), "test setup missing " + key);
        return manifest(entries);
    }

    private static FingerprintManifest.Entry jar(String name) {
        return value("mods/" + name, "SHA-example-" + name);
    }

    private static FingerprintManifest.Entry value(String key, String value) {
        return new FingerprintManifest.Entry(key, value.length(),
            CompatibilityFingerprint.sha256(value),
            FingerprintManifest.Impact.RELEVANT, "test semantic input");
    }

    private static FingerprintManifest.Manifest manifest(List<FingerprintManifest.Entry> entries) {
        return new FingerprintManifest.Manifest(
            CompatibilityFingerprint.compute(entries), 1L, Set.of(
                CompatibilityFingerprint.compute(entries)), entries);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
