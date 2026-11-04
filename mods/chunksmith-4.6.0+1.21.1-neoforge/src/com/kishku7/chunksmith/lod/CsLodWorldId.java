package com.kishku7.chunksmith.lod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class CsLodWorldId {
   public static final String FILE_NAME = "world-id";
   private static final Pattern VALID = Pattern.compile("[0-9a-f]{32}");
   private static final Map<Path, String> CACHE = new ConcurrentHashMap<>();

   private CsLodWorldId() {
   }

   public static String forStore(Path storeRootBase) {
      if (storeRootBase == null) {
         return "";
      }

      Path key = storeRootBase.toAbsolutePath().normalize();
      String cached = CACHE.get(key);
      if (cached != null) {
         return cached;
      }

      String id = load(key);
      if (!id.isEmpty()) {
         CACHE.put(key, id);
      }

      return id;
   }

   public static void forget(Path storeRootBase) {
      if (storeRootBase != null) {
         CACHE.remove(storeRootBase.toAbsolutePath().normalize());
      }
   }

   public static boolean isValid(String id) {
      return id != null && VALID.matcher(id).matches();
   }

   private static String load(Path storeRootBase) {
      Path file = storeRootBase.resolve("world-id");

      try {
         if (Files.isRegularFile(file)) {
            String existing = Files.readString(file, StandardCharsets.UTF_8).trim();
            if (isValid(existing)) {
               return existing;
            }
         }

         Files.createDirectories(storeRootBase);
         String minted = UUID.randomUUID().toString().replace("-", "");
         Files.writeString(file, minted + System.lineSeparator(), StandardCharsets.UTF_8);
         return minted;
      } catch (IOException e) {
         return "";
      }
   }
}
