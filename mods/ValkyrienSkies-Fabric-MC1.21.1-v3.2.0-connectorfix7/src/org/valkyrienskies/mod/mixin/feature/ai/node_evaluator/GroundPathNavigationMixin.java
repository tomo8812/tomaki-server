package org.valkyrienskies.mod.mixin.feature.ai.node_evaluator;

import com.google.common.collect.ImmutableSet;
import net.minecraft.class_11;
import net.minecraft.class_1308;
import net.minecraft.class_1408;
import net.minecraft.class_1409;
import net.minecraft.class_2338;
import net.minecraft.class_4076;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.config.VSGameConfig;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_1409.class)
public abstract class GroundPathNavigationMixin {
   @Inject(method = "createPath(Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/world/level/pathfinder/Path;", at = @At("HEAD"), cancellable = true, require = 1)
   private void vs$skipSurfaceDescentForShipMobs(class_2338 target, int accuracy, CallbackInfoReturnable<class_11> cir) {
      if (VSGameConfig.SERVER.getAiOnShips()) {
         class_1308 mob = ((PathNavigationMobAccessor)this).getMob();
         if (mob instanceof IEntityDraggingInformationProvider dragProvider && dragProvider.getDraggingInformation().isEntityBeingDraggedByAShip()) {
            if (mob.method_37908().method_8398().method_21730(class_4076.method_18675(target.method_10263()), class_4076.method_18675(target.method_10260()))
               == null) {
               cir.setReturnValue(null);
            } else {
               cir.setReturnValue(((class_1408)this).method_29934(ImmutableSet.of(target), accuracy));
            }
         }
      }
   }
}
