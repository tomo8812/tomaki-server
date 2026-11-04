package org.valkyrienskies.core.impl.shadow;

public final class zw implements zH {
   private static final int[] a = new int[]{
      2,
      3,
      5,
      7,
      11,
      13,
      17,
      19,
      23,
      29,
      31,
      37,
      41,
      43,
      47,
      53,
      59,
      61,
      67,
      71,
      73,
      79,
      83,
      89,
      97,
      101,
      103,
      107,
      109,
      113,
      127,
      131,
      137,
      139,
      149,
      151,
      157,
      163,
      167,
      173
   };
   private static final int[] b = new int[]{
      1, 2, 3, 3, 8, 11, 12, 14, 7, 18, 12, 13, 17, 18, 29, 14, 18, 43, 41, 44, 40, 30, 47, 65, 71, 28, 40, 60, 79, 89, 56, 50, 52, 61, 108, 56, 66, 63, 60, 66
   };
   private final int c;
   private int d = 0;
   private final int[] e;
   private final int[] f;

   private zw(int var1) {
      this(var1, a, b);
   }

   private zw(int var1, int[] var2, int[] var3) {
      Ci.a(var2);
      if (var1 > 0 && var1 <= var2.length) {
         if (var3 != null && var3.length != var2.length) {
            throw new oS(var3.length, var2.length);
         }

         this.c = var1;
         this.e = (int[])var2.clone();
         this.f = var3 == null ? null : (int[])var3.clone();
         this.d = 0;
      } else {
         throw new po(var1, 1, a.length);
      }
   }

   @Override
   public final double[] a() {
      double[] var1 = new double[this.c];

      for (int var2 = 0; var2 < this.c; var2++) {
         int var3 = this.d;

         for (double var4 = 1.0 / this.e[var2]; var3 > 0; var4 /= this.e[var2]) {
            int var9 = var3 % this.e[var2];
            int var8 = this.e[var2];
            int var7 = var2;
            zw var6 = this;
            int var10 = this.f != null ? var6.f[var7] * var9 % var8 : var9;
            var1[var2] += var4 * var10;
            var3 /= this.e[var2];
         }
      }

      this.d++;
      return var1;
   }

   private int a(int var1, int var2, int var3) {
      return this.f != null ? this.f[var1] * var3 % var2 : var3;
   }

   private double[] a(int var1) {
      this.d = var1;
      return this.a();
   }

   private int b() {
      return this.d;
   }
}
