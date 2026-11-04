package org.valkyrienskies.mod.mixin.feature.shipyard_entities;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_5582;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.mixinducks.world.OfLevel;

@Mixin(class_638.class)
public class MixinClientLevel {
   @Shadow
   @Final
   private class_5582<class_1297> field_27734;

   @Inject(method = "<init>", at = @At("RETURN"))
   void configureEntitySections(CallbackInfo ci) {
      ((OfLevel)this.field_27734).setLevel((class_1937)class_638.class.cast(this));
   }
}
