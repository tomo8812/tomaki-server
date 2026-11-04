package org.valkyrienskies.core.impl.shadow;

@Deprecated
public class mH implements mI {
   private final boolean a;

   public mH() {
      this(false);
   }

   public mH(boolean var1) {
      this.a = var1;
   }

   public mG b(double[] var1, double[] var2, double[][] var3) {
      if (var1.length != 0 && var2.length != 0 && var3.length != 0) {
         if (var1.length != var3.length) {
            throw new oS(var1.length, var3.length);
         }

         Ch.a(var1);
         Ch.a(var2);
         int var4 = var1.length;
         int var5;
         double[][] var6 = new double[var5 = var2.length][var4];

         for (int var7 = 0; var7 < var4; var7++) {
            if (var3[var7].length != var5) {
               throw new oS(var3[var7].length, var5);
            }

            for (int var8 = 0; var8 < var5; var8++) {
               var6[var8][var7] = var3[var7][var8];
            }
         }

         new mY();
         nl[] var20 = new nl[var5];

         for (int var17 = 0; var17 < var5; var17++) {
            var20[var17] = mY.a(var1, var6[var17]);
         }

         nl[] var18 = new nl[var4];

         for (int var15 = 0; var15 < var4; var15++) {
            var18[var15] = mY.a(var2, var3[var15]);
         }

         var6 = new double[var4][var5];

         for (int var9 = 0; var9 < var5; var9++) {
            lc var10 = var20[var9].a();

            for (int var11 = 0; var11 < var4; var11++) {
               var6[var11][var9] = var10.a(var1[var11]);
            }
         }

         double[][] var23 = new double[var4][var5];

         for (int var24 = 0; var24 < var4; var24++) {
            lc var26 = var18[var24].a();

            for (int var21 = 0; var21 < var5; var21++) {
               var23[var24][var21] = var26.a(var2[var21]);
            }
         }

         double[][] var25 = new double[var4][var5];

         for (int var27 = 0; var27 < var4; var27++) {
            int var22 = a(var27, var4);
            int var19 = a(var27);

            for (int var12 = 0; var12 < var5; var12++) {
               int var13 = a(var12, var5);
               int var14 = a(var12);
               var25[var27][var12] = (var3[var22][var13] - var3[var22][var14] - var3[var19][var13] + var3[var19][var14])
                  / ((var1[var22] - var1[var19]) * (var2[var13] - var2[var14]));
            }
         }

         return new mG(var1, var2, var3, var6, var23, var25, this.a);
      } else {
         throw new pf();
      }
   }

   private static int a(int var0, int var1) {
      return ++var0 < var1 ? var0 : var0 - 1;
   }

   private static int a(int var0) {
      return --var0 >= 0 ? var0 : 0;
   }

   // $VF: synthetic method
   @Override
   public kP a(double[] var1, double[] var2, double[][] var3) {
      return this.b(var1, var2, var3);
   }
}
