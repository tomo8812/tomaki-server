package dev.diorama;

import dev.diorama.data.ImageExport;
import dev.diorama.data.TileStore;
import dev.diorama.drawing.Drawing;
import dev.diorama.drawing.DrawingOverlay;
import dev.diorama.drawing.DrawingStore;
import dev.diorama.render.MapViewport;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jspecify.annotations.Nullable;

public final class ExportScreen extends Screen {
   private static final int SIDEBAR = 112;
   private static final int FOOTER = 28;
   private static final int PANEL = -1073741824;
   private static final int EDGE = -12961214;
   private static final int MUTED = -4932408;
   private static final int DIM = -1879048192;
   private static final int GRAB = 4;
   private static final double MIN_BOX = 16.0;
   private static final String[] DETAIL_LABELS = new String[]{"4", "2", "1", "½"};
   private static final int LEFT = 1;
   private static final int RIGHT = 2;
   private static final int TOP = 4;
   private static final int BOTTOM = 8;
   private static final int MOVE = 16;
   private static final int PAN = 32;
   private final MapScreen parent;
   private final int layer;
   private final MapViewport viewport = new MapViewport();
   private final DrawingOverlay drawingOverlay = new DrawingOverlay();
   private double centerU;
   private double centerV;
   private double zoom;
   private boolean whole;
   private double u0;
   private double v0;
   private double u1;
   private double v1;
   private double @Nullable [] explored;
   private @Nullable CompletableFuture<double @Nullable []> finding;
   private @Nullable TileStore boundsStore;
   private int chosenLod = -1;
   private final Button[] detail = new Button[4];
   private @Nullable Button save;
   private boolean withDrawings = true;
   private boolean atNight;
   private int grip;
   private int pressButton = -1;
   private final boolean[] tooBig = new boolean[4];

   public ExportScreen(MapScreen parent, double centerU, double centerV, double zoom, int layer, int width, int height) {
      super(Component.translatable("diorama.export.title"));
      this.parent = parent;
      this.layer = layer;
      this.zoom = zoom;
      this.centerU = centerU + 56.0 / zoom;
      this.centerV = centerV - 14.0 / zoom;
      double halfW = (width - 112) / 2.0 / zoom;
      double halfH = (height - 28) / 2.0 / zoom;
      this.u0 = this.centerU - halfW * 0.75;
      this.u1 = this.centerU + halfW * 0.75;
      this.v0 = this.centerV - halfH * 0.75;
      this.v1 = this.centerV + halfH * 0.75;
   }

   protected void init() {
      this.pressButton = -1;
      this.grip = 0;
      Arrays.fill(this.tooBig, false);

      for (int i = 0; i < this.detail.length; i++) {
         int lod = i;
         this.detail[i] = (Button)this.addRenderableWidget(
            Button.builder(Component.literal(DETAIL_LABELS[i]), b -> this.chosenLod = lod).bounds(6 + i * 25, 46, 24, 20).build()
         );
      }

      if (!this.drawings().isEmpty()) {
         this.addRenderableWidget(
            Checkbox.builder(Component.translatable("diorama.export.drawings"), this.font)
               .pos(6, 104)
               .selected(this.withDrawings)
               .onValueChange((box, value) -> this.withDrawings = value)
               .build()
         );
      }

      if (this.hasNight()) {
         int y = this.drawings().isEmpty() ? 104 : 128;
         this.addRenderableWidget(
            Checkbox.builder(Component.translatable("diorama.export.night"), this.font)
               .pos(6, y)
               .selected(this.atNight)
               .onValueChange((box, value) -> this.atNight = value)
               .build()
         );
      }

      this.save = (Button)this.addRenderableWidget(
         Button.builder(Component.translatable("diorama.export.save"), b -> this.save()).bounds(6, this.height - 28 + 4, 80, 20).build()
      );
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose()).bounds(90, this.height - 28 + 4, 60, 20).build());
   }

   private int previewWidth() {
      return this.width - 112;
   }

   private int previewHeight() {
      return this.height - 28;
   }

   private double @Nullable [] box() {
      return this.whole ? this.explored : new double[]{this.u0, this.v0, this.u1, this.v1};
   }

   private List<Integer> levels(double[] box) {
      return ImageExport.levels(box[0], box[1], box[2], box[3]);
   }

   private int lod(double[] box) {
      List<Integer> levels = this.levels(box);
      if (this.chosenLod >= 0 && levels.contains(this.chosenLod)) {
         return this.chosenLod;
      }

      if (this.chosenLod >= 0) {
         for (int lod : levels) {
            if (lod > this.chosenLod) {
               return lod;
            }
         }
      }

      return ImageExport.defaultLevel(box[0], box[1], box[2], box[3]);
   }

   private double screenX(double u) {
      return 112.0 + this.previewWidth() / 2.0 + (u - this.centerU) * this.zoom;
   }

   private double screenY(double v) {
      return this.previewHeight() / 2.0 + (v - this.centerV) * this.zoom;
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float a) {
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
      MapSession session = MapSession.get();
      if (session != null && ServerRules.minimapAllowed() && (this.caves() ? ServerRules.caveMapsAllowed() : session.map().savedMap() != null)) {
         if (this.whole && !this.caves() && session.map().savedMap() != this.boundsStore) {
            this.whole = false;
            this.showWhole(true);
         }

         if (this.whole && this.caves()) {
            double[] held = this.sliceBox(session);
            if (!Arrays.equals(held, this.explored)) {
               this.explored = held;
               if (held != null) {
                  this.fit(held);
               }
            }
         }

         if (this.whole && this.explored != null && this.finding != null) {
            this.finding = null;
            this.fit(this.explored);
         }

         double fullU = this.centerU - 56.0 / this.zoom;
         double fullV = this.centerV + 14.0 / this.zoom;
         MapViewport.View view = new MapViewport.View(0, 0, this.width, this.height, fullU, fullV, this.zoom, this.layer);
         this.viewport.night(this.atNight && this.hasNight() ? 1.0F : 0.0F);
         this.viewport.draw(graphics, session.map(), view);
         if (this.withDrawings) {
            this.drawingOverlay.draw(graphics, this.font, view, this.drawings(), null, null, true);
         }

         double[] box = this.box();
         int pw = this.previewWidth();
         int ph = this.previewHeight();
         if (box != null) {
            int x0 = (int)Math.round(Math.max(112.0, Math.min(this.width, this.screenX(box[0]))));
            int x1 = (int)Math.round(Math.max(112.0, Math.min(this.width, this.screenX(box[2]))));
            int y0 = (int)Math.round(Math.max(0.0, Math.min(ph, this.screenY(box[1]))));
            int y1 = (int)Math.round(Math.max(0.0, Math.min(ph, this.screenY(box[3]))));
            graphics.fill(112, 0, this.width, y0, -1879048192);
            graphics.fill(112, y1, this.width, ph, -1879048192);
            graphics.fill(112, y0, x0, y1, -1879048192);
            graphics.fill(x1, y0, this.width, y1, -1879048192);
            this.outline(graphics, box);
         } else if (this.whole) {
            graphics.fill(112, 0, this.width, ph, -1879048192);
            Component what = Component.translatable(this.finding != null ? "diorama.export.finding" : "diorama.export.nothing");
            graphics.drawCenteredString(this.font, what, 112 + pw / 2, ph / 2 - 4, -1);
         }

         graphics.fill(0, 0, 112, ph, -1073741824);
         graphics.fill(111, 0, 112, ph, -12961214);
         graphics.fill(0, ph, this.width, this.height, -1073741824);
         graphics.fill(0, ph, this.width, ph + 1, -12961214);
         this.drawTabs(graphics, mouseX, mouseY);
         graphics.drawString(this.font, Component.translatable("diorama.export.detail"), 6, 34, -4932408, false);
         int lod = box != null ? this.lod(box) : -1;
         List<Integer> levels = box != null ? this.levels(box) : List.of();

         for (int i = 0; i < this.detail.length; i++) {
            this.detail[i].active = box != null && levels.contains(i) && i != lod;
            boolean tooBig = box != null && !levels.contains(i);
            if (tooBig != this.tooBig[i]) {
               this.tooBig[i] = tooBig;
               this.detail[i].setTooltip(tooBig ? Tooltip.create(Component.translatable("diorama.export.too_big", new Object[]{number(16384L)})) : null);
            }
         }

         if (this.save != null) {
            this.save.active = box != null && !levels.isEmpty() && MapExport.running() == null;
         }

         if (box != null && !levels.isEmpty()) {
            ImageExport.Area area = ImageExport.Area.ofUnits(lod, box[0], box[1], box[2], box[3]);
            graphics.drawString(
               this.font, Component.translatable("diorama.export.pixels", new Object[]{number(area.width()), number(area.height())}), 6, 76, -1, false
            );
            graphics.drawString(this.font, size(ImageExport.estimateBytes(area)), 6, 88, -4932408, false);
            Component blocks = Component.translatable(
               "diorama.export.blocks", new Object[]{number(Math.round(box[2] - box[0])), number(Math.round(box[3] - box[1]))}
            );
            graphics.fill(116, 4, 122 + this.font.width(blocks), 16, -1879048192);
            graphics.drawString(this.font, blocks, 119, 6, -1, false);
         }

         if (!this.whole) {
            String help = this.font.plainSubstrByWidth(Component.translatable("diorama.export.help").getString(), Math.max(0, this.width - 164));
            graphics.drawString(this.font, help, 158, ph + 10, -4932408, false);
         }

         super.render(graphics, mouseX, mouseY, a);
         if (lod >= 0) {
            Button chosen = this.detail[lod];
            int x = chosen.getX();
            int y = chosen.getY();
            graphics.fill(x, y, x + chosen.getWidth(), y + chosen.getHeight(), -1);
            graphics.fill(x + 1, y + 1, x + chosen.getWidth() - 1, y + chosen.getHeight() - 1, -15724524);
            String label = DETAIL_LABELS[lod];
            int lx = x + (chosen.getWidth() - this.font.width(label)) / 2;
            graphics.drawString(this.font, label, lx, y + 6, -1, true);
            graphics.fill(lx - 1, y + 15, lx + this.font.width(label), y + 16, -1);
         }
      } else {
         this.onClose();
      }
   }

   private void outline(GuiGraphics graphics, double[] box) {
      int x0 = (int)Math.round(this.screenX(box[0]));
      int x1 = (int)Math.round(this.screenX(box[2]));
      int y0 = (int)Math.round(this.screenY(box[1]));
      int y1 = (int)Math.round(this.screenY(box[3]));
      graphics.enableScissor(112, 0, this.width, this.previewHeight());
      graphics.fill(x0 - 1, y0 - 1, x1 + 1, y0, -1);
      graphics.fill(x0 - 1, y1, x1 + 1, y1 + 1, -1);
      graphics.fill(x0 - 1, y0, x0, y1, -1);
      graphics.fill(x1, y0, x1 + 1, y1, -1);
      if (!this.whole) {
         int mx = (x0 + x1) / 2;
         int my = (y0 + y1) / 2;

         for (int[] p : new int[][]{{x0, y0}, {mx, y0}, {x1, y0}, {x0, my}, {x1, my}, {x0, y1}, {mx, y1}, {x1, y1}}) {
            graphics.fill(p[0] - 3, p[1] - 3, p[0] + 3, p[1] + 3, -16777216);
            graphics.fill(p[0] - 2, p[1] - 2, p[0] + 2, p[1] + 2, -1);
         }
      }

      graphics.disableScissor();
   }

   private void drawTabs(GuiGraphics graphics, int mouseX, int mouseY) {
      int x = 6;

      for (int i = 0; i < 2; i++) {
         boolean selected = i == 1 == this.whole;
         Component name = Component.translatable(i == 0 ? "diorama.export.area" : "diorama.export.whole");
         int nameWidth = this.font.width(name);
         int w = this.tabWidth(i);
         int h = selected ? 20 : 18;
         int y = 6;
         boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + 18;
         MapScreen.tab(graphics, selected, hovered, x, y, w, h);
         if (selected) {
            graphics.fill(x + 2, y + 2, x + w - 2, y + h, 1073741824);
            int underline = nameWidth - 1;
            graphics.fill(x + (w - underline) / 2, y + h - 3, x + (w - underline) / 2 + underline, y + h - 2, -1);
         }

         graphics.drawString(this.font, name, x + w / 2 - nameWidth / 2, y + (selected ? 5 : 6), selected ? -1 : -2039584, true);
         x += w;
      }
   }

   private int tabWidth(int i) {
      int whole = Math.max(this.font.width(Component.translatable("diorama.export.whole")) + 12, 50);
      return i == 1 ? Math.min(whole, 70) : 100 - Math.min(whole, 70);
   }

   private int tabAt(double x, double y) {
      if (y < 6.0 || y >= 24.0) {
         return -1;
      } else if (x >= 6.0 && x < 6 + this.tabWidth(0)) {
         return 0;
      } else {
         return x >= 6 + this.tabWidth(0) && x < 6 + this.tabWidth(0) + this.tabWidth(1) ? 1 : -1;
      }
   }

   private void showWhole(boolean whole) {
      if (whole != this.whole) {
         this.whole = whole;
         if (!whole) {
            this.fit(new double[]{this.u0, this.v0, this.u1, this.v1});
         } else {
            MapSession session = MapSession.get();
            this.explored = null;
            this.finding = null;
            if (session != null && this.caves()) {
               this.explored = this.sliceBox(session);
               if (this.explored != null) {
                  this.fit(this.explored);
               }
            } else {
               TileStore store = session == null ? null : session.map().savedMap();
               if (store != null) {
                  this.boundsStore = store;
                  CompletableFuture<double[]> found = CompletableFuture.supplyAsync(() -> {
                     try {
                        store.catchUp();
                     } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                     }

                     ImageExport.Area area = ImageExport.explored(store, 0);
                     if (area == null) {
                        return null;
                     }

                     double[] units = area.units();
                     double m = 32.0;
                     return new double[]{units[0] - m, units[1] - m, units[2] + m, units[3] + m};
                  });
                  this.finding = found;
                  found.whenCompleteAsync((units, failure) -> {
                     if (failure != null) {
                        Diorama.LOGGER.warn("Couldn't find what's explored for an export", failure);
                     }

                     if (this.finding == found) {
                        this.explored = failure != null ? null : units;
                        if (this.explored == null) {
                           this.finding = null;
                        }
                     }
                  }, this.minecraft);
               }
            }
         }
      }
   }

   private double fitZoom(double[] box) {
      double zoom = Math.min(this.previewWidth() / (box[2] - box[0]), this.previewHeight() / (box[3] - box[1])) * 0.9;
      return Math.max(0.04, Math.min(16.0, zoom));
   }

   private void fit(double[] box) {
      this.centerU = (box[0] + box[2]) / 2.0;
      this.centerV = (box[1] + box[3]) / 2.0;
      this.zoom = this.fitZoom(box);
   }

   private double @Nullable [] sliceBox(MapSession session) {
      double[] held = session.map().sliceBounds(this.layer);
      double m = 32.0;
      return held == null ? null : new double[]{held[0] - m, held[1] - m, held[2] + m, held[3] + m};
   }

   private boolean caves() {
      return this.layer != Integer.MIN_VALUE;
   }

   private boolean hasNight() {
      MapSession session = MapSession.get();
      return session != null
         && !this.caves()
         && !session.map().roofless()
         && this.minecraft.level != null
         && !this.minecraft.level.dimensionType().hasFixedTime();
   }

   private List<Drawing> drawings() {
      DrawingStore store = DrawingStore.get();
      return store != null && this.minecraft != null && this.minecraft.level != null
         ? store.in(this.minecraft.level.dimension().location().toString())
         : List.of();
   }

   private void save() {
      double[] box = this.box();
      MapSession session = MapSession.get();
      TileStore store = session == null ? null : session.map().savedMap();
      if (box != null && session != null && (this.caves() || store != null) && !this.levels(box).isEmpty()) {
         ImageExport.Area area = ImageExport.Area.ofUnits(this.lod(box), box[0], box[1], box[2], box[3]);
         List<Drawing> drawings = this.withDrawings ? this.drawings() : List.of();
         float night = this.atNight && this.hasNight() ? 1.0F : 0.0F;
         boolean started = this.caves()
            ? MapExport.startCave(session.map(), this.layer, area, drawings)
            : MapExport.start(store, area, session.map().roofless(), night, drawings);
         if (started) {
            this.onClose();
         }
      }
   }

   public boolean mouseClicked(double x, double y, int button) {
      return super.mouseClicked(x, y, button) || this.pressed(x, y, button);
   }

   public boolean mouseReleased(double x, double y, int button) {
      this.released(button);
      return super.mouseReleased(x, y, button);
   }

   public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
      return this.dragged(button, dx, dy) || super.mouseDragged(x, y, button, dx, dy);
   }

   private boolean pressed(double x, double y, int button) {
      if (button != 0) {
         return false;
      } else {
         int tab = this.tabAt(x, y);
         if (tab >= 0) {
            this.showWhole(tab == 1);
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
         } else if (!(x < 112.0) && !(y >= this.previewHeight())) {
            this.pressButton = button;
            this.grip = this.whole ? 32 : this.gripAt(x, y);
            return true;
         } else {
            return false;
         }
      }
   }

   private int gripAt(double x, double y) {
      double x0 = this.screenX(this.u0);
      double x1 = this.screenX(this.u1);
      double y0 = this.screenY(this.v0);
      double y1 = this.screenY(this.v1);
      boolean inX = x > x0 - 4.0 && x < x1 + 4.0;
      boolean inY = y > y0 - 4.0 && y < y1 + 4.0;
      int grip = 0;
      if (inY && Math.abs(x - x0) <= 4.0) {
         grip |= 1;
      } else if (inY && Math.abs(x - x1) <= 4.0) {
         grip |= 2;
      }

      if (inX && Math.abs(y - y0) <= 4.0) {
         grip |= 4;
      } else if (inX && Math.abs(y - y1) <= 4.0) {
         grip |= 8;
      }

      if (grip != 0) {
         return grip;
      } else {
         return x > x0 && x < x1 && y > y0 && y < y1 ? 16 : 32;
      }
   }

   private void released(int button) {
      if (button == this.pressButton) {
         this.pressButton = -1;
         this.grip = 0;
      }
   }

   private boolean dragged(int button, double dx, double dy) {
      if (button == this.pressButton && this.grip != 0) {
         double du = dx / this.zoom;
         double dv = dy / this.zoom;
         if (this.grip == 32) {
            this.centerU -= du;
            this.centerV -= dv;
         } else if (this.grip == 16) {
            this.u0 += du;
            this.u1 += du;
            this.v0 += dv;
            this.v1 += dv;
         } else {
            if ((this.grip & 1) != 0) {
               this.u0 = Math.min(this.u0 + du, this.u1 - 16.0);
            }

            if ((this.grip & 2) != 0) {
               this.u1 = Math.max(this.u1 + du, this.u0 + 16.0);
            }

            if ((this.grip & 4) != 0) {
               this.v0 = Math.min(this.v0 + dv, this.v1 - 16.0);
            }

            if ((this.grip & 8) != 0) {
               this.v1 = Math.max(this.v1 + dv, this.v0 + 16.0);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
      if (!(x < 112.0) && !(y >= this.previewHeight())) {
         double before = this.zoom;
         double after = Math.max(0.04, Math.min(16.0, before * Math.pow(1.25, scrollY)));
         double px = x - 112.0 - this.previewWidth() / 2.0;
         double py = y - this.previewHeight() / 2.0;
         double mapU = this.centerU + px / before;
         double mapV = this.centerV + py / before;
         this.centerU = mapU - px / after;
         this.centerV = mapV - py / after;
         this.zoom = after;
         return true;
      } else {
         return false;
      }
   }

   private static String number(long n) {
      return String.format(Locale.ROOT, "%,d", n);
   }

   private static Component size(long bytes) {
      return bytes < 1000000L
         ? Component.translatable("diorama.export.size_kb", new Object[]{number(Math.max(1L, Math.round(bytes / 1000.0)))})
         : Component.translatable("diorama.export.size_mb", new Object[]{number(Math.round(bytes / 1000000.0))});
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void removed() {
      this.viewport.close();
   }

   public boolean isPauseScreen() {
      return false;
   }

   public ImageExport.@Nullable Area plannedArea() {
      double[] box = this.box();
      return box != null && !this.levels(box).isEmpty() ? ImageExport.Area.ofUnits(this.lod(box), box[0], box[1], box[2], box[3]) : null;
   }

   public double @Nullable [] boxOnScreen() {
      double[] box = this.box();
      return box == null ? null : new double[]{this.screenX(box[0]), this.screenY(box[1]), this.screenX(box[2]), this.screenY(box[3])};
   }

   public int[] tabCenter(int tab) {
      int x = 6 + (tab == 1 ? this.tabWidth(0) : 0);
      return new int[]{x + this.tabWidth(tab) / 2, 14};
   }

   public Button detailButton(int lod) {
      return this.detail[lod];
   }

   public @Nullable Button saveButton() {
      return this.save;
   }
}
