package com.kishku7.chunksmith.util;

import com.kishku7.chunksmith.platform.LodMode;

public final class WorldEnterLod {
   private WorldEnterLod() {
   }

   public static boolean resolve(LodMode mode, boolean rendererPresent) {
      if (mode == LodMode.ON) {
         return true;
      } else {
         return mode == LodMode.OFF ? false : rendererPresent;
      }
   }
}
