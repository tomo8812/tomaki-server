package net.satisfy.bakery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import net.satisfy.foundation.render.ClientUtil;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.storage.StorageTypeRenderer;

@OnlyIn(Dist.CLIENT)
public class TrayRenderer implements StorageTypeRenderer {
   public void render(StorageBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, NonNullList<ItemStack> itemStacks) {
      matrices.translate(0.0, 0.3, 0.2F);
      matrices.scale(0.5F, 0.5F, 0.5F);
      matrices.mulPose(Axis.YP.rotationDegrees(90.0F));

      for (int i = 0; i < itemStacks.size(); i++) {
         ItemStack stack = (ItemStack)itemStacks.get(i);
         if (!stack.isEmpty()) {
            matrices.pushPose();
            if (stack.getItem() instanceof BlockItem blockItem) {
               if (blockItem.getBlock() == ObjectRegistry.CRUSTY_BREAD_BLOCK.get()) {
                  matrices.translate(-0.6F, -0.5F, -1.0F);
                  matrices.scale(2.0F, 2.0F, 2.0F);
                  ClientUtil.renderBlockFromItem(blockItem, matrices, vertexConsumers, entity);
               } else if (blockItem.getBlock() == ObjectRegistry.BUN_BLOCK.get()) {
                  matrices.translate(-0.6F, -0.5F, -1.0F);
                  matrices.scale(2.0F, 2.0F, 2.0F);
                  ClientUtil.renderBlockFromItem(blockItem, matrices, vertexConsumers, entity);
               } else if (blockItem.getBlock() == ObjectRegistry.BREAD_BLOCK.get()) {
                  matrices.translate(-0.6F, -0.5F, 1.0F);
                  matrices.scale(2.0F, 2.0F, 2.0F);
                  matrices.mulPose(Axis.YP.rotationDegrees(90.0F));
                  ClientUtil.renderBlockFromItem(blockItem, matrices, vertexConsumers, entity);
               } else if (blockItem.getBlock() == ObjectRegistry.BRAIDED_BREAD_BLOCK.get()) {
                  matrices.translate(-0.6F, -0.5F, 1.0F);
                  matrices.scale(2.0F, 2.0F, 2.0F);
                  matrices.mulPose(Axis.YP.rotationDegrees(90.0F));
                  ClientUtil.renderBlockFromItem(blockItem, matrices, vertexConsumers, entity);
               } else if (blockItem.getBlock() == ObjectRegistry.TOAST_BLOCK.get()) {
                  matrices.translate(-0.6F, -0.5F, 1.0F);
                  matrices.scale(2.0F, 2.0F, 2.0F);
                  matrices.mulPose(Axis.YP.rotationDegrees(90.0F));
                  ClientUtil.renderBlockFromItem(blockItem, matrices, vertexConsumers, entity);
               } else if (blockItem.getBlock() == ObjectRegistry.BAGUETTE_BLOCK.get()) {
                  matrices.translate(-0.3F, -0.5F, 0.7F);
                  matrices.scale(1.4F, 1.4F, 1.4F);
                  matrices.mulPose(Axis.YP.rotationDegrees(90.0F));
                  ClientUtil.renderBlockFromItem(blockItem, matrices, vertexConsumers, entity);
               } else {
                  matrices.translate(0.1, -0.5, -0.4F);
                  matrices.scale(0.8F, 0.8F, 0.8F);
                  ClientUtil.renderBlockFromItem(blockItem, matrices, vertexConsumers, entity);
               }
            } else {
               matrices.translate(0.2F * i, 0.0F, 0.0F);
               matrices.mulPose(Axis.YN.rotationDegrees(66.0F));
               ClientUtil.renderItem(stack, matrices, vertexConsumers, entity);
            }

            matrices.popPose();
         }
      }
   }
}
