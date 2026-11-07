package maki.chunksmithcache;

import java.util.concurrent.CompletableFuture;

/** Waits for native LOD completion without occupying a worker or generation lease. */
final class PublicationGate {
    private PublicationGate() {}

    static void afterLod(CompletableFuture<Boolean> lod, CompletableFuture<Boolean> result, Runnable publish) {
        lod.whenComplete((committed, error) -> {
            if (result.isDone()) return;
            if (error != null || !Boolean.TRUE.equals(committed)) {
                result.complete(false);
                return;
            }
            try { publish.run(); }
            catch (RuntimeException rejected) { result.complete(false); }
        });
    }
}
