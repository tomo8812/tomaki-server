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

   public static Settings parse(Properties var0) {
      Settings var1 = new Settings();
      var1.min = integer(var0, "min-concurrent", 1, 1, 64);
      var1.max = integer(var0, "max-concurrent", 8, var1.min, 64);
      var1.interval = number(var0, "sample-seconds", 5.0, 2.0, 60.0);
      var1.upSamples = integer(var0, "increase-after-samples", 6, 2, 120);
      var1.downSamples = integer(var0, "decrease-after-samples", 2, 1, 10);
      var1.lowCpu = number(var0, "cpu-increase-below-percent", 65.0, 1.0, 90.0);
      var1.highCpu = number(var0, "cpu-decrease-at-percent", 80.0, var1.lowCpu + 1.0, 99.0);
      var1.emergencyCpu = number(var0, "cpu-emergency-at-percent", 95.0, var1.highCpu + 1.0, 100.0);
      var1.lowMspt = number(var0, "mspt-increase-below", 40.0, 1.0, 45.0);
      var1.highMspt = number(var0, "mspt-decrease-at", 50.0, var1.lowMspt + 1.0, 80.0);
      var1.emergencyMspt = number(var0, "mspt-emergency-at", 100.0, var1.highMspt + 1.0, 500.0);
      var1.highHeap = number(var0, "heap-decrease-at-percent", 75.0, 50.0, 90.0);
      var1.cpuCapacity = number(var0, "cpu-capacity-cores", 0.0, 0.0, 256.0);
      var1.windowSeconds = integer(var0, "throughput-window-seconds", 30, 20, 120);
      var1.settleSeconds = integer(var0, "throughput-settle-seconds", 8, 4, 30);
      var1.retrySeconds = integer(var0, "throughput-retry-seconds", 60, 30, 600);
      var1.minimumEvents = integer(var0, "throughput-minimum-events", 8, 4, 1000);
      var1.maxStep = integer(var0, "throughput-max-step", 8, 1, 16);
      var1.noProgressSeconds = integer(var0, "no-progress-seconds", 60, 30, 300);
      var1.minimumGainPercent = number(var0, "throughput-minimum-gain-percent", 5.0, 2.0, 25.0);
      var1.memoryReserveMb = number(var0, "minimum-system-headroom-mb", 512.0, 256.0, 4096.0);
      var1.gcPressurePercent = number(var0, "gc-pressure-percent", 20.0, 10.0, 50.0);
      var1.onlineMax = integer(var0, "online-max-concurrent", 2, 1, 2);
      var1.followRadius = integer(var0, "follow-radius-blocks", 4096, 128, 8192);
      var1.recenterSeconds = integer(var0, "follow-recenter-seconds", 30, 10, 600);
      var1.emptyGraceSeconds = integer(var0, "follow-empty-grace-seconds", 0, 0, 3600);
      String var2 = var0.getProperty("follow-enabled", "true");
      if (!var2.equals("true") && !var2.equals("false")) {
         throw new IllegalArgumentException("follow-enabled");
      }

      var1.followEnabled = Boolean.parseBoolean(var2);
      String var3 = var0.getProperty("generate-while-online", "false");
      if (!var3.equals("true") && !var3.equals("false")) {
         throw new IllegalArgumentException("generate-while-online");
      }

      var1.generateWhileOnline = Boolean.parseBoolean(var3);
      var1.followPlayer = var0.getProperty("follow-player", "tomo8812");
      if (!var1.followPlayer.matches("[A-Za-z0-9_]{1,16}")) {
         throw new IllegalArgumentException("follow-player");
      }

      var1.followUuid = UUID.fromString(var0.getProperty("follow-player-uuid", var1.followUuid)).toString();
      return var1;
   }

   private static double number(Properties var0, String var1, double var2, double var4, double var6) {
      double var8 = Double.parseDouble(var0.getProperty(var1, Double.toString(var2)));
      if (Double.isFinite(var8) && !(var8 < var4) && !(var8 > var6)) {
         return var8;
      } else {
         throw new IllegalArgumentException(var1);
      }
   }

   private static int integer(Properties var0, String var1, int var2, int var3, int var4) {
      int var5 = Integer.parseInt(var0.getProperty(var1, Integer.toString(var2)));
      if (var5 >= var3 && var5 <= var4) {
         return var5;
      } else {
         throw new IllegalArgumentException(var1);
      }
   }
}
