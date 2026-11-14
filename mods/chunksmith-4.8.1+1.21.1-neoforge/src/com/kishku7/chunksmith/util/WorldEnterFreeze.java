package com.kishku7.chunksmith.util;

public final class WorldEnterFreeze {
   private static volatile boolean frozen;

   private WorldEnterFreeze() {
   }

   public static boolean isFrozen() {
      return frozen;
   }

   public static void set(boolean value) {
      frozen = value;
   }
}
