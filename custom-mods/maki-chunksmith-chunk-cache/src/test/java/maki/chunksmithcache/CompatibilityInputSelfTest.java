package maki.chunksmithcache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public final class CompatibilityInputSelfTest {
    private static final FingerprintPolicy POLICY = FingerprintPolicy.forTesting(List.of());

    public static void main(String[] args) throws Exception {
        Path temp = Files.createTempDirectory("maki-cache-issue68-test");
        try {
            testZfastnoise(temp);
            testAuditedModContracts(temp);
            testInstalledModAudit(temp);
            testHistoricalSemanticRecovery();
            testReAdoptionConflictPolicy(temp);
            System.out.println("Issue #68 semantic compatibility regression checks: PASS");
        } finally {
            deleteTree(temp);
        }
    }

    private static void testZfastnoise(Path temp) throws Exception {
        String a = "#\n#Thu Nov 05 07:41:10 UTC 2026\nmixin.perf.biome=true\nmixin.perf.noise=true\n";
        String b = "# rewritten\n#Fri Nov 06 10:00:00 UTC 2026\nmixin.perf.noise = true\nignored.key = x\nmixin.perf.biome : true\n";
        String missingTrue = "mixin.perf.noise=true\n";
        String biomeFalse = "mixin.perf.biome=false\nmixin.perf.noise=true\n";
        String invalidFalse = "mixin.perf.biome=not-true\nmixin.perf.noise=true\n";

        List<FingerprintManifest.Entry> ea = configEntries(temp, "a.properties", CompatibilityInput.ZFASTNOISE_PATH, a);
        List<FingerprintManifest.Entry> eb = configEntries(temp, "b.properties", CompatibilityInput.ZFASTNOISE_PATH, b);
        List<FingerprintManifest.Entry> missing = configEntries(temp, "missing.properties", CompatibilityInput.ZFASTNOISE_PATH, missingTrue);
        List<FingerprintManifest.Entry> changed = configEntries(temp, "false.properties", CompatibilityInput.ZFASTNOISE_PATH, biomeFalse);
        List<FingerprintManifest.Entry> invalid = configEntries(temp, "invalid.properties", CompatibilityInput.ZFASTNOISE_PATH, invalidFalse);

        require(fingerprint(ea).equals(fingerprint(eb)), "comments/order/whitespace/unknown zfastnoise keys must not invalidate equal effective values");
        require(fingerprint(ea).equals(fingerprint(missing)), "removing an explicit true key must preserve the actual default=true semantics");
        require(!fingerprint(changed).equals(fingerprint(missing)), "removing an explicit false key must invalidate because the effective value becomes true");
        require(fingerprint(changed).equals(fingerprint(invalid)), "Boolean.parseBoolean invalid text is effectively false in zfastnoise");

        String malformed = "mixin.perf.biome=" + "\\" + "u12ZZ\n";
        List<FingerprintManifest.Entry> failed = configEntries(temp, "malformed.properties", CompatibilityInput.ZFASTNOISE_PATH, malformed);
        require(failed.size() == 1 && failed.getFirst().impact() == FingerprintManifest.Impact.RELEVANT, "parse failure must fail closed");
        require(failed.getFirst().reason().contains("fail-closed"), "parse failure reason must be explicit");

        List<FingerprintManifest.Entry> unknownA = configEntries(temp, "u1.properties", "config/unknown.properties", "# one\nx=1\n");
        List<FingerprintManifest.Entry> unknownB = configEntries(temp, "u2.properties", "config/unknown.properties", "# two\nx=1\n");
        require(!fingerprint(unknownA).equals(fingerprint(unknownB)), "unknown config formatting remains fail-closed relevant");

        List<String> ignored = FingerprintManifest.changedInputs(manifest(ea), manifest(eb), FingerprintManifest.Impact.IGNORED, 8);
        require(ignored.stream().anyMatch(v -> v.contains("source-format/comment changed")), "zfastnoise audit must explain source-only changes");
    }

    private static void testAuditedModContracts(Path temp) throws Exception {
        Path unknownLod = temp.resolve("maki-lod-cpu-9.9.9.jar");
        writeModJar(unknownLod, "maki_lod_cpu", "9.9.9", "future-code");
        List<FingerprintManifest.Entry> unknown = jarEntries("mods/maki-lod-cpu-9.9.9.jar", unknownLod);
        require(unknown.size() == 1 && unknown.getFirst().impact() == FingerprintManifest.Impact.IGNORED,
            "source-owned generation-neutral MOD updates must not rotate cache generations");

        String millenaire105Name = "maki-millenaire-lod-compat-1.0.5.jar";
        String millenaire106Name = "maki-millenaire-lod-compat-1.0.6.jar";
        String millenaire105Sha = AuditedModCompatibility.trustedMillenaireRawSha(millenaire105Name);
        String millenaire106Sha = AuditedModCompatibility.trustedMillenaireRawSha(millenaire106Name);
        require(millenaire105Sha != null && millenaire106Sha != null && !millenaire105Sha.equals(millenaire106Sha),
            "Millenaire 1.0.5/1.0.6 must have distinct exact audited artifact hashes");

        List<FingerprintManifest.Entry> millenaireContract = List.of(
            contractEntry("maki_millenaire_lod", "millenaire-disposable-generation-v1")
        );
        require(FingerprintManifest.sameCompatibilityInputsExcept(
                manifest(List.of(rawJarEntry("mods/" + millenaire105Name, millenaire105Sha))),
                manifest(millenaireContract), Set.of()),
            "exact audited Millenaire 1.0.5 may normalize to the shared generated-result contract");
        require(FingerprintManifest.sameCompatibilityInputsExcept(
                manifest(List.of(rawJarEntry("mods/" + millenaire106Name, millenaire106Sha))),
                manifest(millenaireContract), Set.of()),
            "exact audited Millenaire 1.0.6 may normalize to the shared generated-result contract");

        Path tamperedMillenaire = temp.resolve(millenaire106Name);
        writeModJar(tamperedMillenaire, "maki_millenaire_lod", "1.0.6", "unreviewed generation change");
        List<FingerprintManifest.Entry> tamperedMillenaireEntries =
            jarEntries("mods/" + millenaire106Name, tamperedMillenaire);
        require(tamperedMillenaireEntries.size() == 1
                && tamperedMillenaireEntries.getFirst().impact() == FingerprintManifest.Impact.RELEVANT,
            "same Millenaire filename/version with unreviewed bytes must fail closed");
        require(!FingerprintManifest.sameCompatibilityInputsExcept(
                manifest(List.of(rawJarEntry("mods/" + millenaire106Name, sha256("tampered".getBytes(StandardCharsets.UTF_8))))),
                manifest(millenaireContract), Set.of()),
            "historical Millenaire filename alone cannot prove the shared generation contract");

        Path newQol = temp.resolve("example-qol.jar");
        writeModJar(newQol, "example_qol", "1.0.0", "ui-only");
        byte[] qolBytes = Files.readAllBytes(newQol);
        FingerprintPolicy qolPolicy = FingerprintPolicy.forTesting(Set.of("example_qol"), List.of());
        List<FingerprintManifest.Entry> qol = CompatibilityInput.entries(
            "mods/example-qol.jar", newQol, qolBytes.length, sha256(qolBytes), qolPolicy
        );
        require(qol.size() == 1 && qol.getFirst().impact() == FingerprintManifest.Impact.IGNORED,
            "an explicitly audited new QoL MOD must not become a generation input");

        Path indirectOverride = temp.resolve("maki-lod-cpu-10.0.0.jar");
        writeOverrideModJar(indirectOverride, "maki_lod_cpu", "10.0.0", "future-code");
        List<FingerprintManifest.Entry> overrideEntries = jarEntries(
            "mods/maki-lod-cpu-10.0.0.jar", indirectOverride
        );
        require(overrideEntries.size() == 1
                && overrideEntries.getFirst().impact() == FingerprintManifest.Impact.RELEVANT,
            "a generation override must fail closed even for a normally generation-neutral MOD role");

        Path protectedWorldgen = temp.resolve("zfastnoise-future.jar");
        writeModJar(protectedWorldgen, "zfastnoise", "9.9.9", "terrain-change");
        byte[] protectedBytes = Files.readAllBytes(protectedWorldgen);
        List<FingerprintManifest.Entry> protectedEntries = CompatibilityInput.entries(
            "mods/zfastnoise-future.jar", protectedWorldgen, protectedBytes.length, sha256(protectedBytes),
            FingerprintPolicy.forTesting(Set.of("zfastnoise"), List.of())
        );
        require(protectedEntries.getFirst().impact() == FingerprintManifest.Impact.RELEVANT,
            "known generation-sensitive MOD IDs cannot be operator-exempted");

        Path unknownWorldgen = temp.resolve("worldgen.jar");
        writeModJar(unknownWorldgen, "worldgen_mod", "1.0.1", "terrain-change");
        require(jarEntries("mods/worldgen.jar", unknownWorldgen).getFirst().impact() == FingerprintManifest.Impact.RELEVANT,
            "unknown/worldgen mod must remain relevant");
    }

    private static void testInstalledModAudit(Path temp) throws Exception {
        String configured = System.getProperty("issue68.modsDir");
        if (configured == null || configured.isBlank()) return;
        Path mods = Path.of(configured);

        assertAudited(mods.resolve("maki-lod-cpu-1.4.1.jar"), "maki_lod_cpu");
        assertAudited(mods.resolve("maki-lod-cpu-online-fix-1.0.3.jar"), "maki_lod_cpu_online_fix");
        assertAudited(mods.resolve("maki-diorama-share-1.0.1.jar"), "maki_diorama_share");
        assertAudited(mods.resolve("maki-mods-observer-1.0.0.jar"), "maki_mods_observer");
        Path jade = mods.resolve("Jade-1.21.1-NeoForge-15.10.6.jar");
        assertAudited(jade, "jade");
        assertAudited(mods.resolve("emi-1.1.24+1.21.1+neoforge.jar"), "emi");
        assertAudited(mods.resolve("clientsort-neoforge-3.89.2+1.21.1.jar"), "clientsort");
        assertAudited(mods.resolve("sound-physics-remastered-neoforge-1.21.1-1.5.2.jar"), "sound_physics_remastered");
        assertMillenaireAudited(mods.resolve("maki-millenaire-lod-compat-1.0.6.jar"));

        Path repacked = temp.resolve("jade-renamed-repacked.jar");
        repack(jade, repacked, false);
        List<FingerprintManifest.Entry> original = jarEntries("mods/Jade-1.21.1-NeoForge-15.10.6.jar", jade);
        List<FingerprintManifest.Entry> renamed = jarEntries("mods/Jade-renamed.jar", repacked);
        require(fingerprint(original).equals(fingerprint(renamed)),
            "audited distributed mod rename/repack with identical entry contents must preserve compatibility");

        Path altered = temp.resolve("jade-altered.jar");
        repack(jade, altered, true);
        List<FingerprintManifest.Entry> alteredEntries = jarEntries("mods/Jade-future.jar", altered);
        require(alteredEntries.size() == 1 && alteredEntries.getFirst().impact() == FingerprintManifest.Impact.IGNORED,
            "role-audited UI MOD content updates must remain generation-neutral");

        for (String name : List.of(
            "zfastnoise-1.0.13+1.21.1+neoforge.jar",
            "epicterrain-0.3.0-1.21.1.jar",
            "millenaire-9.0.3.jar",
            "worldgenfeaturefix-neoforge-1.21-1.0.1.jar"
        )) {
            Path jar = mods.resolve(name);
            List<FingerprintManifest.Entry> entries = jarEntries("mods/" + name, jar);
            require(entries.size() == 1 && entries.getFirst().impact() == FingerprintManifest.Impact.RELEVANT,
                name + " must remain fail-closed relevant");
        }
    }

    private static void testHistoricalSemanticRecovery() {
        List<FingerprintManifest.Entry> old = new ArrayList<>();
        old.add(entry("policy:compatibility", "old-policy", FingerprintManifest.Impact.RELEVANT));
        old.add(new FingerprintManifest.Entry(
            CompatibilityInput.ZFASTNOISE_PATH, 76L,
            "802908edb8bb4c295a71796823e68320dc0e0b56f52f4d8db482eacc31bc6d3c",
            FingerprintManifest.Impact.RELEVANT, "legacy zfastnoise"
        ));
        old.add(rawJarEntry("mods/maki-lod-cpu-1.3.4.jar",
            AuditedModCompatibility.trustedRawSha("maki_lod_cpu", "load-control-only-v1", 0)));
        old.add(rawJarEntry("mods/maki-lod-cpu-online-fix-1.0.2.jar",
            AuditedModCompatibility.trustedRawSha("maki_lod_cpu_online_fix", "adaptive-cap-only-v1", 0)));
        old.add(rawJarEntry("mods/maki-diorama-share-1.0.0.jar",
            AuditedModCompatibility.trustedRawSha("maki_diorama_share", "waypoint-sharing-only-v1", 0)));
        old.add(entry("mods/worldgen.jar", "same-worldgen", FingerprintManifest.Impact.RELEVANT));

        List<FingerprintManifest.Entry> current = new ArrayList<>();
        current.add(entry("policy:compatibility", "new-policy", FingerprintManifest.Impact.RELEVANT));
        current.add(CompatibilityInput.zfastnoiseCanonicalEntry(true, true));
        // These source-owned workload/QoL MODs are no longer generated-result inputs.
        // Their historical exact attestations normalize away rather than becoming
        // relevant compatibility contracts in the current generation.
        current.add(entry("mods/worldgen.jar", "same-worldgen", FingerprintManifest.Impact.RELEVANT));

        require(FingerprintManifest.sameCompatibilityInputsExcept(manifest(old), manifest(current), Set.of("policy:compatibility")),
            "historical manifests for audited neutral updates must be recoverable without hiding worldgen changes");

        List<FingerprintManifest.Entry> changedWorldgen = new ArrayList<>(current);
        changedWorldgen.set(changedWorldgen.size() - 1, entry("mods/worldgen.jar", "changed-worldgen", FingerprintManifest.Impact.RELEVANT));
        require(!FingerprintManifest.sameCompatibilityInputsExcept(manifest(old), manifest(changedWorldgen), Set.of("policy:compatibility")),
            "historical recovery must reject real worldgen changes");

        String raw = "#\n#Fri Nov 06 12:34:56 UTC 2026\nmixin.perf.biome=true\nmixin.perf.noise=true\n";
        byte[] bytes = raw.getBytes(StandardCharsets.UTF_8);
        List<FingerprintManifest.Entry> oldRaw = new ArrayList<>(old);
        oldRaw.set(1, new FingerprintManifest.Entry(CompatibilityInput.ZFASTNOISE_PATH, bytes.length, sha256(bytes),
            FingerprintManifest.Impact.RELEVANT, "legacy raw zfastnoise"));
        List<FingerprintManifest.Entry> currentRaw = new ArrayList<>(current);
        currentRaw.add(new FingerprintManifest.Entry(CompatibilityInput.RAW_AUDIT_PREFIX + CompatibilityInput.ZFASTNOISE_PATH,
            bytes.length, sha256(bytes), FingerprintManifest.Impact.IGNORED, "current raw zfastnoise audit"));
        require(FingerprintManifest.sameCompatibilityInputsExcept(manifest(oldRaw), manifest(currentRaw), Set.of("policy:compatibility")),
            "an old raw zfastnoise entry may migrate only when it exactly matches the currently parsed source");
    }

    private static void testReAdoptionConflictPolicy(Path temp) throws Exception {
        Path source = temp.resolve("quarantine-source");
        Path active = temp.resolve("active-generation");
        Files.createDirectories(source);
        Files.createDirectories(active);
        Files.writeString(source.resolve("same.nbt.gz"), "quarantined-old");
        Files.writeString(active.resolve("same.nbt.gz"), "active-new");
        Files.writeString(source.resolve("old-only.nbt.gz"), "recover-me");

        CacheDirectoryMerge.merge(source, active);

        require(Files.readString(active.resolve("same.nbt.gz")).equals("active-new"),
            "re-adoption must never overwrite an already active cache entry");
        require(Files.readString(active.resolve("old-only.nbt.gz")).equals("recover-me"),
            "non-conflicting quarantined cache entries must be recovered");
        require(!Files.exists(source), "successfully merged quarantine source must be removed");
    }

    private static void assertMillenaireAudited(Path jar) throws Exception {
        require(Files.isRegularFile(jar), "missing installed Millenaire compatibility JAR " + jar);
        String rawSha = sha256(Files.readAllBytes(jar));
        require(rawSha.equals(AuditedModCompatibility.trustedMillenaireRawSha(jar.getFileName().toString())),
            "installed Millenaire compatibility JAR must match its exact reviewed artifact hash");
        List<FingerprintManifest.Entry> entries = jarEntries("mods/" + jar.getFileName(), jar);
        require(entries.size() == 2
                && entries.getFirst().key().equals("compat:mod:maki_millenaire_lod")
                && entries.getFirst().impact() == FingerprintManifest.Impact.RELEVANT
                && entries.get(1).key().equals(AuditedModCompatibility.RAW_AUDIT_PREFIX + "maki_millenaire_lod")
                && entries.get(1).impact() == FingerprintManifest.Impact.IGNORED,
            "installed Millenaire compatibility JAR must resolve to audited contract + raw audit");
    }

    private static void assertAudited(Path jar, String modId) throws Exception {
        require(Files.isRegularFile(jar), "missing installed test JAR " + jar);
        List<FingerprintManifest.Entry> entries = jarEntries("mods/" + jar.getFileName(), jar);
        require(entries.size() == 1, jar.getFileName() + " must resolve to one raw audit entry");
        require(entries.getFirst().key().equals(AuditedModCompatibility.RAW_AUDIT_PREFIX + modId)
            && entries.getFirst().impact() == FingerprintManifest.Impact.IGNORED,
            jar.getFileName() + " must remain generation-neutral across content updates");
    }

    private static List<FingerprintManifest.Entry> configEntries(Path temp, String fileName, String key, String content) throws Exception {
        Path file = temp.resolve(fileName);
        Files.writeString(file, content, StandardCharsets.UTF_8);
        byte[] bytes = Files.readAllBytes(file);
        return CompatibilityInput.entries(key, file, bytes.length, sha256(bytes), POLICY);
    }

    private static List<FingerprintManifest.Entry> jarEntries(String key, Path jar) throws Exception {
        byte[] bytes = Files.readAllBytes(jar);
        return CompatibilityInput.entries(key, jar, bytes.length, sha256(bytes), POLICY);
    }

    private static FingerprintManifest.Entry rawJarEntry(String key, String sha) {
        require(sha != null, "missing attested historical hash");
        return new FingerprintManifest.Entry(key, 1L, sha, FingerprintManifest.Impact.RELEVANT, "legacy raw JAR");
    }

    private static FingerprintManifest.Entry contractEntry(String modId, String contract) {
        byte[] bytes = contract.getBytes(StandardCharsets.UTF_8);
        return new FingerprintManifest.Entry("compat:mod:" + modId, bytes.length, CompatibilityFingerprint.sha256(contract),
            FingerprintManifest.Impact.RELEVANT, "test contract");
    }

    private static FingerprintManifest.Entry entry(String key, String value, FingerprintManifest.Impact impact) {
        return new FingerprintManifest.Entry(key, value.length(), CompatibilityFingerprint.sha256(value), impact, "test");
    }

    private static FingerprintManifest.Manifest manifest(List<FingerprintManifest.Entry> entries) {
        return new FingerprintManifest.Manifest("0123456789abcdef01234567", 1L, Set.of("0123456789abcdef01234567"), entries);
    }

    private static String fingerprint(List<FingerprintManifest.Entry> entries) {
        return CompatibilityFingerprint.compute(entries);
    }

    private static String sha256(byte[] bytes) {
        MessageDigest digest = CompatibilityFingerprint.digest();
        digest.update(bytes);
        return CompatibilityFingerprint.hex(digest.digest());
    }

    private static void writeModJar(Path path, String modId, String version, String payload) throws IOException {
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(path))) {
            jar.putNextEntry(new JarEntry("META-INF/neoforge.mods.toml"));
            jar.write(("modLoader=\"javafml\"\n[[mods]]\nmodId=\"" + modId + "\"\nversion=\"" + version + "\"\n").getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
            jar.putNextEntry(new JarEntry("payload.txt"));
            jar.write(payload.getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
        }
    }

    private static void writeOverrideModJar(Path path, String modId, String version, String payload) throws IOException {
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(path))) {
            jar.putNextEntry(new JarEntry("META-INF/neoforge.mods.toml"));
            jar.write(("modLoader=\"javafml\"\n[[mods]]\nmodId=\"" + modId + "\"\nversion=\"" + version
                + "\"\n# zfastnoise:overrides\n").getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
            jar.putNextEntry(new JarEntry("payload.txt"));
            jar.write(payload.getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
        }
    }

    private static void repack(Path source, Path target, boolean alter) throws IOException {
        try (JarFile input = new JarFile(source.toFile());
             JarOutputStream output = new JarOutputStream(Files.newOutputStream(target))) {
            List<JarEntry> entries = input.stream().filter(e -> !e.isDirectory()).sorted(Comparator.comparing(JarEntry::getName).reversed()).toList();
            for (JarEntry sourceEntry : entries) {
                JarEntry copy = new JarEntry(sourceEntry.getName());
                copy.setTime(315532800000L);
                output.putNextEntry(copy);
                try (InputStream stream = input.getInputStream(sourceEntry)) {
                    stream.transferTo(output);
                }
                output.closeEntry();
            }
            if (alter) {
                JarEntry extra = new JarEntry("issue68-content-change.txt");
                extra.setTime(315532800000L);
                output.putNextEntry(extra);
                output.write("changed".getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
            }
        }
    }

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) return;
        try (var stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
