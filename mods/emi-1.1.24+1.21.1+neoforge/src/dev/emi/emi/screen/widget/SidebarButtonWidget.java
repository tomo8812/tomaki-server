package dev.emi.emi.screen.widget;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.config.SidebarType;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.input.EmiInput;
import dev.emi.emi.screen.EmiScreenManager;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class SidebarButtonWidget extends SizedButtonWidget {
   private final EmiScreenManager.SidebarPanel panel;

   public SidebarButtonWidget(int x, int y, int width, int height, EmiScreenManager.SidebarPanel panel) {
      super(x, y, width, height, 0, 0, () -> panel.pages.pages.size() > 0, null, () -> 0, () -> {
         List<Component> list = Lists.newArrayList();
         list.add(panel.getType().getText());
         list.add(panel.getType().getDescription());
         if (panel.getType() == SidebarType.FAVORITES && EmiConfig.favorite.isBound()) {
            list.add(EmiPort.translatable("emi.sidebar.favorite_stack", EmiConfig.favorite.getBindText()).withStyle(ChatFormatting.GRAY));
         }

         if (panel.pages.pages.size() > 1) {
            list.add(EmiPort.translatable("emi.sidebar.cycle", EmiBind.LEFT_CLICK.getBindText()).withStyle(ChatFormatting.GRAY));
         }

         return list;
      });
      this.panel = panel;
      this.texture = EmiRenderHelper.WIDGETS;
   }

   public void onPress() {
      this.panel.cycleType(EmiInput.isShiftDown() ? -1 : 1);
   }

   @Override
   protected int getU(int mouseX, int mouseY) {
      return this.panel.getType().u;
   }

   @Override
   protected int getV(int mouseX, int mouseY) {
      this.v = this.panel.getType().v;
      return !this.active ? super.getV(mouseX, mouseY) - this.height * 2 : super.getV(mouseX, mouseY);
   }
}
