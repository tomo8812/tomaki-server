package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class BL {
   private static final int[] a = new int[]{
      1,
      2,
      4,
      8,
      16,
      32,
      64,
      128,
      256,
      512,
      1024,
      2048,
      4096,
      8192,
      16384,
      32768,
      65536,
      131072,
      262144,
      524288,
      1048576,
      2097152,
      4194304,
      8388608,
      16777216,
      33554432,
      67108864,
      134217728,
      268435456,
      536870912,
      1073741824
   };

   private BL() {
   }

   public static double[] a(double[] var0, double var1) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         var0[var3] *= var1;
      }

      return var0;
   }

   private static nQ[] a(nQ[] var0, double var1) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         nQ var4;
         nQ var5;
         var0[var3] = new nQ(var1 * (var4 = var0[var3]).d, var1 * (var5 = var0[var3]).c);
      }

      return var0;
   }

   public static double[][] a(nQ[] var0) {
      double[][] var1;
      double[] var2 = (var1 = new double[2][var0.length])[0];
      double[] var3 = var1[1];

      for (int var4 = 0; var4 < var0.length; var4++) {
         nQ var5 = var0[var4];
         var2[var4] = var5.d;
         var3[var4] = var5.c;
      }

      return var1;
   }

   public static nQ[] a(double[][] var0) {
      if (var0.length != 2) {
         throw new oS(var0.length, 2);
      }

      double[] var1 = var0[0];
      double[] var5 = var0[1];
      if (var1.length != var5.length) {
         throw new oS(var5.length, var1.length);
      }

      int var2;
      nQ[] var3 = new nQ[var2 = var1.length];

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = new nQ(var1[var4], var5[var4]);
      }

      return var3;
   }

   private static int a(int var0) {
      int var1;
      if ((var1 = Arrays.binarySearch(a, var0)) < 0) {
         throw new oV(px.NOT_POWER_OF_TWO_CONSIDER_PADDING, var0);
      } else {
         return var1;
      }
   }
}
