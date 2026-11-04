package com.kishku7.chunksmith.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorldgenOverreachReporter {
   private static final WorldgenOverreachReporter INSTANCE = new WorldgenOverreachReporter();
   public static final String FAR_CHUNK_MARKER = "Detected setBlock in a far chunk";
   private static final Pattern FAR_CHUNK = Pattern.compile("far chunk \\[(-?\\d+), *(-?\\d+)]");
   private static final Pattern POS_Y = Pattern.compile("y=(-?\\d+)");
   private static final Pattern STATUS = Pattern.compile("status: *(.*?)(?:, currently generating:|$)");
   private static final Pattern FEATURE = Pattern.compile("currently generating: *(.+?)\\s*$");
   private final Logger log = LoggerFactory.getLogger("Chunksmith");
   private final Map<String, WorldgenOverreachReporter.Bucket> active = new ConcurrentHashMap<>();
   private final Map<String, WorldgenOverreachReporter.FeatureStats> features = new ConcurrentHashMap<>();
   private volatile boolean enabled = true;
   private volatile long debounceMillis = 500L;
   private volatile long rollupMillis = 30000L;
   private volatile boolean wasRunning = false;

   public static WorldgenOverreachReporter get() {
      return INSTANCE;
   }

   private WorldgenOverreachReporter() {
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void configure(long debounceMillis, long rollupMillis) {
      this.debounceMillis = Math.max(0L, debounceMillis);
      this.rollupMillis = Math.max(1000L, rollupMillis);
   }

   public void record(String feature, String step, String dimension, int sourceChunkX, int sourceChunkZ, int farChunkX, int farChunkZ, int y, int writeRadius) {
      String f = feature != null && !feature.isEmpty() ? feature : "<unknown>";
      String s = step == null ? "?" : step;
      String d = dimension == null ? "?" : dimension;
      String key = d + "|" + f + "|" + s + "|" + sourceChunkX + "|" + sourceChunkZ;
      this.accumulate(key, f, s, d, sourceChunkX, sourceChunkZ, writeRadius, false, farChunkX, farChunkZ, y);
   }

   public boolean recordFromMessage(String message) {
      if (message != null && message.contains("Detected setBlock in a far chunk")) {
         try {
            Matcher fc = FAR_CHUNK.matcher(message);
            int farX = 0;
            int farZ = 0;
            if (fc.find()) {
               farX = Integer.parseInt(fc.group(1));
               farZ = Integer.parseInt(fc.group(2));
            }

            int y = Integer.MIN_VALUE;
            Matcher my = POS_Y.matcher(message);
            if (my.find()) {
               y = Integer.parseInt(my.group(1));
            }

            String step = "?";
            Matcher ms = STATUS.matcher(message);
            if (ms.find()) {
               step = ms.group(1).trim();
            }

            String feature = "<unknown>";
            Matcher mf = FEATURE.matcher(message);
            if (mf.find()) {
               feature = mf.group(1).trim();
            }

            String key = "best|" + feature + "|" + step;
            this.accumulate(key, feature, step, null, Integer.MIN_VALUE, Integer.MIN_VALUE, -1, true, farX, farZ, y);
         } catch (RuntimeException var12) {
         }

         return true;
      } else {
         return false;
      }
   }

   private void accumulate(
      String key,
      String feature,
      String step,
      String dimension,
      int sourceChunkX,
      int sourceChunkZ,
      int writeRadius,
      boolean bestEffort,
      int farChunkX,
      int farChunkZ,
      int y
   ) {
      if (this.enabled) {
         WorldgenOverreachReporter.Bucket b = this.active
            .computeIfAbsent(key, k -> new WorldgenOverreachReporter.Bucket(feature, step, dimension, sourceChunkX, sourceChunkZ, writeRadius, bestEffort));
         synchronized (b) {
            b.count++;
            if (farChunkX < b.minFarX) {
               b.minFarX = farChunkX;
            }

            if (farChunkX > b.maxFarX) {
               b.maxFarX = farChunkX;
            }

            if (farChunkZ < b.minFarZ) {
               b.minFarZ = farChunkZ;
            }

            if (farChunkZ > b.maxFarZ) {
               b.maxFarZ = farChunkZ;
            }

            if (y != Integer.MIN_VALUE) {
               if (y < b.minY) {
                  b.minY = y;
               }

               if (y > b.maxY) {
                  b.maxY = y;
               }
            }

            b.lastUpdate = System.currentTimeMillis();
         }
      }
   }

   public void tick(boolean taskRunning) {
      if (this.enabled) {
         if (taskRunning && !this.wasRunning) {
            this.log.info("[Chunksmith] worldgen overreach diagnostic active - watching this run for worldgen features writing outside their chunk.");
         }

         long now = System.currentTimeMillis();
         this.flushIdle(now, false);
         this.emitRollups(now);
         if (this.wasRunning && !taskRunning) {
            this.flushIdle(now, true);
            this.summarizeAndReset();
         }

         this.wasRunning = taskRunning;
      }
   }

   private void flushIdle(long now, boolean force) {
      Iterator<Entry<String, WorldgenOverreachReporter.Bucket>> it = this.active.entrySet().iterator();

      while (it.hasNext()) {
         WorldgenOverreachReporter.Bucket b = it.next().getValue();
         WorldgenOverreachReporter.Bucket snap;
         synchronized (b) {
            if (!force && now - b.lastUpdate < this.debounceMillis) {
               continue;
            }

            snap = b.copy();
         }

         it.remove();
         WorldgenOverreachReporter.FeatureStats fs = this.features.computeIfAbsent(b.feature, k -> new WorldgenOverreachReporter.FeatureStats());
         fs.totalChunks++;
         fs.totalBlocks = fs.totalBlocks + snap.count;
         if (!fs.firstEmitted) {
            fs.firstEmitted = true;
            fs.lastRollupBlocks = fs.totalBlocks;
            fs.lastRollupAt = now;
            this.log.warn(this.detailedLine(snap));
         }
      }
   }

   private void emitRollups(long now) {
      for (Entry<String, WorldgenOverreachReporter.FeatureStats> e : this.features.entrySet()) {
         WorldgenOverreachReporter.FeatureStats fs = e.getValue();
         if (fs.firstEmitted && now - fs.lastRollupAt >= this.rollupMillis && fs.totalBlocks > fs.lastRollupBlocks) {
            long delta = fs.totalBlocks - fs.lastRollupBlocks;
            this.log
               .warn(
                  String.format(
                     "[Chunksmith] overreach (cont.): %s - +%d more blocks refused; now %d chunks / %d blocks this run.",
                     e.getKey(),
                     delta,
                     fs.totalChunks,
                     fs.totalBlocks
                  )
               );
            fs.lastRollupBlocks = fs.totalBlocks;
            fs.lastRollupAt = now;
         }
      }
   }

   private void summarizeAndReset() {
      for (Entry<String, WorldgenOverreachReporter.FeatureStats> e : this.features.entrySet()) {
         WorldgenOverreachReporter.FeatureStats fs = e.getValue();
         this.log
            .warn(
               String.format(
                  "[Chunksmith] overreach summary: %s - clipped in %d chunks (%d blocks refused) this run. Likely a multi-chunk structure placed as a feature; report to the owning mod.",
                  e.getKey(),
                  fs.totalChunks,
                  fs.totalBlocks
               )
            );
      }

      this.features.clear();
      this.active.clear();
   }

   private String detailedLine(WorldgenOverreachReporter.Bucket b) {
      String yRange = b.minY == Integer.MAX_VALUE ? "Y ?" : String.format("Y %d..%d", b.minY, b.maxY);
      return b.bestEffort
         ? String.format(
            "[Chunksmith] worldgen overreach: %s (step %s) - %d blocks refused into far chunks [%d,%d]..[%d,%d] (%s); vanilla refused the writes at the chunk boundary. Likely a multi-chunk structure placed as a feature; report to the owning mod. (best-effort: source chunk/dimension/writeRadius unavailable on this platform)",
            b.feature,
            b.step,
            b.count,
            b.minFarX,
            b.minFarZ,
            b.maxFarX,
            b.maxFarZ,
            yRange
         )
         : String.format(
            "[Chunksmith] worldgen overreach: %s (step %s) @ chunk[%d,%d] %s - %d blocks refused into far chunks [%d,%d]..[%d,%d] (%s); vanilla writeRadius=%d, structure clipped at chunk boundary.",
            b.feature,
            b.step,
            b.sourceChunkX,
            b.sourceChunkZ,
            b.dimension,
            b.count,
            b.minFarX,
            b.minFarZ,
            b.maxFarX,
            b.maxFarZ,
            yRange,
            b.writeRadius
         );
   }

   private static final class Bucket {
      String feature;
      String step;
      String dimension;
      int sourceChunkX;
      int sourceChunkZ;
      int writeRadius;
      boolean bestEffort;
      long count;
      int minFarX = Integer.MAX_VALUE;
      int maxFarX = Integer.MIN_VALUE;
      int minFarZ = Integer.MAX_VALUE;
      int maxFarZ = Integer.MIN_VALUE;
      int minY = Integer.MAX_VALUE;
      int maxY = Integer.MIN_VALUE;
      long lastUpdate;

      Bucket(String feature, String step, String dimension, int sourceChunkX, int sourceChunkZ, int writeRadius, boolean bestEffort) {
         this.feature = feature;
         this.step = step;
         this.dimension = dimension;
         this.sourceChunkX = sourceChunkX;
         this.sourceChunkZ = sourceChunkZ;
         this.writeRadius = writeRadius;
         this.bestEffort = bestEffort;
      }

      WorldgenOverreachReporter.Bucket copy() {
         WorldgenOverreachReporter.Bucket c = new WorldgenOverreachReporter.Bucket(
            this.feature, this.step, this.dimension, this.sourceChunkX, this.sourceChunkZ, this.writeRadius, this.bestEffort
         );
         c.count = this.count;
         c.minFarX = this.minFarX;
         c.maxFarX = this.maxFarX;
         c.minFarZ = this.minFarZ;
         c.maxFarZ = this.maxFarZ;
         c.minY = this.minY;
         c.maxY = this.maxY;
         c.lastUpdate = this.lastUpdate;
         return c;
      }
   }

   private static final class FeatureStats {
      long totalChunks;
      long totalBlocks;
      boolean firstEmitted;
      long lastRollupBlocks;
      long lastRollupAt;
   }
}
