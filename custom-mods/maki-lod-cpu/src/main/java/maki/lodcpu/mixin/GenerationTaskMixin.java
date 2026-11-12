package maki.lodcpu.mixin;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CompletableFuture;
import com.kishku7.chunksmith.platform.World;
import maki.lodcpu.PregenControlHooks;
import java.util.concurrent.locks.LockSupport;
import maki.lodcpu.CpuGovernor;
import maki.lodcpu.ResourceBudgetApi;
import maki.lodcpu.TaskOwnership;
import maki.lodcpu.PlayerCenter;
import maki.lodcpu.WorkTelemetry;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.iterator.ChunkIterator;
import com.kishku7.chunksmith.util.ChunkResidency;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets="com.kishku7.chunksmith.GenerationTask",remap=false)
public abstract class GenerationTaskMixin {
    @Shadow @Final private AtomicInteger dispatchLimit;
    @Shadow @Final private AtomicInteger inFlight;
    @Shadow @Final private Selection selection;
    @Shadow private volatile boolean writeQueueStalled;
    @Shadow private volatile boolean chunkResidencyStalled;
    @Shadow private volatile boolean heapStalled;
    @Shadow private boolean stopped;
    @Unique private final WorkTelemetry maki$metrics=new WorkTelemetry();

    @Unique
    private boolean maki$owned(){return TaskOwnership.isOwned((GenerationTask)(Object)this);}

    @Inject(method="run()V",at=@At("HEAD"),require=1)
    private void maki$taskStarted(CallbackInfo ci){
        if(maki$owned())PlayerCenter.nativeTaskStarted((GenerationTask)(Object)this);
    }

    @Inject(method="stop(Z)V",at=@At("HEAD"),require=1)
    private void maki$taskStopping(boolean cancelled,CallbackInfo ci){
        if(maki$owned())PlayerCenter.nativeTaskStopping((GenerationTask)(Object)this,cancelled);
    }

    /**
     * Establishes a safe boundary without calling GenerationTask.stop().
     * One iterator coordinate may already have been selected before the
     * reservation; waiting here until inFlight reaches zero drains that work.
     * The selected coordinate is retained and dispatch continues unchanged
     * after the backfill slice releases the shared budget.
     */
    @Redirect(
        method="run()V",
        at=@At(value="INVOKE",target="Lcom/kishku7/chunksmith/iterator/ChunkIterator;next()Ljava/lang/Object;"),
        require=1
    )
    private Object maki$sharedBackfillBarrier(ChunkIterator iterator) {
        if(maki$owned()) {
            while(!stopped&&ResourceBudgetApi.blockFollowAdmission((GenerationTask)(Object)this))
                LockSupport.parkNanos(1_000_000L);
        }
        Object next=iterator.next();
        if(!maki$owned())return next;
        while(!stopped&&ResourceBudgetApi.blockFollowDispatch()) {
            ResourceBudgetApi.noteFollowInFlight(inFlight.get());
            LockSupport.parkNanos(1_000_000L);
        }
        return next;
    }

    /**
     * Observe Chunksmith's own generated-state result. The native generated/LOD
     * reuse decision remains authoritative: E-lite only reserves scheduling WIP.
     */
    @Redirect(method="run()V",at=@At(value="INVOKE",
        target="Lcom/kishku7/chunksmith/platform/World;isChunkGenerated(II)Ljava/util/concurrent/CompletableFuture;"),require=1)
    private CompletableFuture<Boolean> maki$admitNativeCandidate(World world,int x,int z){
        GenerationTask owner=(GenerationTask)(Object)this;
        if(!maki$owned())return world.isChunkGenerated(x,z);
        String dim=selection.world().getName();
        if(!PregenControlHooks.reserveCandidate(dim,x,z,owner)){
            PregenControlHooks.defer(dim,x,z,owner);
            return CompletableFuture.failedFuture(new IllegalStateException("E-lite candidate WIP full"));
        }
        try{
            return world.isChunkGenerated(x,z).whenComplete((generated,error)->{
                if(error!=null)PregenControlHooks.candidateFailed(dim,x,z,owner);
                else PregenControlHooks.classify(dim,x,z,owner,Boolean.TRUE.equals(generated));
            });
        }catch(Throwable error){
            PregenControlHooks.candidateFailed(dim,x,z,owner);
            throw error;
        }
    }

    /**
     * Exactly one admission at the actual Chunksmith getChunkAtAsync call.
     * FULL persistence ownership and validity remain with chunk-cache.
     */
    @Redirect(method="lambda$run$1",at=@At(value="INVOKE",
        target="Lcom/kishku7/chunksmith/platform/World;getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;"),require=2)
    private CompletableFuture<Void> maki$admitNativeRequest(World world,int x,int z){
        GenerationTask owner=(GenerationTask)(Object)this;
        if(!maki$owned())return world.getChunkAtAsync(x,z);
        String dim=selection.world().getName();
        if(!PregenControlHooks.beginActual(dim,x,z,owner)){
            PregenControlHooks.defer(dim,x,z,owner);
            return CompletableFuture.failedFuture(new IllegalStateException("E-lite actual WIP admission rejected"));
        }
        try{
            return world.getChunkAtAsync(x,z).whenComplete((value,error)->{
                if(error!=null)PregenControlHooks.requestFailed(dim,x,z,owner);
            });
        }catch(Throwable error){
            PregenControlHooks.requestFailed(dim,x,z,owner);
            throw error;
        }
    }

    @Inject(method="update(IIZZ)V",at=@At("HEAD"),cancellable=true,require=1)
    private void maki$preserveDeferred(int x,int z,boolean loaded,boolean persisted,CallbackInfo ci){
        GenerationTask owner=(GenerationTask)(Object)this;
        if(!maki$owned())return;
        String dim=selection.world().getName();
        if(PregenControlHooks.consumeRejected(dim,x,z,owner)){ci.cancel();return;}
        PregenControlHooks.nativeCoordinateFinished(dim,x,z,owner);
    }

    @Unique
    private void maki$clamp(){
        int upper=CpuGovernor.cap(),old;
        do{old=dispatchLimit.get();if(old<=upper)return;}
        while(!dispatchLimit.compareAndSet(old,upper));
    }

    @Inject(method="adjustFromTickHealth()V",at=@At("HEAD"),require=1)
    private void maki$beforeHealth(CallbackInfo ci){
        if(!maki$owned())return;
        CpuGovernor.markActive();maki$clamp();
        if(selection.world().getName().equals("minecraft:overworld")&&maki$metrics.due()){
            maki$metrics.observe(dispatchLimit.get(),inFlight.get(),selection.world().getQueuedChunkWrites(),
                writeQueueStalled,chunkResidencyStalled,heapStalled,ChunkResidency.addedChunks());
            CpuGovernor.observe(maki$metrics);
        }
    }

    @Inject(method="adjustFromTickHealth()V",at=@At("RETURN"),require=1)
    private void maki$afterHealth(CallbackInfo ci){if(maki$owned())maki$clamp();}

    @Inject(method="rampUp(Z)V",at=@At("HEAD"),cancellable=true,require=1)
    private void maki$preventRamp(boolean burst,CallbackInfo ci){
        if(!maki$owned())return;
        maki$clamp();
        if(dispatchLimit.get()>=CpuGovernor.cap())ci.cancel();
    }

    @Inject(method="adjustFromChunkLatency(J)V",at=@At("HEAD"),require=1)
    private void maki$completion(long elapsed,CallbackInfo ci){if(maki$owned())maki$metrics.completed(elapsed);}
}
