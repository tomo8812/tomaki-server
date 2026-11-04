package org.valkyrienskies.mod.mixin.feature.shipyard_entities;

import net.minecraft.class_1297;
import net.minecraft.class_2561;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_897;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_897.class)
public class MixinEntityRenderer {
   @Inject(
      method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", shift = Shift.AFTER)
   )
   private void revertShipRotation(
      class_1297 entity, class_2561 component, class_4587 matrices, class_4597 bufferSource, int packedLight, float partialTicks, CallbackInfo ci
   ) {
      ClientShip ship = (ClientShip)VSGameUtilsKt.getLoadedShipManagingPos(entity.method_37908(), entity.method_24515());
      if (ship != null) {
         matrices.method_22907(new Quaternionf(ship.getRenderTransform().getShipToWorldRotation()).invert());
      }
   }
}
