package dev.terminalmc.clientsort.mixin.client.compat.supermartijn642corelib;

import dev.terminalmc.clientsort.client.gui.widget.TriggerButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.supermartijn642.core.gui.WidgetContainerScreen")
public class WidgetContainerScreenMixin extends Screen {
   protected WidgetContainerScreenMixin(Component title) {
      super(title);
   }

   @Inject(method = "render", at = @At("RETURN"))
   private void afterRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      this.children().forEach(child -> {
         if (child instanceof TriggerButton button) {
            button.render(graphics, mouseX, mouseY, partialTick);
         }
      });
   }
}
