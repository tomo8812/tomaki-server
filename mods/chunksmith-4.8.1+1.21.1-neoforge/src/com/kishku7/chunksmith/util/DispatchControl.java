package com.kishku7.chunksmith.util;

public final class DispatchControl {
   public static final int MIN_WORKING_WIDTH = 1;
   public static final int MIN_USEFUL_WIDTH = 8;

   private DispatchControl() {
   }

   public static int reduce(int current, boolean sustained) {
      return reduce(current, sustained, false);
   }

   public static int reduce(int current, boolean sustained, boolean mayGoBelowFloor) {
      if (current <= 8) {
         return mayGoBelowFloor ? Math.max(1, current - 1) : Math.max(1, current);
      }

      int next = sustained ? current / 2 : current - 1;
      return Math.max(8, next);
   }

   public static int resumeThreshold(int maxWorkingCount, int failedWidth) {
      return failedWidth <= 0 ? maxWorkingCount : Math.max(8, Math.min(maxWorkingCount, failedWidth));
   }

   public static boolean mayBurst(int current, int goodWidth) {
      return current < goodWidth;
   }
}
