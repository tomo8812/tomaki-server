package org.embeddedt.modernfix.screen;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ModernFixConfigScreen extends Screen {
   private OptionList optionList;
   private Screen lastScreen;
   public boolean madeChanges = false;
   private Button doneButton;
   private Button wikiButton;
   private double lastScrollAmount = 0.0;

   public ModernFixConfigScreen(Screen lastScreen) {
      super(Component.translatable("modernfix.config"));
      this.lastScreen = lastScreen;
   }

   protected void init() {
      this.optionList = new OptionList(this, this.minecraft);
      this.optionList.setScrollAmount(this.lastScrollAmount);
      this.addWidget(this.optionList);
      this.wikiButton = new Builder(
            Component.translatable("modernfix.config.wiki"),
            arg -> Util.getPlatform().openUri("https://github.com/embeddedt/ModernFix/wiki/Summary-of-Patches")
         )
         .pos(this.width / 2 - 155, this.height - 29)
         .size(150, 20)
         .build();
      this.doneButton = new Builder(CommonComponents.GUI_DONE, arg -> this.onClose()).pos(this.width / 2 - 155 + 160, this.height - 29).size(150, 20).build();
      this.addRenderableWidget(this.wikiButton);
      this.addRenderableWidget(this.doneButton);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.optionList.render(guiGraphics, mouseX, mouseY, partialTicks);
      guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 16777215);
      this.doneButton.setMessage((Component)(this.madeChanges ? Component.translatable("modernfix.config.done_restart") : CommonComponents.GUI_DONE));
   }

   public void setLastScrollAmount(double d) {
      this.lastScrollAmount = d;
   }
}
