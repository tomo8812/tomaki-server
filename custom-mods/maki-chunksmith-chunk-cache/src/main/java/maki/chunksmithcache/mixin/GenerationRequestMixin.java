package maki.chunksmithcache.mixin;

import com.kishku7.chunksmith.platform.World;
import java.util.concurrent.CompletableFuture;
import maki.chunksmithcache.PipelineMetrics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Measures the adapter request, including its ticket queue and completion cleanup. */
@Mixin(targets="com.kishku7.chunksmith.GenerationTask", remap=false)
public abstract class GenerationRequestMixin {
    @Redirect(method="lambda$run$1", at=@At(value="INVOKE",
        target="Lcom/kishku7/chunksmith/platform/World;getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;"), require=2)
    private CompletableFuture<Void> maki$measureRequest(World world, int x, int z) {
        long started = System.nanoTime();
        CompletableFuture<Void> future = world.getChunkAtAsync(x,z);
        future.whenComplete((value,error) -> {
            PipelineMetrics.time("adapter-request",System.nanoTime()-started);
            if (error != null) PipelineMetrics.count("adapter-failed");
        });
        return future;
    }
}
