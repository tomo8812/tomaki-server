package dev.terminalmc.clientsort.client.gui.screen.edit;

import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.gui.TriggerButtonManager;
import dev.terminalmc.clientsort.client.gui.widget.TriggerButton;
import dev.terminalmc.clientsort.util.Localization;
import java.util.LinkedList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;

public class SelectorScreen extends Screen {
   private final Screen lastScreen;
   private final AbstractContainerScreen<?> underlay;
   private final LinkedList<TriggerButton> buttons = new LinkedList<>();

   public SelectorScreen(AbstractContainerScreen<?> underlay) {
      this(underlay, underlay);
   }

   public SelectorScreen(AbstractContainerScreen<?> underlay, Screen lastScreen) {
      super(Localization.localized("title", "groupSelector"));
      this.font = Minecraft.getInstance().font;
      this.underlay = underlay;
      this.lastScreen = lastScreen;
   }

   public void init() {
      super.init();
      this.underlay.init(Minecraft.getInstance(), this.width, this.height);
      this.reloadButtons();
      this.rebuildGui();
   }

   private void reloadButtons() {
      this.buttons.clear();
      this.buttons.addAll(TriggerButtonManager.getContainerButtons());
      this.buttons.addAll(TriggerButtonManager.getPlayerButtons());
   }

   private void rebuildGui() {
      this.clearWidgets();
      StringWidget titleWidget = new StringWidget(0, 2, this.width, 9, this.title, this.font);
      this.addRenderableWidget(titleWidget);
      CycleButton<Boolean> toggleButton = CycleButton.booleanBuilder(
            Localization.localized("editor", "enabled").withStyle(ChatFormatting.GREEN),
            Localization.localized("editor", "disabled").withStyle(ChatFormatting.RED)
         )
         .withInitialValue(Config.options().showButtons)
         .create(this.width / 2 - 125, this.height - 22, 120, 20, Localization.localized("editor", "buttons"), (buttons, status) -> {
            Config.options().showButtons = status;
            Config.save();
            this.init();
         });
      this.addRenderableWidget(toggleButton);
      Button cancelButton = Button.builder(CommonComponents.GUI_BACK, button -> this.onClose()).pos(this.width / 2 + 5, this.height - 22).size(120, 20).build();
      this.addRenderableWidget(cancelButton);
   }

   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.underlay.render(graphics, mouseX, mouseY, partialTick);
      super.render(graphics, mouseX, mouseY, partialTick);
      if (Config.options().showButtons) {
         for (TriggerButton cb : this.buttons) {
            cb.renderWidget(graphics, mouseX, mouseY, partialTick);
         }
      }
   }

   protected void renderBlurredBackground(float partialTick) {
      int original = (Integer)Minecraft.getInstance().options.menuBackgroundBlurriness().get();
      Minecraft.getInstance().options.menuBackgroundBlurriness().set(6);
      super.renderBlurredBackground(partialTick);
      Minecraft.getInstance().options.menuBackgroundBlurriness().set(original);
   }

   public void onClose() {
      super.onClose();
      if (this.lastScreen instanceof EditorScreen pes && !Config.options().showButtons) {
         pes.onClose();
      } else {
         this.lastScreen.init(Minecraft.getInstance(), this.width, this.height);
         Minecraft.getInstance().setScreen(this.lastScreen);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
         return true;
      }

      for (TriggerButton cb : this.buttons) {
         if (cb.isMouseOver(mouseX, mouseY)) {
            cb.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onClose();
            cb.openEditScreen();
         }
      }

      return false;
   }
}
