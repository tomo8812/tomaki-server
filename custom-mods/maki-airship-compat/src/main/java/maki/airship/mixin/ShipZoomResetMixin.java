package maki.airship.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.client.ShipCameraZoom;

@Mixin(value = ShipCameraZoom.class, remap = false)
public abstract class ShipZoomResetMixin {
    @Shadow private static double multiplier;

    @Inject(method = "reset()V", at = @At("RETURN"), remap = false)
    private static void maki$restoreDefaultShipZoom(CallbackInfo ci) {
        multiplier = 1.0;
    }
}
