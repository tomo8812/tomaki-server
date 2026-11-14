package dev.diorama.render;

import org.jspecify.annotations.Nullable;

public final class PinArt {
   static final int INK = 1053720;
   static final int DEATH_FACE = 2829617;
   private static final float RIM_ALPHA = 0.94F;
   private static final String[] HEADSTONE = new String[]{"..gWg..", ".gWWWg.", ".WgdgW.", ".WWWWW.", ".WgdgW.", "ggggggg", "gWWWWWg"};
   private static final String[] LARGE_PIN = new String[]{
      "........###........",
      ".....#########.....",
      "....###########....",
      "...#############...",
      "...#############...",
      "...#############...",
      "..###############..",
      "..###############..",
      "..###############..",
      "...#############...",
      "...#############...",
      "...#############...",
      "....###########....",
      ".....#########.....",
      "......#######......",
      ".......#####.......",
      ".......#####.......",
      "........###........",
      "........###........",
      ".........#.........",
      ".........#........."
   };
   private static final String[] SMALL_PIN = new String[]{
      ".......###.......",
      ".....#######.....",
      "....#########....",
      "...###########...",
      "...###########...",
      "..#############..",
      "..#############..",
      "..#############..",
      "...###########...",
      "...###########...",
      "....#########....",
      ".....#######.....",
      "......#####......",
      "......#####......",
      ".......###.......",
      ".......###.......",
      ".......###.......",
      "........#........"
   };
   private static final int STICKER_WHITE = 16185078;
   private static final int ICON_TIP = 6;

   private PinArt() {
   }

   public static PinArt.Art standing(int colour, boolean death, int head, int stretch) {
      int d = standingHead(head);
      int tip = d == 13 ? 5 : 6;
      int face = death ? 2829617 : colour;
      int w = d + 4 + stretch;
      int h = d + tip + 3;
      PinArt.Image image = new PinArt.Image(w, h);
      int ox = 2;
      float ax = ox + (d + stretch) / 2.0F;
      float ay = d + tip;
      image.over(ellipse(w, h, ax, ay - 0.5F, d * 0.26F + 0.6F, 1.3F), 0, 0.28F);
      boolean[] mask = outline(stretched(d == 13 ? SMALL_PIN : LARGE_PIN, d, stretch), w, h);
      face(image, mask, w, h, face);
      if (death) {
         headstone(image, ox + 1, 1, ox + d - 1 + stretch, d - 1);
      }

      return new PinArt.Art(image, ax, ay, ox + 1, 1, ox + d - 1 + stretch, d - 1);
   }

   public static int stretchFor(int head, int ink) {
      int d = standingHead(head);
      if (ink <= d - 4) {
         return 0;
      }

      int extra = ink - (d - 8);
      return extra + (extra & 1);
   }

   private static String[] stretched(String[] rows, int head, int stretch) {
      if (stretch == 0) {
         return rows;
      }

      String[] out = new String[rows.length];
      int middle = rows[0].length() / 2;

      for (int y = 0; y < rows.length; y++) {
         String row = rows[y];
         out[y] = y < head
            ? row.substring(0, middle) + String.valueOf(row.charAt(middle)).repeat(stretch) + row.substring(middle)
            : ".".repeat(stretch / 2) + row + ".".repeat(stretch / 2);
      }

      return out;
   }

   public static PinArt.Art badge(int colour, boolean death, int d, int stretch, double caretAngle, int caretSize, PinArt.@Nullable Image icon) {
      int face = death ? 2829617 : colour;
      boolean caret = !Double.isNaN(caretAngle);
      int pad = caret ? 6 : 0;
      int w = d + stretch + pad * 2;
      int h = d + pad * 2;
      PinArt.Image image = new PinArt.Image(w, h);
      float cx = pad + (d + stretch) / 2.0F;
      float cy = pad + d / 2.0F;
      float r = d / 2.0F;
      float half = stretch / 2.0F;
      boolean[] mask = pill(w, h, cx, cy, r, half);
      face(image, mask, w, h, face);
      if (death) {
         headstone(image, pad + 1, pad + 1, pad + d + stretch - 1, pad + d - 1);
      } else if (icon != null) {
         sticker(image, icon, cx, cy);
      }

      if (caret) {
         float fx = (float)Math.cos(caretAngle);
         float fy = (float)Math.sin(caretAngle);
         float px = -fy;
         float py = fx;
         float reach = pillReach(r, half, fx, fy);
         float x = cx + fx * (reach + 1.5F);
         float y = cy + fy * (reach + 1.5F);
         boolean[] m = polygon(
            w,
            h,
            new float[]{
               x + fx * caretSize * 0.55F,
               y + fy * caretSize * 0.55F,
               x - fx * caretSize * 0.4F + px * caretSize * 0.5F,
               y - fy * caretSize * 0.4F + py * caretSize * 0.5F,
               x - fx * caretSize * 0.4F - px * caretSize * 0.5F,
               y - fy * caretSize * 0.4F - py * caretSize * 0.5F
            }
         );
         boolean[] rim = dilate(m, w, h);

         for (int i = 0; i < rim.length; i++) {
            rim[i] &= !m[i] && image.a[i] < 1.0F;
         }

         image.put(rim, 1053720, 0.9F);
         image.put(m, colour, 1.0F);
      }

      return icon != null && !death
         ? new PinArt.Art(image, cx, cy, 0, 0, 0, 0)
         : new PinArt.Art(image, cx, cy, pad + 1, pad + 1, pad + d + stretch - 1, pad + d - 1);
   }

   private static float pillReach(float r, float half, float fx, float fy) {
      float lo = 0.0F;
      float hi = r + half;

      for (int i = 0; i < 20; i++) {
         float t = (lo + hi) / 2.0F;
         float dx = Math.max(0.0F, Math.abs(fx * t) - half);
         float dy = fy * t;
         if (dx * dx + dy * dy < r * r) {
            lo = t;
         } else {
            hi = t;
         }
      }

      return lo;
   }

   public static int iconHead(PinArt.Image icon) {
      int d = Math.max(icon.width, icon.height) + 10;
      return d % 2 == 0 ? d + 1 : d;
   }

   public static float iconHeadAbove(PinArt.Image icon) {
      return iconHead(icon) / 2.0F + 6.0F;
   }

   public static PinArt.Art iconPin(PinArt.Image icon, int colour) {
      int d = iconHead(icon);
      int w = d + 4;
      int h = d + 6 + 3;
      PinArt.Image image = new PinArt.Image(w, h);
      float ax = 2.0F + d / 2.0F;
      float ay = d + 6;
      image.over(ellipse(w, h, ax, ay - 0.5F, d * 0.26F + 0.6F, 1.3F), 0, 0.28F);
      boolean[] mask = disc(w, h, ax, d / 2.0F, d / 2.0F);
      boolean[] point = polygon(w, h, new float[]{ax - d * 0.32F, d * 0.75F, ax + d * 0.32F, d * 0.75F, ax, ay});

      for (int i = 0; i < mask.length; i++) {
         mask[i] |= point[i];
      }

      face(image, mask, w, h, colour);
      sticker(image, icon, ax, d / 2.0F);
      return new PinArt.Art(image, ax, ay, 0, 0, 0, 0);
   }

   private static void sticker(PinArt.Image image, PinArt.Image icon, float cx, float cy) {
      int w = image.width;
      int ix = Math.round(cx - icon.width / 2.0F);
      int iy = Math.round(cy - icon.height / 2.0F);
      boolean[] shape = new boolean[image.width * image.height];

      for (int y = 0; y < icon.height; y++) {
         for (int x = 0; x < icon.width; x++) {
            shape[(iy + y) * w + ix + x] = icon.a[y * icon.width + x] >= 128.0F;
         }
      }

      boolean[] edge = grow4(shape, w, image.height);

      for (int i = 0; i < edge.length; i++) {
         edge[i] &= !shape[i];
      }

      image.put(edge, 16185078, 1.0F);

      for (int y = 0; y < icon.height; y++) {
         for (int x = 0; x < icon.width; x++) {
            int s = y * icon.width + x;
            if (icon.a[s] >= 128.0F) {
               int t = (iy + y) * w + ix + x;
               image.r[t] = icon.r[s];
               image.g[t] = icon.g[s];
               image.b[t] = icon.b[s];
               image.a[t] = 255.0F;
            }
         }
      }
   }

   public static PinArt.Image fromArgb(int width, int height, int[] argb) {
      PinArt.Image image = new PinArt.Image(width, height);

      for (int i = 0; i < argb.length; i++) {
         image.r[i] = argb[i] >> 16 & 0xFF;
         image.g[i] = argb[i] >> 8 & 0xFF;
         image.b[i] = argb[i] & 0xFF;
         image.a[i] = argb[i] >>> 24;
      }

      return image;
   }

   public static int standingHead(int head) {
      return head >= 14 ? 15 : 13;
   }

   public static int centred(int x0, int x1, int size) {
      return x0 + Math.floorDiv(x1 - x0 - size + 1, 2);
   }

   public static boolean light(int rgb) {
      int r = rgb >> 16 & 0xFF;
      int g = rgb >> 8 & 0xFF;
      int b = rgb & 0xFF;
      return 0.299 * r + 0.587 * g + 0.114 * b > 160.0;
   }

   private static void face(PinArt.Image image, boolean[] mask, int w, int h, int colour) {
      boolean[] ring = ring(mask, w, h);
      boolean[] inside = new boolean[mask.length];

      for (int i = 0; i < mask.length; i++) {
         inside[i] = mask[i] && !ring[i];
      }

      image.put(ring, 1053720, 0.94F);
      image.put(inside, colour, 1.0F);
      boolean[] top = new boolean[mask.length];
      boolean[] bottom = new boolean[mask.length];

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            int i = y * w + x;
            if (inside[i]) {
               boolean ringAboveOrLeft = at(ring, w, h, x, y - 1) || at(ring, w, h, x - 1, y);
               boolean ringBelowOrRight = at(ring, w, h, x, y + 1) || at(ring, w, h, x + 1, y);
               top[i] = ringAboveOrLeft && !ringBelowOrRight;
               bottom[i] = ringBelowOrRight && !ringAboveOrLeft;
            }
         }
      }

      image.put(top, mix(colour, 16777215, 0.35F), 1.0F);
      image.put(bottom, mix(colour, 0, 0.25F), 1.0F);
   }

   private static void headstone(PinArt.Image image, int x0, int y0, int x1, int y1) {
      int size = HEADSTONE.length;
      int left = centred(x0, x1, size);
      int top = centred(y0, y1, size);

      for (int y = 0; y < size; y++) {
         for (int x = 0; x < size; x++) {
            int rgb = switch (HEADSTONE[y].charAt(x)) {
               case 'W' -> 12763857;
               case 'd' -> 6316144;
               case 'g' -> 8289935;
               default -> -1;
            };
            if (rgb >= 0) {
               int i = (top + y) * image.width + left + x;
               image.r[i] = rgb >> 16 & 0xFF;
               image.g[i] = rgb >> 8 & 0xFF;
               image.b[i] = rgb & 0xFF;
               image.a[i] = 255.0F;
            }
         }
      }
   }

   static int mix(int rgb, int other, float t) {
      int r = Math.round((rgb >> 16 & 0xFF) * (1.0F - t) + (other >> 16 & 0xFF) * t);
      int g = Math.round((rgb >> 8 & 0xFF) * (1.0F - t) + (other >> 8 & 0xFF) * t);
      int b = Math.round((rgb & 0xFF) * (1.0F - t) + (other & 0xFF) * t);
      return r << 16 | g << 8 | b;
   }

   private static boolean at(boolean[] m, int w, int h, int x, int y) {
      return x >= 0 && y >= 0 && x < w && y < h && m[y * w + x];
   }

   private static boolean[] disc(int w, int h, float cx, float cy, float r) {
      boolean[] m = new boolean[w * h];
      float rr = (r - 0.25F) * (r - 0.25F);

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            float dx = x + 0.5F - cx;
            float dy = y + 0.5F - cy;
            m[y * w + x] = dx * dx + dy * dy <= rr;
         }
      }

      return m;
   }

   private static boolean[] pill(int w, int h, float cx, float cy, float r, float half) {
      boolean[] m = new boolean[w * h];
      float rr = (r - 0.25F) * (r - 0.25F);

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            float dx = Math.max(0.0F, Math.abs(x + 0.5F - cx) - half);
            float dy = y + 0.5F - cy;
            m[y * w + x] = dx * dx + dy * dy <= rr;
         }
      }

      return m;
   }

   private static boolean[] ellipse(int w, int h, float cx, float cy, float rx, float ry) {
      boolean[] m = new boolean[w * h];

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            float dx = (x + 0.5F - cx) / rx;
            float dy = (y + 0.5F - cy) / ry;
            m[y * w + x] = dx * dx + dy * dy <= 1.0F;
         }
      }

      return m;
   }

   private static boolean[] polygon(int w, int h, float[] pts) {
      boolean[] m = new boolean[w * h];

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            int hits = 0;

            for (int sy = 0; sy < 4; sy++) {
               for (int sx = 0; sx < 4; sx++) {
                  if (inside(pts, x + (sx + 0.5F) / 4.0F, y + (sy + 0.5F) / 4.0F)) {
                     hits++;
                  }
               }
            }

            m[y * w + x] = hits >= 8;
         }
      }

      return m;
   }

   static boolean inside(float[] pts, float x, float y) {
      boolean in = false;
      int n = pts.length / 2;
      int i = 0;

      for (int j = n - 1; i < n; j = i++) {
         float xi = pts[i * 2];
         float yi = pts[i * 2 + 1];
         float xj = pts[j * 2];
         float yj = pts[j * 2 + 1];
         if (yi > y != yj > y && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
            in = !in;
         }
      }

      return in;
   }

   private static boolean[] outline(String[] rows, int w, int h) {
      boolean[] m = new boolean[w * h];

      for (int y = 0; y < rows.length; y++) {
         for (int x = 0; x < w; x++) {
            m[y * w + x] = rows[y].charAt(x) == '#';
         }
      }

      return m;
   }

   private static boolean[] ring(boolean[] m, int w, int h) {
      boolean[] out = new boolean[m.length];

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            int i = y * w + x;
            if (m[i]) {
               boolean edge = x == 0 || y == 0 || x == w - 1 || y == h - 1;
               out[i] = edge || !m[i - 1] || !m[i + 1] || !m[i - w] || !m[i + w];
            }
         }
      }

      return out;
   }

   private static boolean[] grow4(boolean[] m, int w, int h) {
      boolean[] out = new boolean[m.length];

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            out[y * w + x] = at(m, w, h, x, y) || at(m, w, h, x - 1, y) || at(m, w, h, x + 1, y) || at(m, w, h, x, y - 1) || at(m, w, h, x, y + 1);
         }
      }

      return out;
   }

   private static boolean[] dilate(boolean[] m, int w, int h) {
      boolean[] out = new boolean[m.length];

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            boolean any = false;

            for (int dy = -1; dy <= 1 && !any; dy++) {
               for (int dx = -1; dx <= 1 && !any; dx++) {
                  any = at(m, w, h, x + dx, y + dy);
               }
            }

            out[y * w + x] = any;
         }
      }

      return out;
   }

   public record Art(PinArt.Image image, float anchorX, float anchorY, int labelX0, int labelY0, int labelX1, int labelY1) {
   }

   public static final class Image {
      public final int width;
      public final int height;
      final float[] r;
      final float[] g;
      final float[] b;
      final float[] a;

      Image(int width, int height) {
         this.width = width;
         this.height = height;
         this.r = new float[width * height];
         this.g = new float[width * height];
         this.b = new float[width * height];
         this.a = new float[width * height];
      }

      void put(boolean[] mask, int rgb, float alpha) {
         for (int i = 0; i < mask.length; i++) {
            if (mask[i]) {
               this.r[i] = rgb >> 16 & 0xFF;
               this.g[i] = rgb >> 8 & 0xFF;
               this.b[i] = rgb & 0xFF;
               this.a[i] = alpha * 255.0F;
            }
         }
      }

      void over(boolean[] mask, int rgb, float alpha) {
         for (int i = 0; i < mask.length; i++) {
            if (mask[i]) {
               float dst = this.a[i] / 255.0F;
               float out = alpha + dst * (1.0F - alpha);
               this.r[i] = ((rgb >> 16 & 0xFF) * alpha + this.r[i] * dst * (1.0F - alpha)) / out;
               this.g[i] = ((rgb >> 8 & 0xFF) * alpha + this.g[i] * dst * (1.0F - alpha)) / out;
               this.b[i] = ((rgb & 0xFF) * alpha + this.b[i] * dst * (1.0F - alpha)) / out;
               this.a[i] = out * 255.0F;
            }
         }
      }

      public int[] abgr(int edge) {
         int[] out = new int[edge * edge];

         for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
               int i = y * this.width + x;
               out[y * edge + x] = Math.round(this.a[i]) << 24 | Math.round(this.b[i]) << 16 | Math.round(this.g[i]) << 8 | Math.round(this.r[i]);
            }
         }

         return out;
      }

      public int argb(int x, int y) {
         int i = y * this.width + x;
         return Math.round(this.a[i]) << 24 | Math.round(this.r[i]) << 16 | Math.round(this.g[i]) << 8 | Math.round(this.b[i]);
      }
   }
}
