package dev.diorama.mixin;

import dev.diorama.compat.FtbChunks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.ftb.mods.ftbchunks.client.map.UpdateChunkFromServerTask", remap = false)
public abstract class FtbChunksUpdateMixin {
   @Inject(method = "runMapTask", at = @At("RETURN"), require = 0, remap = false)
   private void diorama$claimUpdated(CallbackInfo ci) {
      FtbChunks.updated(this);
   }
}
