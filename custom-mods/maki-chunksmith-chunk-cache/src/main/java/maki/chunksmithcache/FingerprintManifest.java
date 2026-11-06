package maki.chunksmithcache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class FingerprintManifest {
    static final String FILE_NAME = "manifest-v2.txt";
    static final String CURRENT_FILE_NAME = ".current-manifest-v2.txt";
    private static final int FORMAT = 2;

    enum Impact { RELEVANT, IGNORED }

    record Entry(String key, long size, String sha256, Impact impact, String reason) {
        Entry {
            if (key == null || key.isEmpty() || sha256 == null || sha256.isEmpty() || impact == null || reason == null) {
                throw new IllegalArgumentException("invalid fingerprint manifest entry");
            }
        }
    }

    record Manifest(String fingerprint, long createdAtMillis, Set<String> storedFingerprints, List<Entry> entries) {
        Manifest {
            if (fingerprint == null || !fingerprint.matches("[0-9a-f]{24}")) {
                throw new IllegalArgumentException("invalid fingerprint");
            }
            storedFingerprints = Set.copyOf(storedFingerprints);
            entries = List.copyOf(entries);
        }

        Manifest withStoredFingerprints(Set<String> fingerprints) {
            return new Manifest(fingerprint, createdAtMillis, fingerprints, entries);
        }
    }

    private FingerprintManifest() {}

    static void writeAtomic(Path path, Manifest manifest) throws IOException {
        Files.createDirectories(path.getParent());
        Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
        Files.writeString(tmp, encode(manifest), StandardCharsets.UTF_8);
        try {
            Files.move(tmp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (java.nio.file.AtomicMoveNotSupportedException ex) {
            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    static Manifest read(Path path) throws IOException {
        return decode(Files.readString(path, StandardCharsets.UTF_8));
    }

    static Manifest readIfValid(Path path) {
        try {
            return Files.isRegularFile(path) ? read(path) : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    static String encode(Manifest manifest) {
        StringBuilder out = new StringBuilder();
        out.append("format\t").append(FORMAT).append('\n');
        out.append("fingerprint\t").append(manifest.fingerprint()).append('\n');
        out.append("created\t").append(manifest.createdAtMillis()).append('\n');
        List<String> stored = new ArrayList<>(manifest.storedFingerprints());
        stored.sort(String::compareTo);
        out.append("stored\t").append(String.join(",", stored)).append('\n');
        List<Entry> entries = new ArrayList<>(manifest.entries());
        entries.sort(Comparator.comparing(Entry::key));
        for (Entry entry : entries) {
            out.append("entry\t")
                .append(b64(entry.key())).append('\t')
                .append(entry.size()).append('\t')
                .append(entry.sha256()).append('\t')
                .append(entry.impact().name()).append('\t')
                .append(b64(entry.reason())).append('\n');
        }
        return out.toString();
    }

    static Manifest decode(String text) throws IOException {
        String fingerprint = null;
        long created = -1L;
        Set<String> stored = new LinkedHashSet<>();
        List<Entry> entries = new ArrayList<>();
        boolean formatSeen = false;
        for (String line : text.split("\\R")) {
            if (line.isBlank()) continue;
            String[] parts = line.split("\\t", -1);
            switch (parts[0]) {
                case "format" -> {
                    if (parts.length != 2 || Integer.parseInt(parts[1]) != FORMAT) throw new IOException("unsupported manifest format");
                    formatSeen = true;
                }
                case "fingerprint" -> {
                    if (parts.length != 2 || !parts[1].matches("[0-9a-f]{24}")) throw new IOException("bad manifest fingerprint");
                    fingerprint = parts[1];
                }
                case "created" -> {
                    if (parts.length != 2) throw new IOException("bad manifest created field");
                    created = Long.parseLong(parts[1]);
                }
                case "stored" -> {
                    if (parts.length != 2) throw new IOException("bad manifest stored field");
                    if (!parts[1].isEmpty()) {
                        for (String value : parts[1].split(",")) {
                            if (!value.matches("[0-9a-f]{24}")) throw new IOException("bad stored fingerprint");
                            stored.add(value);
                        }
                    }
                }
                case "entry" -> {
                    if (parts.length != 6) throw new IOException("bad manifest entry");
                    String key = unb64(parts[1]);
                    long size = Long.parseLong(parts[2]);
                    String sha = parts[3];
                    if (!sha.matches("[0-9a-f]{64}")) throw new IOException("bad entry digest");
                    Impact impact = Impact.valueOf(parts[4]);
                    entries.add(new Entry(key, size, sha, impact, unb64(parts[5])));
                }
                default -> throw new IOException("unknown manifest field: " + parts[0]);
            }
        }
        if (!formatSeen || fingerprint == null || created < 0L) throw new IOException("incomplete manifest");
        if (stored.isEmpty()) stored.add(fingerprint);
        return new Manifest(fingerprint, created, stored, entries);
    }

    static List<String> changedInputs(Manifest oldManifest, Manifest newManifest, Impact impact, int limit) {
        Map<String, Entry> oldEntries = byKey(oldManifest.entries());
        Map<String, Entry> newEntries = byKey(newManifest.entries());
        Set<String> keys = new LinkedHashSet<>();
        keys.addAll(oldEntries.keySet());
        keys.addAll(newEntries.keySet());
        List<String> changed = new ArrayList<>();
        for (String key : keys.stream().sorted().toList()) {
            Entry oldEntry = oldEntries.get(key);
            Entry newEntry = newEntries.get(key);
            boolean relevant = (oldEntry != null && oldEntry.impact() == impact) || (newEntry != null && newEntry.impact() == impact);
            if (!relevant || equivalent(oldEntry, newEntry)) continue;
            changed.add(key + changeSuffix(oldEntry, newEntry));
            if (changed.size() >= limit) break;
        }
        return changed;
    }

    static boolean sameRelevantInputsExcept(Manifest oldManifest, Manifest newManifest, Set<String> excludedKeys) {
        Map<String, Entry> oldEntries = byKey(oldManifest.entries());
        Map<String, Entry> newEntries = byKey(newManifest.entries());
        Set<String> keys = new LinkedHashSet<>();
        keys.addAll(oldEntries.keySet());
        keys.addAll(newEntries.keySet());
        for (String key : keys) {
            if (excludedKeys.contains(key)) continue;
            Entry oldEntry = oldEntries.get(key);
            Entry newEntry = newEntries.get(key);
            boolean relevant = (oldEntry != null && oldEntry.impact() == Impact.RELEVANT)
                || (newEntry != null && newEntry.impact() == Impact.RELEVANT);
            if (relevant && !equivalent(oldEntry, newEntry)) return false;
        }
        return true;
    }

    static boolean sameCompatibilityInputsExcept(Manifest oldManifest, Manifest newManifest, Set<String> excludedKeys) {
        Entry currentRawZfastnoise = findEntry(
            newManifest.entries(),
            CompatibilityInput.RAW_AUDIT_PREFIX + CompatibilityInput.ZFASTNOISE_PATH
        );
        Entry currentEffectiveZfastnoise = findEntry(newManifest.entries(), CompatibilityInput.ZFASTNOISE_PATH);
        Map<String, Entry> oldEntries = compatibilityByKey(
            oldManifest.entries(),
            excludedKeys,
            currentRawZfastnoise,
            currentEffectiveZfastnoise
        );
        Map<String, Entry> newEntries = compatibilityByKey(newManifest.entries(), excludedKeys, null, null);
        if (oldEntries == null || newEntries == null || !oldEntries.keySet().equals(newEntries.keySet())) return false;
        for (String key : oldEntries.keySet()) {
            if (!equivalent(oldEntries.get(key), newEntries.get(key))) return false;
        }
        return true;
    }

    private static Entry findEntry(List<Entry> entries, String key) {
        for (Entry entry : entries) {
            if (entry.key().equals(key)) return entry;
        }
        return null;
    }

    private static Map<String, Entry> compatibilityByKey(
        List<Entry> entries,
        Set<String> excludedKeys,
        Entry currentRawZfastnoise,
        Entry currentEffectiveZfastnoise
    ) {
        Map<String, Entry> result = new LinkedHashMap<>();
        for (Entry original : entries) {
            if (excludedKeys.contains(original.key())) continue;
            Entry entry;
            if (original.key().equals(CompatibilityInput.ZFASTNOISE_PATH)
                && currentRawZfastnoise != null
                && currentEffectiveZfastnoise != null
                && original.size() == currentRawZfastnoise.size()
                && original.sha256().equals(currentRawZfastnoise.sha256())) {
                entry = currentEffectiveZfastnoise;
            } else {
                entry = CompatibilityInput.normalizeHistoricalEntry(original);
            }
            if (entry == null || entry.impact() != Impact.RELEVANT) continue;
            Entry previous = result.putIfAbsent(entry.key(), entry);
            if (previous != null && !equivalent(previous, entry)) return null;
        }
        return result;
    }

    private static Map<String, Entry> byKey(List<Entry> entries) {
        Map<String, Entry> result = new LinkedHashMap<>();
        for (Entry entry : entries) result.put(entry.key(), entry);
        return result;
    }

    private static boolean equivalent(Entry a, Entry b) {
        return a != null && b != null && a.size() == b.size() && a.sha256().equals(b.sha256()) && a.impact() == b.impact();
    }

    private static String changeSuffix(Entry oldEntry, Entry newEntry) {
        Entry present = newEntry != null ? newEntry : oldEntry;
        String key = present == null ? "" : present.key();
        if (key.equals(CompatibilityInput.RAW_AUDIT_PREFIX + CompatibilityInput.ZFASTNOISE_PATH)) {
            return "(source-format/comment changed; effective values unchanged)";
        }
        if (key.startsWith(AuditedModCompatibility.RAW_AUDIT_PREFIX)) {
            return "(audited generation-neutral JAR changed; compatibility contract unchanged)";
        }
        if (key.equals(CompatibilityInput.ZFASTNOISE_PATH) && oldEntry != null && newEntry != null) {
            return "(effective values changed)";
        }
        if (oldEntry == null) return "(added)";
        if (newEntry == null) return "(removed)";
        if (oldEntry.impact() != newEntry.impact()) return "(classification " + oldEntry.impact() + "->" + newEntry.impact() + ")";
        if (present.reason().contains("fail-closed")) return "(changed; fail-closed relevant)";
        return "(changed)";
    }

    private static String b64(String text) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private static String unb64(String text) throws IOException {
        try {
            return new String(Base64.getUrlDecoder().decode(text), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            throw new IOException("invalid base64 in manifest", ex);
        }
    }
}
