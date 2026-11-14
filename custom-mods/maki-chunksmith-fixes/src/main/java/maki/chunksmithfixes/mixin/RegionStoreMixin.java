package maki.chunksmithfixes.mixin;

import com.kishku7.chunksmith.lod.CsLodChunk;
import java.nio.file.Path;
import maki.chunksmithfixes.SavedLodPresence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.kishku7.chunksmith.lod.CsLodRegionStore", remap = false)
public abstract class RegionStoreMixin {
    @Shadow @Final private Path root;
    @Inject(method = "write", at = @At("RETURN"), require = 1)
    private void maki$committed(CsLodChunk record, CallbackInfoReturnable<Integer> ci) {
        SavedLodPresence.written(root, record);
    }
}
