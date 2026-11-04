package net.satisfy.brewery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.block.BeerMugFlowerPotBlock;
import net.satisfy.brewery.core.block.entity.BeerMugBlockEntity;
import net.satisfy.foundation.render.ClientUtil;

public class BeerMugRenderer implements BlockEntityRenderer<BeerMugBlockEntity> {
   public BeerMugRenderer(Context ctx) {
   }

   public void render(BeerMugBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
      if (entity.hasLevel()) {
         BlockState selfState = entity.getBlockState();
         if (selfState.getBlock() instanceof BeerMugFlowerPotBlock) {
            Item item = entity.getFlower();
            matrices.pushPose();
            if (item instanceof BlockItem) {
               BlockState state = ((BlockItem)item).getBlock().defaultBlockState();
               matrices.translate(0.0F, 0.4F, 0.0F);
               ClientUtil.renderBlock(state, matrices, vertexConsumers, entity);
            }
         }

         matrices.popPose();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((BeerMugBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
