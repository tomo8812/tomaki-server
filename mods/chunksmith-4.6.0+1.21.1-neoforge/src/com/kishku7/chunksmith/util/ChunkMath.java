package com.kishku7.chunksmith.util;

public final class ChunkMath {
   private ChunkMath() {
   }

   public static long pack(int x, int z) {
      long lx = x & 4294967295L;
      long lz = z & 4294967295L;
      return lx << 32 | lz;
   }

   public static int regionIndex(int x, int z) {
      return (x & 31) * 32 + (z & 31);
   }
}
