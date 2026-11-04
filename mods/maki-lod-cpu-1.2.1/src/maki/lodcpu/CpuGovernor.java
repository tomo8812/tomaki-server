package maki.lodcpu;

import com.google.gson.Gson;
import com.sun.management.OperatingSystemMXBean;
import java.io.BufferedReader;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CpuGovernor {
   private static final Logger LOG = LoggerFactory.getLogger("MakiLodCpu");
   private static final Path CONFIG = Path.of("config/maki-lod-cpu.properties");
   private static final AdaptivePolicy POLICY = new AdaptivePolicy();
   private static final Gson JSON = new Gson();
   private static Settings settings = new Settings();
   private static volatile int cap = 2;
   private static volatile long taskActivity;
   private static volatile WorkTelemetry observedWork;
   private static WorkTelemetry previousWork;
   private static LodCounters previousLod;
   private static long lastTime;
   private static long lastCpu;
   private static long lastGc = -1L;
   private static long lastOom = -1L;
   private static long lastThrottled = -1L;
   private static long lastStamp = Long.MIN_VALUE;
   private static long lastReport;
   private static long reportedExperiment;
   private static boolean configGood = true;
   private static double capacity = detectCapacity();
   private static double observedCpu = Double.NaN;
   private static double observedMspt = Double.NaN;
   private static double observedHeap = 100.0;

   public static Settings settings() {
      return settings;
   }

   public static boolean healthyConfig() {
      return configGood;
   }

   public static boolean onlineOverload() {
      return !configGood || !Double.isFinite(observedCpu) || observedCpu >= 98.0 || observedMspt >= 45.0 || observedHeap >= settings.highHeap;
   }

   public static boolean recovered() {
      return configGood && Double.isFinite(observedCpu) && observedCpu < 80.0 && observedMspt < 25.0 && observedHeap < settings.highHeap - 10.0;
   }

   public static int cap() {
      return cap;
   }

   public static void markActive() {
      taskActivity = System.nanoTime();
   }

   public static void observe(WorkTelemetry var0) {
      observedWork = var0;
   }

   public static void tick(double var0, int var2) {
      long var3 = System.nanoTime();
      if (lastTime == 0L || !(var3 - lastTime < settings.interval * 1.0E9)) {
         reload();
         long var5 = cpuTime();
         long var7 = gcMillis();
         if (lastTime == 0L) {
            lastTime = var3;
            lastCpu = var5;
            lastGc = var7;
            LOG.info(
               "Throughput governor 1.2.1 ready: allocated {} cores, ceiling {}..{}; CPU saturation alone does not reduce idle generation",
               new Object[]{capacity, settings.min, settings.max}
            );
         } else {
            double var9 = (var3 - lastTime) / 1.0E9;
            double var11 = var5 >= lastCpu && lastCpu >= 0L ? 100.0 * (var5 - lastCpu) / (var3 - lastTime) / capacity : Double.NaN;
            double var13 = var7 >= lastGc && lastGc >= 0L ? 100.0 * (var7 - lastGc) / (var9 * 1000.0) : Double.NaN;
            lastTime = var3;
            lastCpu = var5;
            lastGc = var7;
            MemoryUsage var15 = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            double var16 = var15.getMax() > 0L ? 100.0 * var15.getUsed() / var15.getMax() : Double.NaN;
            observedCpu = var11;
            observedMspt = var0;
            observedHeap = var16;
            MemoryBudget var18 = MemoryBudget.read();
            LodCounters var19 = LodCounters.read();
            boolean var20 = previousLod != null && var19.identity() == previousLod.identity() && var19.available() && previousLod.available();
            long var21 = var20 ? Math.max(0L, var19.written() - previousLod.written()) : 0L;
            long var23 = var20 ? Math.max(0L, var19.bytes() - previousLod.bytes()) : 0L;
            long var25 = var20 ? Math.max(0L, var19.synchronousWrites() - previousLod.synchronousWrites()) : 0L;
            previousLod = var19;
            long var27 = var18.oomKills() >= 0L && lastOom >= 0L ? Math.max(0L, var18.oomKills() - lastOom) : 0L;
            lastOom = var18.oomKills();
            long var29 = var18.throttledMicros() >= 0L && lastThrottled >= 0L ? Math.max(0L, var18.throttledMicros() - lastThrottled) : 0L;
            lastThrottled = var18.throttledMicros();
            WorkTelemetry var31 = observedWork;
            WorkTelemetry.NativeState var32 = var31 == null ? null : var31.state;
            boolean var33 = var32 != null && var3 - var32.time() < 10000000000L && taskActivity != 0L && var3 - taskActivity < 10000000000L;
            WorkTelemetry.Batch var34 = var31 == null ? new WorkTelemetry.Batch(0L, 0.0) : var31.drain();
            int var35 = cap;
            if (var31 != previousWork) {
               POLICY.reset(settings, "new-generation-task");
               previousWork = var31;
            }

            AdaptiveSample var36 = new AdaptiveSample(
               var9,
               var11,
               var0,
               var16,
               var13,
               var18.headroomMb(),
               var34.completions(),
               var21,
               var20,
               var32 == null ? 0 : var32.width(),
               var32 == null ? 0 : var32.inFlight(),
               var19.queue(),
               var32 == null ? -1L : var32.worldWrites(),
               var25,
               var32 != null && var32.held(),
               var34.latencyP95Ms(),
               var27
            );
            if (!configGood) {
               cap = POLICY.unavailable(settings, "invalid-config");
            } else if (var2 > 0) {
               cap = POLICY.update(var36, var2, settings);
            } else if (!var33) {
               cap = POLICY.reset(settings, "generation-idle");
            } else {
               cap = POLICY.update(var36, var2, settings);
            }

            boolean var37 = reportedExperiment != POLICY.experimentNumber;
            if (var37) {
               LOG.info(
                  "LOD throughput experiment #{}: {}; decision {}; rates are completed-request AND successful-LOD-write pipeline chunks/s",
                  new Object[]{POLICY.experimentNumber, POLICY.lastExperiment, POLICY.reason}
               );
               reportedExperiment = POLICY.experimentNumber;
            }

            if (var35 != cap || var37 || var3 - lastReport >= 30000000000L) {
               LOG.info(
                  "LOD CPU ceiling {} -> {}; CPU {}% of {} cores, MSPT {}, heap {}%, players {}, reason {}; LOD saved {}/s, requests {}/s, GC {}%, native width {}, in-flight {}, LOD queue {}, phase {}",
                  new Object[]{
                     var35,
                     cap,
                     fmt(var11),
                     fmt(capacity),
                     fmt(var0),
                     fmt(var16),
                     var2,
                     POLICY.reason,
                     fmt(var21 / var9),
                     fmt(var34.completions() / var9),
                     fmt(var13),
                     var36.nativeWidth(),
                     var36.inFlight(),
                     var36.lodQueue(),
                     POLICY.phase
                  }
               );
               report(var36, var32, var18, var23, var29, var2, var33);
               lastReport = var3;
            }
         }
      }
   }

   private static String fmt(double var0) {
      return String.format(Locale.ROOT, "%.1f", var0);
   }

   private static long cpuTime() {
      return ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean var1 ? var1.getProcessCpuTime() : -1L;
   }

   private static long gcMillis() {
      long var0 = 0L;
      boolean var2 = false;

      for (GarbageCollectorMXBean var4 : ManagementFactory.getGarbageCollectorMXBeans()) {
         long var5 = var4.getCollectionTime();
         if (var5 >= 0L) {
            var0 += var5;
            var2 = true;
         }
      }

      return var2 ? var0 : -1L;
   }

   static double detectCapacity() {
      double var0 = Runtime.getRuntime().availableProcessors();

      try {
         var0 = CpuTopology.restrict(var0, Files.readString(Path.of("/sys/devices/system/cpu/online")));
      } catch (Exception var10) {
      }

      try {
         String var2 = Files.readAllLines(Path.of("/proc/self/status"))
            .stream()
            .filter(var0x -> var0x.startsWith("Cpus_allowed_list:"))
            .findFirst()
            .orElseThrow();
         var0 = CpuTopology.restrict(var0, var2.substring(var2.indexOf(58) + 1));
      } catch (Exception var9) {
      }

      try {
         String[] var11 = Files.readString(Path.of("/sys/fs/cgroup/cpu.max")).trim().split("\\s+");
         if (!var11[0].equals("max")) {
            var0 = Math.min(var0, Double.parseDouble(var11[0]) / Double.parseDouble(var11[1]));
         }
      } catch (Exception var8) {
         try {
            double var3 = Double.parseDouble(Files.readString(Path.of("/sys/fs/cgroup/cpu/cpu.cfs_quota_us")).trim());
            double var5 = Double.parseDouble(Files.readString(Path.of("/sys/fs/cgroup/cpu/cpu.cfs_period_us")).trim());
            if (var3 > 0.0 && var5 > 0.0) {
               var0 = Math.min(var0, var3 / var5);
            }
         } catch (Exception var7) {
         }
      }

      return Math.max(0.1, var0);
   }

   private static void reload() {
      try {
         long var0 = Files.getLastModifiedTime(CONFIG).toMillis();
         if (var0 == lastStamp) {
            return;
         }

         Properties var2 = new Properties();

         try (BufferedReader var3 = Files.newBufferedReader(CONFIG)) {
            var2.load(var3);
         }

         Settings var9 = Settings.parse(var2);
         settings = var9;
         capacity = var9.cpuCapacity > 0.0 ? var9.cpuCapacity : detectCapacity();
         lastStamp = var0;
         configGood = true;
         cap = POLICY.reset(settings, "config-reloaded");
         LOG.info(
            "Loaded {}; CPU budget {} cores, ceiling {}..{}; A/B/A saved-LOD throughput window {}s, settle {}s",
            new Object[]{CONFIG, capacity, var9.min, var9.max, var9.windowSeconds, var9.settleSeconds}
         );
      } catch (Exception var8) {
         if (configGood) {
            LOG.error("Throughput governor config missing/invalid; limiting generation to 1 until fixed: {}", var8.toString());
         }

         configGood = false;
      }
   }

   private static Object finite(double var0) {
      return Double.isFinite(var0) ? var0 : null;
   }

   private static void report(AdaptiveSample var0, WorkTelemetry.NativeState var1, MemoryBudget var2, long var3, long var5, int var7, boolean var8) {
      try {
         LinkedHashMap var9 = new LinkedHashMap();
         var9.put("time", Instant.now().toString());
         var9.put("controllerVersion", "1.2.1");
         var9.put("cpuBudgetCores", capacity);
         var9.put("cpuBudgetUsedPercent", finite(var0.cpu()));
         var9.put("mspt", finite(var0.mspt()));
         var9.put("heapUsedPercent", finite(var0.heap()));
         var9.put("players", var7);
         var9.put("cpuConcurrencyCeiling", cap);
         var9.put("reason", POLICY.reason);
         var9.put("active", var8);
         var9.put("nativeDispatchWidth", var0.nativeWidth());
         var9.put("inFlight", var0.inFlight());
         var9.put("lodSavedPerSecond", finite(var0.lodWrites() / var0.seconds()));
         var9.put("lodSavedBytesPerSecond", finite(var3 / var0.seconds()));
         var9.put("requestCompletionsPerSecond", finite(var0.completions() / var0.seconds()));
         var9.put("throughputBasis", var0.storeAvailable() ? "successful-native-LOD-store-writes" : "completed-requests-fallback");
         var9.put("lodWriteQueue", var0.lodQueue());
         var9.put("worldWriteQueue", var0.worldWriteQueue());
         var9.put("synchronousLodWrites", var0.synchronousWrites());
         var9.put("nativeBackpressure", var0.nativeHeld());
         var9.put("gcTimePercent", finite(var0.gc()));
         var9.put("nativeWriteHeld", var1 != null && var1.writeHeld());
         var9.put("nativeResidencyHeld", var1 != null && var1.residencyHeld());
         var9.put("nativeHeapHeld", var1 != null && var1.heapHeld());
         var9.put("nativeAddedChunks", var1 == null ? -1L : var1.addedChunks());
         var9.put("systemMemoryHeadroomMb", var2.headroomMb());
         var9.put("processRssMb", var2.rssMb());
         var9.put("memoryPsiSomeAvg10", var2.memoryPsi());
         var9.put("ioPsiSomeAvg10", var2.ioPsi());
         var9.put("cgroupThrottledMicrosInSample", var5);
         var9.put("cgroupOomKillsInSample", var0.oomKills());
         var9.put("requestLatencyP95BucketUpperMs", var0.latencyP95Ms());
         var9.put("experimentPhase", POLICY.phase);
         var9.put("lastWindowRate", POLICY.lastWindowRate);
         var9.put("baselineRate", POLICY.baselineRate);
         var9.put("probeRate", POLICY.probeRate);
         var9.put("confirmedBaselineRate", POLICY.confirmedBaselineRate);
         var9.put("lastExperiment", POLICY.lastExperiment);
         var9.put("experimentNumber", POLICY.experimentNumber);
         var9.put("experimentRetryRemainingSeconds", POLICY.retryRemainingSeconds());
         Path var10 = Path.of("logs/maki-lod-cpu-status.json.tmp");
         Path var11 = Path.of("logs/maki-lod-cpu-status.json");
         Files.writeString(var10, JSON.toJson(var9) + "\n");

         try {
            Files.move(var10, var11, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException var13) {
            Files.move(var10, var11, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Exception var14) {
         LOG.debug("Cannot write throughput status", var14);
      }
   }
}
