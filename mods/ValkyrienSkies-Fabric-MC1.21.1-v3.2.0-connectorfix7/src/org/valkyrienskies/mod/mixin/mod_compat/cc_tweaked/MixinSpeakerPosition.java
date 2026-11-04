package org.valkyrienskies.mod.mixin.mod_compat.cc_tweaked;

import dan200.computercraft.shared.peripheral.speaker.SpeakerPosition;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Pseudo
@Mixin(value = SpeakerPosition.class, priority = 2000)
public abstract class MixinSpeakerPosition {
   @Shadow
   public abstract class_1937 level();

   @Redirect(method = "withinDistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
   public double withinDistance$distanceToSqr(class_243 position, class_243 other) {
      Ship ship = VSGameUtilsKt.getShipManagingPos(this.level(), position);
      return ship != null
         ? VSGameUtilsKt.squaredDistanceBetweenInclShips(
            this.level(), position.field_1352, position.field_1351, position.field_1350, other.field_1352, other.field_1351, other.field_1350
         )
         : position.method_1025(other);
   }
}
