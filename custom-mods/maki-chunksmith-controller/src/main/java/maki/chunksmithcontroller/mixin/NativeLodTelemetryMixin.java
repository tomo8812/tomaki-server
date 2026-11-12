package maki.chunksmithcontroller.mixin;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodRegionStore;
import java.io.IOException;
import maki.chunksmithcontroller.NativeLodTelemetry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets="com.kishku7.chunksmith.lod.CsLodStoreSink",remap=false)
public abstract class NativeLodTelemetryMixin {
    @Inject(method="offer(Ljava/lang/Object;)Z",at=@At("HEAD"),require=1)
    private void maki$offered(Object record,CallbackInfoReturnable<Boolean> ci){NativeLodTelemetry.offered(record);}
    @Inject(method="offer(Ljava/lang/Object;)Z",at=@At("RETURN"),require=1)
    private void maki$accepted(Object record,CallbackInfoReturnable<Boolean> ci){
        if(!Boolean.TRUE.equals(ci.getReturnValue()))NativeLodTelemetry.rejected(record);
    }
    @Redirect(method="persist",at=@At(value="INVOKE",target="Lcom/kishku7/chunksmith/lod/CsLodRegionStore;write(Lcom/kishku7/chunksmith/lod/CsLodChunk;)I"),require=1)
    private int maki$write(CsLodRegionStore store,CsLodChunk record)throws IOException{
        String token=NativeLodTelemetry.writing(record);
        long started=System.nanoTime();boolean success=false;
        try{int bytes=store.write(record);success=true;return bytes;}
        finally{NativeLodTelemetry.written(token,System.nanoTime()-started,success);}
    }
}
