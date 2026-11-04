package org.valkyrienskies.mod.mixin.feature.thrown_velocity;

import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_1297.class)
public class MixinEntity {
   @Shadow
   private class_1937 field_6002;

   @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
   private void setItemDragged(class_1799 itemStack, float f, CallbackInfoReturnable<class_1542> cir) {
      class_1542 result = (class_1542)cir.getReturnValue();
      if (result != null) {
         Ship ship = VSGameUtilsKt.getShipMountedTo(class_1297.class.cast(this));
         EntityDraggingInformation info = ((IEntityDraggingInformationProvider)this).getDraggingInformation();
         if (ship == null && info.isEntityBeingDraggedByAShip()) {
            ship = ValkyrienSkies.getShipById(this.field_6002, info.getLastShipStoodOn());
         }

         ((IEntityDraggingInformationProvider)result).vs$dragImmediately(ship);
      }
   }
}
