package maki.lodcpu.onlinefix.mixin;

import maki.lodcpu.AdaptivePolicy;
import maki.lodcpu.AdaptiveSample;
import maki.lodcpu.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AdaptivePolicy.class, remap = false)
public abstract class AdaptivePolicyMixin {
   @Unique
   private boolean maki$onlineAdaptive;
   @Unique
   private boolean maki$wasOnlineAdaptive;

   @ModifyVariable(method = "update(Lmaki/lodcpu/AdaptiveSample;ILmaki/lodcpu/Settings;)I", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 1)
   private int maki$keepAdaptivePolicyOnline(int players, AdaptiveSample sample, int originalPlayers, Settings settings) {
      boolean onlineAdaptive = players > 0 && settings.generateWhileOnline;
      if (onlineAdaptive != this.maki$wasOnlineAdaptive) {
         ((AdaptivePolicy)this).reset(settings, onlineAdaptive ? "online-mode" : "offline-mode");
         this.maki$wasOnlineAdaptive = onlineAdaptive;
      }

      this.maki$onlineAdaptive = onlineAdaptive;
      return onlineAdaptive ? 0 : players;
   }

   @Redirect(
      method = "update(Lmaki/lodcpu/AdaptiveSample;ILmaki/lodcpu/Settings;)I",
      at = @At(value = "FIELD", target = "Lmaki/lodcpu/Settings;max:I"),
      require = 1
   )
   private int maki$useOnlineMaximum(Settings settings) {
      return this.maki$onlineAdaptive ? Math.min(settings.max, settings.onlineMax) : settings.max;
   }

   @Redirect(
      method = "update(Lmaki/lodcpu/AdaptiveSample;ILmaki/lodcpu/Settings;)I",
      at = @At(value = "FIELD", target = "Lmaki/lodcpu/Settings;min:I"),
      require = 1
   )
   private int maki$useOnlineMinimum(Settings settings) {
      return this.maki$onlineAdaptive ? Math.min(settings.min, settings.onlineMax) : settings.min;
   }
}
