package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.model.WellPumpModel;
import net.satisfy.farm_and_charm.core.block.TimberWellBlock;
import net.satisfy.farm_and_charm.core.block.entity.TimberWellBlockEntity;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

public class TimberWellRenderer implements BlockEntityRenderer<TimberWellBlockEntity> {
   private static final ResourceLocation TEXTURE = FarmAndCharm.identifier("textures/entity/well_pump.png");
   private static final float PIVOT_X = 1.0F;
   private static final float PIVOT_Y = 0.4375F;
   private static final float PIVOT_Z = 0.5F;
   private static final float REST_ANGLE = 0.0F;
   private static final float PRESSED_ANGLE = 35.0F;
   private static final float PRESS_PORTION = 0.3F;
   private final ModelPart lever;

   public TimberWellRenderer(Context context) {
      this.lever = context.bakeLayer(WellPumpModel.LAYER_LOCATION).getChild("bone");
   }

   public void render(TimberWellBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
      BlockState state = be.getBlockState();
      if (state.getBlock() instanceof TimberWellBlock && be.getLevel() != null) {
         float angle = !PlatformHelper.animationsEnabled() ? 0.0F : getLeverAngle((float)(be.getLevel().getGameTime() - be.getPumpStartTick()) + partialTick);
         float blockstateYRot = (((Direction)state.getValue(TimberWellBlock.FACING)).toYRot() + 90.0F) % 360.0F;
         poseStack.pushPose();
         poseStack.translate(0.5, 0.0, 0.5);
         poseStack.mulPose(Axis.YP.rotationDegrees(-blockstateYRot));
         poseStack.translate(-0.5, 0.0, -0.5);
         poseStack.translate(1.0F, 0.4375F, 0.5F);
         poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
         poseStack.translate(-1.0F, -0.4375F, -0.5F);
         poseStack.translate(0.5, 1.5, 0.5);
         poseStack.scale(-1.0F, -1.0F, 1.0F);
         this.lever.render(poseStack, buffers.getBuffer(RenderType.entityCutout(TEXTURE)), light, overlay);
         poseStack.popPose();
      }
   }

   private static float getLeverAngle(float ticksSincePump) {
      float progress = ticksSincePump / 12.0F;
      if (!(progress < 0.0F) && !(progress >= 1.0F)) {
         float pressed;
         if (progress < 0.3F) {
            float t = progress / 0.3F;
            pressed = t * t * (3.0F - 2.0F * t);
         } else {
            float t = (progress - 0.3F) / 0.7F;
            float c1 = 1.70158F;
            float c3 = c1 + 1.0F;
            float easeOutBack = 1.0F + c3 * (float)Math.pow(t - 1.0F, 3.0) + c1 * (float)Math.pow(t - 1.0F, 2.0);
            pressed = 1.0F - easeOutBack;
         }

         return Mth.lerp(pressed, 0.0F, 35.0F);
      } else {
         return 0.0F;
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((TimberWellBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
