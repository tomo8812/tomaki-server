package dev.emi.emi.api;

import dev.emi.emi.api.stack.EmiStackInteraction;
import net.minecraft.client.gui.screens.Screen;

public interface EmiStackProvider<T extends Screen> {
   EmiStackInteraction getStackAt(T var1, int var2, int var3);
}
