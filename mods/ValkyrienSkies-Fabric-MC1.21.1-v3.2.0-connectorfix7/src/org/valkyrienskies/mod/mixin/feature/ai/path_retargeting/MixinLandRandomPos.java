package org.valkyrienskies.mod.mixin.feature.ai.path_retargeting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import net.minecraft.class_1314;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_5493;
import net.minecraft.class_5534;
import net.minecraft.class_5535;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_5534.class)
public class MixinLandRandomPos {
   @Inject(method = "generateRandomPosTowardDirection", at = @At("TAIL"), cancellable = true)
   private static void postGenerateRandomPosTowardDirection(
      class_1314 pathfinderMob, int i, boolean bl, class_2338 blockPos, CallbackInfoReturnable<class_2338> cir
   ) {
      if (cir.getReturnValue() == null) {
         if (pathfinderMob.method_37908() != null) {
            class_2338 blockPos3 = class_5535.method_31537(pathfinderMob, i, pathfinderMob.method_59922(), blockPos);
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
      method = "getPosInDirection",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/ai/util/RandomPos;generateRandomPos(Lnet/minecraft/world/entity/PathfinderMob;Ljava/util/function/Supplier;)Lnet/minecraft/world/phys/Vec3;"
      )
   )
   private static class_243 redirectGetPosInDirection(class_1314 arg, Supplier<class_2338> supplier, Operation<class_243> original) {
      class_243 result = (class_243)original.call(new Object[]{arg, supplier});
      return result != null ? VSGameUtilsKt.toWorldCoordinates(arg.method_37908(), result) : null;
   }

   @Inject(
      method = "getPos(Lnet/minecraft/world/entity/PathfinderMob;IILjava/util/function/ToDoubleFunction;)Lnet/minecraft/world/phys/Vec3;",
      at = @At("TAIL"),
      cancellable = true
   )
   private static void preGetPos(class_1314 pathfinderMob, int i, int j, ToDoubleFunction<class_2338> toDoubleFunction, CallbackInfoReturnable<class_243> cir) {
      if (cir.getReturnValue() == null) {
         boolean bl = class_5493.method_31517(pathfinderMob, i);
         class_243 randomPos = class_5535.method_31543(() -> {
            class_2338 blockPos = class_5535.method_31541(pathfinderMob.method_59922(), i, j);
            class_2338 blockPos2 = class_5534.method_31532(pathfinderMob, i, bl, blockPos);
            return blockPos2 == null ? null : class_5534.method_31533(pathfinderMob, blockPos2);
         }, toDoubleFunction);
         if (randomPos != null) {
            cir.setReturnValue(VSGameUtilsKt.toWorldCoordinates(pathfinderMob.method_37908(), randomPos));
         }
      }
   }
}
