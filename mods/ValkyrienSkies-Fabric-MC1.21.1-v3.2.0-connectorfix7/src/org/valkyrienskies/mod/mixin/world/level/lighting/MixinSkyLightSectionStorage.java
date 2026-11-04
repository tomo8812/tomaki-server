package org.valkyrienskies.mod.mixin.world.level.lighting;

import net.minecraft.class_2804;
import net.minecraft.class_3560;
import net.minecraft.class_3569;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_3569.class)
public abstract class MixinSkyLightSectionStorage extends class_3560 {
   private MixinSkyLightSectionStorage() {
      super(null, null, null);
   }

   @Inject(method = "createDataLayer", at = @At("HEAD"), cancellable = true)
   private void copyQueuedDataLayer(long sectionPos, CallbackInfoReturnable<class_2804> cir) {
      class_2804 queued = (class_2804)this.field_15807.get(sectionPos);
      if (queued != null) {
         cir.setReturnValue(queued.method_12144());
      }
   }
}
