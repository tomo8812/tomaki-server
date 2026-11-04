package org.valkyrienskies.mod.mixin.feature.fluid_escaping_ship_config;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2541;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import org.joml.primitives.AABBic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.config.VSGameConfig;

@Mixin(class_2541.class)
public class MixinVineBlock {
   @WrapOperation(
      method = "randomTick",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
      ),
      require = 0
   )
   boolean cancelSpread(class_3218 level, class_2338 toPos, class_2680 blockState, int i, Operation<Boolean> original) {
      if (VSGameConfig.SERVER.getPreventVinesEscapingShip() && level != null) {
         Ship ship = VSGameUtilsKt.getShipManagingPos((class_1937)level, toPos);
         if (ship != null && ship.getShipAABB() != null) {
            AABBic a = ship.getShipAABB();
            int x = toPos.method_10263();
            int y = toPos.method_10264();
            int z = toPos.method_10260();
            if (x < a.minX() || y < a.minY() || z < a.minZ() || x >= a.maxX() || y >= a.maxY() || z >= a.maxZ()) {
               return true;
            }
         }
      }

      return (Boolean)original.call(new Object[]{level, toPos, blockState, i});
   }
}
