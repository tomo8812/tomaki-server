package dev.emi.emi.screen;

import dev.emi.emi.EmiPort;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import net.minecraft.resources.ResourceLocation;

public class DisabledToast implements Toast {
   private static final ResourceLocation TEXTURE = EmiPort.id("toast/advancement");

   public Visibility render(GuiGraphics raw, ToastComponent manager, long time) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      context.resetColor();
      raw.blitSprite(TEXTURE, 0, 0, this.width(), this.height());
      context.drawCenteredText(EmiPort.translatable("emi.disabled"), this.width() / 2, 7);
      context.drawCenteredText(EmiConfig.toggleVisibility.getBindText(), this.width() / 2, 18);
      return time <= 8000L && !EmiConfig.enabled ? Visibility.SHOW : Visibility.HIDE;
   }
}
