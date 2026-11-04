package org.valkyrienskies.core.impl.shadow;

public final class xs extends xj {
   private final int b;
   private final xt c;
   private final xl d;

   private xs(int var1, wK<wQ> var2) {
      this(var1, var2, 1.0E-8, 1.0E-8, 1.0E-8, new xs.c());
   }

   @Deprecated
   private xs(int var1, wK<wQ> var2, nO var3) {
      this(var1, var2, var3, new xs.c());
   }

   private xs(int var1, wK<wQ> var2, double var3, double var5, double var7) {
      this(var1, var2, var3, var5, var7, new xs.c());
   }

   @Deprecated
   private xs(int var1, wK<wQ> var2, nO var3, xt var4) {
      this(var1, var2, var3.h(), var3.g(), var3.g(), var4);
   }

   private xs(int var1, wK<wQ> var2, double var3, double var5, double var7, xt var9) {
      super(var2);
      this.b = var1;
      this.c = var9;
      this.d = new xl(this, var3, var5, var7);
   }

   @Override
   public final wQ c(wO... var1) {
      return super.c(var1);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private wQ b() {
      wK var1 = this.j();
      double[] var2 = this.e();
      xi var3 = this.n();
      int var4 = var2.length;
      double[] var5 = this.a(var2);
      if (var3 == xi.MINIMIZE) {
         for (int var6 = 0; var6 < var4; var6++) {
            var5[var6] = -var5[var6];
         }
      }

      double[] var25;
      double[] var7 = (double[])(var25 = this.c.a(var5)).clone();
      double var8 = 0.0;

      for (int var10 = 0; var10 < var4; var10++) {
         var8 += var5[var10] * var7[var10];
      }

      wQ var26 = null;

      while (true) {
         this.l();
         double var11 = this.b(var2);
         wQ var23 = var26;
         var26 = new wQ(var2, var11);
         if (var23 != null && var1.a(this.i(), var23, var26)) {
            return var26;
         }

         double var14 = this.d.a(var2, var7).a;

         for (int var16 = 0; var16 < var2.length; var16++) {
            var2[var16] += var14 * var7[var16];
         }

         var5 = this.a(var2);
         if (var3 == xi.MINIMIZE) {
            for (int var28 = 0; var28 < var4; var28++) {
               var5[var28] = -var5[var28];
            }
         }

         double var29 = var8;
         double[] var27 = this.c.a(var5);
         var8 = 0.0;

         for (int var19 = 0; var19 < var4; var19++) {
            var8 += var5[var19] * var27[var19];
         }

         double var30;
         switch (<unrepresentable>.a[this.b - 1]) {
            case 1:
               var30 = var8 / var29;
               break;
            case 2:
               double var21 = 0.0;

               for (int var12 = 0; var12 < var5.length; var12++) {
                  var21 += var5[var12] * var25[var12];
               }

               var30 = (var8 - var21) / var29;
               break;
            default:
               throw new oY();
         }

         var25 = var27;
         if (this.i() % var4 != 0 && !(var30 < 0.0)) {
            for (int var31 = 0; var31 < var4; var31++) {
               var7[var31] = var25[var31] + var30 * var7[var31];
            }
         } else {
            var7 = (double[])var25.clone();
         }
      }
   }

   @Override
   public final void b(wO... var1) {
      super.b(var1);
      xs var2 = this;
      if (this.f() != null || var2.g() != null) {
         throw new pb(px.CONSTRAINT);
      }
   }

   private void d() {
      if (this.f() != null || this.g() != null) {
         throw new pb(px.CONSTRAINT);
      }
   }

   // $VF: synthetic method
   @Override
   public final Object a(wO[] var1) {
      wO[] var2 = var1;
      return super.c(var2);
   }

   // $VF: synthetic method
   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object c() {
      xs var1 = this;
      wK var2 = this.j();
      double[] var3 = var1.e();
      xi var4 = var1.n();
      int var5 = var3.length;
      double[] var6 = var1.a(var3);
      if (var4 == xi.MINIMIZE) {
         for (int var7 = 0; var7 < var5; var7++) {
            var6[var7] = -var6[var7];
         }
      }

      double[] var26;
      double[] var8 = (double[])(var26 = var1.c.a(var6)).clone();
      double var9 = 0.0;

      for (int var11 = 0; var11 < var5; var11++) {
         var9 += var6[var11] * var8[var11];
      }

      wQ var27 = null;

      while (true) {
         var1.l();
         double var12 = var1.b(var3);
         wQ var24 = var27;
         var27 = new wQ(var3, var12);
         if (var24 != null && var2.a(var1.i(), var24, var27)) {
            return var27;
         }

         double var15 = var1.d.a(var3, var8).a;

         for (int var17 = 0; var17 < var3.length; var17++) {
            var3[var17] += var15 * var8[var17];
         }

         var6 = var1.a(var3);
         if (var4 == xi.MINIMIZE) {
            for (int var29 = 0; var29 < var5; var29++) {
               var6[var29] = -var6[var29];
            }
         }

         double var30 = var9;
         double[] var28 = var1.c.a(var6);
         var9 = 0.0;

         for (int var20 = 0; var20 < var5; var20++) {
            var9 += var6[var20] * var28[var20];
         }

         double var31;
         switch (<unrepresentable>.a[var1.b - 1]) {
            case 1:
               var31 = var9 / var30;
               break;
            case 2:
               double var22 = 0.0;

               for (int var13 = 0; var13 < var6.length; var13++) {
                  var22 += var6[var13] * var26[var13];
               }

               var31 = (var9 - var22) / var30;
               break;
            default:
               throw new oY();
         }

         var26 = var28;
         if (var1.i() % var5 != 0 && !(var31 < 0.0)) {
            for (int var32 = 0; var32 < var5; var32++) {
               var8[var32] = var26[var32] + var31 * var8[var32];
            }
         } else {
            var8 = (double[])var26.clone();
         }
      }
   }

   @Deprecated
   public static class a implements wO {
      private final double a;

      private a(double var1) {
         this.a = var1;
      }

      private double a() {
         return this.a;
      }
   }

   public enum b {
      public static final int FLETCHER_REEVES$65df34e0 = 1;
      public static final int POLAK_RIBIERE$65df34e0 = 2;
      // $VF: synthetic field
      private static final int[] $VALUES$7075a8c5 = new int[]{FLETCHER_REEVES$65df34e0, POLAK_RIBIERE$65df34e0};

      public static int[] values$41b1d4e6() {
         return (int[])$VALUES$7075a8c5.clone();
      }

      private b(String var1, int var2) {
         super(var1, var2);
      }
   }

   public static class c implements xt {
      @Override
      public final double[] a(double[] var1) {
         return (double[])var1.clone();
      }
   }
}
