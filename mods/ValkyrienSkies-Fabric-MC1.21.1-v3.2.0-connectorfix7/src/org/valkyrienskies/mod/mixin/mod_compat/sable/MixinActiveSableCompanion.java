package org.valkyrienskies.mod.mixin.mod_compat.sable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.ryanhcode.sable.ActiveSableCompanion;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(ActiveSableCompanion.class)
public class MixinActiveSableCompanion {
   @ModifyReturnValue(
      method = "projectOutOfSubLevel(Lnet/minecraft/world/level/Level;Lorg/joml/Vector3dc;Lorg/joml/Vector3d;)Lorg/joml/Vector3d;",
      at = @At(value = "RETURN", ordinal = 0)
   )
   private Vector3d returnVSShipToWorldPos(Vector3d original, class_1937 level) {
      Ship ship = VSGameUtilsKt.getShipManagingPos(level, original);
      return ship != null ? ship.getTransform().getShipToWorld().transformPosition(original) : original;
   }

   @ModifyReturnValue(
      method = "projectOutOfSubLevel(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/Position;)Lnet/minecraft/world/phys/Vec3;",
      at = @At(value = "RETURN", ordinal = 0)
   )
   private class_243 returnVSShipToWorldPos(class_243 original, class_1937 level) {
      Ship ship = VSGameUtilsKt.getShipManagingPos(level, original);
      return ship != null
         ? VectorConversionsMCKt.toMinecraft(ship.getTransform().getShipToWorld().transformPosition(VectorConversionsMCKt.toJOML(original)))
         : original;
   }

   @ModifyReturnValue(
      method = "getVelocity(Lnet/minecraft/world/level/Level;Lorg/joml/Vector3dc;Lorg/joml/Vector3d;)Lorg/joml/Vector3d;",
      at = @At(value = "RETURN", ordinal = 0)
   )
   private Vector3d returnShipVel(Vector3d original, class_1937 level) {
      Ship ship = VSGameUtilsKt.getShipManagingPos(level, original);
      return ship != null ? original.set(ship.getVelocity()) : original;
   }
}
