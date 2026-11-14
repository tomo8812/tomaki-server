package dev.diorama.drawing;

import dev.diorama.render.MapViewport;
import dev.diorama.waypoint.Waypoint;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class DrawMode {
   private static final String[][] ICONS = new String[][]{
      {"..##.......", ".#..#......", "#....#....#", "......#..#.", ".......##.."},
      {"........#..", ".......###.", "......#.#..", ".....#.....", "....#......", "...#.......", "..#........", ".###.......", "..#........"},
      {".#######...", ".#.....#...", ".#......#..", ".#.......#.", ".#.......#.", ".#......#..", ".########.."},
      {"#########", "#...#...#", "....#....", "....#....", "....#....", "....#....", "...###..."},
      {"....###..", "...#..##.", "..#..###.", ".#..###..", "#..###...", ".####....", "..#......", "...######"},
      {"###########", "#.#.#.#.#.#", "#.#...#...#", "#.........#", "###########"}
   };
   private static final String[] UNDO_ICON = new String[]{
      "..#......", ".##......", "#######..", ".##....#.", "..#.....#", "........#", ".......#.", "...####.."
   };
   public static final String[] PENCIL_ICON = new String[]{
      "......##.", ".....#..#", "....#..#.", "...#..#..", "..#..#...", ".#..#....", "#..#.....", "###......"
   };
   private static final int BUTTON = 20;
   private static final int GAP = 2;
   private static final int LEFT = 4;
   private static final int SWATCH = 10;
   private static final double PEN_STEP = 1.5;
   private static final double PEN_TOLERANCE = 0.4;
   private static final long DOUBLE_CLICK_MS = 350L;
   private static final int NOTE_LENGTH = 60;
   private static DrawMode.Tool tool = DrawMode.Tool.PEN;
   private static int color = Waypoint.PALETTE[2];
   private boolean active;
   private final List<Button> toolButtons = new ArrayList<>();
   private @Nullable Button colourButton;
   private @Nullable Button undoButton;
   private @Nullable Button doneButton;
   private @Nullable EditBox noteBox;
   private boolean paletteOpen;
   private final DrawingOverlay overlay = new DrawingOverlay();
   private final Measure measure = new Measure();
   private Measure.Picker picker = (x, y) -> CompletableFuture.completedFuture(null);
   private GroundSnap ground = new GroundSnap((u, v) -> CompletableFuture.completedFuture(null));
   private Supplier<String> dimension = () -> "";
   private Consumer<AbstractWidget> focus = w -> {};
   private final List<double[]> points = new ArrayList<>();
   private boolean penDown;
   private boolean erasing;
   private double @Nullable [] followed;
   private boolean paletteRelease;
   private double @Nullable [] notePoint;
   private long lastClickTime;
   private double lastClickX;
   private double lastClickY;
   private final Deque<List<DrawMode.Change>> undo = new ArrayDeque<>();
   private @Nullable List<DrawMode.Change> erasedNow;

   public boolean active() {
      return this.active;
   }

   public void init(
      Font font, Consumer<AbstractWidget> add, Consumer<AbstractWidget> focus, Measure.Picker picker, GroundSnap.Picker mapPicker, Supplier<String> dimension
   ) {
      this.commitNote();
      this.endGesture();
      this.dimension = dimension;
      this.focus = focus;
      this.picker = picker;
      this.ground = new GroundSnap(mapPicker);
      this.toolButtons.clear();
      this.paletteOpen = false;
      this.paletteRelease = false;
      int y = 20;

      for (DrawMode.Tool t : DrawMode.Tool.values()) {
         String name = t.name().toLowerCase(Locale.ROOT);
         Button button = Button.builder(Component.empty(), b -> this.pick(t))
            .bounds(4, y, 20, 20)
            .tooltip(Tooltip.create(Component.translatable("diorama.draw." + name)))
            .createNarration(n -> Component.translatable("diorama.draw." + name))
            .build();
         this.toolButtons.add(button);
         add.accept(button);
         y += 22;
      }

      y += 4;
      this.colourButton = Button.builder(Component.empty(), b -> this.paletteOpen = !this.paletteOpen)
         .bounds(4, y, 20, 20)
         .tooltip(Tooltip.create(Component.translatable("diorama.draw.colour")))
         .createNarration(n -> Component.translatable("diorama.draw.colour"))
         .build();
      add.accept(this.colourButton);
      y += 24;
      this.undoButton = Button.builder(Component.empty(), b -> this.undo())
         .bounds(4, y, 20, 20)
         .tooltip(Tooltip.create(Component.translatable("diorama.draw.undo")))
         .createNarration(n -> Component.translatable("diorama.draw.undo"))
         .build();
      add.accept(this.undoButton);
      this.doneButton = Button.builder(Component.translatable("gui.done"), b -> this.stop()).bounds(26, y, 40, 20).build();
      add.accept(this.doneButton);
      this.noteBox = new EditBox(font, 0, 0, 120, 20, Component.translatable("diorama.draw.note"));
      this.noteBox.setMaxLength(60);
      add.accept(this.noteBox);
      this.showWidgets();
   }

   public void toggle() {
      if (this.active) {
         this.stop();
      } else {
         this.active = true;
         this.showWidgets();
      }
   }

   public void stop() {
      this.commitNote();
      this.cancel();
      this.active = false;
      this.paletteOpen = false;
      this.showWidgets();
   }

   public void close() {
      this.commitNote();
      this.undo.clear();
   }

   private void showWidgets() {
      for (Button button : this.toolButtons) {
         button.visible = this.active;
      }

      for (AbstractWidget widget : new AbstractWidget[]{this.colourButton, this.undoButton, this.doneButton}) {
         if (widget != null) {
            widget.visible = this.active;
         }
      }

      if (this.undoButton != null) {
         this.undoButton.active = !this.undo.isEmpty();
      }

      if (this.noteBox != null) {
         this.noteBox.visible = this.active && this.notePoint != null;
      }
   }

   private void pick(DrawMode.Tool picked) {
      this.commitNote();
      this.cancel();
      tool = picked;
      this.paletteOpen = false;
   }

   public void leaveDimension() {
      this.commitNote();
      this.endGesture();
      this.cancel();
      this.undo.clear();
   }

   private void endGesture() {
      this.measure.end();
      if (this.penDown) {
         this.penDown = false;
         if (this.points.size() >= 2) {
            this.finish(new Drawing(Drawing.Kind.STROKE, this.dimension.get(), color, flatten(this.points), ""));
         }

         this.points.clear();
      }

      if (this.erasing) {
         this.erasing = false;
         if (this.erasedNow != null && !this.erasedNow.isEmpty()) {
            this.undo.push(this.erasedNow);
         }

         this.erasedNow = null;
      }
   }

   public void anyPress(double x, double y) {
      if (this.notePoint != null && this.noteBox != null && !on(this.noteBox, x, y)) {
         this.commitNote();
      }
   }

   private void cancel() {
      this.points.clear();
      this.measure.clear();
      this.penDown = false;
      this.paletteRelease = false;
      this.erasing = false;
      if (this.erasedNow != null && !this.erasedNow.isEmpty()) {
         this.undo.push(this.erasedNow);
      }

      this.erasedNow = null;
      if (this.noteBox != null) {
         this.noteBox.setValue("");
         this.noteBox.visible = false;
         if (this.notePoint != null) {
            this.focus.accept(null);
         }
      }

      this.notePoint = null;
   }

   public boolean typing() {
      return this.active && this.notePoint != null;
   }

   public boolean onControls(double x, double y) {
      if (!this.active) {
         return false;
      }

      if (this.paletteOpen && this.onPalette(x, y)) {
         return true;
      }

      for (Button button : this.toolButtons) {
         if (on(button, x, y)) {
            return true;
         }
      }

      for (AbstractWidget widget : new AbstractWidget[]{this.colourButton, this.undoButton, this.doneButton, this.noteBox}) {
         if (widget != null && widget.visible && on(widget, x, y)) {
            return true;
         }
      }

      return false;
   }

   private static boolean on(AbstractWidget widget, double x, double y) {
      return x >= widget.getX() && x < widget.getX() + widget.getWidth() && y >= widget.getY() && y < widget.getY() + widget.getHeight();
   }

   private int paletteX() {
      return 28;
   }

   private int paletteY() {
      return this.colourButton == null ? 0 : this.colourButton.getY() + 20 - 40;
   }

   private boolean onPalette(double x, double y) {
      return x >= this.paletteX() - 2 && x < this.paletteX() + 40 + 2 && y >= this.paletteY() - 2 && y < this.paletteY() + 40 + 2;
   }

   public boolean pans(int button) {
      return !this.active || button == 1 || button == 2 || tool == DrawMode.Tool.LINE || tool == DrawMode.Tool.AREA || tool == DrawMode.Tool.NOTE;
   }

   public boolean press(MapViewport.View view, Font font, String dimension, double x, double y, int button) {
      if (this.paletteOpen) {
         if (this.onPalette(x, y)) {
            int i = (int)((x - this.paletteX()) / 10.0);
            int j = (int)((y - this.paletteY()) / 10.0);
            if (i >= 0 && i < 4 && j >= 0 && j < 4) {
               color = Waypoint.PALETTE[j * 4 + i];
            }
         }

         this.paletteOpen = false;
         this.paletteRelease = true;
         return true;
      } else if (button != 0) {
         return false;
      } else {
         this.followed = null;
         if (tool == DrawMode.Tool.PEN) {
            this.points.clear();
            this.points.add(mapPoint(view, x, y));
            this.penDown = true;
            return true;
         } else if (tool == DrawMode.Tool.ERASE) {
            this.erasing = true;
            this.erasedNow = new ArrayList<>();
            this.eraseAt(view, font, dimension, x, y);
            return true;
         } else if (tool == DrawMode.Tool.MEASURE) {
            this.measure.start(mapPoint(view, x, y), this.picker.at(x, y));
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean holding() {
      return this.penDown || this.erasing || this.measure.dragging();
   }

   public void follow(MapViewport.View view, Font font, String dimension, double x, double y) {
      double[] at = mapPoint(view, x, y);
      if (!Arrays.equals(at, this.followed)) {
         this.followed = at;
         this.drag(view, font, dimension, x, y);
      }
   }

   public boolean drag(MapViewport.View view, Font font, String dimension, double x, double y) {
      if (this.penDown) {
         double[] last = this.points.get(this.points.size() - 1);
         if (Math.hypot(view.screenX(last[0]) - x, view.screenY(last[1]) - y) >= 1.5) {
            this.points.add(mapPoint(view, x, y));
         }

         return true;
      } else if (this.erasing) {
         this.eraseAt(view, font, dimension, x, y);
         return true;
      } else if (this.measure.dragging()) {
         this.measure.move(mapPoint(view, x, y), this.picker.at(x, y));
         return true;
      } else {
         return false;
      }
   }

   public boolean release(MapViewport.View view, Font font, String dimension, double x, double y, int button, boolean moved) {
      if (this.paletteRelease) {
         this.paletteRelease = false;
         return true;
      }

      if (this.penDown && button == 0) {
         this.penDown = false;
         this.points.add(mapPoint(view, x, y));
         double tolerance = 0.4 / view.zoom();
         this.finish(new Drawing(Drawing.Kind.STROKE, dimension, color, thin(this.points, tolerance), ""));
         this.points.clear();
         return true;
      }

      if (this.measure.dragging() && button == 0) {
         this.measure.move(mapPoint(view, x, y), this.picker.at(x, y));
         this.measure.end();
         return true;
      }

      if (this.erasing && button == 0) {
         this.erasing = false;
         if (this.erasedNow != null && !this.erasedNow.isEmpty()) {
            this.undo.push(this.erasedNow);
         }

         this.erasedNow = null;
         this.showWidgets();
         return true;
      } else {
         if (moved) {
            return false;
         }

         boolean clicking = tool == DrawMode.Tool.LINE || tool == DrawMode.Tool.AREA;
         if (clicking && button == 1) {
            this.finishShape(dimension);
            return true;
         }

         if (button != 0) {
            return true;
         }

         long now = System.currentTimeMillis();
         boolean doubleClick = now - this.lastClickTime < 350L && Math.hypot(x - this.lastClickX, y - this.lastClickY) < 4.0;
         this.lastClickTime = now;
         this.lastClickX = x;
         this.lastClickY = y;
         if (!clicking) {
            if (tool == DrawMode.Tool.NOTE && this.noteBox != null) {
               this.notePoint = mapPoint(view, x, y);
               this.noteBox.setValue("");
               int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
               this.noteBox.setX((int)Math.max(2L, Math.min(screenWidth - this.noteBox.getWidth() - 2, Math.round(x - this.noteBox.getWidth() / 2.0))));
               this.noteBox.setY((int)Math.round(y - 10.0));
               this.noteBox.visible = true;
               this.focus.accept(this.noteBox);
               return true;
            } else {
               return true;
            }
         } else {
            if (doubleClick && !this.points.isEmpty()) {
               this.finishShape(dimension);
               this.lastClickTime = 0L;
            } else {
               this.points.add(this.snapped(view, x, y, true));
            }

            return true;
         }
      }
   }

   public boolean key(int key, String dimension) {
      if (!this.active) {
         return false;
      }

      if (this.notePoint != null) {
         if (key == 257 || key == 335) {
            this.commitNote();
            return true;
         } else if (key == 256) {
            this.cancel();
            return true;
         } else {
            return false;
         }
      } else if (key != 256) {
         if (key != 257 && key != 335) {
            if (key == 90 && controlDown()) {
               this.undo();
               return true;
            } else {
               return false;
            }
         } else {
            if (!this.penDown && !this.points.isEmpty()) {
               this.finishShape(dimension);
            }

            return true;
         }
      } else {
         if (this.paletteOpen) {
            this.paletteOpen = false;
         } else if (this.points.isEmpty() && !this.penDown && !this.measure.shown()) {
            this.stop();
         } else {
            this.cancel();
         }

         return true;
      }
   }

   private static boolean controlDown() {
      return Screen.hasControlDown();
   }

   public static boolean shiftDown() {
      return Screen.hasShiftDown();
   }

   private void finishShape(String dimension) {
      Drawing.Kind kind = tool == DrawMode.Tool.AREA ? Drawing.Kind.AREA : Drawing.Kind.STROKE;
      double[] flat = flatten(this.points);
      this.points.clear();
      this.finish(new Drawing(kind, dimension, color, flat, ""));
   }

   private void commitNote() {
      if (this.notePoint != null && this.noteBox != null) {
         String text = this.noteBox.getValue().strip();
         String dimension = this.dimension.get();
         double[] at = this.notePoint;
         this.cancel();
         this.finish(new Drawing(Drawing.Kind.NOTE, dimension, color, at, text));
      }
   }

   private void finish(Drawing drawing) {
      DrawingStore store = DrawingStore.get();
      if (store != null && drawing.complete()) {
         store.add(drawing);
         this.undo.push(List.of(new DrawMode.Change(drawing, -1)));
         this.showWidgets();
      }
   }

   private void eraseAt(MapViewport.View view, Font font, String dimension, double x, double y) {
      DrawingStore store = DrawingStore.get();
      if (store != null) {
         Drawing hit = DrawingOverlay.under(font, view, store.in(dimension), x, y, true);
         if (hit != null) {
            int index = store.remove(hit);
            if (this.erasedNow != null) {
               this.erasedNow.add(new DrawMode.Change(hit, index));
            }
         }
      }
   }

   private void undo() {
      DrawingStore store = DrawingStore.get();
      if (store != null && !this.undo.isEmpty()) {
         List<DrawMode.Change> changes = this.undo.pop();
         store.batch(() -> {
            for (int i = changes.size() - 1; i >= 0; i--) {
               DrawMode.Change change = changes.get(i);
               if (change.removedFrom < 0) {
                  store.remove(change.drawing);
               } else {
                  store.restore(change.drawing, change.removedFrom);
               }
            }
         });
         this.showWidgets();
      }
   }

   public void drawMap(GuiGraphics graphics, Font font, MapViewport.View view, List<Drawing> drawings, double mouseX, double mouseY, boolean overMap) {
      Drawing preview = null;
      Drawing target = null;
      if (this.active && !this.points.isEmpty()) {
         List<double[]> shown = new ArrayList<>(this.points);
         if (!this.penDown && overMap) {
            shown.add(this.snapped(view, mouseX, mouseY, false));
         }

         preview = new Drawing(tool == DrawMode.Tool.AREA ? Drawing.Kind.AREA : Drawing.Kind.STROKE, "", color, flatten(shown), "");
      } else if (this.active && tool == DrawMode.Tool.ERASE && overMap) {
         target = DrawingOverlay.under(font, view, drawings, mouseX, mouseY, true);
      } else if (this.active && this.measure.shown()) {
         preview = this.measure.line();
      }

      this.overlay.draw(graphics, font, view, drawings, preview, target, true);
      if (this.active && this.measure.shown()) {
         this.measure.drawReading(graphics, font, view);
      }

      if (this.active && this.notePoint != null) {
         float sx = view.screenX(this.notePoint[0]);
         float sy = view.screenY(this.notePoint[1]);
         graphics.fill(Math.round(sx) - 1, Math.round(sy) - 1, Math.round(sx) + 1, Math.round(sy) + 1, 0xFF000000 | color);
      }
   }

   public void drawControls(GuiGraphics graphics) {
      if (this.active) {
         DrawMode.Tool[] tools = DrawMode.Tool.values();

         for (int i = 0; i < this.toolButtons.size(); i++) {
            Button button = this.toolButtons.get(i);
            icon(graphics, ICONS[i], button.getX(), button.getY(), -1);
            if (tools[i] == tool) {
               outline(graphics, button.getX(), button.getY(), 20, 20, -1);
            }
         }

         if (this.colourButton != null) {
            int x = this.colourButton.getX() + 4;
            int y = this.colourButton.getY() + 4;
            graphics.fill(x, y, x + 12, y + 12, -16777216);
            graphics.fill(x + 1, y + 1, x + 11, y + 11, 0xFF000000 | color);
         }

         if (this.undoButton != null) {
            icon(graphics, UNDO_ICON, this.undoButton.getX(), this.undoButton.getY(), this.undoButton.active ? -1 : -6250336);
         }

         if (this.paletteOpen) {
            int x0 = this.paletteX();
            int y0 = this.paletteY();
            graphics.fill(x0 - 2, y0 - 2, x0 + 40 + 2, y0 + 40 + 2, -536870912);

            for (int k = 0; k < 16; k++) {
               int x = x0 + k % 4 * 10;
               int y = y0 + k / 4 * 10;
               int c = Waypoint.PALETTE[k];
               if (c == color) {
                  graphics.fill(x - 1, y - 1, x + 10, y + 10, -1);
               }

               graphics.fill(x, y, x + 10 - 1, y + 10 - 1, -16777216);
               graphics.fill(x + 1, y + 1, x + 10 - 2, y + 10 - 2, c);
            }
         }
      }
   }

   public int measured() {
      return this.measure.shown() ? this.measure.blocks() : -1;
   }

   public @Nullable String measuredText() {
      return this.measure.shown() ? this.measure.reading().getString() : null;
   }

   public List<Button> toolButtons() {
      return this.toolButtons;
   }

   public List<Button> otherButtons() {
      return List.of(this.colourButton, this.undoButton);
   }

   public float[] swatch(int k) {
      return new float[]{this.paletteX() + k % 4 * 10 + 5.0F, this.paletteY() + k / 4 * 10 + 5.0F};
   }

   public Component help() {
      return Component.translatable("diorama.draw.help." + tool.name().toLowerCase(Locale.ROOT));
   }

   public static void icon(GuiGraphics graphics, String[] rows, int x, int y, int argb) {
      int h = rows.length;
      int w = rows[0].length();
      int x0 = x + (20 - w) / 2;
      int y0 = y + (20 - h) / 2;
      int runs = 0;

      for (String row : rows) {
         for (int c = 0; c < w; c++) {
            if (row.charAt(c) == '#' && (c == 0 || row.charAt(c - 1) != '#')) {
               runs++;
            }
         }
      }

      if (runs != 0) {
         float[] corners = new float[runs * 8];
         int k = 0;

         for (int r = 0; r < h; r++) {
            String row = rows[r];
            int c = 0;

            while (c < w) {
               if (row.charAt(c) != '#') {
                  c++;
               } else {
                  int start = c;

                  while (c < w && row.charAt(c) == '#') {
                     c++;
                  }

                  float left = x0 + start;
                  float right = x0 + c;
                  float top = y0 + r;
                  float bottom = y0 + r + 1;
                  corners[k++] = left;
                  corners[k++] = top;
                  corners[k++] = left;
                  corners[k++] = bottom;
                  corners[k++] = right;
                  corners[k++] = bottom;
                  corners[k++] = right;
                  corners[k++] = top;
               }
            }
         }

         for (int i = 0; i < corners.length; i += 8) {
            graphics.fill((int)corners[i], (int)corners[i + 1], (int)corners[i + 4], (int)corners[i + 5], argb);
         }
      }
   }

   public static void outline(GuiGraphics graphics, int x, int y, int w, int h, int argb) {
      graphics.fill(x, y, x + w, y + 1, argb);
      graphics.fill(x, y + h - 1, x + w, y + h, argb);
      graphics.fill(x, y + 1, x + 1, y + h - 1, argb);
      graphics.fill(x + w - 1, y + 1, x + w, y + h - 1, argb);
   }

   private static double[] mapPoint(MapViewport.View view, double x, double y) {
      return new double[]{
         view.centerU() + (x - view.x() - view.width() / 2.0) / view.zoom(), view.centerV() + (y - view.y() - view.height() / 2.0) / view.zoom()
      };
   }

   private double[] snapped(MapViewport.View view, double x, double y, boolean click) {
      double[] point = mapPoint(view, x, y);
      if (!this.points.isEmpty() && shiftDown()) {
         double[] last = this.points.get(this.points.size() - 1);
         double[] onGround = this.ground.snap(view, last, point, click);
         if (onGround != null) {
            return onGround;
         }

         double du = point[0] - last[0];
         double dv = point[1] - last[1];
         double length = Math.hypot(du, dv);
         double angle = Math.round(Math.atan2(dv, du) / (Math.PI / 4)) * (Math.PI / 4);
         double[] level = new double[]{last[0] + Math.cos(angle) * length, last[1] + Math.sin(angle) * length};
         if (click) {
            this.ground.clicked(view, level);
         }

         return level;
      } else {
         if (click) {
            this.ground.clicked(view, point);
         }

         return point;
      }
   }

   private static double[] flatten(List<double[]> points) {
      double[] flat = new double[points.size() * 2];

      for (int i = 0; i < points.size(); i++) {
         flat[i * 2] = points.get(i)[0];
         flat[i * 2 + 1] = points.get(i)[1];
      }

      return flat;
   }

   static double[] thin(List<double[]> points, double tolerance) {
      int n = points.size();
      if (n < 3) {
         return flatten(n == 1 ? List.of(points.get(0), points.get(0)) : points);
      }

      boolean[] keep = new boolean[n];
      keep[0] = true;
      keep[n - 1] = true;
      Deque<int[]> spans = new ArrayDeque<>();
      spans.push(new int[]{0, n - 1});

      while (!spans.isEmpty()) {
         int[] span = spans.pop();
         double[] a = points.get(span[0]);
         double[] b = points.get(span[1]);
         double far = -1.0;
         int at = -1;

         for (int i = span[0] + 1; i < span[1]; i++) {
            double[] p = points.get(i);
            double dx = b[0] - a[0];
            double dy = b[1] - a[1];
            double length = dx * dx + dy * dy;
            double t = length == 0.0 ? 0.0 : Math.max(0.0, Math.min(1.0, ((p[0] - a[0]) * dx + (p[1] - a[1]) * dy) / length));
            double d = Math.hypot(p[0] - (a[0] + t * dx), p[1] - (a[1] + t * dy));
            if (d > far) {
               far = d;
               at = i;
            }
         }

         if (at >= 0 && far > tolerance) {
            keep[at] = true;
            spans.push(new int[]{span[0], at});
            spans.push(new int[]{at, span[1]});
         }
      }

      List<double[]> kept = new ArrayList<>();

      for (int i = 0; i < n; i++) {
         if (keep[i]) {
            kept.add(points.get(i));
         }
      }

      return flatten(kept);
   }

   private record Change(Drawing drawing, int removedFrom) {
   }

   public enum Tool {
      PEN,
      LINE,
      AREA,
      NOTE,
      ERASE,
      MEASURE;

      // $VF: synthetic method
      private static DrawMode.Tool[] $values() {
         return new DrawMode.Tool[]{PEN, LINE, AREA, NOTE, ERASE, MEASURE};
      }
   }
}
