package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public final class Ch {
   private Ch() {
   }

   private static double[] b(double var0, double[] var2) {
      double[] var3 = new double[var2.length];

      for (int var4 = 0; var4 < var2.length; var4++) {
         var3[var4] = var2[var4] * var0;
      }

      return var3;
   }

   public static void a(double var0, double[] var2) {
      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] *= var0;
      }
   }

   private static double[] k(double[] var0, double[] var1) {
      double[] var3 = var1;
      a(var0, var3, true);
      double[] var2 = (double[])var0.clone();

      for (int var4 = 0; var4 < var0.length; var4++) {
         var2[var4] += var1[var4];
      }

      return var2;
   }

   public static double[] a(double[] var0, double[] var1) {
      double[] var3 = var1;
      a(var0, var3, true);
      double[] var2 = (double[])var0.clone();

      for (int var4 = 0; var4 < var0.length; var4++) {
         var2[var4] -= var1[var4];
      }

      return var2;
   }

   private static double[] l(double[] var0, double[] var1) {
      double[] var3 = var1;
      a(var0, var3, true);
      double[] var2 = (double[])var0.clone();

      for (int var4 = 0; var4 < var0.length; var4++) {
         var2[var4] *= var1[var4];
      }

      return var2;
   }

   public static double[] b(double[] var0, double[] var1) {
      double[] var3 = var1;
      a(var0, var3, true);
      double[] var2 = (double[])var0.clone();

      for (int var4 = 0; var4 < var0.length; var4++) {
         var2[var4] /= var1[var4];
      }

      return var2;
   }

   public static double c(double[] var0, double[] var1) {
      double[] var3 = var1;
      a(var0, var3, true);
      double var2 = 0.0;

      for (int var4 = 0; var4 < var0.length; var4++) {
         var2 += BY.w(var0[var4] - var1[var4]);
      }

      return var2;
   }

   private static int c(int[] var0, int[] var1) {
      int[] var3 = var1;
      a(var0, var3, true);
      int var2 = 0;

      for (int var4 = 0; var4 < var0.length; var4++) {
         var2 += BY.a(var0[var4] - var1[var4]);
      }

      return var2;
   }

   public static double d(double[] var0, double[] var1) {
      double[] var3 = var1;
      a(var0, var3, true);
      double var2 = 0.0;

      for (int var4 = 0; var4 < var0.length; var4++) {
         double var5 = var0[var4] - var1[var4];
         var2 += var5 * var5;
      }

      return BY.a(var2);
   }

   public static double e(double[] var0, double[] var1) {
      return h(var0, var1) / (d(var0) * d(var1));
   }

   public static double a(int[] var0, int[] var1) {
      int[] var3 = var1;
      a(var0, var3, true);
      double var2 = 0.0;

      for (int var4 = 0; var4 < var0.length; var4++) {
         double var5 = var0[var4] - var1[var4];
         var2 += var5 * var5;
      }

      return BY.a(var2);
   }

   public static double f(double[] var0, double[] var1) {
      double[] var3 = var1;
      a(var0, var3, true);
      double var2 = 0.0;

      for (int var4 = 0; var4 < var0.length; var4++) {
         var2 = BY.e(var2, BY.w(var0[var4] - var1[var4]));
      }

      return var2;
   }

   private static int d(int[] var0, int[] var1) {
      int[] var3 = var1;
      a(var0, var3, true);
      int var2 = 0;

      for (int var4 = 0; var4 < var0.length; var4++) {
         var2 = BY.b(var2, BY.a(var0[var4] - var1[var4]));
      }

      return var2;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static <T extends Comparable<? super T>> boolean a(T[] var0, int var1, boolean var2) {
      Comparable var3 = var0[0];
      int var4 = var0.length;

      for (int var5 = 1; var5 < var4; var5++) {
         switch (<unrepresentable>.a[var1 - 1]) {
            case 1:
               int var7 = var3.compareTo(var0[var5]);
               if (var2) {
                  if (var7 >= 0) {
                     return false;
                  }
               } else if (var7 > 0) {
                  return false;
               }
               break;
            case 2:
               int var6 = var0[var5].compareTo(var3);
               if (var2) {
                  if (var6 >= 0) {
                     return false;
                  }
               } else if (var6 > 0) {
                  return false;
               }
               break;
            default:
               throw new oY();
         }

         var3 = var0[var5];
      }

      return true;
   }

   private static boolean a(double[] var0, int var1, boolean var2) {
      return a(var0, var1, var2, false);
   }

   public static boolean a(double[] var0, double[] var1, boolean var2) {
      if (var0.length == var1.length) {
         return true;
      } else {
         throw new oS(var0.length, var1.length);
      }
   }

   public static void g(double[] var0, double[] var1) {
      a(var0, var1, true);
   }

   public static boolean a(int[] var0, int[] var1, boolean var2) {
      if (var0.length == var1.length) {
         return true;
      } else {
         throw new oS(var0.length, var1.length);
      }
   }

   public static void b(int[] var0, int[] var1) {
      a(var0, var1, true);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static boolean a(double[] var0, int var1, boolean var2, boolean var3) {
      double var4 = var0[0];
      int var6 = var0.length;
      int var7 = 1;

      while (true) {
         label46: {
            if (var7 < var6) {
               switch (<unrepresentable>.a[var1 - 1]) {
                  case 1:
                     if (var2 ? !(var0[var7] <= var4) : !(var0[var7] < var4)) {
                        break label46;
                     }
                     break;
                  case 2:
                     if (var2 ? !(var0[var7] >= var4) : !(var0[var7] > var4)) {
                        break label46;
                     }
                     break;
                  default:
                     throw new oY();
               }
            }

            if (var7 == var6) {
               return true;
            }

            if (var3) {
               throw new pg(var0[var7], var4, var7, var1, var2);
            }

            return false;
         }

         var4 = var0[var7];
         var7++;
      }
   }

   private static void b(double[] var0, int var1, boolean var2) {
      a(var0, var1, true, true);
   }

   public static void a(double[] var0) {
      b(var0, Ch.b.INCREASING$37efe540, true);
   }

   public static void a(long[][] var0) {
      Ci.a(var0);

      for (int var1 = 1; var1 < var0.length; var1++) {
         if (var0[var1].length != var0[0].length) {
            throw new oS(px.DIFFERENT_ROWS_LENGTHS, var0[var1].length, var0[0].length);
         }
      }
   }

   public static void b(double[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         if (var0[var1] <= 0.0) {
            throw new pk(var0[var1]);
         }
      }
   }

   public static void c(double[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         if (Double.isNaN(var0[var1])) {
            throw new ph();
         }
      }
   }

   public static void a(long[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         if (var0[var1] < 0L) {
            throw new pj(var0[var1]);
         }
      }
   }

   public static void b(long[][] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         for (int var2 = 0; var2 < var0[var1].length; var2++) {
            if (var0[var1][var2] < 0L) {
               throw new pj(var0[var1][var2]);
            }
         }
      }
   }

   public static double d(double[] var0) {
      double var1 = 0.0;
      double var3 = 0.0;
      double var5 = 0.0;
      double var7 = 0.0;
      double var9 = 0.0;
      double var11 = var0.length;
      double var13 = 1.304E19 / var11;

      for (int var15 = 0; var15 < var0.length; var15++) {
         double var16;
         if (!((var16 = BY.w(var0[var15])) < 3.834E-20) && !(var16 > var13)) {
            var3 += var16 * var16;
         } else if (var16 > 3.834E-20) {
            if (var16 > var7) {
               double var18 = var7 / var16;
               var1 = 1.0 + var1 * var18 * var18;
               var7 = var16;
            } else {
               double var21 = var16 / var7;
               var1 += var21 * var21;
            }
         } else if (var16 > var9) {
            double var22 = var9 / var16;
            var5 = 1.0 + var5 * var22 * var22;
            var9 = var16;
         } else if (var16 != 0.0) {
            double var23 = var16 / var9;
            var5 += var23 * var23;
         }
      }

      double var20;
      if (var1 != 0.0) {
         var20 = var7 * Math.sqrt(var1 + var3 / var7 / var7);
      } else if (var3 == 0.0) {
         var20 = var9 * Math.sqrt(var5);
      } else if (var3 >= var9) {
         var20 = Math.sqrt(var3 * (1.0 + var9 / var3 * (var9 * var5)));
      } else {
         var20 = Math.sqrt(var9 * (var3 / var9 + var9 * var5));
      }

      return var20;
   }

   public static void a(double[] var0, double[]... var1) {
      a(var0, Ch.b.INCREASING$37efe540, var1);
   }

   private static void a(double[] var0, int var1, double[]... var2) {
      if (var0 == null) {
         throw new pl();
      }

      int var3 = var2.length;
      int var4 = var0.length;

      for (int var5 = 0; var5 < var3; var5++) {
         double[] var6;
         if ((var6 = var2[var5]) == null) {
            throw new pl();
         }

         if (var6.length != var4) {
            throw new oS(var6.length, var4);
         }
      }

      ArrayList var11 = new ArrayList(var4);

      for (int var13 = 0; var13 < var4; var13++) {
         var11.add(new Ch.c(var0[var13], var13));
      }

      Comparator var14 = var1 == Ch.b.INCREASING$37efe540 ? new Comparator<Ch.c>() {
         private static int a(Ch.c var0x, Ch.c var1x) {
            return Double.compare(var0x.a, var1x.a);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public final int compare(Object var1, Object var2) {
            Ch.c var10000 = (Ch.c)var1;
            var2 = var2;
            return Double.compare(var10000.a, var2.a);
         }
      } : new Comparator<Ch.c>() {
         private static int a(Ch.c var0, Ch.c var1) {
            return Double.compare(var1.a, var0.a);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public final int compare(Object var1, Object var2) {
            Ch.c var10000 = var1;
            var2 = var2;
            var1 = var10000;
            return Double.compare(var2.a, var1.a);
         }
      };
      Collections.sort(var11, var14);
      int[] var10 = new int[var4];

      for (int var15 = 0; var15 < var4; var15++) {
         Ch.c var7 = (Ch.c)var11.get(var15);
         var0[var15] = var7.a;
         var10[var15] = var7.b;
      }

      for (int var16 = 0; var16 < var3; var16++) {
         double[] var17;
         var0 = (double[])(var17 = var2[var16]).clone();

         for (int var12 = 0; var12 < var4; var12++) {
            var17[var12] = var0[var10[var12]];
         }
      }
   }

   public static int[] a(int[] var0) {
      return a(var0, var0.length);
   }

   public static double[] e(double[] var0) {
      return a(var0, var0.length);
   }

   public static int[] a(int[] var0, int var1) {
      int[] var2 = new int[var1];
      System.arraycopy(var0, 0, var2, 0, BY.a(var1, var0.length));
      return var2;
   }

   public static double[] a(double[] var0, int var1) {
      double[] var2 = new double[var1];
      System.arraycopy(var0, 0, var2, 0, BY.a(var1, var0.length));
      return var2;
   }

   public static double[] a(double[] var0, int var1, int var2) {
      int var4;
      double[] var3 = new double[var4 = var2 - var1];
      System.arraycopy(var0, var1, var3, 0, BY.a(var4, var0.length - var1));
      return var3;
   }

   public static double h(double[] var0, double[] var1) {
      double[] var3 = var1;
      a(var0, var3, true);
      int var2;
      if ((var2 = var0.length) == 1) {
         return var0[0] * var1[0];
      }

      var3 = new double[var2];
      double var4 = 0.0;

      for (int var6 = 0; var6 < var2; var6++) {
         double var7;
         double var9 = Double.longBitsToDouble(Double.doubleToRawLongBits(var7 = var0[var6]) & -134217728L);
         double var11 = var7 - var9;
         double var13;
         double var15 = Double.longBitsToDouble(Double.doubleToRawLongBits(var13 = var1[var6]) & -134217728L);
         double var17 = var13 - var15;
         var3[var6] = var7 * var13;
         double var19 = var11 * var17 - (var3[var6] - var9 * var15 - var11 * var15 - var9 * var17);
         var4 += var19;
      }

      double var22 = var3[0];
      double var8 = var3[1];
      double var10;
      double var12 = (var10 = var22 + var8) - var8;
      double var14 = var8 - (var10 - var12) + (var22 - var12);
      int var16 = var2 - 1;

      for (int var25 = 1; var25 < var16; var25++) {
         var8 = var3[var25 + 1];
         double var18;
         var12 = (var18 = var10 + var8) - var8;
         var14 += var8 - (var18 - var12) + (var10 - var12);
         var10 = var18;
      }

      double var26;
      if (Double.isNaN(var26 = var10 + (var4 + var14))) {
         var26 = 0.0;

         for (int var27 = 0; var27 < var2; var27++) {
            var26 += var0[var27] * var1[var27];
         }
      }

      return var26;
   }

   public static double a(double var0, double var2, double var4, double var6) {
      double var8 = Double.longBitsToDouble(Double.doubleToRawLongBits(var0) & -134217728L);
      double var10 = var0 - var8;
      double var12 = Double.longBitsToDouble(Double.doubleToRawLongBits(var2) & -134217728L);
      double var14 = var2 - var12;
      double var16 = var0 * var2;
      double var18 = var10 * var14 - (var16 - var8 * var12 - var10 * var12 - var8 * var14);
      double var20 = Double.longBitsToDouble(Double.doubleToRawLongBits(var4) & -134217728L);
      double var22 = var4 - var20;
      double var24 = Double.longBitsToDouble(Double.doubleToRawLongBits(var6) & -134217728L);
      double var26 = var6 - var24;
      double var28 = var4 * var6;
      double var30 = var22 * var26 - (var28 - var20 * var24 - var22 * var24 - var20 * var26);
      double var32;
      double var34 = (var32 = var16 + var28) - var28;
      double var36 = var28 - (var32 - var34) + (var16 - var34);
      double var38;
      if (Double.isNaN(var38 = var32 + (var18 + var30 + var36))) {
         var38 = var0 * var2 + var4 * var6;
      }

      return var38;
   }

   public static double a(double var0, double var2, double var4, double var6, double var8, double var10) {
      double var12 = Double.longBitsToDouble(Double.doubleToRawLongBits(var0) & -134217728L);
      double var14 = var0 - var12;
      double var16 = Double.longBitsToDouble(Double.doubleToRawLongBits(var2) & -134217728L);
      double var18 = var2 - var16;
      double var20 = var0 * var2;
      double var22 = var14 * var18 - (var20 - var12 * var16 - var14 * var16 - var12 * var18);
      double var24 = Double.longBitsToDouble(Double.doubleToRawLongBits(var4) & -134217728L);
      double var26 = var4 - var24;
      double var28 = Double.longBitsToDouble(Double.doubleToRawLongBits(var6) & -134217728L);
      double var30 = var6 - var28;
      double var32 = var4 * var6;
      double var34 = var26 * var30 - (var32 - var24 * var28 - var26 * var28 - var24 * var30);
      double var36 = Double.longBitsToDouble(Double.doubleToRawLongBits(var8) & -134217728L);
      double var38 = var8 - var36;
      double var40 = Double.longBitsToDouble(Double.doubleToRawLongBits(var10) & -134217728L);
      double var42 = var10 - var40;
      double var44 = var8 * var10;
      double var46 = var38 * var42 - (var44 - var36 * var40 - var38 * var40 - var36 * var42);
      double var48;
      double var50 = (var48 = var20 + var32) - var32;
      double var52 = var32 - (var48 - var50) + (var20 - var50);
      double var54;
      double var56 = (var54 = var48 + var44) - var44;
      double var58 = var44 - (var54 - var56) + (var48 - var56);
      double var60;
      if (Double.isNaN(var60 = var54 + (var22 + var34 + var46 + var52 + var58))) {
         var60 = var0 * var2 + var4 * var6 + var8 * var10;
      }

      return var60;
   }

   public static double a(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      double var16 = Double.longBitsToDouble(Double.doubleToRawLongBits(var0) & -134217728L);
      double var18 = var0 - var16;
      double var20 = Double.longBitsToDouble(Double.doubleToRawLongBits(var2) & -134217728L);
      double var22 = var2 - var20;
      double var24 = var0 * var2;
      double var26 = var18 * var22 - (var24 - var16 * var20 - var18 * var20 - var16 * var22);
      double var28 = Double.longBitsToDouble(Double.doubleToRawLongBits(var4) & -134217728L);
      double var30 = var4 - var28;
      double var32 = Double.longBitsToDouble(Double.doubleToRawLongBits(var6) & -134217728L);
      double var34 = var6 - var32;
      double var36 = var4 * var6;
      double var38 = var30 * var34 - (var36 - var28 * var32 - var30 * var32 - var28 * var34);
      double var40 = Double.longBitsToDouble(Double.doubleToRawLongBits(var8) & -134217728L);
      double var42 = var8 - var40;
      double var44 = Double.longBitsToDouble(Double.doubleToRawLongBits(var10) & -134217728L);
      double var46 = var10 - var44;
      double var48 = var8 * var10;
      double var50 = var42 * var46 - (var48 - var40 * var44 - var42 * var44 - var40 * var46);
      double var52 = Double.longBitsToDouble(Double.doubleToRawLongBits(var12) & -134217728L);
      double var54 = var12 - var52;
      double var56 = Double.longBitsToDouble(Double.doubleToRawLongBits(var14) & -134217728L);
      double var58 = var14 - var56;
      double var60 = var12 * var14;
      double var62 = var54 * var58 - (var60 - var52 * var56 - var54 * var56 - var52 * var58);
      double var64;
      double var66 = (var64 = var24 + var36) - var36;
      double var68 = var36 - (var64 - var66) + (var24 - var66);
      double var70;
      double var72 = (var70 = var64 + var48) - var48;
      double var74 = var48 - (var70 - var72) + (var64 - var72);
      double var76;
      double var78 = (var76 = var70 + var60) - var60;
      double var80 = var60 - (var76 - var78) + (var70 - var78);
      double var82;
      if (Double.isNaN(var82 = var76 + (var26 + var38 + var50 + var62 + var68 + var74 + var80))) {
         var82 = var0 * var2 + var4 * var6 + var8 * var10 + var12 * var14;
      }

      return var82;
   }

   private static boolean a(float[] var0, float[] var1) {
      if (var0 != null && var1 != null) {
         if (var0.length != var1.length) {
            return false;
         }

         for (int var2 = 0; var2 < var0.length; var2++) {
            if (!Cq.a(var0[var2], var1[var2])) {
               return false;
            }
         }

         return true;
      } else {
         return !(var0 == null ^ var1 == null);
      }
   }

   private static boolean b(float[] var0, float[] var1) {
      if (var0 != null && var1 != null) {
         if (var0.length != var1.length) {
            return false;
         }

         for (int var2 = 0; var2 < var0.length; var2++) {
            if (!Cq.b(var0[var2], var1[var2])) {
               return false;
            }
         }

         return true;
      } else {
         return !(var0 == null ^ var1 == null);
      }
   }

   public static boolean i(double[] var0, double[] var1) {
      if (var0 != null && var1 != null) {
         if (var0.length != var1.length) {
            return false;
         }

         for (int var2 = 0; var2 < var0.length; var2++) {
            if (!Cq.a(var0[var2], var1[var2])) {
               return false;
            }
         }

         return true;
      } else {
         return !(var0 == null ^ var1 == null);
      }
   }

   public static boolean j(double[] var0, double[] var1) {
      if (var0 != null && var1 != null) {
         if (var0.length != var1.length) {
            return false;
         }

         for (int var2 = 0; var2 < var0.length; var2++) {
            if (!Cq.b(var0[var2], var1[var2])) {
               return false;
            }
         }

         return true;
      } else {
         return !(var0 == null ^ var1 == null);
      }
   }

   public static double[] a(double[] var0, double var1) {
      if (Double.isInfinite(1.0)) {
         throw new oV(px.NORMALIZE_INFINITE);
      }

      if (Double.isNaN(1.0)) {
         throw new oV(px.NORMALIZE_NAN);
      }

      double var3 = 0.0;
      int var6;
      double[] var2 = new double[var6 = var0.length];

      for (int var5 = 0; var5 < var6; var5++) {
         if (Double.isInfinite(var0[var5])) {
            throw new oV(px.INFINITE_ARRAY_ELEMENT, var0[var5], var5);
         }

         if (!Double.isNaN(var0[var5])) {
            var3 += var0[var5];
         }
      }

      if (var3 == 0.0) {
         throw new oU(px.ARRAY_SUMS_TO_ZERO);
      }

      for (int var7 = 0; var7 < var6; var7++) {
         if (Double.isNaN(var0[var7])) {
            var2[var7] = Double.NaN;
         } else {
            var2[var7] = var0[var7] / var3;
         }
      }

      return var2;
   }

   public static <T> T[] a(kM<T> var0, int var1) {
      Object[] var2;
      Arrays.fill(var2 = (Object[])Array.newInstance(var0.c(), var1), var0.a());
      return (T[])var2;
   }

   public static <T> T[][] a(kM<T> var0, int var1, int var2) {
      Object[][] var4;
      if (var2 < 0) {
         var4 = (Object[][])Array.newInstance(a(var0, 0).getClass(), var1);
      } else {
         var4 = (Object[][])Array.newInstance(var0.c(), var1, var2);

         for (int var3 = 0; var3 < var1; var3++) {
            Arrays.fill(var4[var3], var0.a());
         }
      }

      return (T[][])var4;
   }

   private static double[] m(double[] var0, double[] var1) {
      Ci.a(var0);
      Ci.a(var1);
      int var2 = var0.length;
      int var3 = var1.length;
      if (var2 != 0 && var3 != 0) {
         int var4;
         double[] var5 = new double[var4 = var2 + var3 - 1];

         for (int var6 = 0; var6 < var4; var6++) {
            double var7 = 0.0;
            int var9 = BY.b(0, var6 + 1 - var2);
            int var10 = var6 - var9;

            while (var9 < var3 && var10 >= 0) {
               var7 += var0[var10--] * var1[var9++];
            }

            var5[var6] = var7;
         }

         return var5;
      } else {
         throw new pf();
      }
   }

   private static void a(int[] var0, int var1, int var2) {
      a(var0, var1, var2, new zR());
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static void a(int[] var0, int var1, int var2, zF var3) {
      switch (<unrepresentable>.b[var2 - 1]) {
         case 1:
            for (int var7 = var0.length - 1; var7 >= var1; var7--) {
               int var8;
               if (var7 == var1) {
                  var8 = var1;
               } else {
                  var8 = new oM(var3, var1, var7).a();
               }

               int var9 = var0[var8];
               var0[var8] = var0[var7];
               var0[var7] = var9;
            }

            return;
         case 2:
            for (int var6 = 0; var6 <= var1; var6++) {
               int var4;
               if (var6 == var1) {
                  var4 = var1;
               } else {
                  var4 = new oM(var3, var6, var1).a();
               }

               int var5 = var0[var4];
               var0[var4] = var0[var6];
               var0[var6] = var5;
            }

            return;
         default:
            throw new oY();
      }
   }

   public static void a(int[] var0, zF var1) {
      a(var0, 0, Ch.d.TAIL$748fd098, var1);
   }

   private static void b(int[] var0) {
      a(var0, new zR());
   }

   public static int[] a(int var0) {
      return a(var0, 0, 1);
   }

   public static int[] a(int var0, int var1, int var2) {
      int[] var3 = new int[var0];

      for (int var4 = 0; var4 < var0; var4++) {
         var3[var4] = 0 + var4;
      }

      return var3;
   }

   public static boolean b(double[] var0, int var1, int var2) {
      return a(var0, var1, var2, false);
   }

   public static boolean a(double[] var0, int var1, int var2, boolean var3) {
      if (var0 == null) {
         throw new pl(px.INPUT_ARRAY);
      } else if (var1 < 0) {
         throw new pj(px.START_POSITION, var1);
      } else if (var2 < 0) {
         throw new pj(px.LENGTH, var2);
      } else if (var1 + var2 > var0.length) {
         throw new pm(px.SUBARRAY_ENDS_AFTER_ARRAY_END, var1 + var2, var0.length, true);
      } else {
         return var2 != 0 || var3;
      }
   }

   private static boolean a(double[] var0, double[] var1, int var2, int var3) {
      return a(var0, var1, var2, var3, false);
   }

   public static boolean a(double[] var0, double[] var1, int var2, int var3, boolean var4) {
      if (var1 != null && var0 != null) {
         double[] var6 = var0;
         a(var1, var6, true);
         boolean var5 = false;

         for (int var9 = var2; var9 < var2 + var3; var9++) {
            double var7;
            if (Double.isNaN(var7 = var1[var9])) {
               throw new oV(px.NAN_ELEMENT_AT_INDEX, var9);
            }

            if (Double.isInfinite(var7)) {
               throw new oV(px.INFINITE_ARRAY_ELEMENT, var7, var9);
            }

            if (var7 < 0.0) {
               throw new oV(px.NEGATIVE_ELEMENT_AT_INDEX, var9, var7);
            }

            if (!var5 && var7 > 0.0) {
               var5 = true;
            }
         }

         if (!var5) {
            throw new oV(px.WEIGHT_AT_LEAST_ONE_NON_ZERO);
         } else {
            return a(var0, var2, var3, var4);
         }
      } else {
         throw new pl(px.INPUT_ARRAY);
      }
   }

   public static double[] a(double[]... var0) {
      int var1 = 0;
      double[][] var2 = var0;
      int var10000 = var0.length;

      for (int var3 = 0; var3 < 2; var3++) {
         double[] var4 = var2[var3];
         var1 += var4.length;
      }

      int var6 = 0;
      double[] var7 = new double[var1];

      for (int var8 = 0; var8 < var0.length; var8++) {
         var1 = var0[var8].length;
         System.arraycopy(var0[var8], 0, var7, var6, var1);
         var6 += var1;
      }

      return var7;
   }

   public static double[] f(double[] var0) {
      TreeSet var1 = new TreeSet();

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1.add(var0[var2]);
      }

      int var6;
      var0 = new double[var6 = var1.size()];
      Iterator var5 = var1.iterator();

      for (int var3 = 0; var5.hasNext(); var0[var6 - var3] = (Double)var5.next()) {
         var3++;
      }

      return var0;
   }

   public interface a {
      double a(double[] var1);

      double a(double[] var1, int var2, int var3);
   }

   public enum b {
      public static final int INCREASING$37efe540 = 1;
      public static final int DECREASING$37efe540 = 2;
      // $VF: synthetic field
      private static final int[] $VALUES$50371d45 = new int[]{INCREASING$37efe540, DECREASING$37efe540};

      public static int[] values$121350fa() {
         return (int[])$VALUES$50371d45.clone();
      }

      private b(String var1, int var2) {
         super(var1, var2);
      }
   }

   static class c {
      final double a;
      final int b;

      c(double var1, int var3) {
         this.a = var1;
         this.b = var3;
      }

      public final double a() {
         return this.a;
      }

      public final int b() {
         return this.b;
      }
   }

   public enum d {
      public static final int HEAD$748fd098 = 1;
      public static final int TAIL$748fd098 = 2;
      // $VF: synthetic field
      private static final int[] $VALUES$2890f0d3 = new int[]{HEAD$748fd098, TAIL$748fd098};

      public static int[] values$75deba52() {
         return (int[])$VALUES$2890f0d3.clone();
      }

      private d(String var1, int var2) {
         super(var1, var2);
      }
   }
}
