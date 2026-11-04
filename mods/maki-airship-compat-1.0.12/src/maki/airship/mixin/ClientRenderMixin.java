package maki.airship.mixin;

import maki.airship.ClientDiagnostics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class, remap = false)
public abstract class ClientRenderMixin {
   @Inject(method = "render(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("TAIL"), remap = false)
   private void maki$readRenderState(DeltaTracker var1, boolean var2, CallbackInfo var3) {
      ClientDiagnostics.frame();
   }
}
