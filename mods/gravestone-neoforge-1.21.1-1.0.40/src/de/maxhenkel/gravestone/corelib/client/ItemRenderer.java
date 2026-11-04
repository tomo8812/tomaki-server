package de.maxhenkel.gravestone.corelib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ItemRenderer {
   @OnlyIn(Dist.CLIENT)
   protected Minecraft minecraft = Minecraft.getInstance();
   @OnlyIn(Dist.CLIENT)
   private final ItemRenderer.Renderer renderer = new ItemRenderer.Renderer();

   @OnlyIn(Dist.CLIENT)
   public void renderByItem(
      ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay
   ) {
   }

   @OnlyIn(Dist.CLIENT)
   protected ItemRenderer.Renderer getRenderer() {
      return this.renderer;
   }

   @OnlyIn(Dist.CLIENT)
   protected class Renderer extends BlockEntityWithoutLevelRenderer {
      public Renderer() {
         super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
      }

      public void renderByItem(
         ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay
      ) {
         ItemRenderer.this.renderByItem(stack, itemDisplayContext, poseStack, multiBufferSource, light, overlay);
      }
   }
}
