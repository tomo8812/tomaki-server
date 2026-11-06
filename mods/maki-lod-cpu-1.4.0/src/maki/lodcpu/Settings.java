package maki.lodcpu;

import java.util.Properties;
import java.util.UUID;

public final class Settings {
   public int min = 1;
   public int max = 8;
   public int upSamples = 6;
   public int downSamples = 2;
   public double interval = 5.0;
   public double lowCpu = 65.0;
   public double highCpu = 80.0;
   public double emergencyCpu = 95.0;
   public double lowMspt = 40.0;
   public double highMspt = 50.0;
   public double emergencyMspt = 100.0;
   public double highHeap = 75.0;
   public double cpuCapacity = 0.0;
   public int onlineMax = 2;
   public int followRadius = 4096;
   public int recenterSeconds = 30;
   public int emptyGraceSeconds = 0;
   public boolean followEnabled = true;
   public boolean generateWhileOnline = false;
   public String followPlayer = "tomo8812";
   public String followUuid = "9845d198-4c17-4be1-a109-b96b7e42aef9";
   public int windowSeconds = 30;
   public int settleSeconds = 8;
   public int retrySeconds = 60;
   public int minimumEvents = 8;
   public int maxStep = 8;
   public int noProgressSeconds = 60;
   public double minimumGainPercent = 5.0;
   public double memoryReserveMb = 512.0;
   public double gcPressurePercent = 20.0;
   public double backfillIoPressurePercent = 25.0;
   public double backfillMemoryPsiPressurePercent = 25.0;
   public int backfillLodQueueHigh = 64;
   public int backfillWriteQueueHigh = 32;
   public int backfillMinSpacingSeconds = 2;
   public int backfillMaxSafeWaitSeconds = 30;
   public int backfillDrainWaitSeconds = 30;
   public int backfillSampleMaxAgeSeconds = 10;

   public static Settings parse(Properties p) {
      Settings s = new Settings();
      s.min = integer(p, "min-concurrent", 1, 1, 64);
      s.max = integer(p, "max-concurrent", 8, s.min, 64);
      s.interval = number(p, "sample-seconds", 5.0, 2.0, 60.0);
      s.upSamples = integer(p, "increase-after-samples", 6, 2, 120);
      s.downSamples = integer(p, "decrease-after-samples", 2, 1, 10);
      s.lowCpu = number(p, "cpu-increase-below-percent", 65.0, 1.0, 90.0);
      s.highCpu = number(p, "cpu-decrease-at-percent", 80.0, s.lowCpu + 1.0, 99.0);
      s.emergencyCpu = number(p, "cpu-emergency-at-percent", 95.0, s.highCpu + 1.0, 100.0);
      s.lowMspt = number(p, "mspt-increase-below", 40.0, 1.0, 45.0);
      s.highMspt = number(p, "mspt-decrease-at", 50.0, s.lowMspt + 1.0, 80.0);
      s.emergencyMspt = number(p, "mspt-emergency-at", 100.0, s.highMspt + 1.0, 500.0);
      s.highHeap = number(p, "heap-decrease-at-percent", 75.0, 50.0, 90.0);
      s.cpuCapacity = number(p, "cpu-capacity-cores", 0.0, 0.0, 256.0);
      s.windowSeconds = integer(p, "throughput-window-seconds", 30, 20, 120);
      s.settleSeconds = integer(p, "throughput-settle-seconds", 8, 4, 30);
      s.retrySeconds = integer(p, "throughput-retry-seconds", 60, 30, 600);
      s.minimumEvents = integer(p, "throughput-minimum-events", 8, 4, 1000);
      s.maxStep = integer(p, "throughput-max-step", 8, 1, 16);
      s.noProgressSeconds = integer(p, "no-progress-seconds", 60, 30, 300);
      s.minimumGainPercent = number(p, "throughput-minimum-gain-percent", 5.0, 2.0, 25.0);
      s.memoryReserveMb = number(p, "minimum-system-headroom-mb", 512.0, 256.0, 4096.0);
      s.gcPressurePercent = number(p, "gc-pressure-percent", 20.0, 10.0, 50.0);
      s.backfillIoPressurePercent = number(p, "backfill-io-pressure-percent", 25.0, 1.0, 100.0);
      s.backfillMemoryPsiPressurePercent = number(p, "backfill-memory-psi-pressure-percent", 25.0, 1.0, 100.0);
      s.backfillLodQueueHigh = integer(p, "backfill-lod-queue-high", 64, 1, 100000);
      s.backfillWriteQueueHigh = integer(p, "backfill-write-queue-high", 32, 1, 100000);
      s.backfillMinSpacingSeconds = integer(p, "backfill-min-spacing-seconds", 2, 0, 60);
      int cleanFollowWindowSeconds = (int)Math.ceil(s.interval * 2.0);
      int effectiveMinSpacing = Math.max(s.backfillMinSpacingSeconds, cleanFollowWindowSeconds);
      s.backfillMaxSafeWaitSeconds = integer(p, "backfill-max-safe-wait-seconds", Math.max(30, effectiveMinSpacing), effectiveMinSpacing, 300);
      s.backfillDrainWaitSeconds = integer(p, "backfill-drain-wait-seconds", 30, 5, 300);
      s.backfillSampleMaxAgeSeconds = integer(p, "backfill-sample-max-age-seconds", 10, 4, 60);
      s.onlineMax = integer(p, "online-max-concurrent", 2, 1, 2);
      s.followRadius = integer(p, "follow-radius-blocks", 4096, 128, 8192);
      s.recenterSeconds = integer(p, "follow-recenter-seconds", 30, 10, 600);
      s.emptyGraceSeconds = integer(p, "follow-empty-grace-seconds", 0, 0, 3600);
      String enabled = p.getProperty("follow-enabled", "true");
      if (!enabled.equals("true") && !enabled.equals("false")) {
         throw new IllegalArgumentException("follow-enabled");
      }

      s.followEnabled = Boolean.parseBoolean(enabled);
      String online = p.getProperty("generate-while-online", "false");
      if (!online.equals("true") && !online.equals("false")) {
         throw new IllegalArgumentException("generate-while-online");
      }

      s.generateWhileOnline = Boolean.parseBoolean(online);
      s.followPlayer = p.getProperty("follow-player", "tomo8812");
      if (!s.followPlayer.matches("[A-Za-z0-9_]{1,16}")) {
         throw new IllegalArgumentException("follow-player");
      }

      s.followUuid = UUID.fromString(p.getProperty("follow-player-uuid", s.followUuid)).toString();
      return s;
   }

   private static double number(Properties p, String k, double d, double lo, double hi) {
      double v = Double.parseDouble(p.getProperty(k, Double.toString(d)));
      if (Double.isFinite(v) && !(v < lo) && !(v > hi)) {
         return v;
      } else {
         throw new IllegalArgumentException(k);
      }
   }

   private static int integer(Properties p, String k, int d, int lo, int hi) {
      int v = Integer.parseInt(p.getProperty(k, Integer.toString(d)));
      if (v >= lo && v <= hi) {
         return v;
      } else {
         throw new IllegalArgumentException(k);
      }
   }
}
