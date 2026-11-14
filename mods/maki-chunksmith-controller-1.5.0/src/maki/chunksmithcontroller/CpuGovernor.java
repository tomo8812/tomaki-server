package maki.chunksmithcontroller;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.platform.Config;
import com.sun.management.OperatingSystemMXBean;
import java.io.IOException;
import java.io.Reader;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CpuGovernor {
   private static final Logger LOG = LoggerFactory.getLogger("MakiChunksmithController");
   private static final Path CONFIG = Path.of("config/maki-chunksmith-controller.properties");
   private static final Path PREGEN_PROFILE = Path.of("config/maki-chunksmith-controller-pregen-profile.properties");
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
   private static long previousPairConfirmed = -1L;
   private static long previousPairNew = -1L;
   private static long previousPairRepair = -1L;
   private static long previousUncertain = -1L;
   private static WorkTelemetry profileWork;
   private static boolean profileDecisionMade;
   private static long profileNewPairs;
   private static long profileRepairPairs;
   private static long lastProfileSavedMillis;
   private static boolean configGood = true;
   private static double capacity = detectCapacity();
   private static double latestHeadroom = -1.0;
   private static double latestGc = Double.NaN;

   public static Settings settings() {
      return settings;
   }

   public static boolean healthyConfig() {
      return configGood;
   }

   public static int cap() {
      return cap;
   }

   public static boolean permitsWork(double mspt) {
      MemoryUsage memory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
      double heap = memory.getMax() > 0L ? 100.0 * memory.getUsed() / memory.getMax() : 0.0;
      return configGood
         && mspt < settings.highMspt
         && heap < settings.highHeap
         && (latestHeadroom < 0.0 || latestHeadroom >= settings.memoryReserveMb)
         && (!Double.isFinite(latestGc) || latestGc < settings.gcPressurePercent);
   }

   private static CpuGovernor.ControllerObservation controllerObservation() {
      PregenFlowController.Snapshot state = PregenFlowController.snapshot();
      return new CpuGovernor.ControllerObservation(
         true,
         state.pairConfirmedRuntime(),
         state.pairNew(),
         state.pairRepair(),
         state.uncertain(),
         state.oldestMillis(),
         state.blocked(),
         state.sensorUnknown(),
         state.fastSignature(),
         "elite-local-control"
      );
   }

   private static void notifyGenerationCapChanged(int nextCap) {
      PregenFlowController.generationCapChanged(nextCap);
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
               "Maki Chunksmith Controller E-lite ready: allocated {} cores, ceiling {}..{}; generation uses stage WIP and confirmed FULL/LOD results",
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
            MemoryBudget budget = MemoryBudget.read();
            latestHeadroom = budget.headroomMb();
            latestGc = gcPercent;
            LodCounters lod = LodCounters.read();
            boolean comparable = previousLod != null && lod.identity() == previousLod.identity() && lod.available() && previousLod.available();
            long writes = comparable ? Math.max(0L, lod.written() - previousLod.written()) : 0L;
            long sync = comparable ? Math.max(0L, lod.synchronousWrites() - previousLod.synchronousWrites()) : 0L;
            previousLod = lod;
            long oom = budget.oomKills() >= 0L && lastOom >= 0L ? Math.max(0L, budget.oomKills() - lastOom) : 0L;
            long throttled = budget.throttledMicros() >= 0L && lastThrottled >= 0L ? Math.max(0L, budget.throttledMicros() - lastThrottled) : 0L;
            lastOom = budget.oomKills();
            lastThrottled = budget.throttledMicros();
            WorkTelemetry work = observedWork;
            WorkTelemetry.NativeState state = work == null ? null : work.state;
            boolean stateFresh = state != null && now >= state.time() && now - state.time() < 10000000000L;
            boolean active = stateFresh && taskActivity != 0L && now - taskActivity < 10000000000L;
            WorkTelemetry.Batch batch = work == null ? new WorkTelemetry.Batch(0L, 0.0) : work.drain();
            int previous = cap;
            if (work != previousWork) {
               POLICY.reset(settings, "new-generation-task");
               previousWork = work;
               profileWork = work;
               profileDecisionMade = false;
               profileRepairPairs = 0L;
               profileNewPairs = 0L;
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
               stateFresh ? state.width() : 0,
               stateFresh ? state.inFlight() : 0,
               lod.queue(),
               stateFresh ? state.worldWrites() : -1L,
               sync,
               stateFresh && state.held(),
               batch.latencyP95Ms(),
               oom,
               throttled
            );
            CpuGovernor.ControllerObservation controller = controllerObservation();
            long pairDelta = controller.available() && previousPairConfirmed >= 0L ? Math.max(0L, controller.pairConfirmed() - previousPairConfirmed) : 0L;
            long newDelta = controller.available() && previousPairNew >= 0L ? Math.max(0L, controller.pairNew() - previousPairNew) : 0L;
            long repairDelta = controller.available() && previousPairRepair >= 0L ? Math.max(0L, controller.pairRepair() - previousPairRepair) : 0L;
            long uncertainDelta = controller.available() && previousUncertain >= 0L ? Math.max(0L, controller.uncertain() - previousUncertain) : 0L;
            long unknownDelta = Math.max(0L, pairDelta - newDelta - repairDelta);
            previousPairConfirmed = controller.available() ? controller.pairConfirmed() : -1L;
            previousPairNew = controller.available() ? controller.pairNew() : -1L;
            previousPairRepair = controller.available() ? controller.pairRepair() : -1L;
            previousUncertain = controller.available() ? controller.uncertain() : -1L;
            boolean pairAvailable = controller.available() && !controller.sensorUnknown();
            if (work == profileWork && controller.available()) {
               profileNewPairs += newDelta;
               profileRepairPairs += repairDelta;
            }

            String workClassBand = PregenPerformanceProfile.workClassBand(profileNewPairs, profileRepairPairs);
            if (!profileDecisionMade && active && players == 0 && pairAvailable && !"UNKNOWN".equals(workClassBand)) {
               PregenPerformanceProfile.Environment environment = performanceEnvironment(controller.fastSignature(), workClassBand);
               OptionalInt warm = PregenPerformanceProfile.load(PREGEN_PROFILE, environment, settings, System.currentTimeMillis());
               if (warm.isPresent()) {
                  cap = POLICY.warmStart(settings, warm.getAsInt(), "warm-start-profile");
                  LOG.info(
                     "Applied pregen c warm-start profile: c={}, workClassBand={}, fastSignature={}",
                     new Object[]{cap, workClassBand, controller.fastSignature()}
                  );
               }

               profileDecisionMade = true;
            }

            if (!configGood) {
               cap = POLICY.unavailable(settings, "invalid-config");
            } else if (players > 0) {
               cap = POLICY.update(
                  sample,
                  players,
                  settings,
                  pairDelta,
                  pairAvailable,
                  controller.admissionBlocked(),
                  controller.fastSignature(),
                  newDelta,
                  repairDelta,
                  uncertainDelta,
                  controller.oldestMillis()
               );
            } else if (!active) {
               cap = POLICY.reset(settings, "generation-idle");
            } else {
               cap = POLICY.update(
                  sample,
                  players,
                  settings,
                  pairDelta,
                  pairAvailable,
                  controller.admissionBlocked(),
                  controller.fastSignature(),
                  newDelta,
                  repairDelta,
                  uncertainDelta,
                  controller.oldestMillis()
               );
            }

            if (previous != cap) {
               notifyGenerationCapChanged(cap);
            }

            boolean experimentFinished = reportedExperiment != POLICY.experimentNumber;
            if (experimentFinished) {
               LOG.info(
                  "LOD c-neighbourhood experiment #{}: {}; decision {}; rate is confirmed runtime FULL+LOD PAIR/s",
                  new Object[]{POLICY.experimentNumber, POLICY.lastExperiment, POLICY.reason}
               );
               reportedExperiment = POLICY.experimentNumber;
            }

            long wallNow = System.currentTimeMillis();
            if (active
               && players == 0
               && pairAvailable
               && !controller.admissionBlocked()
               && !"UNKNOWN".equals(workClassBand)
               && "baseline".equals(POLICY.phase)
               && wallNow - lastProfileSavedMillis >= 300000L) {
               PregenPerformanceProfile.Environment environment = performanceEnvironment(controller.fastSignature(), workClassBand);
               if (PregenPerformanceProfile.save(PREGEN_PROFILE, environment, cap, wallNow)) {
                  lastProfileSavedMillis = wallNow;
               }
            }

            if (previous != cap || experimentFinished || now - lastReport >= 30000000000L) {
               LOG.info(
                  "LOD CPU ceiling {} -> {}; CPU {}% of {} cores, MSPT {}, heap {}%, players {}, reason {} (follow-owned); confirmed PAIR {}/s ({} in sample; new {}, reused FULL {}, unclassified {}), all LOD saved {}/s ({} in sample), follow requests {}/s ({} in sample), GC {}%, native width {}, in-flight {}, LOD queue {}, phase {}, admissionBlocked={}, controllerReason={}",
                  new Object[]{
                     previous,
                     cap,
                     fmt(used),
                     fmt(capacity),
                     fmt(mspt),
                     fmt(heap),
                     players,
                     POLICY.reason,
                     fmt(pairDelta / seconds),
                     pairDelta,
                     newDelta,
                     repairDelta,
                     unknownDelta,
                     fmt(writes / seconds),
                     writes,
                     fmt(batch.completions() / seconds),
                     batch.completions(),
                     fmt(gcPercent),
                     sample.nativeWidth(),
                     sample.inFlight(),
                     sample.lodQueue(),
                     POLICY.phase,
                     controller.admissionBlocked(),
                     controller.reason()
                  }
               );
               lastReport = now;
            }
         }
      }
   }

   private static PregenPerformanceProfile.Environment performanceEnvironment(long fastSignature, String workClassBand) {
      long heapMax = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
      return new PregenPerformanceProfile.Environment(
         "native-full-v8", cpuAffinity(), cpuQuota(), capacity, heapMax, chunksmithConfigSignature(), pairFeedVersion(), fastSignature, workClassBand
      );
   }

   private static String cpuAffinity() {
      try {
         return Files.readAllLines(Path.of("/proc/self/status"))
            .stream()
            .filter(line -> line.startsWith("Cpus_allowed_list:"))
            .findFirst()
            .map(line -> line.substring(line.indexOf(58) + 1).trim())
            .orElse("unknown");
      } catch (Exception unavailable) {
         return "unknown";
      }
   }

   private static String cpuQuota() {
      try {
         return Files.readString(Path.of("/sys/fs/cgroup/cpu.max")).trim();
      } catch (Exception ignored) {
         try {
            return Files.readString(Path.of("/sys/fs/cgroup/cpu/cpu.cfs_quota_us")).trim()
               + "/"
               + Files.readString(Path.of("/sys/fs/cgroup/cpu/cpu.cfs_period_us")).trim();
         } catch (Exception unavailable) {
            return "unknown";
         }
      }
   }

   private static String chunksmithConfigSignature() {
      try {
         Config config = ChunksmithProvider.get().getConfig();
         return config.isIoThrottleEnabled() + "|" + config.getThrottleMaxChunkMillis() + "|" + config.getDispatchMaxConcurrent();
      } catch (Throwable unavailable) {
         return "unknown";
      }
   }

   private static String pairFeedVersion() {
      return "E_LITE_FULL_PROVENANCE_V2";
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

   private static void migrateConfig() throws IOException {
      Path legacy = Path.of("config/maki-lod-cpu.properties");
      if (!Files.exists(CONFIG) && Files.isRegularFile(legacy)) {
         Files.createDirectories(CONFIG.getParent());

         try {
            Files.move(legacy, CONFIG, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException unsupported) {
            Files.move(legacy, CONFIG);
         }

         LOG.info("Migrated Chunksmith controller config to {}", CONFIG);
      }
   }

   private static void reload() {
      try {
         migrateConfig();
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
         profileDecisionMade = false;
         profileRepairPairs = 0L;
         profileNewPairs = 0L;
         LOG.info(
            "Loaded {}; CPU budget {} cores, ceiling {}..{}; confirmed-PAIR c-neighbourhood window {}s, settle {}s",
            new Object[]{CONFIG, capacity, fresh.min, fresh.max, fresh.windowSeconds, fresh.settleSeconds}
         );
      } catch (Exception e) {
         if (configGood) {
            LOG.error("Throughput governor config missing/invalid; limiting generation until fixed: {}", e.toString());
         }

         configGood = false;
      }
   }

   private record ControllerObservation(
      boolean available,
      long pairConfirmed,
      long pairNew,
      long pairRepair,
      long uncertain,
      long oldestMillis,
      boolean admissionBlocked,
      boolean sensorUnknown,
      long fastSignature,
      String reason
   ) {
   }
}
