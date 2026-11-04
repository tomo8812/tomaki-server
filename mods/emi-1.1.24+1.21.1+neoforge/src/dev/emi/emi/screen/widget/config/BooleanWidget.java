package dev.emi.emi.screen.widget.config;

import dev.emi.emi.EmiPort;
import dev.emi.emi.screen.ConfigScreen;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class BooleanWidget extends ConfigEntryWidget {
   private final ConfigScreen.Mutator<Boolean> mutator;
   private Button button;

   public BooleanWidget(Component name, List<ClientTooltipComponent> tooltip, Supplier<String> search, ConfigScreen.Mutator<Boolean> mutator) {
      super(name, tooltip, search, 20);
      this.mutator = mutator;
      this.button = EmiPort.newButton(0, 0, 150, 20, this.getText(), button -> {
         mutator.set(!mutator.get());
         button.setMessage(this.getText());
      });
      this.setChildren(List.of(this.button));
   }

   public Component getText() {
      return this.mutator.get() ? EmiPort.literal("true", ChatFormatting.GREEN) : EmiPort.literal("false", ChatFormatting.RED);
   }

   @Override
   public void update(int y, int x, int width, int height) {
      this.button.x = x + width - this.button.getWidth();
      this.button.y = y;
   }
}
