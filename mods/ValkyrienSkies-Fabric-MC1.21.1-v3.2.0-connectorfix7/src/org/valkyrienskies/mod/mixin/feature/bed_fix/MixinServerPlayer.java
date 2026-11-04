package org.valkyrienskies.mod.mixin.feature.bed_fix;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_3222.class)
public abstract class MixinServerPlayer extends class_1297 {
   public MixinServerPlayer(class_1299<?> entityType, class_1937 level) {
      super(entityType, level);
   }

   @Inject(at = @At("TAIL"), method = "isReachableBedBlock", cancellable = true)
   private void isReachableBedBlock(class_2338 blockPos, CallbackInfoReturnable<Boolean> cir) {
      if (!(Boolean)cir.getReturnValue()) {
         class_243 vec3 = class_243.method_24955(blockPos);
         double origX = vec3.field_1352;
         double origY = vec3.field_1351;
         double origZ = vec3.field_1350;
         VSGameUtilsKt.transformToNearbyShipsAndWorld(
            this.method_37908(),
            origX,
            origY,
            origZ,
            1.0,
            (x, y, z) -> cir.setReturnValue(
               Math.abs(this.method_23317() - x) <= 3.0 && Math.abs(this.method_23318() - y) <= 2.0 && Math.abs(this.method_23321() - z) <= 3.0
            )
         );
      }
   }
}
