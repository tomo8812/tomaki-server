package org.valkyrienskies.mod.mixin.feature.huge_bounding_box_fix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_1657.class)
public abstract class MixinPlayer extends class_1309 {
   protected MixinPlayer(class_1299<? extends class_1309> entityType, class_1937 level) {
      super(entityType, level);
   }

   @ModifyExpressionValue(
      method = "aiStep",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBoundingBox()Lnet/minecraft/world/phys/AABB;")
   )
   private class_238 transformBoundingBoxToWorld(class_238 aabb) {
      return VSGameUtilsKt.transformAabbToWorld(this.method_37908(), aabb);
   }
}
