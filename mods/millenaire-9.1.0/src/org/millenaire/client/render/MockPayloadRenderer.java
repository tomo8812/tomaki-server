package org.millenaire.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.millenaire.block.mock.MockGenericMaterialBlock;
import org.millenaire.block.mock.MockPayloadBlockEntity;

public class MockPayloadRenderer implements BlockEntityRenderer<MockPayloadBlockEntity> {
   private static final float ICON_SCALE = 0.6F;
   private static final float LIFT = 0.01F;
   private static final float BLOCK_SCALE = 0.998F;
   private final ItemRenderer itemRenderer;
   private final BlockRenderDispatcher blockRenderer;

   public MockPayloadRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
      this.blockRenderer = context.getBlockRenderDispatcher();
   }

   public void render(
      MockPayloadBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      if (blockEntity.getBlockState().getBlock() instanceof MockGenericMaterialBlock material) {
         this.renderMaterial(material, blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
      } else {
         ItemStack icon = blockEntity.getDisplayStack();
         if (!icon.isEmpty()) {
            Level level = blockEntity.getLevel();
            if (level != null) {
               BlockPos pos = blockEntity.getBlockPos();
               double top = blockEntity.getBlockState().getShape(level, pos).max(Axis.Y);
               int light = LevelRenderer.getLightColor(level, pos.above());
               poseStack.pushPose();
               poseStack.translate(0.5, top + 0.01F, 0.5);
               poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
               poseStack.scale(0.6F, 0.6F, 0.6F);
               this.itemRenderer.renderStatic(icon, ItemDisplayContext.FIXED, light, packedOverlay, poseStack, bufferSource, level, (int)pos.asLong());
               poseStack.popPose();
            }
         }
      }
   }

   private void renderMaterial(
      MockGenericMaterialBlock material,
      MockPayloadBlockEntity blockEntity,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
   ) {
      BlockState state = material.resolvedState(blockEntity.getPayload());
      if (state != null && state.getRenderShape() == RenderShape.MODEL) {
         poseStack.pushPose();
         poseStack.translate(0.5, 0.5, 0.5);
         poseStack.scale(0.998F, 0.998F, 0.998F);
         poseStack.translate(-0.5, -0.5, -0.5);
         this.blockRenderer.renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay);
         poseStack.popPose();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((MockPayloadBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
