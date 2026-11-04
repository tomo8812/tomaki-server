package net.satisfy.bakery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.satisfy.foundation.render.ClientUtil;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.storage.StorageTypeRenderer;

public class WallDisplayRenderer implements StorageTypeRenderer {
   private static final float[][] ITEM_POSITIONS = new float[][]{{-0.5F, 0.7F}, {0.5F, 0.7F}, {-0.5F, 1.8F}, {0.5F, 1.8F}};
   private static final float SCALE = 0.4F;
   private static final int MAX_LAYERS = 4;
   private static final int ITEMS_PER_LAYER = 16;
   private static final float[] LAYER_SHIFT_X = new float[]{0.0F, 0.12F, -0.1F, 0.06F};
   private static final float[] LAYER_SHIFT_Y = new float[]{0.0F, 0.06F, 0.1F, 0.16F};
   private static final float[] LAYER_ROLL = new float[]{0.0F, -9.0F, 7.0F, -4.0F};
   private static final float LAYER_FORWARD = -0.06F;

   public void render(StorageBlockEntity entity, PoseStack poseStack, MultiBufferSource buffer, NonNullList<ItemStack> items) {
      for (int i = 0; i < Math.min(items.size(), ITEM_POSITIONS.length); i++) {
         ItemStack stack = (ItemStack)items.get(i);
         if (!stack.isEmpty()) {
            int layers = Math.min(4, 1 + (stack.getCount() - 1) / 16);

            for (int layer = 0; layer < layers; layer++) {
               poseStack.pushPose();
               poseStack.scale(0.4F, 0.4F, 0.4F);
               poseStack.translate(ITEM_POSITIONS[i][0] + LAYER_SHIFT_X[layer], ITEM_POSITIONS[i][1] + LAYER_SHIFT_Y[layer], 0.5F + layer * -0.06F);
               poseStack.mulPose(Axis.ZP.rotationDegrees(LAYER_ROLL[layer]));
               ClientUtil.renderItem(stack, poseStack, buffer, entity);
               poseStack.popPose();
            }
         }
      }
   }
}
