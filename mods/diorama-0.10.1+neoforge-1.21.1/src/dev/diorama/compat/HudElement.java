package dev.diorama.compat;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface HudElement {
   void render(GuiGraphics var1, DeltaTracker var2);
}
