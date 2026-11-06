package maki.airship.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import maki.airship.ShipSleepingRender;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityRenderer.class, remap = false)
public abstract class SleepingRenderMixin {
   @Inject(
      method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = Shift.AFTER, ordinal = 0),
      remap = false
   )
   private void maki$sleepInBedBasis(LivingEntity entity, float yaw, float partialTicks, PoseStack pose, MultiBufferSource buffers, int light, CallbackInfo ci) {
      ShipSleepingRender.apply(entity, partialTicks, pose);
   }
}
