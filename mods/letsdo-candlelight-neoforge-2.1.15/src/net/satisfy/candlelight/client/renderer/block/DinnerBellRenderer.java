package net.satisfy.candlelight.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.candlelight.Candlelight;
import net.satisfy.candlelight.client.model.DinnerBellModel;
import net.satisfy.candlelight.core.block.entity.DinnerBellBlockEntity;

public class DinnerBellRenderer implements BlockEntityRenderer<DinnerBellBlockEntity> {
   private static final float PRESS_TICKS = 2.0F;
   private static final float RELEASE_TICKS = 4.0F;
   private static final float WOBBLE_TICKS = 24.0F;
   private static final float WOBBLE_SPEED = 1.6F;
   private static final float WOBBLE_DEGREES = 4.0F;
   private static final float WOBBLE_DECAY = 6.0F;
   private static final ResourceLocation BELL_TEXTURE = Candlelight.identifier("textures/entity/dinner_bell.png");
   private final ModelPart dinner_bell_base;
   private final ModelPart dinner_bell_button;

   public DinnerBellRenderer(Context context) {
      ModelPart root = context.bakeLayer(DinnerBellModel.LAYER_LOCATION);
      this.dinner_bell_base = root.getChild("dinner_bell_base");
      this.dinner_bell_button = root.getChild("dinner_bell_button");
   }

   public void render(
      DinnerBellBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay
   ) {
      float time = blockEntity.getRingProgress(partialTicks);
      boolean ringing = time >= 0.0F && time < 24.0F;
      poseStack.pushPose();
      if (ringing) {
         float wobble = Mth.sin(time * 1.6F) * 4.0F * (float)Math.exp(-time / 6.0F);
         poseStack.translate(0.5F, 0.0F, 0.5F);
         poseStack.mulPose(Axis.ZP.rotationDegrees(wobble));
         poseStack.mulPose(Axis.XP.rotationDegrees(wobble * 0.5F));
         poseStack.translate(-0.5F, 0.0F, -0.5F);
      }

      VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(BELL_TEXTURE));
      this.dinner_bell_base.render(poseStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY);
      poseStack.translate(0.0F, -buttonPress(ringing ? time : -1.0F) / 16.0F, 0.0F);
      this.dinner_bell_button.render(poseStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY);
      poseStack.popPose();
   }

   private static float buttonPress(float time) {
      if (time < 0.0F) {
         return 0.0F;
      }

      if (time < 2.0F) {
         return time / 2.0F;
      }

      float release = (time - 2.0F) / 4.0F;
      return release >= 1.0F ? 0.0F : 1.0F - release * release * (3.0F - 2.0F * release);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((DinnerBellBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
