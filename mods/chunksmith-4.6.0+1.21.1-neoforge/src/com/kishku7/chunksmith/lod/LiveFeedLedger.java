package com.kishku7.chunksmith.lod;

import com.kishku7.chunksmith.lod.net.CsLodRegionHash;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LiveFeedLedger {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   public static final String FILE = "live-fed.ledger";
   private static final Map<Path, Map<Long, Boolean>> SESSION = new ConcurrentHashMap<>();

   private LiveFeedLedger() {
   }

   public static void touch(Path dimensionDir, int chunkX, int chunkZ, boolean fed) {
      Map<Long, Boolean> regions = SESSION.computeIfAbsent(dimensionDir, ignored -> new ConcurrentHashMap<>());
      regions.merge(key(chunkX >> 5, chunkZ >> 5), fed, (was, now) -> was && now);
   }

   public static void finishAll() {
      for (Entry<Path, Map<Long, Boolean>> dimension : SESSION.entrySet()) {
         try {
            finish(dimension.getKey(), dimension.getValue());
         } catch (IOException e) {
            LOGGER.warn("Chunksmith: could not record which LOD regions voxy already holds in {}: {}", dimension.getKey(), e.toString());
         }
      }

      SESSION.clear();
   }

   private static void finish(Path dir, Map<Long, Boolean> touched) throws IOException {
      Map<Long, Long> ledger = read(dir);

      for (Entry<Long, Boolean> region : touched.entrySet()) {
         long key = region.getKey();
         Path file = dir.resolve("r." + regionX(key) + "." + regionZ(key) + ".cslod");
         if (region.getValue() && Files.isRegularFile(file)) {
            BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            ledger.put(key, CsLodRegionHash.of(attrs.lastModifiedTime().toMillis(), attrs.size()));
         } else {
            ledger.remove(key);
         }
      }

      List<String> lines = new ArrayList<>(ledger.size());

      for (Entry<Long, Long> entry : ledger.entrySet()) {
         lines.add(regionX(entry.getKey()) + " " + regionZ(entry.getKey()) + " " + entry.getValue());
      }

      Path tmp = dir.resolve("live-fed.ledger.tmp");
      Files.write(tmp, lines, StandardCharsets.US_ASCII);
      Files.move(tmp, dir.resolve("live-fed.ledger"), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
   }

   public static boolean vouches(Map<Long, Long> ledger, int regionX, int regionZ, long token) {
      Long recorded = ledger.get(key(regionX, regionZ));
      return recorded != null && recorded == token;
   }

   public static Map<Long, Long> read(Path dir) {
      Map<Long, Long> out = new HashMap<>();
      Path file = dir.resolve("live-fed.ledger");
      if (!Files.isRegularFile(file)) {
         return out;
      }

      try {
         for (String line : Files.readAllLines(file, StandardCharsets.US_ASCII)) {
            String[] parts = line.trim().split(" ");
            if (parts.length == 3) {
               try {
                  out.put(key(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])), Long.parseLong(parts[2]));
               } catch (NumberFormatException var7) {
               }
            }
         }
      } catch (IOException e) {
         LOGGER.warn("Chunksmith: could not read {}: {}", file, e.toString());
      }

      return out;
   }

   static long key(int regionX, int regionZ) {
      return (long)regionX << 32 | regionZ & 4294967295L;
   }

   private static int regionX(long key) {
      return (int)(key >> 32);
   }

   private static int regionZ(long key) {
      return (int)key;
   }
}
