package net.satisfy.bakery.client.renderer.block;

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
public class CakeStandRenderer implements StorageTypeRenderer {
   public void render(StorageBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, NonNullList<ItemStack> itemStacks) {
      for (int i = 0; i < itemStacks.size(); i++) {
         ItemStack stack = (ItemStack)itemStacks.get(i);
         if (!stack.isEmpty()) {
            matrices.pushPose();
            if (stack.getItem() instanceof BlockItem blockItem) {
               matrices.scale(0.65F, 0.65F, 0.65F);
               matrices.translate(-0.5, 0.8F, -0.5);
               ClientUtil.renderBlockFromItem(blockItem, matrices, vertexConsumers, entity);
            } else {
               matrices.scale(0.4F, 0.4F, 0.4F);
               if (i == 0) {
                  matrices.translate(-0.4F, 1.3F, 0.4F);
               } else if (i == 1) {
                  matrices.translate(-0.2F, 1.3F, -0.4F);
               } else {
                  matrices.translate(0.4F, 1.3F, 0.2F);
               }

               matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
               StackedDisplayItems.renderFlatPile(stack, i, 0.0625F, matrices, vertexConsumers, entity);
            }

            matrices.popPose();
         }
      }
   }
}
