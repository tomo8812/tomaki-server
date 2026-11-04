package dev.emi.emi.screen.widget.config;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.config.IntGroup;
import dev.emi.emi.screen.ConfigScreen;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class IntGroupWidget extends ConfigEntryWidget {
   public final IntGroup group;
   public final List<IntEdit> edits = Lists.newArrayList();

   public IntGroupWidget(Component name, List<ClientTooltipComponent> tooltip, Supplier<String> search, ConfigScreen.Mutator<IntGroup> mutator) {
      super(name, tooltip, search, 20);
      this.group = mutator.get();
      int width = this.getWidth();
      List<GuiEventListener> children = Lists.newArrayList();

      for (int i = 0; i < this.group.size; i++) {
         int f = i;
         IntEdit edit = new IntEdit(width, () -> this.group.values.getInt(f), v -> {
            this.group.values.set(f, v);
            mutator.set(this.group);
         });
         this.edits.add(edit);
         children.add(edit.text);
         children.add(edit.up);
         children.add(edit.down);
      }

      this.setChildren(children);
   }

   public int getSpacing() {
      return this.group.values.size() > 2 ? 19 : 20;
   }

   public int getWidth() {
      return this.group.values.size() > 2 ? 40 : 99;
   }

   @Override
   public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
      for (int i = 0; i < this.edits.size(); i++) {
         IntEdit e = this.edits.get(i);
         if (e.contains(mouseX, mouseY)) {
            return List.of(ClientTooltipComponent.create(EmiPort.ordered(this.group.getValueTranslation(i))));
         }
      }

      return super.getTooltip(mouseX, mouseY);
   }

   @Override
   public void update(int y, int x, int width, int height) {
      int spacing = this.getSpacing();
      int w = this.getWidth();
      int es = this.edits.size();

      for (int i = 0; i < es; i++) {
         int fromRight = es - i;
         int extra = 0;
         if (es > 2 && i < 2) {
            extra = -1;
         }

         this.edits.get(i).setPosition(x + width - w * fromRight - spacing * (fromRight - 1) + extra, y);
      }
   }
}
