package dev.diorama.mixin;

import dev.diorama.MinimapHud;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public abstract class BossBarMixin {
   @Inject(method = "drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V", at = @At("HEAD"))
   private void diorama$bossBarDrawn(GuiGraphics graphics, int x, int y, BossEvent event, CallbackInfo ci) {
      MinimapHud.bossBarDrawn(y);
   }
}
