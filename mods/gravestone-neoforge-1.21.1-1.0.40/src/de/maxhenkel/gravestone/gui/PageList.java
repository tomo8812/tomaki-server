package de.maxhenkel.gravestone.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class PageList {
   private final List<Page> list = new ArrayList<>();

   public PageList(NonNullList<ItemStack> items, ObituaryScreen gui) {
      ItemStack[] temp = new ItemStack[10];
      int i = 0;

      for (ItemStack s : items) {
         temp[i] = s;
         if (++i > 9) {
            this.list.add(new Page(temp, gui));
            temp = new ItemStack[10];
            i = 0;
         }
      }

      if (Stream.of(temp).anyMatch(Objects::nonNull)) {
         this.list.add(new Page(temp, gui));
      }
   }

   public int getPages() {
      return this.list.size();
   }

   public void drawPage(GuiGraphics guiGraphics, int p, int mouseX, int mouseY) {
      if (p >= this.list.size()) {
         p = this.list.size() - 1;
      }

      Page page = this.list.get(p);
      page.drawPage(guiGraphics, p + 1, this.list.size(), mouseX, mouseY);
   }

   @Override
   public String toString() {
      return Arrays.deepToString(this.list.toArray(new Page[0]));
   }
}
