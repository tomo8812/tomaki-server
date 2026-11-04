package dev.emi.emi.screen.widget.config;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.emi.emi.EmiPort;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.screen.ConfigScreen;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class EmiBindWidget extends ConfigEntryWidget {
   private final ConfigScreen screen;
   private EmiBind bind;
   private List<Button> buttons = Lists.newArrayList();

   public EmiBindWidget(ConfigScreen screen, List<ClientTooltipComponent> tooltip, Supplier<String> search, EmiBind bind) {
      super(EmiPort.translatable(bind.translationKey), tooltip, search, 0);
      this.screen = screen;
      this.bind = bind;
      this.updateButtons();
      this.setChildren(this.buttons);
   }

   private void updateButtons() {
      this.buttons.clear();

      for (int i = 0; i < this.bind.boundKeys.size(); i++) {
         int j = i;
         Button widget = EmiPort.newButton(
            0, 0, 200, 20, this.bind.boundKeys.get(i).getKeyText(ChatFormatting.RESET), button -> this.screen.setActiveBind(this.bind, j)
         );
         this.buttons.add(widget);
      }
   }

   @Override
   public void update(int y, int x, int width, int height) {
      if (this.buttons.size() != this.bind.boundKeys.size()) {
         this.updateButtons();
      }

      int h = 0;

      for (int i = 0; i < this.buttons.size(); i++) {
         Button button = this.buttons.get(i);
         button.x = x + width - 224;
         button.y = y + h;
         if (this.screen.activeBind == this.bind && this.screen.activeBindOffset == i) {
            button.setWidth(200);
            button.x = x + width - 224;
            if (this.screen.lastModifier == 0) {
               button.setMessage(EmiPort.literal("...", ChatFormatting.YELLOW));
            } else {
               button.setMessage(
                  new EmiBind.ModifiedKey(Type.KEYSYM.getOrCreate(this.screen.lastModifier), this.screen.activeModifiers).getKeyText(ChatFormatting.YELLOW)
               );
            }
         } else if (i < this.bind.boundKeys.size()) {
            if (this.bind.boundKeys.get(i).isUnbound() && i > 0) {
               button.setWidth(20);
               button.x = x + width - 20;
               button.y = y;
               button.setMessage(EmiPort.literal("+", ChatFormatting.AQUA));
            } else {
               button.setMessage(this.bind.boundKeys.get(i).getKeyText(ChatFormatting.RESET));
            }
         }

         h += 24;
      }
   }

   @Override
   public int getHeight() {
      if (this.isVisible() && this.isParentVisible()) {
         int size = this.buttons.size() * 24;
         if (this.buttons.size() > 1
            && this.buttons.size() <= this.bind.boundKeys.size()
            && this.bind.boundKeys.get(this.buttons.size() - 1).isUnbound()
            && (this.screen.activeBind != this.bind || this.screen.activeBindOffset != this.buttons.size() - 1)) {
            size -= 24;
         }

         return size - 4;
      } else {
         return 0;
      }
   }
}
