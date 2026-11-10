package maki.chunksmithcache.mixin;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.platform.World;
import com.kishku7.chunksmith.platform.NeoForgeWorld;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import maki.chunksmithcache.PipelineMetrics;
import maki.chunksmithcache.CacheManager;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Measures the adapter request, including its ticket queue and completion cleanup. */
@Mixin(targets="com.kishku7.chunksmith.GenerationTask", remap=false)
public abstract class GenerationRequestMixin {
    @Shadow @Final private long maxChunkMillis;
    @Shadow @Final private AtomicInteger dispatchLimit;

    // This is the elapsed value used by Chunksmith's slow-load decision,
    // including the preceding isChunkGenerated Future (unlike adapter-request).
    @Inject(method="adjustFromChunkLatency(J)V", at=@At("HEAD"), require=1)
    private void maki$nativeLatency(long elapsed, CallbackInfo ci) {
        PipelineMetrics.time("native-dispatch", TimeUnit.MILLISECONDS.toNanos(Math.max(0, elapsed)));
        if (elapsed > maxChunkMillis) PipelineMetrics.count("native-slow-threshold");
    }

    @Inject(method="adjustFromChunkLatency(J)V", at=@At("RETURN"), require=1)
    private void maki$nativeWidth(long elapsed, CallbackInfo ci) {
        PipelineMetrics.gauge("native-width-last", dispatchLimit.get());
    }

    @Redirect(method="run()V", at=@At(value="INVOKE",
        target="Lcom/kishku7/chunksmith/platform/World;isChunkGenerated(II)Ljava/util/concurrent/CompletableFuture;"), require=1)
    private CompletableFuture<Boolean> maki$trackGeneratedDecision(World world, int x, int z) {
        GenerationTask sourceTask=(GenerationTask)(Object)this;
        CompletableFuture<Boolean> nativeResult=world.isChunkGenerated(x,z);
        if(!(world instanceof NeoForgeWorld neoforge))return nativeResult;
        ChunkPos pos=new ChunkPos(x,z);
        CompletableFuture<Boolean> result=CacheManager.protectChunksmithGeneratedResult(
            neoforge.getWorld(),pos,sourceTask,nativeResult);
        return result.whenComplete((generated,error) -> {
            if(error==null)
                CacheManager.noteChunksmithGeneratedResult(neoforge.getWorld(),pos,sourceTask,Boolean.TRUE.equals(generated));
            else CacheManager.noteChunksmithGeneratedFailure(neoforge.getWorld(),pos,sourceTask);
        });
    }

    @Redirect(method="lambda$run$1", at=@At(value="INVOKE",
        target="Lcom/kishku7/chunksmith/platform/World;getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;"), require=2)
    private CompletableFuture<Void> maki$measureRequest(World world, int x, int z) {
        long started = System.nanoTime();
        GenerationTask sourceTask=(GenerationTask)(Object)this;
        if(world instanceof NeoForgeWorld neoforge
            && !CacheManager.preparePregenRequest(neoforge.getWorld(),new ChunkPos(x,z),sourceTask)) {
            CacheManager.pregenRequestFailed(neoforge.getWorld(),new ChunkPos(x,z),sourceTask);
            return CompletableFuture.failedFuture(
                new IllegalStateException("Unified pregen could not acquire normal-persistence guard for "+x+","+z));
        }
        CompletableFuture<Void> future = world.getChunkAtAsync(x,z);
        future.whenComplete((value,error) -> {
            PipelineMetrics.time("adapter-request",System.nanoTime()-started);
            if(world instanceof NeoForgeWorld neoforge)
                CacheManager.pregenRequestFinished(neoforge.getWorld(),new ChunkPos(x,z),sourceTask,error==null);
            if (error != null) PipelineMetrics.count("adapter-failed");
        });
        return future;
    }
}
