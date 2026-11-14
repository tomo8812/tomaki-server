package com.kishku7.chunksmith.util;

public final class TickBudget {
   public static final double MSPT_BAND = 3.0;
   private static final double ALPHA = 0.1;
   private static final double MIN_ALLOWANCE_MS = 5.0;
   private static final double MAX_ALLOWANCE_FACTOR = 3.0;
   private static volatile double baselineMspt = -1.0;
   private static volatile double ourCostMspt = -1.0;
   private static volatile int lastPlayerCount = -1;
   private static volatile long minAllowanceMillis = 25L;
   private static volatile long playerReserveMillis = 20L;
   private static volatile long ceilingMillis = 150L;
   private static final long PROBE_INTERVAL_MS = 60000L;
   private static final long PROBE_DURATION_MS = 2000L;
   private static final int IDLE_TICKS_BEFORE_TRUSTED = 15;
   private static volatile int consecutiveIdleTicks;
   private static volatile long lastProbeEndedAt;
   private static volatile long probeStartedAt;

   private TickBudget() {
   }

   public static void configure(long minAllowanceMillis, long playerReserveMillis, long ceilingMillis) {
      TickBudget.minAllowanceMillis = Math.max(0L, minAllowanceMillis);
      TickBudget.playerReserveMillis = Math.max(0L, playerReserveMillis);
      TickBudget.ceilingMillis = Math.max(0L, ceilingMillis);
   }

   public static void sample(double mspt, boolean ourWorkInFlight, int players) {
      if (players != lastPlayerCount) {
         lastPlayerCount = players;
         baselineMspt = -1.0;
         ourCostMspt = -1.0;
         consecutiveIdleTicks = 0;
      }

      if (!(mspt < 0.0)) {
         if (!ourWorkInFlight) {
            if (++consecutiveIdleTicks >= 15 || !(baselineMspt >= 0.0)) {
               baselineMspt = baselineMspt < 0.0 ? mspt : baselineMspt * 0.9 + mspt * 0.1;
            }
         } else {
            consecutiveIdleTicks = 0;
            if (!(baselineMspt < 0.0)) {
               double cost = Math.max(0.0, mspt - baselineMspt);
               ourCostMspt = ourCostMspt < 0.0 ? cost : ourCostMspt * 0.9 + cost * 0.1;
            }
         }
      }
   }

   public static double baseline() {
      return baselineMspt;
   }

   public static double ourCost() {
      return ourCostMspt;
   }

   public static double allowance() {
      double doubled = ourCostMspt < 0.0 ? minAllowanceMillis : ourCostMspt * 2.0;
      double ceiling = Math.max(minAllowanceMillis, 1.0) * 3.0;
      double granted = Math.min(ceiling, Math.max(doubled, minAllowanceMillis));
      double reserved = (double)lastPlayerCount * playerReserveMillis;
      return Math.max(5.0, granted - Math.max(0.0, reserved));
   }

   public static double effectiveTarget() {
      if (baselineMspt < 0.0) {
         return -1.0;
      }

      double adaptive = baselineMspt + allowance();
      return ceilingMillis <= 0L ? adaptive : Math.min(adaptive, ceilingMillis);
   }

   public static boolean atCeiling() {
      return ceilingMillis > 0L && baselineMspt >= 0.0 && baselineMspt + allowance() > ceilingMillis;
   }

   public static boolean shouldProbe(long now) {
      if (probeStartedAt != 0L) {
         if (now - probeStartedAt < 2000L) {
            return true;
         }

         probeStartedAt = 0L;
         lastProbeEndedAt = now;
         return false;
      } else if (lastProbeEndedAt == 0L) {
         lastProbeEndedAt = now;
         return false;
      } else if (now - lastProbeEndedAt >= 60000L) {
         probeStartedAt = now;
         return true;
      } else {
         return false;
      }
   }

   public static boolean isProbing() {
      return probeStartedAt != 0L;
   }

   public static void reset() {
      baselineMspt = -1.0;
      ourCostMspt = -1.0;
      lastPlayerCount = -1;
      consecutiveIdleTicks = 0;
      probeStartedAt = 0L;
      lastProbeEndedAt = 0L;
   }

   public static String describe() {
      return String.format(
            "baseline=%s ourCost=%s allowance=%.1fms target=%s players=%d reservePerPlayer=%dms probing=%s",
            baselineMspt < 0.0 ? "unmeasured" : String.format("%.1fms", baselineMspt),
            ourCostMspt < 0.0 ? "unmeasured" : String.format("%.1fms", ourCostMspt),
            allowance(),
            effectiveTarget() < 0.0 ? "unmeasured" : String.format("%.1fms", effectiveTarget()),
            Math.max(0, lastPlayerCount),
            playerReserveMillis,
            probeStartedAt != 0L
         )
         + (atCeiling() ? String.format(" AT-CEILING(%dms)", ceilingMillis) : "");
   }
}
