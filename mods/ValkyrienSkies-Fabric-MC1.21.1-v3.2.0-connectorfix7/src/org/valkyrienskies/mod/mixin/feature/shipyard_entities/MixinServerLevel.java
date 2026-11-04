package org.valkyrienskies.mod.mixin.feature.shipyard_entities;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_3218;
import net.minecraft.class_5579;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.mixinducks.world.OfLevel;

@Mixin(class_3218.class)
public class MixinServerLevel {
   @Shadow
   @Final
   private class_5579<class_1297> field_26935;

   @Inject(method = "<init>", at = @At("RETURN"))
   void configureEntitySections(CallbackInfo ci) {
      ((OfLevel)this.field_26935).setLevel((class_1937)class_3218.class.cast(this));
   }
}
