package net.satisfy.bakery.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.bakery.Bakery;
import net.satisfy.bakery.core.block.entity.StreetSignBlockEntity;
import net.satisfy.bakery.core.network.PacketHandler;
import net.satisfy.bakery.core.network.packet.SetStreetSignTextPacket;

public class StreetSignEditGui extends Screen {
   private final StreetSignBlockEntity entity;
   private final List<EditBox> textFields = new ArrayList<>();

   public StreetSignEditGui(StreetSignBlockEntity entity) {
      super(Component.translatable("gui.bakery.street_sign.edit"));
      this.entity = entity;
   }

   protected void init() {
      int[] offsetX = new int[]{75, 75, 75};
      int[] offsetY = new int[]{-15, -12, -15};

      for (int i = 0; i < 3; i++) {
         EditBox box = new EditBox(this.font, this.width / 2 - 100 + offsetX[i], this.height / 2 - 30 + i * 24 + offsetY[i], 200, 20, Component.literal(""));
         box.setValue(this.entity.getText(i).getString());
         box.setMaxLength(8);
         box.setBordered(false);
         box.setTextColor(15255954);
         this.addRenderableWidget(box);
         this.textFields.add(box);
      }

      this.setInitialFocus((GuiEventListener)this.textFields.get(0));
      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.bakery.street_sign.done"), button -> this.onClose())
            .bounds(this.width / 2 - 50, this.height - 40, 100, 20)
            .build()
      );
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.renderMenuBackground(graphics);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(graphics, mouseX, mouseY, partialTick);
      ResourceLocation texture = Bakery.identifier("textures/block/street_sign_front.png");
      int w = 128;
      int h = 128;
      int x = (int)(this.width / 2.0 - 65.0);
      int y = (int)(this.height / 2.0 - 65.0);
      graphics.blit(texture, x, y, w, h, 0.0F, 0.0F, 16, 16, 16, 16);
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

   public void onClose() {
      List<String> texts = this.textFields.stream().<String>map(EditBox::getValue).toList();
      PacketHandler.sendToServer(new SetStreetSignTextPacket(this.entity.getBlockPos(), texts));
      super.onClose();
   }
}
