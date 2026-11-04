package com.kishku7.chunksmith.lod;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public final class CsLodPresenceIndex {
   private static final int REGION_CHUNKS = 32;
   private static final int SLOTS = 1024;
   private static final int SLOT_BYTES = 8;
   private static final int HEADER_BYTES = 8192;
   private static final int BITMAP_LONGS = 16;
   private static final int MAX_CACHED_REGIONS = 256;
   private final Path root;
   private final CsLodPresenceIndex.RegionLru bitmaps = new CsLodPresenceIndex.RegionLru(256);
   private final AtomicLong regionsLoaded = new AtomicLong();
   private final AtomicLong headerBytesRead = new AtomicLong();
   private final AtomicLong loadNanos = new AtomicLong();
   private final AtomicLong queries = new AtomicLong();

   public CsLodPresenceIndex(Path root) {
      this.root = root;
   }

   public Path getRoot() {
      return this.root;
   }

   public synchronized boolean hasLod(int chunkX, int chunkZ) {
      this.queries.incrementAndGet();
      long[] bitmap = this.bitmapFor(regionX(chunkX), regionZ(chunkZ));
      int slot = slotIndex(chunkX, chunkZ);
      return (bitmap[slot >>> 6] & 1L << (slot & 63)) != 0L;
   }

   public synchronized void markLod(int chunkX, int chunkZ) {
      long[] bitmap = this.bitmapFor(regionX(chunkX), regionZ(chunkZ));
      int slot = slotIndex(chunkX, chunkZ);
      bitmap[slot >>> 6] = bitmap[slot >>> 6] | 1L << (slot & 63);
   }

   public synchronized int countInRegion(int regionX, int regionZ) {
      int count = 0;

      for (long word : this.bitmapFor(regionX, regionZ)) {
         count += Long.bitCount(word);
      }

      return count;
   }

   public synchronized void invalidate() {
      this.bitmaps.clear();
   }

   public long getRegionsLoaded() {
      return this.regionsLoaded.get();
   }

   public long getHeaderBytesRead() {
      return this.headerBytesRead.get();
   }

   public long getLoadNanos() {
      return this.loadNanos.get();
   }

   public long getQueries() {
      return this.queries.get();
   }

   public CsLodPresenceIndex.Cost cost() {
      return new CsLodPresenceIndex.Cost(this.queries.get(), this.regionsLoaded.get(), this.headerBytesRead.get(), this.loadNanos.get());
   }

   public String describeCostSince(CsLodPresenceIndex.Cost before) {
      long asked = this.queries.get() - before.queries;
      long loaded = this.regionsLoaded.get() - before.regionsLoaded;
      long bytes = this.headerBytesRead.get() - before.headerBytesRead;
      double millis = (this.loadNanos.get() - before.loadNanos) / 1000000.0;
      return String.format(
         "lod presence check: %d chunks queried, %d region headers read (%d KB), %.1f ms total (%.4f ms per 1k chunks)",
         asked,
         loaded,
         bytes / 1024L,
         millis,
         asked == 0L ? 0.0 : millis * 1000.0 / asked
      );
   }

   public static long countRecords(Path root) throws IOException {
      if (!Files.isDirectory(root)) {
         return 0L;
      }

      long total = 0L;

      try (Stream<Path> walk = Files.walk(root)) {
         for (Path region : walk.filter(path -> path.getFileName().toString().endsWith(".cslod")).toList()) {
            total += countIn(region);
         }
      }

      return total;
   }

   private static long countIn(Path region) throws IOException {
      byte[] header = new byte[8192];
      int read = 0;

      try (RandomAccessFile file = new RandomAccessFile(region.toFile(), "r")) {
         int available = (int)Math.min(8192L, file.length());
         if (available > 0) {
            file.readFully(header, 0, available);
            read = available;
         }
      }

      long var10 = 0L;
      int slots = read / 8;

      for (int slot = 0; slot < slots; slot++) {
         int base = slot * 8;
         if (readInt(header, base) > 0 && readInt(header, base + 4) > 0) {
            var10++;
         }
      }

      return var10;
   }

   private long[] bitmapFor(int regionX, int regionZ) {
      long key = regionKey(regionX, regionZ);
      long[] bitmap = this.bitmaps.get(key);
      if (bitmap == null) {
         bitmap = this.readHeader(regionX, regionZ);
         this.bitmaps.put(key, bitmap);
      }

      return bitmap;
   }

   private long[] readHeader(int regionX, int regionZ) {
      long[] bitmap = new long[16];
      Path path = this.root.resolve("r." + regionX + "." + regionZ + ".cslod");
      if (!Files.isRegularFile(path)) {
         this.regionsLoaded.incrementAndGet();
         return bitmap;
      }

      long start = System.nanoTime();
      byte[] header = new byte[8192];
      int read = 0;

      try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "r")) {
         int available = (int)Math.min(8192L, file.length());
         if (available > 0) {
            file.readFully(header, 0, available);
            read = available;
         }
      } catch (IOException e) {
         return bitmap;
      } finally {
         this.loadNanos.addAndGet(System.nanoTime() - start);
         this.regionsLoaded.incrementAndGet();
      }

      this.headerBytesRead.addAndGet(read);
      int var22 = read / 8;

      for (int var23 = 0; var23 < var22; var23++) {
         int base = var23 * 8;
         int offset = readInt(header, base);
         int length = readInt(header, base + 4);
         if (offset > 0 && length > 0) {
            bitmap[var23 >>> 6] = bitmap[var23 >>> 6] | 1L << (var23 & 63);
         }
      }

      return bitmap;
   }

   private static int readInt(byte[] buffer, int index) {
      return (buffer[index] & 0xFF) << 24 | (buffer[index + 1] & 0xFF) << 16 | (buffer[index + 2] & 0xFF) << 8 | buffer[index + 3] & 0xFF;
   }

   private static int regionX(int chunkX) {
      return Math.floorDiv(chunkX, 32);
   }

   private static int regionZ(int chunkZ) {
      return Math.floorDiv(chunkZ, 32);
   }

   private static long regionKey(int regionX, int regionZ) {
      return (long)regionX << 32 ^ regionZ & 4294967295L;
   }

   private static int slotIndex(int chunkX, int chunkZ) {
      int localX = Math.floorMod(chunkX, 32);
      int localZ = Math.floorMod(chunkZ, 32);
      return localZ * 32 + localX;
   }

   public static final class Cost {
      private final long queries;
      private final long regionsLoaded;
      private final long headerBytesRead;
      private final long loadNanos;

      private Cost(long queries, long regionsLoaded, long headerBytesRead, long loadNanos) {
         this.queries = queries;
         this.regionsLoaded = regionsLoaded;
         this.headerBytesRead = headerBytesRead;
         this.loadNanos = loadNanos;
      }
   }

   private static final class RegionLru extends LinkedHashMap<Long, long[]> {
      private static final long serialVersionUID = 1L;
      private final int max;

      RegionLru(int max) {
         super(64, 0.75F, true);
         this.max = max;
      }

      @Override
      protected boolean removeEldestEntry(Entry<Long, long[]> eldest) {
         return this.size() > this.max;
      }
   }
}
