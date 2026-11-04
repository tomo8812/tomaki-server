package fuzs.puzzleslib.api.client.event.v2.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ScreenOpeningCallback {
   EventInvoker<ScreenOpeningCallback> EVENT = EventInvoker.lookup(ScreenOpeningCallback.class);

   EventResultHolder<@Nullable Screen> onScreenOpening(@Nullable Screen var1, @Nullable Screen var2);
}
