package org.valkyrienskies.mod.mixin.world.entity.projectile;

import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_1675;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3966;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

@Mixin(class_1675.class)
public class ProjectileUtilMixin {
   @Inject(
      at = @At("RETURN"),
      method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
      cancellable = true
   )
   private static void beforeGetEntityHitResult(
      class_1297 entity,
      class_243 vec3,
      class_243 vec32,
      class_238 aABB,
      Predicate<class_1297> predicate,
      double d,
      CallbackInfoReturnable<@Nullable class_3966> cir
   ) {
      if (cir.getReturnValue() == null && VSGameUtilsKt.getShipsIntersecting(entity.method_37908(), aABB).iterator().hasNext()) {
         cir.setReturnValue(RaycastUtilsKt.raytraceEntities(entity.method_37908(), entity, vec3, vec32, aABB, predicate, d));
      }
   }
}
