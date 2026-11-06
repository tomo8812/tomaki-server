package maki.chunksmithcache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public final class CompatibilityInputSelfTest {
    private static final FingerprintPolicy POLICY = FingerprintPolicy.forTesting(Set.of(), List.of());

    public static void main(String[] args) throws Exception {
        Path temp = Files.createTempDirectory("maki-cache-issue68-test");
        try {
            testZfastnoise(temp);
            testLoadControlContracts(temp);
            testHistoricalSemanticRecovery(temp);
            System.out.println("Issue #68 semantic compatibility regression checks: PASS");
        } finally {
            deleteTree(temp);
        }
    }

    private static void testZfastnoise(Path temp) throws Exception {
        String timestampA = "#\n#Thu Nov 05 07:41:10 UTC 2026\nmixin.perf.biome=true\nmixin.perf.noise=true\n";
        String timestampB = "# rewritten\n#Fri Nov 06 10:00:00 UTC 2026\nmixin.perf.noise = true\nignored.key = preserved-but-unused\nmixin.perf.biome : true\n";
        String missingTrue = "mixin.perf.noise=true\n";
        String biomeFalse = "mixin.perf.biome=false\nmixin.perf.noise=true\n";
        String invalidMeansFalse = "mixin.perf.biome=definitely-not-true\nmixin.perf.noise=true\n";

        List<FingerprintManifest.Entry> a = configEntries(temp, "zfast-a.properties", CompatibilityInput.ZFASTNOISE_PATH, timestampA);
        List<FingerprintManifest.Entry> b = configEntries(temp, "zfast-b.properties", CompatibilityInput.ZFASTNOISE_PATH, timestampB);
        List<FingerprintManifest.Entry> missing = configEntries(temp, "zfast-missing.properties", CompatibilityInput.ZFASTNOISE_PATH, missingTrue);
        List<FingerprintManifest.Entry> changed = configEntries(temp, "zfast-false.properties", CompatibilityInput.ZFASTNOISE_PATH, biomeFalse);
        List<FingerprintManifest.Entry> invalid = configEntries(temp, "zfast-invalid.properties", CompatibilityInput.ZFASTNOISE_PATH, invalidMeansFalse);

        require(fingerprint(a).equals(fingerprint(b)), "comments/order/whitespace/unknown zfastnoise keys must not invalidate equal effective values");
        require(fingerprint(a).equals(fingerprint(missing)), "missing zfastnoise true key must use the real default=true semantics");
        require(!fingerprint(a).equals(fingerprint(changed)), "effective zfastnoise value change must invalidate");
        require(fingerprint(changed).equals(fingerprint(invalid)), "Boolean.parseBoolean invalid text is effectively false in zfastnoise");

        String malformed = "mixin.perf.biome=" + "\\" + "u12ZZ\n";
        List<FingerprintManifest.Entry> parseFailure = configEntries(temp, "zfast-malformed.properties", CompatibilityInput.ZFASTNOISE_PATH, malformed);
        require(parseFailure.size() == 1, "parse failure must not create a normalized audit pair");
        require(parseFailure.getFirst().impact() == FingerprintManifest.Impact.RELEVANT, "parse failure must remain relevant");
        require(parseFailure.getFirst().reason().contains("fail-closed"), "parse failure must be logged as fail-closed");

        List<FingerprintManifest.Entry> unknownA = configEntries(temp, "unknown-a.properties", "config/unknown.properties", "# one\nx=1\n");
        List<FingerprintManifest.Entry> unknownB = configEntries(temp, "unknown-b.properties", "config/unknown.properties", "# two\nx=1\n");
        require(!fingerprint(unknownA).equals(fingerprint(unknownB)), "unknown config formatting changes must remain fail-closed relevant");

        FingerprintManifest.Manifest ma = manifest(a);
        FingerprintManifest.Manifest mb = manifest(b);
        List<String> ignored = FingerprintManifest.changedInputs(ma, mb, FingerprintManifest.Impact.IGNORED, 8);
        require(ignored.stream().anyMatch(value -> value.contains("source-format/comment changed")), "raw zfastnoise audit log must identify non-semantic source changes");
    }

    private static void testLoadControlContracts(Path temp) throws Exception {
        Path lodOld = temp.resolve("maki-lod-cpu-1.3.4.jar");
        Path lodNew = temp.resolve("maki-lod-cpu-1.4.0.jar");
        Path lodUnknown = temp.resolve("maki-lod-cpu-1.4.1.jar");
        writeModJar(lodOld, "maki_lod_cpu", "1.3.4", "old-load-control");
        writeModJar(lodNew, "maki_lod_cpu", "1.4.0", "new-shared-budget");
        writeModJar(lodUnknown, "maki_lod_cpu", "1.4.1", "future-unknown");

        List<FingerprintManifest.Entry> oldEntries = jarEntries(
            "mods/maki-lod-cpu-1.3.4.jar",
            lodOld,
            CompatibilityInput.trustedArtifactSha("maki_lod_cpu", "1.3.4")
        );
        List<FingerprintManifest.Entry> newEntries = jarEntries(
            "mods/maki-lod-cpu-1.4.0.jar",
            lodNew,
            CompatibilityInput.trustedArtifactSha("maki_lod_cpu", "1.4.0")
        );
        List<FingerprintManifest.Entry> alteredSameVersion = jarEntries("mods/maki-lod-cpu-1.4.0.jar", lodNew);
        List<FingerprintManifest.Entry> unknownEntries = jarEntries("mods/maki-lod-cpu-1.4.1.jar", lodUnknown);
        require(fingerprint(oldEntries).equals(fingerprint(newEntries)), "audited 1.3.4 -> 1.4.0 load-control update must keep compatibility");
        require(!fingerprint(newEntries).equals(fingerprint(alteredSameVersion)), "same version with unverified JAR bytes must fail closed");
        require(!fingerprint(newEntries).equals(fingerprint(unknownEntries)), "unknown future maki_lod_cpu version must fail closed");

        Path fixOld = temp.resolve("maki-lod-cpu-online-fix-1.0.2.jar");
        Path fixNew = temp.resolve("maki-lod-cpu-online-fix-1.0.3.jar");
        writeModJar(fixOld, "maki_lod_cpu_online_fix", "1.0.2", "adaptive-cap-a");
        writeModJar(fixNew, "maki_lod_cpu_online_fix", "1.0.3", "adaptive-cap-b");
        require(
            fingerprint(jarEntries(
                "mods/maki-lod-cpu-online-fix-1.0.2.jar",
                fixOld,
                CompatibilityInput.trustedArtifactSha("maki_lod_cpu_online_fix", "1.0.2")
            )).equals(fingerprint(jarEntries(
                "mods/maki-lod-cpu-online-fix-1.0.3.jar",
                fixNew,
                CompatibilityInput.trustedArtifactSha("maki_lod_cpu_online_fix", "1.0.3")
            ))),
            "audited 1.0.2 -> 1.0.3 online cap update must keep compatibility"
        );

        Path worldgenA = temp.resolve("worldgen-a.jar");
        Path worldgenB = temp.resolve("worldgen-b.jar");
        writeModJar(worldgenA, "worldgen_mod", "1.0.0", "terrain-a");
        writeModJar(worldgenB, "worldgen_mod", "1.0.1", "terrain-b");
        require(
            !fingerprint(jarEntries("mods/worldgen.jar", worldgenA)).equals(fingerprint(jarEntries("mods/worldgen.jar", worldgenB))),
            "unverified/worldgen mod changes must remain relevant"
        );

        List<String> ignored = FingerprintManifest.changedInputs(manifest(oldEntries), manifest(newEntries), FingerprintManifest.Impact.IGNORED, 8);
        require(ignored.stream().allMatch(value -> value.contains("verified non-worldgen JAR changed")), "safe JAR raw-byte audit changes must be explicit");
    }

    private static void testHistoricalSemanticRecovery(Path temp) throws Exception {
        List<FingerprintManifest.Entry> old = new ArrayList<>();
        old.add(entry("policy:compatibility", "old-policy", FingerprintManifest.Impact.RELEVANT));
        old.add(new FingerprintManifest.Entry(
            CompatibilityInput.ZFASTNOISE_PATH,
            76L,
            "802908edb8bb4c295a71796823e68320dc0e0b56f52f4d8db482eacc31bc6d3c",
            FingerprintManifest.Impact.RELEVANT,
            "worldgen/serialization input by default"
        ));
        old.add(new FingerprintManifest.Entry(
            "mods/maki-lod-cpu-1.3.4.jar",
            1L,
            CompatibilityInput.trustedArtifactSha("maki_lod_cpu", "1.3.4"),
            FingerprintManifest.Impact.RELEVANT,
            "mod ids=maki_lod_cpu; fail-closed relevant"
        ));
        old.add(new FingerprintManifest.Entry(
            "mods/maki-lod-cpu-online-fix-1.0.2.jar",
            1L,
            CompatibilityInput.trustedArtifactSha("maki_lod_cpu_online_fix", "1.0.2"),
            FingerprintManifest.Impact.RELEVANT,
            "mod ids=maki_lod_cpu_online_fix; fail-closed relevant"
        ));
        old.add(entry("mods/worldgen.jar", "same-worldgen", FingerprintManifest.Impact.RELEVANT));

        List<FingerprintManifest.Entry> current = new ArrayList<>();
        current.add(entry("policy:compatibility", "new-policy", FingerprintManifest.Impact.RELEVANT));
        current.add(CompatibilityInput.zfastnoiseCanonicalEntry(true, true));
        current.add(contractEntry(CompatibilityInput.LOD_CPU_CONTRACT_KEY, CompatibilityInput.LOD_CPU_CONTRACT));
        current.add(contractEntry(CompatibilityInput.LOD_CPU_ONLINE_FIX_CONTRACT_KEY, CompatibilityInput.LOD_CPU_ONLINE_FIX_CONTRACT));
        current.add(entry("mods/worldgen.jar", "same-worldgen", FingerprintManifest.Impact.RELEVANT));

        require(
            FingerprintManifest.sameCompatibilityInputsExcept(manifest(old), manifest(current), Set.of("policy:compatibility")),
            "known legacy timestamp + audited LOD load-control versions must be recoverable by manifest proof"
        );

        List<FingerprintManifest.Entry> changedWorldgen = new ArrayList<>(current);
        changedWorldgen.set(changedWorldgen.size() - 1, entry("mods/worldgen.jar", "changed-worldgen", FingerprintManifest.Impact.RELEVANT));
        require(
            !FingerprintManifest.sameCompatibilityInputsExcept(manifest(old), manifest(changedWorldgen), Set.of("policy:compatibility")),
            "historical recovery must reject real worldgen changes"
        );

        List<FingerprintManifest.Entry> changedConfig = new ArrayList<>(current);
        changedConfig.set(1, CompatibilityInput.zfastnoiseCanonicalEntry(false, true));
        require(
            !FingerprintManifest.sameCompatibilityInputsExcept(manifest(old), manifest(changedConfig), Set.of("policy:compatibility")),
            "historical recovery must reject changed effective zfastnoise values"
        );

        FingerprintManifest.Entry unknownLegacy = new FingerprintManifest.Entry(
            CompatibilityInput.ZFASTNOISE_PATH,
            77L,
            CompatibilityFingerprint.sha256("unknown legacy zfastnoise bytes"),
            FingerprintManifest.Impact.RELEVANT,
            "legacy raw"
        );
        List<FingerprintManifest.Entry> unknownOld = new ArrayList<>(old);
        unknownOld.set(1, unknownLegacy);
        require(
            !FingerprintManifest.sameCompatibilityInputsExcept(manifest(unknownOld), manifest(current), Set.of("policy:compatibility")),
            "unrecognized legacy config signatures must remain fail closed"
        );
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

    private static List<FingerprintManifest.Entry> jarEntries(String key, Path jar, String trustedSha256) throws Exception {
        byte[] bytes = Files.readAllBytes(jar);
        return CompatibilityInput.entries(key, jar, bytes.length, trustedSha256, POLICY);
    }

    private static FingerprintManifest.Entry contractEntry(String key, String contract) {
        byte[] bytes = contract.getBytes(StandardCharsets.UTF_8);
        return new FingerprintManifest.Entry(key, bytes.length, CompatibilityFingerprint.sha256(contract), FingerprintManifest.Impact.RELEVANT, "test contract");
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

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) return;
        try (var stream = Files.walk(root)) {
            for (Path path : stream.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
