package maki.chunksmithcache;

import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

/**
 * Regression of the actual audited old/new distribution identity contract.
 * Other terrain inputs remain immutable even for this approved JAR bundle.
 */
public final class AuditedGenerationUpgradeSelfTest {
    private record Artifact(String oldName, long oldSize, String oldSha,
                            String newName, long newSize, String newSha) {}

    private static final List<Artifact> PAIRS = List.of(
        new Artifact("chunksmith-4.6.0+1.21.1-neoforge.jar", 700128L,
            "0bf37facd76117b1e6a1b638232d840b60c1da5a2f74bb52d15e7c2e80011646",
            "chunksmith-4.8.1+1.21.1-neoforge.jar", 707645L,
            "bf6371873cb78f2a7645001eb1ef0f7f6d7e5e80c51a52667b7d37ee898888ca"),
        new Artifact("millenaire-9.0.3.jar", 18506464L,
            "5882528027ac67e8759db3926ebef36abe2300029526582f80c7a988b583ec62",
            "millenaire-9.1.0.jar", 18988125L,
            "79932591423c867bada662827a984c52bd355525358b215fe31a5413897708ce"),
        new Artifact("maki-millenaire-lod-compat-1.0.6.jar", 106228L,
            "27d466ac52e1bbb510ee639294ab6713fdebac16597f146affa3d9d6660dfc52",
            "maki-millenaire-lod-compat-1.0.8.jar", 116616L,
            "b468365be07ac1829acbe1e1a366298ca6faffafb40f10658516a3694ee75af4"),
        new Artifact("maki-idle-pregen-1.1.1.jar", 54977L,
            "85edab9fbce7fd0a643aead86577e269037fc39fe84606c229f1d446441a5f12",
            "maki-idle-pregen-1.1.2.jar", 54977L,
            "95f1178e70c852982faf9200db3fbf19754b5ff9807e2b391382fae7436e2181"),
        new Artifact("maki-ship-save-1.0.2.jar", 11013L,
            "a62a206c33995e141e8e365c0766867b98be6e636001007f30591b05d196eb32",
            "maki-ship-save-1.0.3.jar", 11012L,
            "1775a10df3660e68901dd510798b4a159f6a15df9efbf188f07ee30d40777801")
    );

    private AuditedGenerationUpgradeSelfTest() {}

    public static void main(String[] args) {
        List<FingerprintManifest.Entry> oldEntries = baseline();
        List<FingerprintManifest.Entry> newEntries = baseline();
        for (Artifact pair : PAIRS) {
            oldEntries.add(jar(pair.oldName(), pair.oldSize(), pair.oldSha()));
            newEntries.add(jar(pair.newName(), pair.newSize(), pair.newSha()));
        }

        FingerprintManifest.Manifest old = manifest(oldEntries);
        FingerprintManifest.Manifest updated = manifest(newEntries);
        require(!old.fingerprint().equals(updated.fingerprint()),
            "real artifact upgrade must still rotate the current fingerprint");
        require(AuditedGenerationUpgrade.eligible(old, updated), "audited exact distribution should be eligible");
        require(FingerprintManifest.sameCompatibilityInputsExcept(
            old, updated, Set.of("policy:compatibility")),
            "verified old bundle must adopt as current generation without modifying cache NBT");
        require(FingerprintManifest.changedCompatibilityInputs(
            old, updated, Set.of("policy:compatibility"), 20, null).isEmpty(),
            "verified JAR changes must be normalized in the migration diagnostic");
        require(FingerprintManifest.compatibilityProofs(
            old, updated, Set.of("policy:compatibility"), 20, null).size() >= PAIRS.size(),
            "audit proof must explain exactly which artifact upgrades were accepted");

        assertRejected(old, replace(updated, "mods/" + PAIRS.getFirst().newName(), "changed-chunksmith"),
            "changed Chunksmith byte payload");
        assertRejected(old, replace(updated, "mods/" + PAIRS.get(1).newName(), "changed-millenaire"),
            "changed Millenaire byte payload");
        assertRejected(old, remove(updated, "mods/" + PAIRS.get(2).newName()),
            "missing compatibility bridge");
        assertRejected(old, replace(old, "mods/" + PAIRS.get(3).oldName(), "fake-historical-sha"),
            "unaudited old idle-pregen binary");
        assertRejected(old, replace(updated, "level:seed", "different-seed"),
            "world seed changed");
        assertRejected(old, replace(updated, "level:generator-class", "different-generator"),
            "generator implementation changed");
        assertRejected(old, replace(updated, "world/datapacks/example/pack.mcmeta", "different-data"),
            "datapack changed");
        assertRejected(old, replace(updated, "config/epicterrain.properties", "different-terrain-setting"),
            "terrain config changed");
        assertRejected(old, replace(updated, "policy:compatibility", "different-policy"),
            "compatibility policy changed");

        List<FingerprintManifest.Entry> extra = new ArrayList<>(newEntries);
        extra.add(value("mods/unverified-worldgen.jar", "new-code"));
        assertRejected(old, manifest(extra), "unreviewed added generation MOD");

        List<FingerprintManifest.Entry> two = new ArrayList<>(newEntries);
        two.add(jar(PAIRS.getFirst().oldName(), PAIRS.getFirst().oldSize(), PAIRS.getFirst().oldSha()));
        assertRejected(old, manifest(two), "two Chunksmith versions installed together");

        require(!AuditedGenerationUpgrade.eligible(updated, old),
            "approval is one-way, not permission to downgrade");
        require(!FingerprintManifest.sameCompatibilityInputsExcept(
            updated, old, Set.of("policy:compatibility")),
            "downgrade cannot be admitted by this transition");

        verifyShippedArtifacts();
        System.out.println("Audited generation upgrade: positive + fail-closed scenarios PASS");
    }

    private static void verifyShippedArtifacts() {
        String dir = System.getProperty("auditedUpgrade.modsDir");
        if (dir == null || dir.isBlank()) return;
        for (Artifact pair : PAIRS) {
            Path file = Path.of(dir).resolve(pair.newName());
            try {
                require(Files.isRegularFile(file), "missing shipped JAR " + file);
                require(Files.size(file) == pair.newSize(), "shipped JAR size changed: " + file);
                byte[] data = Files.readAllBytes(file);
                String sha = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data));
                require(sha.equals(pair.newSha()), "unreviewed shipped JAR hash: " + file);
            } catch (Exception e) {
                throw new AssertionError("Unable to verify exact distributed JAR " + file, e);
            }
        }
    }

    private static void assertRejected(FingerprintManifest.Manifest old,
                                       FingerprintManifest.Manifest current, String scenario) {
        require(!FingerprintManifest.sameCompatibilityInputsExcept(
            old, current, Set.of("policy:compatibility")), scenario + " must fail closed");
    }

    private static List<FingerprintManifest.Entry> baseline() {
        return new ArrayList<>(List.of(
            value("policy:compatibility", "policy-v9 example"),
            value("level:seed", "real-seed"),
            value("level:dimension", "minecraft:overworld"),
            value("level:generator-class", "net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator"),
            value("minecraft:data-version", "3955"),
            value("level.dat:WorldGenSettings", "worldgen-settings"),
            value("world/datapacks/example/pack.mcmeta", "source-datapack"),
            value("config/epicterrain.properties", "terrain-settings"),
            value("mods/epicterrain-0.3.0-1.21.1.jar", "same-epicterrain-jar")
        ));
    }

    private static FingerprintManifest.Manifest replace(FingerprintManifest.Manifest original,
                                                         String key, String text) {
        List<FingerprintManifest.Entry> entries = new ArrayList<>();
        boolean found = false;
        for (FingerprintManifest.Entry entry : original.entries()) {
            if (entry.key().equals(key)) { entries.add(value(key, text)); found = true; }
            else entries.add(entry);
        }
        require(found, "test setup missing key " + key);
        return manifest(entries);
    }

    private static FingerprintManifest.Manifest remove(FingerprintManifest.Manifest original, String key) {
        List<FingerprintManifest.Entry> entries = new ArrayList<>(original.entries());
        require(entries.removeIf(entry -> entry.key().equals(key)), "test setup missing key " + key);
        return manifest(entries);
    }

    private static FingerprintManifest.Entry jar(String name, long size, String sha) {
        return new FingerprintManifest.Entry("mods/" + name, size, sha,
            FingerprintManifest.Impact.RELEVANT, "exact audited source");
    }

    private static FingerprintManifest.Entry value(String key, String text) {
        return new FingerprintManifest.Entry(key, text.length(), CompatibilityFingerprint.sha256(text),
            FingerprintManifest.Impact.RELEVANT, "test input");
    }

    private static FingerprintManifest.Manifest manifest(List<FingerprintManifest.Entry> entries) {
        return new FingerprintManifest.Manifest(
            CompatibilityFingerprint.compute(entries), 1L, Set.of(), entries);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
