package maki.chunksmithcontroller.mixin;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.kishku7.chunksmith.platform.World;
import maki.chunksmithcontroller.PregenControlHooks;
import maki.chunksmithcontroller.RequestContext;
import java.util.concurrent.locks.LockSupport;
import maki.chunksmithcontroller.CpuGovernor;
import maki.chunksmithcontroller.ResourceBudgetApi;
import maki.chunksmithcontroller.TaskOwnership;
import maki.chunksmithcontroller.PlayerCenter;
import maki.chunksmithcontroller.WorkTelemetry;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.iterator.ChunkIterator;
import com.kishku7.chunksmith.util.ChunkCoordinate;
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
    @Shadow @Final private boolean ioThrottleEnabled;
    @Shadow @Final private int maxWorkingCount;
    @Shadow @Final private long maxLodQueue;
    @Unique private final WorkTelemetry maki$metrics=new WorkTelemetry();

    @Unique
    private boolean maki$owned(){return TaskOwnership.isOwned((GenerationTask)(Object)this);}

    @Inject(method="run()V",at=@At("HEAD"),require=1)
    private void maki$taskStarted(CallbackInfo ci){
        if(maki$owned())PlayerCenter.nativeTaskStarted((GenerationTask)(Object)this);
    }

    @Inject(method="run()V",at=@At("RETURN"),require=1)
    private void maki$taskFinished(CallbackInfo ci){
        if(maki$owned())PregenControlHooks.taskFinished((GenerationTask)(Object)this);
    }

    @Inject(method="stop(Z)V",at=@At("HEAD"),require=1)
    private void maki$taskStopping(boolean cancelled,CallbackInfo ci){
        if(maki$owned())PlayerCenter.nativeTaskStopping((GenerationTask)(Object)this,cancelled);
    }

    /**
     * Chunksmith 4.8.1 can dispatch a LOD repair or force-load without calling
     * World.isChunkGenerated. Reserve at the one native iterator boundary, so
     * every dispatched coordinate has the same WIP protection and skip paths
     * return their predictive credits through update().
     *
     * The selected coordinate is retained while waiting; an E-lite WIP limit
     * never converts Chunksmith's own generated/LOD decision into a failure.
     */
    @Redirect(method="run()V",
        at=@At(value="INVOKE",target="Lcom/kishku7/chunksmith/iterator/ChunkIterator;next()Ljava/lang/Object;"),
        require=1)
    private Object maki$candidateAdmission(ChunkIterator iterator) {
        GenerationTask task=(GenerationTask)(Object)this;
        if(maki$owned()){
            while(!stopped&&ResourceBudgetApi.blockFollowAdmission(task)){
                LockSupport.parkNanos(1_000_000L);
            }
        }
        Object next=iterator.next();
        if(!maki$owned())return next;
        if(next instanceof ChunkCoordinate coordinate){
            String dimension=selection.world().getName();
            while(!stopped&&!PregenControlHooks.reserveCandidate(
                dimension,coordinate.x(),coordinate.z(),task)){
                LockSupport.parkNanos(1_000_000L);
            }
        }
        return next;
    }

    /**
     * Observe the native decision; never substitute an artificial "generated"
     * result and never fail a native isChunkGenerated() Future for WIP pressure.
     */
    @Redirect(method="run()V",at=@At(value="INVOKE",
        target="Lcom/kishku7/chunksmith/platform/World;isChunkGenerated(II)Ljava/util/concurrent/CompletableFuture;"),require=1)
    private CompletableFuture<Boolean> maki$observeNativeCandidate(World world,int x,int z){
        CompletableFuture<Boolean> result=world.isChunkGenerated(x,z);
        if(!maki$owned())return result;
        GenerationTask owner=(GenerationTask)(Object)this;
        String dimension=selection.world().getName();
        return result.whenComplete((generated,error)->{
            if(error!=null)PregenControlHooks.candidateFailed(dimension,x,z,owner);
            else PregenControlHooks.classify(dimension,x,z,owner,Boolean.TRUE.equals(generated));
        });
    }

    @Inject(method="lambda$run$1",at=@At("HEAD"),cancellable=true,require=1)
    private void maki$admitDispatch(CsLodPresenceIndex index,ChunkCoordinate coordinate,
        boolean lodOnly,boolean horizon,boolean[] persisted,Boolean generated,
        CallbackInfoReturnable<CompletionStage<Void>> ci){
        if(!maki$owned()||Boolean.TRUE.equals(generated)&&(index==null||index.hasLod(coordinate.x(),coordinate.z())))return;
        if(!PregenControlHooks.beginActual(selection.world().getName(),coordinate.x(),coordinate.z(),(GenerationTask)(Object)this)){
            PregenControlHooks.candidateFailed(selection.world().getName(),coordinate.x(),coordinate.z(),(GenerationTask)(Object)this);
            ci.setReturnValue(CompletableFuture.failedFuture(new IllegalStateException("Dispatch admission closed before native LOD mark")));
        }
    }

    /**
     * Never refuse the native request here. Chunksmith 4.8.1 marks its LOD
     * presence index before this callsite. Admission has already completed;
     * native chunk generation and ordinary world persistence remain unchanged.
     */
    @Redirect(method="lambda$run$1",at=@At(value="INVOKE",
        target="Lcom/kishku7/chunksmith/platform/World;getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;"),require=2)
    private CompletableFuture<Void> maki$observeNativeRequest(World world,int x,int z){
        GenerationTask owner=(GenerationTask)(Object)this;
        if(!maki$owned())return world.getChunkAtAsync(x,z);
        String dimension=selection.world().getName();
        String token=ResourceBudgetApi.attemptToken(dimension,x,z,owner);
        ResourceBudgetApi.stageStarted(token,"FULL");long started=System.nanoTime();
        try{
            return RequestContext.with(new RequestContext.Request(token,dimension,x,z),()->world.getChunkAtAsync(x,z)).whenComplete((value,error)->
                ResourceBudgetApi.stageFinished(token,"FULL",System.nanoTime()-started,error==null));
        }catch(Throwable error){
            ResourceBudgetApi.stageFinished(token,"FULL",System.nanoTime()-started,false);throw error;
        }
    }

    // Chunksmith's shape/LOD/native skip branches call update without any
    // asynchronous request. Release the predictive stage credits for skips.
    @Inject(method="update(IIZZ)V",at=@At("HEAD"),require=1)
    private void maki$coordinateCompleted(int x,int z,boolean loaded,boolean persisted,CallbackInfo ci){
        GenerationTask owner=(GenerationTask)(Object)this;
        if(maki$owned()){
            PregenControlHooks.nativeCoordinateFinished(selection.world().getName(),x,z,owner);
        }
    }

    @Unique
    private void maki$clamp(){
        int upper=CpuGovernor.cap(),old;
        do{old=dispatchLimit.get();if(old<=upper)return;}
        while(!dispatchLimit.compareAndSet(old,upper));
    }

    // This native dispatch-loop hook runs even with ioThrottle=false.
    @Inject(method="adjustFromLodQueue()V",at=@At("HEAD"),require=1)
    private void maki$beforeDispatch(CallbackInfo ci){
        if(!maki$owned())return;
        CpuGovernor.markActive();maki$clamp();
        // With native tick throttling disabled, it has no ramp-up callsite.
        // Apply the controller target before the native LOD queue checks, which
        // may still narrow it; heap/residency/write gates remain native.
        dispatchLimit.set(maki.chunksmithcontroller.DispatchCap.apply(dispatchLimit.get(),CpuGovernor.cap(),maxWorkingCount,
            ioThrottleEnabled,heapStalled||chunkResidencyStalled||writeQueueStalled,
            com.kishku7.chunksmith.lod.LodSinks.get().queueDepth(),maxLodQueue));
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
