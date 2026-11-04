package org.valkyrienskies.mod.mixin.client;

import net.minecraft.class_310;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.client.ShipCameraZoom;

@Mixin(class_312.class)
public abstract class MixinMouseHandler {
   @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true, require = 1)
   private void valkyrienskies$shipCameraZoomScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
      class_310 mc = class_310.method_1551();
      if (mc != null && mc.field_1755 == null && mc.field_1724 != null && ShipCameraZoom.isShipCameraActive()) {
         if (yOffset != 0.0) {
            ShipCameraZoom.scroll(yOffset > 0.0 ? 1.0 : -1.0);
         }

         ci.cancel();
      }
   }
}
