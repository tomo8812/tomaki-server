package com.kishku7.chunksmith.lod.client;

import com.kishku7.chunksmith.lod.net.CsLodMessages;
import com.kishku7.chunksmith.lod.net.CsLodSummary;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CsLodManifest {
   static final String FILE_NAME = ".manifest";
   private final Path file;
   private final Map<Long, CsLodManifest.Entry> entries = new ConcurrentHashMap<>();

   private CsLodManifest(Path file) {
      this.file = file;
   }

   public static CsLodManifest open(Path storeRoot, String dimension) {
      Path dir = CsLodStore.dimensionDir(storeRoot, dimension);
      if (dir == null) {
         return null;
      }

      CsLodManifest manifest = new CsLodManifest(dir.resolve(".manifest"));
      manifest.load();
      return manifest;
   }

   public void put(int regionX, int regionZ, long hash, long sizeBytes) {
      this.entries.put(key(regionX, regionZ), new CsLodManifest.Entry(hash, sizeBytes));
   }

   public CsLodManifest.Entry get(int regionX, int regionZ) {
      return this.entries.get(key(regionX, regionZ));
   }

   public void remove(int regionX, int regionZ) {
      this.entries.remove(key(regionX, regionZ));
   }

   public int size() {
      return this.entries.size();
   }

   public boolean holds(Path dimensionDir, CsLodMessages.RegionEntry advertised) {
      CsLodManifest.Entry mine = this.get(advertised.regionX(), advertised.regionZ());
      if (mine == null || mine.hash() != advertised.hash()) {
         return false;
      }

      if (advertised.hash() == 0L) {
         return false;
      }

      Path region = dimensionDir.resolve("r." + advertised.regionX() + "." + advertised.regionZ() + ".cslod");

      try {
         return Files.isRegularFile(region) && Files.size(region) == mine.sizeBytes();
      } catch (IOException e) {
         return false;
      }
   }

   public CsLodSummary.Snapshot fold(Path dimensionDir, List<CsLodMessages.RegionEntry> advertised) {
      int count = 0;
      long aggregate = 0L;

      for (CsLodMessages.RegionEntry entry : advertised) {
         if (this.holds(dimensionDir, entry)) {
            count++;
            aggregate = CsLodSummary.fold(aggregate, entry.regionX(), entry.regionZ(), entry.hash());
         }
      }

      return new CsLodSummary.Snapshot(count, aggregate);
   }

   public void save() throws IOException {
      List<String> lines = new ArrayList<>(this.entries.size());

      for (Map.Entry<Long, CsLodManifest.Entry> entry : this.entries.entrySet()) {
         long packed = entry.getKey();
         int regionX = (int)(packed >> 32);
         int regionZ = (int)packed;
         lines.add(regionX + "," + regionZ + "=" + entry.getValue().hash() + "," + entry.getValue().sizeBytes());
      }

      Files.createDirectories(this.file.getParent());
      Path temp = this.file.resolveSibling(".manifest.part");
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

         for (String line : lines) {
            this.parse(line);
         }
      }
   }

   private void parse(String line) {
      int equals = line.indexOf(61);
      if (equals > 0) {
         String[] coords = line.substring(0, equals).split(",", -1);
         String[] values = line.substring(equals + 1).split(",", -1);
         if (coords.length == 2 && values.length == 2) {
            try {
               this.entries
                  .put(
                     key(Integer.parseInt(coords[0].trim()), Integer.parseInt(coords[1].trim())),
                     new CsLodManifest.Entry(Long.parseLong(values[0].trim()), Long.parseLong(values[1].trim()))
                  );
            } catch (NumberFormatException var6) {
            }
         }
      }
   }

   private static long key(int regionX, int regionZ) {
      return (long)regionX << 32 | regionZ & 4294967295L;
   }

   public record Entry(long hash, long sizeBytes) {
   }
}
