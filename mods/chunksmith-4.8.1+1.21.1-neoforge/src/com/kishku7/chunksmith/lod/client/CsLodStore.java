package com.kishku7.chunksmith.lod.client;

import java.nio.file.Path;
import java.util.regex.Pattern;

public final class CsLodStore {
   private static final Pattern DIM_DIR = Pattern.compile("[a-z0-9_.-]{1,64}");

   private CsLodStore() {
   }

   public static Path dimensionDir(Path storeRoot, String dimension) {
      if (storeRoot != null && dimension != null && !dimension.isEmpty() && DIM_DIR.matcher(dimension).matches()) {
         Path root = storeRoot.normalize();
         Path dir = root.resolve(dimension).normalize();
         return dir.startsWith(root) ? dir : null;
      } else {
         return null;
      }
   }
}
