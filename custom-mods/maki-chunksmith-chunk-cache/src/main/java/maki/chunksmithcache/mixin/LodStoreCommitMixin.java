package maki.chunksmithcache.mixin;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodRegionStore;
import java.io.IOException;
import maki.chunksmithcache.LodCommitTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets="com.kishku7.chunksmith.lod.CsLodStoreSink", remap=false)
public abstract class LodStoreCommitMixin {
    @Inject(method="offer(Ljava/lang/Object;)Z", at=@At("HEAD"), require=1)
    private void maki$observeOffer(Object record, CallbackInfoReturnable<Boolean> ci) {
        LodCommitTracker.offered(record);
    }
    @Redirect(method="persist", at=@At(value="INVOKE", target="Lcom/kishku7/chunksmith/lod/CsLodRegionStore;write(Lcom/kishku7/chunksmith/lod/CsLodChunk;)I"), require=1)
    private int maki$acknowledge(CsLodRegionStore store, CsLodChunk record) throws IOException {
        try {
            int bytes = store.write(record);
            LodCommitTracker.written(record, null);
            return bytes;
        } catch (IOException | RuntimeException | Error error) {
            LodCommitTracker.written(record, error);
            throw error;
        }
    }
}
