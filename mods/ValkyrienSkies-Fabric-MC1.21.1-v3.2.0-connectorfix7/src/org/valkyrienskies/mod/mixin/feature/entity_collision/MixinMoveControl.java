package org.valkyrienskies.mod.mixin.feature.entity_collision;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1308;
import net.minecraft.class_1335;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2350.class_2351;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_1335.class)
public class MixinMoveControl {
   @Shadow
   @Final
   protected class_1308 field_6371;
   @Unique
   private static final double VS$STEP_THRESHOLD = 0.5;

   @WrapOperation(
      method = "tick",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
      )
   )
   private class_265 vs$insertShipCollisions(class_2680 instance, class_1922 blockGetter, class_2338 blockPos, Operation<class_265> original) {
      class_265 originalShape = (class_265)original.call(new Object[]{instance, this.field_6371.method_37908(), blockPos});
      if (!originalShape.method_1110()) {
         return originalShape;
      }

      double cx = blockPos.method_10263() + 0.5;
      double cy = blockPos.method_10264() + 0.5;
      double cz = blockPos.method_10260() + 0.5;
      AABBdc cellAABB = new AABBd(cx - 0.5, cy - 0.5, cz - 0.5, cx + 0.5, cy + 0.5, cz + 0.5);

      for (Ship ship : VSGameUtilsKt.getShipsIntersecting(this.field_6371.method_37908(), cellAABB)) {
         Vector3dc cellShip = ship.getTransform().getWorldToShip().transformPosition(new Vector3d(cx, cy, cz), new Vector3d());
         class_2338 shipPos = class_2338.method_49637(cellShip.x(), cellShip.y(), cellShip.z());
         class_2680 shipState = this.field_6371.method_37908().method_8320(shipPos);
         if (!shipState.method_26215()) {
            class_265 shipShape = shipState.method_26220(this.field_6371.method_37908(), shipPos);
            if (!shipShape.method_1110()) {
               double deckTopShipY = shipPos.method_10264() + shipShape.method_1105(class_2351.field_11052);
               Vector3dc feetShip = ship.getTransform()
                  .getWorldToShip()
                  .transformPosition(
                     new Vector3d(this.field_6371.method_23317(), this.field_6371.method_23318(), this.field_6371.method_23321()), new Vector3d()
                  );
               double delta = deckTopShipY - feetShip.y();
               if (delta > 0.5) {
                  return shipShape;
               }
            }
         }
      }

      return originalShape;
   }
}
