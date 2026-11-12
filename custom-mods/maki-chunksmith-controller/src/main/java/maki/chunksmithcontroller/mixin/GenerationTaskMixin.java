package maki.chunksmithcontroller.mixin;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.kishku7.chunksmith.platform.World;
import maki.chunksmithcontroller.PregenControlHooks;
import maki.chunksmithcache.CacheManager;
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
            while(!stopped&&ResourceBudgetApi.blockFollowAdmission(task))
                LockSupport.parkNanos(1_000_000L);
        }
        Object next=iterator.next();
        if(!maki$owned())return next;
        if(next instanceof ChunkCoordinate coordinate){
            String dimension=selection.world().getName();
            while(!stopped&&!PregenControlHooks.reserveCandidate(
                dimension,coordinate.x(),coordinate.z(),task))
                LockSupport.parkNanos(1_000_000L);
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

    @Unique private final ThreadLocal<Boolean> maki$nativeDispatch=new ThreadLocal<>();

    @Invoker("lambda$run$1")
    protected abstract CompletionStage<Void> maki$dispatch(CsLodPresenceIndex index,ChunkCoordinate coordinate,
        boolean lodOnly,boolean horizon,boolean[] persisted,Boolean generated);

    @Inject(method="lambda$run$1",at=@At("HEAD"),cancellable=true,require=1)
    private void maki$prepareDispatch(CsLodPresenceIndex index,ChunkCoordinate coordinate,
        boolean lodOnly,boolean horizon,boolean[] persisted,Boolean generated,
        CallbackInfoReturnable<CompletionStage<Void>> ci){
        if(!maki$owned()||Boolean.TRUE.equals(maki$nativeDispatch.get()))return;
        GenerationTask owner=(GenerationTask)(Object)this;
        String dim=selection.world().getName();int x=coordinate.x(),z=coordinate.z();
        // Observe the exact 4.8.1 skip branch; let native code perform the skip.
        if(Boolean.TRUE.equals(generated)&&(index==null||index.hasLod(x,z)))return;
        if(!PregenControlHooks.beginActual(dim,x,z,owner)){
            ci.setReturnValue(CompletableFuture.failedFuture(new IllegalStateException("Dispatch admission closed")));
            return;
        }
        ci.setReturnValue(PregenControlHooks.prepareDispatch(selection.world(),x,z,owner).thenCompose(ignored->{
            if(!PlayerCenter.admissionOpen(owner)||owner.isStopping())
                return CompletableFuture.failedFuture(new IllegalStateException("Dispatch stopped before native LOD mark"));
            PregenControlHooks.preparationSucceeded(dim,x,z);
            maki$nativeDispatch.set(true);
            try{return maki$dispatch(index,coordinate,lodOnly,horizon,persisted,generated);}
            finally{maki$nativeDispatch.remove();}
        }).whenComplete((ignored,error)->{
            if(error!=null){
                CacheManager.pregenRequestFailed(((com.kishku7.chunksmith.platform.NeoForgeWorld)selection.world()).getWorld(),
                    new net.minecraft.world.level.ChunkPos(x,z),owner);
                PregenControlHooks.requestFailed(dim,x,z,owner);
            }
        }));
    }

    /**
     * Never refuse the native request here. Chunksmith 4.8.1 marks its LOD
     * presence index before this callsite, and a fabricated failed Future would
     * leave a false "LOD exists" bit. Admission and FULL ownership preparation
     * have already completed before the native dispatch body and LOD mark.
     */
    @Redirect(method="lambda$run$1",at=@At(value="INVOKE",
        target="Lcom/kishku7/chunksmith/platform/World;getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;"),require=2)
    private CompletableFuture<Void> maki$observeNativeRequest(World world,int x,int z){
        GenerationTask owner=(GenerationTask)(Object)this;
        if(!maki$owned())return world.getChunkAtAsync(x,z);
        String dimension=selection.world().getName();
        PregenControlHooks.requestInvoked(dimension,x,z,owner);
        try{
            return CacheManager.request(world,x,z,owner,()->RequestContext.with(owner,()->world.getChunkAtAsync(x,z))).whenComplete((value,error)->{
                if(error!=null)PregenControlHooks.requestFailed(dimension,x,z,owner);
            });
        }catch(Throwable error){
            PregenControlHooks.requestFailed(dimension,x,z,owner);
            throw error;
        }
    }

    // Chunksmith's shape/LOD/native skip branches call update without any
    // asynchronous request. Release the predictive stage credits for skips.
    @Inject(method="update(IIZZ)V",at=@At("HEAD"),require=1)
    private void maki$coordinateCompleted(int x,int z,boolean loaded,boolean persisted,CallbackInfo ci){
        GenerationTask owner=(GenerationTask)(Object)this;
        if(maki$owned()){
            CacheManager.releaseUnusedPreparation(((com.kishku7.chunksmith.platform.NeoForgeWorld)selection.world()).getWorld(),new net.minecraft.world.level.ChunkPos(x,z),owner);
            PregenControlHooks.nativeCoordinateFinished(selection.world().getName(),x,z,owner);
        }
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
