package dev.diorama.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntConsumer;

public final class ArrowArt {
   public static final int GUI_SIZE = 36;
   public static final int SCALE = 4;
   private static final int SS = 16;
   private static final int WHITE = 16777215;
   private static final int LIT_SIDE = 15265524;
   private static final int DARK = 1053720;
   private static final double[] SUN = normalise(
      -0.878 * Math.cos(Math.toRadians(38.0)), Math.sin(Math.toRadians(38.0)), 0.479 * Math.cos(Math.toRadians(38.0))
   );

   private ArrowArt() {
   }

   public static PinArt.Image voxel(double yawDegrees, float size, double sin, double cos, int accent) {
      int shade = PinArt.mix(accent, 0, 0.33F);
      float s = size * 1.15F;
      float[] outer = turn(dart(s), yawDegrees);
      float[] inner = turn(dart(s * 0.5F), yawDegrees);
      int range = (int)(size * 1.3F) + 2;
      List<int[]> cells = new ArrayList<>();

      for (int i = -range; i <= range; i++) {
         for (int j = -range; j <= range; j++) {
            if (PinArt.inside(outer, i + 0.5F, j + 0.5F)) {
               cells.add(new int[]{i, j, PinArt.inside(inner, i + 0.5F, j + 0.5F) ? 1 : 0});
            }
         }
      }

      double minX = Double.MAX_VALUE;
      double maxX = -Double.MAX_VALUE;
      double minY = Double.MAX_VALUE;
      double maxY = -Double.MAX_VALUE;
      List<double[]> corners = new ArrayList<>();

      for (int[] c : cells) {
         for (int k = 0; k < 4; k++) {
            double x = c[0] + (k != 1 && k != 2 ? 0 : 1);
            double z = c[1] + (k >= 2 ? 1 : 0);

            for (int y = 0; y <= 1; y++) {
               corners.add(new double[]{x, y, z});
               double sx = x;
               double sy = z * sin - y * cos;
               minX = Math.min(minX, sx);
               maxX = Math.max(maxX, sx);
               minY = Math.min(minY, sy);
               maxY = Math.max(maxY, sy);
            }
         }
      }

      ArrowArt.Canvas canvas = new ArrowArt.Canvas(18.0 - (minX + maxX) / 2.0, 18.0 - (minY + maxY) / 2.0, sin);
      List<double[]> flat = new ArrayList<>();

      for (double[] p : corners) {
         double t = p[1] / SUN[1];
         flat.add(new double[]{p[0] - SUN[0] * t, p[2] - SUN[2] * t});
      }

      List<double[]> hull = hull(flat);
      double[] shadow = new double[hull.size() * 2];

      for (int i = 0; i < hull.size(); i++) {
         shadow[i * 2] = hull.get(i)[0];
         shadow[i * 2 + 1] = hull.get(i)[1] * sin;
      }

      canvas.cast(shadow);
      cells.sort(Comparator.comparingInt(cx -> cx[1]));

      for (int[] c : cells) {
         prism(canvas, c[0], c[1], c[2] == 1 ? accent : 16777215, sin, cos, shade);
      }

      return canvas.result(0.28F, 1.0F);
   }

   private static float[] dart(float s) {
      return new float[]{1.3F * s, 0.0F, -0.7F * s, 0.55F * s, -0.3F * s, 0.0F, -0.7F * s, -0.55F * s};
   }

   private static float[] turn(float[] pts, double yawDegrees) {
      double yaw = Math.toRadians(yawDegrees);
      double fx = -Math.sin(yaw);
      double fz = Math.cos(yaw);
      double px = -fz;
      double pz = fx;
      float[] out = new float[pts.length];

      for (int i = 0; i < pts.length; i += 2) {
         out[i] = (float)(fx * pts[i] + px * pts[i + 1]);
         out[i + 1] = (float)(fz * pts[i] + pz * pts[i + 1]);
      }

      return out;
   }

   private static void prism(ArrowArt.Canvas canvas, int i, int j, int top, double sin, double cos, int shade) {
      double[][] base = new double[][]{{i, j}, {i + 1, j}, {i + 1, j + 1}, {i, j + 1}};
      List<Object[]> faces = new ArrayList<>();
      faces.add(new Object[]{new double[][]{{i, 1.0, j}, {i + 1, 1.0, j}, {i + 1, 1.0, j + 1}, {i, 1.0, j + 1}}, top, new double[]{0.0, 1.0, 0.0}});
      double area = 0.0;

      for (int k = 0; k < 4; k++) {
         area += base[k][0] * base[(k + 1) % 4][1] - base[(k + 1) % 4][0] * base[k][1];
      }

      int sign = area > 0.0 ? 1 : -1;

      for (int k = 0; k < 4; k++) {
         double x0 = base[k][0];
         double z0 = base[k][1];
         double x1 = base[(k + 1) % 4][0];
         double z1 = base[(k + 1) % 4][1];
         double[] n = new double[]{sign * (z1 - z0), 0.0, -sign * (x1 - x0)};
         faces.add(new Object[]{new double[][]{{x0, 0.0, z0}, {x1, 0.0, z1}, {x1, 1.0, z1}, {x0, 1.0, z0}}, material(n, shade), n});
      }

      List<Object[]> shown = new ArrayList<>();

      for (Object[] f : faces) {
         double[] n = (double[])f[2];
         if (n[1] * sin + n[2] * cos > 1.0E-9) {
            shown.add(f);
         }
      }

      shown.sort(Comparator.comparingDouble(fx -> {
         double depth = 0.0;

         for (double[] p : (double[][])fx[0]) {
            depth += -p[1] * sin - p[2] * cos;
         }

         return -depth;
      }));

      for (Object[] f : shown) {
         double[][] pts = (double[][])f[0];
         double[] poly = new double[pts.length * 2];

         for (int k = 0; k < pts.length; k++) {
            poly[k * 2] = pts[k][0];
            poly[k * 2 + 1] = pts[k][2] * sin - pts[k][1] * cos;
         }

         canvas.poly(poly, (Integer)f[1]);
      }
   }

   private static int material(double[] n, int shade) {
      double[] u = normalise(n[0], n[1], n[2]);
      double lit = Math.max(0.0, u[0] * SUN[0] + u[1] * SUN[1] + u[2] * SUN[2]) / SUN[1];
      return PinArt.mix(shade, 15265524, (float)Math.min(1.0, lit));
   }

   private static double[] normalise(double x, double y, double z) {
      double l = Math.sqrt(x * x + y * y + z * z);
      return new double[]{x / l, y / l, z / l};
   }

   private static List<double[]> hull(List<double[]> points) {
      List<double[]> pts = new ArrayList<>(points);
      pts.sort((a, b) -> a[0] != b[0] ? Double.compare(a[0], b[0]) : Double.compare(a[1], b[1]));
      List<double[]> lower = new ArrayList<>();

      for (double[] p : pts) {
         while (lower.size() >= 2 && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0.0) {
            lower.remove(lower.size() - 1);
         }

         lower.add(p);
      }

      List<double[]> upper = new ArrayList<>();

      for (int i = pts.size() - 1; i >= 0; i--) {
         double[] p = pts.get(i);

         while (upper.size() >= 2 && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0.0) {
            upper.remove(upper.size() - 1);
         }

         upper.add(p);
      }

      lower.remove(lower.size() - 1);
      upper.remove(upper.size() - 1);
      lower.addAll(upper);
      return lower;
   }

   private static double cross(double[] o, double[] a, double[] b) {
      return (a[0] - o[0]) * (b[1] - o[1]) - (a[1] - o[1]) * (b[0] - o[0]);
   }

   private static final class Canvas {
      private final int size = 576;
      private final float[] r = new float[576 * 576];
      private final float[] g = new float[576 * 576];
      private final float[] b = new float[576 * 576];
      private final boolean[] covered = new boolean[576 * 576];
      private final boolean[] shadow = new boolean[576 * 576];
      private final double ox;
      private final double oy;

      Canvas(double ox, double oy, double sin) {
         this.ox = ox;
         this.oy = oy;
      }

      void poly(double[] pts, int rgb) {
         this.fill(pts, i -> {
            this.r[i] = rgb >> 16 & 0xFF;
            this.g[i] = rgb >> 8 & 0xFF;
            this.b[i] = rgb & 0xFF;
            this.covered[i] = true;
         });
      }

      void cast(double[] pts) {
         this.fill(pts, i -> this.shadow[i] = true);
      }

      private void fill(double[] pts, IntConsumer pixel) {
         float[] p = new float[pts.length];
         float minX = Float.MAX_VALUE;
         float maxX = -Float.MAX_VALUE;
         float minY = Float.MAX_VALUE;
         float maxY = -Float.MAX_VALUE;

         for (int k = 0; k < pts.length; k += 2) {
            p[k] = (float)((pts[k] + this.ox) * 16.0);
            p[k + 1] = (float)((pts[k + 1] + this.oy) * 16.0);
            minX = Math.min(minX, p[k]);
            maxX = Math.max(maxX, p[k]);
            minY = Math.min(minY, p[k + 1]);
            maxY = Math.max(maxY, p[k + 1]);
         }

         int x0 = Math.max(0, (int)Math.floor(minX));
         int x1 = Math.min(576 - 1, (int)Math.ceil(maxX));
         int y0 = Math.max(0, (int)Math.floor(minY));
         int y1 = Math.min(576 - 1, (int)Math.ceil(maxY));

         for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
               if (PinArt.inside(p, x + 0.5F, y + 0.5F)) {
                  pixel.accept(y * 576 + x);
               }
            }
         }
      }

      PinArt.Image result(float shadowAlpha, float rim) {
         int w = 144;
         int f = 4;
         float[] sa = new float[w * w];
         float[] sr = new float[w * w];
         float[] sg = new float[w * w];
         float[] sb = new float[w * w];
         float[] sh = new float[w * w];

         for (int y = 0; y < w; y++) {
            for (int x = 0; x < w; x++) {
               float cov = 0.0F;
               float rr = 0.0F;
               float gg = 0.0F;
               float bb = 0.0F;
               float shade = 0.0F;

               for (int dy = 0; dy < f; dy++) {
                  for (int dx = 0; dx < f; dx++) {
                     int i = (y * f + dy) * 576 + x * f + dx;
                     if (this.covered[i]) {
                        cov++;
                        rr += this.r[i];
                        gg += this.g[i];
                        bb += this.b[i];
                     }

                     if (this.shadow[i]) {
                        shade++;
                     }
                  }
               }

               int i = y * w + x;
               sa[i] = cov / (f * f);
               if (cov > 0.0F) {
                  sr[i] = rr / cov;
                  sg[i] = gg / cov;
                  sb[i] = bb / cov;
               }

               sh[i] = shade / (f * f) * shadowAlpha;
            }
         }

         float reach = rim * 4.0F;
         int span = (int)reach + 1;
         int[] kx = new int[(span * 2 + 1) * (span * 2 + 1)];
         int[] ky = new int[kx.length];
         float[] kw = new float[kx.length];
         int kn = 0;

         for (int dy = -span; dy <= span; dy++) {
            for (int dx = -span; dx <= span; dx++) {
               double d = Math.hypot(dx, dy);
               if (d <= reach + 0.5) {
                  kx[kn] = dx;
                  ky[kn] = dy;
                  kw[kn] = (float)Math.min(1.0, reach + 0.5 - d);
                  kn++;
               }
            }
         }

         PinArt.Image out = new PinArt.Image(w, w);

         for (int y = 0; y < w; y++) {
            for (int x = 0; x < w; x++) {
               float grown = 0.0F;

               for (int k = 0; k < kn; k++) {
                  int xx = x - kx[k];
                  int yy = y - ky[k];
                  if (xx >= 0 && yy >= 0 && xx < w && yy < w) {
                     float v = sa[yy * w + xx] * kw[k];
                     if (v > grown) {
                        grown = v;
                     }
                  }
               }

               int i = y * w + x;
               float a = sh[i];
               float cr = 0.0F;
               float cg = 0.0F;
               float cb = 0.0F;
               float ringAlpha = grown * 0.9F;
               float na = ringAlpha + a * (1.0F - ringAlpha);
               if (na > 0.0F) {
                  cr = (16.0F * ringAlpha + cr * a * (1.0F - ringAlpha)) / na;
                  cg = (20.0F * ringAlpha + cg * a * (1.0F - ringAlpha)) / na;
                  cb = (24.0F * ringAlpha + cb * a * (1.0F - ringAlpha)) / na;
               }

               a = na;
               float top = sa[i];
               na = top + a * (1.0F - top);
               if (na > 0.0F) {
                  cr = (sr[i] * top + cr * a * (1.0F - top)) / na;
                  cg = (sg[i] * top + cg * a * (1.0F - top)) / na;
                  cb = (sb[i] * top + cb * a * (1.0F - top)) / na;
               }

               out.r[i] = cr;
               out.g[i] = cg;
               out.b[i] = cb;
               out.a[i] = na * 255.0F;
            }
         }

         return out;
      }
   }
}
