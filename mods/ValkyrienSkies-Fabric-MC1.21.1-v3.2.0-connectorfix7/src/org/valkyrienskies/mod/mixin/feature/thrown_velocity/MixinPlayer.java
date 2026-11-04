package org.valkyrienskies.mod.mixin.feature.thrown_velocity;

import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_1657.class)
public abstract class MixinPlayer extends class_1309 {
   protected MixinPlayer(class_1299<? extends class_1309> entityType, class_1937 level) {
      super(entityType, level);
   }

   @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
   private void shipVelocityToThrownItem(class_1799 itemStack, boolean bl, boolean bl2, CallbackInfoReturnable<class_1542> cir) {
      class_1542 result = (class_1542)cir.getReturnValue();
      if (result != null) {
         Ship ship = VSGameUtilsKt.getShipMountedTo(this);
         EntityDraggingInformation info = ((IEntityDraggingInformationProvider)this).getDraggingInformation();
         if (ship == null && info.isEntityBeingDraggedByAShip()) {
            ship = ValkyrienSkies.getShipById(this.method_37908(), info.getLastShipStoodOn());
         }

         ((IEntityDraggingInformationProvider)result).vs$dragImmediately(ship);
      }
   }
}
