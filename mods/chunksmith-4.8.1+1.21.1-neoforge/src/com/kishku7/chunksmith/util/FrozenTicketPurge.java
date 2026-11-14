package com.kishku7.chunksmith.util;

public final class FrozenTicketPurge {
   public static final long BYTES_PER_CHUNK = 131072L;
   public static final double CAP_HEAP_FRACTION = 0.35;
   public static final double FULL_AFTER_COLLECTION_PERCENT = 70.0;
   public static final int MIN_LEARN_RESIDENT = 1024;
   public static final double LEARN_FRACTION = 0.6;
   public static final double RESUME_FRACTION = 0.8;
   public static final long UNLOAD_BUDGET_NANOS = 10000000L;
   private static final int CAP_OVERRIDE = Integer.getInteger("chunksmith.test.frozenPurgeCap", -1);
   private boolean purging;
   private int switchesOn;
   private boolean holdSeen;
   private int learnedCap = Integer.MAX_VALUE;

   public static int capFor(long maxHeapBytes) {
      long cap = (long)(Math.max(0L, maxHeapBytes) * 0.35) / 131072L;
      return (int)Math.min(2147483647L, cap);
   }

   public static int effectiveCap(long maxHeapBytes) {
      return CAP_OVERRIDE >= 0 ? CAP_OVERRIDE : capFor(maxHeapBytes);
   }

   public boolean tick(int resident, long maxHeapBytes, boolean heapFull) {
      if (heapFull) {
         if (!this.holdSeen) {
            this.holdSeen = true;
            if (resident >= 1024) {
               this.learnedCap = Math.min(this.learnedCap, (int)(resident * 0.6));
            }
         }

         if (!this.purging) {
            this.purging = true;
            this.switchesOn++;
         }

         return true;
      } else {
         this.holdSeen = false;
         int cap = Math.min(effectiveCap(maxHeapBytes), this.learnedCap);
         if (!this.purging && resident > cap) {
            this.purging = true;
            this.switchesOn++;
         } else if (this.purging && resident <= (int)(cap * 0.8)) {
            this.purging = false;
         }

         return this.purging;
      }
   }

   public int learnedCap() {
      return this.learnedCap;
   }

   public int switchesOn() {
      return this.switchesOn;
   }

   public void reset() {
      this.purging = false;
      this.switchesOn = 0;
      this.holdSeen = false;
      this.learnedCap = Integer.MAX_VALUE;
   }
}
