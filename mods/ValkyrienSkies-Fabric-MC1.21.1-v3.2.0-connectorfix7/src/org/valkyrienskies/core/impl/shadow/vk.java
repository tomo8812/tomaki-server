package org.valkyrienskies.core.impl.shadow;

public final class vk<T extends kO<T>> {
   public final vj<T> a;
   private final double j;
   public final T b;
   private final int k;
   public T c;
   public T d;
   public boolean e;
   public boolean f;
   public T g;
   private T l;
   private boolean m;
   private boolean n;
   public int h;
   public final nw<T> i;

   public vk(vj<T> var1, double var2, T var4, int var5, nw<T> var6) {
      this.a = var1;
      this.j = var2;
      this.b = (T)var4.e();
      this.k = var5;
      this.i = var6;
      this.c = null;
      this.d = null;
      this.e = true;
      this.f = false;
      this.g = null;
      this.l = null;
      this.n = true;
      this.h = vf.CONTINUE$3522afc3;
   }

   public final vj<T> a() {
      return this.a;
   }

   private double d() {
      return this.j;
   }

   private T e() {
      return this.b;
   }

   private int f() {
      return this.k;
   }

   public final void a(ww<T> var1) {
      uJ var2 = var1.c();
      this.c = var2.a;
      this.d = this.a.b();
      if (this.d.d() == 0.0) {
         double var3 = BY.e(this.i.c().d(), BY.w(this.i.d().c(this.c).d()));
         kO var5 = this.c.a(var3 * 0.5);
         vj var10001 = this.a;
         var1.a((T)var5);
         this.d = (T)var10001.b();
      }

      this.e = this.d.d() >= 0.0;
   }

   public final boolean b(final ww<T> var1) {
      this.m = var1.e();
      kO var2;
      kO var3;
      if (((kO)(var3 = (kO)(var2 = var1.d().a).b(this.c)).e().b(this.b)).d() < 0.0) {
         return false;
      }

      int var4 = BY.b(1, (int)BY.z(BY.w(var3.d()) / this.j));
      var3 = var3.d((double)var4);
      la var5 = new la<T>() {
         @Override
         public final T a(T var1x) {
            vk var2x = vk.this;
            vj var10000 = vk.this.a;
            var1.a((T)var1x);
            return (T)var10000.b();
         }
      };
      kO var6 = this.c;
      kO var7 = this.d;

      for (int var8 = 0; var8 < var4; var8++) {
         kO var9 = (kO)(var8 == var4 - 1 ? var2 : this.c.a((T)var3.a(var8 + 1)));
         vj var10000 = this.a;
         var1.a((T)var9);
         kO var10 = var10000.b();
         if (this.e ^ var10.d() >= 0.0) {
            this.n = var10.b(var7).d() >= 0.0;
            kO var11 = this.m
               ? this.i.a(this.k, var5, (T)var6, (T)var9, nr.RIGHT_SIDE$37d0779)
               : this.i.a(this.k, var5, (T)var9, (T)var6, nr.LEFT_SIDE$37d0779);
            if (this.l != null && ((kO)var11.b(var6).e().b(this.b)).d() <= 0.0 && ((kO)((kO)var11.b(this.l)).e().b(this.b)).d() <= 0.0) {
               do {
                  var6 = this.m ? (kO)var6.a(this.b) : (kO)var6.b(this.b);
                  var7 = var5.a((T)var6);
               } while (this.e ^ var7.d() >= 0.0 && this.m ^ var6.b(var9).d() >= 0.0);

               if (!(this.m ^ var6.b(var9).d() >= 0.0)) {
                  this.g = (T)var11;
                  this.f = true;
                  return true;
               }

               var8--;
            } else {
               if (this.l == null || this.l.b((T)var11).e().b(this.b).d() > 0.0) {
                  this.g = (T)var11;
                  this.f = true;
                  return true;
               }

               var6 = var9;
               var7 = var10;
            }
         } else {
            var6 = var9;
            var7 = var10;
         }
      }

      this.f = false;
      this.g = null;
      return false;
   }

   public final T b() {
      return this.f ? this.g : this.c.c().a().a(this.m ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
   }

   public final void a(uJ<T> var1) {
      this.c = var1.a;
      this.d = this.a.b();
      if (this.f && this.g.b(var1.a).e().b(this.b).d() <= 0.0) {
         this.l = var1.a;
         this.e = this.n;
         this.h = this.a.c();
      } else {
         this.e = this.d.d() >= 0.0;
         this.h = vf.CONTINUE$3522afc3;
      }
   }

   public final boolean c() {
      return this.h == vf.STOP$3522afc3;
   }

   public final uI<T> b(uJ<T> var1) {
      if (this.f && this.g.b(var1.a).e().b(this.b).d() <= 0.0) {
         uI var3;
         if (this.h == vf.RESET_STATE$3522afc3) {
            var3 = this.a.d();
         } else if (this.h == vf.RESET_DERIVATIVES$3522afc3) {
            var3 = var1;
         } else {
            var3 = null;
         }

         this.f = false;
         this.g = null;
         return var3;
      } else {
         return null;
      }
   }

   // $VF: synthetic method
   static vj a(vk var0) {
      return var0.a;
   }
}
