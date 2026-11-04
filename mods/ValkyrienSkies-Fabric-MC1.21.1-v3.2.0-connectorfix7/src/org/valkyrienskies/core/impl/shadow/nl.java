package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class nl implements kS, ln {
   private final double[] a;
   private final ni[] b;
   private final int c;

   public nl(double[] var1, ni[] var2) {
      if (var1 == null) {
         throw new pl();
      }

      if (var1.length < 2) {
         throw new pn(px.NOT_ENOUGH_POINTS_IN_SPLINE_PARTITION, 2, var1.length, false);
      }

      if (var1.length - 1 != var2.length) {
         throw new oS(var2.length, var1.length);
      }

      Ch.a(var1);
      this.c = var1.length - 1;
      this.a = new double[this.c + 1];
      System.arraycopy(var1, 0, this.a, 0, this.c + 1);
      this.b = new ni[this.c];
      System.arraycopy(var2, 0, this.b, 0, this.c);
   }

   @Override
   public final double a(double var1) {
      if (!(var1 < this.a[0]) && !(var1 > this.a[this.c])) {
         int var3;
         if ((var3 = Arrays.binarySearch(this.a, var1)) < 0) {
            var3 = -var3 - 2;
         }

         if (var3 >= this.b.length) {
            var3--;
         }

         return this.b[var3].a(var1 - this.a[var3]);
      } else {
         throw new po(var1, this.a[0], this.a[this.c]);
      }
   }

   @Override
   public final lc a() {
      return this.b();
   }

   private nl b() {
      ni[] var1 = new ni[this.c];

      for (int var2 = 0; var2 < this.c; var2++) {
         var1[var2] = this.b[var2].c();
      }

      return new nl(this.a, var1);
   }

   @Override
   public final lg a(lg var1) {
      double var2;
      if (!((var2 = var1.b[0]) < this.a[0]) && !(var2 > this.a[this.c])) {
         int var8;
         if ((var8 = Arrays.binarySearch(this.a, var2)) < 0) {
            var8 = -var8 - 2;
         }

         if (var8 >= this.b.length) {
            var8--;
         }

         ni var10000 = this.b[var8];
         double var6 = this.a[var8];
         return var10000.a(var1.h(-var6));
      } else {
         throw new po(var2, this.a[0], this.a[this.c]);
      }
   }

   private int c() {
      return this.c;
   }

   private ni[] d() {
      ni[] var1 = new ni[this.c];
      System.arraycopy(this.b, 0, var1, 0, this.c);
      return var1;
   }

   private double[] e() {
      double[] var1 = new double[this.c + 1];
      System.arraycopy(this.a, 0, var1, 0, this.c + 1);
      return var1;
   }

   private boolean b(double var1) {
      return !(var1 < this.a[0]) && !(var1 > this.a[this.c]);
   }
}
