package net.satisfy.bakery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.storage.StorageTypeRenderer;

@OnlyIn(Dist.CLIENT)
public class CupcakeDisplayRenderer implements StorageTypeRenderer {
   public void render(StorageBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, NonNullList<ItemStack> itemStacks) {
      for (int i = 0; i < itemStacks.size(); i++) {
         ItemStack stack = (ItemStack)itemStacks.get(i);
         if (!stack.isEmpty()) {
            matrices.pushPose();
            matrices.scale(0.3F, 0.3F, 0.3F);
            float xOffset = i % 3 * 2.1F;
            float yOffset = i < 3 ? 1.7F : 0.45F;
            float zOffset = -1.0F;
            xOffset += i < 3 ? -0.9F : 2.7F;
            matrices.translate(xOffset, yOffset, zOffset);
            matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
            matrices.translate(-1.2F * i, 1.0F, 0.0F);
            StackedDisplayItems.renderFlatPile(stack, i, 0.0833F, matrices, vertexConsumers, entity);
            matrices.popPose();
         }
      }
   }
}
