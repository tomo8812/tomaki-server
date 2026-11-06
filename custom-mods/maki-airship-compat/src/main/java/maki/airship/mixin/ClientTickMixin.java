package maki.airship.mixin;

import maki.airship.PassengerRecovery;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public abstract class ClientTickMixin {
    @Inject(method = "tick()V", at = @At("TAIL"), remap = false)
    private void maki$retryPassengers(CallbackInfo ci) {
        PassengerRecovery.tick();
    }
}
