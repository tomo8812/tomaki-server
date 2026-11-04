package dev.emi.emi.api.widget;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public abstract class Widget implements Renderable {
   public abstract Bounds getBounds();

   public abstract void render(GuiGraphics var1, int var2, int var3, float var4);

   public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
      return List.of();
   }

   public boolean mouseClicked(int mouseX, int mouseY, int button) {
      return false;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return false;
   }
}
