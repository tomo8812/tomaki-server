package maki.lodcpu.onlinefix;

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
   private int maki$keepAdaptivePolicyOnline(int var1, AdaptiveSample var2, int var3, Settings var4) {
      boolean var5 = var1 > 0 && var4.generateWhileOnline;
      if (var5 != this.maki$wasOnlineAdaptive) {
         ((AdaptivePolicy)this).reset(var4, var5 ? "online-mode" : "offline-mode");
         this.maki$wasOnlineAdaptive = var5;
      }

      this.maki$onlineAdaptive = var5;
      return var5 ? 0 : var1;
   }

   @Redirect(
      method = "update(Lmaki/lodcpu/AdaptiveSample;ILmaki/lodcpu/Settings;)I",
      at = @At(value = "FIELD", target = "Lmaki/lodcpu/Settings;max:I"),
      require = 1
   )
   private int maki$useOnlineMaximum(Settings var1) {
      return this.maki$onlineAdaptive ? Math.min(var1.max, var1.onlineMax) : var1.max;
   }

   @Redirect(
      method = "update(Lmaki/lodcpu/AdaptiveSample;ILmaki/lodcpu/Settings;)I",
      at = @At(value = "FIELD", target = "Lmaki/lodcpu/Settings;min:I"),
      require = 1
   )
   private int maki$useOnlineMinimum(Settings var1) {
      return this.maki$onlineAdaptive ? Math.min(var1.min, var1.onlineMax) : var1.min;
   }
}
