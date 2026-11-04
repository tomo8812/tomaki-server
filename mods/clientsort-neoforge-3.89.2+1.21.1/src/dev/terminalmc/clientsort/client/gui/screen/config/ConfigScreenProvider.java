package dev.terminalmc.clientsort.client.gui.screen.config;

import dev.terminalmc.clientsort.util.Localization;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

public class ConfigScreenProvider {
   private ConfigScreenProvider() {
      throw new UnsupportedOperationException("This class cannot be instantiated.");
   }

   public static Screen getConfigScreen(Screen parent) {
      try {
         return ClothScreenProvider.getConfigScreen(parent);
      } catch (NoClassDefFoundError ignored) {
         return new ConfigScreenProvider.BackupScreen(parent, "installCloth", "https://modrinth.com/project/9s6osm5g");
      }
   }

   private static class BackupScreen extends Screen {
      private final Screen parent;
      private final String modKey;
      private final String modUrl;

      public BackupScreen(Screen parent, String modKey, String modUrl) {
         super(Localization.localized("name"));
         this.parent = parent;
         this.modKey = modKey;
         this.modUrl = modUrl;
      }

      public void init() {
         MultiLineTextWidget messageWidget = new MultiLineTextWidget(
            this.width / 2 - 120, this.height / 2 - 40, Localization.localized("message", this.modKey), Minecraft.getInstance().font
         );
         messageWidget.setMaxWidth(240);
         messageWidget.setCentered(true);
         this.addRenderableWidget(messageWidget);
         Button openLinkButton = Button.builder(
               Localization.localized("message", "viewModrinth"), button -> Minecraft.getInstance().setScreen(new ConfirmLinkScreen(open -> {
                  if (open) {
                     Util.getPlatform().openUri(this.modUrl);
                  }

                  this.onClose();
               }, this.modUrl, true))
            )
            .pos(this.width / 2 - 120, this.height / 2)
            .size(115, 20)
            .build();
         this.addRenderableWidget(openLinkButton);
         Button exitButton = Button.builder(CommonComponents.GUI_OK, button -> this.onClose()).pos(this.width / 2 + 5, this.height / 2).size(115, 20).build();
         this.addRenderableWidget(exitButton);
      }

      public void onClose() {
         Minecraft.getInstance().setScreen(this.parent);
      }
   }

   private static class DisabledScreen extends Screen {
      private final Screen parent;

      public DisabledScreen(Screen parent) {
         super(Localization.localized("name"));
         this.parent = parent;
      }

      public void init() {
         MultiLineTextWidget messageWidget = new MultiLineTextWidget(
            this.width / 2 - 120, this.height / 2 - 40, Localization.localized("message", "configScreenDisabled"), Minecraft.getInstance().font
         );
         messageWidget.setMaxWidth(240);
         messageWidget.setCentered(true);
         this.addRenderableWidget(messageWidget);
         Button exitButton = Button.builder(CommonComponents.GUI_OK, button -> this.onClose()).pos(this.width / 2 - 115, this.height / 2).size(230, 20).build();
         this.addRenderableWidget(exitButton);
      }

      public void onClose() {
         Minecraft.getInstance().setScreen(this.parent);
      }
   }
}
