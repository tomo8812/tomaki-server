package net.satisfy.farm_and_charm.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.gui.handler.CookingPotGuiHandler;
import net.satisfy.foundation.client.gui.recipebook.StationRecipeBookScreen;

@OnlyIn(Dist.CLIENT)
public class CookingPotGui extends StationRecipeBookScreen<CookingPotGuiHandler> {
   public static final ResourceLocation BACKGROUND = FarmAndCharm.identifier("textures/gui/pot_gui.png");
   public static final int ARROW_X = 95;
   public static final int ARROW_Y = 14;

   public CookingPotGui(CookingPotGuiHandler handler, Inventory playerInventory, Component title) {
      super(handler, playerInventory, title);
   }

   protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BACKGROUND);
      int posX = this.leftPos;
      int posY = this.topPos;
      guiGraphics.blit(BACKGROUND, posX, posY, 0, 0, this.imageWidth - 1, this.imageHeight);
      this.renderProgressArrow(guiGraphics);
      this.renderBurnIcon(guiGraphics, posX, posY);
   }

   public void renderProgressArrow(GuiGraphics guiGraphics) {
      int progress = ((CookingPotGuiHandler)this.menu).getScaledProgress(23);
      guiGraphics.blit(BACKGROUND, this.leftPos + 95, this.topPos + 14, 178, 15, progress, 30);
   }

   public void renderBurnIcon(GuiGraphics guiGraphics, int posX, int posY) {
      if (((CookingPotGuiHandler)this.menu).isBeingBurned()) {
         guiGraphics.blit(BACKGROUND, posX + 124, posY + 56, 176, 0, 17, 15);
      }
   }
}
