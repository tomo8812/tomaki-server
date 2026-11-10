package maki.chunksmithcache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public final class FingerprintPolicySelfTest {
    public static void main(String[] args) throws Exception {
        Path temp = Files.createTempDirectory("maki-cache-issue40-test");
        try {
            Path observerJar = temp.resolve("diagnostic-probe.jar");
            writeModJar(observerJar, "maki_lod_cpu", "payload-a");
            Path unknownJar = temp.resolve("unknown.jar");
            writeModJar(unknownJar, "diagnostic_probe", "payload-a");
            Path worldgenJar = temp.resolve("worldgen.jar");
            writeModJar(worldgenJar, "epicterrain", "payload-a");

            FingerprintPolicy policy = FingerprintPolicy.forTesting(List.of("config/diagnostic-probe.properties"));
            require(policy.classify("mods/diagnostic-probe.jar", observerJar) == FingerprintManifest.Impact.IGNORED,
                "owned scheduling-only MOD role must remain neutral across updates");
            require(policy.classify("mods/unknown.jar", unknownJar) == FingerprintManifest.Impact.RELEVANT,
                "unknown code-only MOD must fail closed even without recognizable worldgen resources");
            require(policy.classify("mods/worldgen.jar", worldgenJar) == FingerprintManifest.Impact.RELEVANT, "known worldgen mod must remain relevant");

            // An independently audited non-worldgen binary is neutral only for
            // its exact ID+digest. Updating the audit policy must not rotate
            // generations; updating the JAR without auditing its new digest must.
            FingerprintPolicy unauditedPolicy=FingerprintPolicy.load(temp);
            String unknownSha=sha256(unknownJar);
            Path config=temp.resolve(FingerprintPolicy.CONFIG_RELATIVE);
            Files.createDirectories(config.getParent());
            Files.writeString(config,"fingerprint.audited-neutral-mod-sha256=diagnostic_probe:"+unknownSha+"\n");
            FingerprintPolicy auditedPolicy=FingerprintPolicy.load(temp);
            require(auditedPolicy.compatibilitySignature().equals(unauditedPolicy.compatibilitySignature()),
                "audit allowlist changes must not invalidate unrelated generations");
            List<FingerprintManifest.Entry> audited=CompatibilityInput.entries(
                "mods/unknown.jar",unknownJar,Files.size(unknownJar),unknownSha,auditedPolicy);
            require(audited.size()==1 && audited.getFirst().impact()==FingerprintManifest.Impact.IGNORED,
                "exact SHA/ID audited non-worldgen JAR should be neutral");
            require(CompatibilityInput.entries(
                "mods/worldgen.jar",worldgenJar,Files.size(worldgenJar),sha256(worldgenJar),auditedPolicy)
                .getFirst().impact()==FingerprintManifest.Impact.RELEVANT,
                "known worldgen MOD cannot be masked by a neutral audit");
            require(FingerprintPolicy.isAlwaysExcluded("config/chunksmith/tasks/minecraft/overworld.properties"), "Chunksmith task progress regression");

            List<FingerprintManifest.Entry> baseline = new ArrayList<>();
            baseline.add(entry("policy:compatibility", "policy", FingerprintManifest.Impact.RELEVANT));
            baseline.add(entry("mods/worldgen.jar", "wg-a", FingerprintManifest.Impact.RELEVANT));
            baseline.add(entry("mods/diagnostic-probe.jar", "diag-a", FingerprintManifest.Impact.IGNORED));
            baseline.add(entry("config/diagnostic-probe.properties", "cfg-a", FingerprintManifest.Impact.IGNORED));
            baseline.add(entry("world/datapacks/test.zip", "data-a", FingerprintManifest.Impact.RELEVANT));
            baseline.add(entry("level:seed", "123", FingerprintManifest.Impact.RELEVANT));

            String base = CompatibilityFingerprint.compute(baseline);
            List<FingerprintManifest.Entry> withAuditedMod=new ArrayList<>(baseline);
            withAuditedMod.addAll(audited);
            require(base.equals(CompatibilityFingerprint.compute(withAuditedMod)),
                "adding a verified generation-neutral MOD must preserve cache compatibility");

            // Regression: a FULL+LOD pair created before the same unknown JAR
            // was audited must be adopted after the audit without recomputation.
            List<FingerprintManifest.Entry> previouslyUnaudited = new ArrayList<>(baseline);
            FingerprintManifest.Entry originalUnknown = CompatibilityInput.entries(
                "mods/unknown.jar", unknownJar, Files.size(unknownJar), unknownSha, unauditedPolicy).getFirst();
            require(originalUnknown.impact() == FingerprintManifest.Impact.RELEVANT,
                "unapproved unknown MOD must initially be relevant");
            previouslyUnaudited.add(originalUnknown);
            FingerprintManifest.Manifest oldGeneration = new FingerprintManifest.Manifest(
                CompatibilityFingerprint.compute(previouslyUnaudited), 1L, Set.of(), previouslyUnaudited);
            FingerprintManifest.Manifest auditedGeneration = new FingerprintManifest.Manifest(
                CompatibilityFingerprint.compute(withAuditedMod), 2L, Set.of(), withAuditedMod);
            require(!oldGeneration.fingerprint().equals(auditedGeneration.fingerprint()),
                "test must cover a genuine pre-audit to audited fingerprint transition");
            require(FingerprintManifest.sameCompatibilityInputsExcept(
                oldGeneration, auditedGeneration, Set.of("policy:compatibility")),
                "an explicitly audited identical JAR must preserve the pre-audit FULL generation");
            require(FingerprintManifest.changedCompatibilityInputs(
                oldGeneration, auditedGeneration, Set.of("policy:compatibility"), 8, null).isEmpty(),
                "proven neutral audit must not appear as an incompatible generation change");
            require(FingerprintManifest.compatibilityProofs(
                oldGeneration, auditedGeneration, Set.of("policy:compatibility"), 8, null)
                .stream().anyMatch(note -> note.contains("now explicitly audited")),
                "cache adoption must document why historical relevance is safely disregarded");

            FingerprintManifest.Entry tamperedAudit = new FingerprintManifest.Entry(
                audited.getFirst().key(), audited.getFirst().size(), CompatibilityFingerprint.sha256("different-binary"),
                FingerprintManifest.Impact.IGNORED, CompatibilityInput.EXPLICIT_NEUTRAL_AUDIT);
            List<FingerprintManifest.Entry> modifiedBinary = new ArrayList<>(baseline);
            modifiedBinary.add(tamperedAudit);
            require(!FingerprintManifest.sameCompatibilityInputsExcept(
                oldGeneration, manifestFor(modifiedBinary), Set.of("policy:compatibility")),
                "an audit for different bytes must not adopt the earlier generation");
            List<FingerprintManifest.Entry> formerlySensitive = new ArrayList<>(baseline);
            formerlySensitive.add(new FingerprintManifest.Entry(
                originalUnknown.key(), originalUnknown.size(), originalUnknown.sha256(),
                FingerprintManifest.Impact.RELEVANT, "generation-sensitive MOD id=epicterrain"));
            require(!FingerprintManifest.sameCompatibilityInputsExcept(
                manifestFor(formerlySensitive), auditedGeneration, Set.of("policy:compatibility")),
                "audits must not override a previously known generation-sensitive input");
            require(!FingerprintManifest.sameCompatibilityInputsExcept(
                oldGeneration, manifestFor(replace(withAuditedMod, "mods/worldgen.jar", "wg-b")),
                Set.of("policy:compatibility")),
                "a matching neutral audit must not mask unrelated worldgen changes");

            writeModJar(unknownJar,"diagnostic_probe","payload-b");
            String updatedSha=sha256(unknownJar);
            require(CompatibilityInput.entries("mods/unknown.jar",unknownJar,Files.size(unknownJar),
                updatedSha,auditedPolicy).getFirst().impact()==FingerprintManifest.Impact.RELEVANT,
                "modified unknown JAR without a fresh audit must fail closed");
            Files.writeString(config,"fingerprint.audited-neutral-mod-sha256=diagnostic_probe:"+updatedSha+"\n");
            FingerprintPolicy updatedAuditPolicy=FingerprintPolicy.load(temp);
            List<FingerprintManifest.Entry> newlyAudited=CompatibilityInput.entries(
                "mods/unknown.jar",unknownJar,Files.size(unknownJar),updatedSha,updatedAuditPolicy);
            require(newlyAudited.getFirst().impact()==FingerprintManifest.Impact.IGNORED
                && updatedAuditPolicy.compatibilitySignature().equals(auditedPolicy.compatibilitySignature()),
                "newly audited neutral update must remain generation-compatible");
            List<FingerprintManifest.Entry> withUpdatedAuditedMod=new ArrayList<>(baseline);
            withUpdatedAuditedMod.addAll(newlyAudited);
            require(base.equals(CompatibilityFingerprint.compute(withUpdatedAuditedMod)),
                "audited update must not rotate the known-good FULL cache");
            require(base.equals(CompatibilityFingerprint.compute(replace(baseline, "mods/diagnostic-probe.jar", "diag-b"))), "observation-only JAR change must not invalidate");
            require(base.equals(CompatibilityFingerprint.compute(replace(baseline, "config/diagnostic-probe.properties", "cfg-b"))), "explicit non-worldgen config change must not invalidate");
            require(!base.equals(CompatibilityFingerprint.compute(replace(baseline, "mods/worldgen.jar", "wg-b"))), "worldgen mod change must invalidate");
            require(!base.equals(CompatibilityFingerprint.compute(replace(baseline, "world/datapacks/test.zip", "data-b"))), "datapack change must invalidate");
            require(!base.equals(CompatibilityFingerprint.compute(replace(baseline, "level:seed", "124"))), "seed change must invalidate");

            List<FingerprintManifest.Entry> renamedObserver = new ArrayList<>();
            renamedObserver.add(entry("policy:compatibility", "policy-renamed-observer", FingerprintManifest.Impact.RELEVANT));
            renamedObserver.add(entry("mods/worldgen.jar", "wg-a", FingerprintManifest.Impact.RELEVANT));
            renamedObserver.add(entry("mods/maki-mods-observer.jar", "diag-b", FingerprintManifest.Impact.IGNORED));
            renamedObserver.add(entry("config/maki-mods-observer.properties", "cfg-b", FingerprintManifest.Impact.IGNORED));
            renamedObserver.add(entry("world/datapacks/test.zip", "data-a", FingerprintManifest.Impact.RELEVANT));
            renamedObserver.add(entry("level:seed", "123", FingerprintManifest.Impact.RELEVANT));
            FingerprintManifest.Manifest oldPolicyManifest = new FingerprintManifest.Manifest(base, 1L, Set.of(base), baseline);
            FingerprintManifest.Manifest renamedPolicyManifest = new FingerprintManifest.Manifest(
                CompatibilityFingerprint.compute(renamedObserver), 2L, Set.of(), renamedObserver);
            require(
                FingerprintManifest.sameRelevantInputsExcept(oldPolicyManifest, renamedPolicyManifest, Set.of("policy:compatibility")),
                "diagnostic observer rename must preserve relevant cache inputs"
            );
            List<FingerprintManifest.Entry> changedWorldgen = replace(renamedObserver, "mods/worldgen.jar", "wg-b");
            require(
                !FingerprintManifest.sameRelevantInputsExcept(
                    oldPolicyManifest,
                    new FingerprintManifest.Manifest(CompatibilityFingerprint.compute(changedWorldgen), 3L, Set.of(), changedWorldgen),
                    Set.of("policy:compatibility")
                ),
                "worldgen change must not be adopted as policy-equivalent"
            );

            FingerprintManifest.Manifest manifest = new FingerprintManifest.Manifest(base, 1L, Set.of(base), baseline);
            String encoded = FingerprintManifest.encode(manifest);
            FingerprintManifest.Manifest decoded = FingerprintManifest.decode(encoded);
            require(decoded.fingerprint().equals(base) && decoded.entries().size() == baseline.size(), "manifest round-trip");
            boolean corruptRejected = false;
            try {
                FingerprintManifest.decode(encoded.replaceFirst("entry\\t[^\\t]+", "entry\t%%%"));
            } catch (IOException expected) {
                corruptRejected = true;
            }
            require(corruptRejected, "corrupt manifest must fail closed");

            writeModJar(observerJar, "maki_lod_cpu", "payload-b");
            require(policy.classify("mods/diagnostic-probe.jar", observerJar) == FingerprintManifest.Impact.IGNORED,
                "owned neutral MOD byte updates must not invalidate generated-result compatibility");
            System.out.println("Issue #40 fingerprint/quarantine regression checks: PASS");
        } finally {
            deleteTree(temp);
        }
    }

    private static FingerprintManifest.Manifest manifestFor(List<FingerprintManifest.Entry> entries) {
        String fingerprint = CompatibilityFingerprint.compute(entries);
        return new FingerprintManifest.Manifest(fingerprint, 1L, Set.of(fingerprint), entries);
    }

    private static FingerprintManifest.Entry entry(String key, String value, FingerprintManifest.Impact impact) {
        return new FingerprintManifest.Entry(key, value.length(), CompatibilityFingerprint.sha256(value), impact, impact == FingerprintManifest.Impact.RELEVANT ? "test relevant" : "test ignored");
    }

    private static List<FingerprintManifest.Entry> replace(List<FingerprintManifest.Entry> source, String key, String value) {
        List<FingerprintManifest.Entry> result = new ArrayList<>();
        for (FingerprintManifest.Entry entry : source) {
            result.add(entry.key().equals(key) ? entry(key, value, entry.impact()) : entry);
        }
        return result;
    }

    private static String sha256(Path path) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(path)));
    }

    private static void writeModJar(Path path, String modId, String payload) throws IOException {
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(path))) {
            jar.putNextEntry(new JarEntry("META-INF/neoforge.mods.toml"));
            jar.write(("modLoader=\"javafml\"\n[[mods]]\nmodId=\"" + modId + "\"\nversion=\"1\"\n[[dependencies." + modId + "]]\nmodId=\"minecraft\"\n").getBytes(StandardCharsets.UTF_8));
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
