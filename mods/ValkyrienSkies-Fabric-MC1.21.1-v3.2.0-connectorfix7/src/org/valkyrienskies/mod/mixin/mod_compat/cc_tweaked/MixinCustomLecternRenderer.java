package org.valkyrienskies.mod.mixin.mod_compat.cc_tweaked;

import dan200.computercraft.client.render.CustomLecternRenderer;
import dan200.computercraft.shared.lectern.CustomLecternBlockEntity;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Pseudo
@Mixin(CustomLecternRenderer.class)
public class MixinCustomLecternRenderer {
   @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;closerThan(Lnet/minecraft/core/Position;D)Z"))
   public boolean render$closerThan(class_243 origin, class_2374 pos, double dist, CustomLecternBlockEntity lectern) {
      double distSqr = VSGameUtilsKt.squaredDistanceBetweenInclShips(
         lectern.method_10997(), origin.field_1352, origin.field_1351, origin.field_1350, pos.method_10216(), pos.method_10214(), pos.method_10215()
      );
      return distSqr < dist * dist;
   }
}
