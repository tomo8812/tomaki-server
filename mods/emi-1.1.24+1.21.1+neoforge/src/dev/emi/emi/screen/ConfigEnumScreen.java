package dev.emi.emi.screen;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.widget.config.EmiNameWidget;
import dev.emi.emi.screen.widget.config.ListWidget;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class ConfigEnumScreen<T> extends Screen {
   private final ConfigScreen last;
   private final List<ConfigEnumScreen.Entry<T>> entries;
   private final Consumer<T> selection;
   private ListWidget list;

   public ConfigEnumScreen(ConfigScreen last, List<ConfigEnumScreen.Entry<T>> entries, Consumer<T> selection) {
      super(EmiPort.translatable("screen.emi.config"));
      this.last = last;
      this.entries = entries;
      this.selection = selection;
   }

   public void init() {
      super.init();
      this.addRenderableOnly(new EmiNameWidget(this.width / 2, 16));
      int w = 200;
      int x = (this.width - w) / 2;
      this.addRenderableWidget(EmiPort.newButton(x, this.height - 30, w, 20, EmiPort.translatable("gui.done"), button -> this.onClose()));
      this.list = new ListWidget(this.minecraft, this.width, this.height, 40, this.height - 40);

      for (ConfigEnumScreen.Entry<T> e : this.entries) {
         this.list.addEntry(new ConfigEnumScreen.SelectionWidget<>(this, e));
      }

      this.addWidget(this.list);
   }

   public void render(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      this.list.setScrollAmount(this.list.getScrollAmount());
      super.render(context.raw(), mouseX, mouseY, delta);
      this.list.render(context.raw(), mouseX, mouseY, delta);
      if (this.list.getHoveredEntry() instanceof ConfigEnumScreen.SelectionWidget<?> widget && widget.button.isHovered()) {
         EmiRenderHelper.drawTooltip(this, context, widget.tooltip, mouseX, mouseY);
      }
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.last);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.onClose();
         return true;
      } else if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
         this.onClose();
         return true;
      } else {
         return keyCode == 258 ? false : super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public record Entry<T>(T value, Component name, List<ClientTooltipComponent> tooltip) {
   }

   public static class SelectionWidget<T> extends ListWidget.Entry {
      private final Button button;
      private final List<ClientTooltipComponent> tooltip;

      public SelectionWidget(ConfigEnumScreen<T> screen, ConfigEnumScreen.Entry<T> e) {
         this.button = EmiPort.newButton(0, 0, 200, 20, e.name(), t -> {
            screen.selection.accept(e.value());
            screen.onClose();
         });
         this.tooltip = e.tooltip();
      }

      public List<? extends GuiEventListener> children() {
         return List.of(this.button);
      }

      @Override
      public void render(GuiGraphics raw, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
         this.button.y = y;
         this.button.x = x + width / 2 - this.button.getWidth() / 2;
         this.button.render(raw, mouseX, mouseY, delta);
      }

      @Override
      public int getHeight() {
         return 20;
      }
   }
}
