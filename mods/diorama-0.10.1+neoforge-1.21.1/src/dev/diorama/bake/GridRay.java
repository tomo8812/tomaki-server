package dev.diorama.bake;

import org.jspecify.annotations.Nullable;

final class GridRay {
   private final int[] size;
   final int[] cell = new int[3];
   int axis;
   double enter;
   private final double[] o = new double[3];
   private final double[] r = new double[3];
   private final int[] step = new int[3];
   private final double[] next = new double[3];
   private final double[] delta = new double[3];
   private double end;

   GridRay(CraftBlocks blocks) {
      this.size = new int[]{blocks.sizeX, blocks.sizeY, blocks.sizeZ};
   }

   boolean start(double ox, double oy, double oz, double rx, double ry, double rz, double from) {
      double[] o = this.o;
      double[] r = this.r;
      o[0] = ox;
      o[1] = oy;
      o[2] = oz;
      r[0] = rx;
      r[1] = ry;
      r[2] = rz;
      double t0 = from;
      double t1 = Double.MAX_VALUE;
      this.axis = -1;

      for (int a = 0; a < 3; a++) {
         if (Math.abs(r[a]) < 1.0E-12) {
            if (o[a] < 0.0 || o[a] > this.size[a]) {
               return false;
            }
         } else {
            double ta = -o[a] / r[a];
            double tb = (this.size[a] - o[a]) / r[a];
            if (Math.min(ta, tb) > t0) {
               t0 = Math.min(ta, tb);
               this.axis = a;
            }

            t1 = Math.min(t1, Math.max(ta, tb));
         }
      }

      if (t0 >= t1) {
         return false;
      }

      this.end = t1;
      this.enter = t0;
      if (this.axis < 0) {
         this.axis = 1;
      }

      double inside = t0 + 1.0E-7;

      for (int a = 0; a < 3; a++) {
         this.cell[a] = Math.max(0, Math.min(this.size[a] - 1, (int)Math.floor(o[a] + r[a] * inside)));
         if (Math.abs(r[a]) < 1.0E-12) {
            this.step[a] = 0;
            this.next[a] = Double.MAX_VALUE;
            this.delta[a] = Double.MAX_VALUE;
         } else {
            this.step[a] = r[a] > 0.0 ? 1 : -1;
            this.next[a] = (this.cell[a] + (r[a] > 0.0 ? 1 : 0) - o[a]) / r[a];
            this.delta[a] = Math.abs(1.0 / r[a]);
         }
      }

      return true;
   }

   double exit() {
      return Math.min(this.end, Math.min(this.next[0], Math.min(this.next[1], this.next[2])));
   }

   boolean next() {
      int a = this.next[0] < this.next[1] ? (this.next[0] < this.next[2] ? 0 : 2) : (this.next[1] < this.next[2] ? 1 : 2);
      if (this.next[a] >= this.end) {
         return false;
      } else {
         this.cell[a] = this.cell[a] + this.step[a];
         if (this.cell[a] >= 0 && this.cell[a] < this.size[a]) {
            this.enter = this.next[a];
            this.next[a] = this.next[a] + this.delta[a];
            this.axis = a;
            return true;
         } else {
            return false;
         }
      }
   }

   static double @Nullable [] invert(double[] l) {
      double a = l[0];
      double b = l[3];
      double c = l[6];
      double d = l[1];
      double e = l[4];
      double f = l[7];
      double g = l[2];
      double h = l[5];
      double i = l[8];
      double det = a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g);
      if (Math.abs(det) > 1.0E-9 && Double.isFinite(det)) {
         double k = 1.0 / det;
         return new double[]{
            (e * i - f * h) * k,
            (c * h - b * i) * k,
            (b * f - c * e) * k,
            (f * g - d * i) * k,
            (a * i - c * g) * k,
            (c * d - a * f) * k,
            (d * h - e * g) * k,
            (b * g - a * h) * k,
            (a * e - b * d) * k
         };
      } else {
         return null;
      }
   }
}
