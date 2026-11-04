package org.valkyrienskies.core.impl.shadow;

public final class ny extends nq implements nx<lc> {
   private static final double d = 1.0E-6;
   private static final int e = 5;
   private static final int f = 2;
   private static final double g = 0.0625;
   private final int h;
   private int i;

   public ny() {
      this(1.0E-6, 5);
   }

   public ny(double var1, int var3) {
      super(var1);
      this.h = 5;
      this.i = nr.ANY_SIDE$37d0779;
   }

   private ny(double var1, double var3, int var5) {
      super(var1, var3);
      if (var5 < 2) {
         throw new pn(var5, 2, true);
      }

      this.h = var5;
      this.i = nr.ANY_SIDE$37d0779;
   }

   private ny(double var1, double var3, double var5, int var7) {
      super(var1, var3, var5);
      if (var7 < 2) {
         throw new pn(var7, 2, true);
      }

      this.h = var7;
      this.i = nr.ANY_SIDE$37d0779;
   }

   private int a() {
      return this.h;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final double j() {
      double[] var1 = new double[this.h + 1];
      double[] var2 = new double[this.h + 1];
      var1[0] = this.d();
      var1[1] = this.f();
      var1[2] = this.e();
      double var10000 = var1[0];
      double var10001 = var1[1];
      double var43 = var1[2];
      double var41 = var10001;
      nP.b(var10000, var41, var43);
      var2[1] = this.b(var1[1]);
      if (Cq.b(var2[1], 0.0, 1)) {
         return var1[1];
      }

      var2[0] = this.b(var1[0]);
      if (Cq.b(var2[0], 0.0, 1)) {
         return var1[0];
      }

      int var3;
      int var4;
      if (var2[0] * var2[1] < 0.0) {
         var3 = 2;
         var4 = 1;
      } else {
         var2[2] = this.b(var1[2]);
         if (Cq.b(var2[2], 0.0, 1)) {
            return var1[2];
         }

         if (!(var2[1] * var2[2] < 0.0)) {
            throw new pe(var1[0], var1[2], var2[0], var2[2]);
         }

         var3 = 3;
         var4 = 2;
      }

      double[] var5 = new double[var1.length];
      double var6 = var1[var4 - 1];
      double var8;
      double var10 = BY.w(var8 = var2[var4 - 1]);
      int var12 = 0;
      double var13 = var1[var4];
      double var15;
      double var17 = BY.w(var15 = var2[var4]);
      int var19 = 0;

      while (true) {
         double var20 = this.g() + this.h() * BY.e(BY.w(var6), BY.w(var13));
         if (var13 - var6 <= var20 || BY.e(var10, var17) < this.i()) {
            switch (<unrepresentable>.a[this.i - 1]) {
               case 1:
                  if (var10 < var17) {
                     return var6;
                  }

                  return var13;
               case 2:
                  return var6;
               case 3:
                  return var13;
               case 4:
                  if (var8 <= 0.0) {
                     return var6;
                  }

                  return var13;
               case 5:
                  if (var8 < 0.0) {
                     return var13;
                  }

                  return var6;
               default:
                  throw new oY();
            }
         }

         double var22;
         if (var12 >= 2) {
            int var24 = var12 - 2;
            double var25 = (1 << var24) - 1;
            double var27 = var24 + 1;
            var22 = (var25 * var8 - var27 * 0.0625 * var15) / (var25 + var27);
         } else if (var19 >= 2) {
            int var46;
            double var49 = (var46 = var19 - 2) + 1;
            double var51;
            var22 = ((var51 = (1 << var46) - 1) * var15 - var49 * 0.0625 * var8) / (var49 + var51);
         } else {
            var22 = 0.0;
         }

         int var26 = 0;
         int var52 = var3;

         double var48;
         do {
            System.arraycopy(var1, var26, var5, var26, var52 - var26);
            int var50 = var52;
            int var47 = var26;
            double[] var21 = var2;
            double[] var45 = var5;
            double var30 = var22;

            for (int var36 = var47; var36 < var50 - 1; var36++) {
               int var37 = var36 + 1 - var47;

               for (int var28 = var50 - 1; var28 > var36; var28--) {
                  var45[var28] = (var45[var28] - var45[var28 - 1]) / (var21[var28] - var21[var28 - var37]);
               }
            }

            double var55 = 0.0;

            for (int var53 = var50 - 1; var53 >= var47; var53--) {
               var55 = var45[var53] + var55 * (var30 - var21[var53]);
            }

            var48 = var55;
            if (!(var55 > var6) || !(var48 < var13)) {
               if (var4 - var26 >= var52 - var4) {
                  var26++;
               } else {
                  var52--;
               }

               var48 = Double.NaN;
            }
         } while (Double.isNaN(var48) && var52 - var26 > 1);

         if (Double.isNaN(var48)) {
            var48 = var6 + 0.5 * (var13 - var6);
            var26 = var4 - 1;
            var52 = var4;
         }

         double var54;
         if (Cq.b(var54 = this.b(var48), 0.0, 1)) {
            return var48;
         }

         if (var3 > 2 && var52 - var26 != var3) {
            var3 = var52 - var26;
            System.arraycopy(var1, var26, var1, 0, var3);
            System.arraycopy(var2, var26, var2, 0, var3);
            var4 -= var26;
         } else if (var3 == var1.length) {
            var3--;
            if (var4 >= (var1.length + 1) / 2) {
               System.arraycopy(var1, 1, var1, 0, var3);
               System.arraycopy(var2, 1, var2, 0, var3);
               var4--;
            }
         }

         System.arraycopy(var1, var4, var1, var4 + 1, var3 - var4);
         var1[var4] = var48;
         System.arraycopy(var2, var4, var2, var4 + 1, var3 - var4);
         var2[var4] = var54;
         var3++;
         if (var54 * var8 <= 0.0) {
            var13 = var48;
            var15 = var54;
            var17 = BY.w(var54);
            var12++;
            var19 = 0;
         } else {
            var6 = var48;
            var8 = var54;
            var10 = BY.w(var54);
            var12 = 0;
            var19++;
            var4++;
         }
      }
   }

   private static double a(double var0, double[] var2, double[] var3, int var4, int var5) {
      for (int var6 = var4; var6 < var5 - 1; var6++) {
         int var7 = var6 + 1 - var4;

         for (int var8 = var5 - 1; var8 > var6; var8--) {
            var2[var8] = (var2[var8] - var2[var8 - 1]) / (var3[var8] - var3[var8 - var7]);
         }
      }

      double var9 = 0.0;

      for (int var10 = var5 - 1; var10 >= var4; var10--) {
         var9 = var2[var10] + var9 * (var0 - var3[var10]);
      }

      return var9;
   }

   @Override
   public final double a(int var1, <unrepresentable> var2, double var3, double var5, int var7) {
      this.i = var7;
      return super.a(var1, var2, var3, var5);
   }

   @Override
   public final double a(int var1, lc var2, double var3, double var5, double var7, int var9) {
      this.i = var9;
      return super.b(var1, var2, var3, var5, var7);
   }
}
