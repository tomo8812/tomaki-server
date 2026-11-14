package dev.diorama.drawing;

import dev.diorama.data.ImageExport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.server.packs.resources.ResourceManager;

public final class ExportDrawings implements ImageExport.Finish {
   private static final int SCALE = 2;
   private final List<ExportDrawings.Shape> shapes = new ArrayList<>();
   private final List<ExportDrawings.Plate> plates = new ArrayList<>();
   private final int width;
   private final int height;
   private final long top;
   private final ThreadLocal<float[]> coverage;

   private ExportDrawings(ImageExport.Area area) {
      this.width = area.width();
      this.height = area.height();
      this.top = area.y();
      this.coverage = ThreadLocal.withInitial(() -> new float[this.width]);
   }

   public static ExportDrawings build(List<Drawing> drawings, ImageExport.Area area, ResourceManager resources, Set<String> fontOptions) {
      ExportDrawings out = new ExportDrawings(area);
      double perUnit = 4.0 / (1 << area.lod());
      List<String> texts = new ArrayList<>();

      for (Drawing drawing : drawings) {
         if (drawing.kind == Drawing.Kind.NOTE && drawing.complete()) {
            texts.add(drawing.text);
         }
      }

      ExportFont font = texts.isEmpty() ? null : ExportFont.load(resources, texts, fontOptions);
      float reach = 8.0F;

      for (Drawing drawing : drawings) {
         if (drawing.complete()) {
            int n = drawing.pointCount();
            float[] p = new float[n * 2];
            float top = Float.MAX_VALUE;
            float bottom = -Float.MAX_VALUE;
            float left = Float.MAX_VALUE;
            float right = -Float.MAX_VALUE;

            for (int k = 0; k < n; k++) {
               p[k * 2] = (float)(drawing.points[k * 2] * perUnit - area.x());
               p[k * 2 + 1] = (float)(drawing.points[k * 2 + 1] * perUnit - area.y());
               left = Math.min(left, p[k * 2]);
               right = Math.max(right, p[k * 2]);
               top = Math.min(top, p[k * 2 + 1]);
               bottom = Math.max(bottom, p[k * 2 + 1]);
            }

            if (drawing.kind == Drawing.Kind.NOTE) {
               out.plate(font, drawing.text, p[0], p[1], drawing.color);
            } else if (!(right + reach < 0.0F) && !(left - reach > area.width()) && !(bottom + reach < 0.0F) && !(top - reach > area.height())) {
               out.shapes.add(new ExportDrawings.Shape(drawing.kind, p, drawing.color, top - reach, bottom + reach, left - reach, right + reach));
            }
         }
      }

      return out;
   }

   private void plate(ExportFont font, String text, float x, float y, int color) {
      String shaped = ExportFont.shaped(text);
      int textWidth = (int)Math.ceil(font.width(text));
      int plateLeft = Math.min(-2, textWidth + 1);
      int plateRight = Math.max(-2, textWidth + 1);
      float[] written = font.span(text);
      float[] drawn = font.span(shaped);
      float[] writtenRows = font.extent(text);
      float[] drawnRows = font.extent(shaped);
      int left = (int)Math.floor(Math.min(plateLeft, Math.min(written[0], drawn[0])));
      int right = (int)Math.ceil(Math.max(plateRight, Math.max(written[1], drawn[1])));
      int top = (int)Math.floor(Math.min(-2.0F, Math.min(writtenRows[0], drawnRows[0])));
      int bottom = (int)Math.ceil(Math.max(9.0F, Math.max(writtenRows[1], drawnRows[1])));
      int w = (right - left) * 2;
      int h = (bottom - top) * 2;
      int textX = Math.round(x - textWidth * 2 / 2.0F);
      int textY = Math.round(y - 8.0F);
      int canvasLeft = textX + left * 2;
      int canvasTop = textY + top * 2;
      if (w > 0 && h > 0 && canvasLeft + w > 0 && canvasLeft < this.width && canvasTop + h > 0 && canvasTop < this.height) {
         int[] argb = new int[w * h];
         int plateTop = (-2 - top) * 2;

         for (int row = plateTop; row < plateTop + 22; row++) {
            Arrays.fill(argb, row * w + (plateLeft - left) * 2, row * w + (plateRight - left) * 2, -1879048192);
         }

         font.draw(argb, w, h, text, -left * 2, -top * 2, 2, color & 16777215);
         this.plates.add(new ExportDrawings.Plate(argb, w, h, canvasLeft, canvasTop));
      }
   }

   @Override
   public void row(int[] abgr, long y, long x0) {
      float cy = (float)(y - this.top) + 0.5F;

      for (ExportDrawings.Shape shape : this.shapes) {
         if (!(cy < shape.top()) && !(cy > shape.bottom())) {
            if (shape.kind() == Drawing.Kind.AREA) {
               this.fill(abgr, shape, cy, 1073741824 | shape.color() & 16777215);
               this.line(abgr, shape, cy, true, 2.0F, 0xFF000000 | shape.color());
            } else {
               this.line(abgr, shape, cy, false, 8.0F, DrawingOverlay.rim(shape.color()));
               this.line(abgr, shape, cy, false, 4.0F, 0xFF000000 | shape.color());
            }
         }
      }

      int iy = (int)Math.floor(cy);

      for (ExportDrawings.Plate plate : this.plates) {
         int py = iy - plate.y();
         if (py >= 0 && py < plate.height()) {
            for (int px = Math.max(0, -plate.x()); px < plate.width() && plate.x() + px < abgr.length; px++) {
               int i = plate.x() + px;
               abgr[i] = over(abgr[i], plate.argb()[py * plate.width() + px]);
            }
         }
      }
   }

   private void line(int[] abgr, ExportDrawings.Shape shape, float cy, boolean closed, float width, int argb) {
      float half = width / 2.0F;
      float[] p = shape.points();
      int n = p.length / 2;
      float[] cover = this.coverage.get();
      int from = Integer.MAX_VALUE;
      int to = Integer.MIN_VALUE;
      int segments = closed ? n : n - 1;

      for (int k = 0; k < segments; k++) {
         int j = (k + 1) % n;
         float ax = p[k * 2];
         float ay = p[k * 2 + 1];
         float bx = p[j * 2];
         float by = p[j * 2 + 1];
         if (!(cy < Math.min(ay, by) - half - 1.0F) && !(cy > Math.max(ay, by) + half + 1.0F)) {
            float t0 = 0.0F;
            float t1 = 1.0F;
            if (Math.abs(by - ay) > 1.0E-4F) {
               float ta = (cy - half - 1.0F - ay) / (by - ay);
               float tb = (cy + half + 1.0F - ay) / (by - ay);
               t0 = Math.max(0.0F, Math.min(ta, tb));
               t1 = Math.min(1.0F, Math.max(ta, tb));
            }

            float sx0 = ax + (bx - ax) * t0;
            float sx1 = ax + (bx - ax) * t1;
            int x0 = Math.max(0, (int)Math.floor(Math.min(sx0, sx1) - half - 1.0F));
            int x1 = Math.min(this.width - 1, (int)Math.ceil(Math.max(sx0, sx1) + half + 1.0F));
            if (x0 <= x1) {
               if (from > to) {
                  Arrays.fill(cover, x0, x1 + 1, 0.0F);
                  from = x0;
                  to = x1;
               } else {
                  if (x0 < from) {
                     Arrays.fill(cover, x0, from, 0.0F);
                     from = x0;
                  }

                  if (x1 > to) {
                     Arrays.fill(cover, to + 1, x1 + 1, 0.0F);
                     to = x1;
                  }
               }

               float dx = bx - ax;
               float dy = by - ay;
               float length = dx * dx + dy * dy;

               for (int x = x0; x <= x1; x++) {
                  float px = x + 0.5F;
                  float t = length == 0.0F ? 0.0F : Math.max(0.0F, Math.min(1.0F, ((px - ax) * dx + (cy - ay) * dy) / length));
                  float d = (float)Math.hypot(px - (ax + t * dx), cy - (ay + t * dy));
                  float c = Math.max(0.0F, Math.min(1.0F, half + 0.5F - d));
                  if (c > cover[x]) {
                     cover[x] = c;
                  }
               }
            }
         }
      }

      int alpha = argb >>> 24;

      for (int x = from; x <= to; x++) {
         if (cover[x] > 0.0F) {
            abgr[x] = over(abgr[x], Math.round(alpha * cover[x]) << 24 | argb & 16777215);
         }
      }
   }

   private void fill(int[] abgr, ExportDrawings.Shape shape, float cy, int argb) {
      float[] p = shape.points();
      int n = p.length / 2;
      float[] crossings = new float[n];
      int count = 0;
      int k = 0;

      for (int j = n - 1; k < n; j = k++) {
         float ay = p[k * 2 + 1];
         float by = p[j * 2 + 1];
         if (ay > cy != by > cy) {
            crossings[count++] = p[k * 2] + (cy - ay) * (p[j * 2] - p[k * 2]) / (by - ay);
         }
      }

      Arrays.sort(crossings, 0, count);

      for (int i = 0; i + 1 < count; i += 2) {
         int x0 = Math.max(0, (int)Math.ceil(crossings[i] - 0.5F));
         int x1 = Math.min(this.width - 1, (int)Math.floor(crossings[i + 1] - 0.5F));

         for (int x = x0; x <= x1; x++) {
            abgr[x] = over(abgr[x], argb);
         }
      }
   }

   private static int over(int abgr, int argb) {
      int a = argb >>> 24;
      if (a == 0) {
         return abgr;
      }

      int r = ((argb >> 16 & 0xFF) * a + (abgr & 0xFF) * (255 - a)) / 255;
      int g = ((argb >> 8 & 0xFF) * a + (abgr >> 8 & 0xFF) * (255 - a)) / 255;
      int b = ((argb & 0xFF) * a + (abgr >> 16 & 0xFF) * (255 - a)) / 255;
      return 0xFF000000 | b << 16 | g << 8 | r;
   }

   private record Plate(int[] argb, int width, int height, int x, int y) {
   }

   private record Shape(Drawing.Kind kind, float[] points, int color, float top, float bottom, float left, float right) {
   }
}
