package org.valkyrienskies.mod.mixin.client.gui.screens;

import net.minecraft.class_412;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

@Mixin(class_412.class)
public class MixinConnectScreen {
   @Inject(method = "startConnecting", at = @At("HEAD"))
   private static void preStartConnecting(CallbackInfo ci) {
      ValkyrienSkiesMod.getVsCore().setClientUsesUDP(false);
   }
}
