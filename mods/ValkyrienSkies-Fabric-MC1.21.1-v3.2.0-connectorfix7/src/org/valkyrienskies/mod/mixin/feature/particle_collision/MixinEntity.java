package org.valkyrienskies.mod.mixin.feature.particle_collision;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils;

@Mixin(class_1297.class)
public class MixinEntity {
   @ModifyVariable(
      method = "collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;",
      at = @At("HEAD"),
      argsOnly = true,
      index = 1
   )
   private static class_243 includeShipsForParticleCollision(
      class_243 movement,
      @Local(argsOnly = true) class_1297 entity,
      @Local(argsOnly = true) class_238 entityBoundingBox,
      @Local(argsOnly = true) class_1937 level
   ) {
      return entity == null && level.field_9236
         ? EntityShipCollisionUtils.INSTANCE.adjustEntityMovementForShipCollisions(null, movement, entityBoundingBox, level)
         : movement;
   }
}
