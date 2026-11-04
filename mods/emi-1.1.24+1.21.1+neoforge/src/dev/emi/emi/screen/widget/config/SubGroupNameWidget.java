package dev.emi.emi.screen.widget.config;

import dev.emi.emi.EmiPort;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SubGroupNameWidget extends GroupNameWidget {
   public GroupNameWidget parent;

   public SubGroupNameWidget(String id, Component text) {
      super(id, text);
   }

   @Override
   public void render(GuiGraphics raw, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      context.drawTextWithShadow(this.text, x + 20, y + 3);
      if (hovered || this.collapsed) {
         String collapse = "[-]";
         int cx = x;
         if (this.collapsed) {
            collapse = "[+]";
         }

         context.drawTextWithShadow(EmiPort.literal(collapse), cx, y + 3);
      }
   }

   @Override
   public int getHeight() {
      return this.parent.collapsed ? 0 : super.getHeight();
   }
}
