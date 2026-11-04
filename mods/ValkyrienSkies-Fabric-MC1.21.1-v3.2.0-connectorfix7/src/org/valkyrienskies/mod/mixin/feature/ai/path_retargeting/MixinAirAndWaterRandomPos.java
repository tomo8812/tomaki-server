package org.valkyrienskies.mod.mixin.feature.ai.path_retargeting;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1314;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_5493;
import net.minecraft.class_5530;
import net.minecraft.class_5535;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_5530.class)
public class MixinAirAndWaterRandomPos {
   @Inject(method = "generateRandomPos", at = @At("TAIL"), cancellable = true)
   private static void preGenerateRandomPos(
      class_1314 pathfinderMob,
      int i,
      int j,
      int k,
      double d,
      double e,
      double f,
      boolean bl,
      CallbackInfoReturnable<class_2338> cir,
      @Local(ordinal = 1) class_2338 blockPos2
   ) {
      if (cir.getReturnValue() == null) {
         if (pathfinderMob.method_37908() != null) {
            if (blockPos2 == null) {
               return;
            }

            class_238 checker = new class_238(blockPos2);
            Iterable<LoadedShip> ships = VSGameUtilsKt.getShipObjectWorld(pathfinderMob.method_37908())
               .getLoadedShips()
               .getIntersecting(VectorConversionsMCKt.toJOML(checker), VSGameUtilsKt.getDimensionId(pathfinderMob.method_37908()));
            if (ships.iterator().hasNext()) {
               for (LoadedShip ship : ships) {
                  Vector3d posInShip = ship.getWorldToShip().transformPosition(VectorConversionsMCKt.toJOMLD(blockPos2), new Vector3d());
                  class_2338 blockPosInShip = class_2338.method_49638(VectorConversionsMCKt.toMinecraft(posInShip));
                  if (!class_5493.method_31521(bl, pathfinderMob, blockPosInShip)
                     && !class_5493.method_31522(
                        pathfinderMob,
                        blockPos2 = class_5535.method_31540(
                           blockPos2, pathfinderMob.method_37908().method_31600(), arg2 -> class_5493.method_31523(pathfinderMob, arg2)
                        )
                     )) {
                     cir.setReturnValue(blockPosInShip);
                     break;
                  }
               }
            }
         }
      }
   }
}
