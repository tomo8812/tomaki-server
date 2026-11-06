package maki.fndseat.mixin;

import maki.fndseat.SeatCompat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.phys.Vec3;
import net.satisfy.foundation.seat.ChairEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChairEntity.class, remap = false)
public abstract class FoundationChairMixin {
   @Inject(
      method = "getDismountLocationForPassenger(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/phys/Vec3;",
      at = @At("HEAD"),
      cancellable = true,
      remap = false
   )
   private void maki$dismountOnShip(LivingEntity passenger, CallbackInfoReturnable<Vec3> cir) {
      Entity chair = (Entity)this;
      Vec3 location = SeatCompat.dismount(chair, passenger);
      if (location != null) {
         chair.discard();
         cir.setReturnValue(location);
      }
   }

   @Inject(method = "remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V", at = @At("HEAD"), remap = false)
   private void maki$removeSeatAnchor(RemovalReason reason, CallbackInfo ci) {
      SeatCompat.remove((Entity)this);
   }
}
