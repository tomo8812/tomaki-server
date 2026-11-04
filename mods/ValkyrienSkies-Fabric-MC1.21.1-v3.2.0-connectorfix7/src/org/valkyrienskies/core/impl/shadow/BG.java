package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.lang.reflect.Array;

public class BG implements Serializable {
   private static long a;
   private static final double[] b = new double[]{
      1.0,
      -1.0,
      6.123233995736766E-17,
      0.7071067811865476,
      0.9238795325112867,
      0.9807852804032304,
      0.9951847266721969,
      0.9987954562051724,
      0.9996988186962042,
      0.9999247018391445,
      0.9999811752826011,
      0.9999952938095762,
      0.9999988234517019,
      0.9999997058628822,
      0.9999999264657179,
      0.9999999816164293,
      0.9999999954041073,
      0.9999999988510269,
      0.9999999997127567,
      0.9999999999281892,
      0.9999999999820472,
      0.9999999999955118,
      0.999999999998878,
      0.9999999999997194,
      0.9999999999999298,
      0.9999999999999825,
      0.9999999999999957,
      0.9999999999999989,
      0.9999999999999998,
      0.9999999999999999,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0
   };
   private static final double[] c = new double[]{
      2.4492935982947064E-16,
      -1.2246467991473532E-16,
      -1.0,
      -0.7071067811865475,
      -0.3826834323650898,
      -0.19509032201612825,
      -0.0980171403295606,
      -0.049067674327418015,
      -0.024541228522912288,
      -0.012271538285719925,
      -0.006135884649154475,
      -0.003067956762965976,
      -0.0015339801862847655,
      -7.669903187427045E-4,
      -3.8349518757139556E-4,
      -1.917475973107033E-4,
      -9.587379909597734E-5,
      -4.793689960306688E-5,
      -2.396844980841822E-5,
      -1.1984224905069705E-5,
      -5.9921124526424275E-6,
      -2.996056226334661E-6,
      -1.4980281131690111E-6,
      -7.490140565847157E-7,
      -3.7450702829238413E-7,
      -1.8725351414619535E-7,
      -9.362675707309808E-8,
      -4.681337853654909E-8,
      -2.340668926827455E-8,
      -1.1703344634137277E-8,
      -5.8516723170686385E-9,
      -2.9258361585343192E-9,
      -1.4629180792671596E-9,
      -7.314590396335798E-10,
      -3.657295198167899E-10,
      -1.8286475990839495E-10,
      -9.143237995419748E-11,
      -4.571618997709874E-11,
      -2.285809498854937E-11,
      -1.1429047494274685E-11,
      -5.714523747137342E-12,
      -2.857261873568671E-12,
      -1.4286309367843356E-12,
      -7.143154683921678E-13,
      -3.571577341960839E-13,
      -1.7857886709804195E-13,
      -8.928943354902097E-14,
      -4.4644716774510487E-14,
      -2.2322358387255243E-14,
      -1.1161179193627622E-14,
      -5.580589596813811E-15,
      -2.7902947984069054E-15,
      -1.3951473992034527E-15,
      -6.975736996017264E-16,
      -3.487868498008632E-16,
      -1.743934249004316E-16,
      -8.71967124502158E-17,
      -4.35983562251079E-17,
      -2.179917811255395E-17,
      -1.0899589056276974E-17,
      -5.449794528138487E-18,
      -2.7248972640692436E-18,
      -1.3624486320346218E-18
   };
   private final int d;
   // $VF: synthetic field
   private static boolean e = !BG.class.desiredAssertionStatus();

   public BG(int var1) {
      this.d = var1;
   }

   private static void a(double[] var0, double[] var1) {
      int var2 = var0.length;
      assert var1.length == var2;
      int var3 = var2 >> 1;
      int var4 = 0;

      for (int var5 = 0; var5 < var2; var5++) {
         if (var5 < var4) {
            double var6 = var0[var5];
            var0[var5] = var0[var4];
            var0[var4] = var6;
            var6 = var1[var5];
            var1[var5] = var1[var4];
            var1[var4] = var6;
         }

         int var9;
         for (var9 = var3; var9 <= var4 && var9 > 0; var9 >>= 1) {
            var4 -= var9;
         }

         var4 += var9;
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static void a(double[][] var0, int var1, int var2) {
      double[] var3 = var0[0];
      double[] var8 = var0[1];
      int var4 = var3.length;
      assert var8.length == var4;
      switch (<unrepresentable>.a[var1 - 1]) {
         case 1:
            if (var2 != BK.INVERSE$4b20d4ed) {
               return;
            }

            double var11 = 1.0 / var4;

            for (int var10 = 0; var10 < var4; var10++) {
               var3[var10] *= var11;
               var8[var10] *= var11;
            }

            return;
         case 2:
            double var6 = 1.0 / BY.a((double)var4);

            for (int var9 = 0; var9 < var4; var9++) {
               var3[var9] *= var6;
               var8[var9] *= var6;
            }

            return;
         default:
            throw new oX();
      }
   }

   private static void b(double[][] var0, int var1, int var2) {
      if (var0.length != 2) {
         throw new oS(var0.length, 2);
      }

      double[] var3 = var0[0];
      double[] var4 = var0[1];
      if (var3.length != var4.length) {
         throw new oS(var4.length, var3.length);
      }

      int var5;
      if (!BM.a(var5 = var3.length)) {
         throw new oV(px.NOT_POWER_OF_TWO_CONSIDER_PADDING, var5);
      }

      if (var5 != 1) {
         if (var5 == 2) {
            double var44 = var3[0];
            double var51 = var4[0];
            double var58 = var3[1];
            double var61 = var4[1];
            var3[0] = var44 + var58;
            var4[0] = var51 + var61;
            var3[1] = var44 - var58;
            var4[1] = var51 - var61;
            a(var0, var1, var2);
         } else {
            double[] var7 = var4;
            double[] var6 = var3;
            int var8 = var3.length;
            assert var7.length == var8;
            int var9 = var8 >> 1;
            int var10 = 0;

            for (int var11 = 0; var11 < var8; var11++) {
               if (var11 < var10) {
                  double var39 = var6[var11];
                  var6[var11] = var6[var10];
                  var6[var10] = var39;
                  var39 = var7[var11];
                  var7[var11] = var7[var10];
                  var7[var10] = var39;
               }

               int var73;
               for (var73 = var9; var73 <= var10 && var73 > 0; var73 >>= 1) {
                  var10 -= var73;
               }

               var10 += var73;
            }

            if (var2 == BK.INVERSE$4b20d4ed) {
               for (byte var41 = 0; var41 < var5; var41 += 4) {
                  int var45 = var41 + 1;
                  var8 = var41 + 2;
                  var9 = var41 + 3;
                  double var55 = var3[var41];
                  double var12 = var4[var41];
                  double var14 = var3[var8];
                  double var16 = var4[var8];
                  double var18 = var3[var45];
                  double var20 = var4[var45];
                  double var22 = var3[var9];
                  double var24 = var4[var9];
                  var3[var41] = var55 + var14 + var18 + var22;
                  var4[var41] = var12 + var16 + var20 + var24;
                  var3[var45] = var55 - var18 + (var24 - var16);
                  var4[var45] = var12 - var20 + (var14 - var22);
                  var3[var8] = var55 - var14 + var18 - var22;
                  var4[var8] = var12 - var16 + var20 - var24;
                  var3[var9] = var55 - var18 + (var16 - var24);
                  var4[var9] = var12 - var20 + (var22 - var14);
               }
            } else {
               for (byte var42 = 0; var42 < var5; var42 += 4) {
                  int var46 = var42 + 1;
                  var8 = var42 + 2;
                  var9 = var42 + 3;
                  double var56 = var3[var42];
                  double var59 = var4[var42];
                  double var62 = var3[var8];
                  double var64 = var4[var8];
                  double var66 = var3[var46];
                  double var68 = var4[var46];
                  double var70 = var3[var9];
                  double var71 = var4[var9];
                  var3[var42] = var56 + var62 + var66 + var70;
                  var4[var42] = var59 + var64 + var68 + var71;
                  var3[var46] = var56 - var66 + (var64 - var71);
                  var4[var46] = var59 - var68 + (var70 - var62);
                  var3[var8] = var56 - var62 + var66 - var70;
                  var4[var8] = var59 - var64 + var68 - var71;
                  var3[var9] = var56 - var66 + (var71 - var64);
                  var4[var9] = var59 - var68 + (var62 - var70);
               }
            }

            int var43 = 4;
            int var47 = 2;

            while (var43 < var5) {
               var8 = var43 << 1;
               var9 = var47 + 1;
               double var57 = b[var9];
               double var60 = c[var9];
               if (var2 == BK.INVERSE$4b20d4ed) {
                  var60 = -var60;
               }

               for (int var63 = 0; var63 < var5; var63 += var8) {
                  int var15 = var63 + var43;
                  double var65 = 1.0;
                  double var67 = 0.0;

                  for (int var69 = 0; var69 < var43; var69++) {
                     double var21 = var3[var63 + var69];
                     double var23 = var4[var63 + var69];
                     double var25 = var3[var15 + var69];
                     double var27 = var4[var15 + var69];
                     var3[var63 + var69] = var21 + var65 * var25 - var67 * var27;
                     var4[var63 + var69] = var23 + var65 * var27 + var67 * var25;
                     var3[var15 + var69] = var21 - (var65 * var25 - var67 * var27);
                     var4[var15 + var69] = var23 - (var65 * var27 + var67 * var25);
                     double var29 = var65 * var57 - var67 * var60;
                     double var31 = var65 * var60 + var67 * var57;
                     var65 = var29;
                     var67 = var31;
                  }
               }

               var43 = var8;
               var47 = var9;
            }

            a(var0, var1, var2);
         }
      }
   }

   public final nQ[] a(double[] var1, int var2) {
      double[][] var3;
      b(var3 = new double[][]{Ch.a(var1, var1.length), new double[var1.length]}, this.d, var2);
      return BL.a(var3);
   }

   private nQ[] a(lc var1, double var2, double var4, int var6, int var7) {
      double[] var8 = kV.a(var1, var2, var4, var6);
      return this.a(var8, var7);
   }

   private nQ[] a(nQ[] var1, int var2) {
      double[][] var3;
      b(var3 = BL.a(var1), this.d, var2);
      return BL.a(var3);
   }

   @Deprecated
   private Object a(Object var1, int var2) {
      BG.a var5;
      int[] var3 = (var5 = (BG.a)new BG.a(var1).clone()).a();

      for (int var4 = 0; var4 < var3.length; var4++) {
         this.a(var5, var2, var4, new int[0]);
      }

      return var5.b();
   }

   @Deprecated
   private void a(BG.a var1, int var2, int var3, int[] var4) {
      int[] var5 = var1.a();
      if (var4.length == var5.length) {
         nQ[] var8 = new nQ[var5[var3]];

         for (int var10 = 0; var10 < var5[var3]; var10++) {
            var4[var3] = var10;
            var8[var10] = var1.a(var4);
         }

         var8 = this.a(var8, var2);

         for (int var11 = 0; var11 < var5[var3]; var11++) {
            var4[var3] = var11;
            var1.a(var8[var11], var4);
         }
      } else {
         int[] var6 = new int[var4.length + 1];
         System.arraycopy(var4, 0, var6, 0, var4.length);
         if (var4.length == var3) {
            var6[var3] = 0;
            this.a(var1, var2, var3, var6);
         } else {
            for (int var7 = 0; var7 < var5[var4.length]; var7++) {
               var6[var4.length] = var7;
               this.a(var1, var2, var3, var6);
            }
         }
      }
   }

   @Deprecated
   static class a implements Cloneable {
      private int[] a;
      private Object b;

      a(Object var1) {
         this.b = var1;
         int var2 = 0;
         Object[] var3 = (Object[])var1;

         while (var3 instanceof Object[]) {
            var3 = var3;
            var2++;
            var3 = (Object[])var3[0];
         }

         this.a = new int[var2];
         var2 = 0;
         var3 = (Object[])var1;

         while (var3 instanceof Object[]) {
            var3 = var3;
            this.a[var2++] = var3.length;
            var3 = (Object[])var3[0];
         }
      }

      public final nQ a(int... var1) {
         if (var1 == null) {
            if (this.a.length > 0) {
               throw new oS(0, this.a.length);
            } else {
               return null;
            }
         } else {
            if (var1.length != this.a.length) {
               throw new oS(var1.length, this.a.length);
            }

            Object var2 = this.b;

            for (int var3 = 0; var3 < this.a.length; var3++) {
               var2 = ((Object[])var2)[var1[var3]];
            }

            return (nQ)var2;
         }
      }

      public final nQ a(nQ var1, int... var2) {
         if (var2 == null) {
            if (this.a.length > 0) {
               throw new oS(0, this.a.length);
            } else {
               return null;
            }
         } else {
            if (var2.length != this.a.length) {
               throw new oS(var2.length, this.a.length);
            }

            Object[] var3 = (Object[])this.b;

            for (int var4 = 0; var4 < this.a.length - 1; var4++) {
               var3 = (Object[])var3[var2[var4]];
            }

            nQ var5 = (nQ)var3[var2[this.a.length - 1]];
            var3[var2[this.a.length - 1]] = var1;
            return var5;
         }
      }

      public final int[] a() {
         return (int[])this.a.clone();
      }

      public final Object b() {
         return this.b;
      }

      @Override
      public final Object clone() {
         BG.a var1 = new BG.a(Array.newInstance(nQ.class, this.a));
         BG.a var3 = var1;
         BG.a var2 = this;
         int[] var4 = new int[this.a.length];
         int var5 = 1;

         for (int var6 = 0; var6 < var2.a.length; var6++) {
            var5 *= var2.a[var6];
         }

         int[][] var10;
         int[][] var12;
         int var7 = (var10 = var12 = new int[var5][var2.a.length]).length;

         for (int var8 = 0; var8 < var7; var8++) {
            int[] var9 = var10[var8];
            System.arraycopy(var4, 0, var9, 0, var2.a.length);

            for (int var15 = 0; var15 < var2.a.length; var15++) {
               var4[var15]++;
               if (var4[var15] < var2.a[var15]) {
                  break;
               }

               var4[var15] = 0;
            }
         }

         int[][] var11 = var12;
         var7 = var12.length;

         for (int var14 = 0; var14 < var7; var14++) {
            int[] var16 = var11[var14];
            var3.a(var2.a(var16), var16);
         }

         return var1;
      }

      private void a(BG.a var1) {
         int[] var2 = new int[this.a.length];
         int var3 = 1;

         for (int var4 = 0; var4 < this.a.length; var4++) {
            var3 *= this.a[var4];
         }

         int[][] var8;
         int[][] var10;
         int var5 = (var8 = var10 = new int[var3][this.a.length]).length;

         for (int var6 = 0; var6 < var5; var6++) {
            int[] var7 = var8[var6];
            System.arraycopy(var2, 0, var7, 0, this.a.length);

            for (int var13 = 0; var13 < this.a.length; var13++) {
               var2[var13]++;
               if (var2[var13] < this.a[var13]) {
                  break;
               }

               var2[var13] = 0;
            }
         }

         int[][] var9 = var10;
         var5 = var10.length;

         for (int var12 = 0; var12 < var5; var12++) {
            int[] var14 = var9[var12];
            var1.a(this.a(var14), var14);
         }
      }
   }
}
