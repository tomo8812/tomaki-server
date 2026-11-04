package org.valkyrienskies.mod.mixin.feature.entity_collision;

import net.minecraft.class_1313;
import net.minecraft.class_1656;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.world.ShipWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDragger;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_1657.class)
public abstract class MixinPlayer implements IEntityDraggingInformationProvider {
   @Shadow
   @Final
   private class_1656 field_7503;

   @Shadow
   protected abstract boolean method_21825();

   @Inject(method = "maybeBackOffFromEdge", at = @At("HEAD"), cancellable = true)
   private void preMaybeBackOffFromEdge(class_243 vec3, class_1313 moverType, CallbackInfoReturnable<class_243> callbackInfoReturnable) {
      if (this.getDraggingInformation().isEntityBeingDraggedByAShip()
         && this.getDraggingInformation().getLastShipStoodOn() != null
         && this.getDraggingInformation().getTicksSinceStoodOnShip() < 1) {
         class_1657 player = (class_1657)this;
         class_1937 level = player.method_37908();
         if (level != null) {
            ShipWorld shipWorld = VSGameUtilsKt.getShipObjectWorld(level);
            if (shipWorld == null) {
               return;
            }

            Ship ship = shipWorld.getLoadedShips().getById(this.getDraggingInformation().getLastShipStoodOn());
            if (ship == null) {
               return;
            }

            if (vec3.field_1351 <= 0.0
               && (moverType == class_1313.field_6308 || moverType == class_1313.field_6305)
               && this.method_21825()
               && !this.field_7503.field_7479) {
               class_243 adjustedVec = EntityDragger.backOff(vec3, ship, player, level);
               callbackInfoReturnable.setReturnValue(adjustedVec);
            }
         }
      }
   }
}
