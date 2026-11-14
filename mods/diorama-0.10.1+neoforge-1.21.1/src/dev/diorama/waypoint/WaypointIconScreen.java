package dev.diorama.waypoint;

import dev.diorama.render.Pins;
import dev.diorama.render.WaypointIcons;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class WaypointIconScreen extends Screen {
   private static final int CELL = 34;
   private static final int MIN_COLUMNS = 6;
   private static final int LABEL = -6250336;
   private final Screen parent;
   private final Waypoint sample = new Waypoint();
   private final String current;
   private final Consumer<String> picked;
   private final List<String> icons = new ArrayList<>();

   public WaypointIconScreen(Screen parent, Waypoint shown, String current, Consumer<String> picked) {
      super(Component.translatable("diorama.waypoint.icon_title"));
      this.parent = parent;
      this.current = current;
      this.picked = picked;
      this.sample.name = shown.name;
      this.sample.initials = shown.initials;
      this.sample.color = shown.color;
      this.icons.add("");
      this.icons.addAll(WaypointIcons.all());
   }

   private int columns() {
      int rows = Math.max(1, (this.height - 64) / 34);
      return Math.max(6, (this.icons.size() + rows - 1) / rows);
   }

   private int rows() {
      return (this.icons.size() + this.columns() - 1) / this.columns();
   }

   private int left() {
      return this.width / 2 - this.columns() * 34 / 2;
   }

   private int top() {
      return this.height / 2 - (this.rows() * 34 + 28) / 2;
   }

   protected void init() {
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose()).bounds(this.width / 2 - 50, this.top() + this.rows() * 34 + 8, 100, 20).build()
      );
   }

   private int cellAt(double mouseX, double mouseY) {
      int col = (int)Math.floor((mouseX - this.left()) / 34.0);
      int row = (int)Math.floor((mouseY - this.top()) / 34.0);
      int i = row * this.columns() + col;
      return col >= 0 && col < this.columns() && row >= 0 && i < this.icons.size() ? i : -1;
   }

   public float[] cellCentre(String id) {
      int i = this.icons.indexOf(id);
      return new float[]{this.left() + i % this.columns() * 34 + 17.0F, this.top() + i / this.columns() * 34 + 17.0F};
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      super.render(graphics, mouseX, mouseY, a);
      int left = this.left();
      int top = this.top();
      graphics.drawCenteredString(this.font, this.title, this.width / 2, top - 14, -1);
      ScreenArt.list(graphics, left - 2, top - 2, left + this.columns() * 34 + 2, top + this.rows() * 34 + 2);
      int hovered = this.cellAt(mouseX, mouseY);

      for (int i = 0; i < this.icons.size(); i++) {
         int x = left + i % this.columns() * 34;
         int y = top + i / this.columns() * 34;
         String id = this.icons.get(i);
         if (id.equals(this.current)) {
            ScreenArt.selection(graphics, x, y, x + 34, y + 34);
         } else if (i == hovered) {
            graphics.fill(x, y, x + 34, y + 34, 822083583);
         }

         this.sample.icon = id;
         Pins.badge(graphics, this.sample, x + 17.0F, y + 17.0F, 15);
      }

      if (hovered >= 0) {
         this.setTooltipForNextRenderPass(name(this.icons.get(hovered)));
      }
   }

   public static Component name(String id) {
      if (id.isEmpty()) {
         return Component.translatable("diorama.waypoint.icon_none");
      }

      String path = id.substring(id.indexOf(58) + 1);
      String fallback = path.replace('_', ' ');
      fallback = fallback.isEmpty() ? id : Character.toUpperCase(fallback.charAt(0)) + fallback.substring(1);
      return Component.translatableWithFallback("diorama.waypoint.icon." + id.replace(':', '.'), fallback);
   }

   private boolean clicked(double mouseX, double mouseY, int button) {
      int i = this.cellAt(mouseX, mouseY);
      if (i >= 0 && button == 0) {
         this.picked.accept(this.icons.get(i));
         this.onClose();
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.clicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
