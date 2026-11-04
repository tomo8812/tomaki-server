package net.satisfy.bakery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.bakery.core.block.PieBlock;
import net.satisfy.bakery.core.block.entity.CakeCandleBlockEntity;
import net.satisfy.bakery.platform.PlatformHelper;
import net.satisfy.foundation.client.render.WobbleAnimation;

public class CakeCandleRenderer implements BlockEntityRenderer<CakeCandleBlockEntity> {
   private static final float CUT_WOBBLE_STRENGTH = 0.6F;

   public void render(CakeCandleBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
      BlockState state = entity.getBlockState();
      if (state.getBlock() instanceof PieBlock pie) {
         BlockRenderDispatcher var15 = Minecraft.getInstance().getBlockRenderer();
         poseStack.pushPose();
         if (PlatformHelper.animationsEnabled()) {
            WobbleAnimation.apply(
               poseStack, entity.getLevel(), entity.getWobbleStart(), partialTicks, WobbleAnimation.seed(entity.getBlockPos().asLong()), 0.6F
            );
         }

         int pieces = entity.getPieces();

         for (Direction quarter : Plane.HORIZONTAL) {
            if (PieBlock.hasPiece(pieces, quarter)) {
               BlockState piece = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(PieBlock.CUTS, 3)).setValue(PieBlock.FACING, quarter))
                     .setValue(PieBlock.CANDLE_COUNT, 0))
                  .setValue(PieBlock.LIT, false);
               var15.getModelRenderer()
                  .renderModel(
                     poseStack.last(),
                     buffer.getBuffer(ItemBlockRenderTypes.getRenderType(piece, false)),
                     piece,
                     var15.getBlockModel(piece),
                     1.0F,
                     1.0F,
                     1.0F,
                     light,
                     overlay
                  );
            }
         }

         int count = (Integer)state.getValue(PieBlock.CANDLE_COUNT);
         if (count > 0) {
            BlockState candle = (BlockState)entity.getCandleBlock().defaultBlockState().setValue(CandleBlock.LIT, (Boolean)state.getValue(PieBlock.LIT));

            for (int index = 0; index < count; index++) {
               float[] spot = pie.candleSpot(state, index);
               poseStack.pushPose();
               poseStack.translate((spot[0] - 8.0F) / 16.0F, spot[2] / 16.0F, (spot[1] - 8.0F) / 16.0F);
               var15.renderSingleBlock(candle, poseStack, buffer, light, overlay);
               poseStack.popPose();
            }
         }

         poseStack.popPose();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((CakeCandleBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
