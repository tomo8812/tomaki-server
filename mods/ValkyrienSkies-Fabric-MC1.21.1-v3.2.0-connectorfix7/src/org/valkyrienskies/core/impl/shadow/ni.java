package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public final class ni implements Serializable, kS, ln {
   private static final long b = -7726511984200295583L;
   public final double[] a;

   public ni(double[] var1) {
      Ci.a(var1);
      int var2;
      if ((var2 = var1.length) == 0) {
         throw new pf(px.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
      }

      while (var2 > 1 && var1[var2 - 1] == 0.0) {
         var2--;
      }

      this.a = new double[var2];
      System.arraycopy(var1, 0, this.a, 0, var2);
   }

   @Override
   public final double a(double var1) {
      return a(this.a, var1);
   }

   private int d() {
      return this.a.length - 1;
   }

   public final double[] b() {
      return (double[])this.a.clone();
   }

   protected static double a(double[] var0, double var1) {
      Ci.a(var0);
      int var3;
      if ((var3 = var0.length) == 0) {
         throw new pf(px.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
      }

      double var4 = var0[var3 - 1];

      for (int var6 = var3 - 2; var6 >= 0; var6--) {
         var4 = var1 * var4 + var0[var6];
      }

      return var4;
   }

   @Override
   public final lg a(lg var1) {
      Ci.a(this.a);
      int var2;
      if ((var2 = this.a.length) == 0) {
         throw new pf(px.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
      }

      lg var3 = new lg(var1.C(), var1.D(), this.a[var2 - 1]);

      for (int var4 = var2 - 2; var4 >= 0; var4--) {
         var3 = var3.b(var1).h(this.a[var4]);
      }

      return var3;
   }

   public final ni a(ni var1) {
      int var2 = BY.a(this.a.length, var1.a.length);
      int var3;
      double[] var4 = new double[var3 = BY.b(this.a.length, var1.a.length)];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = this.a[var5] + var1.a[var5];
      }

      System.arraycopy(this.a.length < var1.a.length ? var1.a : this.a, var2, var4, var2, var3 - var2);
      return new ni(var4);
   }

   private ni c(ni var1) {
      int var2 = BY.a(this.a.length, var1.a.length);
      int var3;
      double[] var4 = new double[var3 = BY.b(this.a.length, var1.a.length)];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = this.a[var5] - var1.a[var5];
      }

      if (this.a.length < var1.a.length) {
         for (int var6 = var2; var6 < var3; var6++) {
            var4[var6] = -var1.a[var6];
         }
      } else {
         System.arraycopy(this.a, var2, var4, var2, var3 - var2);
      }

      return new ni(var4);
   }

   private ni e() {
      double[] var1 = new double[this.a.length];

      for (int var2 = 0; var2 < this.a.length; var2++) {
         var1[var2] = -this.a[var2];
      }

      return new ni(var1);
   }

   public final ni b(ni var1) {
      double[] var2 = new double[this.a.length + var1.a.length - 1];

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = 0.0;

         for (int var4 = BY.b(0, var3 + 1 - var1.a.length); var4 < BY.a(this.a.length, var3 + 1); var4++) {
            var2[var3] += this.a[var4] * var1.a[var3 - var4];
         }
      }

      return new ni(var2);
   }

   private static double[] a(double[] var0) {
      Ci.a(var0);
      int var1;
      if ((var1 = var0.length) == 0) {
         throw new pf(px.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
      }

      if (var1 == 1) {
         return new double[]{0.0};
      }

      double[] var2 = new double[var1 - 1];
      var1--;

      while (var1 > 0) {
         var2[var1 - 1] = var1 * var0[var1];
         var1--;
      }

      return var2;
   }

   public final ni c() {
      return new ni(a(this.a));
   }

   @Override
   public final lc a() {
      return this.c();
   }

   @Override
   public final String toString() {
      StringBuilder var1 = new StringBuilder();
      if (this.a[0] == 0.0) {
         if (this.a.length == 1) {
            return "0";
         }
      } else {
         var1.append(b(this.a[0]));
      }

      for (int var2 = 1; var2 < this.a.length; var2++) {
         if (this.a[var2] != 0.0) {
            if (var1.length() > 0) {
               if (this.a[var2] < 0.0) {
                  var1.append(" - ");
               } else {
                  var1.append(" + ");
               }
            } else if (this.a[var2] < 0.0) {
               var1.append("-");
            }

            double var3;
            if ((var3 = BY.w(this.a[var2])) - 1.0 != 0.0) {
               var1.append(b(var3));
               var1.append(' ');
            }

            var1.append("x");
            if (var2 > 1) {
               var1.append('^');
               var1.append(Integer.toString(var2));
            }
         }
      }

      return var1.toString();
   }

   private static String b(double var0) {
      String var2;
      return (var2 = Double.toString(var0)).endsWith(".0") ? var2.substring(0, var2.length() - 2) : var2;
   }

   @Override
   public final int hashCode() {
      return 31 + Arrays.hashCode(this.a);
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof ni)) {
         return false;
      }

      obj = (ni)obj;
      return Arrays.equals(this.a, ((ni)obj).a);
   }

   public static class a implements kZ {
      @Override
      public final double[] b(double var1, double... var3) {
         double[] var4 = new double[var3.length];
         double var5 = 1.0;

         for (int var7 = 0; var7 < var3.length; var7++) {
            var4[var7] = var5;
            var5 *= var1;
         }

         return var4;
      }

      @Override
      public final double a(double var1, double... var3) {
         return ni.a(var3, var1);
      }
   }
}
