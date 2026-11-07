package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Bounded persistent per-coordinate retry debt; a bad target never pins the discovery cursor. */
final class BackfillRetries {
    record Key(String dimension, long pos) {}
    record Entry(int attempts, long retryAt, String reason) {}
    private final Map<Key, Entry> entries = new LinkedHashMap<>();
    private boolean loaded;
    synchronized void defer(String dimension, long pos, long now, String reason) {
        Key key = new Key(dimension,pos);
        Entry previous = entries.get(key);
        if (previous == null && entries.size() >= 4096) return; // cursor wrap remains the bounded-memory fallback
        int attempts = previous == null ? 1 : Math.min(16,previous.attempts+1);
        long delay = Math.min(300_000L, 5_000L << Math.min(6,attempts-1));
        entries.put(key,new Entry(attempts,now+delay,reason));
    }
    synchronized boolean waiting(String dimension,long pos,long now) {
        Entry entry=entries.get(new Key(dimension,pos));
        return entry != null && entry.retryAt > now;
    }
    synchronized List<Key> due(String dimension,long now) {
        return entries.entrySet().stream().filter(e -> e.getKey().dimension.equals(dimension) && e.getValue().retryAt<=now)
            .sorted(Comparator.comparingLong(e -> e.getValue().retryAt)).limit(4).map(Map.Entry::getKey).toList();
    }
    synchronized void complete(String dimension,long pos) { entries.remove(new Key(dimension,pos)); }
    synchronized void load(Path path) {
        if (loaded) return;
        loaded=true;
        Properties source=new Properties();
        try (var input=Files.newInputStream(path)) { source.load(input); }
        catch (IOException ignored) { return; }
        for (String key:source.stringPropertyNames()) {
            if (entries.size()>=4096) break;
            try {
                int slash=key.lastIndexOf('/');
                String[] value=source.getProperty(key).split(",",3);
                entries.put(new Key(key.substring(0,slash),Long.parseLong(key.substring(slash+1))),
                    new Entry(Math.max(1,Math.min(16,Integer.parseInt(value[0]))),
                        Math.min(System.currentTimeMillis()+300_000L,Long.parseLong(value[1])),value[2]));
            } catch (RuntimeException malformed) { /* still discoverable through cursor wrap */ }
        }
    }
    synchronized void save(Path path) throws IOException {
        Properties output=new Properties();
        entries.forEach((key,value) -> output.setProperty(key.dimension+"/"+key.pos,value.attempts+","+value.retryAt+","+value.reason));
        Files.createDirectories(path.getParent());
        Path temporary=path.resolveSibling(path.getFileName()+".tmp");
        try (var stream=Files.newOutputStream(temporary)) { output.store(stream,"Backfill retries"); }
        try { Files.move(temporary,path,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
        catch (AtomicMoveNotSupportedException unsupported) { Files.move(temporary,path,StandardCopyOption.REPLACE_EXISTING); }
    }
}
