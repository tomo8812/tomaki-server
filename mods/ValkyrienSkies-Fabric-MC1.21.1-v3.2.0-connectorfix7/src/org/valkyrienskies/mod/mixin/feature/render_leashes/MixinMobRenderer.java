package org.valkyrienskies.mod.mixin.feature.render_leashes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_897;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_897.class)
public class MixinMobRenderer {
   @WrapOperation(
      method = "renderLeash",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getRopeHoldPosition(F)Lnet/minecraft/world/phys/Vec3;")
   )
   public class_243 getRopeHoldPosition(class_1297 instance, float partialTicks, Operation<class_243> getRopeHoldPosition) {
      class_243 origVec = (class_243)getRopeHoldPosition.call(new Object[]{instance, partialTicks});
      Vector3d vec = VectorConversionsMCKt.toJOML(origVec);
      LoadedShip ship = VSGameUtilsKt.getShipObjectManagingPos(instance.method_37908(), vec);
      if (ship != null) {
         ship.getShipToWorld().transformPosition(vec);
         return VectorConversionsMCKt.toMinecraft(vec);
      } else {
         return origVec;
      }
   }
}
