package com.kishku7.chunksmith.lod.net;

public final class CsLodRegionHash {
   private CsLodRegionHash() {
   }

   public static long of(long lastModifiedMillis, long sizeBytes) {
      return mix(mix(lastModifiedMillis) ^ sizeBytes * -7046029254386353131L);
   }

   private static long mix(long value) {
      long z = value + -7046029254386353131L;
      z = (z ^ z >>> 30) * -4658895280553007687L;
      z = (z ^ z >>> 27) * -7723592293110705685L;
      return z ^ z >>> 31;
   }
}
