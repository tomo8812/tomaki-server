package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.Comparator;

@Deprecated
public abstract class yx implements yo {
   yp[] a;
   double[][] b;
   final int c;

   private yx(int var1) {
      this(var1, 1.0);
   }

   protected yx(int var1, double var2) {
      this(a(var1, var2));
   }

   protected yx(double[] var1) {
      if (var1 == null) {
         throw new pl();
      }

      if (var1.length == 0) {
         throw new pr();
      }

      this.c = var1.length;
      this.b = new double[this.c][this.c];

      for (int var2 = 0; var2 < this.c; var2++) {
         double[] var3 = this.b[var2];

         for (int var4 = 0; var4 < var2 + 1; var4++) {
            if (var1[var4] == 0.0) {
               throw new pr(px.EQUAL_VERTICES_IN_SIMPLEX);
            }

            System.arraycopy(var1, 0, var3, 0, var4 + 1);
         }
      }
   }

   protected yx(double[][] var1) {
      if (var1.length <= 0) {
         throw new pk(px.SIMPLEX_NEED_ONE_POINT, var1.length);
      }

      this.c = var1.length - 1;
      this.b = new double[this.c][this.c];
      double[] var2 = var1[0];

      for (int var3 = 0; var3 < var1.length; var3++) {
         double[] var4;
         if ((var4 = var1[var3]).length != this.c) {
            throw new oS(var4.length, this.c);
         }

         for (int var5 = 0; var5 < var3; var5++) {
            double[] var6 = var1[var5];
            boolean var7 = true;

            for (int var8 = 0; var8 < this.c; var8++) {
               if (var4[var8] != var6[var8]) {
                  var7 = false;
                  break;
               }
            }

            if (var7) {
               throw new oV(px.EQUAL_VERTICES_IN_SIMPLEX, var3, var5);
            }
         }

         if (var3 > 0) {
            double[] var9 = this.b[var3 - 1];

            for (int var10 = 0; var10 < this.c; var10++) {
               var9[var10] = var4[var10] - var2[var10];
            }
         }
      }
   }

   public final int a() {
      return this.c;
   }

   public final int b() {
      return this.a.length;
   }

   public abstract void a(kW var1, Comparator<yp> var2);

   public final void a(double[] var1) {
      if (this.c != var1.length) {
         throw new oS(this.c, var1.length);
      }

      this.a = new yp[this.c + 1];
      this.a[0] = new yp(var1, Double.NaN);

      for (int var2 = 0; var2 < this.c; var2++) {
         double[] var3 = this.b[var2];
         double[] var4 = new double[this.c];

         for (int var5 = 0; var5 < this.c; var5++) {
            var4[var5] = var1[var5] + var3[var5];
         }

         this.a[var2 + 1] = new yp(var4, Double.NaN);
      }
   }

   public final void a(kW var1, Comparator<yp> var2) {
      for (int var3 = 0; var3 < this.a.length; var3++) {
         yp var4;
         double[] var5 = (double[])(var4 = this.a[var3]).a;
         if (Double.isNaN((Double)var4.c())) {
            this.a[var3] = new yp(var5, var1.a(var5), false);
         }
      }

      Arrays.sort(this.a, var2);
   }

   protected final void a(yp var1, Comparator<yp> var2) {
      for (int var3 = 0; var3 < this.c; var3++) {
         if (var2.compare(this.a[var3], var1) > 0) {
            yp var4 = this.a[var3];
            this.a[var3] = var1;
            var1 = var4;
         }
      }

      this.a[this.c] = var1;
   }

   public final yp[] c() {
      yp[] var1 = new yp[this.a.length];
      System.arraycopy(this.a, 0, var1, 0, this.a.length);
      return var1;
   }

   public final yp a(int var1) {
      if (var1 >= 0 && var1 < this.a.length) {
         return this.a[var1];
      } else {
         throw new po(var1, 0, this.a.length - 1);
      }
   }

   protected final void a(int var1, yp var2) {
      if (var1 >= 0 && var1 < this.a.length) {
         this.a[var1] = var2;
      } else {
         throw new po(var1, 0, this.a.length - 1);
      }
   }

   protected final void a(yp[] var1) {
      if (var1.length != this.a.length) {
         throw new oS(var1.length, this.a.length);
      }

      this.a = var1;
   }

   private static double[] a(int var0, double var1) {
      double[] var3 = new double[var0];

      for (int var4 = 0; var4 < var0; var4++) {
         var3[var4] = var1;
      }

      return var3;
   }
}
