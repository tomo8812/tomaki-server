package org.valkyrienskies.core.impl.shadow;

public final class vi {
   public final vh a;
   private final double k;
   public final double b;
   private final int l;
   public uF c;
   public double d;
   public double e;
   public boolean f;
   public boolean g;
   public double h;
   private double m;
   private boolean n;
   private boolean o;
   public int i;
   public final nO j;

   public vi(vh var1, double var2, double var4, int var6, nO var7) {
      this.a = var1;
      this.k = var2;
      this.b = BY.w(var4);
      this.l = var6;
      this.j = var7;
      this.c = null;
      this.d = Double.NaN;
      this.e = Double.NaN;
      this.f = true;
      this.g = false;
      this.h = Double.NaN;
      this.m = Double.NaN;
      this.o = true;
      this.i = vh.a.CONTINUE$534a8113;
   }

   public final vh a() {
      return this.a;
   }

   public final void a(uF var1) {
      this.c = var1;
   }

   private double d() {
      return this.k;
   }

   private double e() {
      return this.b;
   }

   private int f() {
      return this.l;
   }

   public final void a(ws var1) {
      this.d = var1.g();
      var1.d(this.d);
      this.e = this.a.a(this.d, this.a((wB)var1));
      if (this.e == 0.0) {
         double var2 = BY.e(this.j.g(), BY.w(this.j.h() * this.d));
         double var4 = this.d + var2 * 0.5;
         var1.d(var4);
         this.e = this.a.a(var4, this.a((wB)var1));
      }

      this.f = this.e >= 0.0;
   }

   public double[] a(wB var1) {
      double[] var2 = new double[this.c.b()];
      uF var3 = this.c;
      this.c.b.b(var1.k(), var2);
      int var8 = 0;
      uE[] var4;
      int var5 = (var4 = this.c.d()).length;

      for (int var6 = 0; var6 < var5; var6++) {
         uE var7;
         (var7 = var4[var6]).b(var1.a(var8++), var2);
      }

      return var2;
   }

   public final boolean b(final ws var1) {
      try {
         this.n = var1.j();
         double var2;
         double var4;
         if (BY.w(var4 = (var2 = var1.h()) - this.d) < this.b) {
            return false;
         }

         int var6 = BY.b(1, (int)BY.z(BY.w(var4) / this.k));
         double var7 = var4 / var6;
         lc var24 = new lc() {
            @Override
            public final double a(double var1x) {
               try {
                  var1.d(var1x);
                  vi var3 = vi.this;
                  wB var5 = var1;
                  var3 = vi.this;
                  return vi.this.a.a(var1x, vi.this.a(var5));
               } catch (pc var4x) {
                  throw new vi.a(var4x);
               }
            }
         };
         double var10 = this.d;
         double var12 = this.e;

         for (int var5 = 0; var5 < var6; var5++) {
            double var15 = var5 == var6 - 1 ? var2 : this.d + (var5 + 1) * var7;
            var1.d(var15);
            double var17 = this.a.a(var15, this.a((wB)var1));
            if (this.f ^ var17 >= 0.0) {
               this.o = var17 >= var12;
               double var19;
               if (this.j instanceof nx) {
                  nx var21 = (nx)this.j;
                  var19 = this.n ? var21.a(this.l, var24, var10, var15, nr.RIGHT_SIDE$37d0779) : var21.a(this.l, var24, var15, var10, nr.LEFT_SIDE$37d0779);
               } else {
                  double var26 = this.n ? this.j.a(this.l, var24, var10, var15) : this.j.a(this.l, var24, var15, var10);
                  int var9 = this.l - this.j.c();
                  nI var25 = new nI(this.j.h(), this.j.g());
                  var19 = this.n
                     ? nP.a(var9, var24, var25, var26, var10, var15, nr.RIGHT_SIDE$37d0779)
                     : nP.a(var9, var24, var25, var26, var15, var10, nr.LEFT_SIDE$37d0779);
               }

               if (!Double.isNaN(this.m) && BY.w(var19 - var10) <= this.b && BY.w(var19 - this.m) <= this.b) {
                  do {
                     var10 = this.n ? var10 + this.b : var10 - this.b;
                     var12 = var24.a(var10);
                  } while (this.f ^ var12 >= 0.0 && this.n ^ var10 >= var15);

                  if (!(this.n ^ var10 >= var15)) {
                     this.h = var19;
                     this.g = true;
                     return true;
                  }

                  var5--;
               } else {
                  if (Double.isNaN(this.m) || BY.w(this.m - var19) > this.b) {
                     this.h = var19;
                     this.g = true;
                     return true;
                  }

                  var10 = var15;
                  var12 = var17;
               }
            } else {
               var10 = var15;
               var12 = var17;
            }
         }

         this.g = false;
         this.h = Double.NaN;
         return false;
      } catch (vi.a var23) {
         throw var23.a;
      }
   }

   public final double b() {
      if (this.g) {
         return this.h;
      } else {
         return this.n ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
      }
   }

   public final void a(double var1, double[] var3) {
      this.d = var1;
      this.e = this.a.a(var1, var3);
      if (this.g && BY.w(this.h - var1) <= this.b) {
         this.m = var1;
         this.f = this.o;
         this.i = this.a.b(var1, var3);
      } else {
         this.f = this.e >= 0.0;
         this.i = vh.a.CONTINUE$534a8113;
      }
   }

   public final boolean c() {
      return this.i == vh.a.STOP$534a8113;
   }

   public final boolean b(double var1, double[] var3) {
      if (this.g && BY.w(this.h - var1) <= this.b) {
         if (this.i == vh.a.RESET_STATE$534a8113) {
            ;
         }

         this.g = false;
         this.h = Double.NaN;
         return this.i == vh.a.RESET_STATE$534a8113 || this.i == vh.a.RESET_DERIVATIVES$534a8113;
      } else {
         return false;
      }
   }

   // $VF: synthetic method
   static double[] a(vi var0, wB var1) {
      return var0.a(var1);
   }

   // $VF: synthetic method
   static vh a(vi var0) {
      return var0.a;
   }

   static class a extends RuntimeException {
      private static final long b = 20120901L;
      final pc a;

      a(pc var1) {
         this.a = var1;
      }

      public final pc a() {
         return this.a;
      }
   }
}
