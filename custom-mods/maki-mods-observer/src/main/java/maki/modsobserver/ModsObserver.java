package maki.modsobserver;

import com.sun.management.OperatingSystemMXBean;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModsObserver {
    private static final Logger LOG = LoggerFactory.getLogger("MakiModsObserver");
    private static final ThreadMXBean THREADS = ManagementFactory.getThreadMXBean();
    private static final Path CONFIG = Path.of("config/maki-mods-observer.properties");
    private static final Path LOG_FILE = Path.of("logs/maki-mods-observer.jsonl");
    private static final long THREAD_CPU_BASELINE_INTERVAL_NANOS = 1_000_000_000L;
    private static final long CONFIG_RELOAD_INTERVAL_NANOS = 5_000_000_000L;
    private static final BlockingQueue<String> WRITE_QUEUE = new ArrayBlockingQueue<>(64);
    private static final AtomicBoolean WRITER_STARTED = new AtomicBoolean();
    private static final AtomicBoolean WRITE_WARNING_LOGGED = new AtomicBoolean();
    private static final AtomicBoolean CAPTURE_WARNING_LOGGED = new AtomicBoolean();
    private static final AtomicBoolean QUEUE_WARNING_LOGGED = new AtomicBoolean();
    private static final AtomicBoolean SESSION_LIMIT_REACHED = new AtomicBoolean();
    private static final AtomicLong SESSION_STARTED_NANOS = new AtomicLong();
    private static final AtomicLong ACCEPTED_EVENTS = new AtomicLong();
    private static final AtomicLong DROPPED_EVENTS = new AtomicLong();
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
    private static volatile TickSnapshot tickState = TickSnapshot.initial();
    private static volatile long threadCpuBaselineNanos;
    private static volatile Settings settings = Settings.defaults();
    private static volatile long configStamp = Long.MIN_VALUE;
    private static volatile boolean active;
    private static volatile long nextConfigCheckNanos;

    private ModsObserver() {
    }

    public static void tickStart(MinecraftServer server) {
        if (owner != null && owner != server) {
            shutdown(owner);
        }
        owner = server;
        serverThread = Thread.currentThread();
        reloadSettingsIfDue();
        if (!settings.enabled || SESSION_LIMIT_REACHED.get()) {
            if (active) {
                active = false;
                stopWatchdog();
                resetTimingState();
            }
            return;
        }
        if (!active) {
            active = true;
            resetTimingState();
            ensureCpuTimeEnabled();
        }
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
        TickSnapshot snapshot = new TickSnapshot(
                sequence, true, now, cpuNow, lastTickEndNanos, lastTickEndCpuNanos);
        tickState = snapshot;

        Settings s = settings;
        if (s.enabled && previous != 0L) {
            long gap = now - previous;
            if (gap >= millisToNanos(s.tickStartGapThresholdMs)) {
                writeEvent(server, snapshot, "tick-start-gap", gap,
                        cpuDelta(previousCpu, cpuNow), null, hottestThreads());
            }
        }
    }

    public static void tickEnd(MinecraftServer server) {
        if (owner != server || !active) {
            return;
        }
        long now = System.nanoTime();
        long started = tickStartNanos;
        long cpuStarted = tickStartCpuNanos;
        long cpuNow = cpuTime(serverThread);
        lastTickEndNanos = now;
        lastTickEndCpuNanos = cpuNow;
        tickInProgress = false;
        TickSnapshot snapshot = new TickSnapshot(
                tickSequence, false, started, cpuStarted, now, cpuNow);
        tickState = snapshot;
        if (started == 0L || !settings.enabled) {
            return;
        }
        long elapsed = Math.max(0L, now - started);
        if (elapsed >= millisToNanos(settings.longTickThresholdMs)) {
            writeEvent(server, snapshot, "long-tick", elapsed,
                    cpuDelta(cpuStarted, cpuNow), null, HotThreadSample.empty());
        }
    }

    public static void shutdown(MinecraftServer server) {
        if (owner != server) {
            return;
        }
        active = false;
        stopWatchdog();
        tickInProgress = false;
        lastTickEndCpuNanos = -1L;
        tickState = TickSnapshot.stopped(tickSequence);
        owner = null;
        serverThread = null;
        synchronized (PREVIOUS_THREAD_CPU) {
            PREVIOUS_THREAD_CPU.clear();
            threadCpuBaselineNanos = 0L;
        }
    }

    private static void resetTimingState() {
        tickInProgress = false;
        tickStartNanos = 0L;
        tickStartCpuNanos = -1L;
        lastTickStartNanos = 0L;
        lastTickEndNanos = 0L;
        lastTickEndCpuNanos = -1L;
        lastStallSampleNanos = 0L;
        tickState = TickSnapshot.initial();
        synchronized (PREVIOUS_THREAD_CPU) {
            PREVIOUS_THREAD_CPU.clear();
            threadCpuBaselineNanos = 0L;
        }
    }

    private static void stopWatchdog() {
        running = false;
        Thread thread = watchdog;
        watchdog = null;
        if (thread != null) {
            thread.interrupt();
        }
    }

    private static void ensureWatchdog() {
        if (watchdog != null && watchdog.isAlive()) {
            return;
        }
        running = true;
        Thread thread = new Thread(ModsObserver::watchLoop, "maki-mods-observer-watchdog");
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        primeThreadCpuSnapshot();
        watchdog = thread;
        thread.start();
        LOG.info("Maki Mods Observer stall diagnostics enabled; thresholded samples use {}", LOG_FILE);
    }

    private static void watchLoop() {
        while (running) {
            Settings beforeSleep = settings;
            try {
                Thread.sleep(beforeSleep.watchdogIntervalMs);
            } catch (InterruptedException interrupted) {
                if (!running) {
                    return;
                }
            }

            reloadSettingsIfDue();
            Settings s = settings;
            MinecraftServer server = owner;
            Thread thread = serverThread;
            if (!s.enabled || server == null || thread == null) {
                continue;
            }

            long now = System.nanoTime();
            TickSnapshot snapshot = tickState;
            long age = snapshot.inProgress()
                    ? now - snapshot.tickStartNanos()
                    : (snapshot.lastTickEndNanos() == 0L ? 0L : now - snapshot.lastTickEndNanos());
            if (age < millisToNanos(s.stallThresholdMs)) {
                refreshThreadCpuBaselineIfDue(now);
                continue;
            }
            if (now - lastStallSampleNanos < millisToNanos(s.stallSampleIntervalMs)) {
                continue;
            }

            String event = snapshot.inProgress() ? "stall-sample" : "between-ticks-sample";
            if (capture(server, snapshot, event, age, true)) {
                lastStallSampleNanos = now;
            }
        }
    }

    private static boolean capture(
            MinecraftServer server,
            TickSnapshot snapshot,
            String event,
            long wallNanos,
            boolean includeHotThreads) {
        Thread thread = serverThread;
        if (thread == null || tickState != snapshot) {
            return false;
        }

        ThreadInfo info = THREADS.getThreadInfo(thread.threadId(), settings.maxStackFrames);
        long cpuNow = cpuTime(thread);
        long sampledAt = System.nanoTime();
        long cpu = snapshot.inProgress()
                ? cpuDelta(snapshot.tickStartCpuNanos(), cpuNow)
                : cpuDelta(snapshot.lastTickEndCpuNanos(), cpuNow);
        long effectiveWall = snapshot.inProgress()
                ? Math.max(wallNanos, sampledAt - snapshot.tickStartNanos())
                : Math.max(wallNanos, sampledAt - snapshot.lastTickEndNanos());
        HotThreadSample hot = includeHotThreads ? hottestThreads() : HotThreadSample.empty();

        if (tickState != snapshot || owner != server || serverThread != thread) {
            return false;
        }
        writeEvent(server, snapshot, event, effectiveWall, cpu, info, hot);
        return true;
    }

    private static void writeEvent(
            MinecraftServer server,
            TickSnapshot snapshot,
            String event,
            long wallNanos,
            long serverCpuNanos,
            ThreadInfo info,
            HotThreadSample hotThreadSample) {
        if (!reserveDiagnosticEvent()) return;
        try {
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("time", Instant.now().toString());
            root.put("event", event);
            root.put("tickSequence", snapshot.sequence());
            root.put("tickInProgress", snapshot.inProgress());
            root.put("wallMs", nanosToMillis(wallNanos));
            root.put("serverThreadCpuMs", serverCpuNanos < 0L ? null : nanosToMillis(serverCpuNanos));
            root.put("serverThreadCpuToWallPercent", serverCpuNanos < 0L || wallNanos <= 0L
                    ? null : round1(100.0 * serverCpuNanos / wallNanos));
            root.put("averageTickMs", round1(server.getAverageTickTimeNanos() / 1_000_000.0));
            root.put("players", server.getPlayerCount());
            root.put("frozen", server.tickRateManager().isFrozen());
            root.put("lastTickEndAgoMs", snapshot.lastTickEndNanos() == 0L
                    ? null : nanosToMillis(Math.max(0L, System.nanoTime() - snapshot.lastTickEndNanos())));

            if (info != null) {
                root.put("serverThreadState", info.getThreadState().name());
                root.put("serverThreadLock", info.getLockName());
                root.put("serverThreadLockOwner", info.getLockOwnerName());
                root.put("serverThreadBlockedCount", info.getBlockedCount());
                root.put("serverThreadWaitedCount", info.getWaitedCount());
                ArrayList<String> stack = new ArrayList<>();
                for (StackTraceElement frame : info.getStackTrace()) stack.add(frame.toString());
                root.put("serverThreadStack", stack);
            }

            root.put("hotThreadWindowMs", hotThreadSample.windowNanos() < 0L
                    ? null : nanosToMillis(hotThreadSample.windowNanos()));
            root.put("hotThreads", hotThreadSample.threads().stream().map(HotThread::asMap).toList());
            root.put("process", processMetrics());
            root.put("jvm", jvmMetrics());
            root.put("linux", linuxMetrics());
            root.put("modState", modState());
            root.put("droppedEventsTotal", DROPPED_EVENTS.get());

            enqueue(Json.encode(root) + System.lineSeparator());
            CAPTURE_WARNING_LOGGED.set(false);
        } catch (Throwable error) {
            if (CAPTURE_WARNING_LOGGED.compareAndSet(false, true)) {
                LOG.warn("Could not capture MOD diagnostic sample", error);
            }
        }
    }

    private static boolean reserveDiagnosticEvent() {
        Settings s = settings;
        if (!s.enabled || SESSION_LIMIT_REACHED.get()) return false;
        long now = System.nanoTime();
        long started = SESSION_STARTED_NANOS.get();
        if (started == 0L) {
            SESSION_STARTED_NANOS.compareAndSet(0L, now);
            started = SESSION_STARTED_NANOS.get();
        }
        if (now - started >= s.maxDurationNanos) {
            markSessionLimit("duration limit " + (s.maxDurationNanos / 60_000_000_000L) + " minute(s) reached");
            return false;
        }
        long accepted = ACCEPTED_EVENTS.incrementAndGet();
        if (accepted > s.maxEvents) {
            ACCEPTED_EVENTS.decrementAndGet();
            markSessionLimit("event limit " + s.maxEvents + " reached");
            return false;
        }
        if (accepted == s.maxEvents) markSessionLimit("event limit " + s.maxEvents + " reached");
        return true;
    }

    private static void markSessionLimit(String reason) {
        if (SESSION_LIMIT_REACHED.compareAndSet(false, true)) {
            LOG.warn("Maki Mods Observer stall diagnostics stopped for the current config: {}", reason);
        }
    }

    private static void resetDiagnosticSession() {
        SESSION_STARTED_NANOS.set(0L);
        ACCEPTED_EVENTS.set(0L);
        DROPPED_EVENTS.set(0L);
        SESSION_LIMIT_REACHED.set(false);
        QUEUE_WARNING_LOGGED.set(false);
    }

    private static void enqueue(String line) {
        ensureWriter();
        if (!WRITE_QUEUE.offer(line)) {
            long dropped = DROPPED_EVENTS.incrementAndGet();
            if (QUEUE_WARNING_LOGGED.compareAndSet(false, true)) {
                LOG.warn("Maki Mods Observer diagnostic writer queue is full; dropping samples (dropped={})", dropped);
            }
        }
    }

    private static void ensureWriter() {
        if (!WRITER_STARTED.compareAndSet(false, true)) return;
        Thread writer = new Thread(ModsObserver::writerLoop, "maki-mods-observer-writer");
        writer.setDaemon(true);
        writer.setPriority(Thread.MIN_PRIORITY);
        writer.start();
    }

    private static void writerLoop() {
        while (true) {
            try {
                String line = WRITE_QUEUE.take();
                Files.createDirectories(LOG_FILE.getParent());
                if (Files.isRegularFile(LOG_FILE) && Files.size(LOG_FILE) >= settings.maxFileBytes) {
                    DROPPED_EVENTS.incrementAndGet();
                    markSessionLimit("file size cap " + (settings.maxFileBytes / 1048576L) + " MiB reached");
                    continue;
                }
                Files.writeString(LOG_FILE, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                WRITE_WARNING_LOGGED.set(false);
                if (WRITE_QUEUE.remainingCapacity() > 0) QUEUE_WARNING_LOGGED.set(false);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                return;
            } catch (Throwable error) {
                if (WRITE_WARNING_LOGGED.compareAndSet(false, true)) {
                    LOG.warn("Could not write MOD diagnostic sample", error);
                }
            }
        }
    }

    private static Map<String, Object> processMetrics() {
        LinkedHashMap<String, Object> out = new LinkedHashMap<>();
        var bean = ManagementFactory.getOperatingSystemMXBean();
        out.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        out.put("systemLoadAverage", finite(bean.getSystemLoadAverage()));
        if (bean instanceof OperatingSystemMXBean os) {
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
            if (gc.getCollectionCount() >= 0L) gcCount += gc.getCollectionCount();
            if (gc.getCollectionTime() >= 0L) gcMillis += gc.getCollectionTime();
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
        Path node = current.getParent();
        while (node != null && node.startsWith(root)) {
            String cpuMax = readTrimmed(node.resolve("cpu.max").toString());
            Map<String, Object> stat = keyValues(node.resolve("cpu.stat").toString());
            if (cpuMax != null || !stat.isEmpty()) {
                LinkedHashMap<String, Object> entry = new LinkedHashMap<>();
                entry.put("path", cgroupDisplayPath(root, node));
                entry.put("cpuMax", cpuMax);
                entry.put("stat", stat);
                ancestors.add(entry);
            }
            if (node.equals(root)) break;
            node = node.getParent();
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
        } catch (IOException ignored) {
        }
        return root;
    }

    private static Map<String, Object> legacyCgroupCpu() {
        LinkedHashMap<String, Object> out = new LinkedHashMap<>();
        Path root = legacyCpuRoot();
        if (root == null) return out;
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
            if (Files.isDirectory(candidate)) return candidate;
        }
        return null;
    }

    private static Path resolveLegacyCpuCgroup(Path root) {
        try {
            for (String line : Files.readAllLines(Path.of("/proc/self/cgroup"))) {
                String[] parts = line.split(":", 3);
                if (parts.length != 3) continue;
                for (String controller : parts[1].split(",")) {
                    if (controller.equals("cpu") || controller.equals("cpuacct")) {
                        return safeCgroupPath(root, parts[2]);
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return root;
    }

    private static List<Map<String, Object>> legacyCpuAncestors(Path root, Path current) {
        ArrayList<Map<String, Object>> ancestors = new ArrayList<>();
        Path node = current.getParent();
        while (node != null && node.startsWith(root)) {
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
            if (node.equals(root)) break;
            node = node.getParent();
        }
        return ancestors;
    }

    private static Path safeCgroupPath(Path root, String membership) {
        Path candidate = root.resolve(membership.replaceFirst("^/+", "")).normalize();
        return candidate.startsWith(root) && Files.isDirectory(candidate) ? candidate : root;
    }

    private static String cgroupDisplayPath(Path root, Path node) {
        if (node.equals(root)) return "/";
        return "/" + root.relativize(node).toString().replace('\\', '/');
    }

    private static Map<String, Object> procCpu() {
        LinkedHashMap<String, Object> out = new LinkedHashMap<>();
        try {
            String line = Files.readAllLines(Path.of("/proc/stat")).stream()
                    .filter(v -> v.startsWith("cpu ")).findFirst().orElse(null);
            if (line == null) return out;
            String[] parts = line.trim().split("\\s+");
            String[] names = {"user", "nice", "system", "idle", "iowait", "irq", "softirq", "steal", "guest", "guestNice"};
            for (int i = 1; i < parts.length && i <= names.length; i++) {
                out.put(names[i - 1] + "Jiffies", parseNumber(parts[i]));
            }
        } catch (IOException ignored) {
        }
        return out;
    }

    private static Object parseNumberOrNull(String value) {
        return value == null ? null : parseNumber(value);
    }

    private static Map<String, Object> selectedProcessStatus() {
        LinkedHashMap<String, Object> out = new LinkedHashMap<>();
        String allowed = statusField(Path.of("/proc/self/status"), "Cpus_allowed_list");
        if (allowed != null) out.put("Cpus_allowed_list", allowed);
        return out;
    }

    private static Map<String, Object> serverTaskStatus() {
        LinkedHashMap<String, Object> out = new LinkedHashMap<>();
        Thread thread = serverThread;
        if (thread == null) return out;

        String expectedComm = linuxTaskComm(thread.getName());
        out.put("javaThreadName", thread.getName());
        out.put("expectedComm", expectedComm);

        ArrayList<Path> matches = new ArrayList<>();
        Path taskRoot = Path.of("/proc/self/task");
        try (var tasks = Files.list(taskRoot)) {
            for (Path task : tasks.filter(Files::isDirectory).toList()) {
                String comm = readTrimmed(task.resolve("comm").toString());
                if (expectedComm.equals(comm)) matches.add(task);
            }
        } catch (IOException ignored) {
        }

        out.put("matchCount", matches.size());
        if (matches.size() != 1) return out;

        Path task = matches.get(0);
        out.put("nativeTid", parseNumber(task.getFileName().toString()));
        String allowed = statusField(task.resolve("status"), "Cpus_allowed_list");
        String voluntary = statusField(task.resolve("status"), "voluntary_ctxt_switches");
        String nonvoluntary = statusField(task.resolve("status"), "nonvoluntary_ctxt_switches");
        if (allowed != null) out.put("Cpus_allowed_list", allowed);
        if (voluntary != null) out.put("voluntary_ctxt_switches", parseNumber(voluntary));
        if (nonvoluntary != null) out.put("nonvoluntary_ctxt_switches", parseNumber(nonvoluntary));
        return out;
    }

    private static Map<String, Object> taskContextSwitchTotals() {
        LinkedHashMap<String, Object> out = new LinkedHashMap<>();
        long voluntary = 0L;
        long nonvoluntary = 0L;
        int sampledTasks = 0;
        Path taskRoot = Path.of("/proc/self/task");
        try (var tasks = Files.list(taskRoot)) {
            for (Path task : tasks.filter(Files::isDirectory).toList()) {
                String voluntaryRaw = statusField(task.resolve("status"), "voluntary_ctxt_switches");
                String nonvoluntaryRaw = statusField(task.resolve("status"), "nonvoluntary_ctxt_switches");
                if (voluntaryRaw == null || nonvoluntaryRaw == null) continue;
                try {
                    voluntary += Long.parseLong(voluntaryRaw);
                    nonvoluntary += Long.parseLong(nonvoluntaryRaw);
                    sampledTasks++;
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (IOException ignored) {
        }
        out.put("sampledTasks", sampledTasks);
        out.put("voluntary_ctxt_switches", voluntary);
        out.put("nonvoluntary_ctxt_switches", nonvoluntary);
        return out;
    }

    private static String statusField(Path status, String key) {
        try {
            for (String line : Files.readAllLines(status)) {
                int colon = line.indexOf(':');
                if (colon <= 0 || !line.substring(0, colon).equals(key)) continue;
                return line.substring(colon + 1).trim();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    private static String linuxTaskComm(String javaThreadName) {
        if (javaThreadName == null) return "";
        return javaThreadName.length() <= 15 ? javaThreadName : javaThreadName.substring(0, 15);
    }

    private static Map<String, Object> pressure(String file) {
        LinkedHashMap<String, Object> out = new LinkedHashMap<>();
        try {
            for (String line : Files.readAllLines(Path.of(file))) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 0) continue;
                LinkedHashMap<String, Object> metrics = new LinkedHashMap<>();
                for (int i = 1; i < parts.length; i++) {
                    int equals = parts[i].indexOf('=');
                    if (equals > 0) metrics.put(parts[i].substring(0, equals), parseNumber(parts[i].substring(equals + 1)));
                }
                out.put(parts[0], metrics);
            }
        } catch (IOException ignored) {
        }
        return out;
    }

    private static Map<String, Object> keyValues(String file) {
        LinkedHashMap<String, Object> out = new LinkedHashMap<>();
        try {
            for (String line : Files.readAllLines(Path.of(file))) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;
                String[] parts = trimmed.split("[:\\s]+", 2);
                if (parts.length == 2) out.put(parts[0], parseNumber(parts[1].trim()));
            }
        } catch (IOException ignored) {
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
            Class<?> provider = Class.forName("com.kishku7.chunksmith.ChunksmithProvider", false, ModsObserver.class.getClassLoader());
            boolean loaded = (boolean)provider.getMethod("isLoaded").invoke(null);
            if (!loaded) return 0;
            Object chunksmith = provider.getMethod("get").invoke(null);
            Object tasks = chunksmith.getClass().getMethod("getGenerationTasks").invoke(chunksmith);
            if (tasks instanceof Map<?, ?> map) return map.size();
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static Object reflectiveBoolean(String type, String method) {
        try {
            Class<?> cls = Class.forName(type, false, ModsObserver.class.getClassLoader());
            Object value = cls.getMethod(method).invoke(null);
            return value instanceof Boolean ? value : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static HotThreadSample hottestThreads() {
        if (!THREADS.isThreadCpuTimeSupported() || !THREADS.isThreadCpuTimeEnabled()) {
            return HotThreadSample.empty();
        }
        long sampledAt = System.nanoTime();
        long[] ids = THREADS.getAllThreadIds();
        ThreadInfo[] infos = THREADS.getThreadInfo(ids, 1);
        ArrayList<HotThread> hot = new ArrayList<>();
        long windowNanos;
        synchronized (PREVIOUS_THREAD_CPU) {
            windowNanos = threadCpuBaselineNanos == 0L
                    ? -1L : Math.max(0L, sampledAt - threadCpuBaselineNanos);
            for (int i = 0; i < ids.length; i++) {
                long cpu = THREADS.getThreadCpuTime(ids[i]);
                Long previous = PREVIOUS_THREAD_CPU.put(ids[i], cpu);
                ThreadInfo info = infos[i];
                if (cpu < 0L || previous == null || previous < 0L || info == null) continue;
                long delta = Math.max(0L, cpu - previous);
                if (delta == 0L) continue;
                String top = info.getStackTrace().length == 0 ? null : info.getStackTrace()[0].toString();
                hot.add(new HotThread(info.getThreadName(), info.getThreadState().name(), delta, top));
            }
            PREVIOUS_THREAD_CPU.keySet().removeIf(id -> {
                for (long live : ids) if (live == id) return false;
                return true;
            });
            threadCpuBaselineNanos = sampledAt;
        }
        hot.sort(Comparator.comparingLong(HotThread::cpuNanos).reversed());
        int limit = Math.min(settings.topThreads, hot.size());
        return new HotThreadSample(windowNanos, List.copyOf(hot.subList(0, limit)));
    }

    private static void refreshThreadCpuBaselineIfDue(long now) {
        long baseline = threadCpuBaselineNanos;
        if (baseline == 0L || now - baseline >= THREAD_CPU_BASELINE_INTERVAL_NANOS) {
            primeThreadCpuSnapshot();
        }
    }

    private static void primeThreadCpuSnapshot() {
        if (!THREADS.isThreadCpuTimeSupported() || !THREADS.isThreadCpuTimeEnabled()) {
            return;
        }
        long sampledAt = System.nanoTime();
        synchronized (PREVIOUS_THREAD_CPU) {
            PREVIOUS_THREAD_CPU.clear();
            for (long id : THREADS.getAllThreadIds()) {
                long cpu = THREADS.getThreadCpuTime(id);
                if (cpu >= 0L) PREVIOUS_THREAD_CPU.put(id, cpu);
            }
            threadCpuBaselineNanos = sampledAt;
        }
    }

    private static void ensureCpuTimeEnabled() {
        try {
            if (THREADS.isThreadCpuTimeSupported() && !THREADS.isThreadCpuTimeEnabled()) {
                THREADS.setThreadCpuTimeEnabled(true);
            }
        } catch (SecurityException ignored) {
        }
    }

    private static long cpuTime(Thread thread) {
        if (thread == null || !THREADS.isThreadCpuTimeSupported() || !THREADS.isThreadCpuTimeEnabled()) return -1L;
        return THREADS.getThreadCpuTime(thread.threadId());
    }

    private static long cpuDelta(long start, long end) {
        return start >= 0L && end >= start ? end - start : -1L;
    }

    private static void reloadSettingsIfDue() {
        long now = System.nanoTime();
        if (now < nextConfigCheckNanos) return;
        nextConfigCheckNanos = now + CONFIG_RELOAD_INTERVAL_NANOS;
        try {
            long stamp = Files.exists(CONFIG) ? Files.getLastModifiedTime(CONFIG).toMillis() : -1L;
            if (stamp == configStamp) return;
            Properties properties = new Properties();
            if (stamp >= 0L) {
                try (var reader = Files.newBufferedReader(CONFIG)) {
                    properties.load(reader);
                }
            }
            settings = Settings.parse(properties);
            configStamp = stamp;
            resetDiagnosticSession();
            if (!settings.enabled) WRITE_QUEUE.clear();
            LOG.info("Maki Mods Observer stall diagnostics enabled={}; maxDurationMinutes={}; maxEvents={}; maxFileMiB={}; config={}",
                settings.enabled,
                settings.maxDurationNanos / 60_000_000_000L,
                settings.maxEvents,
                settings.maxFileBytes / 1048576L,
                CONFIG);
        } catch (Exception error) {
            LOG.warn("Invalid Maki Mods Observer config; retaining previous settings", error);
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
            if (value.indexOf('.') >= 0) return Double.parseDouble(value);
            return Long.parseLong(value);
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
        return ms * 1_000_000L;
    }

    private static double nanosToMillis(long nanos) {
        return round1(nanos / 1_000_000.0);
    }

    private static double bytesToMb(long bytes) {
        return round1(bytes / 1048576.0);
    }

    private static double round1(double value) {
        return Math.rint(value * 10.0) / 10.0;
    }

    private record TickSnapshot(
            long sequence,
            boolean inProgress,
            long tickStartNanos,
            long tickStartCpuNanos,
            long lastTickEndNanos,
            long lastTickEndCpuNanos) {
        static TickSnapshot initial() {
            return new TickSnapshot(0L, false, 0L, -1L, 0L, -1L);
        }

        static TickSnapshot stopped(long sequence) {
            return new TickSnapshot(sequence, false, 0L, -1L, 0L, -1L);
        }
    }

    private record HotThreadSample(long windowNanos, List<HotThread> threads) {
        static HotThreadSample empty() {
            return new HotThreadSample(-1L, List.of());
        }
    }

    private record HotThread(String name, String state, long cpuNanos, String topFrame) {
        Map<String, Object> asMap() {
            LinkedHashMap<String, Object> out = new LinkedHashMap<>();
            out.put("name", name);
            out.put("state", state);
            out.put("cpuDeltaMs", nanosToMillis(cpuNanos));
            out.put("topFrame", topFrame);
            return out;
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
            int maxStackFrames,
            long maxFileBytes,
            long maxDurationNanos,
            long maxEvents) {
        static Settings defaults() {
            return new Settings(false, 100L, 500L, 500L, 200L, 500L, 8, 128,
                    16L * 1024L * 1024L, 30L * 60_000_000_000L, 5000L);
        }

        static Settings parse(Properties p) {
            Settings d = defaults();
            return new Settings(
                    Boolean.parseBoolean(p.getProperty("enabled", Boolean.toString(d.enabled))),
                    boundedLong(p, "watchdog-interval-ms", d.watchdogIntervalMs, 50L, 5000L),
                    boundedLong(p, "stall-threshold-ms", d.stallThresholdMs, 100L, 60000L),
                    boundedLong(p, "stall-sample-interval-ms", d.stallSampleIntervalMs, 100L, 60000L),
                    boundedLong(p, "long-tick-threshold-ms", d.longTickThresholdMs, 50L, 60000L),
                    boundedLong(p, "tick-start-gap-threshold-ms", d.tickStartGapThresholdMs, 100L, 60000L),
                    (int)boundedLong(p, "top-threads", d.topThreads, 1L, 32L),
                    (int)boundedLong(p, "max-stack-frames", d.maxStackFrames, 4L, 128L),
                    boundedLong(p, "diagnostic-max-file-mb", d.maxFileBytes / 1048576L, 1L, 1024L) * 1048576L,
                    boundedLong(p, "diagnostic-max-duration-minutes", d.maxDurationNanos / 60_000_000_000L, 1L, 1440L)
                            * 60_000_000_000L,
                    boundedLong(p, "diagnostic-max-events", d.maxEvents, 1L, 1_000_000L));
        }

        private static long boundedLong(Properties p, String key, long fallback, long min, long max) {
            String raw = p.getProperty(key);
            if (raw == null || raw.isBlank()) return fallback;
            long value = Long.parseLong(raw.trim());
            if (value < min || value > max) throw new IllegalArgumentException(key + " outside " + min + ".." + max);
            return value;
        }
    }

    private static final class Json {
        private Json() {
        }

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
                for (var entry : map.entrySet()) {
                    if (!first) out.append(',');
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
                    if (!first) out.append(',');
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
                    case '"' -> out.append("\\\"");
                    case '\\' -> out.append("\\\\");
                    case '\b' -> out.append("\\b");
                    case '\f' -> out.append("\\f");
                    case '\n' -> out.append("\\n");
                    case '\r' -> out.append("\\r");
                    case '\t' -> out.append("\\t");
                    default -> {
                        if (c < 0x20) out.append(String.format(Locale.ROOT, "\\u%04x", (int)c));
                        else out.append(c);
                    }
                }
            }
            out.append('"');
        }
    }
}
