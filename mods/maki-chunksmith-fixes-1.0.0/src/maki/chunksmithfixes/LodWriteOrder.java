package maki.chunksmithfixes;

import com.kishku7.chunksmith.lod.CsLodChunk;
import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class LodWriteOrder {
   private final Map<Long, LodWriteOrder.Slot> slots = new HashMap<>();
   private final IdentityHashMap<CsLodChunk, LodWriteOrder.Ticket> pending = new IdentityHashMap<>();
   private final Object writer = new Object();

   public synchronized void submitted(CsLodChunk record) {
      long key = (long)record.getChunkX() << 32 | record.getChunkZ() & 4294967295L;
      if (this.pending.containsKey(record)) {
         throw new IllegalStateException("LOD record submitted twice");
      }

      LodWriteOrder.Slot slot = this.slots.computeIfAbsent(key, ignored -> new LodWriteOrder.Slot());
      slot.pending++;
      this.pending.put(record, new LodWriteOrder.Ticket(key, slot, ++slot.next));
   }

   public LodWriteOrder.Result write(CsLodChunk record, LodWriteOrder.Write write) throws IOException {
      synchronized (this.writer) {
         LodWriteOrder.Ticket ticket;
         synchronized (this) {
            ticket = this.pending.get(record);
            if (ticket == null) {
               throw new IllegalStateException("Unregistered native LOD write");
            }
         }

         LodWriteOrder.Result bytes;
         try {
            if (ticket.sequence() >= ticket.slot().saved) {
               int bytesx = write.run();
               ticket.slot().saved = ticket.sequence();
               return new LodWriteOrder.Result(bytesx, true);
            }

            bytes = new LodWriteOrder.Result(0, false);
         } finally {
            synchronized (this) {
               this.pending.remove(record);
               if (--ticket.slot().pending == 0) {
                  this.slots.remove(ticket.key(), ticket.slot());
               }
            }
         }

         return bytes;
      }
   }

   public record Result(int bytes, boolean written) {
   }

   private static final class Slot {
      long next;
      long saved;
      int pending;
   }

   private record Ticket(long key, LodWriteOrder.Slot slot, long sequence) {
   }

   @FunctionalInterface
   public interface Write {
      int run() throws IOException;
   }
}
