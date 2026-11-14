package com.kishku7.chunksmith.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AutoPause {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static volatile boolean enabled = true;
   private static volatile long graceMillis = 120000L;
   private static volatile long heapThresholdPercent = 85L;
   private static final long RESUME_GRACE_CAP_MS = 5000L;
   private static final long RESUME_GRACE_MAX_MS = 60000L;
   private static final long BELOW_FLOOR_GRACE_MS = 10000L;
   private static volatile int recommendedStartWidth;
   private static volatile int failedWidth;
   private static volatile int consecutivePauses;
   private static volatile long gatedSince;
   private static volatile boolean countdownAnnounced;
   private static volatile long healthySince;
   private static volatile boolean autoPaused;
   private static volatile String pausedWorld;

   private AutoPause() {
   }

   public static void configure(boolean enabled, long graceMillis, long heapThresholdPercent) {
      AutoPause.enabled = enabled;
      AutoPause.graceMillis = Math.max(1000L, graceMillis);
      AutoPause.heapThresholdPercent = heapThresholdPercent;
   }

   public static boolean healthyNow(double mspt, double heapUsedPercent) {
      double heapResumeAt = Math.max(50.0, heapThresholdPercent - 15.0);
      boolean heapOk = heapUsedPercent >= 0.0 && heapUsedPercent < heapResumeAt;
      double target = TickBudget.effectiveTarget();
      boolean tickOk = mspt < 0.0 || target < 0.0 || mspt <= target + 3.0;
      return heapOk && tickOk;
   }

   public static String describeHealth(double mspt, double heapUsedPercent) {
      double heapResumeAt = Math.max(50.0, heapThresholdPercent - 15.0);
      double target = TickBudget.effectiveTarget();
      return String.format(
         "heap=%.1f%s (resume under %.0f) mspt=%.1f target=%s band=%.0f",
         heapUsedPercent,
         "pct",
         heapResumeAt,
         mspt,
         target < 0.0 ? "unmeasured" : String.format("%.1f", target),
         3.0
      );
   }

   public static boolean isEnabled() {
      return enabled;
   }

   public static long graceMillis() {
      return graceMillis;
   }

   public static void noteStruggling(boolean struggling, boolean atHardMinimum, long now) {
      if (!struggling) {
         if (countdownAnnounced && gatedSince != 0L && enabled && !autoPaused) {
            LOGGER.info(
               "Chunksmith: the server recovered after {}s of struggling -- the auto-pause countdown is cancelled.", Math.max(0L, (now - gatedSince) / 1000L)
            );
         }

         gatedSince = 0L;
         countdownAnnounced = false;
      } else {
         if (gatedSince == 0L) {
            gatedSince = now;
         }

         if (atHardMinimum && enabled && !autoPaused && !countdownAnnounced) {
            long left = graceMillis - (now - gatedSince);
            if (left >= 1000L) {
               LOGGER.warn(
                  "Chunksmith: the pre-gen is at its narrowest and the server still cannot sustain it. It will AUTO-PAUSE in {}s unless the server recovers first.",
                  left / 1000L
               );
               countdownAnnounced = true;
            }
         }
      }
   }

   static boolean countdownAnnounced() {
      return countdownAnnounced;
   }

   @Deprecated
   public static boolean shouldPause(long now) {
      return shouldPause(now, true);
   }

   public static boolean shouldPause(long now, boolean atHardMinimum) {
      return enabled && !autoPaused && atHardMinimum && gatedSince != 0L && now - gatedSince >= graceMillis;
   }

   public static long strugglingSeconds(long now) {
      return gatedSince == 0L ? 0L : Math.max(0L, (now - gatedSince) / 1000L);
   }

   public static void markAutoPaused(String world) {
      autoPaused = true;
      pausedWorld = world;
      gatedSince = 0L;
      countdownAnnounced = false;
      healthySince = 0L;
      consecutivePauses++;
   }

   public static boolean isAutoPaused() {
      return autoPaused;
   }

   public static String pausedWorld() {
      return pausedWorld;
   }

   public static void noteHealthy(boolean healthy, long now) {
      if (!healthy) {
         healthySince = 0L;
      } else if (healthySince == 0L) {
         healthySince = now;
      }
   }

   public static long resumeGraceMillis() {
      long base = Math.min(graceMillis, 5000L);
      int repeats = Math.max(0, consecutivePauses - 1);
      long scaled = base << Math.min(repeats, 16);
      long ceiling = Math.min(graceMillis, 60000L);
      return Math.max(Math.min(scaled, ceiling), Math.min(base, ceiling));
   }

   public static long belowFloorGraceMillis() {
      return Math.min(graceMillis, 10000L);
   }

   public static int failedWidth() {
      return failedWidth;
   }

   public static int consecutivePauses() {
      return consecutivePauses;
   }

   public static void noteSustainedHealth() {
      consecutivePauses = 0;
   }

   public static boolean shouldResume(long now) {
      return enabled && autoPaused && healthySince != 0L && now - healthySince >= resumeGraceMillis();
   }

   public static int recommendedStartWidth() {
      return recommendedStartWidth;
   }

   public static void noteWidthFailed(int width) {
      recommendedStartWidth = Math.max(1, width / 2);
      failedWidth = Math.max(0, width);
   }

   public static void logAutoResumed(String world, long healthySeconds) {
      LOGGER.warn(
         "Chunksmith: AUTO-RESUMING the pre-gen for {} -- the server has looked healthy for {}s.", world == null ? "the paused world" : world, healthySeconds
      );
   }

   public static void clearAutoPaused() {
      autoPaused = false;
      pausedWorld = null;
      healthySince = 0L;
      gatedSince = 0L;
      countdownAnnounced = false;
   }

   public static void clear() {
      autoPaused = false;
      pausedWorld = null;
      gatedSince = 0L;
      countdownAnnounced = false;
      healthySince = 0L;
      recommendedStartWidth = 0;
      failedWidth = 0;
      consecutivePauses = 0;
   }

   public static String describe() {
      return String.format(
         "enabled=%s grace=%ds resumeGrace=%ds autoPaused=%s world=%s repeats=%d failedWidth=%d",
         enabled,
         graceMillis / 1000L,
         resumeGraceMillis() / 1000L,
         autoPaused,
         pausedWorld == null ? "none" : pausedWorld,
         consecutivePauses,
         failedWidth
      );
   }
}
