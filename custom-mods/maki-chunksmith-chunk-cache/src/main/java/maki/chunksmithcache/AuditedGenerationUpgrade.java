package maki.chunksmithcache;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Version-independent, format-gated adoption of Chunksmith-owned FULL data.
 *
 * <p>These roles own LOD/pregen scheduling or deferred village placement, not
 * the underlying terrain generator. Their distribution JAR hashes are not
 * useful measures of FULL NBT compatibility. A role transition is permitted
 * only when both manifests describe a single artifact for that role, it
 * remains within the same major version with no downgrade, and every other
 * relevant generation input (terrain MODs, data packs, configs, level.dat,
 * seed, generator, DataVersion) still matches. All transferred FULL files
 * undergo CacheFormatValidator checks; invalid ones remain quarantined.
 *
 * <p>New release numbers are deliberately not enumerated here. The stable
 * schema/format checks apply to caches from any compatible generation.</p>
 */
final class AuditedGenerationUpgrade {
    private record Role(String id, Pattern jarName) {
        Version parse(FingerprintManifest.Entry entry) {
            if (entry == null || entry.impact() != FingerprintManifest.Impact.RELEVANT) return null;
            Matcher found = jarName.matcher(entry.key());
            if (!found.matches()) return null;
            try {
                return new Version(Integer.parseInt(found.group(1)),
                    Integer.parseInt(found.group(2)), Integer.parseInt(found.group(3)));
            } catch (NumberFormatException invalid) {
                return null;
            }
        }

        FingerprintManifest.Entry contract() {
            String value = "cached-full-v3:role=" + id + ":worldgen-owned-by-unchanged-terrain-inputs";
            return new FingerprintManifest.Entry("compat:cached-full-format:" + id,
                value.getBytes(StandardCharsets.UTF_8).length,
                CompatibilityFingerprint.sha256(value),
                FingerprintManifest.Impact.RELEVANT,
                "FULL v3 role-equivalence; individual NBT is validated before transfer");
        }
    }

    private record Version(int major, int minor, int patch) implements Comparable<Version> {
        @Override public int compareTo(Version other) {
            int a = Integer.compare(major, other.major);
            if (a != 0) return a;
            a = Integer.compare(minor, other.minor);
            return a != 0 ? a : Integer.compare(patch, other.patch);
        }
    }

    // Stable *roles* and versioned artifact name shapes; no release or JAR hash pairs.
    private static final List<Role> ROLES = List.of(
        role("chunksmith", "chunksmith-"),
        role("millenaire", "millenaire-"),
        role("maki_millenaire_lod", "maki-millenaire-lod-compat-"),
        role("maki_idle_pregen", "maki-idle-pregen-"),
        role("maki_ship_save", "maki-ship-save-")
    );

    private AuditedGenerationUpgrade() {}

    private static Role role(String id, String prefix) {
        return new Role(id, Pattern.compile("mods/" + Pattern.quote(prefix)
            + "(\\d+)\\.(\\d+)\\.(\\d+)(?:\\+[^/]*)?\\.jar"));
    }

    static boolean eligible(FingerprintManifest.Manifest historical,
                            FingerprintManifest.Manifest current) {
        FingerprintManifest.Entry oldPolicy = unique(historical, "policy:compatibility");
        FingerprintManifest.Entry newPolicy = unique(current, "policy:compatibility");
        if (!same(oldPolicy, newPolicy)
            || !GenerationRoleEvidence.compatible(historical, current)) return false;

        boolean changed = false;
        for (Role role : ROLES) {
            FingerprintManifest.Entry oldJar = artifactForRole(historical, role);
            FingerprintManifest.Entry newJar = artifactForRole(current, role);
            if (oldJar == null || newJar == null) return false;
            Version oldVersion = role.parse(oldJar);
            Version newVersion = role.parse(newJar);
            if (oldVersion == null || newVersion == null
                || oldVersion.major() != newVersion.major()
                || oldVersion.compareTo(newVersion) > 0) return false;
            // A same-version JAR mutation is not an identifiable version
            // transition. Fail closed instead of masking an untracked rebuild.
            if (oldVersion.compareTo(newVersion) == 0
                && (!oldJar.key().equals(newJar.key()) || !same(oldJar, newJar))) return false;
            if (!oldJar.key().equals(newJar.key())) changed = true;
        }
        return changed;
    }

    static FingerprintManifest.Entry normalize(FingerprintManifest.Entry entry, boolean eligible) {
        if (!eligible || entry.impact() != FingerprintManifest.Impact.RELEVANT) return entry;
        for (Role role : ROLES) {
            if (role.parse(entry) != null) return role.contract();
        }
        return entry;
    }

    static List<String> proofDetails(FingerprintManifest.Manifest historical,
                                     FingerprintManifest.Manifest current) {
        if (!eligible(historical, current)) return List.of();
        List<String> proof = new ArrayList<>();
        for (Role role : ROLES) {
            FingerprintManifest.Entry oldJar = artifactForRole(historical, role);
            FingerprintManifest.Entry newJar = artifactForRole(current, role);
            if (!oldJar.key().equals(newJar.key())) {
                proof.add(oldJar.key() + " -> " + newJar.key()
                    + " (format-based FULL v3 role migration; per-file validation required)");
            }
        }
        return List.copyOf(proof);
    }

    private static FingerprintManifest.Entry artifactForRole(
        FingerprintManifest.Manifest manifest, Role role
    ) {
        FingerprintManifest.Entry found = null;
        for (FingerprintManifest.Entry entry : manifest.entries()) {
            if (role.parse(entry) == null) continue;
            if (found != null) return null; // duplicated MOD role
            found = entry;
        }
        return found;
    }

    private static FingerprintManifest.Entry unique(FingerprintManifest.Manifest manifest, String key) {
        FingerprintManifest.Entry found = null;
        for (FingerprintManifest.Entry entry : manifest.entries()) {
            if (!entry.key().equals(key)) continue;
            if (found != null) return null;
            found = entry;
        }
        return found;
    }

    private static boolean same(FingerprintManifest.Entry a, FingerprintManifest.Entry b) {
        return a != null && b != null && a.key().equals(b.key())
            && a.size() == b.size() && a.sha256().equals(b.sha256())
            && a.impact() == b.impact();
    }
}
