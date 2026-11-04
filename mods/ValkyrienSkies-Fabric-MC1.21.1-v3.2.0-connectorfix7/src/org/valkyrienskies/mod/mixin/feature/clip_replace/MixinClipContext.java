package org.valkyrienskies.mod.mixin.feature.clip_replace;

import net.minecraft.class_243;
import net.minecraft.class_3959;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.world.RayCastExtraParameter;
import org.valkyrienskies.mod.mixinducks.feature.clip_replace.ClipContextDuck;

@Mixin(class_3959.class)
public class MixinClipContext implements ClipContextDuck {
   @Unique
   private static RayCastExtraParameter defaultParameters = new RayCastExtraParameter();
   @Unique
   private RayCastExtraParameter parameters = new RayCastExtraParameter();

   @Override
   public void setRayCastExtraParameter(RayCastExtraParameter parameters) {
      if (parameters == null) {
         this.reset();
      } else {
         this.parameters = parameters;
      }
   }

   @Override
   public RayCastExtraParameter getRayCastExtraParameter() {
      return this.parameters;
   }

   @Override
   public void reset() {
      this.parameters = defaultParameters;
   }

   @Inject(method = "getFrom", at = @At("HEAD"), cancellable = true)
   private void returnedShipRealStart(CallbackInfoReturnable<class_243> cir) {
      if (this.parameters.get_posStart() != null) {
         cir.setReturnValue(this.parameters.get_posStart());
      }
   }

   @Inject(method = "getTo", at = @At("HEAD"), cancellable = true)
   private void returnedShipRealEnd(CallbackInfoReturnable<class_243> cir) {
      if (this.parameters.get_posEnd() != null) {
         cir.setReturnValue(this.parameters.get_posEnd());
      }
   }
}
