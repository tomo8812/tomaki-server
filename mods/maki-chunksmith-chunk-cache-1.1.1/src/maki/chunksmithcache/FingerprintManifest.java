package maki.chunksmithcache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
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

   private FingerprintManifest() {
   }

   static void writeAtomic(Path path, FingerprintManifest.Manifest manifest) throws IOException {
      Files.createDirectories(path.getParent());
      Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
      Files.writeString(tmp, encode(manifest), StandardCharsets.UTF_8);

      try {
         Files.move(tmp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException ex) {
         Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   static FingerprintManifest.Manifest read(Path path) throws IOException {
      return decode(Files.readString(path, StandardCharsets.UTF_8));
   }

   static FingerprintManifest.Manifest readIfValid(Path path) {
      try {
         return Files.isRegularFile(path) ? read(path) : null;
      } catch (Throwable ignored) {
         return null;
      }
   }

   static String encode(FingerprintManifest.Manifest manifest) {
      StringBuilder out = new StringBuilder();
      out.append("format\t").append(2).append('\n');
      out.append("fingerprint\t").append(manifest.fingerprint()).append('\n');
      out.append("created\t").append(manifest.createdAtMillis()).append('\n');
      List<String> stored = new ArrayList<>(manifest.storedFingerprints());
      stored.sort(String::compareTo);
      out.append("stored\t").append(String.join(",", stored)).append('\n');
      List<FingerprintManifest.Entry> entries = new ArrayList<>(manifest.entries());
      entries.sort(Comparator.comparing(FingerprintManifest.Entry::key));

      for (FingerprintManifest.Entry entry : entries) {
         out.append("entry\t")
            .append(b64(entry.key()))
            .append('\t')
            .append(entry.size())
            .append('\t')
            .append(entry.sha256())
            .append('\t')
            .append(entry.impact().name())
            .append('\t')
            .append(b64(entry.reason()))
            .append('\n');
      }

      return out.toString();
   }

   static FingerprintManifest.Manifest decode(String text) throws IOException {
      String fingerprint = null;
      long created = -1L;
      Set<String> stored = new LinkedHashSet<>();
      List<FingerprintManifest.Entry> entries = new ArrayList<>();
      boolean formatSeen = false;

      for (String line : text.split("\\R")) {
         if (!line.isBlank()) {
            String[] parts = line.split("\\t", -1);
            switch (parts[0]) {
               case "format":
                  if (parts.length != 2 || Integer.parseInt(parts[1]) != 2) {
                     throw new IOException("unsupported manifest format");
                  }

                  formatSeen = true;
                  break;
               case "fingerprint":
                  if (parts.length != 2 || !parts[1].matches("[0-9a-f]{24}")) {
                     throw new IOException("bad manifest fingerprint");
                  }

                  fingerprint = parts[1];
                  break;
               case "created":
                  if (parts.length != 2) {
                     throw new IOException("bad manifest created field");
                  }

                  created = Long.parseLong(parts[1]);
                  break;
               case "stored":
                  if (parts.length != 2) {
                     throw new IOException("bad manifest stored field");
                  }

                  if (!parts[1].isEmpty()) {
                     for (String value : parts[1].split(",")) {
                        if (!value.matches("[0-9a-f]{24}")) {
                           throw new IOException("bad stored fingerprint");
                        }

                        stored.add(value);
                     }
                  }
                  break;
               case "entry":
                  if (parts.length != 6) {
                     throw new IOException("bad manifest entry");
                  }

                  String key = unb64(parts[1]);
                  long size = Long.parseLong(parts[2]);
                  String sha = parts[3];
                  if (!sha.matches("[0-9a-f]{64}")) {
                     throw new IOException("bad entry digest");
                  }

                  FingerprintManifest.Impact impact = FingerprintManifest.Impact.valueOf(parts[4]);
                  entries.add(new FingerprintManifest.Entry(key, size, sha, impact, unb64(parts[5])));
                  break;
               default:
                  throw new IOException("unknown manifest field: " + parts[0]);
            }
         }
      }

      if (formatSeen && fingerprint != null && created >= 0L) {
         if (stored.isEmpty()) {
            stored.add(fingerprint);
         }

         return new FingerprintManifest.Manifest(fingerprint, created, stored, entries);
      } else {
         throw new IOException("incomplete manifest");
      }
   }

   static List<String> changedInputs(
      FingerprintManifest.Manifest oldManifest, FingerprintManifest.Manifest newManifest, FingerprintManifest.Impact impact, int limit
   ) {
      Map<String, FingerprintManifest.Entry> oldEntries = byKey(oldManifest.entries());
      Map<String, FingerprintManifest.Entry> newEntries = byKey(newManifest.entries());
      Set<String> keys = new LinkedHashSet<>();
      keys.addAll(oldEntries.keySet());
      keys.addAll(newEntries.keySet());
      List<String> changed = new ArrayList<>();

      for (String key : keys.stream().sorted().toList()) {
         FingerprintManifest.Entry oldEntry = oldEntries.get(key);
         FingerprintManifest.Entry newEntry = newEntries.get(key);
         boolean relevant = oldEntry != null && oldEntry.impact() == impact || newEntry != null && newEntry.impact() == impact;
         if (relevant && !equivalent(oldEntry, newEntry)) {
            changed.add(key + changeSuffix(oldEntry, newEntry));
            if (changed.size() >= limit) {
               break;
            }
         }
      }

      return changed;
   }

   static boolean sameRelevantInputsExcept(FingerprintManifest.Manifest oldManifest, FingerprintManifest.Manifest newManifest, Set<String> excludedKeys) {
      Map<String, FingerprintManifest.Entry> oldEntries = byKey(oldManifest.entries());
      Map<String, FingerprintManifest.Entry> newEntries = byKey(newManifest.entries());
      Set<String> keys = new LinkedHashSet<>();
      keys.addAll(oldEntries.keySet());
      keys.addAll(newEntries.keySet());

      for (String key : keys) {
         if (!excludedKeys.contains(key)) {
            FingerprintManifest.Entry oldEntry = oldEntries.get(key);
            FingerprintManifest.Entry newEntry = newEntries.get(key);
            boolean relevant = oldEntry != null && oldEntry.impact() == FingerprintManifest.Impact.RELEVANT
               || newEntry != null && newEntry.impact() == FingerprintManifest.Impact.RELEVANT;
            if (relevant && !equivalent(oldEntry, newEntry)) {
               return false;
            }
         }
      }

      return true;
   }

   private static Map<String, FingerprintManifest.Entry> byKey(List<FingerprintManifest.Entry> entries) {
      Map<String, FingerprintManifest.Entry> result = new LinkedHashMap<>();

      for (FingerprintManifest.Entry entry : entries) {
         result.put(entry.key(), entry);
      }

      return result;
   }

   private static boolean equivalent(FingerprintManifest.Entry a, FingerprintManifest.Entry b) {
      return a != null && b != null && a.size() == b.size() && a.sha256().equals(b.sha256()) && a.impact() == b.impact();
   }

   private static String changeSuffix(FingerprintManifest.Entry oldEntry, FingerprintManifest.Entry newEntry) {
      if (oldEntry == null) {
         return "(added)";
      } else if (newEntry == null) {
         return "(removed)";
      } else {
         return oldEntry.impact() != newEntry.impact() ? "(classification " + oldEntry.impact() + "->" + newEntry.impact() + ")" : "(changed)";
      }
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

   record Entry(String key, long size, String sha256, FingerprintManifest.Impact impact, String reason) {
      Entry(String key, long size, String sha256, FingerprintManifest.Impact impact, String reason) {
         if (key != null && !key.isEmpty() && sha256 != null && !sha256.isEmpty() && impact != null && reason != null) {
            this.key = key;
            this.size = size;
            this.sha256 = sha256;
            this.impact = impact;
            this.reason = reason;
         } else {
            throw new IllegalArgumentException("invalid fingerprint manifest entry");
         }
      }
   }

   enum Impact {
      RELEVANT,
      IGNORED;

      // $VF: synthetic method
      private static FingerprintManifest.Impact[] $values() {
         return new FingerprintManifest.Impact[]{RELEVANT, IGNORED};
      }
   }

   record Manifest(String fingerprint, long createdAtMillis, Set<String> storedFingerprints, List<FingerprintManifest.Entry> entries) {
      Manifest(String fingerprint, long createdAtMillis, Set<String> storedFingerprints, List<FingerprintManifest.Entry> entries) {
         if (fingerprint != null && fingerprint.matches("[0-9a-f]{24}")) {
            storedFingerprints = Set.copyOf(storedFingerprints);
            entries = List.copyOf(entries);
            this.fingerprint = fingerprint;
            this.createdAtMillis = createdAtMillis;
            this.storedFingerprints = storedFingerprints;
            this.entries = entries;
         } else {
            throw new IllegalArgumentException("invalid fingerprint");
         }
      }

      FingerprintManifest.Manifest withStoredFingerprints(Set<String> fingerprints) {
         return new FingerprintManifest.Manifest(this.fingerprint, this.createdAtMillis, fingerprints, this.entries);
      }
   }
}
