package dev.emi.emi.api;

import dev.emi.emi.api.widget.Bounds;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;

public interface EmiExclusionArea<T extends Screen> {
   void addExclusionArea(T var1, Consumer<Bounds> var2);
}
