package dev.diorama.drawing;

import dev.diorama.render.Circle;
import dev.diorama.render.MapViewport;
import dev.diorama.render.gpu.MapCanvas;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.jspecify.annotations.Nullable;

public final class DrawingOverlay {
   public static final float CORE = 2.0F;
   public static final float RIM = 1.0F;
   static final float EDGE = 1.0F;
   static final int FILL_ALPHA = 64;
   static final int PLATE = -1879048192;
   private static final float PICK_REACH = 3.0F;
   private static final float[] OCTAGON = new float[16];
   private final float[] polygon = new float[16];
   private float[] xyz = new float[3072];
   private int[] colors = new int[256];
   private int quads;
   private @Nullable Circle circle;

   public void draw(
      GuiGraphics graphics, Font font, MapViewport.View view, List<Drawing> drawings, @Nullable Drawing preview, @Nullable Drawing highlighted, boolean notes
   ) {
      this.circle = view.round() ? Circle.in(view) : null;
      this.quads = 0;
      graphics.enableScissor(view.x(), view.y(), view.x() + view.width(), view.y() + view.height());

      for (Drawing drawing : drawings) {
         if (drawing.kind != Drawing.Kind.NOTE && this.onScreen(view, drawing)) {
            this.shape(view, drawing, drawing == highlighted);
         }
      }

      if (preview != null && preview.kind != Drawing.Kind.NOTE) {
         this.shape(view, preview, false);
      }

      if (this.quads > 0) {
         MapCanvas.of(graphics).solids(this.xyz, this.colors, this.quads);
      }

      if (notes) {
         for (Drawing drawing : drawings) {
            if (drawing.kind == Drawing.Kind.NOTE) {
               this.note(graphics, font, view, drawing, drawing == highlighted);
            }
         }
      }

      graphics.disableScissor();
   }

   public static @Nullable Drawing under(Font font, MapViewport.View view, List<Drawing> drawings, double x, double y, boolean notes) {
      for (boolean noteRound : new boolean[]{true, false}) {
         for (int i = drawings.size() - 1; i >= 0; i--) {
            Drawing drawing = drawings.get(i);
            if (drawing.kind == Drawing.Kind.NOTE == noteRound && hits(font, view, drawing, x, y, notes)) {
               return drawing;
            }
         }
      }

      return null;
   }

   private static boolean hits(Font font, MapViewport.View view, Drawing drawing, double x, double y, boolean notes) {
      double[] p = drawing.shown();
      int n = drawing.pointCount();
      switch (drawing.kind) {
         case NOTE:
            if (!notes) {
               return false;
            }

            float[] plate = plate(font, view, drawing);
            return x >= plate[0] && x < plate[2] && y >= plate[1] && y < plate[3];
         case STROKE:
            float reach = 5.0F;

            for (int k = 0; k + 1 < n; k++) {
               if (segmentDistance(x, y, view.screenX(p[k * 2]), view.screenY(p[k * 2 + 1]), view.screenX(p[k * 2 + 2]), view.screenY(p[k * 2 + 3])) <= reach) {
                  return true;
               }
            }

            return n == 1 && Math.hypot(x - view.screenX(p[0]), y - view.screenY(p[1])) <= reach;
         case AREA:
            boolean inside = false;
            int k = 0;

            for (int j = n - 1; k < n; j = k++) {
               double ax = view.screenX(p[k * 2]);
               double ay = view.screenY(p[k * 2 + 1]);
               double bx = view.screenX(p[j * 2]);
               double by = view.screenY(p[j * 2 + 1]);
               if (segmentDistance(x, y, ax, ay, bx, by) <= 3.0) {
                  return true;
               }

               if (ay > y != by > y && x < ax + (y - ay) * (bx - ax) / (by - ay)) {
                  inside = !inside;
               }
            }

            return inside;
         default:
            return false;
      }
   }

   private static double segmentDistance(double x, double y, double ax, double ay, double bx, double by) {
      double dx = bx - ax;
      double dy = by - ay;
      double length = dx * dx + dy * dy;
      double t = length == 0.0 ? 0.0 : Math.max(0.0, Math.min(1.0, ((x - ax) * dx + (y - ay) * dy) / length));
      return Math.hypot(x - (ax + t * dx), y - (ay + t * dy));
   }

   private boolean onScreen(MapViewport.View view, Drawing drawing) {
      double[] p = drawing.shown();
      double minU = Double.MAX_VALUE;
      double minV = Double.MAX_VALUE;
      double maxU = -Double.MAX_VALUE;
      double maxV = -Double.MAX_VALUE;

      for (int i = 0; i < p.length; i += 2) {
         minU = Math.min(minU, p[i]);
         maxU = Math.max(maxU, p[i]);
         minV = Math.min(minV, p[i + 1]);
         maxV = Math.max(maxV, p[i + 1]);
      }

      float grow = 4.0F;
      return view.screenX(maxU) + grow >= view.x()
         && view.screenX(minU) - grow <= view.x() + view.width()
         && view.screenY(maxV) + grow >= view.y()
         && view.screenY(minV) - grow <= view.y() + view.height();
   }

   private void shape(MapViewport.View view, Drawing drawing, boolean lit) {
      int n = drawing.pointCount();
      float[] sx = new float[n];
      float[] sy = new float[n];

      for (int k = 0; k < n; k++) {
         sx[k] = view.screenX(drawing.shown()[k * 2]);
         sy[k] = view.screenY(drawing.shown()[k * 2 + 1]);
      }

      if (drawing.kind == Drawing.Kind.AREA) {
         if (n >= 3) {
            this.fill(sx, sy, n, 1073741824 | drawing.color & 16777215);
         }

         if (lit) {
            this.line(sx, sy, n, true, 3.0F, -1);
         }

         this.line(sx, sy, n, true, 1.0F, 0xFF000000 | drawing.color);
      } else {
         this.line(sx, sy, n, false, 4.0F, lit ? -1 : rim(drawing.color));
         this.line(sx, sy, n, false, 2.0F, 0xFF000000 | drawing.color);
      }
   }

   static int rim(int color) {
      int r = (color >> 16 & 0xFF) * 3 / 10;
      int g = (color >> 8 & 0xFF) * 3 / 10;
      int b = (color & 0xFF) * 3 / 10;
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   private void line(float[] sx, float[] sy, int n, boolean closed, float width, int argb) {
      float half = width / 2.0F;
      int segments = closed ? n : n - 1;

      for (int k = 0; k < segments; k++) {
         int j = (k + 1) % n;
         float dx = sx[j] - sx[k];
         float dy = sy[j] - sy[k];
         float length = (float)Math.sqrt(dx * dx + dy * dy);
         if (!(length < 1.0E-4F)) {
            float nx = -dy / length * half;
            float ny = dx / length * half;
            this.polygon[0] = sx[k] + nx;
            this.polygon[1] = sy[k] + ny;
            this.polygon[2] = sx[k] - nx;
            this.polygon[3] = sy[k] - ny;
            this.polygon[4] = sx[j] - nx;
            this.polygon[5] = sy[j] - ny;
            this.polygon[6] = sx[j] + nx;
            this.polygon[7] = sy[j] + ny;
            this.convex(4, argb);
         }
      }

      for (int k = 0; k < n; k++) {
         for (int c = 0; c < 8; c++) {
            this.polygon[c * 2] = sx[k] + OCTAGON[c * 2] * half;
            this.polygon[c * 2 + 1] = sy[k] + OCTAGON[c * 2 + 1] * half;
         }

         this.convex(8, argb);
      }
   }

   private void fill(float[] sx, float[] sy, int n, int argb) {
      int[] left = new int[n];
      int k = 0;

      while (k < n) {
         left[k] = k++;
      }

      float area = 0.0F;
      int kx = 0;

      for (int j = n - 1; kx < n; j = kx++) {
         area += sx[j] * sy[kx] - sx[kx] * sy[j];
      }

      float turn = Math.signum(area);
      int count = n;
      int guard = 0;

      while (count > 3 && guard++ < n * n) {
         boolean clipped = false;

         for (int i = 0; i < count && !clipped; i++) {
            int a = left[(i + count - 1) % count];
            int b = left[i];
            int c = left[(i + 1) % count];
            if (!(turn * cross(sx, sy, a, b, c) <= 0.0F) && !anyInside(sx, sy, left, count, a, b, c)) {
               this.triangle(sx[a], sy[a], sx[b], sy[b], sx[c], sy[c], argb);
               System.arraycopy(left, i + 1, left, i, count - i - 1);
               count--;
               clipped = true;
            }
         }

         if (!clipped) {
            break;
         }
      }

      for (int kxx = 1; kxx + 1 < count; kxx++) {
         this.triangle(sx[left[0]], sy[left[0]], sx[left[kxx]], sy[left[kxx]], sx[left[kxx + 1]], sy[left[kxx + 1]], argb);
      }
   }

   private static float cross(float[] sx, float[] sy, int a, int b, int c) {
      return (sx[b] - sx[a]) * (sy[c] - sy[b]) - (sy[b] - sy[a]) * (sx[c] - sx[b]);
   }

   private static boolean anyInside(float[] sx, float[] sy, int[] left, int count, int a, int b, int c) {
      for (int k = 0; k < count; k++) {
         int p = left[k];
         if (p != a && p != b && p != c) {
            float d1 = (sx[b] - sx[a]) * (sy[p] - sy[a]) - (sy[b] - sy[a]) * (sx[p] - sx[a]);
            float d2 = (sx[c] - sx[b]) * (sy[p] - sy[b]) - (sy[c] - sy[b]) * (sx[p] - sx[b]);
            float d3 = (sx[a] - sx[c]) * (sy[p] - sy[c]) - (sy[a] - sy[c]) * (sx[p] - sx[c]);
            boolean negative = d1 < 0.0F || d2 < 0.0F || d3 < 0.0F;
            boolean positive = d1 > 0.0F || d2 > 0.0F || d3 > 0.0F;
            if (!negative || !positive) {
               return true;
            }
         }
      }

      return false;
   }

   private void triangle(float ax, float ay, float bx, float by, float cx, float cy, int argb) {
      this.polygon[0] = ax;
      this.polygon[1] = ay;
      this.polygon[2] = bx;
      this.polygon[3] = by;
      this.polygon[4] = cx;
      this.polygon[5] = cy;
      this.convex(3, argb);
   }

   private void convex(int n, int argb) {
      float area = 0.0F;
      int k = 0;

      for (int j = n - 1; k < n; j = k++) {
         area += this.polygon[j * 2] * this.polygon[k * 2 + 1] - this.polygon[k * 2] * this.polygon[j * 2 + 1];
      }

      if (area != 0.0F) {
         if (area > 0.0F) {
            for (int kx = 0; kx < n / 2; kx++) {
               int j = n - 1 - kx;
               float x = this.polygon[kx * 2];
               float y = this.polygon[kx * 2 + 1];
               this.polygon[kx * 2] = this.polygon[j * 2];
               this.polygon[kx * 2 + 1] = this.polygon[j * 2 + 1];
               this.polygon[j * 2] = x;
               this.polygon[j * 2 + 1] = y;
            }
         }

         float[] corners;
         if (this.circle != null) {
            corners = this.circle.clip(this.polygon, n);
            if (corners == null) {
               return;
            }
         } else {
            corners = fan(this.polygon, n);
         }

         for (int i = 0; i + 8 <= corners.length; i += 8) {
            this.add(corners, i, argb);
         }
      }
   }

   private static float[] fan(float[] polygon, int count) {
      float[] corners = new float[(count - 1) / 2 * 8];
      int out = 0;

      for (int k = 1; k + 1 < count; k += 2) {
         for (int at : new int[]{0, k, k + 1, Math.min(k + 2, count - 1)}) {
            corners[out++] = polygon[at * 2];
            corners[out++] = polygon[at * 2 + 1];
         }
      }

      return corners;
   }

   private void add(float[] corners, int from, int argb) {
      if (this.quads == this.colors.length) {
         this.colors = Arrays.copyOf(this.colors, this.quads * 2);
         this.xyz = Arrays.copyOf(this.xyz, this.quads * 24);
      }

      int base = this.quads * 12;

      for (int c = 0; c < 4; c++) {
         this.xyz[base + c * 3] = corners[from + c * 2];
         this.xyz[base + c * 3 + 1] = corners[from + c * 2 + 1];
         this.xyz[base + c * 3 + 2] = 0.0F;
      }

      this.colors[this.quads++] = argb;
   }

   private static float[] plate(Font font, MapViewport.View view, Drawing note) {
      int width = font.width(note.text);
      float x = Math.round(view.screenX(note.shown()[0]) - width / 2.0F);
      float y = Math.round(view.screenY(note.shown()[1]) - 4.0F);
      return new float[]{x - 2.0F, y - 2.0F, x + width + 1.0F, y + 9.0F, x, y};
   }

   private void note(GuiGraphics graphics, Font font, MapViewport.View view, Drawing note, boolean lit) {
      float[] plate = plate(font, view, note);
      if (!(plate[2] < view.x()) && !(plate[0] > view.x() + view.width()) && !(plate[3] < view.y()) && !(plate[1] > view.y() + view.height())) {
         int x0 = (int)plate[0];
         int y0 = (int)plate[1];
         int x1 = (int)plate[2];
         int y1 = (int)plate[3];
         if (lit) {
            graphics.fill(x0 - 1, y0 - 1, x1 + 1, y1 + 1, -1);
         }

         graphics.fill(x0, y0, x1, y1, -1879048192);
         graphics.drawString(font, note.text, (int)plate[4], (int)plate[5], 0xFF000000 | note.color, true);
      }
   }

   static {
      for (int i = 0; i < 8; i++) {
         double a = (Math.PI * 2) * (i + 0.5) / 8.0;
         OCTAGON[i * 2] = (float)Math.cos(a);
         OCTAGON[i * 2 + 1] = (float)(-Math.sin(a));
      }
   }
}
