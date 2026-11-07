package maki.chunksmithcache;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Bounded persistent per-coordinate retry debt; a bad target never pins the discovery cursor. */
final class BackfillRetries {
    private static final int LIMIT=4096;
    record Key(String dimension, long pos) {}
    record Entry(int attempts, long retryAt, String reason) {}
    private final Map<Key, Entry> entries = new LinkedHashMap<>();
    /** New failures observed while the persisted ledger is temporarily unreadable. */
    private final Map<Key, Entry> pendingBeforeLoad = new LinkedHashMap<>();
    private boolean loaded;

    synchronized boolean defer(String dimension, long pos, long now, String reason) {
        Key key = new Key(dimension,pos);
        Entry previous = pendingBeforeLoad.get(key);
        if (previous != null) {
            pendingBeforeLoad.put(key,next(previous,now,reason));
            return true;
        }
        previous = entries.get(key);
        if (previous != null) {
            entries.put(key,next(previous,now,reason));
            return true;
        }
        Map<Key,Entry> target=loaded?entries:pendingBeforeLoad;
        if (target.size() >= LIMIT) return false;
        target.put(key,next(null,now,reason));
        return true;
    }

    private static Entry next(Entry previous,long now,String reason) {
        int attempts = previous == null ? 1 : Math.min(16,previous.attempts+1);
        long delay = Math.min(300_000L, 5_000L << Math.min(6,attempts-1));
        return new Entry(attempts,now+delay,reason);
    }

    synchronized boolean waiting(String dimension,long pos,long now) {
        Key key=new Key(dimension,pos);
        Entry entry=pendingBeforeLoad.get(key);
        if(entry==null) entry=entries.get(key);
        return entry != null && entry.retryAt > now;
    }

    synchronized List<Key> due(String dimension,long now) {
        LinkedHashMap<Key,Entry> combined=new LinkedHashMap<>(entries);
        pendingBeforeLoad.forEach(combined::put);
        return combined.entrySet().stream().filter(e -> e.getKey().dimension.equals(dimension) && e.getValue().retryAt<=now)
            .sorted(Comparator.comparingLong(e -> e.getValue().retryAt)).limit(4).map(Map.Entry::getKey).toList();
    }

    synchronized void complete(String dimension,long pos) {
        Key key=new Key(dimension,pos);
        entries.remove(key);
        pendingBeforeLoad.remove(key);
    }

    /**
     * Returns true only after the persisted state was read successfully or confirmed absent.
     * Transient read/permission failures leave the ledger unresolved so a later call retries.
     */
    synchronized boolean load(Path path) {
        if (loaded) return true;
        Properties source=new Properties();
        try (var input=Files.newInputStream(path)) {
            source.load(input);
        } catch (NoSuchFileException missing) {
            loaded=true;
            mergePending();
            return true;
        } catch (IOException | RuntimeException unreadable) {
            return false;
        }

        if(source.size()>LIMIT) return false;
        LinkedHashMap<Key,Entry> disk=new LinkedHashMap<>();
        for (String key:source.stringPropertyNames()) {
            try {
                int slash=key.lastIndexOf('/');
                if(slash<=0 || slash==key.length()-1) return false;
                String[] value=source.getProperty(key).split(",",3);
                if(value.length!=3) return false;
                disk.put(new Key(key.substring(0,slash),Long.parseLong(key.substring(slash+1))),
                    new Entry(Math.max(1,Math.min(16,Integer.parseInt(value[0]))),
                        Math.min(System.currentTimeMillis()+300_000L,Long.parseLong(value[1])),value[2]));
            } catch (RuntimeException malformed) {
                // A malformed persisted coordinate may be the only discovery path for a
                // first-write LOD failure. Keep the whole ledger unresolved and never
                // overwrite it with a partial parse.
                return false;
            }
        }
        entries.clear();
        entries.putAll(disk);
        loaded=true;
        mergePending();
        return true;
    }

    /** Disk entries win capacity; same-coordinate in-memory failures are merged conservatively. */
    private void mergePending() {
        Iterator<Map.Entry<Key,Entry>> iterator=pendingBeforeLoad.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<Key,Entry> pending=iterator.next();
            Entry persisted=entries.get(pending.getKey());
            if(persisted!=null) {
                Entry current=pending.getValue();
                entries.put(pending.getKey(),new Entry(
                    Math.max(persisted.attempts,current.attempts),
                    Math.max(persisted.retryAt,current.retryAt),
                    current.reason));
                iterator.remove();
            } else if(entries.size()<LIMIT) {
                entries.put(pending.getKey(),pending.getValue());
                iterator.remove();
            }
        }
    }

    synchronized void save(Path path) throws IOException {
        if(!loaded)
            throw new IOException("Retry ledger has not been read successfully; refusing to replace unresolved persisted debt");
        mergePending();
        if(!pendingBeforeLoad.isEmpty())
            throw new IOException("Retry ledger capacity is full; refusing a partial save while "+pendingBeforeLoad.size()+" pre-load debt entries remain pending");
        Properties output=new Properties();
        entries.forEach((key,value) -> output.setProperty(key.dimension+"/"+key.pos,value.attempts+","+value.retryAt+","+value.reason));
        Files.createDirectories(path.getParent());
        Path temporary=path.resolveSibling(path.getFileName()+".tmp");
        try (var stream=Files.newOutputStream(temporary)) { output.store(stream,"Backfill retries"); }
        try { Files.move(temporary,path,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
        catch (AtomicMoveNotSupportedException unsupported) { Files.move(temporary,path,StandardCopyOption.REPLACE_EXISTING); }
    }
}
