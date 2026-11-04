package org.valkyrienskies.mod.mixin.world.chunk;

import net.minecraft.class_1923;
import net.minecraft.class_3215;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

@Mixin(class_3215.class)
public class MixinServerChunkCache {
   @Inject(method = "isPositionTicking", at = @At("HEAD"), cancellable = true)
   private void vs$allowShipyardTicking(long pos, CallbackInfoReturnable<Boolean> cir) {
      int chunkX = class_1923.method_8325(pos);
      int chunkZ = class_1923.method_8332(pos);
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkX, chunkZ) && ((class_3215)this).method_21730(chunkX, chunkZ) != null) {
         cir.setReturnValue(true);
      }
   }
}
