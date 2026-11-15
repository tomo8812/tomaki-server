package maki.chunksmithfixes.mixin;

import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import java.nio.file.Path;
import maki.chunksmithfixes.SavedLodPresence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.kishku7.chunksmith.lod.CsLodPresenceIndex", remap = false)
public abstract class PresenceIndexMixin {
   @Inject(method = "<init>", at = @At("RETURN"), require = 1)
   private void maki$register(Path root, CallbackInfo ci) {
      SavedLodPresence.register(root, (CsLodPresenceIndex)this);
   }
}
