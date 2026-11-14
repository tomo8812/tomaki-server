package maki.chunksmithfixes;

import com.kishku7.chunksmith.lod.CsLodChunk;

/** Read-only notifications for the resource controller; no scheduling decisions here. */
public final class LodWriteEvents {
    public enum Outcome { WRITTEN, SUPERSEDED, FAILED }
    public interface Observer {
        void submitted(CsLodChunk record);
        void completed(CsLodChunk record, long serviceNanos, Outcome outcome);
    }
    private static volatile Observer observer;
    private LodWriteEvents() {}
    public static void observe(Observer value) { observer = value; }
    public static void submitted(CsLodChunk record) {
        Observer current = observer;
        if (current != null) current.submitted(record);
    }
    public static void completed(CsLodChunk record, long nanos, Outcome outcome) {
        Observer current = observer;
        if (current != null) current.completed(record, nanos, outcome);
    }
}
