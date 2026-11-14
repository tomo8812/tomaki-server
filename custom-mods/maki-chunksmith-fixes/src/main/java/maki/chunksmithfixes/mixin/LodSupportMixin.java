package maki.chunksmithfixes.mixin;

import maki.chunksmithfixes.SavedLodPresence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.kishku7.chunksmith.lod.LodSupport", remap = false)
public abstract class LodSupportMixin {
    @Inject(method = "shutdown", at = @At("RETURN"), require = 1)
    private static void maki$shutdown(CallbackInfo ci) { SavedLodPresence.clear(); maki.chunksmithfixes.ExploredLodRefresh.clear(); }
}
