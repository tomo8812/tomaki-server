package com.chaosthedude.naturescompass.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TransparentButton extends Button {
   public TransparentButton(int x, int y, int width, int height, Component label, OnPress onPress) {
      super(x, y, width, height, label, onPress, DEFAULT_NARRATION);
   }

   public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         Minecraft mc = Minecraft.getInstance();
         float state = 2.0F;
         if (!this.active) {
            state = 5.0F;
         } else if (this.isHovered) {
            state = 4.0F;
         }

         float f = state / 2.0F * 0.9F + 0.1F;
         int color = (int)(255.0F * f);
         guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color / 2 << 24);
         guiGraphics.drawCenteredString(mc.font, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, 16777215);
      }
   }
}
