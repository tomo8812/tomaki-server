package maki.lodcpu;

import java.util.Locale;

final class SharedWorkBudget {
   private SharedWorkBudget.Sample sample;
   private boolean reserved;
   private boolean followDrained;
   private boolean followWasPresent;
   private boolean cleanFollowSampleRequired;
   private long reservationAtNanos;
   private long workStartedAtNanos;
   private long demandSinceNanos;
   private long nextEligibleAtNanos;
   private long transitionSerial;
   private double averageBackfillMillis = -1.0;
   private String lastReason = "warming-up";
   private String pipelinePressure;

   synchronized void pipelinePressure(String reason) {
      this.pipelinePressure = reason;
   }

   synchronized void observe(SharedWorkBudget.Sample next) {
      this.sample = next;
   }

   synchronized SharedWorkBudget.Decision request(long nowNanos, String taskMode, Settings settings) {
      if ("external".equals(taskMode)) {
         this.cancelPending(nowNanos, settings);
         this.lastReason = "external-chunksmith-task";
         return this.decision(nowNanos, false, false, this.lastReason);
      }

      String pressure = this.pipelinePressure == null ? this.pressure(nowNanos, settings) : "pipeline-" + this.pipelinePressure;
      if (pressure != null) {
         this.cancelPending(nowNanos, settings);
         this.lastReason = pressure;
         return this.decision(nowNanos, false, false, this.lastReason);
      }

      if ("none".equals(taskMode)) {
         this.cleanFollowSampleRequired = false;
      }

      if (this.demandSinceNanos == 0L) {
         this.demandSinceNanos = nowNanos;
      }

      if (!this.reserved) {
         if (this.cleanFollowSampleRequired) {
            this.lastReason = "waiting-clean-follow-sample";
            return this.decision(nowNanos, false, false, this.lastReason);
         }

         if (nowNanos < this.nextEligibleAtNanos) {
            this.lastReason = "dynamic-cooldown";
            return this.decision(nowNanos, false, false, this.lastReason);
         }

         this.reserved = true;
         this.transitionSerial++;
         this.reservationAtNanos = nowNanos;
         this.workStartedAtNanos = 0L;
         this.followWasPresent = "follow".equals(taskMode);
         this.followDrained = "none".equals(taskMode);
      }

      if ("none".equals(taskMode)) {
         this.followDrained = true;
      }

      if ("follow".equals(taskMode) && !this.followDrained) {
         if (elapsedMillis(nowNanos, this.reservationAtNanos) >= settings.backfillDrainWaitSeconds * 1000L) {
            this.cancelPending(nowNanos, settings);
            this.lastReason = "follow-drain-timeout";
            return this.decision(nowNanos, false, false, this.lastReason);
         } else {
            this.lastReason = "draining-follow-dispatch";
            return this.decision(nowNanos, true, false, this.lastReason);
         }
      } else {
         this.lastReason = "granted";
         return this.decision(nowNanos, true, true, this.lastReason);
      }
   }

   synchronized void noteFollowInFlight(int count) {
      if (this.reserved && this.workStartedAtNanos == 0L && count == 0) {
         this.followDrained = true;
         this.lastReason = "follow-drained";
      }
   }

   synchronized void noteCleanFollowSample() {
      if (!this.reserved && this.cleanFollowSampleRequired) {
         this.cleanFollowSampleRequired = false;
         this.lastReason = "clean-follow-sample";
      }
   }

   synchronized long transitionSerial() {
      return this.transitionSerial;
   }

   synchronized boolean blockFollowDispatch() {
      return this.reserved;
   }

   synchronized void backfillStarted(long nowNanos) {
      if (this.reserved && this.followDrained && this.workStartedAtNanos == 0L) {
         this.workStartedAtNanos = nowNanos;
         this.lastReason = "backfill-active";
      }
   }

   synchronized void release(long nowNanos, String outcome, Settings settings) {
      if (this.reserved) {
         if (this.workStartedAtNanos != 0L && nowNanos >= this.workStartedAtNanos) {
            double elapsed = (nowNanos - this.workStartedAtNanos) / 1000000.0;
            this.averageBackfillMillis = this.averageBackfillMillis < 0.0 ? elapsed : this.averageBackfillMillis * 0.75 + elapsed * 0.25;
         }

         double measured = this.averageBackfillMillis < 0.0 ? settings.backfillMinSpacingSeconds * 1000.0 : this.averageBackfillMillis;
         double factor = 2.0;
         SharedWorkBudget.Sample current = this.sample;
         if (current != null) {
            boolean abundant = Double.isFinite(current.mspt())
               && current.mspt() < 25.0
               && current.memoryHeadroomMb() >= settings.memoryReserveMb * 2.0
               && (current.ioPsi() < 0.0 || current.ioPsi() < settings.backfillIoPressurePercent * 0.5)
               && current.lodQueue() < Math.max(1, settings.backfillLodQueueHigh / 2)
               && current.worldWriteQueue() < Math.max(1, settings.backfillWriteQueueHigh / 2);
            if (abundant) {
               factor = 1.0;
            }

            if (current.ioPsi() >= 0.0 && current.ioPsi() >= settings.backfillIoPressurePercent * 0.5) {
               factor *= 1.5;
            }

            if (current.followRequestsPerSecond() > 0.0 && current.lodWritesPerSecond() < current.followRequestsPerSecond() * 0.8) {
               factor *= 1.5;
            }
         }

         if (outcome != null && !outcome.startsWith("completed") && !outcome.equals("no-candidate") && !outcome.equals("snapshot-complete")) {
            factor *= 2.0;
         }

         long configuredMin = settings.backfillMinSpacingSeconds * 1000L;
         long min = this.followWasPresent ? Math.max(configuredMin, cleanFollowWindowMillis(settings)) : configuredMin;
         long max = settings.backfillMaxSafeWaitSeconds * 1000L;
         long cooldown = Math.max(min, Math.min(max, Math.round(measured * factor)));
         if (this.followWasPresent) {
            cooldown = Math.max(cooldown, Math.round(elapsedMillis(nowNanos, this.reservationAtNanos) * factor));
         }

         this.nextEligibleAtNanos = nowNanos + cooldown * 1000000L;
         this.transitionSerial++;
         this.cleanFollowSampleRequired = this.followWasPresent;
         this.followWasPresent = false;
         this.reserved = false;
         this.followDrained = false;
         this.reservationAtNanos = 0L;
         this.workStartedAtNanos = 0L;
         this.demandSinceNanos = 0L;
         this.lastReason = "cooldown-after-" + (outcome == null ? "release" : outcome);
      }
   }

   synchronized boolean reserved() {
      return this.reserved;
   }

   synchronized String summary(long nowNanos) {
      SharedWorkBudget.Sample current = this.sample;
      String p = this.pipelinePressure != null
         ? "pipeline-" + this.pipelinePressure
         : (current == null ? "measurement-unavailable" : this.pressure(nowNanos, CpuGovernor.settings()));
      return "state="
         + (this.reserved ? (this.workStartedAtNanos == 0L ? "draining" : "backfill") : "follow")
         + ",reason="
         + this.lastReason
         + ",safeWaitMs="
         + elapsedMillis(nowNanos, this.demandSinceNanos)
         + ",drainMs="
         + elapsedMillis(nowNanos, this.reservationAtNanos)
         + ",avgBackfillMs="
         + value(this.averageBackfillMillis)
         + ",cooldownRemainingMs="
         + Math.max(0L, (this.nextEligibleAtNanos - nowNanos) / 1000000L)
         + ",cleanFollowSampleRequired="
         + this.cleanFollowSampleRequired
         + ",pressure="
         + (p == null ? "none" : p)
         + ",cpu="
         + value(current == null ? Double.NaN : current.cpu())
         + ",mspt="
         + value(current == null ? Double.NaN : current.mspt())
         + ",heap="
         + value(current == null ? Double.NaN : current.heap())
         + ",gc="
         + value(current == null ? Double.NaN : current.gc())
         + ",memoryHeadroomMb="
         + value(current == null ? Double.NaN : current.memoryHeadroomMb())
         + ",memoryPsi="
         + value(current == null ? Double.NaN : current.memoryPsi())
         + ",ioPsi="
         + value(current == null ? Double.NaN : current.ioPsi())
         + ",lodWritesPerSec="
         + value(current == null ? Double.NaN : current.lodWritesPerSecond())
         + ",followRequestsPerSec="
         + value(current == null ? Double.NaN : current.followRequestsPerSecond())
         + ",inFlight="
         + (current == null ? -1 : current.inFlight())
         + ",lodQueue="
         + (current == null ? -1 : current.lodQueue())
         + ",worldWriteQueue="
         + (current == null ? -1L : current.worldWriteQueue());
   }

   private String pressure(long nowNanos, Settings settings) {
      SharedWorkBudget.Sample current = this.sample;
      if (current == null) {
         return "measurement-unavailable";
      } else {
         long maxAge = settings.backfillSampleMaxAgeSeconds * 1000000000L;
         if (current.observedAtNanos() <= 0L || nowNanos < current.observedAtNanos() || nowNanos - current.observedAtNanos() > maxAge) {
            return "measurement-stale";
         } else if (current.players() > 0) {
            return "players-online";
         } else if (!Double.isFinite(current.mspt()) || current.mspt() >= settings.highMspt) {
            return "mspt-pressure";
         } else if (!Double.isFinite(current.heap()) || current.heap() >= settings.highHeap) {
            return "heap-pressure";
         } else if (current.memoryHeadroomMb() >= 0.0 && current.memoryHeadroomMb() < settings.memoryReserveMb) {
            return "memory-headroom";
         } else if (current.memoryPsi() >= 0.0 && current.memoryPsi() >= settings.backfillMemoryPsiPressurePercent) {
            return "memory-pressure";
         } else if (Double.isFinite(current.gc()) && current.gc() >= settings.gcPressurePercent) {
            return "gc-pressure";
         } else if (current.ioPsi() >= 0.0 && current.ioPsi() >= settings.backfillIoPressurePercent) {
            return "io-pressure";
         } else if (current.nativeHeld() || current.synchronousWrites() > 0L) {
            return "native-backpressure";
         } else if (current.lodQueue() >= settings.backfillLodQueueHigh) {
            return "lod-save-queue";
         } else {
            return current.worldWriteQueue() >= settings.backfillWriteQueueHigh ? "chunk-write-queue" : null;
         }
      }
   }

   private void cancelPending(long nowNanos, Settings settings) {
      if (this.reserved && this.workStartedAtNanos == 0L) {
         this.transitionSerial++;
         this.cleanFollowSampleRequired = this.followWasPresent;
         this.followWasPresent = false;
         this.reserved = false;
         this.followDrained = false;
         this.reservationAtNanos = 0L;
         this.nextEligibleAtNanos = Math.max(this.nextEligibleAtNanos, nowNanos + cleanFollowWindowMillis(settings) * 1000000L);
      }

      this.demandSinceNanos = 0L;
   }

   private static long cleanFollowWindowMillis(Settings settings) {
      return (long)Math.ceil(settings.interval * 2000.0);
   }

   private SharedWorkBudget.Decision decision(long nowNanos, boolean hasReservation, boolean ready, String reason) {
      long wait = this.demandSinceNanos == 0L ? 0L : elapsedMillis(nowNanos, this.demandSinceNanos);
      return new SharedWorkBudget.Decision(hasReservation, ready, reason, wait);
   }

   private static long elapsedMillis(long now, long then) {
      return then > 0L && now >= then ? (now - then) / 1000000L : 0L;
   }

   private static String value(double v) {
      return Double.isFinite(v) && v >= 0.0 ? String.format(Locale.ROOT, "%.1f", v) : "unknown";
   }

   record Decision(boolean reserved, boolean ready, String reason, long waitMillis) {
   }

   record Sample(
      long observedAtNanos,
      int players,
      double cpu,
      double mspt,
      double heap,
      double gc,
      double memoryHeadroomMb,
      double memoryPsi,
      double ioPsi,
      double lodWritesPerSecond,
      double followRequestsPerSecond,
      int nativeWidth,
      int inFlight,
      int lodQueue,
      long worldWriteQueue,
      long synchronousWrites,
      boolean nativeHeld
   ) {
   }
}
