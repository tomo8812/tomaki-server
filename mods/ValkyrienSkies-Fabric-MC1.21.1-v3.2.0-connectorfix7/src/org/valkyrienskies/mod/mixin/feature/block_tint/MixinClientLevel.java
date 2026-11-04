package org.valkyrienskies.mod.mixin.feature.block_tint;

import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_638;
import net.minecraft.class_6539;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.config.VSGameConfig;

@Mixin(class_638.class)
public abstract class MixinClientLevel {
   @ModifyVariable(
      method = "calculateBlockTint(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I",
      ordinal = 0,
      at = @At("HEAD"),
      argsOnly = true
   )
   private class_2338 fixBlockPos(class_2338 old) {
      if (!VSGameConfig.CLIENT.getBlockTinting().getFixBlockTinting()) {
         return old;
      }

      Vector3d newPos = VSGameUtilsKt.toWorldCoordinates(
         (class_1937)class_638.class.cast(this), new Vector3d(old.method_10263(), old.method_10264(), old.method_10260())
      );
      return class_2338.method_49637(newPos.x, newPos.y, newPos.z);
   }

   @Inject(method = "getBlockTint(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I", at = @At("HEAD"), cancellable = true)
   public void getBlockTint(class_2338 blockPos, class_6539 colorResolver, CallbackInfoReturnable<Integer> cir) {
      if (VSGameConfig.CLIENT.getBlockTinting().getFixBlockTinting() && VSGameUtilsKt.isBlockInShipyard((class_1937)class_638.class.cast(this), blockPos)) {
         cir.setReturnValue(class_638.class.cast(this).method_23780(blockPos, colorResolver));
      }
   }
}
