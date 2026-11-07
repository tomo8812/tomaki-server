package maki.chunksmithcache;

import java.util.Map;
import java.util.TreeMap;
import org.slf4j.LoggerFactory;

/** Fixed call-site keys, bounded histograms, interval counts. No per-chunk INFO or files. */
public final class PipelineMetrics {
    private static final Map<String, Long> COUNTS = new TreeMap<>();
    private static final Map<String, LatencyHistogram> TIMES = new TreeMap<>();
    private static long lastReport = System.nanoTime();
    private PipelineMetrics() {}
    public static synchronized void count(String name) { COUNTS.merge(name, 1L, Long::sum); }
    public static synchronized void time(String name, long nanos) {
        TIMES.computeIfAbsent(name, ignored -> new LatencyHistogram()).record(nanos);
    }
    static synchronized void report() {
        long now = System.nanoTime();
        if (now - lastReport < 60_000_000_000L) return;
        var config = com.kishku7.chunksmith.ChunksmithProvider.get().getConfig();
        LoggerFactory.getLogger("MakiChunkCache").info(
            "Chunksmith effective config: ioThrottle={}, maxChunkMillis={}, dispatchMaxConcurrent={}",
            config.isIoThrottleEnabled(), config.getThrottleMaxChunkMillis(), config.getDispatchMaxConcurrent());
        LoggerFactory.getLogger("MakiChunkCache").info(
            "Generation pipeline intervalMs={}, counts={}, latencyMs=[{}], worldgen-internal=unknown, sharedBudget=[{}]",
            (now-lastReport)/1_000_000, COUNTS, TIMES, LodResourceBudgetBridge.status());
        COUNTS.clear(); TIMES.clear(); lastReport = now;
    }
}
