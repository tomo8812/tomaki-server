package net.satisfy.bakery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.storage.StorageTypeRenderer;

@OnlyIn(Dist.CLIENT)
public class CakeDisplayRenderer implements StorageTypeRenderer {
   public void render(StorageBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, NonNullList<ItemStack> itemStacks) {
      int invSize = 6;
      float scale = 0.35F;
      float itemScaleY = 0.4F;
      float yOffsetFirstColumn = 1.15F;
      float yOffsetSecondColumn = 0.25F;
      float zOffset = -0.45F;
      float xOffsetModifier = 2.1F;
      float secondColumnAdditionalOffset = -1.5F;
      float firstColumnLeftShift = 1.2F;

      for (int i = 0; i < invSize; i++) {
         ItemStack stack = (ItemStack)itemStacks.get(i);
         if (!stack.isEmpty() && !(stack.getItem() instanceof BlockItem)) {
            matrices.pushPose();
            matrices.scale(scale, itemScaleY, scale);
            boolean isFirstColumn = i < invSize / 2;
            float xOffset = (i - 1) * xOffsetModifier + (isFirstColumn ? firstColumnLeftShift : secondColumnAdditionalOffset);
            float yOffset = isFirstColumn ? yOffsetFirstColumn : yOffsetSecondColumn;
            matrices.translate(xOffset, yOffset, zOffset);
            matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
            matrices.translate(-1.2F * i, 1.0F, 0.0F);
            StackedDisplayItems.renderFlatPile(stack, i, 0.0625F, matrices, vertexConsumers, entity);
            matrices.popPose();
         }
      }
   }
}
