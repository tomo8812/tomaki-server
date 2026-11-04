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
public class TableSetRenderer implements StorageTypeRenderer {
   public void render(StorageBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, NonNullList<ItemStack> itemStacks) {
      ItemStack stack = (ItemStack)itemStacks.get(0);
      if (!stack.isEmpty()) {
         float oP = 0.0625F;
         matrices.translate(oP, oP, -oP);
         matrices.scale(0.5F, 0.5F, 0.5F);
         matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
         ClientUtil.renderItem(stack, matrices, vertexConsumers, entity);
      }
   }
}
