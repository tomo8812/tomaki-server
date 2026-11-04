package dev.emi.emi.screen.widget.config;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.config.SidebarSubpanels;
import dev.emi.emi.config.SidebarType;
import dev.emi.emi.screen.ConfigScreen;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class SidebarSubpanelsWidget extends ConfigEntryWidget {
   private List<Button> buttons = Lists.newArrayList();
   private List<AbstractWidget> raws = Lists.newArrayList();
   private List<IntEdit> rows = Lists.newArrayList();
   private ConfigScreen.Mutator<SidebarSubpanels> mutator;

   public SidebarSubpanelsWidget(Component name, List<ClientTooltipComponent> tooltip, Supplier<String> search, ConfigScreen.Mutator<SidebarSubpanels> mutator) {
      super(name, tooltip, search, 0);
      this.mutator = mutator;
      this.setChildren(this.raws);
      this.updateButtons();
   }

   public void updateButtons() {
      this.raws.clear();
      this.buttons.clear();
      SidebarSubpanels pages = this.mutator.get();

      for (int i = 0; i < pages.subpanels.size(); i++) {
         int j = i;
         SidebarSubpanels.Subpanel page = pages.subpanels.get(i);
         this.buttons.add(EmiPort.newButton(0, 0, 150, 20, page.type.getText(), b -> EnumWidget.page(page.type, t -> t != SidebarType.CHESS, t -> {
            pages.subpanels.get(j).type = (SidebarType)t;
            pages.unique();
         })));
         IntEdit edit = new IntEdit(40, () -> page.rows(), ni -> page.rows = Math.max(1, ni));
         this.rows.add(edit);
         this.raws.add(edit.up);
         this.raws.add(edit.down);
         this.raws.add(edit.text);
      }

      this.buttons.add(EmiPort.newButton(0, 0, 20, 20, EmiPort.literal("+"), b -> EnumWidget.page(SidebarType.INDEX, t -> t != SidebarType.CHESS, t -> {
         pages.subpanels.add(new SidebarSubpanels.Subpanel((SidebarType)t, 1));
         pages.unique();
      })));
      this.raws.addAll(this.buttons);
   }

   @Override
   public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
      for (IntEdit edit : this.rows) {
         if (edit.contains(mouseX, mouseY)) {
            return List.of(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.sidebar.size.rows"))));
         }
      }

      return super.getTooltip(mouseX, mouseY);
   }

   @Override
   public void update(int y, int x, int width, int height) {
      int h = 0;

      for (int i = 0; i < this.buttons.size() - 1; i++) {
         Button button = this.buttons.get(i);
         button.x = x + width - 174;
         button.y = y + h;
         h += 24;
      }

      int var8 = 0;

      for (int i = 0; i < this.rows.size(); i++) {
         IntEdit edit = this.rows.get(i);
         edit.setPosition(x + width - 218, y + var8);
         var8 += 24;
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
