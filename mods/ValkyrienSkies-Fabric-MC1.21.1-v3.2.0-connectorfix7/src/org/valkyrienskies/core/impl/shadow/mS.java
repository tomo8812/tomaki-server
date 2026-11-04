package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public final class mS implements mT {
   final double a;
   private final mM c;
   private final boolean d;
   final double b;

   private mS(int var1, int var2, double var3, double var5, double var7, double var9, boolean var11, double var12) {
      this(new mM(var1, var2, var3, var5, var7, new zN(var1)), var9, var11, var12);
   }

   private mS(mM var1, double var2, boolean var4, double var5) {
      if (var2 < 0.0) {
         throw new pj(var2);
      }

      this.c = var1;
      this.a = var2;
      this.d = var4;
      this.b = var5;
   }

   @Override
   public final kW a(final double[][] var1, final double[] var2) {
      if (var1 == null || var2 == null) {
         throw new pl();
      }

      if (var1.length == 0) {
         throw new pf();
      }

      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      if (var1[0] == null) {
         throw new pl();
      }

      final mM var3 = this.c;
      int var4 = this.c.c;
      if (this.c.c != var1[0].length) {
         throw new oS(var1[0].length, var4);
      }

      var3 = this.d ? this.c : this.c.a();
      return new kW() {
         @Override
         public final double a(double[] var1x) {
            mS var29 = mS.this;
            mS var30 = mS.this;
            double var8 = mS.this.b;
            double var6 = mS.this.a;
            double[] var4x = var2;
            double[][] var3x = var1;
            double[] var2x = var1x;
            mM var31 = var3;
            if (var6 < 0.0) {
               throw new pj(var6);
            }

            mM var11 = var31;

            for (int var12 = 0; var12 < var11.d; var12++) {
               var11.b.set(var12, new mM.b(0.0, 0.0));
            }

            int var5 = var3x.length;

            for (int var10 = 0; var10 < var5; var10++) {
               double var13;
               double[] var32;
               if (BY.w(var13 = Ch.d(var32 = Ch.a(var3x[var10], var2x))) < var8) {
                  return var4x[var10];
               }

               double var15 = BY.a(var13, -var6);
               double var10002 = var4x[var10];
               double var21 = var15;
               double var19 = var10002;
               double[] var35 = var32;
               var11 = var31;

               for (int var23 = 0; var23 < var11.d; var23++) {
                  double[] var10000 = var11.a.get(var23).a;
                  double[] var14 = var35;
                  double[] var37 = var10000;
                  double var25;
                  double var27;
                  if ((var25 = Ch.h(var10000, var14) / (Ch.d(var37) * Ch.d(var14))) > 0.0 && (var27 = var25 * var21) > var11.f && var27 > var11.b.get(var23).a) {
                     var11.b.set(var23, new mM.b(var27, var19));
                  }
               }
            }

            var11 = var31;
            int var36 = 0;
            double var38 = 0.0;
            double var39 = 0.0;
            Iterator var40 = var11.b.iterator();

            while (var40.hasNext()) {
               mM.b var24;
               double var41;
               if ((var41 = (var24 = (mM.b)var40.next()).a) != 0.0) {
                  var38 += var41 * var24.b;
                  var39 += var41;
               } else {
                  var36++;
               }
            }

            return (double)var36 / var11.d <= var11.e ? var38 / var39 : var11.g;
         }
      };
   }

   // $VF: synthetic method
   static double a(mS var0) {
      return var0.a;
   }

   // $VF: synthetic method
   static double b(mS var0) {
      return var0.b;
   }
}
