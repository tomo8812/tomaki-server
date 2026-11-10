package maki.chunksmithcache;

import java.util.concurrent.CompletableFuture;

final class PublicationGate {
   private PublicationGate() {
   }

   static void afterLod(CompletableFuture<Boolean> lod, CompletableFuture<Boolean> result, Runnable publish) {
      lod.whenComplete((committed, error) -> {
         if (!result.isDone()) {
            if (error == null && Boolean.TRUE.equals(committed)) {
               try {
                  publish.run();
               } catch (RuntimeException rejected) {
                  result.complete(false);
               }
            } else {
               result.complete(false);
            }
         }
      });
   }
}
