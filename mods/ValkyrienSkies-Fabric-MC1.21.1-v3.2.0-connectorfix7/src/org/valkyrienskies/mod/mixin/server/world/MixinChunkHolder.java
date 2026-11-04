package org.valkyrienskies.mod.mixin.server.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1923;
import net.minecraft.class_2818;
import net.minecraft.class_3193;
import net.minecraft.class_3194;
import net.minecraft.class_3218;
import net.minecraft.class_5539;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

@Mixin(class_3193.class)
public abstract class MixinChunkHolder {
   @Shadow
   private class_5539 field_26929;

   @Inject(method = "getTickingChunk", at = @At("HEAD"), cancellable = true)
   private void vs$getTickingChunkForShipyard(CallbackInfoReturnable<class_2818> cir) {
      class_1923 pos = ((class_3193)this).method_60473();
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(pos.field_9181, pos.field_9180) && this.field_26929 instanceof class_3218 serverLevel) {
         class_2818 chunk = serverLevel.method_14178().method_21730(pos.field_9181, pos.field_9180);
         if (chunk != null) {
            cir.setReturnValue(chunk);
         }
      }
   }

   @WrapOperation(
      method = "updateFutures",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkLevel;fullStatus(I)Lnet/minecraft/server/level/FullChunkStatus;")
   )
   private class_3194 vs$promoteShipyardStatus(int ticketLevel, Operation<class_3194> op) {
      class_1923 pos = ((class_3193)this).method_60473();
      return ticketLevel == 33 && VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(pos.field_9181, pos.field_9180)
         ? class_3194.field_44856
         : (class_3194)op.call(new Object[]{ticketLevel});
   }
}
