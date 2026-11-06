package maki.fndseat.mixin;

import maki.fndseat.ShipFurnitureSeats;
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
    private void maki$checkFurniture(CallbackInfo ci) {
        ShipFurnitureSeats.tick((ShipMountingEntity) (Object) this);
    }

    @Inject(method = "getDismountLocationForPassenger(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/phys/Vec3;",
        at = @At("HEAD"), cancellable = true, remap = false)
    private void maki$furnitureDismount(LivingEntity passenger, CallbackInfoReturnable<Vec3> cir) {
        Vec3 location = ShipFurnitureSeats.dismount((ShipMountingEntity) (Object) this, passenger);
        if (location != null) cir.setReturnValue(location);
    }

    @Inject(method = "remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V", at = @At("RETURN"), remap = false)
    private void maki$releaseFurniture(RemovalReason reason, CallbackInfo ci) {
        ShipFurnitureSeats.remove((ShipMountingEntity) (Object) this);
    }
}
