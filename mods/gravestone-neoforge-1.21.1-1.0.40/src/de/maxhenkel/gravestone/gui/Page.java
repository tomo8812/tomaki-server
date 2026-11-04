package de.maxhenkel.gravestone.gui;

import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class Page {
   private static final int ITEM_OFFSET_TOP = 40;
   private static final int PAGE_NUMBER_OFFSET_TOP = 23;
   private ItemStack[] items;
   private ObituaryScreen gui;

   public Page(ItemStack[] it, ObituaryScreen gui) {
      this.gui = gui;
      int arraySize = 10;
      this.items = new ItemStack[10];
      if (it.length < 10) {
         arraySize = it.length;
      }

      for (int i = 0; i < this.items.length && i < arraySize; i++) {
         this.items[i] = it[i];
      }
   }

   public void drawPage(GuiGraphics guiGraphics, int page, int pageCount, int mouseX, int mouseY) {
      this.gui
         .drawCentered(
            guiGraphics,
            this.gui.getFontRenderer(),
            Component.translatable("gui.obituary.title.items").withStyle(ChatFormatting.UNDERLINE),
            this.gui.width / 2,
            this.gui.getGuiTop() + 10,
            ChatFormatting.BLACK.getColor()
         );
      this.gui
         .drawCentered(
            guiGraphics,
            this.gui.getFontRenderer(),
            Component.translatable("gui.obituary.page", new Object[]{page, pageCount}),
            this.gui.width / 2,
            this.gui.getGuiTop() + 23,
            ChatFormatting.DARK_GRAY.getColor()
         );
      int itemStartY = this.gui.getGuiTop() + 40;
      int y = itemStartY;
      int space = 12;

      for (ItemStack s : this.items) {
         if (s != null && !s.isEmpty()) {
            this.gui.drawItem(guiGraphics, Component.translatable(s.getDescriptionId()).withStyle(ChatFormatting.ITALIC), y);
            this.gui.drawItemSize(guiGraphics, Component.literal(String.valueOf(s.getCount())), y);
            y += 12;
         }
      }

      if (mouseX >= this.gui.getGuiLeft() + 15 && mouseX <= this.gui.getGuiLeft() + 163 - 14 && mouseY >= itemStartY && mouseY <= itemStartY + 120) {
         int index = (mouseY + 3 - itemStartY) / 12;
         ItemStack stack = this.items[Math.max(0, Math.min(this.items.length - 1, index))];
         if (stack != null && !stack.isEmpty()) {
            guiGraphics.renderTooltip(this.gui.getFontRenderer(), stack, mouseX, mouseY);
         }
      }
   }

   @Override
   public String toString() {
      return Arrays.toString(this.items);
   }
}
