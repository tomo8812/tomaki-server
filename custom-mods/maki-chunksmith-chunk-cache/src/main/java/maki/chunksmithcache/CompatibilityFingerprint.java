package maki.chunksmithcache;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class CompatibilityFingerprint {
    static final String SCHEMA = "maki-chunk-cache-compat-v3";

    private CompatibilityFingerprint() {}

    static String compute(List<FingerprintManifest.Entry> entries) {
        MessageDigest digest = digest();
        update(digest, SCHEMA);
        List<FingerprintManifest.Entry> sorted = new ArrayList<>(entries);
        sorted.sort(Comparator.comparing(FingerprintManifest.Entry::key));
        for (FingerprintManifest.Entry entry : sorted) {
            if (entry.impact() != FingerprintManifest.Impact.RELEVANT) continue;
            update(digest, entry.key());
            update(digest, Long.toString(entry.size()));
            update(digest, entry.sha256());
        }
        return hex(digest.digest()).substring(0, 24);
    }

    static String sha256(String value) {
        MessageDigest digest = digest();
        digest.update(value.getBytes(StandardCharsets.UTF_8));
        return hex(digest.digest());
    }

    static MessageDigest digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static void update(MessageDigest digest, String value) {
        digest.update(value.getBytes(StandardCharsets.UTF_8));
        digest.update((byte) 0);
    }

    static String hex(byte[] bytes) {
        StringBuilder out = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) out.append(String.format("%02x", value));
        return out.toString();
    }
}
