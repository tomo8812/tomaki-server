package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class ou implements Serializable {
   private static final long a = -4670676796862967187L;
   private int b;

   private ou(int var1) {
      if (var1 <= 0) {
         throw new pk(px.NOT_POSITIVE_NUMBER_OF_SAMPLES, var1);
      }

      this.b = var1;
   }

   private double a(double var1) {
      return this.a(var1, false);
   }

   private double b(double var1) {
      return this.a(var1, true);
   }

   private double a(double var1, boolean var3) {
      double var4 = 1.0 / this.b;
      double var6 = var4 * 0.5;
      if (var1 <= var6) {
         return 0.0;
      }

      if (var6 < var1 && var1 <= var4) {
         double var8 = 1.0;
         double var10 = var1 * 2.0 - var4;

         for (int var12 = 1; var12 <= this.b; var12++) {
            var8 *= var12 * var10;
         }

         return var8;
      } else if (1.0 - var4 <= var1 && var1 < 1.0) {
         return 1.0 - 2.0 * BY.a(1.0 - var1, this.b);
      } else if (1.0 <= var1) {
         return 1.0;
      } else {
         return var3 ? this.c(var1) : this.d(var1);
      }
   }

   private double c(double var1) {
      int var3 = (int)BY.z(this.b * var1);
      qd var4 = this.e(var1).a(this.b).b(var3 - 1, var3 - 1);

      for (int var2 = 1; var2 <= this.b; var2++) {
         var4 = var4.c(var2).b(this.b);
      }

      return var4.a(20, 4).doubleValue();
   }

   private double d(double var1) {
      int var3 = (int)BY.z(this.b * var1);
      sx var10;
      int var2 = (var10 = this.e(var1)).f();
      sy var4 = new sy(var2, var2);

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var8 = 0; var8 < var2; var8++) {
            var4.c(var5, var8, ((qd)var10.b(var5, var8)).doubleValue());
         }
      }

      double var12 = var4.a(this.b).b(var3 - 1, var3 - 1);

      for (int var11 = 1; var11 <= this.b; var11++) {
         var12 *= (double)var11 / this.b;
      }

      return var12;
   }

   private sQ<qd> e(double var1) {
      int var3 = (int)BY.z(this.b * var1);
      int var4 = 2 * var3 - 1;
      double var5;
      if ((var5 = var3 - this.b * var1) >= 1.0) {
         throw new pm(var5, 1.0, false);
      }

      try {
         var9 = new qd(var5, 1.0E-20, 10000);
      } catch (qh var8) {
         try {
            var9 = new qd(var5, 1.0E-10, 10000);
         } catch (qh var7) {
            var9 = new qd(var5, 1.0E-5, 10000);
         }
      }

      qd[][] var2 = new qd[var4][var4];

      for (int var11 = 0; var11 < var4; var11++) {
         for (int var14 = 0; var14 < var4; var14++) {
            if (var11 - var14 + 1 < 0) {
               var2[var11][var14] = qd.c;
            } else {
               var2[var11][var14] = qd.b;
            }
         }
      }

      qd[] var12;
      (var12 = new qd[var4])[0] = var9;

      for (int var15 = 1; var15 < var4; var15++) {
         var12[var15] = var9.d(var12[var15 - 1]);
      }

      for (int var16 = 0; var16 < var4; var16++) {
         var2[var16][0] = var2[var16][0].e(var12[var16]);
         var2[var4 - 1][var16] = var2[var4 - 1][var16].e(var12[var4 - var16 - 1]);
      }

      if (var9.b(qd.e) == 1) {
         var2[var4 - 1][0] = var2[var4 - 1][0].a(var9.c(2).e(1).d(var4));
      }

      for (int var17 = 0; var17 < var4; var17++) {
         for (int var10 = 0; var10 < var17 + 1; var10++) {
            if (var17 - var10 + 1 > 0) {
               for (int var13 = 2; var13 <= var17 - var10 + 1; var13++) {
                  var2[var17][var10] = var2[var17][var10].b(var13);
               }
            }
         }
      }

      return new sx<>(qe.a.a(), var2);
   }
}
