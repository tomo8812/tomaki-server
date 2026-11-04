package dev.emi.emi.screen.widget.config;

import dev.emi.emi.screen.ConfigScreen;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class IntWidget extends ConfigEntryWidget {
   public final IntEdit edit;

   public IntWidget(Component name, List<ClientTooltipComponent> tooltip, Supplier<String> search, ConfigScreen.Mutator<Integer> mutator) {
      super(name, tooltip, search, 20);
      this.edit = new IntEdit(150, () -> mutator.get(), i -> mutator.set(i));
      this.setChildren(List.of(this.edit.text, this.edit.up, this.edit.down));
   }

   @Override
   public void update(int y, int x, int width, int height) {
      int right = x + width;
      this.edit.setPosition(right - 150, y);
   }
}
