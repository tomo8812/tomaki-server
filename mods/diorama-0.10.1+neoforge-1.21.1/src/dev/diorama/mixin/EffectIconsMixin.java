package dev.diorama.mixin;

import dev.diorama.MinimapHud;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class EffectIconsMixin {
   @Inject(method = "renderEffects", at = @At("RETURN"))
   private void diorama$effectIconsDrawn(CallbackInfo ci) {
      MinimapHud.effectIconsDrawn();
   }
}
