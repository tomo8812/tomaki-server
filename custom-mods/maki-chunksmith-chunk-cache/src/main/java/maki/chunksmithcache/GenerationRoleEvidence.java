package maki.chunksmithcache;

import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Track the relevant Chunksmith generation/LOD bytecode independently of the
 * entire distributed JAR SHA. Future cache generations record this audit in
 * the manifest without changing the cache fingerprint (Impact.IGNORED).
 *
 * Old manifests predating this audit remain eligible via strict per-FULL NBT
 * validation, but once both snapshots carry it, any changed generation code
 * requires a new explicit semantic analysis rather than automatic migration.
 */
final class GenerationRoleEvidence {
    static final String CHUNKSMITH_KEY = "audit:cache-format:chunksmith-generation-code-v1";
    private static final String VERIFIED = "observed Chunksmith generation/LOD code, format contract v1";
    private static final String UNVERIFIED = "unverifiable Chunksmith code; migration fails closed";
    private static final List<String> ESSENTIAL_CLASSES = List.of(
        "com/kishku7/chunksmith/GenerationTask.class",
        "com/kishku7/chunksmith/lod/CsLodCodec.class",
        "com/kishku7/chunksmith/lod/CsLodRegionStore.class",
        "com/kishku7/chunksmith/lod/HorizonGuard.class",
        "com/kishku7/chunksmith/mixin/WorldGenRegionMixin.class",
        "com/kishku7/chunksmith/mixin/StructureStartMixin.class"
    );

    private GenerationRoleEvidence() {}

    static FingerprintManifest.Entry inspect(String logicalPath, Path file) {
        if (!logicalPath.startsWith("mods/chunksmith-") || !logicalPath.endsWith(".jar")) return null;
        try (JarFile jar = new JarFile(file.toFile())) {
            MessageDigest digest = CompatibilityFingerprint.digest();
            long total = 0L;
            for (String name : ESSENTIAL_CLASSES) {
                JarEntry entry = jar.getJarEntry(name);
                if (entry == null) return unverified(logicalPath);
                CompatibilityFingerprint.update(digest, name);
                try (InputStream input = jar.getInputStream(entry)) {
                    byte[] buffer = new byte[16384];
                    int count;
                    while ((count = input.read(buffer)) != -1) {
                        if (count > 0) { digest.update(buffer, 0, count); total += count; }
                    }
                }
                digest.update((byte)0);
            }
            return new FingerprintManifest.Entry(CHUNKSMITH_KEY, total,
                CompatibilityFingerprint.hex(digest.digest()),
                FingerprintManifest.Impact.IGNORED, VERIFIED);
        } catch (Exception unableToInspect) {
            return unverified(logicalPath);
        }
    }

    static boolean compatible(FingerprintManifest.Manifest oldManifest,
                              FingerprintManifest.Manifest newManifest) {
        FingerprintManifest.Entry old = find(oldManifest);
        FingerprintManifest.Entry current = find(newManifest);
        // A current server must provide a verifiable generation code digest.
        // Older manifests may lack it; their FULL data is still field-validated.
        if (current == null || !VERIFIED.equals(current.reason())) return false;
        if (old == null) return true;
        return VERIFIED.equals(old.reason())
            && old.size() == current.size()
            && old.sha256().equals(current.sha256());
    }

    private static FingerprintManifest.Entry find(FingerprintManifest.Manifest manifest) {
        FingerprintManifest.Entry result = null;
        for (FingerprintManifest.Entry entry : manifest.entries()) {
            if (!entry.key().equals(CHUNKSMITH_KEY)) continue;
            if (result != null) return null;
            result = entry;
        }
        return result;
    }

    private static FingerprintManifest.Entry unverified(String logicalPath) {
        return new FingerprintManifest.Entry(CHUNKSMITH_KEY, 0L,
            CompatibilityFingerprint.sha256("missing:" + logicalPath),
            FingerprintManifest.Impact.IGNORED, UNVERIFIED);
    }
}
