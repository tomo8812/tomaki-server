package com.archiangel.quadhotbar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public final class QuadHotbarControlButton extends Button {
   private final boolean grid;
   private final boolean frameless;

   public QuadHotbarControlButton(int x, int y, Component label, OnPress action, boolean grid) {
      this(x, y, label, action, grid, false);
   }

   private QuadHotbarControlButton(int x, int y, Component label, OnPress action, boolean grid, boolean frameless) {
      super(x, y, 20, 20, (Component)(grid ? Component.empty() : label), action, DEFAULT_NARRATION);
      this.grid = grid;
      this.frameless = frameless;
      if (grid || frameless) {
         this.setTooltip(Tooltip.create(label));
      }
   }

   public static QuadHotbarControlButton close(int x, int y, OnPress action) {
      return new QuadHotbarControlButton(x, y, Component.translatable("quadhotbar.overview.close"), action, false, true);
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      if (this.frameless) {
         Font font = Minecraft.getInstance().font;
         graphics.drawString(
            font,
            "×",
            this.getX() + (this.getWidth() - font.width("×")) / 2,
            this.getY() + (this.getHeight() - 9) / 2,
            this.active ? (this.isHoveredOrFocused() ? 16777120 : 4210752) : 10526880,
            false
         );
      } else {
         super.renderWidget(graphics, mouseX, mouseY, partialTick);
         if (this.grid) {
            graphics.pose().pushPose();
            graphics.pose().translate(0.5F, 0.5F, 0.0F);

            try {
               for (int row = 0; row < 3; row++) {
                  for (int column = 0; column < 3; column++) {
                     int x = this.getX() + 4 + column * 4;
                     int y = this.getY() + 4 + row * 4;
                     graphics.fill(x, y, x + 3, y + 3, this.active ? -1 : -8947849);
                  }
               }
            } finally {
               graphics.pose().popPose();
            }
         }
      }
   }
}
