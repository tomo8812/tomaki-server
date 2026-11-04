package org.valkyrienskies.mod.mixin.feature.hit_outline;

import javax.annotation.Nullable;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3726;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_638;
import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSClientGameUtils;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_761.class)
public class MixinLevelRenderer {
   @Shadow
   @Nullable
   private class_638 field_4085;

   @Shadow
   private static void method_3291(
      class_4587 matrixStack, class_4588 vertexConsumer, class_265 voxelShape, double d, double e, double f, float red, float green, float blue, float alpha
   ) {
      throw new AssertionError();
   }

   @Inject(
      method = "renderHitOutline",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V",
         shift = Shift.BEFORE
      ),
      cancellable = true
   )
   private void preRenderShape(
      class_4587 matrixStack,
      class_4588 vertexConsumer,
      class_1297 entity,
      double camX,
      double camY,
      double camZ,
      class_2338 pos,
      class_2680 state,
      CallbackInfo ci
   ) {
      ClientShip ship = VSGameUtilsKt.getLoadedShipManagingPos(this.field_4085, pos);
      if (ship != null) {
         matrixStack.method_22903();
         VSClientGameUtils.transformRenderWithShip(ship.getRenderTransform(), matrixStack, pos, camX, camY, camZ);
         method_3291(
            matrixStack, vertexConsumer, state.method_26172(this.field_4085, pos, class_3726.method_16195(entity)), 0.0, 0.0, 0.0, 0.0F, 0.0F, 0.0F, 0.4F
         );
         matrixStack.method_22909();
         ci.cancel();
      }
   }
}
