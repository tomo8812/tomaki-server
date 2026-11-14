package dev.diorama;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class TeleportCommandScreen extends Screen {
   private static final int WIDTH = 260;
   private static final int LABEL = -6250336;
   private final Screen parent;
   private @Nullable EditBox command;

   public TeleportCommandScreen(Screen parent) {
      super(Component.translatable("diorama.settings.teleport_command"));
      this.parent = parent;
   }

   private int left() {
      return this.width / 2 - 130;
   }

   private int top() {
      return this.height / 2 - 40;
   }

   protected void init() {
      int left = this.left();
      int top = this.top();
      String typed = this.command != null ? this.command.getValue() : "/" + Settings.get().teleportCommand;
      EditBox box = new EditBox(this.font, left, top + 16, 260, 20, Component.translatable("diorama.settings.teleport_command"));
      box.setMaxLength(257);
      box.setValue(typed);
      box.setHint(Component.literal("/tp @s {x} {y} {z}"));
      this.command = (EditBox)this.addRenderableWidget(box);
      int third = 84;
      this.addRenderableWidget(Button.builder(Component.translatable("diorama.waypoint.save"), b -> this.save()).bounds(left, top + 64, third, 20).build());
      this.addRenderableWidget(
         Button.builder(Component.translatable("diorama.teleport_command.reset"), b -> box.setValue("/tp @s {x} {y} {z}"))
            .bounds(left + third + 4, top + 64, third, 20)
            .build()
      );
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose()).bounds(left + 260 - third, top + 64, third, 20).build());
      this.setInitialFocus(box);
   }

   private void save() {
      if (this.command != null) {
         Settings settings = Settings.get();
         settings.teleportCommand = Settings.cleanCommand(this.command.getValue());
         settings.save();
      }

      this.onClose();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.render(graphics, mouseX, mouseY, a);
      int left = this.left();
      int top = this.top();
      graphics.drawCenteredString(this.font, this.title, this.width / 2, top - 12, -1);
      graphics.drawString(this.font, Component.translatable("diorama.teleport_command.label"), left, top + 5, -6250336, true);
      graphics.drawString(this.font, Component.translatable("diorama.teleport_command.hint"), left, top + 42, -6250336, true);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
