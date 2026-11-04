package org.valkyrienskies.mod.mixin.feature.bed_fix;

import java.util.Optional;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3222.class_9773;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_3222.class)
public abstract class MixinPlayer {
   @Inject(method = "findRespawnAndUseSpawnBlock", at = @At("RETURN"), cancellable = true, require = 1)
   private static void valkyrienskies$transformShipBedRespawn(
      class_3218 serverLevel, class_2338 blockPos, float f, boolean bl, boolean bl2, CallbackInfoReturnable<Optional<class_9773>> cir
   ) {
      if (!((Optional)cir.getReturnValue()).isEmpty()) {
         if (VSGameUtilsKt.isBlockInShipyard(serverLevel, blockPos)) {
            ServerShip ship = VSGameUtilsKt.getShipManagingPos(serverLevel, blockPos);
            if (ship == null) {
               cir.setReturnValue(Optional.empty());
            } else {
               class_9773 current = (class_9773)((Optional)cir.getReturnValue()).get();
               class_243 shipyardPos = current.comp_2811();
               Vector3d worldPosVec = new Vector3d(shipyardPos.field_1352, shipyardPos.field_1351, shipyardPos.field_1350);
               ship.getShipToWorld().transformPosition(worldPosVec);
               class_243 worldPos = new class_243(worldPosVec.x, worldPosVec.y, worldPosVec.z);
               Vector3d worldBedVec = new Vector3d(blockPos.method_10263() + 0.5, blockPos.method_10264() + 0.5, blockPos.method_10260() + 0.5);
               ship.getShipToWorld().transformPosition(worldBedVec);
               class_2338 worldBedPos = class_2338.method_49637(worldBedVec.x, worldBedVec.y, worldBedVec.z);
               cir.setReturnValue(Optional.of(class_9773.method_60595(worldPos, worldBedPos)));
            }
         }
      }
   }
}
