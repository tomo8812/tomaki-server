package org.valkyrienskies.mod.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_638;
import net.minecraft.class_897;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_897.class)
public class MixinEntityRenderer {
   @WrapOperation(
      method = "shouldRender",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBoundingBoxForCulling()Lnet/minecraft/world/phys/AABB;")
   )
   private class_238 redirectAABBConstructor(class_1297 instance, Operation<class_238> original) {
      if (instance instanceof IEntityDraggingInformationProvider dragProvider
         && dragProvider.getDraggingInformation().isEntityBeingDraggedByAShip()
         && dragProvider.getDraggingInformation().getLastShipStoodOn() != null) {
         EntityDraggingInformation dragInfo = dragProvider.getDraggingInformation();
         ClientShip ship = VSGameUtilsKt.getShipObjectWorld((class_638)instance.method_37908()).getAllShips().getById(dragInfo.getLastShipStoodOn());
         if (ship == null) {
            return (class_238)original.call(new Object[]{instance});
         }

         if (dragInfo.getLastShipStoodOn() != null && (dragInfo.getRelativePositionOnShip() != null || dragInfo.getServerRelativePlayerPosition() != null)) {
            Vector3dc positionToTransform = dragInfo.bestRelativeEntityPosition();
            if (positionToTransform != null) {
               Vector3dc transformed = ship.getRenderTransform().getShipToWorld().transformPosition(positionToTransform, new Vector3d());
               return instance.method_18377(instance.method_18376()).method_30231(transformed.x(), transformed.y(), transformed.z()).method_1014(0.5);
            }
         }
      }

      return (class_238)original.call(new Object[]{instance});
   }
}
