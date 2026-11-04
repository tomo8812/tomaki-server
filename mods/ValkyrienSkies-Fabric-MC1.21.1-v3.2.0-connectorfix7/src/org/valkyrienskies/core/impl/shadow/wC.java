package org.valkyrienskies.core.impl.shadow;

public final class wC implements wA {
   private double a;
   private final wy b;
   private double c;
   private double d;
   private double[] e;
   private double[] f;
   private boolean g;
   private final wD h;
   private final int i;

   private wC(double var1, wy var3) {
      this(var1, var3, wE.INCREMENT$34d2fcad, wD.FIRST);
   }

   private wC(double var1, wy var3, int var4) {
      this(var1, var3, var4, wD.FIRST);
   }

   private wC(double var1, wy var3, wD var4) {
      this(var1, var3, wE.INCREMENT$34d2fcad, var4);
   }

   private wC(double var1, wy var3, int var4, wD var5) {
      this.a = BY.w(var1);
      this.b = var3;
      this.i = var4;
      this.h = var5;
      this.c = Double.NaN;
      this.d = Double.NaN;
      this.e = null;
      this.f = null;
      this.g = true;
   }

   @Override
   public final void a(double var1, double[] var3, double var4) {
      this.c = Double.NaN;
      this.d = Double.NaN;
      this.e = null;
      this.f = null;
      this.g = true;
   }

   @Override
   public final void a(ws var1, boolean var2) {
      if (this.e == null) {
         this.c = var1.g();
         this.d = var1.g();
         var1.d(this.d);
         this.e = (double[])var1.k().clone();
         this.f = (double[])var1.l().clone();
         this.g = var1.h() >= this.d;
         if (!this.g) {
            this.a = -this.a;
         }
      }

      double var3 = this.i == wE.INCREMENT$34d2fcad ? this.d + this.a : (BY.y(this.d / this.a) + 1.0) * this.a;
      if (this.i == wE.MULTIPLES$34d2fcad && Cq.b(var3, this.d, 1)) {
         var3 += this.a;
      }

      while (this.a(var3, var1)) {
         this.a(false);
         this.a(var1, var3);
         var3 += this.a;
      }

      if (var2) {
         wD var6 = this.h;
         var2 = this.h.last && this.d != var1.h();
         this.a(!var2);
         if (var2) {
            this.a(var1, var1.h());
            this.a(true);
         }
      }
   }

   private boolean a(double var1, wB var3) {
      return this.g ? var1 <= var3.h() : var1 >= var3.h();
   }

   private void a(boolean var1) {
      wD var2 = this.h;
      if (this.h.first || this.c != this.d) {
         ;
      }
   }

   private void a(wB var1, double var2) {
      this.d = var2;
      var1.d(this.d);
      System.arraycopy(var1.k(), 0, this.e, 0, this.e.length);
      System.arraycopy(var1.l(), 0, this.f, 0, this.f.length);
   }
}
