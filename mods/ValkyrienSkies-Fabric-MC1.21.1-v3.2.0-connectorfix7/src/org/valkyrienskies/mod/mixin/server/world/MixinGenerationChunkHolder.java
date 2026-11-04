package org.valkyrienskies.mod.mixin.server.world;

import net.minecraft.class_1923;
import net.minecraft.class_3194;
import net.minecraft.class_9761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

@Mixin(class_9761.class)
public abstract class MixinGenerationChunkHolder {
   @Inject(method = "getFullStatus", at = @At("HEAD"), cancellable = true)
   private void vs$upgradeShipyardHolderStatus(CallbackInfoReturnable<class_3194> cir) {
      class_1923 pos = ((class_9761)this).method_60473();
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(pos.field_9181, pos.field_9180)) {
         cir.setReturnValue(class_3194.field_44856);
      }
   }
}
