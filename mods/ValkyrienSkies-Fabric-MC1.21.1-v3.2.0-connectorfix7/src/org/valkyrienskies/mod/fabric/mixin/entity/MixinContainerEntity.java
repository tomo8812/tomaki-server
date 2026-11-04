package org.valkyrienskies.mod.fabric.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.class_1263;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_3908;
import net.minecraft.class_7265;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_7265.class)
public interface MixinContainerEntity extends class_1263, class_3908 {
   @Shadow
   boolean method_31481();

   @Shadow
   class_243 method_19538();

   @WrapMethod(method = "isChestVehicleStillValid")
   default boolean isChestVehicleStillValid(class_1657 player, Operation<Boolean> original) {
      return original.call(new Object[]{player})
         ? true
         : VSGameUtilsKt.squaredDistanceToInclShips(player, this.method_19538().field_1352, this.method_19538().field_1351, this.method_19538().field_1350)
            <= 64.0;
   }
}
