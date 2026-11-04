package com.kishku7.chunksmith.lod.net;

public final class CsLodSummary {
   private CsLodSummary() {
   }

   public static long fold(long aggregate, int regionX, int regionZ, long hash) {
      return aggregate ^ token(regionX, regionZ, hash);
   }

   public static long token(int regionX, int regionZ, long hash) {
      long packed = (long)regionX << 32 ^ regionZ & 4294967295L;
      return mix(mix(packed) ^ hash * -7046029254386353131L);
   }

   private static long mix(long value) {
      long z = value + -7046029254386353131L;
      z = (z ^ z >>> 30) * -4658895280553007687L;
      z = (z ^ z >>> 27) * -7723592293110705685L;
      return z ^ z >>> 31;
   }

   public record Snapshot(int count, long aggregate) {
      public static final CsLodSummary.Snapshot EMPTY = new CsLodSummary.Snapshot(0, 0L);
   }
}
