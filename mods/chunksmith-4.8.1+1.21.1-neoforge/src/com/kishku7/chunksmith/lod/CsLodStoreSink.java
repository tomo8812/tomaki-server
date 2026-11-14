package com.kishku7.chunksmith.lod;

import com.kishku7.chunksmith.lod.net.CsLodGrowth;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CsLodStoreSink implements LodSink {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private final CsLodRegionStore store;
   private final BlockingQueue<CsLodChunk> queue;
   private final Thread writer;
   private final AtomicLong written = new AtomicLong();
   private final AtomicLong bytes = new AtomicLong();
   private final AtomicLong synchronousWrites = new AtomicLong();
   private volatile boolean running = true;

   public CsLodStoreSink(Path root, int capacity) {
      this.store = new CsLodRegionStore(root);
      this.queue = new ArrayBlockingQueue<>(Math.max(16, capacity));
      this.writer = new Thread(this::drain, "chunksmith-lod-writer");
      this.writer.setDaemon(true);
      this.writer.start();
   }

   @Override
   public boolean offer(Object chunk) {
      if (chunk instanceof CsLodChunk record) {
         if (!this.queue.offer(record)) {
            this.synchronousWrites.incrementAndGet();
            this.persist(record);
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public int queueDepth() {
      return this.queue.size();
   }

   public long getWrittenCount() {
      return this.written.get();
   }

   public long getWrittenBytes() {
      return this.bytes.get();
   }

   public long getSynchronousWrites() {
      return this.synchronousWrites.get();
   }

   public void shutdown() {
      this.running = false;
      this.writer.interrupt();

      try {
         this.writer.join(10000L);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }

      CsLodChunk remaining;
      while ((remaining = this.queue.poll()) != null) {
         this.persist(remaining);
      }

      try {
         this.store.close();
      } catch (IOException e) {
         LOGGER.warn("Chunksmith: failed to close the LOD store: " + e);
      }
   }

   private void drain() {
      while (this.running || !this.queue.isEmpty()) {
         try {
            CsLodChunk record = this.queue.take();
            this.persist(record);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
         }
      }
   }

   private void persist(CsLodChunk record) {
      try {
         int size = this.store.write(record);
         CsLodGrowth.noteWrite(System.currentTimeMillis());
         this.written.incrementAndGet();
         this.bytes.addAndGet(size);
      } catch (IOException e) {
         LOGGER.warn(String.format("Chunksmith: failed to write LOD for chunk %d,%d: %s", record.getChunkX(), record.getChunkZ(), e));
      }
   }

   @Override
   public String toString() {
      return "CsLodStoreSink";
   }
}
