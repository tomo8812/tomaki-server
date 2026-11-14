package com.kishku7.chunksmith.lod.client;

import com.kishku7.chunksmith.lod.net.CsLodMessages;
import java.nio.file.Path;

public final class CsLodCache {
   private CsLodCache() {
   }

   public static boolean have(Path storeRoot, String dimension, CsLodManifest manifest, CsLodMessages.RegionEntry entry) {
      if (manifest == null) {
         return false;
      }

      Path dimDir = CsLodStore.dimensionDir(storeRoot, dimension);
      return dimDir == null ? false : manifest.holds(dimDir, entry);
   }
}
