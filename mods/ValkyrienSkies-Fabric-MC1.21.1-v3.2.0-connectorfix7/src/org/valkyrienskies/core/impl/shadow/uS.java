package org.valkyrienskies.core.impl.shadow;

public abstract class uS<T extends kO<T>> extends vw<T> {
   protected T[] d;
   protected sx<T> e;
   private uO<T> k;
   private final int l;
   private double m;
   private double n;
   private double o;
   private double p;

   public uS(kM<T> var1, String var2, int var3, int var4, double var5, double var7, double var9, double var11) {
      super(var1, var2, var5, var7, var9, var11);
      if (var3 < 2) {
         throw new pn(px.INTEGRATION_METHOD_NEEDS_AT_LEAST_TWO_PREVIOUS_POINTS, var3, 2, true);
      }

      this.k = new vG<>(var1, var5, var7, var9, var11);
      this.l = var3;
      this.m = -1.0 / var4;
      double var14 = 0.9;
      this.n = 0.9;
      var14 = 0.2;
      this.o = 0.2;
      var14 = BY.a(2.0, -this.m);
      this.p = var14;
   }

   public uS(kM<T> var1, String var2, int var3, int var4, double var5, double var7, double[] var9, double[] var10) {
      super(var1, var2, var5, var7, var9, var10);
      this.k = new vG<>(var1, var5, var7, var9, var10);
      this.l = var3;
      this.m = -1.0 / var4;
      double var12 = 0.9;
      this.n = 0.9;
      var12 = 0.2;
      this.o = 0.2;
      var12 = BY.a(2.0, -this.m);
      this.p = var12;
   }

   private uO<T> q() {
      return this.k;
   }

   private void a(uO<T> var1) {
      this.k = var1;
   }

   protected final void a(uH<T> var1, uI<T> var2, T var3) {
      this.k.f();
      this.k.d();
      this.k.a(new uS.a(var1.c, (this.l + 3) / 2));

      try {
         this.k.a(var1, var2, (T)var3);
         throw new oX(px.MULTISTEP_STARTER_STOPPED_EARLY);
      } catch (uS.b var5) {
         this.k().d(this.k.j());
         this.k.d();
      }
   }

   protected abstract sx<T> a(T var1, T[] var2, T[][] var3, T[][] var4);

   private double r() {
      return this.o;
   }

   private void a(double var1) {
      this.o = 0.2;
   }

   private double s() {
      return this.p;
   }

   private void b(double var1) {
      this.p = var1;
   }

   private double t() {
      return this.n;
   }

   private void c(double var1) {
      this.n = 0.9;
   }

   private int u() {
      return this.l;
   }

   @Override
   protected final void b(T var1) {
      kO var2 = (kO)var1.d(this.m());

      for (int var3 = 0; var3 < this.d.length; var3++) {
         this.d[var3] = this.d[var3].c((T)var2);
      }

      sx var8 = this.e;
      kO[][] var9 = this.e.a;
      kO var4 = var2;

      for (int var5 = 0; var5 < var9.length; var5++) {
         var4 = var4.c(var2);
         kO[] var6 = var9[var5];

         for (int var7 = 0; var7 < var6.length; var7++) {
            var6[var7] = var6[var7].c(var4);
         }
      }

      this.a((T)var1);
   }

   protected final T c(T var1) {
      return Ci.b((T)((kO)var1.c().a()).a(this.p), Ci.a((T)((kO)var1.c().a()).a(this.o), (T)var1.g(this.m).c(this.n)));
   }

   class a implements wv<T> {
      private final uG<T> a;
      private int b;
      private uJ<T> c;
      private final kO[] d;
      private final kO[][] e;
      private final kO[][] f;

      a(uG<T> var2, int var3) {
         this.a = var2;
         this.b = 0;
         this.d = Ch.a(uS.super.c, var3);
         this.e = Ch.a(uS.super.c, var3, -1);
         this.f = Ch.a(uS.super.c, var3, -1);
      }

      @Override
      public final void a(ww<T> var1, boolean var2) {
         if (this.b == 0) {
            uJ var7 = var1.c();
            this.c = var7;
            this.d[this.b] = var7.a;
            this.e[this.b] = this.a.a((uI<T>)var7);
            this.f[this.b] = this.a.a(var7);
         }

         this.b++;
         uJ var8 = var1.d();
         this.d[this.b] = var8.a;
         this.e[this.b] = this.a.a((uI<T>)var8);
         this.f[this.b] = this.a.a(var8);
         if (this.b == this.d.length - 1) {
            uS var10000 = uS.this;
            kO var4 = this.d[this.d.length - 1].b(this.d[0]).d((double)(this.d.length - 1));
            var10000.b = (T)var4;
            uS var3;
            (var3 = uS.this).d = Ch.a(uS.super.c, this.f[0].length);

            for (int var5 = 0; var5 < uS.this.d.length; var5++) {
               kO var10002 = this.f[0][var5];
               var3 = uS.this;
               uS.this.d[var5] = (T)var10002.c(uS.super.b);
            }

            (var3 = uS.this).e = uS.this.a(uS.super.b, (T[])this.d, (T[][])this.e, (T[][])this.f);
            uJ var6 = this.c;
            var3 = uS.this;
            uS.super.a = var6;
            throw new uS.b();
         }
      }

      @Override
      public final void a(uJ<T> var1, T var2) {
      }
   }

   static class b extends RuntimeException {
      private static final long a = -1914085471038046418L;

      b() {
         super((Throwable)null);
      }
   }
}
