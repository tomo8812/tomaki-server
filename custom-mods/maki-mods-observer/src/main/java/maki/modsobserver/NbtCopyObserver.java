package maki.modsobserver;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Targeted, low-overhead tracing for expensive deep NBT copies.
 *
 * <p>The observer only runs on the Minecraft server thread. Container-entry hooks
 * count the structure already traversed by the copy itself. A full Java stack is
 * allocated only after a top-level copy crosses the configured slow threshold,
 * and disk I/O is delegated to a bounded daemon writer queue.</p>
 */
public final class NbtCopyObserver {
    private static final Logger LOG = LoggerFactory.getLogger("MakiModsObserverNbt");
    private static final ThreadMXBean THREADS = ManagementFactory.getThreadMXBean();
    private static final Path CONFIG = Path.of("config/maki-mods-observer.properties");
    private static final Path LOG_FILE = Path.of("logs/maki-mods-observer-nbt.jsonl");
    private static final long CONFIG_RELOAD_INTERVAL_NANOS = 5_000_000_000L;
    private static final BlockingQueue<String> WRITE_QUEUE = new ArrayBlockingQueue<>(64);
    private static final AtomicBoolean WRITER_STARTED = new AtomicBoolean();
    private static final AtomicBoolean WRITE_WARNING_LOGGED = new AtomicBoolean();
    private static final AtomicBoolean CAPACITY_WARNING_LOGGED = new AtomicBoolean();
    private static final AtomicBoolean QUEUE_WARNING_LOGGED = new AtomicBoolean();
    private static final AtomicBoolean SESSION_LIMIT_REACHED = new AtomicBoolean();
    private static final AtomicLong SESSION_STARTED_NANOS = new AtomicLong();
    private static final AtomicLong ACCEPTED_EVENTS = new AtomicLong();
    private static final AtomicLong DROPPED_EVENTS = new AtomicLong();
    private static final ThreadLocal<Trace> ACTIVE = new ThreadLocal<>();
    private static final Map<Integer, Integer> SLOW_ROOTS_THIS_TICK = new HashMap<>();

    private static volatile MinecraftServer owner;
    private static volatile Thread serverThread;
    private static volatile boolean tickInProgress;
    private static volatile long tickSequence;
    private static volatile Settings settings = Settings.defaults();
    private static volatile long configStamp = Long.MIN_VALUE;
    private static volatile long nextConfigCheckNanos;

    private NbtCopyObserver() {
    }

    public static void tickStart(MinecraftServer server) {
        if (owner != null && owner != server) {
            shutdown(owner);
        }
        owner = server;
        serverThread = Thread.currentThread();
        reloadSettingsIfDue();
        if (!diagnosticSessionActive()) {
            tickInProgress = false;
            return;
        }
        tickInProgress = true;
        tickSequence++;
        ACTIVE.remove();
        SLOW_ROOTS_THIS_TICK.clear();
        ensureCpuTimeEnabled();
    }

    public static void tickEnd(MinecraftServer server) {
        if (owner != server || !settings.enabled || SESSION_LIMIT_REACHED.get()) {
            return;
        }
        tickInProgress = false;
        ACTIVE.remove();
    }

    public static void shutdown(MinecraftServer server) {
        if (owner != server) {
            return;
        }
        if (Thread.currentThread() == serverThread) {
            ACTIVE.remove();
            SLOW_ROOTS_THIS_TICK.clear();
        }
        tickInProgress = false;
        owner = null;
        serverThread = null;
    }

    public static void enterCompound(CompoundTag tag) {
        if (!settings.enabled || SESSION_LIMIT_REACHED.get() || tag == null) return;
        enter("compound", tag.size(), System.identityHashCode(tag));
    }

    public static void exitCompound() {
        exit();
    }

    public static void enterList(ListTag tag) {
        if (!settings.enabled || SESSION_LIMIT_REACHED.get() || tag == null) return;
        enter("list", tag.size(), System.identityHashCode(tag));
    }

    public static void exitList() {
        exit();
    }

    private static void enter(String kind, int entries, int identityHash) {
        Thread current = Thread.currentThread();
        Settings currentSettings = settings;
        if (!currentSettings.enabled || SESSION_LIMIT_REACHED.get() || current != serverThread || owner == null) {
            return;
        }

        Trace trace = ACTIVE.get();
        if (trace == null) {
            MinecraftServer server = owner;
            trace = new Trace(
                    kind,
                    identityHash,
                    Math.max(0, entries),
                    tickSequence,
                    tickInProgress,
                    server == null ? -1 : server.getPlayerCount(),
                    server != null && server.tickRateManager().isFrozen(),
                    System.nanoTime(),
                    cpuTime(current));
            ACTIVE.set(trace);
        }

        trace.depth++;
        trace.containerCount++;
        trace.containerEntries += Math.max(0, entries);
        if ("compound".equals(kind)) {
            trace.compoundCount++;
        } else {
            trace.listCount++;
        }
        if (trace.depth > trace.maxContainerDepth) {
            trace.maxContainerDepth = trace.depth;
        }
    }

    private static void exit() {
        Thread current = Thread.currentThread();
        if (current != serverThread) {
            return;
        }

        Trace trace = ACTIVE.get();
        if (trace == null) {
            return;
        }

        trace.depth--;
        if (trace.depth > 0) {
            return;
        }
        ACTIVE.remove();
        if (trace.depth < 0) {
            return;
        }

        long finishedAt = System.nanoTime();
        long wallNanos = Math.max(0L, finishedAt - trace.startedAtNanos);
        Settings currentSettings = settings;
        if (!currentSettings.enabled || SESSION_LIMIT_REACHED.get()
                || wallNanos < currentSettings.slowThresholdNanos) {
            return;
        }
        if (!reserveDiagnosticEvent()) return;

        long cpuNanos = cpuDelta(trace.startedCpuNanos, cpuTime(current));
        int sameRootSlowCopies = SLOW_ROOTS_THIS_TICK.merge(trace.rootIdentityHash, 1, Integer::sum);
        StackCapture capture = captureStack(currentSettings.maxStackFrames);
        enqueue(formatEvent(trace, wallNanos, cpuNanos, sameRootSlowCopies, capture));
    }

    private static StackCapture captureStack(int maxFrames) {
        StackTraceElement[] raw = Thread.currentThread().getStackTrace();
        int firstNbt = -1;
        for (int i = 0; i < raw.length; i++) {
            if (raw[i].getClassName().startsWith("net.minecraft.nbt.")) {
                firstNbt = i;
                break;
            }
        }

        int start = firstNbt >= 0 ? firstNbt : Math.min(3, raw.length);
        ArrayList<String> frames = new ArrayList<>(Math.min(maxFrames, raw.length - start));
        String firstCaller = null;
        boolean sawNbt = false;
        for (int i = start; i < raw.length && frames.size() < maxFrames; i++) {
            StackTraceElement frame = raw[i];
            String className = frame.getClassName();
            frames.add(frame.toString());
            if (className.startsWith("net.minecraft.nbt.")) {
                sawNbt = true;
                continue;
            }
            if (sawNbt && firstCaller == null && !isObserverFrame(className)) {
                firstCaller = frame.toString();
            }
        }
        return new StackCapture(firstCaller, List.copyOf(frames));
    }

    private static boolean isObserverFrame(String className) {
        return className.startsWith("maki.modsobserver.")
                || className.startsWith("org.spongepowered.asm.mixin.")
                || className.equals(Thread.class.getName());
    }

    private static String formatEvent(
            Trace trace,
            long wallNanos,
            long cpuNanos,
            int sameRootSlowCopies,
            StackCapture capture) {
        StringBuilder out = new StringBuilder(4096);
        out.append('{');
        field(out, "time", Instant.now().toString());
        comma(out);
        field(out, "event", "slow-nbt-copy");
        comma(out);
        number(out, "tickSequence", trace.tickSequence);
        comma(out);
        bool(out, "tickInProgress", trace.tickInProgress);
        comma(out);
        number(out, "players", trace.players);
        comma(out);
        bool(out, "frozen", trace.frozen);
        comma(out);
        decimal(out, "wallMs", nanosToMillis(wallNanos));
        comma(out);
        if (cpuNanos >= 0L) {
            decimal(out, "serverThreadCpuMs", nanosToMillis(cpuNanos));
        } else {
            nullField(out, "serverThreadCpuMs");
        }
        comma(out);
        if (cpuNanos >= 0L && wallNanos > 0L) {
            decimal(out, "serverThreadCpuToWallPercent", round1(100.0 * cpuNanos / wallNanos));
        } else {
            nullField(out, "serverThreadCpuToWallPercent");
        }
        comma(out);
        field(out, "rootType", trace.rootType);
        comma(out);
        number(out, "rootIdentityHash", Integer.toUnsignedLong(trace.rootIdentityHash));
        comma(out);
        number(out, "rootEntries", trace.rootEntries);
        comma(out);
        number(out, "containerCount", trace.containerCount);
        comma(out);
        number(out, "compoundCount", trace.compoundCount);
        comma(out);
        number(out, "listCount", trace.listCount);
        comma(out);
        number(out, "containerEntries", trace.containerEntries);
        comma(out);
        number(out, "tagOccurrences", trace.containerEntries + 1L);
        comma(out);
        number(out, "maxContainerDepth", trace.maxContainerDepth);
        comma(out);
        number(out, "sameRootSlowCopiesThisTick", sameRootSlowCopies);
        comma(out);
        if (capture.firstCaller == null) {
            nullField(out, "firstNonNbtCaller");
        } else {
            field(out, "firstNonNbtCaller", capture.firstCaller);
        }
        comma(out);
        stringArray(out, "copyCallerStack", capture.frames);
        comma(out);
        number(out, "droppedEventsTotal", DROPPED_EVENTS.get());
        out.append('}');
        return out.append(System.lineSeparator()).toString();
    }

    private static boolean diagnosticSessionActive() {
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
        return true;
    }

    private static boolean reserveDiagnosticEvent() {
        Settings s = settings;
        if (!diagnosticSessionActive()) return false;
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
            LOG.warn("Maki Mods Observer slow-NBT diagnostics stopped for the current config: {}", reason);
        }
    }

    private static void resetDiagnosticSession() {
        SESSION_STARTED_NANOS.set(0L);
        ACCEPTED_EVENTS.set(0L);
        DROPPED_EVENTS.set(0L);
        SESSION_LIMIT_REACHED.set(false);
        QUEUE_WARNING_LOGGED.set(false);
        CAPACITY_WARNING_LOGGED.set(false);
    }

    private static void enqueue(String line) {
        ensureWriter();
        if (!WRITE_QUEUE.offer(line)) {
            long dropped = DROPPED_EVENTS.incrementAndGet();
            if (QUEUE_WARNING_LOGGED.compareAndSet(false, true)) {
                LOG.warn("Maki Mods Observer NBT writer queue is full; dropping events (dropped={})", dropped);
            }
        }
    }

    private static void ensureWriter() {
        if (!WRITER_STARTED.compareAndSet(false, true)) {
            return;
        }
        Thread writer = new Thread(NbtCopyObserver::writerLoop, "maki-mods-observer-nbt-writer");
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
                    if (CAPACITY_WARNING_LOGGED.compareAndSet(false, true)) {
                        LOG.warn("Maki Mods Observer NBT diagnostic file reached its {} MiB cap; further events are dropped until the file is rotated or removed",
                            settings.maxFileBytes / 1048576L);
                    }
                    markSessionLimit("file size cap " + (settings.maxFileBytes / 1048576L) + " MiB reached");
                    continue;
                }
                Files.writeString(LOG_FILE, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                CAPACITY_WARNING_LOGGED.set(false);
                WRITE_WARNING_LOGGED.set(false);
                if (WRITE_QUEUE.remainingCapacity() > 0) QUEUE_WARNING_LOGGED.set(false);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                return;
            } catch (IOException error) {
                if (WRITE_WARNING_LOGGED.compareAndSet(false, true)) {
                    LOG.warn("Could not write NBT copy diagnostic sample", error);
                }
            } catch (Throwable error) {
                if (WRITE_WARNING_LOGGED.compareAndSet(false, true)) {
                    LOG.warn("NBT copy diagnostic writer failed", error);
                }
            }
        }
    }

    private static void reloadSettingsIfDue() {
        long now = System.nanoTime();
        if (now < nextConfigCheckNanos) {
            return;
        }
        nextConfigCheckNanos = now + CONFIG_RELOAD_INTERVAL_NANOS;
        try {
            long stamp = Files.exists(CONFIG) ? Files.getLastModifiedTime(CONFIG).toMillis() : -1L;
            if (stamp == configStamp) {
                return;
            }
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
            LOG.info("Maki Mods Observer slow-NBT diagnostics enabled={}; thresholdMs={}; maxDurationMinutes={}; maxEvents={}; maxFileMiB={}",
                settings.enabled,
                settings.slowThresholdNanos / 1_000_000L,
                settings.maxDurationNanos / 60_000_000_000L,
                settings.maxEvents,
                settings.maxFileBytes / 1048576L);
        } catch (Exception error) {
            LOG.warn("Invalid NBT copy observer config; retaining previous settings", error);
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
        if (thread == null || !THREADS.isThreadCpuTimeSupported() || !THREADS.isThreadCpuTimeEnabled()) {
            return -1L;
        }
        return THREADS.getThreadCpuTime(thread.threadId());
    }

    private static long cpuDelta(long start, long end) {
        return start >= 0L && end >= start ? end - start : -1L;
    }

    private static double nanosToMillis(long nanos) {
        return round1(nanos / 1_000_000.0);
    }

    private static double round1(double value) {
        return Math.rint(value * 10.0) / 10.0;
    }

    private static void comma(StringBuilder out) {
        out.append(',');
    }

    private static void field(StringBuilder out, String name, String value) {
        quote(out, name);
        out.append(':');
        quote(out, value);
    }

    private static void number(StringBuilder out, String name, long value) {
        quote(out, name);
        out.append(':').append(value);
    }

    private static void decimal(StringBuilder out, String name, double value) {
        quote(out, name);
        out.append(':').append(String.format(Locale.ROOT, "%.1f", value));
    }

    private static void bool(StringBuilder out, String name, boolean value) {
        quote(out, name);
        out.append(':').append(value);
    }

    private static void nullField(StringBuilder out, String name) {
        quote(out, name);
        out.append(":null");
    }

    private static void stringArray(StringBuilder out, String name, List<String> values) {
        quote(out, name);
        out.append(':').append('[');
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                out.append(',');
            }
            quote(out, values.get(i));
        }
        out.append(']');
    }

    private static void quote(StringBuilder out, String value) {
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
                    if (c < 0x20) {
                        out.append(String.format(Locale.ROOT, "\\u%04x", (int)c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        out.append('"');
    }

    private static final class Trace {
        final String rootType;
        final int rootIdentityHash;
        final int rootEntries;
        final long tickSequence;
        final boolean tickInProgress;
        final int players;
        final boolean frozen;
        final long startedAtNanos;
        final long startedCpuNanos;
        int depth;
        long containerCount;
        long compoundCount;
        long listCount;
        long containerEntries;
        int maxContainerDepth;

        Trace(
                String rootType,
                int rootIdentityHash,
                int rootEntries,
                long tickSequence,
                boolean tickInProgress,
                int players,
                boolean frozen,
                long startedAtNanos,
                long startedCpuNanos) {
            this.rootType = rootType;
            this.rootIdentityHash = rootIdentityHash;
            this.rootEntries = rootEntries;
            this.tickSequence = tickSequence;
            this.tickInProgress = tickInProgress;
            this.players = players;
            this.frozen = frozen;
            this.startedAtNanos = startedAtNanos;
            this.startedCpuNanos = startedCpuNanos;
        }
    }

    private record StackCapture(String firstCaller, List<String> frames) {
    }

    private record Settings(
            boolean enabled,
            long slowThresholdNanos,
            int maxStackFrames,
            long maxFileBytes,
            long maxDurationNanos,
            long maxEvents) {
        static Settings defaults() {
            return new Settings(false, 50_000_000L, 192, 16L * 1024L * 1024L,
                    30L * 60_000_000_000L, 5000L);
        }

        static Settings parse(Properties properties) {
            Settings defaults = defaults();
            boolean enabled = Boolean.parseBoolean(properties.getProperty(
                    "nbt-copy-observer-enabled", Boolean.toString(defaults.enabled)));
            long thresholdMs = boundedLong(
                    properties, "nbt-copy-threshold-ms", defaults.slowThresholdNanos / 1_000_000L, 1L, 60_000L);
            int maxFrames = (int)boundedLong(
                    properties, "nbt-copy-max-stack-frames", defaults.maxStackFrames, 32L, 512L);
            long maxFileMb = boundedLong(
                    properties, "diagnostic-max-file-mb", defaults.maxFileBytes / 1048576L, 1L, 1024L);
            long maxDurationMinutes = boundedLong(
                    properties, "diagnostic-max-duration-minutes",
                    defaults.maxDurationNanos / 60_000_000_000L, 1L, 1440L);
            long maxEvents = boundedLong(
                    properties, "diagnostic-max-events", defaults.maxEvents, 1L, 1_000_000L);
            return new Settings(
                    enabled,
                    thresholdMs * 1_000_000L,
                    maxFrames,
                    maxFileMb * 1048576L,
                    maxDurationMinutes * 60_000_000_000L,
                    maxEvents);
        }

        private static long boundedLong(Properties properties, String key, long fallback, long min, long max) {
            String raw = properties.getProperty(key);
            if (raw == null || raw.isBlank()) {
                return fallback;
            }
            long value = Long.parseLong(raw.trim());
            if (value < min || value > max) {
                throw new IllegalArgumentException(key + " outside " + min + ".." + max);
            }
            return value;
        }
    }
}
