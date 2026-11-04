package com.kishku7.chunksmith.lod.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public final class InjectedIndex {
   static final String FILE_NAME = ".injected";
   private static final String EPOCH_PREFIX = "#epoch=";
   private final Path file;
   private final String epoch;
   private final Map<Long, Long> entries = new ConcurrentHashMap<>();

   private InjectedIndex(Path file, String epoch) {
      this.file = file;
      this.epoch = epoch;
   }

   public static InjectedIndex open(Path storeRoot, String dimension, String epoch, boolean ignore) {
      Path dir = CsLodStore.dimensionDir(storeRoot, dimension);
      if (dir == null) {
         return null;
      }

      InjectedIndex index = new InjectedIndex(dir.resolve(".injected"), epoch);
      if (!ignore) {
         index.load();
      }

      return index;
   }

   public static String epochFor(boolean voxy, boolean dh, int storeVersion) {
      return (voxy ? "voxy" : "-") + "+" + (dh ? "dh" : "-") + "|v" + storeVersion;
   }

   public void put(int regionX, int regionZ, long token) {
      this.entries.put(key(regionX, regionZ), token);
   }

   public void remove(int regionX, int regionZ) {
      this.entries.remove(key(regionX, regionZ));
   }

   public int size() {
      return this.entries.size();
   }

   public List<long[]> entries() {
      List<long[]> out = new ArrayList<>(this.entries.size());

      for (Entry<Long, Long> entry : this.entries.entrySet()) {
         long packed = entry.getKey();
         out.add(new long[]{(int)(packed >> 32), (int)packed, entry.getValue()});
      }

      return out;
   }

   public void save() throws IOException {
      List<String> lines = new ArrayList<>(this.entries.size() + 1);
      lines.add("#epoch=" + this.epoch);

      for (Entry<Long, Long> entry : this.entries.entrySet()) {
         long packed = entry.getKey();
         lines.add((int)(packed >> 32) + "," + (int)packed + "=" + entry.getValue());
      }

      Files.createDirectories(this.file.getParent());
      Path temp = this.file.resolveSibling(".injected.part");
      Files.write(temp, lines, StandardCharsets.US_ASCII);
      Files.move(temp, this.file, StandardCopyOption.REPLACE_EXISTING);
   }

   private void load() {
      if (Files.isRegularFile(this.file)) {
         List<String> lines;
         try {
            lines = Files.readAllLines(this.file, StandardCharsets.US_ASCII);
         } catch (IOException e) {
            return;
         }

         if (!lines.isEmpty() && lines.get(0).equals("#epoch=" + this.epoch)) {
            for (int i = 1; i < lines.size(); i++) {
               this.parse(lines.get(i));
            }
         }
      }
   }

   private void parse(String line) {
      int equals = line.indexOf(61);
      if (equals > 0) {
         String[] coords = line.substring(0, equals).split(",", -1);
         if (coords.length == 2) {
            try {
               this.entries.put(key(Integer.parseInt(coords[0].trim()), Integer.parseInt(coords[1].trim())), Long.parseLong(line.substring(equals + 1).trim()));
            } catch (NumberFormatException var5) {
            }
         }
      }
   }

   private static long key(int regionX, int regionZ) {
      return (long)regionX << 32 | regionZ & 4294967295L;
   }
}
