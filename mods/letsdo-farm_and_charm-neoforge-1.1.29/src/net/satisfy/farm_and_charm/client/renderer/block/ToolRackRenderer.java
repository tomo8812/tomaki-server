package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.foundation.client.render.WobbleAnimation;
import net.satisfy.foundation.render.ClientUtil;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.storage.StorageTypeRenderer;

@OnlyIn(Dist.CLIENT)
public class ToolRackRenderer implements StorageTypeRenderer {
   private static final float WOBBLE_STRENGTH = 3.0F;

   public void render(StorageBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, NonNullList<ItemStack> itemStacks) {
      for (int i = 0; i < itemStacks.size(); i++) {
         ItemStack stack = (ItemStack)itemStacks.get(i);
         if (!stack.isEmpty()) {
            matrices.pushPose();
            Item item = stack.getItem();
            double translate = (i + 1) * 0.3333333333333333;
            float wobble = WobbleAnimation.angle(
                  entity.getLevel(), entity.getWobbleStart(i), Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false)
               )
               * 3.0F;
            if (stack.is(TagRegistry.HANGABLE)) {
               matrices.translate(translate - 0.625, 0.675F, 0.38F);
               matrices.mulPose(Axis.ZP.rotationDegrees(wobble));
               matrices.scale(0.5F, 0.5F, 0.5F);
               matrices.mulPose(Axis.ZN.rotationDegrees(45.0F));
               matrices.mulPose(Axis.YN.rotationDegrees(-180.0F));
               ClientUtil.renderItem(stack, matrices, vertexConsumers, entity);
            } else {
               matrices.translate(translate - 0.6666666666666666, 0.6F, 0.38F);
               matrices.mulPose(Axis.ZP.rotationDegrees(wobble));
               matrices.scale(0.6F, 0.6F, 0.6F);
               matrices.mulPose(Axis.ZN.rotationDegrees(135.0F));
               matrices.mulPose(Axis.YN.rotationDegrees(0.0F));
               ClientUtil.renderItem(stack, matrices, vertexConsumers, entity);
            }

            matrices.popPose();
         }
      }
   }
}
