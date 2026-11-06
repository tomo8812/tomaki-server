package maki.lodcpu;

final class SharedWorkBudget {
    record Sample(
        long observedAtNanos,
        int players,
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
    ) {}

    record Decision(boolean reserved, boolean ready, String reason, long safeWaitMillis) {}

    private Sample sample;
    private boolean reserved;
    private long reservationAtNanos;
    private long workStartedAtNanos;
    private long nextEligibleAtNanos;
    private long demandSinceNanos;
    private double averageBackfillMillis = -1.0;
    private String lastReason = "warming-up";

    synchronized void observe(Sample next) {
        sample = next;
    }

    synchronized Decision reserve(long nowNanos, String taskMode, int baseCap, Settings settings) {
        if (reserved && workStartedAtNanos != 0L) {
            lastReason = "backfill-active";
            return new Decision(true, true, lastReason, safeWaitMillis(nowNanos));
        }

        if (!"none".equals(taskMode) && !"follow".equals(taskMode)) {
            cancelPending();
            lastReason = "external-chunksmith-task";
            return new Decision(false, false, lastReason, 0L);
        }

        String pressure = pressure(nowNanos, settings);
        if (pressure != null) {
            cancelPending();
            lastReason = pressure;
            return new Decision(false, false, lastReason, 0L);
        }

        if (demandSinceNanos == 0L) {
            demandSinceNanos = nowNanos;
        }

        if (!reserved) {
            if (nowNanos < nextEligibleAtNanos) {
                lastReason = "dynamic-cooldown";
                return new Decision(false, false, lastReason, safeWaitMillis(nowNanos));
            }

            long safeWait = safeWaitMillis(nowNanos);
            if ("follow".equals(taskMode) && baseCap <= 1
                && safeWait < settings.backfillMaxSafeWaitSeconds * 1_000L) {
                lastReason = "follow-width-one-fairness-wait";
                return new Decision(false, false, lastReason, safeWait);
            }

            reserved = true;
            reservationAtNanos = nowNanos;
            workStartedAtNanos = 0L;
        }

        int effectiveCap = effectiveFollowCap(baseCap);
        if ("follow".equals(taskMode) && sample != null && sample.inFlight() > effectiveCap) {
            lastReason = "follow-draining-to-reserved-cap";
            return new Decision(true, false, lastReason, safeWaitMillis(nowNanos));
        }

        lastReason = "granted";
        return new Decision(true, true, lastReason, safeWaitMillis(nowNanos));
    }

    synchronized void backfillStarted(long nowNanos) {
        if (reserved && workStartedAtNanos == 0L) {
            workStartedAtNanos = nowNanos;
            lastReason = "backfill-active";
        }
    }

    synchronized void release(long nowNanos, String outcome, Settings settings) {
        if (!reserved) {
            return;
        }

        if (workStartedAtNanos != 0L && nowNanos >= workStartedAtNanos) {
            double elapsedMillis = (nowNanos - workStartedAtNanos) / 1_000_000.0;
            averageBackfillMillis = averageBackfillMillis < 0.0
                ? elapsedMillis
                : averageBackfillMillis * 0.75 + elapsedMillis * 0.25;
        }

        double measuredMillis = averageBackfillMillis < 0.0
            ? settings.backfillMinSpacingSeconds * 1_000.0
            : averageBackfillMillis;
        double factor = 1.0;

        if (sample != null) {
            if (sample.ioPsi() >= 0.0
                && sample.ioPsi() >= settings.backfillIoPressurePercent * 0.5) {
                factor *= 1.5;
            }
            if (sample.followRequestsPerSecond() > 0.0
                && sample.lodWritesPerSecond() < sample.followRequestsPerSecond() * 0.8) {
                factor *= 1.5;
            }
        }

        if (outcome != null && !outcome.startsWith("completed") && !outcome.equals("no-candidate")) {
            factor *= 2.0;
        }

        long minimum = settings.backfillMinSpacingSeconds * 1_000L;
        long maximum = settings.backfillMaxSafeWaitSeconds * 1_000L;
        long dynamicMillis = Math.max(minimum, Math.min(maximum, Math.round(measuredMillis * factor)));
        nextEligibleAtNanos = nowNanos + dynamicMillis * 1_000_000L;

        reserved = false;
        reservationAtNanos = 0L;
        workStartedAtNanos = 0L;
        demandSinceNanos = 0L;
        lastReason = "cooldown-after-" + (outcome == null ? "release" : outcome);
    }

    synchronized boolean reserved() {
        return reserved;
    }

    synchronized int effectiveFollowCap(int baseCap) {
        return reserved && baseCap > 1 ? baseCap - 1 : baseCap;
    }

    synchronized String summary(long nowNanos, int baseCap) {
        Sample current = sample;
        String pressure = current == null ? "measurement-unavailable" : pressure(nowNanos, null);
        return "state=" + (reserved ? (workStartedAtNanos == 0L ? "reserved" : "active") : "idle")
            + ",reason=" + lastReason
            + ",baseCap=" + baseCap
            + ",followCap=" + effectiveFollowCap(baseCap)
            + ",safeWaitMs=" + safeWaitMillis(nowNanos)
            + ",avgBackfillMs=" + rounded(averageBackfillMillis)
            + ",pressure=" + (pressure == null ? "none" : pressure)
            + ",ioPsi=" + (current == null ? "unknown" : rounded(current.ioPsi()))
            + ",memoryPsi=" + (current == null ? "unknown" : rounded(current.memoryPsi()))
            + ",lodWritesPerSec=" + (current == null ? "unknown" : rounded(current.lodWritesPerSecond()))
            + ",followRequestsPerSec=" + (current == null ? "unknown" : rounded(current.followRequestsPerSecond()))
            + ",inFlight=" + (current == null ? -1 : current.inFlight())
            + ",lodQueue=" + (current == null ? -1 : current.lodQueue())
            + ",worldWriteQueue=" + (current == null ? -1 : current.worldWriteQueue());
    }

    private String pressure(long nowNanos, Settings settings) {
        Sample current = sample;
        if (current == null) {
            return "measurement-unavailable";
        }

        Settings limits = settings == null ? CpuGovernor.settings() : settings;
        long maxAgeNanos = limits.backfillSampleMaxAgeSeconds * 1_000_000_000L;
        if (current.observedAtNanos() <= 0L || nowNanos < current.observedAtNanos()
            || nowNanos - current.observedAtNanos() > maxAgeNanos) {
            return "measurement-stale";
        }
        if (current.players() > 0) {
            return "players-online";
        }
        if (!Double.isFinite(current.mspt()) || current.mspt() >= limits.highMspt) {
            return "mspt-pressure";
        }
        if (!Double.isFinite(current.heap()) || current.heap() >= limits.highHeap) {
            return "heap-pressure";
        }
        if (current.memoryHeadroomMb() >= 0.0
            && current.memoryHeadroomMb() < limits.memoryReserveMb) {
            return "memory-headroom";
        }
        if (Double.isFinite(current.gc()) && current.gc() >= limits.gcPressurePercent) {
            return "gc-pressure";
        }
        if (current.ioPsi() >= 0.0 && current.ioPsi() >= limits.backfillIoPressurePercent) {
            return "io-pressure";
        }
        if (current.nativeHeld() || current.synchronousWrites() > 0L) {
            return "native-backpressure";
        }
        if (current.lodQueue() >= limits.backfillLodQueueHigh) {
            return "lod-save-queue";
        }
        if (current.worldWriteQueue() >= limits.backfillWriteQueueHigh) {
            return "chunk-write-queue";
        }
        return null;
    }

    private long safeWaitMillis(long nowNanos) {
        return demandSinceNanos == 0L || nowNanos < demandSinceNanos
            ? 0L
            : (nowNanos - demandSinceNanos) / 1_000_000L;
    }

    private void cancelPending() {
        if (reserved && workStartedAtNanos == 0L) {
            reserved = false;
            reservationAtNanos = 0L;
        }
        demandSinceNanos = 0L;
    }

    private static String rounded(double value) {
        if (!Double.isFinite(value) || value < 0.0) {
            return "unknown";
        }
        return String.format(java.util.Locale.ROOT, "%.1f", value);
    }
}
