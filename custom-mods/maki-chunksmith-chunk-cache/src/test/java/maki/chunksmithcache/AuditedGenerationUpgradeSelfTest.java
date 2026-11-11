package maki.chunksmithcache;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;

/**
 * Dynamic (no historical SHA allowlist) format/field migration regressions.
 * Tests future minor releases, unrelated worldgen changes, bad FULL payloads
 * and preservation of invalid files during a mixed generation transfer.
 */
public final class AuditedGenerationUpgradeSelfTest {
    private static final int DATA_VERSION = 3955;

    private record Pair(String oldName, String newName) {}

    // Concrete examples are test fixtures only; production accepts versioned
    // role names rather than enumerating these releases or their SHA digests.
    private static final List<Pair> PAIRS = List.of(
        new Pair("chunksmith-4.6.0+1.21.1-neoforge.jar",
                 "chunksmith-4.8.1+1.21.1-neoforge.jar"),
        new Pair("millenaire-9.0.3.jar", "millenaire-9.1.0.jar"),
        new Pair("maki-millenaire-lod-compat-1.0.6.jar",
                 "maki-millenaire-lod-compat-1.0.8.jar"),
        new Pair("maki-idle-pregen-1.1.1.jar", "maki-idle-pregen-1.1.2.jar"),
        new Pair("maki-ship-save-1.0.2.jar", "maki-ship-save-1.0.3.jar")
    );

    private AuditedGenerationUpgradeSelfTest() {}

    public static void main(String[] args) throws Exception {
        List<FingerprintManifest.Entry> oldEntries = baseline();
        List<FingerprintManifest.Entry> newEntries = baseline();
        for (Pair pair : PAIRS) {
            oldEntries.add(jar(pair.oldName()));
            newEntries.add(jar(pair.newName()));
        }
        FingerprintManifest.Manifest old = manifest(oldEntries);
        FingerprintManifest.Manifest current = manifest(newEntries);
        require(AuditedGenerationUpgrade.eligible(old, current),
            "role-based migration must not rely on JAR SHA allowlists");
        FingerprintManifest.Manifest historicalWithoutCodeEvidence =
            remove(old, GenerationRoleEvidence.CHUNKSMITH_KEY);
        require(AuditedGenerationUpgrade.eligible(historicalWithoutCodeEvidence, current),
            "historical caches without optional semantic code evidence still use per-file NBT validation");
        require(FingerprintManifest.sameCompatibilityInputsExcept(
            old, current, Set.of("policy:compatibility")),
            "same FULL schema and unchanged terrain inputs permit upgraded cache");
        require(FingerprintManifest.changedCompatibilityInputs(
            old, current, Set.of("policy:compatibility"), 20, null).isEmpty(),
            "approved role transition must normalize into identical format contracts");
        require(FingerprintManifest.compatibilityProofs(
            old, current, Set.of("policy:compatibility"), 20, null).size() >= PAIRS.size(),
            "role transitions must be auditable");

        List<FingerprintManifest.Entry> later = new ArrayList<>(newEntries);
        later.replaceAll(e -> e.key().equals("mods/chunksmith-4.8.1+1.21.1-neoforge.jar")
            ? jar("chunksmith-4.9.7+1.21.1-neoforge.jar") : e);
        later.replaceAll(e -> e.key().equals("mods/millenaire-9.1.0.jar")
            ? jar("millenaire-9.4.2.jar") : e);
        later.replaceAll(e -> e.key().equals("mods/maki-millenaire-lod-compat-1.0.8.jar")
            ? jar("maki-millenaire-lod-compat-1.0.9.jar") : e);
        FingerprintManifest.Manifest future = manifest(later);
        require(AuditedGenerationUpgrade.eligible(current, future),
            "later generated FULL cache generations must be eligible by field/schema, not known version pair");
        require(FingerprintManifest.sameCompatibilityInputsExcept(
            current, future, Set.of("policy:compatibility")),
            "future minor-role releases with unchanged worldgen inputs must not be rejected by release age");

        assertRejected(current, old, "downgrade");
        assertRejected(old, replace(current, GenerationRoleEvidence.CHUNKSMITH_KEY,
            "future-core-methods-changed"), "modified recorded Chunksmith generation code");
        assertRejected(old, replace(current, "mods/" + PAIRS.getFirst().newName(),
            "chunksmith-5.0.0+1.21.1-neoforge.jar", "major-new"), "unknown major upgrade");
        assertRejected(old, replace(current, "mods/" + PAIRS.getFirst().newName(),
            PAIRS.getFirst().newName(), "changed-hash"), "same-version JAR mutation");
        assertRejected(old, remove(current, "mods/" + PAIRS.get(2).newName()),
            "removed versioned role");
        assertRejected(old, replace(current, "level:seed", "seed-different"),
            "world seed");
        assertRejected(old, replace(current, "level:generator-class", "generator-different"),
            "generator implementation");
        assertRejected(old, replace(current, "world/datapacks/test/pack.mcmeta", "datapack-different"),
            "datapack");
        assertRejected(old, replace(current, "config/epicterrain.properties", "terrain-settings-different"),
            "terrain config");
        assertRejected(old, replace(current, "policy:compatibility", "policy-different"),
            "fingerprint policy");

        List<FingerprintManifest.Entry> extra = new ArrayList<>(newEntries);
        extra.add(value("mods/new-unknown-terrain.jar", "unreviewed-worldgen"));
        assertRejected(old, manifest(extra), "added generation-sensitive MOD");

        List<FingerprintManifest.Entry> two = new ArrayList<>(newEntries);
        two.add(jar(PAIRS.getFirst().oldName()));
        assertRejected(old, manifest(two), "duplicate role");

        testPerFileFormatAndSelectiveMigration(old);
        System.out.println("Format-based cache migration: dynamic releases + field validation + selective transfer PASS");
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

            NbtIo.writeCompressed(envelope(historical.fingerprint(), 382, -61), invalid);
            CompoundTag village = envelope(historical.fingerprint(), 382, -61);
            CompoundTag villageEntity = new CompoundTag();
            villageEntity.putString("id", "millenaire:villager");
            ListTag entities = new ListTag();
            entities.add(villageEntity);
            village.getCompound("chunk").put("entities", entities);
            NbtIo.writeCompressed(village, invalid);
            require("contains-millenaire-content".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION)),
                "serialized village content must not be imported blindly");

            village = envelope(historical.fingerprint(), 382, -61);
            village.getCompound("chunk").putInt("DataVersion", DATA_VERSION+1);
            NbtIo.writeCompressed(village, invalid);
            require("data-version-mismatch".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION)),
                "data version mismatch must reject");

            village = envelope(historical.fingerprint(), 382, -61);
            village.putInt("schema", CacheEnvelope.SCHEMA+1);
            NbtIo.writeCompressed(village, invalid);
            require("cache-schema".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION)),
                "new/unsupported serialization must reject");

            village = envelope("aaaaaaaaaaaaaaaaaaaaaaaa", 382, -61);
            NbtIo.writeCompressed(village, invalid);
            require("unaccepted-stored-fingerprint".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION)),
                "untrusted stored generation marker must reject");

            village = envelope(historical.fingerprint(), 381, -61);
            NbtIo.writeCompressed(village, invalid);
            require("coordinates-mismatch".equals(CacheFormatValidator.rejectReason(
                invalid, historical, "minecraft:overworld", DATA_VERSION)),
                "mismatched coordinates must reject");

            village = envelope(historical.fingerprint(), 382, -61);
            village.putBoolean("lodCommitted", false);
            NbtIo.writeCompressed(village, invalid);
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
            new FingerprintManifest.Entry(GenerationRoleEvidence.CHUNKSMITH_KEY, 200L,
                CompatibilityFingerprint.sha256("same-generation-code"),
                FingerprintManifest.Impact.IGNORED,
                "observed Chunksmith generation/LOD code, format contract v1"),
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
