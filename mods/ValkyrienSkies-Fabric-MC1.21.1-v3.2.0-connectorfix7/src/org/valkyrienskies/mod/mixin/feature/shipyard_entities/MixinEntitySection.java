package org.valkyrienskies.mod.mixin.feature.shipyard_entities;

import net.minecraft.class_238;
import net.minecraft.class_5572;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.hooks.VSGameEvents;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.mixinducks.world.OfShip;

@Mixin(class_5572.class)
public class MixinEntitySection implements OfShip {
   @Unique
   private Ship ofShip;

   @Override
   public Ship getShip() {
      return this.ofShip;
   }

   @Override
   public void setShip(Ship ship) {
      this.ofShip = ship;
      if (ship != null) {
         VSGameEvents.INSTANCE.getEntitySectionSetShip().emit(new VSGameEvents.EntitySectionSetShip(class_5572.class.cast(this), ship));
      }
   }

   @ModifyVariable(
      method = "getEntities(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)Lnet/minecraft/util/AbortableIterationConsumer$Continuation;",
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   class_238 modifyAABB1(class_238 aabb) {
      return this.ofShip != null ? VectorConversionsMCKt.toMinecraft(VectorConversionsMCKt.toJOML(aabb).transform(this.ofShip.getWorldToShip())) : aabb;
   }

   @ModifyVariable(
      method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)Lnet/minecraft/util/AbortableIterationConsumer$Continuation;",
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   class_238 modifyAABB2(class_238 aabb) {
      return this.ofShip != null ? VectorConversionsMCKt.toMinecraft(VectorConversionsMCKt.toJOML(aabb).transform(this.ofShip.getWorldToShip())) : aabb;
   }
}
