package org.valkyrienskies.core.impl.shadow;

public final class sE {
   private static double a;
   private static double b;
   private double[][] c;
   private tq d;
   private tq e;

   public sE(tq var1) {
      this(var1, 1.0E-15, 1.0E-10);
   }

   public sE(tq var1, double var2, double var4) {
      if (!var1.e()) {
         throw new th(var1.f(), var1.g());
      }

      int var6 = var1.f();
      this.c = var1.a();
      this.d = null;
      this.e = null;

      for (int var17 = 0; var17 < var6; var17++) {
         double[] var7 = this.c[var17];

         for (int var9 = var17 + 1; var9 < var6; var9++) {
            double[] var10 = this.c[var9];
            double var11 = var7[var9];
            double var13 = var10[var17];
            double var15 = var2 * BY.e(BY.w(var11), BY.w(var13));
            if (BY.w(var11 - var13) > var15) {
               throw new tj(var17, var9, var2);
            }

            var10[var17] = 0.0;
         }
      }

      for (int var18 = 0; var18 < var6; var18++) {
         double[] var19;
         if ((var19 = this.c[var18])[var18] <= var4) {
            throw new te(var19[var18], var18, var4);
         }

         var19[var18] = BY.a(var19[var18]);
         double var20 = 1.0 / var19[var18];

         for (int var21 = var6 - 1; var21 > var18; var21--) {
            var19[var21] *= var20;
            double[] var12 = this.c[var21];

            for (int var22 = var21; var22 < var6; var22++) {
               var12[var22] -= var19[var21] * var19[var22];
            }
         }
      }
   }

   private tq b() {
      if (this.d == null) {
         sE var1 = this;
         if (this.e == null) {
            var1.e = td.a(var1.c);
         }

         this.d = var1.e.d();
      }

      return this.d;
   }

   private tq c() {
      if (this.e == null) {
         this.e = td.a(this.c);
      }

      return this.e;
   }

   private double d() {
      double var1 = 1.0;

      for (int var3 = 0; var3 < this.c.length; var3++) {
         double var4 = this.c[var3][var3];
         var1 *= var4 * var4;
      }

      return var1;
   }

   public final sE.a a() {
      return new sE.a(this.c, (byte)0);
   }

   static class a implements sG {
      private final double[][] a;

      private a(double[][] var1) {
         this.a = var1;
      }

      @Override
      public final boolean a() {
         return true;
      }

      @Override
      public final tu a(tu var1) {
         int var2 = this.a.length;
         if (var1.f() != var2) {
            throw new oS(var1.f(), var2);
         }

         double[] var8 = var1.g();

         for (int var3 = 0; var3 < var2; var3++) {
            double[] var5 = this.a[var3];
            var8[var3] /= var5[var3];
            double var6 = var8[var3];

            for (int var4 = var3 + 1; var4 < var2; var4++) {
               var8[var4] -= var6 * var5[var4];
            }
         }

         for (int var9 = var2 - 1; var9 >= 0; var9--) {
            var8[var9] /= this.a[var9][var9];
            double var10 = var8[var9];

            for (int var7 = 0; var7 < var9; var7++) {
               var8[var7] -= var10 * this.a[var7][var9];
            }
         }

         return new sA(var8, false);
      }

      @Override
      public final tq a(tq var1) {
         int var2 = this.a.length;
         if (var1.f() != var2) {
            throw new oS(var1.f(), var2);
         }

         int var3 = var1.g();
         double[][] var14 = var1.a();

         for (int var4 = 0; var4 < var2; var4++) {
            double[] var6;
            double var7 = (var6 = this.a[var4])[var4];
            double[] var5 = var14[var4];

            for (int var9 = 0; var9 < var3; var9++) {
               var5[var9] /= var7;
            }

            for (int var20 = var4 + 1; var20 < var2; var20++) {
               double[] var11 = var14[var20];
               double var12 = var6[var20];

               for (int var19 = 0; var19 < var3; var19++) {
                  var11[var19] -= var5[var19] * var12;
               }
            }
         }

         for (int var15 = var2 - 1; var15 >= 0; var15--) {
            double var18 = this.a[var15][var15];
            double[] var8 = var14[var15];

            for (int var16 = 0; var16 < var3; var16++) {
               var8[var16] /= var18;
            }

            for (int var17 = 0; var17 < var15; var17++) {
               double[] var21 = var14[var17];
               double var22 = this.a[var17][var15];

               for (int var13 = 0; var13 < var3; var13++) {
                  var21[var13] -= var8[var13] * var22;
               }
            }
         }

         return new sy(var14);
      }

      @Override
      public final tq b() {
         return this.a(td.a(this.a.length));
      }

      // $VF: synthetic method
      a(double[][] var1, byte var2) {
         this(var1);
      }
   }
}
