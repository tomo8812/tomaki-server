package maki.chunksmithcache;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/** Capacity-deferred FULL recovery only; independent of current-cache readiness. */
final class QuarantineRecoveryRetry {
    static final long INTERVAL_NANOS=TimeUnit.MINUTES.toNanos(1);
    private final Set<Path> sources=new HashSet<>();
    private long nextAttempt;
    private boolean queued;

    synchronized void deferred(Path source,long now){
        if(sources.add(source.toAbsolutePath().normalize()))nextAttempt=now+INTERVAL_NANOS;
    }
    synchronized void recovered(Path source){sources.remove(source.toAbsolutePath().normalize());}
    synchronized boolean protects(Path source){return sources.contains(source.toAbsolutePath().normalize());}
    synchronized void retainSources(List<Path> existing){
        Set<Path> retained=new HashSet<>();
        for(Path source:existing)retained.add(source.toAbsolutePath().normalize());
        sources.retainAll(retained);
    }
    synchronized boolean claim(long now){
        if(sources.isEmpty()||queued||now<nextAttempt)return false;
        queued=true;return true;
    }
    synchronized void finished(long now){queued=false;nextAttempt=now+INTERVAL_NANOS;}
}
