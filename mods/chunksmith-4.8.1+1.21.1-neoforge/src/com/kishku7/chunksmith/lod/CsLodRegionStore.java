package com.kishku7.chunksmith.lod;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class CsLodRegionStore {
   public static final int REGION_CHUNKS = 32;
   private static final int SLOTS = 1024;
   private static final int SLOT_BYTES = 8;
   private static final int HEADER_BYTES = 8192;
   private final Path root;
   private final Map<Long, RandomAccessFile> open = new HashMap<>();

   public CsLodRegionStore(Path root) {
      this.root = root;
   }

   public synchronized int write(CsLodChunk chunk) throws IOException {
      byte[] payload = CsLodCodec.encode(chunk);
      int rx = Math.floorDiv(chunk.getChunkX(), 32);
      int rz = Math.floorDiv(chunk.getChunkZ(), 32);
      RandomAccessFile file = this.region(rx, rz);
      int slot = slotIndex(chunk.getChunkX(), chunk.getChunkZ());
      long offset = file.length();
      file.seek(offset);
      file.write(payload);
      file.seek(slot * 8L);
      file.writeInt((int)offset);
      file.writeInt(payload.length);
      return payload.length;
   }

   public synchronized CsLodChunk read(int chunkX, int chunkZ) throws IOException {
      int rx = Math.floorDiv(chunkX, 32);
      int rz = Math.floorDiv(chunkZ, 32);
      Path path = this.regionPath(rx, rz);
      if (!Files.exists(path)) {
         return null;
      }

      RandomAccessFile file = this.region(rx, rz);
      int slot = slotIndex(chunkX, chunkZ);
      file.seek(slot * 8L);
      int offset = file.readInt();
      int length = file.readInt();
      if (offset <= 0 || length <= 0) {
         return null;
      }

      if (length > 33554432) {
         throw new IOException("CSLOD region " + path.getFileName() + " slot " + slot + ": record length " + length + " exceeds 33554432");
      }

      byte[] payload = new byte[length];
      file.seek(offset);
      file.readFully(payload);
      return CsLodCodec.decode(payload);
   }

   public static int forEachChunk(Path root, CsLodRegionStore.ChunkVisitor visitor) throws IOException {
      if (!Files.isDirectory(root)) {
         return 0;
      }

      int visited = 0;

      try (Stream<Path> walk = Files.walk(root)) {
         for (Path region : walk.filter(path -> path.getFileName().toString().endsWith(".cslod")).sorted().toList()) {
            visited += forEachChunkIn(region, visitor);
         }
      }

      return visited;
   }

   public static int forEachChunkInRegion(Path root, int regionX, int regionZ, CsLodRegionStore.ChunkVisitor visitor) throws IOException {
      Path region = root.resolve("r." + regionX + "." + regionZ + ".cslod");
      return !Files.isRegularFile(region) ? 0 : forEachChunkIn(region, visitor);
   }

   private static int forEachChunkIn(Path region, CsLodRegionStore.ChunkVisitor visitor) throws IOException {
      int visited = 0;

      try (RandomAccessFile file = new RandomAccessFile(region.toFile(), "r")) {
         for (int slot = 0; slot < 1024; slot++) {
            file.seek(slot * 8L);
            int offset = file.readInt();
            int length = file.readInt();
            if (offset > 0 && length > 0) {
               if (length > 33554432) {
                  throw new IOException("CSLOD region " + region.getFileName() + " slot " + slot + ": record length " + length + " exceeds 33554432");
               }

               byte[] payload = new byte[length];
               file.seek(offset);
               file.readFully(payload);
               visitor.visit(CsLodCodec.decode(payload));
               visited++;
            }
         }
      }

      return visited;
   }

   public synchronized void close() throws IOException {
      IOException first = null;

      for (RandomAccessFile file : this.open.values()) {
         try {
            file.close();
         } catch (IOException e) {
            if (first == null) {
               first = e;
            }
         }
      }

      this.open.clear();
      if (first != null) {
         throw first;
      }
   }

   private RandomAccessFile region(int rx, int rz) throws IOException {
      long key = (long)rx << 32 ^ rz & 4294967295L;
      RandomAccessFile file = this.open.get(key);
      if (file != null) {
         return file;
      }

      Path path = this.regionPath(rx, rz);
      Files.createDirectories(path.getParent());
      file = new RandomAccessFile(path.toFile(), "rw");
      if (file.length() < 8192L) {
         file.setLength(8192L);
      }

      this.open.put(key, file);
      return file;
   }

   private Path regionPath(int rx, int rz) {
      return this.root.resolve("r." + rx + "." + rz + ".cslod");
   }

   private static int slotIndex(int chunkX, int chunkZ) {
      int localX = Math.floorMod(chunkX, 32);
      int localZ = Math.floorMod(chunkZ, 32);
      return localZ * 32 + localX;
   }

   @FunctionalInterface
   public interface ChunkVisitor {
      void visit(CsLodChunk var1) throws IOException;
   }
}
