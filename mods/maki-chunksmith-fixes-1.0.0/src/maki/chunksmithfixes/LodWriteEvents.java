package maki.chunksmithfixes;

import com.kishku7.chunksmith.lod.CsLodChunk;

public final class LodWriteEvents {
   private static volatile LodWriteEvents.Observer observer;

   private LodWriteEvents() {
   }

   public static void observe(LodWriteEvents.Observer value) {
      observer = value;
   }

   public static void submitted(CsLodChunk record) {
      LodWriteEvents.Observer current = observer;
      if (current != null) {
         current.submitted(record);
      }
   }

   public static void completed(CsLodChunk record, long nanos, LodWriteEvents.Outcome outcome) {
      LodWriteEvents.Observer current = observer;
      if (current != null) {
         current.completed(record, nanos, outcome);
      }
   }

   public interface Observer {
      void submitted(CsLodChunk var1);

      void completed(CsLodChunk var1, long var2, LodWriteEvents.Outcome var4);
   }

   public enum Outcome {
      WRITTEN,
      SUPERSEDED,
      FAILED;

      // $VF: synthetic method
      private static LodWriteEvents.Outcome[] $values() {
         return new LodWriteEvents.Outcome[]{WRITTEN, SUPERSEDED, FAILED};
      }
   }
}
