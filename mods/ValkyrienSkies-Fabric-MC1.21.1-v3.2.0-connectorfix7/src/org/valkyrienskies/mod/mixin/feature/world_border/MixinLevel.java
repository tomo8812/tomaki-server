package org.valkyrienskies.mod.mixin.feature.world_border;

import net.minecraft.class_1937;
import net.minecraft.class_2784;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.mixinducks.world.OfLevel;

@Mixin(class_1937.class)
public class MixinLevel {
   @Shadow
   @Final
   private class_2784 field_9223;

   @Inject(at = @At("RETURN"), method = "<init>")
   private void setWorldBorderLevel(CallbackInfo ci) {
      ((OfLevel)this.field_9223).setLevel(class_1937.class.cast(this));
   }
}
