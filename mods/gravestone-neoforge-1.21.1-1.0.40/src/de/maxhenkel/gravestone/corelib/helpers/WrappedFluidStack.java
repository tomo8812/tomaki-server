package de.maxhenkel.gravestone.corelib.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import de.maxhenkel.gravestone.corelib.client.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class WrappedFluidStack extends AbstractStack<FluidStack> {
   public WrappedFluidStack(FluidStack stack) {
      super(stack);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void render(GuiGraphics guiGraphics, int x, int y) {
      IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(this.stack.getFluid());
      TextureAtlasSprite texture = Minecraft.getInstance()
         .getModelManager()
         .getAtlas(InventoryMenu.BLOCK_ATLAS)
         .getSprite(extensions.getStillTexture(this.stack));
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      int color = extensions.getTintColor(this.stack);
      RenderSystem.setShaderColor(
         RenderUtils.getRedFloat(color), RenderUtils.getGreenFloat(color), RenderUtils.getBlueFloat(color), RenderUtils.getAlphaFloat(color)
      );
      RenderSystem.setShaderTexture(0, texture.atlasLocation());
      fluidBlit(guiGraphics, x, y, 16, 16, texture, color);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public List<Component> getTooltip() {
      List<Component> tooltip = new ArrayList<>();
      tooltip.add(this.getDisplayName());
      if (Minecraft.getInstance().options.advancedItemTooltips) {
         if (BuiltInRegistries.FLUID.containsValue(this.stack.getFluid())) {
            ResourceLocation registryName = BuiltInRegistries.FLUID.getKey(this.stack.getFluid());
            tooltip.add(Component.literal(registryName.toString()).withStyle(ChatFormatting.DARK_GRAY));
         }

         if (!this.stack.getComponentsPatch().isEmpty()) {
            tooltip.add(Component.translatable("item.nbt_tags", new Object[]{this.stack.getComponentsPatch().size()}).withStyle(ChatFormatting.DARK_GRAY));
         }
      }

      return tooltip;
   }

   @Override
   public Component getDisplayName() {
      return Component.literal("").append(this.stack.getHoverName()).withStyle(this.stack.getFluid().getFluidType().getRarity().getStyleModifier());
   }

   @Override
   public boolean isEmpty() {
      return this.stack.isEmpty();
   }

   @OnlyIn(Dist.CLIENT)
   public static void fluidBlit(GuiGraphics guiGraphics, int x, int y, int width, int height, TextureAtlasSprite sprite, int color) {
      innerBlit(
         guiGraphics.pose().last().pose(),
         x,
         x + width,
         y,
         y + height,
         sprite.getU0(),
         sprite.getU1(),
         sprite.getV0(),
         (sprite.getV1() - sprite.getV0()) * height / 16.0F + sprite.getV0(),
         color
      );
   }

   @OnlyIn(Dist.CLIENT)
   private static void innerBlit(Matrix4f matrix, int x1, int x2, int y1, int y2, float minU, float maxU, float minV, float maxV, int color) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      BufferBuilder bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      bufferBuilder.addVertex(matrix, x1, y2, 0.0F)
         .setUv(minU, maxV)
         .setColor(RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), 255);
      bufferBuilder.addVertex(matrix, x2, y2, 0.0F)
         .setUv(maxU, maxV)
         .setColor(RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), 255);
      bufferBuilder.addVertex(matrix, x2, y1, 0.0F)
         .setUv(maxU, minV)
         .setColor(RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), 255);
      bufferBuilder.addVertex(matrix, x1, y1, 0.0F)
         .setUv(minU, minV)
         .setColor(RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), 255);
      BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
   }
}
