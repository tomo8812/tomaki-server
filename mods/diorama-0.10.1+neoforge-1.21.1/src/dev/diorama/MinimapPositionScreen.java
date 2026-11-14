package dev.diorama;

import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class MinimapPositionScreen extends Screen {
   private static final int BOX = 1354612157;
   private static final int BOX_HOVERED = 1694498815;
   private static final int BOX_HELD = -2097152001;
   private static final Settings.Corner[] CORNERS = Settings.Corner.values();
   private final @Nullable Screen parent;
   private final Settings.Corner startCorner;
   private final int startX;
   private final int startY;
   private final boolean startCentered;
   private boolean kept;
   private boolean dragging;
   private int grabX;
   private int grabY;
   private @Nullable Button corner;

   public MinimapPositionScreen(@Nullable Screen parent) {
      super(Component.translatable("diorama.position.title"));
      this.parent = parent;
      Settings s = Settings.get();
      this.startCorner = s.minimapCorner;
      this.startX = s.minimapOffsetX;
      this.startY = s.minimapOffsetY;
      this.startCentered = s.minimapCentered;
   }

   protected void init() {
      this.dragging = false;
      MinimapHud.holdSize(0);
      int y = this.height / 6 + 143;
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.done()).bounds(this.width / 2 - 155, y, 150, 20).build());
      this.corner = (Button)this.addRenderableWidget(
         Button.builder(this.cornerLabel(), b -> this.nextCorner())
            .bounds(this.width / 2 + 5, y, 150, 20)
            .tooltip(Tooltip.create(Component.translatable("diorama.settings.corner.tooltip")))
            .build()
      );
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose()).bounds(this.width / 2 + 5, y + 25, 150, 20).build());
   }

   private Component cornerLabel() {
      return Component.empty().append(Component.translatable("diorama.settings.corner")).append(": ").append(placeName());
   }

   static Component placeName() {
      Settings s = Settings.get();
      return inCorner(s)
         ? Component.translatable("diorama.settings.corner." + s.minimapCorner.name().toLowerCase(Locale.ROOT))
         : Component.translatable("diorama.settings.position.custom");
   }

   private static boolean inCorner(Settings s) {
      return !s.minimapCentered && s.minimapOffsetX == 6 && s.minimapOffsetY == 6;
   }

   private void nextCorner() {
      Settings s = Settings.get();
      s.minimapCorner = inCorner(s) ? CORNERS[(s.minimapCorner.ordinal() + 1) % CORNERS.length] : CORNERS[0];
      s.minimapOffsetX = 6;
      s.minimapOffsetY = 6;
      s.minimapCentered = false;
      this.updateCorner();
   }

   private void updateCorner() {
      if (this.corner != null) {
         this.corner.setMessage(this.cornerLabel());
      }
   }

   private void done() {
      this.kept = true;
      Settings.get().save();
      this.minecraft.setScreen(this.parent);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void removed() {
      MinimapHud.holdSize(0);
      if (!this.kept) {
         Settings s = Settings.get();
         s.minimapCorner = this.startCorner;
         s.minimapOffsetX = this.startX;
         s.minimapOffsetY = this.startY;
         s.minimapCentered = this.startCentered;
      }
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      if (MinimapHud.shown() == null) {
         super.renderBackground(graphics, mouseX, mouseY, a);
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      int[] box = MinimapHud.shown();
      if (box != null) {
         boolean hovered = this.over(box, mouseX, mouseY);
         graphics.fill(box[0], box[1], box[0] + box[2], box[1] + box[3], this.dragging ? -2097152001 : (hovered ? 1694498815 : 1354612157));
      }

      super.render(graphics, mouseX, mouseY, a);
      Component message = Component.translatable(box == null ? "diorama.position.not_in_world" : "diorama.position.help");
      graphics.drawCenteredString(this.font, message, this.width / 2, this.height / 6 + 128, -1);
   }

   private boolean over(int[] box, double x, double y) {
      return x >= box[0] && x < box[0] + box[2] && y >= box[1] && y < box[1] + box[3];
   }

   private boolean pressed(double x, double y, int button) {
      int[] box = MinimapHud.shown();
      if (button == 0 && box != null && this.over(box, x, y)) {
         this.dragging = true;
         this.grabX = box[0] - (int)x;
         this.grabY = box[1] - (int)y;
         MinimapHud.holdSize(box[2]);
         return true;
      } else {
         return false;
      }
   }

   private boolean dragged(double mouseX, double mouseY, int button) {
      int[] box = MinimapHud.shown();
      if (this.dragging && button == 0 && box != null) {
         Settings s = Settings.get();
         int x = (int)mouseX + this.grabX;
         int y = (int)mouseY + this.grabY;
         int size = box[2];
         int fromBottom = this.height - y - box[3];
         boolean bottom = y > fromBottom;
         boolean right;
         if (s.minimapCentered) {
            right = s.minimapCorner == Settings.Corner.TOP_RIGHT || s.minimapCorner == Settings.Corner.BOTTOM_RIGHT;
         } else {
            int fromRight = this.width - x - size;
            right = x > fromRight;
            s.minimapOffsetX = Math.max(6, right ? fromRight : x);
         }

         s.minimapOffsetY = Math.max(6, bottom ? fromBottom : y);
         s.minimapCorner = bottom
            ? (right ? Settings.Corner.BOTTOM_RIGHT : Settings.Corner.BOTTOM_LEFT)
            : (right ? Settings.Corner.TOP_RIGHT : Settings.Corner.TOP_LEFT);
         this.updateCorner();
         return true;
      } else {
         return false;
      }
   }

   private boolean released(int button) {
      if (this.dragging && button == 0) {
         this.dragging = false;
         MinimapHud.holdSize(0);
         return true;
      } else {
         return false;
      }
   }

   private boolean keyDown(int key) {
      if (key != 67) {
         return false;
      }

      Settings s = Settings.get();
      s.minimapCentered = !s.minimapCentered;
      this.updateCorner();
      return true;
   }

   public boolean mouseClicked(double x, double y, int button) {
      return super.mouseClicked(x, y, button) || this.pressed(x, y, button);
   }

   public boolean mouseReleased(double x, double y, int button) {
      return this.released(button) || super.mouseReleased(x, y, button);
   }

   public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
      return this.dragged(x, y, button) || super.mouseDragged(x, y, button, dx, dy);
   }

   public boolean keyPressed(int key, int scancode, int modifiers) {
      return this.keyDown(key) || super.keyPressed(key, scancode, modifiers);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
