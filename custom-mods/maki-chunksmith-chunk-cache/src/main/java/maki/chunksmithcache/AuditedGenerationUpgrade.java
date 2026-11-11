package maki.chunksmithcache;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Verified one-way FULL cache migration for the distributed 4.6.0/9.0.3 to
 * 4.8.1/9.1.0 server bundle. This does not alter the live compatibility
 * fingerprint or globally classify either JAR as generation-neutral.
 *
 * <p>All five changed, previously RELEVANT artifacts must match the exact
 * audited filename, byte count and SHA-256 on both sides. Every other
 * generation-relevant manifest input is still compared by FingerprintManifest.
 * The four other upgraded owned JARs have identical executable .class bytes;
 * two (chunk cache and LOD CPU) are already classified IGNORED in the
 * historical fingerprint and are not exceptions here.</p>
 */
final class AuditedGenerationUpgrade {
    private record Pair(String id, String oldName, long oldSize, String oldSha,
                        String newName, long newSize, String newSha) {
        FingerprintManifest.Entry expectedOld() { return artifact(oldName, oldSize, oldSha); }
        FingerprintManifest.Entry expectedNew() { return artifact(newName, newSize, newSha); }
        FingerprintManifest.Entry contract() {
            String value = "verified-full-cache-compat-v1:" + id;
            return new FingerprintManifest.Entry("compat:verified-full-cache:" + id,
                value.getBytes(StandardCharsets.UTF_8).length,
                CompatibilityFingerprint.sha256(value),
                FingerprintManifest.Impact.RELEVANT,
                "exact distributed JAR pair audited for unchanged cached FULL terrain/serialization");
        }
    }

    private static final List<Pair> PAIRS = List.of(
        new Pair("chunksmith",
            "chunksmith-4.6.0+1.21.1-neoforge.jar", 700128L,
            "0bf37facd76117b1e6a1b638232d840b60c1da5a2f74bb52d15e7c2e80011646",
            "chunksmith-4.8.1+1.21.1-neoforge.jar", 707645L,
            "bf6371873cb78f2a7645001eb1ef0f7f6d7e5e80c51a52667b7d37ee898888ca"),
        new Pair("millenaire",
            "millenaire-9.0.3.jar", 18506464L,
            "5882528027ac67e8759db3926ebef36abe2300029526582f80c7a988b583ec62",
            "millenaire-9.1.0.jar", 18988125L,
            "79932591423c867bada662827a984c52bd355525358b215fe31a5413897708ce"),
        new Pair("maki_millenaire_lod",
            "maki-millenaire-lod-compat-1.0.6.jar", 106228L,
            "27d466ac52e1bbb510ee639294ab6713fdebac16597f146affa3d9d6660dfc52",
            "maki-millenaire-lod-compat-1.0.8.jar", 116616L,
            "b468365be07ac1829acbe1e1a366298ca6faffafb40f10658516a3694ee75af4"),
        new Pair("maki_idle_pregen",
            "maki-idle-pregen-1.1.1.jar", 54977L,
            "85edab9fbce7fd0a643aead86577e269037fc39fe84606c229f1d446441a5f12",
            "maki-idle-pregen-1.1.2.jar", 54977L,
            "95f1178e70c852982faf9200db3fbf19754b5ff9807e2b391382fae7436e2181"),
        new Pair("maki_ship_save",
            "maki-ship-save-1.0.2.jar", 11013L,
            "a62a206c33995e141e8e365c0766867b98be6e636001007f30591b05d196eb32",
            "maki-ship-save-1.0.3.jar", 11012L,
            "1775a10df3660e68901dd510798b4a159f6a15df9efbf188f07ee30d40777801")
    );

    private AuditedGenerationUpgrade() {}

    /**
     * Only an exact old bundle -> exact new bundle with identical compatibility
     * policy may receive the special equivalence proof. Other relevant inputs
     * (seed, generator, datapacks, config, other MODs) are checked separately.
     */
    static boolean eligible(FingerprintManifest.Manifest oldManifest,
                            FingerprintManifest.Manifest newManifest) {
        FingerprintManifest.Entry previousPolicy = findUnique(oldManifest.entries(), "policy:compatibility");
        FingerprintManifest.Entry currentPolicy = findUnique(newManifest.entries(), "policy:compatibility");
        if (previousPolicy == null || !same(previousPolicy, currentPolicy)) return false;
        for (Pair pair : PAIRS) {
            if (!same(pair.expectedOld(), findUnique(oldManifest.entries(), "mods/" + pair.oldName()))
                    || !same(pair.expectedNew(), findUnique(newManifest.entries(), "mods/" + pair.newName()))
                    || findUnique(oldManifest.entries(), "mods/" + pair.newName()) != null
                    || findUnique(newManifest.entries(), "mods/" + pair.oldName()) != null) {
                return false;
            }
        }
        return true;
    }

    static FingerprintManifest.Entry normalize(FingerprintManifest.Entry entry, boolean eligible) {
        if (!eligible || entry.impact() != FingerprintManifest.Impact.RELEVANT) return entry;
        for (Pair pair : PAIRS) {
            if (same(entry, pair.expectedOld()) || same(entry, pair.expectedNew())) return pair.contract();
        }
        return entry;
    }

    static List<String> proofDetails() {
        return PAIRS.stream()
            .map(pair -> "mods/" + pair.oldName() + " -> mods/" + pair.newName()
                + " (exact audited FULL-cache compatibility; all other inputs unchanged)")
            .toList();
    }

    private static FingerprintManifest.Entry artifact(String fileName, long size, String sha) {
        return new FingerprintManifest.Entry("mods/" + fileName, size, sha,
            FingerprintManifest.Impact.RELEVANT, "exact distributed artifact");
    }

    private static FingerprintManifest.Entry findUnique(List<FingerprintManifest.Entry> entries, String key) {
        FingerprintManifest.Entry found = null;
        for (FingerprintManifest.Entry entry : entries) {
            if (!entry.key().equals(key)) continue;
            if (found != null) return null;
            found = entry;
        }
        return found;
    }

    private static boolean same(FingerprintManifest.Entry first, FingerprintManifest.Entry second) {
        return first != null && second != null
            && first.key().equals(second.key())
            && first.size() == second.size()
            && first.sha256().equals(second.sha256())
            && first.impact() == second.impact();
    }
}
