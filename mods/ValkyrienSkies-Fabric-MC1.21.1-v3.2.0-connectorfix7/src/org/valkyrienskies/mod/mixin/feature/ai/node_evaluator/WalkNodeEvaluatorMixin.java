package org.valkyrienskies.mod.mixin.feature.ai.node_evaluator;

import net.minecraft.class_14;
import net.minecraft.class_2338;
import net.minecraft.class_8;
import net.minecraft.class_9;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.config.VSGameConfig;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_14.class)
public abstract class WalkNodeEvaluatorMixin extends class_8 {
   private static final int VS$MAX_DECK_DROP = 4;

   @Inject(method = "getStart()Lnet/minecraft/world/level/pathfinder/Node;", at = @At("HEAD"), cancellable = true)
   private void vs$anchorStartOnDeck(CallbackInfoReturnable<class_9> cir) {
      if (VSGameConfig.SERVER.getAiOnShips()) {
         if (this.field_33 instanceof IEntityDraggingInformationProvider dragProvider && dragProvider.getDraggingInformation().isEntityBeingDraggedByAShip()) {
            class_2338 feet = this.field_33.method_24515();
            int x = feet.method_10263();
            int z = feet.method_10260();
            class_2338 anchor = feet;
            if (!this.field_49416.method_57623(anchor).method_26215()) {
               for (int up = 1; up <= 4; up++) {
                  class_2338 above = new class_2338(x, feet.method_10264() + up, z);
                  if (this.field_49416.method_57623(above).method_26215()) {
                     anchor = above;
                     break;
                  }
               }
            } else if (this.field_49416.method_57623(new class_2338(x, feet.method_10264() - 1, z)).method_26215()) {
               for (int dy = 1; dy <= 4; dy++) {
                  class_2338 below = new class_2338(x, feet.method_10264() - dy, z);
                  if (!this.field_49416.method_57623(below).method_26215()) {
                     anchor = new class_2338(x, feet.method_10264() - dy + 1, z);
                     break;
                  }
               }
            }

            class_9 node = this.method_27137(anchor);
            if (node != null) {
               cir.setReturnValue(node);
            }
         }
      }
   }

   @Inject(method = "tryFindFirstGroundNodeBelow(III)Lnet/minecraft/world/level/pathfinder/Node;", at = @At("HEAD"), cancellable = true, require = 1)
   private void vs$clampDeckDropExploration(int x, int y, int z, CallbackInfoReturnable<class_9> cir) {
      if (VSGameConfig.SERVER.getAiOnShips()) {
         if (this.field_33 instanceof IEntityDraggingInformationProvider dragProvider && dragProvider.getDraggingInformation().isEntityBeingDraggedByAShip()) {
            for (int dy = 1; dy <= 4; dy++) {
               int groundY = y - dy;
               if (!this.field_49416.method_57623(new class_2338(x, groundY, z)).method_26215()) {
                  class_9 node = this.method_13(x, groundY + 1, z);
                  if (node != null) {
                     cir.setReturnValue(node);
                  }

                  return;
               }
            }
         }
      }
   }
}
