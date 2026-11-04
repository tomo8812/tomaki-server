package net.satisfy.bakery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.satisfy.foundation.render.ClientUtil;
import net.satisfy.foundation.storage.StorageBlockEntity;

public final class StackedDisplayItems {
   private static final int ITEMS_PER_LAYER = 8;
   private static final int MAX_LAYERS = 8;
   private static final float[] LAYER_SHIFT_X = new float[]{0.0F, 0.06F, -0.05F, 0.04F, -0.07F, 0.02F, 0.07F, -0.03F};
   private static final float[] LAYER_SHIFT_Y = new float[]{0.0F, -0.04F, 0.05F, 0.02F, -0.06F, 0.07F, -0.02F, 0.04F};
   private static final float[] LAYER_YAW = new float[]{0.0F, 23.0F, -17.0F, 41.0F, -32.0F, 12.0F, -46.0F, 29.0F};

   private StackedDisplayItems() {
   }

   public static void renderFlatPile(ItemStack stack, int slot, float layerRise, PoseStack matrices, MultiBufferSource buffers, StorageBlockEntity entity) {
      int layers = Math.min(8, 1 + (stack.getCount() - 1) / 8);

      for (int layer = 0; layer < layers; layer++) {
         int variant = layer == 0 ? 0 : (slot * 3 + layer) % LAYER_YAW.length;
         matrices.pushPose();
         matrices.translate(LAYER_SHIFT_X[variant], LAYER_SHIFT_Y[variant], -layer * layerRise);
         matrices.mulPose(Axis.ZP.rotationDegrees(LAYER_YAW[variant]));
         ClientUtil.renderItem(stack, matrices, buffers, entity);
         matrices.popPose();
      }
   }
}
