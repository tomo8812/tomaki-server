package net.satisfy.farm_and_charm.client.gui;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.foundation.text.SetTextPacket;

public class PetBowlEditGui extends Screen {
   private final PetBowlBlockEntity entity;
   private EditBox textField;

   public PetBowlEditGui(PetBowlBlockEntity entity) {
      super(Component.translatable("gui.farm_and_charm.pet_bowl.edit"));
      this.entity = entity;
   }

   protected void init() {
      int centerX = this.width / 2;
      int centerY = this.height / 2;
      this.textField = new EditBox(this.font, centerX - 100, centerY - 10, 200, 20, Component.literal(""));
      this.textField.setMaxLength(6);
      this.textField.setValue(this.entity.getText().getString());
      this.addRenderableWidget(this.textField);
      this.setInitialFocus(this.textField);
      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.farm_and_charm.text_edit.done"), button -> this.onClose())
            .bounds(centerX - 50, centerY + 20, 100, 20)
            .build()
      );
   }

   public void onClose() {
      PacketHandler.sendToServer(new SetTextPacket(this.entity.getBlockPos(), List.of(this.textField.getValue())));
      super.onClose();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode != 256 && keyCode != 257 && keyCode != 335) {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }

      this.onClose();
      return true;
   }
}
