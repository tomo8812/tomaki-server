package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.farm_and_charm.core.block.PetBowlBlock;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;

public class PetBowlBlockRenderer implements BlockEntityRenderer<PetBowlBlockEntity> {
   public void render(PetBowlBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
      poseStack.pushPose();
      poseStack.translate(0.5, 0.165, 0.5);
      Direction facing = (Direction)entity.getBlockState().getValue(PetBowlBlock.FACING);

      float rotation = switch (facing) {
         case SOUTH -> 0.0F;
         case WEST -> -90.0F;
         case EAST -> 90.0F;
         default -> 180.0F;
      };
      poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
      poseStack.translate(0.0, 0.0, 0.2505);
      poseStack.scale(0.01F, -0.01F, 0.01F);
      Font font = Minecraft.getInstance().font;
      Component text = entity.getText();
      String str = text.getString();
      if (str.length() > 6) {
         str = str.substring(0, 6);
      }

      if (!str.isEmpty()) {
         font.drawInBatch(
            Component.literal(str).getVisualOrderText(),
            -font.width(str) / 2.0F,
            0.0F,
            14206112,
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

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((PetBowlBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
