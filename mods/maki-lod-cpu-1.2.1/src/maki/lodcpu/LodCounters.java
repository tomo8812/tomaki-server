package maki.lodcpu;

import com.kishku7.chunksmith.lod.CompositeLodSink;
import com.kishku7.chunksmith.lod.CsLodStoreSink;
import com.kishku7.chunksmith.lod.LodSink;
import com.kishku7.chunksmith.lod.LodSinks;

public record LodCounters(LodSink identity, boolean available, long written, long bytes, long synchronousWrites, int queue) {
   public static LodCounters read() {
      LodSink var0 = LodSinks.get();
      long[] var1 = new long[]{0L, 0L, 0L, 0L};
      collect(var0, var1);
      return new LodCounters(var0, var1[3] > 0L, var1[0], var1[1], var1[2], var0.queueDepth());
   }

   private static void collect(LodSink var0, long[] var1) {
      if (var0 instanceof CsLodStoreSink var2) {
         var1[0] += var2.getWrittenCount();
         var1[1] += var2.getWrittenBytes();
         var1[2] += var2.getSynchronousWrites();
         var1[3]++;
      } else if (var0 instanceof CompositeLodSink var3) {
         for (LodSink var5 : var3.getSinks()) {
            collect(var5, var1);
         }
      }
   }
}
