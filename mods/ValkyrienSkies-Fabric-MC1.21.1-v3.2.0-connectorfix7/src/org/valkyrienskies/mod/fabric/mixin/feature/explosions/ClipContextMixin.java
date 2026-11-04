package org.valkyrienskies.mod.fabric.mixin.feature.explosions;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1297;
import net.minecraft.class_3726;
import net.minecraft.class_3959;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(class_3959.class)
public class ClipContextMixin {
   @WrapOperation(
      method = "<init>(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;Lnet/minecraft/world/entity/Entity;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/phys/shapes/CollisionContext;of(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/shapes/CollisionContext;"
      )
   )
   private static class_3726 collisionContextRedirect(class_1297 entity, Operation<class_3726> operation) {
      return entity == null ? class_3726.method_16194() : (class_3726)operation.call(new Object[]{entity});
   }
}
