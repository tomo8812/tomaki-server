package maki.airship.mixin;

import maki.airship.ShipFurnitureSeats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

@Mixin(value = ShipMountingEntity.class, remap = false)
public abstract class FurnitureVehicleMixin {
   @Inject(method = "tick()V", at = @At("HEAD"), remap = false)
   private void maki$checkFurniture(CallbackInfo var1) {
      ShipFurnitureSeats.tick((ShipMountingEntity)this);
   }

   @Inject(
      method = "getDismountLocationForPassenger(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/phys/Vec3;",
      at = @At("HEAD"),
      cancellable = true,
      remap = false
   )
   private void maki$furnitureDismount(LivingEntity var1, CallbackInfoReturnable<Vec3> var2) {
      Vec3 var3 = ShipFurnitureSeats.dismount((ShipMountingEntity)this, var1);
      if (var3 != null) {
         var2.setReturnValue(var3);
      }
   }

   @Inject(method = "remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V", at = @At("RETURN"), remap = false)
   private void maki$releaseFurniture(RemovalReason var1, CallbackInfo var2) {
      ShipFurnitureSeats.remove((ShipMountingEntity)this);
   }
}
