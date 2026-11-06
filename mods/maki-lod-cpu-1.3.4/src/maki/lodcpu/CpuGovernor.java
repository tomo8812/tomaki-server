package maki.lodcpu;

import com.sun.management.OperatingSystemMXBean;
import java.io.Reader;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CpuGovernor {
   private static final Logger LOG = LoggerFactory.getLogger("MakiLodCpu");
   private static final Path CONFIG = Path.of("config/maki-lod-cpu.properties");
   private static final AdaptivePolicy POLICY = new AdaptivePolicy();
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

   public static void observe(WorkTelemetry work) {
      observedWork = work;
   }

   public static void tick(double mspt, int players) {
      long now = System.nanoTime();
      if (lastTime == 0L || !(now - lastTime < settings.interval * 1.0E9)) {
         reload();
         long cpu = cpuTime();
         long gc = gcMillis();
         if (lastTime == 0L) {
            lastTime = now;
            lastCpu = cpu;
            lastGc = gc;
            LOG.info(
               "Throughput governor 1.3.3 ready: allocated {} cores, ceiling {}..{}; CPU saturation alone does not reduce idle generation",
               new Object[]{capacity, settings.min, settings.max}
            );
         } else {
            double seconds = (now - lastTime) / 1.0E9;
            double used = cpu >= lastCpu && lastCpu >= 0L ? 100.0 * (cpu - lastCpu) / (now - lastTime) / capacity : Double.NaN;
            double gcPercent = gc >= lastGc && lastGc >= 0L ? 100.0 * (gc - lastGc) / (seconds * 1000.0) : Double.NaN;
            lastTime = now;
            lastCpu = cpu;
            lastGc = gc;
            MemoryUsage memory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            double heap = memory.getMax() > 0L ? 100.0 * memory.getUsed() / memory.getMax() : Double.NaN;
            observedCpu = used;
            observedMspt = mspt;
            observedHeap = heap;
            MemoryBudget budget = MemoryBudget.read();
            LodCounters lod = LodCounters.read();
            boolean comparable = previousLod != null && lod.identity() == previousLod.identity() && lod.available() && previousLod.available();
            long writes = comparable ? Math.max(0L, lod.written() - previousLod.written()) : 0L;
            if (comparable) {
               Math.max(0L, lod.bytes() - previousLod.bytes());
            } else {
               long bytes = 0L;
            }

            long sync = comparable ? Math.max(0L, lod.synchronousWrites() - previousLod.synchronousWrites()) : 0L;
            previousLod = lod;
            long oom = budget.oomKills() >= 0L && lastOom >= 0L ? Math.max(0L, budget.oomKills() - lastOom) : 0L;
            lastOom = budget.oomKills();
            if (budget.throttledMicros() >= 0L && lastThrottled >= 0L) {
               Math.max(0L, budget.throttledMicros() - lastThrottled);
            } else {
               long throttled = 0L;
            }

            lastThrottled = budget.throttledMicros();
            WorkTelemetry work = observedWork;
            WorkTelemetry.NativeState state = work == null ? null : work.state;
            boolean active = state != null && now - state.time() < 10000000000L && taskActivity != 0L && now - taskActivity < 10000000000L;
            WorkTelemetry.Batch batch = work == null ? new WorkTelemetry.Batch(0L, 0.0) : work.drain();
            int previous = cap;
            if (work != previousWork) {
               POLICY.reset(settings, "new-generation-task");
               previousWork = work;
            }

            AdaptiveSample sample = new AdaptiveSample(
               seconds,
               used,
               mspt,
               heap,
               gcPercent,
               budget.headroomMb(),
               batch.completions(),
               writes,
               comparable,
               state == null ? 0 : state.width(),
               state == null ? 0 : state.inFlight(),
               lod.queue(),
               state == null ? -1L : state.worldWrites(),
               sync,
               state != null && state.held(),
               batch.latencyP95Ms(),
               oom
            );
            if (!configGood) {
               cap = POLICY.unavailable(settings, "invalid-config");
            } else if (players > 0) {
               cap = POLICY.update(sample, players, settings);
            } else if (!active) {
               cap = POLICY.reset(settings, "generation-idle");
            } else {
               cap = POLICY.update(sample, players, settings);
            }

            boolean experimentFinished = reportedExperiment != POLICY.experimentNumber;
            if (experimentFinished) {
               LOG.info(
                  "LOD throughput experiment #{}: {}; decision {}; rates are completed-request AND successful-LOD-write pipeline chunks/s",
                  new Object[]{POLICY.experimentNumber, POLICY.lastExperiment, POLICY.reason}
               );
               reportedExperiment = POLICY.experimentNumber;
            }

            if (previous != cap || experimentFinished || now - lastReport >= 30000000000L) {
               LOG.info(
                  "LOD CPU ceiling {} -> {}; CPU {}% of {} cores, MSPT {}, heap {}%, players {}, reason {} (follow-owned); all LOD saved {}/s ({} in sample), follow requests {}/s ({} in sample), GC {}%, native width {}, in-flight {}, LOD queue {}, phase {}",
                  new Object[]{
                     previous,
                     cap,
                     fmt(used),
                     fmt(capacity),
                     fmt(mspt),
                     fmt(heap),
                     players,
                     POLICY.reason,
                     fmt(writes / seconds),
                     writes,
                     fmt(batch.completions() / seconds),
                     batch.completions(),
                     fmt(gcPercent),
                     sample.nativeWidth(),
                     sample.inFlight(),
                     sample.lodQueue(),
                     POLICY.phase
                  }
               );
               lastReport = now;
            }
         }
      }
   }

   private static String fmt(double n) {
      return String.format(Locale.ROOT, "%.1f", n);
   }

   private static long cpuTime() {
      return ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean os ? os.getProcessCpuTime() : -1L;
   }

   private static long gcMillis() {
      long sum = 0L;
      boolean found = false;

      for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
         long n = bean.getCollectionTime();
         if (n >= 0L) {
            sum += n;
            found = true;
         }
      }

      return found ? sum : -1L;
   }

   static double detectCapacity() {
      double cores = Runtime.getRuntime().availableProcessors();

      try {
         cores = CpuTopology.restrict(cores, Files.readString(Path.of("/sys/devices/system/cpu/online")));
      } catch (Exception var10) {
      }

      try {
         String allowed = Files.readAllLines(Path.of("/proc/self/status"))
            .stream()
            .filter(line -> line.startsWith("Cpus_allowed_list:"))
            .findFirst()
            .orElseThrow();
         cores = CpuTopology.restrict(cores, allowed.substring(allowed.indexOf(58) + 1));
      } catch (Exception var9) {
      }

      try {
         String[] max = Files.readString(Path.of("/sys/fs/cgroup/cpu.max")).trim().split("\\s+");
         if (!max[0].equals("max")) {
            cores = Math.min(cores, Double.parseDouble(max[0]) / Double.parseDouble(max[1]));
         }
      } catch (Exception ignored) {
         try {
            double quota = Double.parseDouble(Files.readString(Path.of("/sys/fs/cgroup/cpu/cpu.cfs_quota_us")).trim());
            double period = Double.parseDouble(Files.readString(Path.of("/sys/fs/cgroup/cpu/cpu.cfs_period_us")).trim());
            if (quota > 0.0 && period > 0.0) {
               cores = Math.min(cores, quota / period);
            }
         } catch (Exception var7) {
         }
      }

      return Math.max(0.1, cores);
   }

   private static void reload() {
      try {
         long stamp = Files.getLastModifiedTime(CONFIG).toMillis();
         if (stamp == lastStamp) {
            return;
         }

         Properties p = new Properties();

         try (Reader reader = Files.newBufferedReader(CONFIG)) {
            p.load(reader);
         }

         Settings fresh = Settings.parse(p);
         settings = fresh;
         capacity = fresh.cpuCapacity > 0.0 ? fresh.cpuCapacity : detectCapacity();
         lastStamp = stamp;
         configGood = true;
         cap = POLICY.reset(settings, "config-reloaded");
         LOG.info(
            "Loaded {}; CPU budget {} cores, ceiling {}..{}; A/B/A saved-LOD throughput window {}s, settle {}s",
            new Object[]{CONFIG, capacity, fresh.min, fresh.max, fresh.windowSeconds, fresh.settleSeconds}
         );
      } catch (Exception e) {
         if (configGood) {
            LOG.error("Throughput governor config missing/invalid; limiting generation to 1 until fixed: {}", e.toString());
         }

         configGood = false;
      }
   }

   private static Object finite(double n) {
      return Double.isFinite(n) ? n : null;
   }
}
