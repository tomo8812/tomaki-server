package org.valkyrienskies.core.impl.shadow;

public abstract class uT extends vx {
   protected double[] f;
   protected sy g;
   private uP m;
   private final int n;
   private double o;
   private double p;
   private double q;
   private double r;

   public uT(String var1, int var2, int var3, double var4, double var6, double var8, double var10) {
      super(var1, var4, var6, var8, var10);
      if (var2 < 2) {
         throw new pn(px.INTEGRATION_METHOD_NEEDS_AT_LEAST_TWO_PREVIOUS_POINTS, var2, 2, true);
      }

      this.m = new vI(var4, var6, var8, var10);
      this.n = var2;
      this.o = -1.0 / var3;
      double var13 = 0.9;
      this.p = 0.9;
      var13 = 0.2;
      this.q = 0.2;
      var13 = BY.a(2.0, -this.o);
      this.r = var13;
   }

   public uT(String var1, int var2, int var3, double var4, double var6, double[] var8, double[] var9) {
      super(var1, var4, var6, var8, var9);
      this.m = new vI(var4, var6, var8, var9);
      this.n = var2;
      this.o = -1.0 / var3;
      double var11 = 0.9;
      this.p = 0.9;
      var11 = 0.2;
      this.q = 0.2;
      var11 = BY.a(2.0, -this.o);
      this.r = var11;
   }

   private uP n() {
      return this.m;
   }

   private void a(uP var1) {
      this.m = var1;
   }

   protected final void b(double var1, double[] var3, double var4) {
      this.m.e();
      this.m.c();
      this.m.a(new uT.b((this.n + 3) / 2, var3.length));

      try {
         if (this.m instanceof uA) {
            ((uA)this.m).a(this.j(), var4);
         } else {
            this.m.a(new uM() {
               @Override
               public final int a() {
                  uT var1x = uT.this;
                  return uT.super.e.b();
               }

               @Override
               public final void a(double var1, double[] var3x, double[] var4x) {
                  uT var5 = uT.this;
                  uT.super.e.a(var1, var3x, var4x);
               }
            }, var1, var3, var4, new double[var3.length]);
         }

         throw new oX(px.MULTISTEP_STARTER_STOPPED_EARLY);
      } catch (uT.a var6) {
         this.k().d(this.m.i());
         this.m.c();
      }
   }

   protected abstract sy a(double var1, double[] var3, double[][] var4, double[][] var5);

   private double o() {
      return this.q;
   }

   private void b(double var1) {
      this.q = 0.2;
   }

   private double p() {
      return this.r;
   }

   private void c(double var1) {
      this.r = var1;
   }

   private double q() {
      return this.p;
   }

   private void d(double var1) {
      this.p = 0.9;
   }

   private int r() {
      return this.n;
   }

   protected final double a(double var1) {
      return BY.d(this.r, BY.e(this.q, this.p * BY.a(var1, this.o)));
   }

   static class a extends RuntimeException {
      private static final long a = -1914085471038046418L;

      a() {
         super((Throwable)null);
      }
   }

   class b implements wA {
      private int a = 0;
      private final double[] b;
      private final double[][] c;
      private final double[][] d;

      b(int var2, int var3) {
         this.b = new double[var2];
         this.c = new double[var2][var3];
         this.d = new double[var2][var3];
      }

      @Override
      public final void a(ws var1, boolean var2) {
         double var3 = var1.g();
         double var5 = var1.h();
         if (this.a == 0) {
            var1.d(var3);
            this.b[0] = var3;
            uT var14 = uT.this;
            uF var9 = uT.super.e;
            uF var15 = uT.super.e;
            uE var16;
            (var16 = uT.super.e.b).b(var1.k(), this.c[this.a]);
            var16.b(var1.l(), this.d[this.a]);
            int var17 = 0;
            uE[] var10;
            int var4 = (var10 = var9.d()).length;

            for (int var7 = 0; var7 < var4; var7++) {
               uE var8;
               (var8 = var10[var7]).b(var1.a(var17), this.c[this.a]);
               var8.b(var1.b(var17), this.d[this.a]);
               var17++;
            }
         }

         this.a++;
         var1.d(var5);
         this.b[this.a] = var5;
         uT var18 = uT.this;
         uF var11 = uT.super.e;
         uF var19 = uT.super.e;
         uE var20;
         (var20 = uT.super.e.b).b(var1.k(), this.c[this.a]);
         var20.b(var1.l(), this.d[this.a]);
         int var21 = 0;
         uE[] var12;
         int var22 = (var12 = var11.d()).length;

         for (int var23 = 0; var23 < var22; var23++) {
            uE var24;
            (var24 = var12[var23]).b(var1.a(var21), this.c[this.a]);
            var24.b(var1.b(var21), this.d[this.a]);
            var21++;
         }

         if (this.a == this.b.length - 1) {
            uT.super.a = this.b[0];
            uT.super.b = (this.b[this.b.length - 1] - this.b[0]) / (this.b.length - 1);
            uT.this.f = (double[])this.d[0].clone();

            for (int var13 = 0; var13 < uT.this.f.length; var13++) {
               uT.this.f[var13] = uT.this.f[var13] * uT.super.b;
            }

            uT.this.g = uT.this.a(uT.super.b, this.b, this.c, this.d);
            throw new uT.a();
         }
      }

      @Override
      public final void a(double var1, double[] var3, double var4) {
      }
   }

   @Deprecated
   public interface c {
      sy a();
   }
}
