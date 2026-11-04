package com.kishku7.chunksmith.util;

import java.util.concurrent.atomic.AtomicLong;

public final class TicketLedger {
   private static final AtomicLong added = new AtomicLong();
   private static final AtomicLong removed = new AtomicLong();
   private static final AtomicLong peak = new AtomicLong();

   private TicketLedger() {
   }

   public static void noteAdd() {
      long out = added.incrementAndGet() - removed.get();
      peak.accumulateAndGet(out, Math::max);
   }

   public static void noteRemove() {
      removed.incrementAndGet();
   }

   public static long added() {
      return added.get();
   }

   public static long removed() {
      return removed.get();
   }

   public static long outstanding() {
      return added.get() - removed.get();
   }

   public static long peak() {
      return peak.get();
   }

   public static void reset() {
      added.set(0L);
      removed.set(0L);
      peak.set(0L);
   }

   public static String describe() {
      return String.format("added=%d removed=%d outstanding=%d peak=%d", added.get(), removed.get(), outstanding(), peak.get());
   }
}
