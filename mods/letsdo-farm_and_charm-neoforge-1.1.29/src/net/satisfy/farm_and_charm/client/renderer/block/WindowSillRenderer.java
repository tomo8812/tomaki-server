package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.foundation.render.ClientUtil;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.storage.StorageTypeRenderer;

@OnlyIn(Dist.CLIENT)
public class WindowSillRenderer implements StorageTypeRenderer {
   public void render(StorageBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, NonNullList<ItemStack> itemStacks) {
      matrices.translate(-0.25F, 0.25F, 0.75F);
      matrices.mulPose(Axis.YP.rotationDegrees(90.0F));

      for (int i = 0; i < itemStacks.size(); i++) {
         ItemStack stack = (ItemStack)itemStacks.get(i);
         if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            matrices.pushPose();
            matrices.translate(0.0F, 0.0F, -0.5F * i);
            ClientUtil.renderBlockFromItem(blockItem, matrices, vertexConsumers, entity);
            matrices.popPose();
         }
      }
   }
}
