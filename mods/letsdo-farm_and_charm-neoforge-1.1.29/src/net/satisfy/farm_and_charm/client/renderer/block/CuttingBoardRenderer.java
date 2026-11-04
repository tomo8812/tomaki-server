package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.farm_and_charm.core.block.CuttingBoardBlock;
import net.satisfy.farm_and_charm.core.block.entity.CuttingBoardBlockEntity;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.render.DisplayItemRenderer;

public class CuttingBoardRenderer implements BlockEntityRenderer<CuttingBoardBlockEntity> {
   private static final double BOARD_TOP = 0.07;
   private static final double STACK_STEP = 0.03;
   private static final float ITEM_SCALE = 0.4F;
   private static final float KNIFE_SCALE = 0.5F;
   private static final double KNIFE_REST_OFFSET = 0.12;
   private static final double CHOP_HEIGHT = 0.18;
   private static final float CHOP_TILT = 35.0F;
   private static final double TOOL_FORWARD = 0.125;
   private static final double STUCK_DEPTH = 0.03;
   private static final float STUCK_YAW = 25.0F;
   private static final float STUCK_TILT = -12.0F;
   private static final float BLADE_DOWN = -135.0F;
   private static final double UPRIGHT_HEIGHT = 0.16;
   private static final double ITEM_OFFSET = 0.125;
   private static final float HOP_TICKS = 6.0F;
   private static final double HOP_HEIGHT = 0.05;
   private static final float HOP_SPIN = 8.0F;

   public CuttingBoardRenderer(Context context) {
   }

   public void render(CuttingBoardBlockEntity board, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
      Direction facing = (Direction)board.getBlockState().getValue(CuttingBoardBlock.FACING);
      float yaw = 180.0F - facing.toYRot();
      int seed = (int)board.getBlockPos().asLong();
      Direction right = facing.getCounterClockWise();
      double itemX = 0.5 + right.getStepX() * 0.125;
      double itemZ = 0.5 + right.getStepZ() * 0.125;
      float hop = this.getHop(board, partialTick);
      double lift = hop * 0.05;
      float spin = hop * 8.0F;
      List<ItemStack> items = board.getItems();

      for (int i = 0; i < items.size(); i++) {
         float wiggle = i % 2 == 0 ? spin : -spin;
         DisplayItemRenderer.renderFlat(
            items.get(i), poseStack, buffers, light, overlay, board.getLevel(), itemX, 0.07 + i * 0.03 + lift, itemZ, yaw + i * 25.0F + wiggle, 0.4F, seed + i
         );
      }

      if (board.isCutting()) {
         DisplayItemRenderer.renderFlat(
            board.getPending(), poseStack, buffers, light, overlay, board.getLevel(), itemX, 0.07 + lift, itemZ, yaw + spin, 0.4F, seed
         );
      }

      if (board.hasKnife()) {
         this.renderKnife(board, facing, yaw, partialTick, poseStack, buffers, light, overlay, seed);
      }
   }

   private float getHop(CuttingBoardBlockEntity board, float partialTick) {
      if (board.getLevel() != null && PlatformHelper.animationsEnabled()) {
         float impact = board.isCutting() ? 4.0F : 0.0F;
         float sinceImpact = (float)(board.getLevel().getGameTime() - board.getCutStart()) + partialTick - impact;
         if (!(sinceImpact < 0.0F) && !(sinceImpact >= 6.0F)) {
            float progress = sinceImpact / 6.0F;
            return Math.abs(Mth.sin(progress * (float) (Math.PI * 2))) * (1.0F - progress);
         } else {
            return 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   private void renderKnife(
      CuttingBoardBlockEntity board,
      Direction facing,
      float yaw,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffers,
      int light,
      int overlay,
      int seed
   ) {
      Direction side = facing.getOpposite();
      Direction right = facing.getCounterClockWise();
      double lift = 0.0;
      float tilt = 0.0F;
      double offset = 0.0;
      if (board.isCutting() && board.getLevel() != null) {
         offset = -0.0050000000000000044;
         float sinceChop = (float)(board.getLevel().getGameTime() - board.getCutStart()) + partialTick;
         if (PlatformHelper.animationsEnabled() && sinceChop >= 0.0F && sinceChop < 4.0F) {
            double chop = Math.sin(Math.PI * sinceChop / 4.0);
            lift = chop * 0.18;
            tilt = (float)chop * 35.0F;
         }
      }

      poseStack.pushPose();
      if (board.isCutting()) {
         poseStack.translate(0.5 + right.getStepX() * 0.125 + side.getStepX() * offset, 0.23 + lift, 0.5 + right.getStepZ() * 0.125 + side.getStepZ() * offset);
         poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
         poseStack.mulPose(Axis.ZP.rotationDegrees(-135.0F + tilt));
      } else {
         poseStack.translate(0.5 + right.getStepX() * 0.125, 0.2, 0.5 + right.getStepZ() * 0.125);
         poseStack.mulPose(Axis.YP.rotationDegrees(yaw + 25.0F));
         poseStack.mulPose(Axis.ZP.rotationDegrees(-147.0F));
      }

      poseStack.scale(0.5F, 0.5F, 0.5F);
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(board.getKnife(), ItemDisplayContext.FIXED, light, overlay, poseStack, buffers, board.getLevel(), seed + 7);
      poseStack.popPose();
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((CuttingBoardBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
