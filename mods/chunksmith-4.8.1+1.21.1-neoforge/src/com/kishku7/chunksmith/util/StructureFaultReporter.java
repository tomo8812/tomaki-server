package com.kishku7.chunksmith.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StructureFaultReporter {
   private static final StructureFaultReporter INSTANCE = new StructureFaultReporter();
   public static final String BLOCK_ATTACHED_MARKER = "Block-attached entity at invalid position";
   private static final String TYPE_MISSING = "block-attached entity: missing anchor (no block_pos / legacy TileX format)";
   private static final String TYPE_FAR = "block-attached entity: anchor more than 16 blocks from the entity";
   private final Logger log = LoggerFactory.getLogger("Chunksmith");
   private volatile boolean enabled = true;
   private volatile long writeIntervalMillis = 600000L;
   private volatile int maxSampleChunks = 10;
   private volatile Path reportFile;
   private volatile long lastWriteAt = 0L;
   private volatile long faultsAtLastWrite = -1L;
   private volatile boolean noticeLogged = false;
   private final Map<String, StructureFaultReporter.Culprit> culprits = new ConcurrentHashMap<>();
   private final AtomicLong totalFaults = new AtomicLong();
   private final ThreadLocal<ArrayDeque<StructureFaultReporter.Ctx>> contextStack = ThreadLocal.withInitial(ArrayDeque::new);

   public static StructureFaultReporter get() {
      return INSTANCE;
   }

   private StructureFaultReporter() {
   }

   public void setReportFile(Path file) {
      this.reportFile = file;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void configure(long writeIntervalMillis, int maxSampleChunks) {
      this.writeIntervalMillis = Math.max(5000L, writeIntervalMillis);
      this.maxSampleChunks = Math.max(1, maxSampleChunks);
   }

   public void pushContext(String structureId, int chunkX, int chunkZ) {
      if (this.enabled) {
         this.contextStack.get().push(new StructureFaultReporter.Ctx(structureId, chunkX, chunkZ));
      }
   }

   public void popContext() {
      if (this.enabled) {
         ArrayDeque<StructureFaultReporter.Ctx> stack = this.contextStack.get();
         if (!stack.isEmpty()) {
            stack.pop();
         }
      }
   }

   public void recordBlockAttached(boolean missingAnchor) {
      if (this.enabled) {
         StructureFaultReporter.Ctx ctx = this.contextStack.get().peek();
         String structure = ctx == null ? null : ctx.structure;
         this.record(
            namespaceOf(structure),
            missingAnchor
               ? "block-attached entity: missing anchor (no block_pos / legacy TileX format)"
               : "block-attached entity: anchor more than 16 blocks from the entity",
            structure,
            ctx
         );
      }
   }

   public boolean recordBlockAttachedBestEffort(String message) {
      if (message != null && message.contains("Block-attached entity at invalid position")) {
         if (this.enabled) {
            boolean missing = message.contains("null");
            this.record(
               "<unknown> (no mixin on this platform)",
               missing
                  ? "block-attached entity: missing anchor (no block_pos / legacy TileX format)"
                  : "block-attached entity: anchor more than 16 blocks from the entity",
               null,
               null
            );
         }

         return true;
      } else {
         return false;
      }
   }

   private void record(String namespace, String type, String structure, StructureFaultReporter.Ctx ctx) {
      this.culprits.computeIfAbsent(namespace, k -> new StructureFaultReporter.Culprit()).add(type, structure, ctx, this.maxSampleChunks);
      this.totalFaults.incrementAndGet();
   }

   public void tick(boolean taskRunning) {
      if (this.enabled && this.reportFile != null) {
         long total = this.totalFaults.get();
         if (total != 0L) {
            if (!this.noticeLogged) {
               this.noticeLogged = true;
               this.log.info("[Chunksmith] worldgen fault diagnostic active - suppressing vanilla fault spam; writing a report to {}", this.reportFile);
            }

            long now = System.currentTimeMillis();
            if (now - this.lastWriteAt >= this.writeIntervalMillis && total != this.faultsAtLastWrite) {
               this.writeReport(taskRunning);
               this.lastWriteAt = now;
               this.faultsAtLastWrite = total;
            }
         }
      }
   }

   public void flush(boolean taskRunning) {
      if (this.enabled && this.reportFile != null && this.totalFaults.get() > 0L) {
         this.writeReport(taskRunning);
      }
   }

   private void writeReport(boolean taskRunning) {
      try {
         StringBuilder sb = new StringBuilder(512);
         sb.append("Chunksmith - Worldgen Fault Report\n");
         sb.append("Generated: ").append(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append('\n');
         sb.append("Chunksmith generation task active: ").append(taskRunning ? "yes" : "no").append('\n');
         sb.append("Total faults this session: ").append(this.totalFaults.get()).append('\n');
         sb.append("\nThese are vanilla worldgen errors Chunksmith caught and kept out of the server log.\n");
         sb.append("Each is attributed to the structure/datapack being generated - report it to that mod's author.\n");
         sb.append("====================================================================\n\n");
         List<Entry<String, StructureFaultReporter.Culprit>> sorted = new ArrayList<>(this.culprits.entrySet());
         sorted.sort((a, b) -> Long.compare(b.getValue().total, a.getValue().total));

         for (Entry<String, StructureFaultReporter.Culprit> entry : sorted) {
            StructureFaultReporter.Culprit c = entry.getValue();
            sb.append("mod/datapack: ").append(entry.getKey()).append('\n');
            sb.append("  total faults: ").append(c.total).append('\n');
            sb.append("  error types:\n");
            c.typeCounts
               .entrySet()
               .stream()
               .sorted((x, y) -> Long.compare(y.getValue().get(), x.getValue().get()))
               .forEach(t -> sb.append("    - ").append(t.getKey()).append(" x").append(t.getValue().get()).append('\n'));
            if (!c.structures.isEmpty()) {
               sb.append("  structures involved:\n");
               c.structures
                  .entrySet()
                  .stream()
                  .sorted((x, y) -> Long.compare(y.getValue().get(), x.getValue().get()))
                  .limit(20L)
                  .forEach(s -> sb.append("    - ").append(s.getKey()).append(" x").append(s.getValue().get()).append('\n'));
            }

            if (!c.sampleChunks.isEmpty()) {
               sb.append("  sample broken chunks (up to ").append(this.maxSampleChunks).append("): ").append(String.join(" ", c.sampleChunks)).append('\n');
            }

            sb.append('\n');
         }

         if (this.reportFile.getParent() != null) {
            Files.createDirectories(this.reportFile.getParent());
         }

         byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
         Path tmp = this.reportFile.resolveSibling(this.reportFile.getFileName().toString() + ".tmp");

         try {
            Files.write(tmp, bytes);
            Files.move(tmp, this.reportFile, StandardCopyOption.REPLACE_EXISTING);
         } catch (IOException atomicFail) {
            Files.write(this.reportFile, bytes);
         }
      } catch (Throwable var8) {
      }
   }

   private static String namespaceOf(String structureId) {
      if (structureId != null && !structureId.isEmpty()) {
         int colon = structureId.indexOf(58);
         return colon > 0 ? structureId.substring(0, colon) : structureId;
      } else {
         return "<unknown> (no structure context)";
      }
   }

   private static final class Ctx {
      String structure;
      int cx;
      int cz;

      Ctx(String structure, int cx, int cz) {
         this.structure = structure;
         this.cx = cx;
         this.cz = cz;
      }
   }

   private static final class Culprit {
      volatile long total;
      Map<String, AtomicLong> typeCounts = new ConcurrentHashMap<>();
      Map<String, AtomicLong> structures = new ConcurrentHashMap<>();
      Set<String> sampleChunks = ConcurrentHashMap.newKeySet();

      synchronized void add(String type, String structure, StructureFaultReporter.Ctx ctx, int maxSamples) {
         this.total++;
         this.typeCounts.computeIfAbsent(type, k -> new AtomicLong()).incrementAndGet();
         if (structure != null) {
            this.structures.computeIfAbsent(structure, k -> new AtomicLong()).incrementAndGet();
         }

         if (ctx != null && this.sampleChunks.size() < maxSamples) {
            this.sampleChunks.add("[" + ctx.cx + "," + ctx.cz + "]");
         }
      }
   }
}
