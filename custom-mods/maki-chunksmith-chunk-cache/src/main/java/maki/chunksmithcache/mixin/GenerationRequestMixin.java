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

/** Measures native dispatch and binds both admission stages to the exact task. */
@Mixin(targets="com.kishku7.chunksmith.GenerationTask",remap=false)
public abstract class GenerationRequestMixin {
    @Shadow @Final private long maxChunkMillis;
    @Shadow @Final private AtomicInteger dispatchLimit;

    @Inject(method="adjustFromChunkLatency(J)V",at=@At("HEAD"),require=1)
    private void maki$nativeLatency(long elapsed,CallbackInfo ci) {
        PipelineMetrics.time("native-dispatch",TimeUnit.MILLISECONDS.toNanos(Math.max(0,elapsed)));
        if(elapsed>maxChunkMillis)PipelineMetrics.count("native-slow-threshold");
    }

    @Inject(method="adjustFromChunkLatency(J)V",at=@At("RETURN"),require=1)
    private void maki$nativeWidth(long elapsed,CallbackInfo ci) {
        PipelineMetrics.gauge("native-width-last",dispatchLimit.get());
    }

    @Redirect(method="run()V",at=@At(value="INVOKE",
        target="Lcom/kishku7/chunksmith/platform/World;isChunkGenerated(II)Ljava/util/concurrent/CompletableFuture;"),require=1)
    private CompletableFuture<Boolean> maki$trackGeneratedDecision(World world,int x,int z) {
        GenerationTask sourceTask=(GenerationTask)(Object)this;
        if(!(world instanceof NeoForgeWorld neoforge))return world.isChunkGenerated(x,z);
        ChunkPos pos=new ChunkPos(x,z);
        if(!CacheManager.reservePregenCandidate(neoforge.getWorld(),pos,sourceTask)) {
            CacheManager.markPregenProgressRejected(sourceTask,pos);
            CacheManager.deferPregenAdmissionRetry(neoforge.getWorld(),pos);
            PipelineMetrics.count("pregen-candidate-admission-rejected");
            return CompletableFuture.failedFuture(
                new IllegalStateException("Unified pregen stage WIP admission blocked at "+pos));
        }
        CompletableFuture<Boolean> nativeResult;
        try { nativeResult=world.isChunkGenerated(x,z); }
        catch(Throwable error) {
            CacheManager.noteChunksmithGeneratedFailure(neoforge.getWorld(),pos,sourceTask);
            throw error;
        }
        CompletableFuture<Boolean> result=CacheManager.protectChunksmithGeneratedResult(
            neoforge.getWorld(),pos,sourceTask,nativeResult);
        return result.whenComplete((generated,error)->{
            if(error==null)
                CacheManager.noteChunksmithGeneratedResult(neoforge.getWorld(),pos,sourceTask,Boolean.TRUE.equals(generated));
            else CacheManager.noteChunksmithGeneratedFailure(neoforge.getWorld(),pos,sourceTask);
        });
    }

    @Inject(method="update(IIZZ)V",at=@At("HEAD"),cancellable=true,require=1)
    private void maki$preserveRejectedCoordinate(int x,int z,boolean loaded,boolean persisted,CallbackInfo ci) {
        GenerationTask sourceTask=(GenerationTask)(Object)this;
        if(CacheManager.consumePregenProgressRejected(sourceTask,x,z)) {
            PipelineMetrics.count("pregen-progress-update-suppressed");
            ci.cancel();
        }
    }

    @Redirect(method="lambda$run$1",at=@At(value="INVOKE",
        target="Lcom/kishku7/chunksmith/platform/World;getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;"),require=2)
    private CompletableFuture<Void> maki$measureRequest(World world,int x,int z) {
        long started=System.nanoTime();
        GenerationTask sourceTask=(GenerationTask)(Object)this;
        if(world instanceof NeoForgeWorld neoforge
            &&!CacheManager.preparePregenRequest(neoforge.getWorld(),new ChunkPos(x,z),sourceTask)) {
            ChunkPos pos=new ChunkPos(x,z);
            CacheManager.markPregenProgressRejected(sourceTask,pos);
            CacheManager.pregenRequestFailed(neoforge.getWorld(),pos,sourceTask);
            return CompletableFuture.failedFuture(
                new IllegalStateException("Unified pregen could not acquire final stage/persistence admission for "+x+","+z));
        }
        CompletableFuture<Void> future=CacheManager.withChunksmithRequestTask(
            sourceTask,()->world.getChunkAtAsync(x,z));
        future.whenComplete((value,error)->{
            PipelineMetrics.time("adapter-request",System.nanoTime()-started);
            if(world instanceof NeoForgeWorld neoforge)
                CacheManager.pregenRequestFinished(neoforge.getWorld(),new ChunkPos(x,z),sourceTask,error==null);
            if(error!=null)PipelineMetrics.count("adapter-failed");
        });
        return future;
    }
}
