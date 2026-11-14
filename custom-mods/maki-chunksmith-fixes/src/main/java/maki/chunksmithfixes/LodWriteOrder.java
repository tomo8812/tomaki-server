package maki.chunksmithfixes;

import com.kishku7.chunksmith.lod.CsLodChunk;
import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/** Orders native async and queue-full synchronous writes for each store. */
public final class LodWriteOrder {
    public record Result(int bytes, boolean written) {}
    @FunctionalInterface public interface Write { int run() throws IOException; }
    private static final class Slot { long next, saved; int pending; }
    private record Ticket(long key, Slot slot, long sequence) {}
    private final Map<Long, Slot> slots = new HashMap<>();
    private final IdentityHashMap<CsLodChunk, Ticket> pending = new IdentityHashMap<>();
    private final Object writer = new Object();

    public synchronized void submitted(CsLodChunk record) {
        long key = ((long)record.getChunkX() << 32) | (record.getChunkZ() & 0xffffffffL);
        if (pending.containsKey(record)) throw new IllegalStateException("LOD record submitted twice");
        Slot slot = slots.computeIfAbsent(key, ignored -> new Slot());
        slot.pending++;
        pending.put(record, new Ticket(key, slot, ++slot.next));
    }

    public Result write(CsLodChunk record, Write write) throws IOException {
        synchronized (writer) {
            Ticket ticket;
            synchronized (this) {
                ticket = pending.get(record);
                if (ticket == null) throw new IllegalStateException("Unregistered native LOD write");
            }
            try {
                // A newer synchronous fallback may already have committed while
                // this older record was waiting in the native async queue.
                if (ticket.sequence() < ticket.slot().saved) return new Result(0, false);
                int bytes = write.run();
                ticket.slot().saved = ticket.sequence();
                return new Result(bytes, true);
            } finally {
                synchronized (this) {
                    pending.remove(record);
                    if (--ticket.slot().pending == 0) slots.remove(ticket.key(), ticket.slot());
                }
            }
        }
    }
}
