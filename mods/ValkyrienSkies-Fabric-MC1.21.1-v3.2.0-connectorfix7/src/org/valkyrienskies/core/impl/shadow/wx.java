package org.valkyrienskies.core.impl.shadow;

public final class wx<T extends kO<T>> implements wv<T> {
   private double a;
   private final wu<T> b;
   private uJ<T> c;
   private uJ<T> d;
   private boolean e;
   private final wD f;
   private final int g;

   private wx(double var1, wu<T> var3) {
      this(var1, var3, wE.INCREMENT$34d2fcad, wD.FIRST);
   }

   private wx(double var1, wu<T> var3, int var4) {
      this(var1, var3, var4, wD.FIRST);
   }

   private wx(double var1, wu<T> var3, wD var4) {
      this(var1, var3, wE.INCREMENT$34d2fcad, var4);
   }

   private wx(double var1, wu<T> var3, int var4, wD var5) {
      this.a = BY.w(var1);
      this.b = var3;
      this.g = var4;
      this.f = var5;
      this.c = null;
      this.d = null;
      this.e = true;
   }

   @Override
   public final void a(uJ<T> var1, T var2) {
      this.c = null;
      this.d = null;
      this.e = true;
   }

   @Override
   public final void a(ww<T> var1, boolean var2) {
      if (this.d == null) {
         this.c = var1.c();
         this.d = this.c;
         this.e = var1.e();
         if (!this.e) {
            this.a = -this.a;
         }
      }

      kO var10000;
      if (this.g == wE.INCREMENT$34d2fcad) {
         uJ var4 = this.d;
         var10000 = this.d.a.a(this.a);
      } else {
         uJ var6 = this.d;
         var10000 = this.d.a.c().a();
         var6 = this.d;
         var10000 = var10000.a((BY.y(this.d.a.d() / this.a) + 1.0) * this.a);
      }

      kO var3 = var10000;
      if (this.g == wE.MULTIPLES$34d2fcad) {
         double var12 = var3.d();
         uJ var8 = this.d;
         if (Cq.b(var12, this.d.a.d(), 1)) {
            var3 = var3.a(this.a);
         }
      }

      while (this.a((T)var3, var1)) {
         this.a(false);
         this.d = var1.a((T)var3);
         var3 = var3.a(this.a);
      }

      if (var2) {
         label39: {
            wD var9 = this.f;
            if (this.f.last) {
               uJ var10 = this.d;
               if (this.d.a.d() != var1.d().a.d()) {
                  var13 = true;
                  break label39;
               }
            }

            var13 = false;
         }

         var2 = var13;
         this.a(!var2);
         if (var2) {
            this.d = var1.d();
            this.a(true);
         }
      }
   }

   private boolean a(T var1, ww<T> var2) {
      return this.e ? var1.d() <= var2.d().a.d() : var1.d() >= var2.d().a.d();
   }

   private void a(boolean var1) {
      wD var2 = this.f;
      if (!this.f.first) {
         uJ var3 = this.c;
         double var10000 = this.c.a.d();
         uJ var4 = this.d;
         if (var10000 == this.d.a.d()) {
            return;
         }
      }
   }
}
