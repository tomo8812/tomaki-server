package org.valkyrienskies.mod.mixin.feature.fix_frustum_dead_loop;

import net.minecraft.class_4604;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_4604.class)
public class MixinFrustum {
   @Unique
   private int vs$loopCount = 0;

   @Inject(
      method = "offsetToFullyIncludeCameraCube",
      at = @At(value = "INVOKE", target = "Lorg/joml/FrustumIntersection;intersectAab(FFFFFF)I"),
      cancellable = true
   )
   private void checkIfItsExceeded(int i, CallbackInfoReturnable<class_4604> cir) {
      if (this.vs$loopCount > 10) {
         cir.setReturnValue((class_4604)this);
         this.vs$loopCount = 0;
      } else {
         this.vs$loopCount++;
      }
   }
}
