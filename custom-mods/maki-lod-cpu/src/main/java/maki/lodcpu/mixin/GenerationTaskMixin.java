package maki.lodcpu.mixin;

import java.util.concurrent.atomic.AtomicInteger;
import maki.lodcpu.CpuGovernor;
import maki.lodcpu.TaskOwnership;
import maki.lodcpu.PlayerCenter;
import maki.lodcpu.WorkTelemetry;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.Selection;
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
