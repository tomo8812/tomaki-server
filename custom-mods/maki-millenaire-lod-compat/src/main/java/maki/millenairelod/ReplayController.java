package maki.millenairelod;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Adaptive admission and deferred-candidate controller.
 *
 * <p>This class intentionally has no compile-time dependency on Minecraft, Millenaire or
 * Chunksmith. The compatibility jar is loaded beside all three, but keeping the controller on
 * JDK APIs plus reflection makes it tolerant of minor implementation drift and lets every optional
 * pressure signal fail independently rather than making the whole mod fail to load.</p>
 */
public final class ReplayController {
    private static final int MAX_BURST = 64;
    private static final int QUEUE_TARGET_AT_FULL_HEADROOM = 72;
    private static final int QUEUE_TARGET_AT_LOW_HEADROOM = 12;
    private static final int MAIN_HARD_STOP = 72;
    private static final int LONE_HARD_STOP = 64;

    // Main-queue DEFERRED candidates are retried only after useful state has changed. This is a
    // fallback for versions where the exact coverage redirect cannot be installed; when exact
    // bounds are known, a newly-complete neighbourhood bypasses the timer immediately.
    private static final int[] DEFER_BACKOFF_TICKS = {20, 40, 80, 160, 200};

    private static final Map<Object, LevelState> LEVEL_STATES = new WeakHashMap<>();
    private static final Map<Object, QueueState> QUEUE_STATES = new WeakHashMap<>();

    private static volatile DeferredAccess deferredAccess;
    private static volatile RuntimeAccess runtimeAccess;
    private static volatile ChunksmithAccess chunksmithAccess;
    private static volatile ConfigAccess configAccess;

    private ReplayController() {
    }

    /** Replacement for Compat.replay. A budget of zero is a real stop, not a floor of one. */
    public static void replay(Object level, Object queue) {
        if (level == null || queue == null) {
            return;
        }
        RuntimeAccess rt = runtime(level);
        if (rt == null) {
            return;
        }
        long tick = rt.gameTime(level);
        if (tick < 0L || tick % 20L != 0L) {
            return;
        }
        if (!generationEnabled()) {
            return;
        }

        DeferredAccess da = deferred(level);
        if (da == null) {
            return;
        }
        Object data = da.get(level);
        if (data == null) {
            return;
        }

        int budget = computeBudget(level, queue, rt);
        if (budget <= 0) {
            return;
        }

        Object server = rt.server(level);
        if (server == null) {
            return;
        }
        int radius = Math.max(1, rt.viewDistance(server) + 1);
        List<?> players = rt.players(level);
        if (players == null || players.isEmpty()) {
            return;
        }

        int admitted = 0;
        for (Object player : players) {
            Object at = rt.playerChunk(player);
            if (at == null) {
                continue;
            }
            int px = rt.chunkX(at);
            int pz = rt.chunkZ(at);
            if (px == Integer.MIN_VALUE || pz == Integer.MIN_VALUE) {
                continue;
            }
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int x = px + dx;
                    int z = pz + dz;
                    long key = packChunk(x, z);
                    if (!da.pendingContains(data, key) || da.queuedContains(data, key)) {
                        continue;
                    }
                    if (!rt.hasChunk(level, x, z)) {
                        continue;
                    }
                    if (!readyForReplay(level, key, tick, rt)) {
                        continue;
                    }
                    Object chunkPos = rt.newChunkPos(x, z);
                    if (chunkPos == null || !VillagePlacementReadiness.candidateReady(level, chunkPos)) {
                        continue;
                    }
                    Object blockPos = rt.newBlockPos((x << 4) + 8, 64, (z << 4) + 8);
                    if (blockPos == null) {
                        continue;
                    }
                    da.remember(data, key, true);
                    if (!rt.enqueue(queue, blockPos)) {
                        da.release(data, key);
                        continue;
                    }
                    noteAdmission(queue);
                    if (++admitted >= budget) {
                        return;
                    }
                }
            }
        }
    }

    /** Replacement for Compat.beginCandidate. */
    public static boolean beginCandidate(Object level, Object pos) {
        if (level == null || pos == null) {
            return false;
        }
        RuntimeAccess rt = runtime(level);
        DeferredAccess da = deferred(level);
        if (rt == null || da == null) {
            return false;
        }
        Object data = da.get(level);
        if (data == null) {
            return false;
        }

        int bx = rt.blockX(pos);
        int bz = rt.blockZ(pos);
        if (bx == Integer.MIN_VALUE || bz == Integer.MIN_VALUE) {
            return false;
        }
        int cx = bx >> 4;
        int cz = bz >> 4;
        long key = packChunk(cx, cz);
        da.release(data, key);

        long tick = rt.gameTime(level);
        LevelState ls = levelState(level);
        DeferredState wait;
        synchronized (ls) {
            wait = ls.deferred.get(key);
        }

        if (wait != null) {
            boolean coverageReady = !wait.hasBounds || rt.hasChunksAt(level,
                    wait.minBlockX, wait.minBlockZ, wait.maxBlockX, wait.maxBlockZ);
            if (!coverageReady) {
                da.remember(data, key, false);
                synchronized (ls) {
                    wait.touchedTick = tick;
                }
                return false;
            }
            // Exact coverage becoming ready is a meaningful state change. Do not force the candidate
            // to sit out the rest of a timer that was only meant to avoid blind retries.
            if (!wait.hasBounds && tick >= 0L && tick < wait.nextRetryTick) {
                da.remember(data, key, false);
                return false;
            }
        }

        Object chunkPos = rt.newChunkPos(cx, cz);
        if (!rt.hasChunk(level, cx, cz) || chunkPos == null
                || !VillagePlacementReadiness.candidateReady(level, chunkPos)) {
            da.remember(data, key, false);
            return false;
        }

        da.consume(data, key);
        synchronized (ls) {
            ls.deferred.remove(key);
        }
        return true;
    }

    /**
     * Redirect target for Millenaire's exact surrounding-chunk readiness check. This records the
     * real village radius selected by Millenaire rather than guessing one in the compatibility mod.
     */
    public static boolean coverageCheck(Object level, Object from, Object to) {
        RuntimeAccess rt = runtime(level);
        if (rt == null || level == null || from == null || to == null) {
            return false;
        }
        boolean ready = rt.hasChunksAt(level, from, to);
        if (!ready) {
            coverageMiss(level, from, to, rt);
        }
        return ready;
    }

    /** Called in place of Compat.waiting for an actual DEFERRED result. */
    public static void deferred(Object level, Object pos, boolean queued) {
        if (level == null || pos == null) {
            return;
        }
        RuntimeAccess rt = runtime(level);
        DeferredAccess da = deferred(level);
        if (rt == null || da == null) {
            return;
        }
        Object data = da.get(level);
        if (data == null) {
            return;
        }
        int bx = rt.blockX(pos);
        int bz = rt.blockZ(pos);
        if (bx == Integer.MIN_VALUE || bz == Integer.MIN_VALUE) {
            return;
        }
        long key = packChunk(bx >> 4, bz >> 4);
        // DEFERRED means the candidate was consumed by Millenaire's poll.
        // A main-queue candidate is not re-enqueued by processMainQueue, so
        // retaining its stale queued flag would suppress replay forever.
        // The lone-building path reclaims its queued flag via addLast below.
        da.release(data, key);
        da.remember(data, key, false);

        // Millenaire's lone-building queue already carries its own retryAfterTick=+200 logic.
        // Only main-queue DEFERRED candidates need an additional controller here.
        if (!queued) {
            return;
        }
        long tick = rt.gameTime(level);
        LevelState ls = levelState(level);
        synchronized (ls) {
            DeferredState state = ls.deferred.computeIfAbsent(key, ignored -> new DeferredState());
            int index = Math.min(state.deferrals, DEFER_BACKOFF_TICKS.length - 1);
            state.deferrals++;
            if (tick >= 0L) {
                state.nextRetryTick = Math.max(state.nextRetryTick, tick + DEFER_BACKOFF_TICKS[index]);
                state.touchedTick = tick;
            }
        }
    }

    private static void coverageMiss(Object level, Object from, Object to, RuntimeAccess rt) {
        int fx = rt.blockX(from);
        int fz = rt.blockZ(from);
        int tx = rt.blockX(to);
        int tz = rt.blockZ(to);
        if (fx == Integer.MIN_VALUE || fz == Integer.MIN_VALUE
                || tx == Integer.MIN_VALUE || tz == Integer.MIN_VALUE) {
            return;
        }
        int minX = Math.min(fx, tx);
        int maxX = Math.max(fx, tx);
        int minZ = Math.min(fz, tz);
        int maxZ = Math.max(fz, tz);
        int centerX = (int) (((long) minX + (long) maxX) / 2L);
        int centerZ = (int) (((long) minZ + (long) maxZ) / 2L);
        long key = packChunk(centerX >> 4, centerZ >> 4);
        long tick = rt.gameTime(level);
        LevelState ls = levelState(level);
        synchronized (ls) {
            DeferredState state = ls.deferred.computeIfAbsent(key, ignored -> new DeferredState());
            state.hasBounds = true;
            state.minBlockX = minX;
            state.maxBlockX = maxX;
            state.minBlockZ = minZ;
            state.maxBlockZ = maxZ;
            state.touchedTick = tick;
        }
    }

    private static boolean readyForReplay(Object level, long key, long tick, RuntimeAccess rt) {
        LevelState ls = levelState(level);
        DeferredState wait;
        synchronized (ls) {
            wait = ls.deferred.get(key);
        }
        if (wait == null) {
            return true;
        }
        if (wait.hasBounds) {
            // Exact readiness is stronger than a timer: if Millenaire would still return DEFERRED,
            // do not put the candidate back into its queue at all.
            return rt.hasChunksAt(level, wait.minBlockX, wait.minBlockZ,
                    wait.maxBlockX, wait.maxBlockZ);
        }
        return tick < 0L || tick >= wait.nextRetryTick;
    }

    private static int computeBudget(Object level, Object queue, RuntimeAccess rt) {
        QueueSnapshot q = rt.queueSnapshot(queue);
        boolean queueKnown = q != null;
        if (!queueKnown) {
            // Unknown downstream state: do not burst blindly. A small amount remains possible if
            // the server itself is demonstrably healthy.
            q = new QueueSnapshot(0, 0);
        }
        QueueState qs = queueState(queue);
        QueueObservation observation;
        synchronized (qs) {
            observation = qs.observe(q.main, q.lone);
        }

        SignalSnapshot s = sampleSignals(level, rt, qs);

        // Hard gates mean there is no spare capacity to admit new Millenaire work. Unlike 1.0.1,
        // zero is intentionally valid and remains zero through the caller.
        if (q.main >= MAIN_HARD_STOP || q.lone >= LONE_HARD_STOP
                || (observation.mainStallSamples >= 2 && q.main >= 8)
                || s.hardStop) {
            return 0;
        }

        double headroom = 1.0D;

        if (s.mspt >= 0.0D) {
            // Replay is main-thread village work. Above 50 ms/tick the server has no 20 TPS budget
            // left; between 25 and 50 we smoothly yield instead of treating one threshold as magic.
            if (s.mspt >= 50.0D) {
                return 0;
            }
            if (s.mspt > 25.0D) {
                headroom *= clamp((50.0D - s.mspt) / 25.0D, 0.0D, 1.0D);
            }
        }

        if (s.csDispatchRatio >= 0.0D) {
            // Chunksmith has already combined tick latency, slow chunks and its writer pressure into
            // the width it is willing to run. Respect that decision instead of double-counting its
            // raw inputs. A heavily narrowed pipeline means this compatibility task should yield.
            if (s.csDispatchRatio <= 0.12D) {
                return 0;
            }
            headroom *= Math.sqrt(clamp(s.csDispatchRatio, 0.0D, 1.0D));
        }

        if (s.heapPercent >= 0.0D) {
            if (s.heapPercent >= 93.0D) {
                return 0;
            }
            if (s.heapPercent > 78.0D) {
                headroom *= clamp((93.0D - s.heapPercent) / 15.0D, 0.0D, 1.0D);
            }
        }

        if (s.gcFraction >= 0.0D) {
            if (s.gcFraction >= 0.15D) {
                return 0;
            }
            if (s.gcFraction > 0.02D) {
                headroom *= clamp((0.15D - s.gcFraction) / 0.13D, 0.0D, 1.0D);
            }
        }

        // A renderer/write queue that keeps growing is useful, but its absolute depth is not: the
        // capacity is implementation-specific. Use only the trend, and only as a soft signal.
        if (s.sinkQueueGrowth > 0 && s.sinkQueue >= 16) {
            headroom *= s.sinkQueueGrowth >= 16 ? 0.35D : 0.65D;
        }

        // Downstream Millenaire backlog is the most direct capacity measurement. Its actual drain
        // rate is observed after accounting for our own previous admissions.
        if (observation.externalMainGrowth > 0) {
            headroom *= observation.externalMainGrowth >= 8 ? 0.35D : 0.65D;
        }
        if (observation.loneGrowth > 0 && q.lone > 0) {
            headroom *= observation.loneGrowth >= 8 ? 0.50D : 0.75D;
        }

        headroom = clamp(headroom, 0.0D, 1.0D);
        if (headroom < 0.06D) {
            return 0;
        }

        int target = QUEUE_TARGET_AT_LOW_HEADROOM
                + (int) Math.round((QUEUE_TARGET_AT_FULL_HEADROOM - QUEUE_TARGET_AT_LOW_HEADROOM) * headroom);
        int room = Math.max(0, target - q.main);
        if (room == 0) {
            return 0;
        }

        int loadCap = (int) Math.floor(MAX_BURST * Math.pow(headroom, 1.35D));
        if (loadCap <= 0) {
            return 0;
        }
        int budget = Math.min(MAX_BURST, Math.min(room, loadCap));
        if (!queueKnown) {
            budget = Math.min(budget, 8);
        }

        // Once there is a live main backlog, never admit faster than the measured service rate.
        if (q.main > 0 && observation.mainDrainEma >= 0.0D) {
            int sustainable = (int) Math.floor(observation.mainDrainEma * 0.85D);
            budget = Math.min(budget, Math.max(0, sustainable));
        }

        // A lone-building backlog is intentionally slower (its own 200-tick backoff). It should not
        // starve ordinary play by letting the main replay queue continuously refill behind it.
        if (q.lone > 0) {
            double loneFactor = 1.0D - 0.80D * clamp(q.lone / (double) LONE_HARD_STOP, 0.0D, 1.0D);
            budget = (int) Math.floor(budget * loneFactor);
        }

        return Math.max(0, budget);
    }

    private static SignalSnapshot sampleSignals(Object level, RuntimeAccess rt, QueueState qs) {
        SignalSnapshot out = new SignalSnapshot();
        Object server = rt.server(level);
        double rawMspt = server == null ? -1.0D : rt.averageMspt(server);
        synchronized (qs) {
            if (rawMspt >= 0.0D && Double.isFinite(rawMspt)) {
                qs.msptEma = qs.msptEma < 0.0D ? rawMspt : qs.msptEma * 0.75D + rawMspt * 0.25D;
            }
            out.mspt = qs.msptEma;
        }

        ChunksmithSignals cs = chunksmith().sample();
        if (cs != null) {
            out.hardStop = cs.probing || cs.heapHolding || cs.residencyDraining
                    || cs.generationHeld || cs.atCeiling;
            if (cs.limit > 0 && cs.ceiling > 0) {
                out.csDispatchRatio = clamp(cs.limit / (double) cs.ceiling, 0.0D, 1.0D);
            }
            if (cs.heapLivePercent >= 0.0D) {
                out.heapPercent = cs.heapLivePercent;
            }
            synchronized (qs) {
                if (cs.sinkQueue >= 0) {
                    out.sinkQueue = cs.sinkQueue;
                    out.sinkQueueGrowth = qs.lastSinkQueue < 0 ? 0 : cs.sinkQueue - qs.lastSinkQueue;
                    qs.lastSinkQueue = cs.sinkQueue;
                }
            }
        }

        if (out.heapPercent < 0.0D) {
            out.heapPercent = fallbackHeapPercent();
        }
        out.gcFraction = GcSampler.sample();
        return out;
    }

    private static double fallbackHeapPercent() {
        try {
            MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heap = bean.getHeapMemoryUsage();
            long max = heap.getMax();
            return max > 0L ? (100.0D * heap.getUsed() / max) : -1.0D;
        } catch (RuntimeException | LinkageError ignored) {
            return -1.0D;
        }
    }

    private static void noteAdmission(Object queue) {
        QueueState state = queueState(queue);
        synchronized (state) {
            state.admittedSinceObservation++;
        }
    }

    private static LevelState levelState(Object level) {
        synchronized (LEVEL_STATES) {
            return LEVEL_STATES.computeIfAbsent(level, ignored -> new LevelState());
        }
    }

    private static QueueState queueState(Object queue) {
        synchronized (QUEUE_STATES) {
            return QUEUE_STATES.computeIfAbsent(queue, ignored -> new QueueState());
        }
    }

    private static DeferredAccess deferred(Object level) {
        DeferredAccess local = deferredAccess;
        if (local != null) {
            return local.usable ? local : null;
        }
        synchronized (ReplayController.class) {
            local = deferredAccess;
            if (local == null) {
                local = new DeferredAccess(level);
                deferredAccess = local;
            }
        }
        return local.usable ? local : null;
    }

    private static RuntimeAccess runtime(Object level) {
        RuntimeAccess local = runtimeAccess;
        if (local != null) {
            return local.usable ? local : null;
        }
        synchronized (ReplayController.class) {
            local = runtimeAccess;
            if (local == null) {
                local = new RuntimeAccess(level);
                runtimeAccess = local;
            }
        }
        return local.usable ? local : null;
    }

    private static ChunksmithAccess chunksmith() {
        ChunksmithAccess local = chunksmithAccess;
        if (local != null) {
            return local;
        }
        synchronized (ReplayController.class) {
            local = chunksmithAccess;
            if (local == null) {
                local = new ChunksmithAccess();
                chunksmithAccess = local;
            }
        }
        return local;
    }

    private static boolean generationEnabled() {
        ConfigAccess local = configAccess;
        if (local == null) {
            synchronized (ReplayController.class) {
                local = configAccess;
                if (local == null) {
                    local = new ConfigAccess();
                    configAccess = local;
                }
            }
        }
        return local.enabled();
    }

    private static long packChunk(int x, int z) {
        return ((long) x & 0xffffffffL) | (((long) z & 0xffffffffL) << 32);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static Method findMethod(Class<?> type, String name, int parameterCount) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
                if (method.getName().equals(name) && method.getParameterCount() == parameterCount) {
                    try {
                        method.setAccessible(true);
                    } catch (RuntimeException ignored) {
                        // Public invocation can still work even if modules reject setAccessible.
                    }
                    return method;
                }
            }
        }
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == parameterCount) {
                return method;
            }
        }
        return null;
    }

    private static Method findAnyMethod(Class<?> type, int parameterCount, String... names) {
        for (String name : names) {
            Method method = findMethod(type, name, parameterCount);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    private static Field findField(Class<?> type, String name) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                Field field = c.getDeclaredField(name);
                try {
                    field.setAccessible(true);
                } catch (RuntimeException ignored) {
                }
                return field;
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    private static Object invoke(Method method, Object receiver, Object... args) {
        if (method == null) {
            return null;
        }
        try {
            return method.invoke(receiver, args);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static final class LevelState {
        final Map<Long, DeferredState> deferred = new HashMap<>();
    }

    private static final class DeferredState {
        boolean hasBounds;
        int minBlockX;
        int maxBlockX;
        int minBlockZ;
        int maxBlockZ;
        int deferrals;
        long nextRetryTick;
        long touchedTick;
    }

    private static final class QueueSnapshot {
        final int main;
        final int lone;

        QueueSnapshot(int main, int lone) {
            this.main = Math.max(0, main);
            this.lone = Math.max(0, lone);
        }
    }

    private static final class QueueObservation {
        int externalMainGrowth;
        int loneGrowth;
        int mainStallSamples;
        int loneStallSamples;
        double mainDrainEma = -1.0D;
    }

    private static final class QueueState {
        int lastMain = -1;
        int lastLone = -1;
        int admittedSinceObservation;
        int mainStallSamples;
        int loneStallSamples;
        double mainDrainEma = -1.0D;
        double msptEma = -1.0D;
        int lastSinkQueue = -1;

        QueueObservation observe(int main, int lone) {
            QueueObservation result = new QueueObservation();
            if (lastMain >= 0) {
                int expected = Math.max(0, lastMain + admittedSinceObservation);
                int drained = Math.max(0, expected - main);
                result.externalMainGrowth = Math.max(0, main - expected);
                mainDrainEma = mainDrainEma < 0.0D ? drained : mainDrainEma * 0.70D + drained * 0.30D;
                if (expected > 0 && main > 0 && drained == 0) {
                    mainStallSamples++;
                } else {
                    mainStallSamples = 0;
                }
            }
            if (lastLone >= 0) {
                result.loneGrowth = Math.max(0, lone - lastLone);
                if (lastLone > 0 && lone >= lastLone) {
                    loneStallSamples++;
                } else {
                    loneStallSamples = 0;
                }
            }
            lastMain = main;
            lastLone = lone;
            admittedSinceObservation = 0;
            result.mainStallSamples = mainStallSamples;
            result.loneStallSamples = loneStallSamples;
            result.mainDrainEma = mainDrainEma;
            return result;
        }
    }

    private static final class SignalSnapshot {
        double mspt = -1.0D;
        double csDispatchRatio = -1.0D;
        double heapPercent = -1.0D;
        double gcFraction = -1.0D;
        boolean hardStop;
        int sinkQueue = -1;
        int sinkQueueGrowth;
    }

    private static final class GcSampler {
        private static long lastWallNanos;
        private static long lastCollectionMillis = -1L;
        private static double ema = -1.0D;

        static synchronized double sample() {
            long now = System.nanoTime();
            long collection = 0L;
            boolean known = false;
            try {
                for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
                    long time = bean.getCollectionTime();
                    if (time >= 0L) {
                        collection += time;
                        known = true;
                    }
                }
            } catch (RuntimeException | LinkageError ignored) {
                return ema;
            }
            if (!known) {
                return ema;
            }
            if (lastWallNanos == 0L || lastCollectionMillis < 0L) {
                lastWallNanos = now;
                lastCollectionMillis = collection;
                return ema;
            }
            double wallMillis = (now - lastWallNanos) / 1_000_000.0D;
            if (wallMillis < 500.0D) {
                return ema;
            }
            long delta = Math.max(0L, collection - lastCollectionMillis);
            double fraction = clamp(delta / wallMillis, 0.0D, 1.0D);
            ema = ema < 0.0D ? fraction : ema * 0.70D + fraction * 0.30D;
            lastWallNanos = now;
            lastCollectionMillis = collection;
            return ema;
        }
    }

    private static final class DeferredAccess {
        final boolean usable;
        final Method get;
        final Method remember;
        final Method release;
        final Method consume;
        final Field pending;
        final Field queued;
        Method contains;

        DeferredAccess(Object level) {
            Method get0 = null, remember0 = null, release0 = null, consume0 = null;
            Field pending0 = null, queued0 = null;
            boolean ok = false;
            try {
                Class<?> type = Class.forName("maki.millenairelod.DeferredData", false,
                        ReplayController.class.getClassLoader());
                get0 = findMethod(type, "get", 1);
                remember0 = findMethod(type, "remember", 2);
                release0 = findMethod(type, "release", 1);
                consume0 = findMethod(type, "consume", 1);
                pending0 = findField(type, "pending");
                queued0 = findField(type, "queued");
                ok = get0 != null && remember0 != null && release0 != null && consume0 != null
                        && pending0 != null && queued0 != null;
            } catch (ClassNotFoundException | LinkageError ignored) {
            }
            get = get0;
            remember = remember0;
            release = release0;
            consume = consume0;
            pending = pending0;
            queued = queued0;
            usable = ok;
        }

        Object get(Object level) {
            return invoke(get, null, level);
        }

        void remember(Object data, long key, boolean claimed) {
            invoke(remember, data, key, claimed);
        }

        void release(Object data, long key) {
            invoke(release, data, key);
        }

        void consume(Object data, long key) {
            invoke(consume, data, key);
        }

        boolean pendingContains(Object data, long key) {
            return contains(set(pending, data), key);
        }

        boolean queuedContains(Object data, long key) {
            return contains(set(queued, data), key);
        }

        private Object set(Field field, Object data) {
            try {
                return field.get(data);
            } catch (IllegalAccessException | RuntimeException ignored) {
                return null;
            }
        }

        private boolean contains(Object set, long key) {
            if (set == null) {
                return false;
            }
            Method local = contains;
            if (local == null || local.getDeclaringClass() != set.getClass()) {
                local = findMethod(set.getClass(), "contains", 1);
                contains = local;
            }
            Object value = invoke(local, set, key);
            return value instanceof Boolean && (Boolean) value;
        }
    }

    private static final class RuntimeAccess {
        final boolean usable;
        final Method getGameTime;
        final Method getServer;
        final Method hasChunk;
        final Method hasChunksAt;
        final Method players;
        final Constructor<?> blockPosCtor;
        final Constructor<?> chunkPosCtor;
        final Method blockGetX;
        final Method blockGetZ;
        final Field chunkX;
        final Field chunkZ;
        volatile Method serverAverage;
        volatile Method serverPlayerList;
        volatile Method viewDistance;
        volatile Method playerChunkPosition;
        volatile Method queueGetStats;
        volatile Method queueEnqueue;

        RuntimeAccess(Object level) {
            Method game = null, server = null, chunk = null, chunksAt = null, playerList = null;
            Constructor<?> bpCtor = null, cpCtor = null;
            Method bx = null, bz = null;
            Field cx = null, cz = null;
            boolean ok = false;
            try {
                Class<?> levelClass = level.getClass();
                game = findAnyMethod(levelClass, 0, "getGameTime");
                server = findAnyMethod(levelClass, 0, "getServer");
                chunk = findAnyMethod(levelClass, 2, "hasChunk");
                Class<?> blockPos = Class.forName("net.minecraft.core.BlockPos", false,
                        ReplayController.class.getClassLoader());
                Class<?> chunkPos = Class.forName("net.minecraft.world.level.ChunkPos", false,
                        ReplayController.class.getClassLoader());
                chunksAt = findAnyMethod(levelClass, 2, "hasChunksAt");
                playerList = findAnyMethod(levelClass, 0, "players");
                bpCtor = blockPos.getConstructor(int.class, int.class, int.class);
                cpCtor = chunkPos.getConstructor(int.class, int.class);
                bx = findAnyMethod(blockPos, 0, "getX");
                bz = findAnyMethod(blockPos, 0, "getZ");
                cx = findField(chunkPos, "x");
                cz = findField(chunkPos, "z");
                ok = game != null && server != null && chunk != null && chunksAt != null
                        && playerList != null && bpCtor != null && cpCtor != null
                        && bx != null && bz != null && cx != null && cz != null;
            } catch (ReflectiveOperationException | LinkageError ignored) {
            }
            getGameTime = game;
            getServer = server;
            hasChunk = chunk;
            hasChunksAt = chunksAt;
            players = playerList;
            blockPosCtor = bpCtor;
            chunkPosCtor = cpCtor;
            blockGetX = bx;
            blockGetZ = bz;
            chunkX = cx;
            chunkZ = cz;
            usable = ok;
        }

        long gameTime(Object level) {
            Object value = invoke(getGameTime, level);
            return value instanceof Number ? ((Number) value).longValue() : -1L;
        }

        Object server(Object level) {
            return invoke(getServer, level);
        }

        boolean hasChunk(Object level, int x, int z) {
            Object value = invoke(hasChunk, level, x, z);
            return value instanceof Boolean && (Boolean) value;
        }

        boolean hasChunksAt(Object level, Object from, Object to) {
            Object value = invoke(hasChunksAt, level, from, to);
            return value instanceof Boolean && (Boolean) value;
        }

        boolean hasChunksAt(Object level, int minX, int minZ, int maxX, int maxZ) {
            Object from = newBlockPos(minX, 0, minZ);
            Object to = newBlockPos(maxX, 0, maxZ);
            return from != null && to != null && hasChunksAt(level, from, to);
        }

        @SuppressWarnings("unchecked")
        List<?> players(Object level) {
            Object value = invoke(players, level);
            return value instanceof List<?> ? (List<?>) value : List.of();
        }

        Object newBlockPos(int x, int y, int z) {
            try {
                return blockPosCtor.newInstance(x, y, z);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return null;
            }
        }

        Object newChunkPos(int x, int z) {
            try {
                return chunkPosCtor.newInstance(x, z);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return null;
            }
        }

        int blockX(Object pos) {
            Object value = invoke(blockGetX, pos);
            return value instanceof Number ? ((Number) value).intValue() : Integer.MIN_VALUE;
        }

        int blockZ(Object pos) {
            Object value = invoke(blockGetZ, pos);
            return value instanceof Number ? ((Number) value).intValue() : Integer.MIN_VALUE;
        }

        int chunkX(Object pos) {
            try {
                return chunkX.getInt(pos);
            } catch (IllegalAccessException | RuntimeException ignored) {
                return Integer.MIN_VALUE;
            }
        }

        int chunkZ(Object pos) {
            try {
                return chunkZ.getInt(pos);
            } catch (IllegalAccessException | RuntimeException ignored) {
                return Integer.MIN_VALUE;
            }
        }

        double averageMspt(Object server) {
            Method local = serverAverage;
            if (local == null) {
                local = findAnyMethod(server.getClass(), 0,
                        "getAverageTickTimeNanos", "getAverageNanosPerTick", "getAverageTickTime");
                serverAverage = local;
            }
            Object value = invoke(local, server);
            if (!(value instanceof Number)) {
                return -1.0D;
            }
            double raw = ((Number) value).doubleValue();
            if (raw < 0.0D) {
                return -1.0D;
            }
            String name = local == null ? "" : local.getName().toLowerCase();
            return name.contains("nano") ? raw / 1_000_000.0D : raw;
        }

        int viewDistance(Object server) {
            Method spl = serverPlayerList;
            if (spl == null) {
                spl = findAnyMethod(server.getClass(), 0, "getPlayerList");
                serverPlayerList = spl;
            }
            Object list = invoke(spl, server);
            if (list == null) {
                return 10;
            }
            Method vd = viewDistance;
            if (vd == null) {
                vd = findAnyMethod(list.getClass(), 0, "getViewDistance");
                viewDistance = vd;
            }
            Object value = invoke(vd, list);
            return value instanceof Number ? ((Number) value).intValue() : 10;
        }

        Object playerChunk(Object player) {
            Method method = playerChunkPosition;
            if (method == null) {
                method = findAnyMethod(player.getClass(), 0, "chunkPosition");
                playerChunkPosition = method;
            }
            return invoke(method, player);
        }

        QueueSnapshot queueSnapshot(Object queue) {
            Method method = queueGetStats;
            if (method == null || !method.getDeclaringClass().isAssignableFrom(queue.getClass())) {
                method = findAnyMethod(queue.getClass(), 0, "getStats");
                queueGetStats = method;
            }
            Object value = invoke(method, queue);
            if (!(value instanceof Map<?, ?> map)) {
                return null;
            }
            return new QueueSnapshot(number(map.get("mainQueueSize")), number(map.get("lbQueueSize")));
        }

        boolean enqueue(Object queue, Object blockPos) {
            Method method = queueEnqueue;
            if (method == null || !method.getDeclaringClass().isAssignableFrom(queue.getClass())) {
                method = findAnyMethod(queue.getClass(), 1, "enqueue");
                queueEnqueue = method;
            }
            if (method == null) {
                return false;
            }
            try {
                method.invoke(queue, blockPos);
                return true;
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return false;
            }
        }

        private int number(Object value) {
            return value instanceof Number ? ((Number) value).intValue() : 0;
        }
    }

    private static final class ConfigAccess {
        final Object serverConfig;
        final Field generateVillages;
        final Field generateLoneBuildings;
        volatile Method valueGet;
        final boolean usable;

        ConfigAccess() {
            Object sc = null;
            Field villages = null, lone = null;
            boolean ok = false;
            try {
                Class<?> type = Class.forName("org.millenaire.config.MillenaireServerConfig", false,
                        ReplayController.class.getClassLoader());
                Field server = findField(type, "SERVER");
                sc = server == null ? null : server.get(null);
                if (sc != null) {
                    villages = findField(sc.getClass(), "generateVillages");
                    lone = findField(sc.getClass(), "generateLoneBuildings");
                    ok = villages != null && lone != null;
                }
            } catch (ReflectiveOperationException | RuntimeException | LinkageError ignored) {
            }
            serverConfig = sc;
            generateVillages = villages;
            generateLoneBuildings = lone;
            usable = ok;
        }

        boolean enabled() {
            if (!usable) {
                // Unknown config is safer treated as enabled: original Compat only used this as an
                // early-out optimisation. All later gates still protect the server.
                return true;
            }
            return read(generateVillages) || read(generateLoneBuildings);
        }

        private boolean read(Field field) {
            try {
                Object holder = field.get(serverConfig);
                if (holder == null) {
                    return false;
                }
                Method get = valueGet;
                if (get == null || !get.getDeclaringClass().isAssignableFrom(holder.getClass())) {
                    get = findAnyMethod(holder.getClass(), 0, "get");
                    valueGet = get;
                }
                Object value = invoke(get, holder);
                return value instanceof Boolean && (Boolean) value;
            } catch (IllegalAccessException | RuntimeException ignored) {
                return false;
            }
        }
    }

    private static final class ChunksmithSignals {
        boolean probing;
        boolean heapHolding;
        boolean residencyDraining;
        boolean generationHeld;
        boolean atCeiling;
        double heapLivePercent = -1.0D;
        int inFlight = -1;
        int limit = -1;
        int ceiling = -1;
        int sinkQueue = -1;
    }

    private static final class ChunksmithAccess {
        final Method tickProbing;
        final Method tickAtCeiling;
        final Method heapHolding;
        final Method heapLive;
        final Method residencyDraining;
        final Method generationHeld;
        final Field inFlight;
        final Field limit;
        final Field ceiling;
        final Field sinkQueue;

        ChunksmithAccess() {
            Method probing = null, atCeiling = null, holding = null, live = null, draining = null, held = null;
            Field inflightF = null, limitF = null, ceilingF = null, sinkF = null;
            try {
                ClassLoader cl = ReplayController.class.getClassLoader();
                Class<?> tick = Class.forName("com.kishku7.chunksmith.util.TickBudget", false, cl);
                probing = findAnyMethod(tick, 0, "isProbing");
                atCeiling = findAnyMethod(tick, 0, "atCeiling");
            } catch (ClassNotFoundException | LinkageError ignored) {
            }
            try {
                Class<?> heap = Class.forName("com.kishku7.chunksmith.util.HeapPressure", false,
                        ReplayController.class.getClassLoader());
                holding = findAnyMethod(heap, 0, "isHolding");
                live = findAnyMethod(heap, 0, "liveEstimatePercent", "usedPercent");
            } catch (ClassNotFoundException | LinkageError ignored) {
            }
            try {
                Class<?> residency = Class.forName("com.kishku7.chunksmith.util.ChunkResidency", false,
                        ReplayController.class.getClassLoader());
                draining = findAnyMethod(residency, 0, "isDraining");
                held = findAnyMethod(residency, 0, "isGenerationHeld");
            } catch (ClassNotFoundException | LinkageError ignored) {
            }
            try {
                Class<?> stats = Class.forName("com.kishku7.chunksmith.util.DispatchStats", false,
                        ReplayController.class.getClassLoader());
                inflightF = findField(stats, "inFlight");
                limitF = findField(stats, "limit");
                ceilingF = findField(stats, "ceiling");
                sinkF = findField(stats, "sinkQueue");
            } catch (ClassNotFoundException | LinkageError ignored) {
            }
            tickProbing = probing;
            tickAtCeiling = atCeiling;
            heapHolding = holding;
            heapLive = live;
            residencyDraining = draining;
            generationHeld = held;
            inFlight = inflightF;
            limit = limitF;
            ceiling = ceilingF;
            sinkQueue = sinkF;
        }

        ChunksmithSignals sample() {
            ChunksmithSignals out = new ChunksmithSignals();
            out.probing = bool(invoke(tickProbing, null));
            out.atCeiling = bool(invoke(tickAtCeiling, null));
            out.heapHolding = bool(invoke(heapHolding, null));
            out.residencyDraining = bool(invoke(residencyDraining, null));
            out.generationHeld = bool(invoke(generationHeld, null));
            Object live = invoke(heapLive, null);
            if (live instanceof Number) {
                out.heapLivePercent = ((Number) live).doubleValue();
            }
            out.inFlight = intField(inFlight);
            out.limit = intField(limit);
            out.ceiling = intField(ceiling);
            out.sinkQueue = intField(sinkQueue);
            return out;
        }

        private boolean bool(Object value) {
            return value instanceof Boolean && (Boolean) value;
        }

        private int intField(Field field) {
            if (field == null || !Modifier.isStatic(field.getModifiers())) {
                return -1;
            }
            try {
                return field.getInt(null);
            } catch (IllegalAccessException | RuntimeException ignored) {
                return -1;
            }
        }
    }
}
