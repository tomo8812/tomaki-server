package org.valkyrienskies.mod.mixin.realms;

import net.minecraft.class_4902;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

@Mixin(class_4902.class)
public class MixinRealmsConnect {
   @Inject(method = "connect", at = @At("HEAD"))
   private void preConnect(CallbackInfo ci) {
      ValkyrienSkiesMod.getVsCore().setClientUsesUDP(false);
   }
}
