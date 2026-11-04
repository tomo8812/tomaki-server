package net.satisfy.candlelight.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.foundation.render.ClientUtil;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.storage.StorageTypeRenderer;

@OnlyIn(Dist.CLIENT)
public class JewelryRenderer implements StorageTypeRenderer {
   public void render(StorageBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, NonNullList<ItemStack> itemStacks) {
      if (!itemStacks.isEmpty()) {
         ItemStack stack = (ItemStack)itemStacks.get(0);
         if (!stack.isEmpty()) {
            matrices.pushPose();
            matrices.mulPose(Axis.ZN.rotationDegrees(45.0F));
            matrices.translate(-0.1, 0.1, 0.0);
            matrices.scale(0.25F, 0.25F, 0.25F);
            ClientUtil.renderItem(stack, matrices, vertexConsumers, entity);
            matrices.popPose();
         }
      }
   }
}
