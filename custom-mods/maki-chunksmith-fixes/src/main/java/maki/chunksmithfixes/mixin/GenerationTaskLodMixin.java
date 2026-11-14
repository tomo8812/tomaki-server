package maki.chunksmithfixes.mixin;

import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.kishku7.chunksmith.GenerationTask", remap = false)
public abstract class GenerationTaskLodMixin {
    @Redirect(method = "lambda$run$1", at = @At(value = "INVOKE",
        target = "Lcom/kishku7/chunksmith/lod/CsLodPresenceIndex;markLod(II)V"), require = 2)
    private void maki$deferPresence(CsLodPresenceIndex index, int x, int z) {
        // RegionStoreMixin publishes presence after successful native write.
    }
}
