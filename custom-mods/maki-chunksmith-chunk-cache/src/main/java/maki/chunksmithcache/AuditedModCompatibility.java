package maki.chunksmithcache;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

final class AuditedModCompatibility {
    static final String RAW_AUDIT_PREFIX = "audit:mod:";

    private static final List<Attestation> ATTESTATIONS = List.of(
        new Attestation(
            "maki_lod_cpu",
            "04c3a6f458550d15e4b8c736c597e376c1825cfb533a66181ff142c383fb7ed2",
            "adc14268a9e182ea2a633c5d48bd0c79b8e584d02b83a411bb4c26ef60bb39d1",
            "load-control-only-v1",
            "1.3.4 audited as Chunksmith/DH load-control only"
        ),
        new Attestation(
            "maki_lod_cpu",
            "2cd157f1b78378f41863e6cfdfe545c3763dc3ddb1f0bce3cbfa6d9fd1d77799",
            "9e217a70f66ef59ef80b793be551d6d7c4a8c48c048de9dc9f612aded50afb08",
            "load-control-only-v1",
            "1.4.0 audited as Chunksmith/DH load-control only"
        ),
        new Attestation(
            "maki_lod_cpu_online_fix",
            "833defdc614826804dcbf8f818827e6baf0a6d69c5f4e9b0946b9a2609d413f1",
            "47014b5ef8263cf772a8f8b75dc8116426826b4eae9e95fb0a94755da8c14d52",
            "adaptive-cap-only-v1",
            "1.0.2 audited as adaptive load-cap correction only"
        ),
        new Attestation(
            "maki_lod_cpu_online_fix",
            "2217e4f0267b8786016e5f2ddbf6491eb4165f37e17439f23b7003f1cd1de206",
            "985853d8403f31501242852eb84c292f6ea5317e58932f2e6e5097a033d3b987",
            "adaptive-cap-only-v1",
            "1.0.3 audited as adaptive load-cap correction only"
        ),
        new Attestation(
            "maki_diorama_share",
            "43a3d604ff3b65c1f198d877575cafa539bdf6972281cb4afbe9980675aa2939",
            "34db18c58af1e8edb508aad2b8c6d6a383abff24dc774f323b91f2e3bdf4c83d",
            "waypoint-sharing-only-v1",
            "1.0.0 audited as Diorama waypoint sharing only"
        ),
        new Attestation(
            "maki_diorama_share",
            "856c638123c658cf3ece04e8dd83b0557aa80dab3463dbfd92bcc34b01971a3a",
            "7e53d08d1d94b5e95e878e5eec535f5a58713d83c87b9d61d1d45e2ac4e942ad",
            "waypoint-sharing-only-v1",
            "1.0.1 audited as Diorama waypoint sharing/persistence only"
        ),
        new Attestation(
            "maki_mods_observer",
            "b80fa342b8b932843e6ad0cd1d338895b6b4300f7a78dd035ddfc1f71cede362",
            "0b51a00d4d795217f8369cd41f8b87842f0d1471889d0d12ae69e6af457d69aa",
            "diagnostics-only-v1",
            "1.0.0 audited as bounded diagnostics/observability only"
        ),
        new Attestation(
            "jade",
            "276f1f65a20d04f536989c4f311949d770b52c440c23c2b904a08860f3c93810",
            "6dbe4660561b91cd57e28f32baa01f17e66aacc642a39936cbf5afbb0abc86f5",
            "overlay-inspection-only-v1",
            "15.10.6 audited from decompiled mixins/providers as inspection overlay only"
        ),
        new Attestation(
            "emi",
            "b68691f94f0727fc517cac2ab55bd6658d1179cc8cc715fe610b60c37469bc4c",
            "b223f13b65f6db10cf3d7e8998088a3bf1436d1e8d10f624a63116db1cb46f9e",
            "recipe-ui-only-v1",
            "1.1.24 audited from decompiled mixins as recipe/inventory UI only"
        ),
        new Attestation(
            "clientsort",
            "de14d1671e25b5446b7a9cd17f3693202ddb78351675d166e2723cbeef7c27e7",
            "cf16a24ab68acf26441033845a7462d88fd5181faecd3c59c477b0678ebe4640",
            "inventory-sorting-only-v1",
            "3.89.2 audited from decompiled mixins as inventory sorting only"
        ),
        new Attestation(
            "sound_physics_remastered",
            "108cac1fe49d0dc210183d69787db5a359cdc03c5b580eb8ab0208be7f072982",
            "9dd3f82c2f2df85bd2df7d60d78f6c84d0552ea621ad72a9a357560da7386318",
            "client-audio-only-v1",
            "1.5.2 audited from decompiled mixins as client acoustic rendering only"
        )
    );

    private AuditedModCompatibility() {}

    static List<FingerprintManifest.Entry> entries(Path jarPath, long rawSize, String rawSha256) {
        Set<String> ids = FingerprintPolicy.readTopLevelModIds(jarPath);
        if (ids.size() != 1) return null;
        String modId = ids.iterator().next();
        List<Attestation> candidates = ATTESTATIONS.stream().filter(value -> value.modId().equals(modId)).toList();
        if (candidates.isEmpty() || declaresZfastnoiseOverride(jarPath)) return null;

        Attestation match = candidates.stream().filter(value -> value.rawSha256().equals(rawSha256)).findFirst().orElse(null);
        if (match == null) {
            String canonical;
            try {
                canonical = canonicalJarSha256(jarPath);
            } catch (Throwable ignored) {
                return null;
            }
            match = candidates.stream().filter(value -> value.canonicalSha256().equals(canonical)).findFirst().orElse(null);
        }
        if (match == null) return null;

        FingerprintManifest.Entry contract = contractEntry(match.modId(), match.contract(), match.reason());
        FingerprintManifest.Entry audit = new FingerprintManifest.Entry(
            RAW_AUDIT_PREFIX + match.modId(),
            rawSize,
            rawSha256,
            FingerprintManifest.Impact.IGNORED,
            "audited generation-neutral JAR bytes; " + match.reason() + "; source=" + jarPath.getFileName()
        );
        return List.of(contract, audit);
    }

    static FingerprintManifest.Entry normalizeHistoricalEntry(FingerprintManifest.Entry entry) {
        if (!entry.key().startsWith("mods/")) return entry;
        Attestation match = ATTESTATIONS.stream()
            .filter(value -> value.rawSha256().equals(entry.sha256()))
            .findFirst()
            .orElse(null);
        return match == null ? entry : contractEntry(match.modId(), match.contract(), "historical audited artifact; " + match.reason());
    }

    static String trustedRawSha(String modId, String contract, int occurrence) {
        List<Attestation> matches = ATTESTATIONS.stream()
            .filter(value -> value.modId().equals(modId) && value.contract().equals(contract))
            .toList();
        return occurrence < 0 || occurrence >= matches.size() ? null : matches.get(occurrence).rawSha256();
    }

    static String policySignature() {
        List<String> values = ATTESTATIONS.stream()
            .map(value -> value.modId() + "@" + value.rawSha256() + ":" + value.canonicalSha256() + "->" + value.contract())
            .sorted()
            .toList();
        return "audited-mod-contracts-v1|" + String.join("|", values);
    }

    static String canonicalJarSha256(Path jarPath) throws Exception {
        List<JarEntry> entries = new ArrayList<>();
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            jar.stream()
                .filter(entry -> !entry.isDirectory())
                .filter(entry -> !isSignatureEntry(entry.getName()))
                .forEach(entries::add);
            entries.sort(Comparator.comparing(JarEntry::getName));

            var digest = CompatibilityFingerprint.digest();
            for (JarEntry entry : entries) {
                byte[] name = entry.getName().getBytes(StandardCharsets.UTF_8);
                byte[] payload;
                try (InputStream input = jar.getInputStream(entry)) {
                    payload = input.readAllBytes();
                }
                updateInt(digest, name.length);
                digest.update(name);
                updateLength(digest, payload.length);
                digest.update(payload);
            }
            return CompatibilityFingerprint.hex(digest.digest());
        }
    }

    private static FingerprintManifest.Entry contractEntry(String modId, String contract, String reason) {
        byte[] bytes = contract.getBytes(StandardCharsets.UTF_8);
        return new FingerprintManifest.Entry(
            "compat:mod:" + modId,
            bytes.length,
            CompatibilityFingerprint.sha256(contract),
            FingerprintManifest.Impact.RELEVANT,
            reason
        );
    }

    private static boolean declaresZfastnoiseOverride(Path jarPath) {
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            JarEntry metadata = jar.getJarEntry("META-INF/neoforge.mods.toml");
            if (metadata == null) return false;
            try (InputStream input = jar.getInputStream(metadata)) {
                return new String(input.readAllBytes(), StandardCharsets.UTF_8).contains("zfastnoise:overrides");
            }
        } catch (Throwable ignored) {
            return true;
        }
    }

    private static boolean isSignatureEntry(String name) {
        String upper = name.toUpperCase(Locale.ROOT);
        return upper.startsWith("META-INF/")
            && (upper.endsWith(".SF") || upper.endsWith(".RSA") || upper.endsWith(".DSA") || upper.endsWith(".EC"));
    }

    private static void updateInt(java.security.MessageDigest digest, int value) {
        for (int shift = 24; shift >= 0; shift -= 8) digest.update((byte) (value >>> shift));
    }

    private static void updateLength(java.security.MessageDigest digest, long value) {
        for (int shift = 56; shift >= 0; shift -= 8) digest.update((byte) (value >>> shift));
    }

    private record Attestation(String modId, String rawSha256, String canonicalSha256, String contract, String reason) {}
}
