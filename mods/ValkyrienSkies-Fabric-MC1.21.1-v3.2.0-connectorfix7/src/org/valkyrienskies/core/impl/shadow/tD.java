package org.valkyrienskies.core.impl.shadow;

public final class tD<T extends kN<T>> extends su<T> {
   private final Cn<T> a;
   private final int b;
   private final int c;

   private tD(kM<T> var1) {
      super(var1);
      this.b = 0;
      this.c = 0;
      this.a = new Cn<>(var1);
   }

   public tD(kM<T> var1, int var2, int var3) {
      super(var1, var2, var3);
      this.b = var2;
      this.c = var3;
      this.a = new Cn<>(var1);
   }

   private tD(tD<T> var1) {
      super(var1.a(), var1.b, var1.c);
      this.b = var1.b;
      this.c = var1.c;
      this.a = new Cn<>(var1.a);
   }

   private tD(sQ<T> var1) {
      super(var1.a(), var1.f(), var1.g());
      this.b = var1.f();
      this.c = var1.g();
      this.a = new Cn<>(this.a());

      for (int var2 = 0; var2 < this.b; var2++) {
         for (int var3 = 0; var3 < this.c; var3++) {
            this.a(var2, var3, (T)var1.b(var2, var3));
         }
      }
   }

   @Override
   public final void b(int var1, int var2, T var3) {
      this.h(var1);
      this.i(var2);
      var1 = this.c(var1, var2);
      kN var5 = this.a.a(var1).a((T)var3);
      if (this.a().a().equals(var5)) {
         this.a.c(var1);
      } else {
         this.a.a(var1, (T)var5);
      }
   }

   @Override
   public final sQ<T> b() {
      return new tD<>(this);
   }

   @Override
   public final sQ<T> a(int var1, int var2) {
      return new tD<>(this.a(), var1, var2);
   }

   @Override
   public final int g() {
      return this.c;
   }

   @Override
   public final T b(int var1, int var2) {
      this.h(var1);
      this.i(var2);
      return this.a.a(this.c(var1, var2));
   }

   @Override
   public final int f() {
      return this.b;
   }

   @Override
   public final void c(int var1, int var2, T var3) {
      this.h(var1);
      this.i(var2);
      var1 = this.c(var1, var2);
      kN var5 = this.a.a(var1).c((T)var3);
      if (this.a().a().equals(var5)) {
         this.a.c(var1);
      } else {
         this.a.a(var1, (T)var5);
      }
   }

   @Override
   public final void a(int var1, int var2, T var3) {
      this.h(var1);
      this.i(var2);
      if (this.a().a().equals(var3)) {
         this.a.c(this.c(var1, var2));
      } else {
         this.a.a(this.c(var1, var2), (T)var3);
      }
   }

   private int c(int var1, int var2) {
      return var1 * this.c + var2;
   }
}
