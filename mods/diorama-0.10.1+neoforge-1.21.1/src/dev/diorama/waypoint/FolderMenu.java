package dev.diorama.waypoint;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import org.jspecify.annotations.Nullable;

public final class FolderMenu {
   private static final int ROW = 12;
   private static final int PAD = 3;
   private static final int TEXT = -1;
   private static final int COUNT = -6577234;
   private static final int HIDDEN = -8355712;
   private static final int CURRENT = -171;
   private static final int SCROLLBAR = 2;
   private final List<FolderMenu.Entry> entries;
   private final @Nullable String current;
   private final Font font;
   private final int x;
   private final int y;
   private final int width;
   private final int shown;
   private int scroll;
   private int cursor = -1;

   public FolderMenu(Font font, List<FolderMenu.Entry> entries, @Nullable String current, int x, int top, int bottom, int width, int screenHeight) {
      this.font = font;
      this.entries = List.copyOf(entries);
      this.current = current;
      this.x = x;
      this.width = width;
      int below = (screenHeight - bottom - 5) / 12;
      int above = (top - 5) / 12;
      boolean up = below < this.entries.size() && above > below;
      this.shown = Math.max(1, Math.min(this.entries.size(), up ? above : below));
      this.y = up ? top - 2 - this.height() : bottom + 2;

      for (int i = 0; i < this.entries.size(); i++) {
         if (this.entries.get(i).key().equals(current)) {
            this.scroll = Math.max(0, Math.min(i - this.shown / 2, this.entries.size() - this.shown));
         }
      }
   }

   private int height() {
      return this.shown * 12 + 2;
   }

   public boolean contains(double mouseX, double mouseY) {
      return mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height();
   }

   public FolderMenu.@Nullable Entry under(double mouseX, double mouseY) {
      if (!this.contains(mouseX, mouseY)) {
         return null;
      }

      int row = (int)Math.floor((mouseY - this.y - 1.0) / 12.0);
      return row >= 0 && row < this.shown ? this.entries.get(this.scroll + row) : null;
   }

   public FolderMenu.@Nullable Entry keyPressed(int key) {
      if (key != 265 && key != 264) {
         boolean enter = key == 257 || key == 335;
         return enter && this.cursor >= 0 ? this.entries.get(this.cursor) : null;
      }

      int from = this.cursor;

      for (int i = 0; from < 0 && i < this.entries.size(); i++) {
         if (this.entries.get(i).key().equals(this.current)) {
            from = i;
         }
      }

      int step = key == 265 ? -1 : 1;
      this.cursor = from < 0 ? (step > 0 ? 0 : this.entries.size() - 1) : Math.max(0, Math.min(from + step, this.entries.size() - 1));
      this.scroll = Math.max(this.cursor - this.shown + 1, Math.min(this.scroll, this.cursor));
      return null;
   }

   public void scroll(double amount) {
      this.scroll = Math.max(0, Math.min(this.scroll - (int)Math.signum(amount), this.entries.size() - this.shown));
   }

   public void draw(GuiGraphics graphics, int mouseX, int mouseY) {
      int height = this.height();
      graphics.fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + height + 1, -12959672);
      graphics.fill(this.x, this.y, this.x + this.width, this.y + height, -267381736);
      boolean scrolls = this.entries.size() > this.shown;
      int right = this.x + this.width - 3 - 2 - (scrolls ? 3 : 0);
      FolderMenu.Entry hovered = this.under(mouseX, mouseY);

      for (int i = 0; i < this.shown; i++) {
         FolderMenu.Entry entry = this.entries.get(this.scroll + i);
         int top = this.y + 1 + i * 12;
         if (entry == hovered || this.scroll + i == this.cursor) {
            graphics.fill(this.x + 1, top, this.x + this.width - 1, top + 12, -13813170);
         }

         int room = right - (this.x + 3 + 2);
         if (entry.count() >= 0) {
            String count = Integer.toString(entry.count());
            graphics.drawString(this.font, count, right - this.font.width(count), top + 2, -6577234, false);
            room -= this.font.width(count) + 6;
         }

         int colour = entry.key().equals(this.current) ? -171 : (entry.hidden() ? -8355712 : -1);
         graphics.drawString(this.font, this.fit(entry.name(), room), this.x + 3 + 2, top + 2, colour, false);
      }

      if (scrolls) {
         int track = height - 2;
         int handle = Math.max(4, track * this.shown / this.entries.size());
         int at = (track - handle) * this.scroll / (this.entries.size() - this.shown);
         int barX = this.x + this.width - 1 - 2;
         graphics.fill(barX, this.y + 1 + at, barX + 2, this.y + 1 + at + handle, -8353648);
      }
   }

   private String fit(String text, int room) {
      if (this.font.width(text) <= room) {
         return text;
      }

      String ellipsis = CommonComponents.ELLIPSIS.getString();
      return this.font.plainSubstrByWidth(text, Math.max(0, room - this.font.width(ellipsis))) + ellipsis;
   }

   public float @Nullable [] centreOf(String name) {
      for (int i = 0; i < this.shown; i++) {
         if (this.entries.get(this.scroll + i).name().equals(name)) {
            return new float[]{this.x + this.width / 2.0F, this.y + 1 + i * 12 + 6.0F};
         }
      }

      return null;
   }

   public List<String> labels() {
      return this.entries.stream().map(entry -> entry.count() < 0 ? entry.name() : entry.name() + " " + entry.count()).toList();
   }

   public int shown() {
      return this.shown;
   }

   public record Entry(String key, String name, int count, boolean hidden) {
   }
}
