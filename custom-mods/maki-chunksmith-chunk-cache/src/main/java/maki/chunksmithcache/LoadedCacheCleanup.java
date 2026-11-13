package maki.chunksmithcache;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.level.ChunkPos;

/** Checks only snapshots corresponding to already loaded, normally persistent FULLs. */
final class LoadedCacheCleanup {
    private LoadedCacheCleanup() {}
    static CompletableFuture<Void> sweep(Map<Path,ChunkPos> loaded,
        Function<ChunkPos,CompletableFuture<Boolean>> normalFull,Consumer<ChunkPos> discard,Executor io){
        CompletableFuture<Void> pending=CompletableFuture.completedFuture(null);
        for(var entry:loaded.entrySet()){
            // One header probe per dimension at a time; keep file checks on cache
            // I/O, yielding between entries so player reads can use the same queue.
            pending=pending.thenComposeAsync(ignored->{
                if(!Files.isRegularFile(entry.getKey()))return CompletableFuture.completedFuture(null);
                return normalFull.apply(entry.getValue()).thenAccept(full->{
                    if(Boolean.TRUE.equals(full))discard.accept(entry.getValue());
                });
            },io);
        }
        return pending;
    }
}
