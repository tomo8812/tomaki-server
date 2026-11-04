package org.valkyrienskies.core.impl.shadow;

public abstract class vw<T extends kO<T>> extends uz<T> {
   protected double f;
   protected double g;
   protected double[] h;
   protected double[] i;
   protected int j;
   private T d;
   private T e;
   private T k;

   public vw(kM<T> var1, String var2, double var3, double var5, double var7, double var9) {
      super(var1, var2);
      double var18 = var9;
      double var16 = var7;
      double var14 = var5;
      double var12 = var3;
      vw var20 = this;
      this.e = (T)((kO)var20.a().a()).a(BY.w(var12));
      var20.k = (T)((kO)var20.a().a()).a(BY.w(var14));
      var20.d = (T)((kO)var20.a().b()).a();
      var20.f = var16;
      var20.g = var18;
      var20.h = null;
      var20.i = null;
      this.f_();
   }

   public vw(kM<T> var1, String var2, double var3, double var5, double[] var7, double[] var8) {
      super(var1, var2);
      double[] var16 = var8;
      double[] var15 = var7;
      double var12 = var5;
      double var10 = var3;
      vw var14 = this;
      this.e = (T)((kO)var14.a().a()).a(BY.w(var10));
      var14.k = (T)((kO)var14.a().a()).a(BY.w(var12));
      var14.d = (T)((kO)var14.a().b()).a();
      var14.f = 0.0;
      var14.g = 0.0;
      var14.h = (double[])var15.clone();
      var14.i = (double[])var16.clone();
      this.f_();
   }

   private void a(double var1, double var3, double var5, double var7) {
      this.e = this.a().a().a(BY.w(var1));
      this.k = this.a().a().a(BY.w(var3));
      this.d = this.a().b().a();
      this.f = var5;
      this.g = var7;
      this.h = null;
      this.i = null;
   }

   private void a(double var1, double var3, double[] var5, double[] var6) {
      this.e = this.a().a().a(BY.w(var1));
      this.k = this.a().a().a(BY.w(var3));
      this.d = this.a().b().a();
      this.f = 0.0;
      this.g = 0.0;
      this.h = (double[])var5.clone();
      this.i = (double[])var6.clone();
   }

   private void b(T var1) {
      if (!(((kO)var1.b(this.e)).d() < 0.0) && !(((kO)var1.b(this.k)).d() > 0.0)) {
         this.d = (T)var1;
      } else {
         this.d = this.a().b().a();
      }
   }

   @Override
   public final void a(uI<T> var1, T var2) {
      super.a(var1, (T)var2);
      this.j = var1.b.length;
      if (this.h != null && this.h.length != this.j) {
         throw new oS(this.j, this.h.length);
      }

      if (this.i != null && this.i.length != this.j) {
         throw new oS(this.j, this.i.length);
      }
   }

   public final T a(boolean var1, int var2, T[] var3, uJ<T> var4, uG<T> var5) {
      if (this.d.d() > 0.0) {
         return var1 ? this.d : this.d.a();
      }

      kO[] var6 = var5.a((uI<T>)var4);
      kO[] var12 = var5.a(var4);
      kO var7 = this.a().a();
      kO var8 = this.a().a();

      for (int var9 = 0; var9 < var3.length; var9++) {
         kO var10 = var6[var9].d(var3[var9]);
         var7 = var7.a(var10.c(var10));
         kO var11 = var12[var9].d(var3[var9]);
         var8 = var8.a(var11.c(var11));
      }

      kO var17 = (kO)(!(var7.d() < 1.0E-10) && !(var8.d() < 1.0E-10) ? var7.d(var8).k().c(0.01) : this.a().a().a(1.0E-6));
      if (!var1) {
         var17 = (kO)var17.a();
      }

      kO[] var20 = Ch.a(this.a(), var6.length);

      for (int var23 = 0; var23 < var6.length; var23++) {
         var20[var23] = var6[var23].a(var12[var23].c(var17));
      }

      kO[] var24 = this.a(var4.a.a((T)var17), (T[])var20);
      kO var13 = this.a().a();

      for (int var15 = 0; var15 < var3.length; var15++) {
         kO var21 = var24[var15].b(var12[var15]).d(var3[var15]);
         var13 = var13.a(var21.c(var21));
      }

      kO var14 = var13.k().d(var17);
      kO var22 = (var7 = Ci.a(var8.k(), var14)).d() < 1.0E-15 ? Ci.a(this.a().a().a(1.0E-6), var17.e().c(0.001)) : ((kO)((kO)var7.a(100)).b()).g(1.0 / var2);
      var17 = Ci.a(Ci.b((kO)var17.e().a(100), var22), var4.a.e().c(1.0E-12));
      var17 = Ci.a(this.e, Ci.b(this.k, var17));
      if (!var1) {
         var17 = (kO)var17.a();
      }

      return (T)var17;
   }

   protected final T a(T var1, boolean var2, boolean var3) {
      kO var4 = var1;
      if (((kO)var1.e().b(this.e)).d() < 0.0) {
         if (!var3) {
            throw new pn(px.MINIMAL_STEPSIZE_REACHED_DURING_INTEGRATION, var1.e().d(), this.e.d(), true);
         }

         var4 = var2 ? this.e : this.e.a();
      }

      if (((kO)var4.b(this.k)).d() > 0.0) {
         var4 = this.k;
      } else if (((kO)var4.a(this.k)).d() < 0.0) {
         var4 = this.k.a();
      }

      return (T)var4;
   }

   protected final void f_() {
      this.a((uJ<T>)null);
      this.a(this.e.c(this.k).k());
   }

   private T s() {
      return this.e;
   }

   private T t() {
      return this.k;
   }
}
