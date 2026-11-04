package org.valkyrienskies.mod.mixin.feature.submarines;

import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_3610;
import net.minecraft.class_3621;
import net.minecraft.class_5819;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_3621.class)
public class MixinWaterFluid {
   @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
   private void preAnimateTick(class_1937 level, class_2338 blockPos, class_3610 fluidState, class_5819 randomSource, CallbackInfo ci) {
      if (!ValkyrienSkies.isBlockInShipyard(level, blockPos)) {
         for (Ship ship : ValkyrienSkies.getShipsIntersecting(level, blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260())) {
            Vector3dc posInShip = ship.getWorldToShip().transformPosition(VectorConversionsMCKt.toJOMLD(blockPos));
            class_2338 blockPosInShip = class_2338.method_49638(VectorConversionsMCKt.toMinecraft(posInShip));
            if (VSGameUtilsKt.isPositionMaybeSealed(level, blockPosInShip) && ValkyrienSkies.isConnectivityEnabled(level.field_9236)) {
               ci.cancel();
               return;
            }
         }
      }
   }
}
