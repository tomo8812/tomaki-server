package org.valkyrienskies.mod.mixin.feature.ai.goal.villagers;

import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.class_1309;
import net.minecraft.class_1646;
import net.minecraft.class_1937;
import net.minecraft.class_4106;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_4106.class)
public class MixinInteractWith {
   @Unique
   private static final int VS$SHIP_SOCIAL_PERCENT = 30;
   @Unique
   private static final long VS$SOCIAL_WINDOW_TICKS = 600L;

   @Inject(
      method = "method_46959(Lnet/minecraft/world/entity/LivingEntity;ILjava/util/function/Predicate;Lnet/minecraft/world/entity/LivingEntity;)Z",
      at = @At("HEAD"),
      cancellable = true,
      require = 1
   )
   private static void vs$dampShipSocialize(
      class_1309 self, int maxDistSqr, Predicate<?> userPredicate, class_1309 candidate, CallbackInfoReturnable<Boolean> cir
   ) {
      if (self instanceof class_1646) {
         class_1937 level = self.method_37908();
         if (!level.method_8608()) {
            if (self instanceof IEntityDraggingInformationProvider sp && sp.getDraggingInformation().isEntityBeingDraggedByAShip()) {
               long window = level.method_8510() / 600L;
               if (!vs$isSocialThisWindow(self.method_5667(), window)) {
                  cir.setReturnValue(false);
               } else {
                  if (candidate instanceof class_1646 && !vs$isSocialThisWindow(candidate.method_5667(), window)) {
                     cir.setReturnValue(false);
                  }
               }
            }
         }
      }
   }

   @Unique
   private static boolean vs$isSocialThisWindow(UUID id, long window) {
      long h = id.getMostSignificantBits() ^ id.getLeastSignificantBits() ^ window * -7046029254386353131L;
      h ^= h >>> 32;
      return Math.floorMod((int)h, 100) < 30;
   }
}
