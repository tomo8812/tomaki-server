package dev.emi.emi.screen.widget.config;

import dev.emi.emi.EmiPort;
import dev.emi.emi.config.ScreenAlign;
import dev.emi.emi.screen.ConfigScreen;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class ScreenAlignWidget extends ConfigEntryWidget {
   private final ConfigScreen.Mutator<ScreenAlign> mutator;
   private Button horizontal;
   private Button vertical;

   public ScreenAlignWidget(Component name, List<ClientTooltipComponent> tooltip, Supplier<String> search, ConfigScreen.Mutator<ScreenAlign> mutator) {
      super(name, tooltip, search, 20);
      this.mutator = mutator;
      this.horizontal = EmiPort.newButton(0, 0, 106, 20, this.getHorizontalText(), button -> EnumWidget.page(mutator.get().horizontal, v -> true, c -> {
         mutator.get().horizontal = (ScreenAlign.Horizontal)c;
         mutator.set(mutator.get());
      }));
      this.vertical = EmiPort.newButton(0, 0, 106, 20, this.getVerticalText(), button -> EnumWidget.page(mutator.get().vertical, v -> true, c -> {
         mutator.get().vertical = (ScreenAlign.Vertical)c;
         mutator.set(mutator.get());
      }));
      this.setChildren(List.of(this.horizontal, this.vertical));
   }

   public Component getHorizontalText() {
      return this.mutator.get().horizontal.getText();
   }

   public Component getVerticalText() {
      return this.mutator.get().vertical.getText();
   }

   @Override
   public void update(int y, int x, int width, int height) {
      this.horizontal.x = x + width - this.horizontal.getWidth() - this.vertical.getWidth() - 7;
      this.horizontal.y = y;
      this.vertical.x = x + width - this.vertical.getWidth();
      this.vertical.y = y;
   }
}
