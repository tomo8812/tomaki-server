package org.valkyrienskies.core.impl.shadow;

public final class tb {
   private static final double a = 1.0E-11;
   private final double[][] b;
   private final int[] c;
   private boolean d;
   private boolean e;
   private tq f;
   private tq g;
   private tq h;

   public tb(tq var1) {
      this(var1, 1.0E-11);
   }

   public tb(tq var1, double var2) {
      if (!var1.e()) {
         throw new th(var1.f(), var1.g());
      }

      int var15 = var1.g();
      this.b = var1.a();
      this.c = new int[var15];
      this.f = null;
      this.g = null;
      this.h = null;
      int var13 = 0;

      while (var13 < var15) {
         this.c[var13] = var13++;
      }

      this.d = true;
      this.e = false;

      for (int var14 = 0; var14 < var15; var14++) {
         for (int var3 = 0; var3 < var14; var3++) {
            double[] var7;
            double var8 = (var7 = this.b[var3])[var14];

            for (int var10 = 0; var10 < var3; var10++) {
               var8 -= var7[var10] * this.b[var10][var14];
            }

            var7[var14] = var8;
         }

         int var16 = var14;
         double var19 = Double.NEGATIVE_INFINITY;

         for (int var9 = var14; var9 < var15; var9++) {
            double[] var22;
            double var11 = (var22 = this.b[var9])[var14];

            for (int var4 = 0; var4 < var14; var4++) {
               var11 -= var22[var4] * this.b[var4][var14];
            }

            var22[var14] = var11;
            if (BY.w(var11) > var19) {
               var19 = BY.w(var11);
               var16 = var9;
            }
         }

         if (BY.w(this.b[var16][var14]) < 1.0E-11) {
            this.e = true;
            return;
         }

         if (var16 != var14) {
            double[] var23 = this.b[var16];
            double[] var12 = this.b[var14];

            for (int var17 = 0; var17 < var15; var17++) {
               double var20 = var23[var17];
               var23[var17] = var12[var17];
               var12[var17] = var20;
            }

            int var18 = this.c[var16];
            this.c[var16] = this.c[var14];
            this.c[var14] = var18;
            this.d = !this.d;
         }

         double var21 = this.b[var14][var14];

         for (int var24 = var14 + 1; var24 < var15; var24++) {
            this.b[var24][var14] = this.b[var24][var14] / var21;
         }
      }
   }

   private tq b() {
      if (this.f == null && !this.e) {
         int var1 = this.c.length;
         this.f = td.a(var1, var1);

         for (int var2 = 0; var2 < var1; var2++) {
            double[] var3 = this.b[var2];

            for (int var4 = 0; var4 < var2; var4++) {
               this.f.c(var2, var4, var3[var4]);
            }

            this.f.c(var2, var2, 1.0);
         }
      }

      return this.f;
   }

   private tq c() {
      if (this.g == null && !this.e) {
         int var1 = this.c.length;
         this.g = td.a(var1, var1);

         for (int var2 = 0; var2 < var1; var2++) {
            double[] var3 = this.b[var2];

            for (int var4 = var2; var4 < var1; var4++) {
               this.g.c(var2, var4, var3[var4]);
            }
         }
      }

      return this.g;
   }

   private tq d() {
      if (this.h == null && !this.e) {
         int var1 = this.c.length;
         this.h = td.a(var1, var1);

         for (int var2 = 0; var2 < var1; var2++) {
            this.h.c(var2, this.c[var2], 1.0);
         }
      }

      return this.h;
   }

   private int[] e() {
      return (int[])this.c.clone();
   }

   private double f() {
      if (this.e) {
         return 0.0;
      }

      int var1 = this.c.length;
      double var2 = this.d ? 1.0 : -1.0;

      for (int var4 = 0; var4 < var1; var4++) {
         var2 *= this.b[var4][var4];
      }

      return var2;
   }

   public final tb.a a() {
      return new tb.a(this.b, this.c, this.e, (byte)0);
   }

   static class a implements sG {
      private final double[][] a;
      private final int[] b;
      private final boolean c;

      private a(double[][] var1, int[] var2, boolean var3) {
         this.a = var1;
         this.b = var2;
         this.c = var3;
      }

      @Override
      public final boolean a() {
         return !this.c;
      }

      @Override
      public final tu a(tu var1) {
         int var2 = this.b.length;
         if (var1.f() != var2) {
            throw new oS(var1.f(), var2);
         }

         if (this.c) {
            throw new tA();
         }

         double[] var3 = new double[var2];

         for (int var4 = 0; var4 < var2; var4++) {
            var3[var4] = var1.a(this.b[var4]);
         }

         for (int var9 = 0; var9 < var2; var9++) {
            double var5 = var3[var9];

            for (int var7 = var9 + 1; var7 < var2; var7++) {
               var3[var7] -= var5 * this.a[var7][var9];
            }
         }

         for (int var10 = var2 - 1; var10 >= 0; var10--) {
            var3[var10] /= this.a[var10][var10];
            double var11 = var3[var10];

            for (int var8 = 0; var8 < var10; var8++) {
               var3[var8] -= var11 * this.a[var8][var10];
            }
         }

         return new sA(var3, false);
      }

      @Override
      public final tq a(tq var1) {
         int var2 = this.b.length;
         if (var1.f() != var2) {
            throw new oS(var1.f(), var2);
         }

         if (this.c) {
            throw new tA();
         }

         int var3 = var1.g();
         double[][] var4 = new double[var2][var3];

         for (int var5 = 0; var5 < var2; var5++) {
            double[] var6 = var4[var5];
            int var7 = this.b[var5];

            for (int var8 = 0; var8 < var3; var8++) {
               var6[var8] = var1.b(var7, var8);
            }
         }

         for (int var14 = 0; var14 < var2; var14++) {
            double[] var16 = var4[var14];

            for (int var18 = var14 + 1; var18 < var2; var18++) {
               double[] var20 = var4[var18];
               double var9 = this.a[var18][var14];

               for (int var11 = 0; var11 < var3; var11++) {
                  var20[var11] -= var16[var11] * var9;
               }
            }
         }

         for (int var15 = var2 - 1; var15 >= 0; var15--) {
            double[] var17 = var4[var15];
            double var19 = this.a[var15][var15];

            for (int var21 = 0; var21 < var3; var21++) {
               var17[var21] /= var19;
            }

            for (int var22 = 0; var22 < var15; var22++) {
               double[] var10 = var4[var22];
               double var23 = this.a[var22][var15];

               for (int var13 = 0; var13 < var3; var13++) {
                  var10[var13] -= var17[var13] * var23;
               }
            }
         }

         return new sy(var4, false);
      }

      @Override
      public final tq b() {
         return this.a(td.a(this.b.length));
      }

      // $VF: synthetic method
      a(double[][] var1, int[] var2, boolean var3, byte var4) {
         this(var1, var2, var3);
      }
   }
}
