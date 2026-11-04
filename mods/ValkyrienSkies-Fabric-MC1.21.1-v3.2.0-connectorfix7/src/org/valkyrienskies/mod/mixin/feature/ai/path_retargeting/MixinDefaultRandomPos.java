package org.valkyrienskies.mod.mixin.feature.ai.path_retargeting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Supplier;
import net.minecraft.class_1314;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_5493;
import net.minecraft.class_5532;
import net.minecraft.class_5535;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_5532.class)
public class MixinDefaultRandomPos {
   @Inject(method = "generateRandomPosTowardDirection", at = @At("TAIL"), cancellable = true)
   private static void postGenerateRandomPosTowardDirection(
      class_1314 pathfinderMob, int i, boolean bl, class_2338 blockPos, CallbackInfoReturnable<class_2338> cir
   ) {
      if (cir.getReturnValue() == null) {
         if (pathfinderMob.method_37908() != null) {
            class_2338 blockPos3 = class_5535.method_31537(pathfinderMob, i, pathfinderMob.method_59922(), blockPos);
            if (blockPos3 == null) {
               return;
            }

            class_238 checker = new class_238(blockPos3);
            Iterable<LoadedShip> ships = VSGameUtilsKt.getShipObjectWorld(pathfinderMob.method_37908())
               .getLoadedShips()
               .getIntersecting(VectorConversionsMCKt.toJOML(checker), VSGameUtilsKt.getDimensionId(pathfinderMob.method_37908()));
            if (ships.iterator().hasNext()) {
               for (LoadedShip ship : ships) {
                  Vector3d posInShip = ship.getWorldToShip().transformPosition(VectorConversionsMCKt.toJOMLD(blockPos3), new Vector3d());
                  class_2338 blockPosInShip = class_2338.method_49638(VectorConversionsMCKt.toMinecraft(posInShip));
                  if (!class_5493.method_31521(bl, pathfinderMob, blockPosInShip)
                     && !class_5493.method_31519(pathfinderMob.method_5942(), blockPosInShip)
                     && !class_5493.method_31522(pathfinderMob, blockPosInShip)) {
                     cir.setReturnValue(blockPosInShip);
                     break;
                  }
               }
            }
         }
      }
   }

   @WrapOperation(
      method = "getPosTowards",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/ai/util/RandomPos;generateRandomPos(Lnet/minecraft/world/entity/PathfinderMob;Ljava/util/function/Supplier;)Lnet/minecraft/world/phys/Vec3;"
      )
   )
   private static class_243 redirectGetPosInDirection(class_1314 arg, Supplier<class_2338> supplier, Operation<class_243> original) {
      class_243 result = (class_243)original.call(new Object[]{arg, supplier});
      return result != null ? VSGameUtilsKt.toWorldCoordinates(arg.method_37908(), result) : null;
   }

   @WrapOperation(
      method = "getPos",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/ai/util/RandomPos;generateRandomPos(Lnet/minecraft/world/entity/PathfinderMob;Ljava/util/function/Supplier;)Lnet/minecraft/world/phys/Vec3;"
      )
   )
   private static class_243 redirectGetPos(class_1314 arg, Supplier<class_2338> supplier, Operation<class_243> original) {
      class_243 result = (class_243)original.call(new Object[]{arg, supplier});
      return result != null ? VSGameUtilsKt.toWorldCoordinates(arg.method_37908(), result) : null;
   }
}
