package net.caffeinemc.mods.lithium.mixin.math.sine_lut;

import net.caffeinemc.mods.lithium.common.util.math.CompactSineLUT;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mth.class)
public class MthMixin {
   @Shadow
   @Final
   @Mutable
   public static float[] SIN;

   @Inject(method = "<clinit>", at = @At("RETURN"))
   private static void onClassInit(CallbackInfo ci) {
      CompactSineLUT.init();
      SIN = null;
   }

   @Overwrite
   public static float sin(float f) {
      return CompactSineLUT.sin(f);
   }

   @Overwrite
   public static float cos(float f) {
      return CompactSineLUT.cos(f);
   }
}
