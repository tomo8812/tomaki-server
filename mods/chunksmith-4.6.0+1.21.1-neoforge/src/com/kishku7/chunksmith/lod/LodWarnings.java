package com.kishku7.chunksmith.lod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LodWarnings {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static final Set<String> SAID = ConcurrentHashMap.newKeySet();

   private LodWarnings() {
   }

   public static void once(String cause, String message) {
      if (SAID.add(cause)) {
         LOGGER.warn("Chunksmith: {}", message);
      }
   }

   public static boolean saidAlready(String cause) {
      return SAID.contains(cause);
   }

   public static void reset() {
      SAID.clear();
   }
}
