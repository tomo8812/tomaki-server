package maki.chunksmithcache;

import java.util.Map;
import java.util.TreeMap;
import org.slf4j.LoggerFactory;

/** Fixed call-site keys, bounded histograms, interval counts. No per-chunk INFO or files. */
public final class PipelineMetrics {
    private static final Map<String, Long> COUNTS = new TreeMap<>();
    private static final Map<String, LatencyHistogram> TIMES = new TreeMap<>();
    private static final Map<String, Long> GAUGES = new TreeMap<>();
    private static long lastReport = System.nanoTime();
    private PipelineMetrics() {}
    public static synchronized void gauge(String name, long value) { GAUGES.put(name, value); }
    public static synchronized void count(String name) { COUNTS.merge(name, 1L, Long::sum); }
    public static synchronized void time(String name, long nanos) {
        TIMES.computeIfAbsent(name, ignored -> new LatencyHistogram()).record(nanos);
    }
    static void report() {
        long now = System.nanoTime();
        long intervalMillis;
        String counts, times, gauges;
        synchronized (PipelineMetrics.class) {
            if (now - lastReport < 60_000_000_000L) return;
            intervalMillis=(now-lastReport)/1_000_000;
            counts=COUNTS.toString(); times=TIMES.toString(); gauges=GAUGES.toString();
            COUNTS.clear(); TIMES.clear(); GAUGES.clear(); lastReport=now;
        }
        // Slow log formatting and providers cannot block native writer ACKs.
        var config = com.kishku7.chunksmith.ChunksmithProvider.get().getConfig();
        LoggerFactory.getLogger("MakiChunkCache").info(
            "Chunksmith effective config: ioThrottle={}, maxChunkMillis={}, dispatchMaxConcurrent={}",
            config.isIoThrottleEnabled(), config.getThrottleMaxChunkMillis(), config.getDispatchMaxConcurrent());
        LoggerFactory.getLogger("MakiChunkCache").info(
            "Generation pipeline intervalMs={}, counts={}, latencyMs=[{}], gauges={}, cacheIo=[{}], worldgen-internal=unknown, sharedBudget=[{}]",
            intervalMillis, counts, times, gauges, CacheManager.ioSummary(now), LodResourceBudgetBridge.status());
    }
}
