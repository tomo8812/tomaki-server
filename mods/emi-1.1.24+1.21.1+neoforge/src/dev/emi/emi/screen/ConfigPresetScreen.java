package dev.emi.emi.screen;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.com.unascribed.qdcss.QDCSS;
import dev.emi.emi.config.ConfigPresets;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.widget.config.EmiNameWidget;
import dev.emi.emi.screen.widget.config.ListWidget;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class ConfigPresetScreen extends Screen {
   private final ConfigScreen last;
   private ListWidget list;
   public Button resetButton;

   public ConfigPresetScreen(ConfigScreen last) {
      super(EmiPort.translatable("screen.emi.presets"));
      this.last = last;
   }

   public void init() {
      super.init();
      this.addRenderableOnly(new EmiNameWidget(this.width / 2, 16));
      int w = Math.min(400, this.width - 40);
      int x = (this.width - w) / 2;
      this.resetButton = EmiPort.newButton(x + 2, this.height - 30, w / 2 - 2, 20, EmiPort.translatable("gui.done"), button -> {
         EmiConfig.loadConfig(QDCSS.load("revert", this.last.originalConfig));
         Minecraft client = Minecraft.getInstance();
         this.init(client, client.getWindow().getGuiScaledWidth(), client.getWindow().getGuiScaledHeight());
      });
      this.addRenderableWidget(this.resetButton);
      this.addRenderableWidget(EmiPort.newButton(x + w / 2 + 2, this.height - 30, w / 2 - 2, 20, EmiPort.translatable("gui.done"), button -> this.onClose()));
      this.list = new ListWidget(this.minecraft, this.width, this.height, 40, this.height - 40);

      try {
         for (Field field : ConfigPresets.class.getFields()) {
            EmiConfig.ConfigValue config = field.getDeclaredAnnotation(EmiConfig.ConfigValue.class);
            if (config != null && field.get(null) instanceof Runnable runnable) {
               EmiConfig.ConfigGroup group = field.getDeclaredAnnotation(EmiConfig.ConfigGroup.class);
               if (group != null) {
                  Component translation = EmiPort.translatable("config.emi." + group.value().replace('-', '_'));
                  this.list.addEntry(new ConfigPresetScreen.PresetGroupWidget(translation));
               }

               Component translation = EmiPort.translatable("config.emi." + config.value().replace('-', '_'));
               this.list.addEntry(new ConfigPresetScreen.PresetWidget(runnable, translation, ConfigScreen.getFieldTooltip(field)));
            }
         }
      } catch (Exception var11) {
      }

      this.addWidget(this.list);
      this.updateChanges();
   }

   public void render(GuiGraphics raw, int mouseX, int mouseY, float delta) {
      EmiDrawContext context = EmiDrawContext.wrap(raw);
      this.list.setScrollAmount(this.list.getScrollAmount());
      this.renderMenuBackground(context.raw());
      this.list.render(context.raw(), mouseX, mouseY, delta);
      super.render(context.raw(), mouseX, mouseY, delta);
      if (this.list.getHoveredEntry() instanceof ConfigPresetScreen.PresetWidget widget && widget.button.isHovered()) {
         EmiRenderHelper.drawTooltip(this, context, widget.tooltip, mouseX, mouseY);
      }
   }

   public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
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

   public void updateChanges() {
      String[] oLines = this.last.originalConfig.split("\n\n");
      String[] cLines = EmiConfig.getSavedConfig().split("\n\n");
      int different = 0;

      for (int i = 0; i < oLines.length && i < cLines.length; i++) {
         if (!oLines[i].equals(cLines[i])) {
            different++;
         }
      }

      this.resetButton.active = different > 0;
      this.resetButton.setMessage(EmiPort.translatable("screen.emi.config.reset", different));
   }

   public class PresetGroupWidget extends ListWidget.Entry {
      private final Component text;

      public PresetGroupWidget(Component text) {
         this.text = text;
      }

      public List<? extends GuiEventListener> children() {
         return List.of();
      }

      @Override
      public void render(GuiGraphics raw, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
         EmiDrawContext context = EmiDrawContext.wrap(raw);
         context.drawCenteredTextWithShadow(this.text, x + width / 2, y + 3, -1);
      }

      @Override
      public int getHeight() {
         return 20;
      }
   }

   public class PresetWidget extends ListWidget.Entry {
      private final Button button;
      private final List<ClientTooltipComponent> tooltip;

      public PresetWidget(Runnable runnable, Component name, List<ClientTooltipComponent> tooltip) {
         this.button = EmiPort.newButton(0, 0, 200, 20, name, t -> {
            runnable.run();
            ConfigPresetScreen.this.updateChanges();
         });
         this.tooltip = tooltip;
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
