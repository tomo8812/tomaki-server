package maki.chunksmithcache;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/** Reference-counted path locks: unrelated cache reads never hold up a normal save claim. */
final class CachePathLocks {
    private static final ConcurrentHashMap<Path, Cell> LOCKS = new ConcurrentHashMap<>();
    private static final class Cell { final ReentrantLock lock=new ReentrantLock(); int users; }
    private CachePathLocks() {}

    static Guard lock(Path file) {
        Path key=file.toAbsolutePath().normalize();
        Cell cell=LOCKS.compute(key,(ignored,current) -> {
            Cell chosen=current==null?new Cell():current;
            chosen.users++;
            return chosen;
        });
        cell.lock.lock();
        return new Guard(key,cell);
    }

    static final class Guard implements AutoCloseable {
        private final Path key;
        private final Cell cell;
        private boolean closed;
        private Guard(Path key,Cell cell) { this.key=key; this.cell=cell; }
        public void close() {
            if(closed) return;
            closed=true;
            cell.lock.unlock();
            LOCKS.compute(key,(ignored,current) -> --cell.users==0?null:cell);
        }
    }
}
