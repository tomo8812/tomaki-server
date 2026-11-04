package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;

public final class uy implements uu {
   private final boolean a;
   private final tW b;

   private uy(boolean var1, tW var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public final double[][] a(uq var1) {
      if (this.a) {
         uq var21 = var1;
         uy var20 = this;
         int var22 = var21.b;
         int var23 = var21.c;
         double[][] var24 = new double[(var22 << 1) + 1][(var23 << 1) + 1];

         for (int var25 = 0; var25 < var22; var25++) {
            int var28 = 2 * var25 + 1;

            for (int var31 = 0; var31 < var23; var31++) {
               int var34 = 2 * var31 + 1;
               double[] var36 = var21.a(var25, var31).c();
               ue var39;
               if ((var39 = var21.a(var25, var31, uq.a.RIGHT$75c8caca, uq.c.CENTER$1c17da1c)) != null) {
                  var24[var28][var34 + 1] = var20.b.a(var36, var39.c());
               }

               if ((var39 = var21.a(var25, var31, uq.a.CENTER$75c8caca, uq.c.DOWN$1c17da1c)) != null) {
                  var24[var28 + 1][var34] = var20.b.a(var36, var39.c());
               }
            }
         }

         for (int var26 = 0; var26 < var22; var26++) {
            int var29 = 2 * var26 + 1;

            for (int var32 = 0; var32 < var23; var32++) {
               int var35 = 2 * var32 + 1;
               ue var37 = var21.a(var26, var32);
               ue var41 = var21.a(var26, var32, uq.a.RIGHT$75c8caca, uq.c.CENTER$1c17da1c);
               ue var42 = var21.a(var26, var32, uq.a.CENTER$75c8caca, uq.c.DOWN$1c17da1c);
               ue var14;
               double var43 = (var14 = var21.a(var26, var32, uq.a.RIGHT$75c8caca, uq.c.DOWN$1c17da1c)) == null ? 0.0 : var20.b.a(var37.c(), var14.c());
               double var44 = var41 != null && var42 != null ? var20.b.a(var41.c(), var42.c()) : 0.0;
               var24[var29 + 1][var35 + 1] = 0.5 * (var43 + var44);
            }
         }

         int var27 = var24.length - 1;
         var24[0] = var24[var27];
         int var30 = var24[0].length - 1;

         for (int var33 = 0; var33 < var27; var33++) {
            var24[var33][0] = var24[var33][var30];
         }

         return var24;
      } else {
         uq var2 = var1;
         uy var19 = this;
         int var3 = var2.b;
         int var4 = var2.c;
         double[][] var5 = new double[var3][var4];
         ud var6 = var2.a;

         for (int var7 = 0; var7 < var3; var7++) {
            for (int var8 = 0; var8 < var4; var8++) {
               ue var9 = var2.a(var7, var8);
               ue var11 = var9;
               Collection var10 = var6.a(var11, (Iterable<ue>)null);
               double[] var38 = var9.c();
               double var13 = 0.0;
               int var15 = 0;

               for (ue var17 : var10) {
                  var15++;
                  var13 += var19.b.a(var38, var17.c());
               }

               var5[var7][var8] = var13 / var15;
            }
         }

         return var5;
      }
   }

   private double[][] b(uq var1) {
      int var2 = var1.b;
      int var3 = var1.c;
      double[][] var4 = new double[(var2 << 1) + 1][(var3 << 1) + 1];

      for (int var5 = 0; var5 < var2; var5++) {
         int var6 = 2 * var5 + 1;

         for (int var7 = 0; var7 < var3; var7++) {
            int var8 = 2 * var7 + 1;
            double[] var9 = var1.a(var5, var7).c();
            ue var10;
            if ((var10 = var1.a(var5, var7, uq.a.RIGHT$75c8caca, uq.c.CENTER$1c17da1c)) != null) {
               var4[var6][var8 + 1] = this.b.a(var9, var10.c());
            }

            if ((var10 = var1.a(var5, var7, uq.a.CENTER$75c8caca, uq.c.DOWN$1c17da1c)) != null) {
               var4[var6 + 1][var8] = this.b.a(var9, var10.c());
            }
         }
      }

      for (int var17 = 0; var17 < var2; var17++) {
         int var19 = 2 * var17 + 1;

         for (int var21 = 0; var21 < var3; var21++) {
            int var23 = 2 * var21 + 1;
            ue var24 = var1.a(var17, var21);
            ue var26 = var1.a(var17, var21, uq.a.RIGHT$75c8caca, uq.c.CENTER$1c17da1c);
            ue var11 = var1.a(var17, var21, uq.a.CENTER$75c8caca, uq.c.DOWN$1c17da1c);
            ue var12;
            double var13 = (var12 = var1.a(var17, var21, uq.a.RIGHT$75c8caca, uq.c.DOWN$1c17da1c)) == null ? 0.0 : this.b.a(var24.c(), var12.c());
            double var15 = var26 != null && var11 != null ? this.b.a(var26.c(), var11.c()) : 0.0;
            var4[var19 + 1][var23 + 1] = 0.5 * (var13 + var15);
         }
      }

      int var18 = var4.length - 1;
      var4[0] = var4[var18];
      int var20 = var4[0].length - 1;

      for (int var22 = 0; var22 < var18; var22++) {
         var4[var22][0] = var4[var22][var20];
      }

      return var4;
   }

   private double[][] c(uq var1) {
      int var2 = var1.b;
      int var3 = var1.c;
      double[][] var4 = new double[var2][var3];
      ud var5 = var1.a;

      for (int var6 = 0; var6 < var2; var6++) {
         for (int var7 = 0; var7 < var3; var7++) {
            ue var8 = var1.a(var6, var7);
            ue var10 = var8;
            Collection var9 = var5.a(var10, (Iterable<ue>)null);
            double[] var14 = var8.c();
            double var11 = 0.0;
            int var16 = 0;

            for (ue var13 : var9) {
               var16++;
               var11 += this.b.a(var14, var13.c());
            }

            var4[var6][var7] = var11 / var16;
         }
      }

      return var4;
   }
}
