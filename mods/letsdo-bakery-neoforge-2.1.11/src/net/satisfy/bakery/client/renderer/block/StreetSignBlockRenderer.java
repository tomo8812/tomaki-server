package net.satisfy.bakery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.bakery.core.block.StreetSignBlock;
import net.satisfy.bakery.core.block.entity.StreetSignBlockEntity;

public class StreetSignBlockRenderer implements BlockEntityRenderer<StreetSignBlockEntity> {
   public void render(StreetSignBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
      poseStack.pushPose();
      poseStack.translate(0.5, 1.0, 0.5);
      Direction facing = (Direction)entity.getBlockState().getValue(StreetSignBlock.FACING);
      float rotation = facing == Direction.NORTH ? 180.0F : (facing == Direction.WEST ? -90.0F : (facing == Direction.EAST ? 90.0F : 0.0F));
      poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
      poseStack.mulPose(Axis.XP.rotationDegrees(-22.5F));
      poseStack.scale(0.01F, -0.01F, 0.01F);
      Font font = Minecraft.getInstance().font;
      float[] offsetY = new float[]{15.0F, 35.0F, 50.0F};
      boolean glow = entity.isGlowing();

      for (int i = 0; i < 3; i++) {
         Component text = entity.getText(i);
         String str = text.getString();
         if (str.length() > 8) {
            str = str.substring(0, 8);
         }

         if (!str.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.0F, offsetY[i], 0.0F);
            if (glow) {
               BufferSource seeThroughBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
               font.drawInBatch(
                  Component.literal(str).getVisualOrderText(),
                  -font.width(str) / 2.0F,
                  0.0F,
                  15255954,
                  false,
                  poseStack.last().pose(),
                  seeThroughBuffer,
                  DisplayMode.SEE_THROUGH,
                  0,
                  15728880
               );
               seeThroughBuffer.endBatch();
            } else {
               font.drawInBatch(
                  Component.literal(str).getVisualOrderText(),
                  -font.width(str) / 2.0F,
                  0.0F,
                  15255954,
                  false,
                  poseStack.last().pose(),
                  buffer,
                  DisplayMode.NORMAL,
                  0,
                  light
               );
            }

            poseStack.popPose();
         }
      }

      poseStack.popPose();
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((StreetSignBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
