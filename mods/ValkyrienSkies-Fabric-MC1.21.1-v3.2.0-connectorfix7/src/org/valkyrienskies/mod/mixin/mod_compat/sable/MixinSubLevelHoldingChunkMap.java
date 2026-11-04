package org.valkyrienskies.mod.mixin.mod_compat.sable;

import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import net.minecraft.class_1923;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(SubLevelHoldingChunkMap.class)
public class MixinSubLevelHoldingChunkMap {
   @Shadow
   @Final
   private class_3218 level;

   @Inject(method = "updateChunkStatus", at = @At("HEAD"), cancellable = true, require = 0)
   private void dontSavePosInsideVSShip(class_1923 chunkPos, boolean loaded, CallbackInfo ci) {
      if (VSGameUtilsKt.isChunkInShipyard(this.level, chunkPos.field_9181, chunkPos.field_9180)) {
         ci.cancel();
      }
   }
}
