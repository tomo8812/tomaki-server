package dev.emi.emi.screen.widget.config;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.config.SidebarPages;
import dev.emi.emi.config.SidebarType;
import dev.emi.emi.screen.ConfigScreen;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class SidebarPagesWidget extends ConfigEntryWidget {
   private List<Button> buttons = Lists.newArrayList();
   private ConfigScreen.Mutator<SidebarPages> mutator;

   public SidebarPagesWidget(Component name, List<ClientTooltipComponent> tooltip, Supplier<String> search, ConfigScreen.Mutator<SidebarPages> mutator) {
      super(name, tooltip, search, 0);
      this.mutator = mutator;
      this.setChildren(this.buttons);
      this.updateButtons();
   }

   public void updateButtons() {
      this.buttons.clear();
      SidebarPages pages = this.mutator.get();

      for (int i = 0; i < pages.pages.size(); i++) {
         int j = i;
         SidebarPages.SidebarPage page = pages.pages.get(i);
         this.buttons
            .add(
               EmiPort.newButton(
                  0, 0, 194, 20, page.type.getText(), b -> EnumWidget.page(page.type, t -> pages.canShowChess() || t != SidebarType.CHESS, t -> {
                     pages.pages.get(j).type = (SidebarType)t;
                     pages.unique();
                  })
               )
            );
      }

      this.buttons
         .add(
            EmiPort.newButton(
               0, 0, 20, 20, EmiPort.literal("+"), b -> EnumWidget.page(SidebarType.INDEX, t -> pages.canShowChess() || t != SidebarType.CHESS, t -> {
                  pages.pages.add(new SidebarPages.SidebarPage((SidebarType)t));
                  pages.unique();
               })
            )
         );
   }

   @Override
   public void update(int y, int x, int width, int height) {
      int h = 0;

      for (int i = 0; i < this.buttons.size() - 1; i++) {
         Button button = this.buttons.get(i);
         button.x = x + width - 218;
         button.y = y + h;
         h += 24;
      }

      Button button = this.buttons.get(this.buttons.size() - 1);
      button.x = x + width - 20;
      button.y = y;
   }

   @Override
   public int getHeight() {
      if (this.isVisible() && this.isParentVisible()) {
         return this.buttons.size() == 1 ? 20 : this.buttons.size() * 24 - 28;
      } else {
         return 0;
      }
   }
}
