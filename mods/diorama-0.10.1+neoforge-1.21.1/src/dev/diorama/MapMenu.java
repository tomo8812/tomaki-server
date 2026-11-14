package dev.diorama;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class MapMenu {
   private static final int ROW = 12;
   private static final int PAD = 3;
   private static final int TEXT = -1;
   private static final int INFO = -6577234;
   private static final int OFF = -10722451;
   private static final int CONFIRM = -34192;
   private static final int DONE = -8400513;
   private final List<MapMenu.Item> items;
   private final Font font;
   private final int x;
   private final int y;
   private final int width;

   public MapMenu(List<MapMenu.Item> items, Font font, double cursorX, double cursorY, int screenWidth, int screenHeight) {
      this.items = items;
      this.font = font;
      this.width = this.measure();
      int height = this.height();
      this.x = (int)Math.max(0.0, Math.min(cursorX, screenWidth - this.width));
      this.y = (int)Math.max(0.0, Math.min(cursorY, screenHeight - height));
   }

   private int measure() {
      int widest = 0;

      for (MapMenu.Item item : this.items) {
         widest = Math.max(widest, this.font.width(item.label));
         if (item.confirm != null) {
            widest = Math.max(widest, this.font.width(item.confirm));
         }
      }

      return widest + 6 + 4;
   }

   private int height() {
      return this.items.size() * 12 + 2;
   }

   public boolean contains(double mouseX, double mouseY) {
      return mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height();
   }

   private MapMenu.@Nullable Item under(double mouseX, double mouseY) {
      if (!this.contains(mouseX, mouseY)) {
         return null;
      }

      int row = (int)Math.floor((mouseY - this.y - 1.0) / 12.0);
      return row >= 0 && row < this.items.size() ? this.items.get(row) : null;
   }

   public boolean click(double mouseX, double mouseY) {
      MapMenu.Item item = this.under(mouseX, mouseY);
      if (item == null || !item.clickable()) {
         return false;
      } else if (item.stays) {
         item.action.run();
         item.armed = true;
         return false;
      } else if (item.confirm != null && !item.armed) {
         item.armed = true;
         return false;
      } else {
         item.action.run();
         return true;
      }
   }

   public void draw(GuiGraphics graphics, int mouseX, int mouseY) {
      int height = this.height();
      graphics.fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + height + 1, -12959672);
      graphics.fill(this.x, this.y, this.x + this.width, this.y + height, -267381736);
      MapMenu.Item hovered = this.under(mouseX, mouseY);

      for (int i = 0; i < this.items.size(); i++) {
         MapMenu.Item item = this.items.get(i);
         int top = this.y + 1 + i * 12;
         if (item == hovered && item.clickable()) {
            graphics.fill(this.x + 1, top, this.x + this.width - 1, top + 12, -13813170);
         }

         int colour = item.action == null ? (item.active ? -1 : -6577234) : (!item.active ? -10722451 : (!item.armed ? -1 : (item.stays ? -8400513 : -34192)));
         graphics.drawString(this.font, item.shown(), this.x + 3 + 2, top + 2, colour, false);
      }
   }

   public float @Nullable [] centreOf(String label) {
      for (int i = 0; i < this.items.size(); i++) {
         if (this.items.get(i).shown().getString().equals(label)) {
            return new float[]{this.x + this.width / 2.0F, this.y + 1 + i * 12 + 6.0F};
         }
      }

      return null;
   }

   public List<String> labels() {
      return this.items.stream().map(item -> item.shown().getString()).toList();
   }

   public static final class Item {
      private final Component label;
      private final @Nullable Runnable action;
      private final boolean active;
      private final @Nullable Component confirm;
      private final boolean stays;
      private boolean armed;

      private Item(Component label, @Nullable Runnable action, boolean active, @Nullable Component confirm, boolean stays) {
         this.label = label;
         this.action = action;
         this.active = active;
         this.confirm = confirm;
         this.stays = stays;
      }

      public static MapMenu.Item info(Component label) {
         return new MapMenu.Item(label, null, false, null, false);
      }

      public static MapMenu.Item title(Component label) {
         return new MapMenu.Item(label, null, true, null, false);
      }

      public static MapMenu.Item action(Component label, boolean active, Runnable action) {
         return new MapMenu.Item(label, action, active, null, false);
      }

      public static MapMenu.Item confirmed(Component label, Component confirm, Runnable action) {
         return new MapMenu.Item(label, action, true, confirm, false);
      }

      public static MapMenu.Item stays(Component label, Component done, Runnable action) {
         return new MapMenu.Item(label, action, true, done, true);
      }

      private Component shown() {
         return this.armed && this.confirm != null ? this.confirm : this.label;
      }

      private boolean clickable() {
         return this.action != null && this.active;
      }
   }
}
