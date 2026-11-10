package maki.chunksmithcache;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.platform.Config;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.LoggerFactory;

public final class PipelineMetrics {
   private static final Map<String, Long> COUNTS = new TreeMap<>();
   private static final Map<String, LatencyHistogram> TIMES = new TreeMap<>();
   private static final Map<String, Long> GAUGES = new TreeMap<>();
   private static long lastReport = System.nanoTime();

   private PipelineMetrics() {
   }

   public static synchronized void gauge(String name, long value) {
      GAUGES.put(name, value);
   }

   public static synchronized void count(String name) {
      COUNTS.merge(name, 1L, Long::sum);
   }

   public static synchronized void time(String name, long nanos) {
      TIMES.computeIfAbsent(name, ignored -> new LatencyHistogram()).record(nanos);
   }

   static synchronized void report() {
      long now = System.nanoTime();
      if (now - lastReport >= 60000000000L) {
         Config config = ChunksmithProvider.get().getConfig();
         LoggerFactory.getLogger("MakiChunkCache")
            .info(
               "Chunksmith effective config: ioThrottle={}, maxChunkMillis={}, dispatchMaxConcurrent={}",
               new Object[]{config.isIoThrottleEnabled(), config.getThrottleMaxChunkMillis(), config.getDispatchMaxConcurrent()}
            );
         LoggerFactory.getLogger("MakiChunkCache")
            .info(
               "Generation pipeline intervalMs={}, counts={}, latencyMs=[{}], gauges={}, cacheIo=[{}], worldgen-internal=unknown, sharedBudget=[{}]",
               new Object[]{(now - lastReport) / 1000000L, COUNTS, TIMES, GAUGES, CacheManager.ioSummary(now), LodResourceBudgetBridge.status()}
            );
         COUNTS.clear();
         TIMES.clear();
         GAUGES.clear();
         lastReport = now;
      }
   }
}
