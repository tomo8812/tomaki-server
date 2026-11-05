package maki.lagobserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;

public final class LagObserver {
   private static final Logger LOG = System.getLogger("MakiLagObserver");
   private static final ThreadMXBean THREADS = ManagementFactory.getThreadMXBean();
   private static final Path CONFIG = Path.of("config/maki-lag-observer.properties");
   private static final Path LOG_FILE = Path.of("logs/maki-lag-observer.jsonl");
   private static final long THREAD_CPU_BASELINE_INTERVAL_NANOS = 1000000000L;
   private static final Object WRITE_LOCK = new Object();
   private static final Map<Long, Long> PREVIOUS_THREAD_CPU = new HashMap<>();
   private static volatile MinecraftServer owner;
   private static volatile Thread serverThread;
   private static volatile Thread watchdog;
   private static volatile boolean running;
   private static volatile boolean tickInProgress;
   private static volatile long tickSequence;
   private static volatile long tickStartNanos;
   private static volatile long tickStartCpuNanos;
   private static volatile long lastTickStartNanos;
   private static volatile long lastTickEndNanos;
   private static volatile long lastTickEndCpuNanos = -1L;
   private static volatile long lastStallSampleNanos;
   private static volatile LagObserver.TickSnapshot tickState = LagObserver.TickSnapshot.initial();
   private static volatile long threadCpuBaselineNanos;
   private static volatile LagObserver.Settings settings = LagObserver.Settings.defaults();
   private static volatile long configStamp = Long.MIN_VALUE;
   private static volatile boolean writeWarningLogged;

   private LagObserver() {
   }

   public static void tickStart(MinecraftServer server) {
      if (owner != null && owner != server) {
         shutdown(owner);
      }

      owner = server;
      serverThread = Thread.currentThread();
      ensureCpuTimeEnabled();
      reloadSettings();
      ensureWatchdog();
      long now = System.nanoTime();
      long previous = lastTickStartNanos;
      long previousCpu = tickStartCpuNanos;
      long cpuNow = cpuTime(serverThread);
      lastTickStartNanos = now;
      tickStartNanos = now;
      tickStartCpuNanos = cpuNow;
      tickInProgress = true;
      long sequence = tickSequence + 1L;
      tickSequence = sequence;
      LagObserver.TickSnapshot snapshot = new LagObserver.TickSnapshot(sequence, true, now, cpuNow, lastTickEndNanos, lastTickEndCpuNanos);
      tickState = snapshot;
      LagObserver.Settings s = settings;
      if (s.enabled && previous != 0L) {
         long gap = now - previous;
         if (gap >= millisToNanos(s.tickStartGapThresholdMs)) {
            writeEvent(server, snapshot, "tick-start-gap", gap, cpuDelta(previousCpu, cpuNow), null, hottestThreads());
         }
      }
   }

   public static void tickEnd(MinecraftServer server) {
      if (owner == server) {
         long now = System.nanoTime();
         long started = tickStartNanos;
         long cpuStarted = tickStartCpuNanos;
         long cpuNow = cpuTime(serverThread);
         lastTickEndNanos = now;
         lastTickEndCpuNanos = cpuNow;
         tickInProgress = false;
         LagObserver.TickSnapshot snapshot = new LagObserver.TickSnapshot(tickSequence, false, started, cpuStarted, now, cpuNow);
         tickState = snapshot;
         if (started != 0L && settings.enabled) {
            long elapsed = Math.max(0L, now - started);
            if (elapsed >= millisToNanos(settings.longTickThresholdMs)) {
               writeEvent(server, snapshot, "long-tick", elapsed, cpuDelta(cpuStarted, cpuNow), null, LagObserver.HotThreadSample.empty());
            }
         }
      }
   }

   public static void shutdown(MinecraftServer server) {
      if (owner == server) {
         running = false;
         Thread w = watchdog;
         if (w != null) {
            w.interrupt();
         }

         watchdog = null;
         tickInProgress = false;
         lastTickEndCpuNanos = -1L;
         tickState = LagObserver.TickSnapshot.stopped(tickSequence);
         owner = null;
         serverThread = null;
         synchronized (PREVIOUS_THREAD_CPU) {
            PREVIOUS_THREAD_CPU.clear();
            threadCpuBaselineNanos = 0L;
         }
      }
   }

   private static void ensureWatchdog() {
      if (watchdog == null || !watchdog.isAlive()) {
         running = true;
         Thread thread = new Thread(LagObserver::watchLoop, "maki-lag-observer-watchdog");
         thread.setDaemon(true);
         thread.setPriority(1);
         primeThreadCpuSnapshot();
         watchdog = thread;
         thread.start();
         LOG.log(Level.INFO, "Lag observer active; stall samples are written to " + LOG_FILE + " only when thresholds are exceeded");
      }
   }

   private static void watchLoop() {
      while (running) {
         LagObserver.Settings beforeSleep = settings;

         try {
            Thread.sleep(beforeSleep.watchdogIntervalMs);
         } catch (InterruptedException interrupted) {
            if (!running) {
               return;
            }
         }

         reloadSettings();
         LagObserver.Settings s = settings;
         MinecraftServer server = owner;
         Thread thread = serverThread;
         if (s.enabled && server != null && thread != null) {
            long now = System.nanoTime();
            LagObserver.TickSnapshot snapshot = tickState;
            long age = snapshot.inProgress() ? now - snapshot.tickStartNanos() : (snapshot.lastTickEndNanos() == 0L ? 0L : now - snapshot.lastTickEndNanos());
            if (age < millisToNanos(s.stallThresholdMs)) {
               refreshThreadCpuBaselineIfDue(now);
            } else if (now - lastStallSampleNanos >= millisToNanos(s.stallSampleIntervalMs)) {
               String event = snapshot.inProgress() ? "stall-sample" : "between-ticks-sample";
               if (capture(server, snapshot, event, age, true)) {
                  lastStallSampleNanos = now;
               }
            }
         }
      }
   }

   private static boolean capture(MinecraftServer server, LagObserver.TickSnapshot snapshot, String event, long wallNanos, boolean includeHotThreads) {
      Thread thread = serverThread;
      if (thread != null && tickState == snapshot) {
         ThreadInfo info = THREADS.getThreadInfo(thread.threadId(), settings.maxStackFrames);
         long cpuNow = cpuTime(thread);
         long sampledAt = System.nanoTime();
         long cpu = snapshot.inProgress() ? cpuDelta(snapshot.tickStartCpuNanos(), cpuNow) : cpuDelta(snapshot.lastTickEndCpuNanos(), cpuNow);
         long effectiveWall = snapshot.inProgress()
            ? Math.max(wallNanos, sampledAt - snapshot.tickStartNanos())
            : Math.max(wallNanos, sampledAt - snapshot.lastTickEndNanos());
         LagObserver.HotThreadSample hot = includeHotThreads ? hottestThreads() : LagObserver.HotThreadSample.empty();
         if (tickState == snapshot && owner == server && serverThread == thread) {
            writeEvent(server, snapshot, event, effectiveWall, cpu, info, hot);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static void writeEvent(
      MinecraftServer server,
      LagObserver.TickSnapshot snapshot,
      String event,
      long wallNanos,
      long serverCpuNanos,
      ThreadInfo info,
      LagObserver.HotThreadSample hotThreadSample
   ) {
      try {
         Map<String, Object> root = new LinkedHashMap<>();
         root.put("time", Instant.now().toString());
         root.put("event", event);
         root.put("tickSequence", snapshot.sequence());
         root.put("tickInProgress", snapshot.inProgress());
         root.put("wallMs", nanosToMillis(wallNanos));
         root.put("serverThreadCpuMs", serverCpuNanos < 0L ? null : nanosToMillis(serverCpuNanos));
         root.put("serverThreadCpuToWallPercent", serverCpuNanos >= 0L && wallNanos > 0L ? round1(100.0 * serverCpuNanos / wallNanos) : null);
         root.put("averageTickMs", round1(server.getAverageTickTimeNanos() / 1000000.0));
         root.put("players", server.getPlayerCount());
         root.put("frozen", server.tickRateManager().isFrozen());
         root.put("lastTickEndAgoMs", snapshot.lastTickEndNanos() == 0L ? null : nanosToMillis(Math.max(0L, System.nanoTime() - snapshot.lastTickEndNanos())));
         if (info != null) {
            root.put("serverThreadState", info.getThreadState().name());
            root.put("serverThreadLock", info.getLockName());
            root.put("serverThreadLockOwner", info.getLockOwnerName());
            root.put("serverThreadBlockedCount", info.getBlockedCount());
            root.put("serverThreadWaitedCount", info.getWaitedCount());
            ArrayList<String> stack = new ArrayList<>();

            for (StackTraceElement frame : info.getStackTrace()) {
               stack.add(frame.toString());
            }

            root.put("serverThreadStack", stack);
         }

         root.put("hotThreadWindowMs", hotThreadSample.windowNanos() < 0L ? null : nanosToMillis(hotThreadSample.windowNanos()));
         root.put("hotThreads", hotThreadSample.threads().stream().map(LagObserver.HotThread::asMap).toList());
         root.put("process", processMetrics());
         root.put("jvm", jvmMetrics());
         root.put("linux", linuxMetrics());
         root.put("modState", modState());
         String line = LagObserver.Json.encode(root) + System.lineSeparator();
         synchronized (WRITE_LOCK) {
            Files.createDirectories(LOG_FILE.getParent());
            Files.writeString(LOG_FILE, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         }

         writeWarningLogged = false;
      } catch (Throwable error) {
         if (!writeWarningLogged) {
            writeWarningLogged = true;
            LOG.log(Level.WARNING, "Could not write lag diagnostic sample: " + error);
         }
      }
   }

   private static Map<String, Object> processMetrics() {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();
      OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
      out.put("availableProcessors", Runtime.getRuntime().availableProcessors());
      out.put("systemLoadAverage", finite(bean.getSystemLoadAverage()));
      if (bean instanceof com.sun.management.OperatingSystemMXBean os) {
         out.put("processCpuLoadPercent", finitePercent(os.getProcessCpuLoad()));
         out.put("systemCpuLoadPercent", finitePercent(os.getCpuLoad()));
         out.put("processCpuTimeMs", nanosToMillis(os.getProcessCpuTime()));
         out.put("committedVirtualMemoryMb", bytesToMb(os.getCommittedVirtualMemorySize()));
         out.put("freePhysicalMemoryMb", bytesToMb(os.getFreeMemorySize()));
         out.put("totalPhysicalMemoryMb", bytesToMb(os.getTotalMemorySize()));
      }

      return out;
   }

   private static Map<String, Object> jvmMetrics() {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();
      MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
      out.put("heapUsedMb", bytesToMb(heap.getUsed()));
      out.put("heapMaxMb", bytesToMb(heap.getMax()));
      out.put("heapUsedPercent", heap.getMax() > 0L ? round1(100.0 * heap.getUsed() / heap.getMax()) : null);
      long gcCount = 0L;
      long gcMillis = 0L;

      for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
         if (gc.getCollectionCount() >= 0L) {
            gcCount += gc.getCollectionCount();
         }

         if (gc.getCollectionTime() >= 0L) {
            gcMillis += gc.getCollectionTime();
         }
      }

      out.put("gcCount", gcCount);
      out.put("gcTimeMs", gcMillis);
      return out;
   }

   private static Map<String, Object> linuxMetrics() {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();
      out.put("loadavg", readTrimmed("/proc/loadavg"));
      out.put("cpuPressure", pressure("/proc/pressure/cpu"));
      out.put("ioPressure", pressure("/proc/pressure/io"));
      out.put("memoryPressure", pressure("/proc/pressure/memory"));
      out.put("cgroupCpu", cgroupV2Cpu());
      out.put("cgroupCpuLegacy", legacyCgroupCpu());
      out.put("procCpu", procCpu());
      out.put("procIo", keyValues("/proc/self/io"));
      out.put("processStatus", selectedProcessStatus());
      out.put("serverTaskStatus", serverTaskStatus());
      out.put("taskContextSwitchTotals", taskContextSwitchTotals());
      return out;
   }

   private static Map<String, Object> cgroupV2Cpu() {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();
      Path root = Path.of("/sys/fs/cgroup");
      Path current = resolveUnifiedCgroup(root);
      out.put("path", cgroupDisplayPath(root, current));
      out.put("cpuMax", readTrimmed(current.resolve("cpu.max").toString()));
      out.put("stat", keyValues(current.resolve("cpu.stat").toString()));
      out.put("ancestors", cgroupV2Ancestors(root, current));
      return out;
   }

   private static List<Map<String, Object>> cgroupV2Ancestors(Path root, Path current) {
      ArrayList<Map<String, Object>> ancestors = new ArrayList<>();

      for (Path node = current.getParent(); node != null && node.startsWith(root); node = node.getParent()) {
         String cpuMax = readTrimmed(node.resolve("cpu.max").toString());
         Map<String, Object> stat = keyValues(node.resolve("cpu.stat").toString());
         if (cpuMax != null || !stat.isEmpty()) {
            LinkedHashMap<String, Object> entry = new LinkedHashMap<>();
            entry.put("path", cgroupDisplayPath(root, node));
            entry.put("cpuMax", cpuMax);
            entry.put("stat", stat);
            ancestors.add(entry);
         }

         if (node.equals(root)) {
            break;
         }
      }

      return ancestors;
   }

   private static Path resolveUnifiedCgroup(Path root) {
      try {
         for (String line : Files.readAllLines(Path.of("/proc/self/cgroup"))) {
            if (line.startsWith("0::")) {
               return safeCgroupPath(root, line.substring(3));
            }
         }
      } catch (IOException var3) {
      }

      return root;
   }

   private static Map<String, Object> legacyCgroupCpu() {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();
      Path root = legacyCpuRoot();
      if (root == null) {
         return out;
      }

      Path current = resolveLegacyCpuCgroup(root);
      out.put("path", cgroupDisplayPath(root, current));
      out.put("quotaUs", parseNumberOrNull(readTrimmed(current.resolve("cpu.cfs_quota_us").toString())));
      out.put("periodUs", parseNumberOrNull(readTrimmed(current.resolve("cpu.cfs_period_us").toString())));
      out.put("stat", keyValues(current.resolve("cpu.stat").toString()));
      out.put("ancestors", legacyCpuAncestors(root, current));
      return out;
   }

   private static Path legacyCpuRoot() {
      for (String name : List.of("cpu", "cpu,cpuacct", "cpuacct,cpu")) {
         Path candidate = Path.of("/sys/fs/cgroup").resolve(name);
         if (Files.isDirectory(candidate)) {
            return candidate;
         }
      }

      return null;
   }

   private static Path resolveLegacyCpuCgroup(Path root) {
      try {
         for (String line : Files.readAllLines(Path.of("/proc/self/cgroup"))) {
            String[] parts = line.split(":", 3);
            if (parts.length == 3) {
               for (String controller : parts[1].split(",")) {
                  if (controller.equals("cpu") || controller.equals("cpuacct")) {
                     return safeCgroupPath(root, parts[2]);
                  }
               }
            }
         }
      } catch (IOException var8) {
      }

      return root;
   }

   private static List<Map<String, Object>> legacyCpuAncestors(Path root, Path current) {
      ArrayList<Map<String, Object>> ancestors = new ArrayList<>();

      for (Path node = current.getParent(); node != null && node.startsWith(root); node = node.getParent()) {
         String quota = readTrimmed(node.resolve("cpu.cfs_quota_us").toString());
         String period = readTrimmed(node.resolve("cpu.cfs_period_us").toString());
         Map<String, Object> stat = keyValues(node.resolve("cpu.stat").toString());
         if (quota != null || period != null || !stat.isEmpty()) {
            LinkedHashMap<String, Object> entry = new LinkedHashMap<>();
            entry.put("path", cgroupDisplayPath(root, node));
            entry.put("quotaUs", parseNumberOrNull(quota));
            entry.put("periodUs", parseNumberOrNull(period));
            entry.put("stat", stat);
            ancestors.add(entry);
         }

         if (node.equals(root)) {
            break;
         }
      }

      return ancestors;
   }

   private static Path safeCgroupPath(Path root, String membership) {
      Path candidate = root.resolve(membership.replaceFirst("^/+", "")).normalize();
      return candidate.startsWith(root) && Files.isDirectory(candidate) ? candidate : root;
   }

   private static String cgroupDisplayPath(Path root, Path node) {
      return node.equals(root) ? "/" : "/" + root.relativize(node).toString().replace('\\', '/');
   }

   private static Map<String, Object> procCpu() {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();

      try {
         String line = Files.readAllLines(Path.of("/proc/stat")).stream().filter(v -> v.startsWith("cpu ")).findFirst().orElse(null);
         if (line == null) {
            return out;
         }

         String[] parts = line.trim().split("\\s+");
         String[] names = new String[]{"user", "nice", "system", "idle", "iowait", "irq", "softirq", "steal", "guest", "guestNice"};

         for (int i = 1; i < parts.length && i <= names.length; i++) {
            out.put(names[i - 1] + "Jiffies", parseNumber(parts[i]));
         }
      } catch (IOException var5) {
      }

      return out;
   }

   private static Object parseNumberOrNull(String value) {
      return value == null ? null : parseNumber(value);
   }

   private static Map<String, Object> selectedProcessStatus() {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();
      String allowed = statusField(Path.of("/proc/self/status"), "Cpus_allowed_list");
      if (allowed != null) {
         out.put("Cpus_allowed_list", allowed);
      }

      return out;
   }

   private static Map<String, Object> serverTaskStatus() {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();
      Thread thread = serverThread;
      if (thread == null) {
         return out;
      }

      String expectedComm = linuxTaskComm(thread.getName());
      out.put("javaThreadName", thread.getName());
      out.put("expectedComm", expectedComm);
      ArrayList<Path> matches = new ArrayList<>();
      Path taskRoot = Path.of("/proc/self/task");

      try (Stream<Path> tasks = Files.list(taskRoot)) {
         for (Path task : tasks.filter(x$0 -> Files.isDirectory(x$0)).toList()) {
            String comm = readTrimmed(task.resolve("comm").toString());
            if (expectedComm.equals(comm)) {
               matches.add(task);
            }
         }
      } catch (IOException var11) {
      }

      out.put("matchCount", matches.size());
      if (matches.size() != 1) {
         return out;
      }

      Path task = matches.get(0);
      out.put("nativeTid", parseNumber(task.getFileName().toString()));
      String allowed = statusField(task.resolve("status"), "Cpus_allowed_list");
      String voluntary = statusField(task.resolve("status"), "voluntary_ctxt_switches");
      String nonvoluntary = statusField(task.resolve("status"), "nonvoluntary_ctxt_switches");
      if (allowed != null) {
         out.put("Cpus_allowed_list", allowed);
      }

      if (voluntary != null) {
         out.put("voluntary_ctxt_switches", parseNumber(voluntary));
      }

      if (nonvoluntary != null) {
         out.put("nonvoluntary_ctxt_switches", parseNumber(nonvoluntary));
      }

      return out;
   }

   private static Map<String, Object> taskContextSwitchTotals() {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();
      long voluntary = 0L;
      long nonvoluntary = 0L;
      int sampledTasks = 0;
      Path taskRoot = Path.of("/proc/self/task");

      try (Stream<Path> tasks = Files.list(taskRoot)) {
         for (Path task : tasks.filter(x$0 -> Files.isDirectory(x$0)).toList()) {
            String voluntaryRaw = statusField(task.resolve("status"), "voluntary_ctxt_switches");
            String nonvoluntaryRaw = statusField(task.resolve("status"), "nonvoluntary_ctxt_switches");
            if (voluntaryRaw != null && nonvoluntaryRaw != null) {
               try {
                  voluntary += Long.parseLong(voluntaryRaw);
                  nonvoluntary += Long.parseLong(nonvoluntaryRaw);
                  sampledTasks++;
               } catch (NumberFormatException var14) {
               }
            }
         }
      } catch (IOException var16) {
      }

      out.put("sampledTasks", sampledTasks);
      out.put("voluntary_ctxt_switches", voluntary);
      out.put("nonvoluntary_ctxt_switches", nonvoluntary);
      return out;
   }

   private static String statusField(Path status, String key) {
      try {
         for (String line : Files.readAllLines(status)) {
            int colon = line.indexOf(58);
            if (colon > 0 && line.substring(0, colon).equals(key)) {
               return line.substring(colon + 1).trim();
            }
         }
      } catch (IOException var5) {
      }

      return null;
   }

   private static String linuxTaskComm(String javaThreadName) {
      if (javaThreadName == null) {
         return "";
      } else {
         return javaThreadName.length() <= 15 ? javaThreadName : javaThreadName.substring(0, 15);
      }
   }

   private static Map<String, Object> pressure(String file) {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();

      try {
         for (String line : Files.readAllLines(Path.of(file))) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length != 0) {
               LinkedHashMap<String, Object> metrics = new LinkedHashMap<>();

               for (int i = 1; i < parts.length; i++) {
                  int equals = parts[i].indexOf(61);
                  if (equals > 0) {
                     metrics.put(parts[i].substring(0, equals), parseNumber(parts[i].substring(equals + 1)));
                  }
               }

               out.put(parts[0], metrics);
            }
         }
      } catch (IOException var8) {
      }

      return out;
   }

   private static Map<String, Object> keyValues(String file) {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();

      try {
         for (String line : Files.readAllLines(Path.of(file))) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
               String[] parts = trimmed.split("[:\\s]+", 2);
               if (parts.length == 2) {
                  out.put(parts[0], parseNumber(parts[1].trim()));
               }
            }
         }
      } catch (IOException var6) {
      }

      return out;
   }

   private static Map<String, Object> modState() {
      LinkedHashMap<String, Object> out = new LinkedHashMap<>();
      out.put("chunksmithTaskCount", reflectiveChunksmithTasks());
      out.put("idlePregenActive", reflectiveBoolean("maki.idlepregen.IdlePregenApi", "isActive"));
      return out;
   }

   private static Object reflectiveChunksmithTasks() {
      try {
         Class<?> provider = Class.forName("com.kishku7.chunksmith.ChunksmithProvider", false, LagObserver.class.getClassLoader());
         boolean loaded = (Boolean)provider.getMethod("isLoaded").invoke(null);
         if (!loaded) {
            return 0;
         }

         Object chunksmith = provider.getMethod("get").invoke(null);
         if (chunksmith.getClass().getMethod("getGenerationTasks").invoke(chunksmith) instanceof Map<?, ?> map) {
            return map.size();
         }
      } catch (Throwable var5) {
      }

      return null;
   }

   private static Object reflectiveBoolean(String type, String method) {
      try {
         Class<?> cls = Class.forName(type, false, LagObserver.class.getClassLoader());
         Object value = cls.getMethod(method).invoke(null);
         return value instanceof Boolean ? value : null;
      } catch (Throwable ignored) {
         return null;
      }
   }

   private static LagObserver.HotThreadSample hottestThreads() {
      if (THREADS.isThreadCpuTimeSupported() && THREADS.isThreadCpuTimeEnabled()) {
         long sampledAt = System.nanoTime();
         long[] ids = THREADS.getAllThreadIds();
         ThreadInfo[] infos = THREADS.getThreadInfo(ids, 1);
         ArrayList<LagObserver.HotThread> hot = new ArrayList<>();
         long windowNanos;
         synchronized (PREVIOUS_THREAD_CPU) {
            windowNanos = threadCpuBaselineNanos == 0L ? -1L : Math.max(0L, sampledAt - threadCpuBaselineNanos);

            for (int i = 0; i < ids.length; i++) {
               long cpu = THREADS.getThreadCpuTime(ids[i]);
               Long previous = PREVIOUS_THREAD_CPU.put(ids[i], cpu);
               ThreadInfo info = infos[i];
               if (cpu >= 0L && previous != null && previous >= 0L && info != null) {
                  long delta = Math.max(0L, cpu - previous);
                  if (delta != 0L) {
                     String top = info.getStackTrace().length == 0 ? null : info.getStackTrace()[0].toString();
                     hot.add(new LagObserver.HotThread(info.getThreadName(), info.getThreadState().name(), delta, top));
                  }
               }
            }

            PREVIOUS_THREAD_CPU.keySet().removeIf(id -> {
               for (long live : ids) {
                  if (live == id) {
                     return false;
                  }
               }

               return true;
            });
            threadCpuBaselineNanos = sampledAt;
         }

         hot.sort(Comparator.comparingLong(LagObserver.HotThread::cpuNanos).reversed());
         int limit = Math.min(settings.topThreads, hot.size());
         return new LagObserver.HotThreadSample(windowNanos, List.copyOf(hot.subList(0, limit)));
      } else {
         return LagObserver.HotThreadSample.empty();
      }
   }

   private static void refreshThreadCpuBaselineIfDue(long now) {
      long baseline = threadCpuBaselineNanos;
      if (baseline == 0L || now - baseline >= 1000000000L) {
         primeThreadCpuSnapshot();
      }
   }

   private static void primeThreadCpuSnapshot() {
      if (THREADS.isThreadCpuTimeSupported() && THREADS.isThreadCpuTimeEnabled()) {
         long sampledAt = System.nanoTime();
         synchronized (PREVIOUS_THREAD_CPU) {
            PREVIOUS_THREAD_CPU.clear();

            for (long id : THREADS.getAllThreadIds()) {
               long cpu = THREADS.getThreadCpuTime(id);
               if (cpu >= 0L) {
                  PREVIOUS_THREAD_CPU.put(id, cpu);
               }
            }

            threadCpuBaselineNanos = sampledAt;
         }
      }
   }

   private static void ensureCpuTimeEnabled() {
      try {
         if (THREADS.isThreadCpuTimeSupported() && !THREADS.isThreadCpuTimeEnabled()) {
            THREADS.setThreadCpuTimeEnabled(true);
         }
      } catch (SecurityException var1) {
      }
   }

   private static long cpuTime(Thread thread) {
      return thread != null && THREADS.isThreadCpuTimeSupported() && THREADS.isThreadCpuTimeEnabled() ? THREADS.getThreadCpuTime(thread.threadId()) : -1L;
   }

   private static long cpuDelta(long start, long end) {
      return start >= 0L && end >= start ? end - start : -1L;
   }

   private static void reloadSettings() {
      try {
         long stamp = Files.exists(CONFIG) ? Files.getLastModifiedTime(CONFIG).toMillis() : -1L;
         if (stamp == configStamp) {
            return;
         }

         Properties properties = new Properties();
         if (stamp >= 0L) {
            try (BufferedReader reader = Files.newBufferedReader(CONFIG)) {
               properties.load(reader);
            }
         }

         settings = LagObserver.Settings.parse(properties);
         configStamp = stamp;
      } catch (Exception error) {
         LOG.log(Level.WARNING, "Invalid lag observer config; retaining previous settings: " + error);
      }
   }

   private static String readTrimmed(String file) {
      try {
         return Files.readString(Path.of(file)).trim();
      } catch (IOException ignored) {
         return null;
      }
   }

   private static Object parseNumber(String value) {
      try {
         return value.indexOf(46) >= 0 ? Double.parseDouble(value) : Long.parseLong(value);
      } catch (NumberFormatException ignored) {
         return value;
      }
   }

   private static Object finite(double value) {
      return Double.isFinite(value) && value >= 0.0 ? round1(value) : null;
   }

   private static Object finitePercent(double fraction) {
      return Double.isFinite(fraction) && fraction >= 0.0 ? round1(fraction * 100.0) : null;
   }

   private static long millisToNanos(long ms) {
      return ms * 1000000L;
   }

   private static double nanosToMillis(long nanos) {
      return round1(nanos / 1000000.0);
   }

   private static double bytesToMb(long bytes) {
      return round1(bytes / 1048576.0);
   }

   private static double round1(double value) {
      return Math.rint(value * 10.0) / 10.0;
   }

   private record HotThread(String name, String state, long cpuNanos, String topFrame) {
      Map<String, Object> asMap() {
         LinkedHashMap<String, Object> out = new LinkedHashMap<>();
         out.put("name", this.name);
         out.put("state", this.state);
         out.put("cpuDeltaMs", LagObserver.nanosToMillis(this.cpuNanos));
         out.put("topFrame", this.topFrame);
         return out;
      }
   }

   private record HotThreadSample(long windowNanos, List<LagObserver.HotThread> threads) {
      static LagObserver.HotThreadSample empty() {
         return new LagObserver.HotThreadSample(-1L, List.of());
      }
   }

   private static final class Json {
      static String encode(Object value) {
         StringBuilder out = new StringBuilder(2048);
         append(out, value);
         return out.toString();
      }

      private static void append(StringBuilder out, Object value) {
         if (value == null) {
            out.append("null");
         } else if (value instanceof String text) {
            string(out, text);
         } else if (value instanceof Number || value instanceof Boolean) {
            out.append(value);
         } else if (value instanceof Map<?, ?> map) {
            out.append('{');
            boolean first = true;

            for (Entry<?, ?> entry : map.entrySet()) {
               if (!first) {
                  out.append(',');
               }

               first = false;
               string(out, String.valueOf(entry.getKey()));
               out.append(':');
               append(out, entry.getValue());
            }

            out.append('}');
         } else if (value instanceof Iterable<?> iterable) {
            out.append('[');
            boolean first = true;

            for (Object item : iterable) {
               if (!first) {
                  out.append(',');
               }

               first = false;
               append(out, item);
            }

            out.append(']');
         } else {
            string(out, String.valueOf(value));
         }
      }

      private static void string(StringBuilder out, String value) {
         out.append('"');

         for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
               case '\b':
                  out.append("\\b");
                  break;
               case '\t':
                  out.append("\\t");
                  break;
               case '\n':
                  out.append("\\n");
                  break;
               case '\f':
                  out.append("\\f");
                  break;
               case '\r':
                  out.append("\\r");
                  break;
               case '"':
                  out.append("\\\"");
                  break;
               case '\\':
                  out.append("\\\\");
                  break;
               default:
                  if (c < ' ') {
                     out.append(String.format(Locale.ROOT, "\\u%04x", Integer.valueOf(c)));
                  } else {
                     out.append(c);
                  }
            }
         }

         out.append('"');
      }
   }

   private record Settings(
      boolean enabled,
      long watchdogIntervalMs,
      long stallThresholdMs,
      long stallSampleIntervalMs,
      long longTickThresholdMs,
      long tickStartGapThresholdMs,
      int topThreads,
      int maxStackFrames
   ) {
      static LagObserver.Settings defaults() {
         return new LagObserver.Settings(true, 100L, 500L, 500L, 200L, 500L, 8, 24);
      }

      static LagObserver.Settings parse(Properties p) {
         LagObserver.Settings d = defaults();
         return new LagObserver.Settings(
            Boolean.parseBoolean(p.getProperty("enabled", Boolean.toString(d.enabled))),
            boundedLong(p, "watchdog-interval-ms", d.watchdogIntervalMs, 50L, 5000L),
            boundedLong(p, "stall-threshold-ms", d.stallThresholdMs, 100L, 60000L),
            boundedLong(p, "stall-sample-interval-ms", d.stallSampleIntervalMs, 100L, 60000L),
            boundedLong(p, "long-tick-threshold-ms", d.longTickThresholdMs, 50L, 60000L),
            boundedLong(p, "tick-start-gap-threshold-ms", d.tickStartGapThresholdMs, 100L, 60000L),
            (int)boundedLong(p, "top-threads", d.topThreads, 1L, 32L),
            (int)boundedLong(p, "max-stack-frames", d.maxStackFrames, 4L, 128L)
         );
      }

      private static long boundedLong(Properties p, String key, long fallback, long min, long max) {
         String raw = p.getProperty(key);
         if (raw != null && !raw.isBlank()) {
            long value = Long.parseLong(raw.trim());
            if (value >= min && value <= max) {
               return value;
            } else {
               throw new IllegalArgumentException(key + " outside " + min + ".." + max);
            }
         } else {
            return fallback;
         }
      }
   }

   private record TickSnapshot(long sequence, boolean inProgress, long tickStartNanos, long tickStartCpuNanos, long lastTickEndNanos, long lastTickEndCpuNanos) {
      static LagObserver.TickSnapshot initial() {
         return new LagObserver.TickSnapshot(0L, false, 0L, -1L, 0L, -1L);
      }

      static LagObserver.TickSnapshot stopped(long sequence) {
         return new LagObserver.TickSnapshot(sequence, false, 0L, -1L, 0L, -1L);
      }
   }
}
