package maki.lodcpu;

import com.kishku7.chunksmith.lod.CompositeLodSink;
import com.kishku7.chunksmith.lod.CsLodStoreSink;
import com.kishku7.chunksmith.lod.LodSink;
import com.kishku7.chunksmith.lod.LodSinks;

public record LodCounters(LodSink identity, boolean available, long written, long bytes, long synchronousWrites, int queue) {
   public static LodCounters read() {
      LodSink root = LodSinks.get();
      long[] totals = new long[]{0L, 0L, 0L, 0L};
      collect(root, totals);
      return new LodCounters(root, totals[3] > 0L, totals[0], totals[1], totals[2], root.queueDepth());
   }

   private static void collect(LodSink sink, long[] totals) {
      if (sink instanceof CsLodStoreSink store) {
         totals[0] += store.getWrittenCount();
         totals[1] += store.getWrittenBytes();
         totals[2] += store.getSynchronousWrites();
         totals[3]++;
      } else if (sink instanceof CompositeLodSink composite) {
         for (LodSink part : composite.getSinks()) {
            collect(part, totals);
         }
      }
   }
}
