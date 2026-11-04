package org.valkyrienskies.mod.mixin.feature.drag_standing_mobs;

import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_1309.class)
public abstract class MixinLivingEntity {
   @Inject(method = "tick", at = @At("TAIL"), require = 1)
   private void vs$keepStandingMobShipStamped(CallbackInfo ci) {
      class_1309 self = (class_1309)this;
      class_1937 level = self.method_37908();
      if (!level.method_8608()) {
         if (!(self instanceof class_1657)) {
            if (self instanceof IEntityDraggingInformationProvider provider) {
               if (VSGameUtilsKt.getShipObjectWorld(level).getAllShips().size() != 0) {
                  Ship ship = this.vs$findShipStandingOn(self, level);
                  if (ship != null) {
                     EntityDraggingInformation info = provider.getDraggingInformation();
                     Long current = info.getLastShipStoodOn();
                     if (current == null || !current.equals(ship.getId())) {
                        info.setLastShipStoodOn(ship.getId());
                     }

                     info.setTicksSinceStoodOnShip(0);
                  }
               }
            }
         }
      }
   }

   @Unique
   private Ship vs$findShipStandingOn(class_1309 self, class_1937 level) {
      double radius = 0.5;
      double gx = self.method_23317();
      double gy = self.method_5829().field_1322 - 0.5;
      double gz = self.method_23321();
      Vector3dc global = new Vector3d(gx, gy, gz);
      AABBdc testAABB = new AABBd(gx - 0.5, gy - 0.5, gz - 0.5, gx + 0.5, gy + 0.5, gz + 0.5);

      for (Ship ship : VSGameUtilsKt.getShipsIntersecting(level, testAABB)) {
         Vector3dc local = ship.getTransform().getWorldToShip().transformPosition(global, new Vector3d());
         class_2338 blockPos = class_2338.method_49637(Math.floor(local.x()), Math.floor(local.y()), Math.floor(local.z()));
         if (!level.method_8320(blockPos).method_26215()) {
            return ship;
         }

         Vector3dc localBelow = ship.getTransform().getWorldToShip().transformPosition(new Vector3d(gx, gy - 1.0, gz));
         class_2338 blockPosBelow = class_2338.method_49637(Math.round(localBelow.x()), Math.round(localBelow.y()), Math.round(localBelow.z()));
         if (!level.method_8320(blockPosBelow).method_26215()) {
            return ship;
         }
      }

      return null;
   }
}
