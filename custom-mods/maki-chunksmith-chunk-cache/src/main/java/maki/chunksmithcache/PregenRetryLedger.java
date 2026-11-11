package maki.chunksmithcache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.concurrent.TimeUnit;

/**
 * Bounded retry debt for the unified pregen iterator. Overflow is sticky and
 * forces a low-frequency whole-selection rescan because an omitted coordinate
 * can no longer be proven complete from the ledger alone.
 */
final class PregenRetryLedger<K> {
    static final int LIMIT=4096;
    private static final long BASE_DELAY_NANOS=TimeUnit.SECONDS.toNanos(5);
    private static final long MAX_DELAY_NANOS=TimeUnit.MINUTES.toNanos(5);
    private static final long OVERFLOW_RESCAN_NANOS=TimeUnit.MINUTES.toNanos(1);

    record Entry(int attempts,long retryAtNanos,String reason) {}
    record Status(int entries,boolean ledgerComplete,boolean overflowDirty,long nextDueMillis) {}

    private final String name;
    private final LinkedHashMap<K,Entry> entries=new LinkedHashMap<>();
    private boolean overflowDirty;
    private long nextOverflowRescanNanos;

    PregenRetryLedger(String name){this.name=name;}

    synchronized boolean defer(K key,long nowNanos,String reason) {
        if(key==null)return false;
        Entry previous=entries.get(key);
        if(previous==null&&entries.size()>=LIMIT) {
            overflowDirty=true;
            if(nextOverflowRescanNanos==0L||nowNanos>=nextOverflowRescanNanos)
                nextOverflowRescanNanos=nowNanos;
            return false;
        }
        int attempts=previous==null?1:Math.min(16,previous.attempts()+1);
        long multiplier=1L<<Math.min(6,attempts-1);
        long delay=Math.min(MAX_DELAY_NANOS,BASE_DELAY_NANOS*multiplier);
        long retryAt=nowNanos>Long.MAX_VALUE-delay?Long.MAX_VALUE:nowNanos+delay;
        entries.put(key,new Entry(attempts,retryAt,reason==null?name:reason));
        return true;
    }

    synchronized boolean contains(K key){return entries.containsKey(key);}

    synchronized boolean due(K key,long nowNanos) {
        Entry entry=entries.get(key);
        return entry!=null&&nowNanos>=entry.retryAtNanos();
    }

    synchronized void complete(K key){entries.remove(key);}

    synchronized List<K> keys(){return new ArrayList<>(entries.keySet());}

    synchronized void removeIf(Predicate<K> predicate) {
        entries.keySet().removeIf(predicate);
    }

    synchronized boolean overflowRescanDue(long nowNanos) {
        if(!overflowDirty||nowNanos<nextOverflowRescanNanos)return false;
        nextOverflowRescanNanos=nowNanos>Long.MAX_VALUE-OVERFLOW_RESCAN_NANOS
            ?Long.MAX_VALUE:nowNanos+OVERFLOW_RESCAN_NANOS;
        return true;
    }

    synchronized Status status(long nowNanos) {
        long next=Long.MAX_VALUE;
        for(Entry entry:entries.values())next=Math.min(next,entry.retryAtNanos());
        if(overflowDirty)next=Math.min(next,nextOverflowRescanNanos);
        long millis=next==Long.MAX_VALUE?-1L:TimeUnit.NANOSECONDS.toMillis(Math.max(0L,next-nowNanos));
        return new Status(entries.size(),!overflowDirty,overflowDirty,millis);
    }

    synchronized void clear() {
        entries.clear();
        overflowDirty=false;
        nextOverflowRescanNanos=0L;
    }
}
