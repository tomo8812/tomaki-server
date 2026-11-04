package net.satisfy.farm_and_charm.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.model.SupplyCartModel;
import net.satisfy.farm_and_charm.core.entity.SupplyCartEntity;
import org.jetbrains.annotations.NotNull;

public class SupplyCartRenderer extends EntityRenderer<SupplyCartEntity> {
   public static final ResourceLocation CART_TEXTURE = FarmAndCharm.identifier("textures/entity/supply_cart.png");
   private final SupplyCartModel<SupplyCartEntity> model;

   public SupplyCartRenderer(Context context) {
      super(context);
      this.model = new SupplyCartModel<>(context.bakeLayer(SupplyCartModel.LAYER_LOCATION));
   }

   @NotNull
   public ResourceLocation getTextureLocation(SupplyCartEntity entity) {
      return CART_TEXTURE;
   }

   public void render(SupplyCartEntity cart, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
      super.render(cart, yaw, partialTick, poseStack, buffer, light);
      poseStack.pushPose();
      poseStack.translate(0.0, 1.4, 0.0);
      poseStack.scale(-1.0F, -1.0F, 1.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
      boolean hasPassenger = !cart.getPassengers().isEmpty();
      boolean isPulled = cart.getPulling() != null;
      boolean pulledByPlayer = cart.getPulling() instanceof Player;
      boolean pulledByHorse = isPulled && !pulledByPlayer;
      if (!pulledByHorse) {
         float basePitch;
         if (isPulled) {
            basePitch = 3.0F;
         } else if (hasPassenger) {
            basePitch = 11.0F;
         } else {
            basePitch = -11.0F;
         }

         poseStack.mulPose(Axis.XP.rotationDegrees(basePitch));
      }

      double deltaX = cart.getX() - cart.xOld;
      double deltaZ = cart.getZ() - cart.zOld;
      boolean isMoving = isPulled && deltaX * deltaX + deltaZ * deltaZ > 1.0E-4;
      if (isMoving) {
         float speed = Mth.sqrt((float)(deltaX * deltaX + deltaZ * deltaZ));
         float wobbleStrength = Mth.clamp(speed * 16.0F, 0.0F, 1.0F);
         float time = cart.tickCount + partialTick;
         float roll = Mth.sin(time * 0.9F) * 0.9F * wobbleStrength;
         float pitch = Mth.sin(time * 1.4F + 1.7F) * 1.1F * wobbleStrength;
         poseStack.mulPose(Axis.ZP.rotationDegrees(roll));
         poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
      }

      this.model.setupAnim(cart, cart.tickCount + partialTick, 0.0F, cart.tickCount + partialTick, yaw, cart.getXRot());
      VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(CART_TEXTURE));
      this.model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
      poseStack.popPose();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   public ResourceLocation getTextureLocation(Entity param1) {
      return this.getTextureLocation((SupplyCartEntity)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(Entity param1, float nullx, float nullxx, PoseStack nullxxx, MultiBufferSource nullxxxx, int nullxxxxx) {
      this.render((SupplyCartEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
