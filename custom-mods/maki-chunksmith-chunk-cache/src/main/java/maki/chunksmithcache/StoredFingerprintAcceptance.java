package maki.chunksmithcache;

import java.nio.file.Path;

final class StoredFingerprintAcceptance {
    private StoredFingerprintAcceptance() {}

    static boolean accepts(Path generationDirectory, String expectedFingerprint, String storedFingerprint) {
        if (expectedFingerprint.equals(storedFingerprint)) return true;
        FingerprintManifest.Manifest manifest = FingerprintManifest.readIfValid(
            generationDirectory.resolve(FingerprintManifest.FILE_NAME)
        );
        return manifest != null
            && expectedFingerprint.equals(manifest.fingerprint())
            && manifest.storedFingerprints().contains(storedFingerprint);
    }
}
