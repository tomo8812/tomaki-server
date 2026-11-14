package dev.diorama.render;

import org.jspecify.annotations.Nullable;

public final class Circle {
   private static final int SIDES = 96;
   private static final float[] UNIT = new float[192];
   private static float[] shape = new float[202];
   private static float[] kept = new float[202];
   public final float cx;
   public final float cy;
   public final float r;
   private final float[] points = new float[192];
   private float @Nullable [] disc;

   public Circle(float cx, float cy, float r) {
      this.cx = cx;
      this.cy = cy;
      this.r = r;

      for (int i = 0; i < 96; i++) {
         this.points[i * 2] = cx + r * UNIT[i * 2];
         this.points[i * 2 + 1] = cy + r * UNIT[i * 2 + 1];
      }
   }

   public static Circle in(MapViewport.View view) {
      return new Circle(view.x() + view.width() / 2.0F, view.y() + view.height() / 2.0F, Math.min(view.width(), view.height()) / 2.0F);
   }

   public boolean contains(float x, float y) {
      float dx = x - this.cx;
      float dy = y - this.cy;
      return dx * dx + dy * dy <= this.r * this.r;
   }

   public Circle.@Nullable Quads clip(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1) {
      float nearX = Math.max(x0, Math.min(this.cx, x1)) - this.cx;
      float nearY = Math.max(y0, Math.min(this.cy, y1)) - this.cy;
      if (nearX * nearX + nearY * nearY >= this.r * this.r) {
         return null;
      }

      if (this.contains(x0, y0) && this.contains(x1, y0) && this.contains(x0, y1) && this.contains(x1, y1)) {
         return new Circle.Quads(new float[]{x0, y0, x0, y1, x1, y1, x1, y0}, new float[]{u0, v0, u0, v1, u1, v1, u1, v0});
      }

      float[] corners;
      if (x0 <= this.cx - this.r && y0 <= this.cy - this.r && x1 >= this.cx + this.r && y1 >= this.cy + this.r) {
         corners = this.disc();
      } else {
         int count = this.cut(x0, y0, x1, y1);
         if (count < 3) {
            return null;
         }

         corners = fan(shape, count);
      }

      float du = (u1 - u0) / (x1 - x0);
      float dv = (v1 - v0) / (y1 - y0);
      float[] uvs = new float[corners.length];

      for (int i = 0; i < corners.length; i += 2) {
         uvs[i] = u0 + (corners[i] - x0) * du;
         uvs[i + 1] = v0 + (corners[i + 1] - y0) * dv;
      }

      return new Circle.Quads(corners, uvs);
   }

   public float[] disc() {
      if (this.disc == null) {
         this.disc = fan(this.points, 96);
      }

      return this.disc;
   }

   public Circle.Quads ring(float width, float pictureWidth) {
      float inner = Math.max(0.0F, this.r - width);
      float innerUv = 0.5F - pictureWidth;
      float[] corners = new float[768];
      float[] uvs = new float[768];
      int out = 0;

      for (int i = 0; i < 96; i++) {
         int j = (i + 1) % 96;
         out = this.corner(corners, uvs, out, i, this.r, 0.5F);
         out = this.corner(corners, uvs, out, j, this.r, 0.5F);
         out = this.corner(corners, uvs, out, j, inner, innerUv);
         out = this.corner(corners, uvs, out, i, inner, innerUv);
      }

      return new Circle.Quads(corners, uvs);
   }

   private int corner(float[] corners, float[] uvs, int out, int side, float radius, float uvRadius) {
      corners[out] = this.cx + radius * UNIT[side * 2];
      corners[out + 1] = this.cy + radius * UNIT[side * 2 + 1];
      uvs[out] = 0.5F + uvRadius * UNIT[side * 2];
      uvs[out + 1] = 0.5F + uvRadius * UNIT[side * 2 + 1];
      return out + 2;
   }

   public float @Nullable [] clip(float[] polygon, int count) {
      boolean inside = true;

      for (int k = 0; k < count && inside; k++) {
         inside = this.contains(polygon[k * 2], polygon[k * 2 + 1]);
      }

      if (inside) {
         return fan(polygon, count);
      }

      int left = this.cut(polygon, count);
      return left < 3 ? null : fan(shape, left);
   }

   private int cut(float x0, float y0, float x1, float y1) {
      return this.cut(new float[]{x0, y0, x0, y1, x1, y1, x1, y0}, 4);
   }

   private int cut(float[] polygon, int corners) {
      if (shape.length < (corners + 96) * 2) {
         shape = new float[(corners + 96) * 2];
         kept = new float[(corners + 96) * 2];
      }

      System.arraycopy(polygon, 0, shape, 0, corners * 2);
      int count = corners;

      for (int i = 0; i < 96 && count > 0; i++) {
         int j = (i + 1) % 96;
         float ax = this.points[i * 2];
         float ay = this.points[i * 2 + 1];
         float ex = this.points[j * 2] - ax;
         float ey = this.points[j * 2 + 1] - ay;
         float inside = Math.signum(ex * (this.cy - ay) - ey * (this.cx - ax));
         boolean cuts = false;

         for (int k = 0; k < corners * 2 && !cuts; k += 2) {
            cuts = inside * (ex * (polygon[k + 1] - ay) - ey * (polygon[k] - ax)) < 0.0F;
         }

         if (cuts) {
            int keptCount = 0;

            for (int k = 0; k < count; k++) {
               int n = (k + 1) % count;
               float px = shape[k * 2];
               float py = shape[k * 2 + 1];
               float qx = shape[n * 2];
               float qy = shape[n * 2 + 1];
               float sp = inside * (ex * (py - ay) - ey * (px - ax));
               float sq = inside * (ex * (qy - ay) - ey * (qx - ax));
               if (sp >= 0.0F) {
                  kept[keptCount * 2] = px;
                  kept[keptCount * 2 + 1] = py;
                  keptCount++;
               }

               if (sp >= 0.0F != sq >= 0.0F) {
                  float t = sp / (sp - sq);
                  kept[keptCount * 2] = px + (qx - px) * t;
                  kept[keptCount * 2 + 1] = py + (qy - py) * t;
                  keptCount++;
               }
            }

            float[] swap = shape;
            shape = kept;
            kept = swap;
            count = keptCount;
         }
      }

      return count;
   }

   private static float[] fan(float[] polygon, int count) {
      float[] corners = new float[(count - 1) / 2 * 8];
      int out = 0;

      for (int k = 1; k + 1 < count; k += 2) {
         out = corner(polygon, 0, corners, out);
         out = corner(polygon, k, corners, out);
         out = corner(polygon, k + 1, corners, out);
         out = corner(polygon, Math.min(k + 2, count - 1), corners, out);
      }

      return corners;
   }

   private static int corner(float[] polygon, int at, float[] corners, int out) {
      corners[out] = polygon[at * 2];
      corners[out + 1] = polygon[at * 2 + 1];
      return out + 2;
   }

   static {
      for (int i = 0; i < 96; i++) {
         double a = (Math.PI * 2) * i / 96.0;
         UNIT[i * 2] = (float)Math.cos(a);
         UNIT[i * 2 + 1] = (float)(-Math.sin(a));
      }
   }

   public record Quads(float[] corners, float[] uvs) {
   }
}
