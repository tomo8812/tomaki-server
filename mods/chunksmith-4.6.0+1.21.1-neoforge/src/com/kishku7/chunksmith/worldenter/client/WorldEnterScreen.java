package com.kishku7.chunksmith.worldenter.client;

import com.kishku7.chunksmith.worldenter.WorldEnterPregen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class WorldEnterScreen extends Screen {
   private static final int BAR_WIDTH = 300;
   private static final int BAR_HEIGHT = 14;
   private static final int COLOR_TEXT = -1;
   private static final int COLOR_DIM = -6250336;
   private static final int COLOR_BAR_BORDER = -16777216;
   private static final int COLOR_BAR_TRACK = -13619152;
   private static final int COLOR_BAR_FILL = -12799933;
   private static final int COLOR_BACKDROP = -15724528;

   public WorldEnterScreen() {
      super(Component.literal("Preparing your world"));
   }

   protected void init() {
      this.addRenderableWidget(
         Button.builder(Component.literal("Enter World Now"), button -> this.onClose()).bounds((this.width - 200) / 2, this.barTop() + 58, 200, 20).build()
      );
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void onClose() {
      WorldEnterPregen.release();
      super.onClose();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
      int centerX = this.width / 2;
      int top = this.barTop();
      double fraction = WorldEnterPregen.fraction();
      graphics.fill(0, 0, this.width, this.height, -15724528);
      super.render(graphics, mouseX, mouseY, partial);
      this.centered(graphics, this.title, centerX, top - 46, -1);
      this.centered(graphics, Component.literal("Pre-generating terrain so it is ready before you arrive."), centerX, top - 32, -6250336);
      int left = centerX - 150;
      int right = left + 300;
      graphics.fill(left - 1, top - 1, right + 1, top + 14 + 1, -16777216);
      graphics.fill(left, top, right, top + 14, -13619152);
      int filled = (int)Math.round(300.0 * fraction);
      if (filled > 0) {
         graphics.fill(left, top, left + filled, top + 14, -12799933);
      }

      this.centered(
         graphics, Component.literal(String.format("%.1f%%  --  %,d chunks", fraction * 100.0, WorldEnterPregen.chunksDone())), centerX, top + 14 + 8, -1
      );
      this.centered(graphics, Component.literal(WorldEnterPregen.eta()), centerX, top + 14 + 22, -6250336);
      this.centered(graphics, Component.literal("Entering now will not stop it -- generation continues in the background."), centerX, top + 84, -6250336);
   }

   private void centered(GuiGraphics g, Component text, int x, int y, int color) {
      g.drawCenteredString(this.font, text, x, y, color);
   }

   private int barTop() {
      return this.height / 2 - 7;
   }
}
