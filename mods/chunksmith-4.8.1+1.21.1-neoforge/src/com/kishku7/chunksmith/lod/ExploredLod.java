package com.kishku7.chunksmith.lod;

import java.util.concurrent.atomic.AtomicLong;

public final class ExploredLod {
   private static final AtomicLong TAKEN = new AtomicLong();
   private static final AtomicLong HELD = new AtomicLong();
   private static final AtomicLong FAILED = new AtomicLong();

   private ExploredLod() {
   }

   public static boolean shouldTake(boolean settingOn, boolean lodOn, boolean fullChunk, boolean dirty, boolean storeHasIt) {
      return settingOn && lodOn && fullChunk && dirty && !storeHasIt;
   }

   public static void noteTaken() {
      TAKEN.incrementAndGet();
   }

   public static void noteHeld() {
      HELD.incrementAndGet();
   }

   public static void noteFailed() {
      FAILED.incrementAndGet();
   }

   public static long taken() {
      return TAKEN.get();
   }

   public static String describe(boolean settingOn) {
      return !settingOn
         ? "off (lodFromExploredChunks false)"
         : TAKEN.get() + " chunks added since start, " + HELD.get() + " already in the store" + (FAILED.get() == 0L ? "" : ", " + FAILED.get() + " failed");
   }

   public static void reset() {
      TAKEN.set(0L);
      HELD.set(0L);
      FAILED.set(0L);
   }
}
