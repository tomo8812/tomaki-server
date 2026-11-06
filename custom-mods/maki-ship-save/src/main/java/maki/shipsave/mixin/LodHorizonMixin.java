package maki.shipsave.mixin;

import com.kishku7.chunksmith.lod.LodHorizon;
import maki.shipsave.SaveProtection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LodHorizon.class, remap = false)
public abstract class LodHorizonMixin {
    @Inject(method = "inDiscardZone(Ljava/lang/String;II)Z", at = @At("HEAD"), cancellable = true)
    private static void maki$preserveShipyard(
        String world,
        int chunkX,
        int chunkZ,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (SaveProtection.protectShipyard(world, chunkX, chunkZ)) {
            cir.setReturnValue(false);
        }
    }
}
