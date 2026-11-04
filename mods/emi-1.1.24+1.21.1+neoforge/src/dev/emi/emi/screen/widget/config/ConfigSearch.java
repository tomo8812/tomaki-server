package dev.emi.emi.screen.widget.config;

import dev.emi.emi.EmiPort;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class ConfigSearch {
   public final ConfigSearch.ConfigSearchWidgetField field;

   public ConfigSearch(int x, int y, int width, int height) {
      Minecraft client = Minecraft.getInstance();
      this.field = new ConfigSearch.ConfigSearchWidgetField(client.font, x, y, width, height, EmiPort.literal(""));
      this.field.setResponder(s -> {
         if (s.length() > 0) {
            this.field.setSuggestion("");
         } else {
            this.field.setSuggestion(I18n.get("emi.search_config", new Object[0]));
         }
      });
      this.field.setSuggestion(I18n.get("emi.search_config", new Object[0]));
   }

   public void setText(String query) {
      this.field.setValue(query);
   }

   public String getSearch() {
      return this.field.getValue();
   }

   private class ConfigSearchWidgetField extends EditBox {
      public ConfigSearchWidgetField(Font textRenderer, int x, int y, int width, int height, Component text) {
         super(textRenderer, x, y, width, height, text);
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         if (button == 1 && this.isMouseOver(mouseX, mouseY)) {
            this.setValue("");
            EmiPort.focus(this, true);
            return true;
         } else {
            return super.mouseClicked(mouseX, mouseY, button);
         }
      }
   }
}
