package maki.chunksmithcache;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

final class CompatibilityInput {
    static final String ZFASTNOISE_PATH = "config/zfastnoise.mixin.properties";
    static final String LOD_CPU_CONTRACT_KEY = "compat:mod:maki_lod_cpu";
    static final String LOD_CPU_ONLINE_FIX_CONTRACT_KEY = "compat:mod:maki_lod_cpu_online_fix";
    static final String LOD_CPU_CONTRACT = "load-control-only-v1";
    static final String LOD_CPU_ONLINE_FIX_CONTRACT = "adaptive-cap-only-v1";
    static final String RAW_AUDIT_PREFIX = "audit:raw:";

    private static final List<String> ZFASTNOISE_KEYS = List.of("mixin.perf.biome", "mixin.perf.noise");
    private static final Set<String> VERIFIED_LOD_CPU_VERSIONS = Set.of("1.3.4", "1.4.0");
    private static final Set<String> VERIFIED_ONLINE_FIX_VERSIONS = Set.of("1.0.2", "1.0.3");
    private static final String LOD_CPU_1_3_4_SHA256 = "04c3a6f458550d15e4b8c736c597e376c1825cfb533a66181ff142c383fb7ed2";
    private static final String LOD_CPU_1_4_0_SHA256 = "2cd157f1b78378f41863e6cfdfe545c3763dc3ddb1f0bce3cbfa6d9fd1d77799";
    private static final String ONLINE_FIX_1_0_2_SHA256 = "833defdc614826804dcbf8f818827e6baf0a6d69c5f4e9b0946b9a2609d413f1";
    private static final String ONLINE_FIX_1_0_3_SHA256 = "2217e4f0267b8786016e5f2ddbf6491eb4165f37e17439f23b7003f1cd1de206";

    private CompatibilityInput() {}

    static List<FingerprintManifest.Entry> entries(
        String key,
        Path file,
        long rawSize,
        String rawSha256,
        FingerprintPolicy policy
    ) {
        if (ZFASTNOISE_PATH.equals(key)) {
            return zfastnoiseEntries(key, file, rawSize, rawSha256);
        }

        VerifiedMod verified = key.startsWith("mods/maki-lod-cpu") ? verifiedLoadControlMod(file, rawSha256) : null;
        if (verified != null) {
            String contractKey = verified.id().equals("maki_lod_cpu") ? LOD_CPU_CONTRACT_KEY : LOD_CPU_ONLINE_FIX_CONTRACT_KEY;
            String contract = verified.id().equals("maki_lod_cpu") ? LOD_CPU_CONTRACT : LOD_CPU_ONLINE_FIX_CONTRACT;
            String reason = "verified non-worldgen load-control contract " + verified.id()
                + " " + verified.version() + " -> " + contract;
            return List.of(
                valueEntry(contractKey, contract, FingerprintManifest.Impact.RELEVANT, reason),
                new FingerprintManifest.Entry(
                    RAW_AUDIT_PREFIX + key,
                    rawSize,
                    rawSha256,
                    FingerprintManifest.Impact.IGNORED,
                    "raw JAR bytes retained for audit; " + reason
                )
            );
        }

        FingerprintManifest.Impact impact = policy.classify(key, file);
        return List.of(new FingerprintManifest.Entry(key, rawSize, rawSha256, impact, policy.reason(key, file, impact)));
    }

    static String policySignature() {
        return "semantic-v1"
            + "|zfastnoise=" + String.join(",", ZFASTNOISE_KEYS)
            + "|maki_lod_cpu=1.3.4@" + LOD_CPU_1_3_4_SHA256 + ",1.4.0@" + LOD_CPU_1_4_0_SHA256
            + ":" + LOD_CPU_CONTRACT
            + "|maki_lod_cpu_online_fix=1.0.2@" + ONLINE_FIX_1_0_2_SHA256 + ",1.0.3@" + ONLINE_FIX_1_0_3_SHA256
            + ":" + LOD_CPU_ONLINE_FIX_CONTRACT;
    }

    static FingerprintManifest.Entry normalizeHistoricalEntry(FingerprintManifest.Entry entry) {
        String key = entry.key();
        if (key.startsWith(RAW_AUDIT_PREFIX)) return null;

        String lodVersion = versionFromHistoricalPath(key, "mods/maki-lod-cpu-", ".jar");
        if (lodVersion != null && entry.sha256().equals(trustedArtifactSha("maki_lod_cpu", lodVersion))) {
            return valueEntry(
                LOD_CPU_CONTRACT_KEY,
                LOD_CPU_CONTRACT,
                FingerprintManifest.Impact.RELEVANT,
                "historical audited maki_lod_cpu " + lodVersion
            );
        }

        String onlineVersion = versionFromHistoricalPath(key, "mods/maki-lod-cpu-online-fix-", ".jar");
        if (onlineVersion != null && entry.sha256().equals(trustedArtifactSha("maki_lod_cpu_online_fix", onlineVersion))) {
            return valueEntry(
                LOD_CPU_ONLINE_FIX_CONTRACT_KEY,
                LOD_CPU_ONLINE_FIX_CONTRACT,
                FingerprintManifest.Impact.RELEVANT,
                "historical audited maki_lod_cpu_online_fix " + onlineVersion
            );
        }

        if (ZFASTNOISE_PATH.equals(key) && isKnownLegacyZfastnoiseTrueTrue(entry)) {
            return zfastnoiseCanonicalEntry(true, true);
        }

        return entry;
    }

    static FingerprintManifest.Entry zfastnoiseCanonicalEntry(boolean biome, boolean noise) {
        String value = "mixin.perf.biome=" + biome + "\n"
            + "mixin.perf.noise=" + noise + "\n";
        return valueEntry(
            ZFASTNOISE_PATH,
            value,
            FingerprintManifest.Impact.RELEVANT,
            "zfastnoise effective properties; comments/order/whitespace and ignored keys do not affect compatibility"
        );
    }

    private static List<FingerprintManifest.Entry> zfastnoiseEntries(String key, Path file, long rawSize, String rawSha256) {
        try {
            Properties properties = new Properties();
            try (InputStream input = Files.newInputStream(file)) {
                properties.load(input);
            }
            boolean biome = Boolean.parseBoolean(properties.getProperty("mixin.perf.biome", "true"));
            boolean noise = Boolean.parseBoolean(properties.getProperty("mixin.perf.noise", "true"));
            return List.of(
                zfastnoiseCanonicalEntry(biome, noise),
                new FingerprintManifest.Entry(
                    RAW_AUDIT_PREFIX + key,
                    rawSize,
                    rawSha256,
                    FingerprintManifest.Impact.IGNORED,
                    "zfastnoise raw source bytes for audit; compatibility uses effective boolean values"
                )
            );
        } catch (Throwable error) {
            return List.of(new FingerprintManifest.Entry(
                key,
                rawSize,
                rawSha256,
                FingerprintManifest.Impact.RELEVANT,
                "zfastnoise parse failed; raw bytes kept fail-closed (" + error.getClass().getSimpleName() + ")"
            ));
        }
    }

    private static boolean isKnownLegacyZfastnoiseTrueTrue(FingerprintManifest.Entry entry) {
        if (entry.size() != 76L) return false;
        return entry.sha256().equals("802908edb8bb4c295a71796823e68320dc0e0b56f52f4d8db482eacc31bc6d3c")
            || entry.sha256().equals("fae3abedad69cc66a78764bb1ad837adc6d730c258af3604b40f4f68e0874fa6");
    }

    private static VerifiedMod verifiedLoadControlMod(Path file, String rawSha256) {
        if (!file.getFileName().toString().endsWith(".jar")) return null;
        List<ModDescriptor> mods = readMods(file);
        if (mods.size() != 1) return null;
        ModDescriptor mod = mods.getFirst();
        String trusted = trustedArtifactSha(mod.id(), mod.version());
        if (trusted != null && trusted.equals(rawSha256)) return new VerifiedMod(mod.id(), mod.version());
        return null;
    }

    static String trustedArtifactSha(String modId, String version) {
        if ("maki_lod_cpu".equals(modId)) {
            if ("1.3.4".equals(version)) return LOD_CPU_1_3_4_SHA256;
            if ("1.4.0".equals(version)) return LOD_CPU_1_4_0_SHA256;
        }
        if ("maki_lod_cpu_online_fix".equals(modId)) {
            if ("1.0.2".equals(version)) return ONLINE_FIX_1_0_2_SHA256;
            if ("1.0.3".equals(version)) return ONLINE_FIX_1_0_3_SHA256;
        }
        return null;
    }

    private static List<ModDescriptor> readMods(Path jarPath) {
        List<ModDescriptor> result = new ArrayList<>();
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            JarEntry metadata = jar.getJarEntry("META-INF/neoforge.mods.toml");
            if (metadata == null) return List.of();
            String text;
            try (InputStream input = jar.getInputStream(metadata)) {
                text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }
            String id = null;
            String version = null;
            boolean inMods = false;
            for (String raw : text.split("\\R")) {
                String line = raw.trim();
                if (line.startsWith("[[") && line.endsWith("]]")) {
                    if (inMods && id != null && version != null) result.add(new ModDescriptor(id, version));
                    inMods = line.equals("[[mods]]");
                    id = null;
                    version = null;
                    continue;
                }
                if (!inMods) continue;
                String value = quotedValue(line);
                if (value == null) continue;
                if (line.startsWith("modId")) id = value.toLowerCase(Locale.ROOT);
                else if (line.startsWith("version")) version = value;
            }
            if (inMods && id != null && version != null) result.add(new ModDescriptor(id, version));
        } catch (Throwable ignored) {
            return List.of();
        }
        return List.copyOf(result);
    }

    private static String quotedValue(String line) {
        int equals = line.indexOf('=');
        if (equals < 0) return null;
        String value = line.substring(equals + 1).trim();
        if (value.length() < 2 || !value.startsWith("\"") || !value.endsWith("\"")) return null;
        return value.substring(1, value.length() - 1);
    }

    private static String versionFromHistoricalPath(String key, String prefix, String suffix) {
        if (!key.startsWith(prefix) || !key.endsWith(suffix)) return null;
        return key.substring(prefix.length(), key.length() - suffix.length());
    }

    private static FingerprintManifest.Entry valueEntry(
        String key,
        String value,
        FingerprintManifest.Impact impact,
        String reason
    ) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return new FingerprintManifest.Entry(key, bytes.length, CompatibilityFingerprint.sha256(value), impact, reason);
    }

    private record ModDescriptor(String id, String version) {}
    private record VerifiedMod(String id, String version) {}
}
