package org.valkyrienskies.core.impl.shadow;

public abstract class wr<T extends kO<T>> implements ww<T> {
   public final uJ<T> a;
   public final uJ<T> b;
   private final uJ<T> d;
   private final uJ<T> e;
   public final boolean c;
   private uG<T> f;

   public wr(boolean var1, uJ<T> var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uG<T> var6) {
      this.c = var1;
      this.a = var2;
      this.b = var3;
      this.d = var4;
      this.e = var5;
      this.f = var6;
   }

   public final wr<T> a(uJ<T> var1, uJ<T> var2) {
      return this.a(this.c, this.a, this.b, var1, var2, this.f);
   }

   protected abstract wr<T> a(boolean var1, uJ<T> var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uG<T> var6);

   public final uJ<T> a() {
      return this.a;
   }

   public final uJ<T> b() {
      return this.b;
   }

   @Override
   public final uJ<T> c() {
      return this.d;
   }

   @Override
   public final uJ<T> d() {
      return this.e;
   }

   @Override
   public final uJ<T> a(T var1) {
      uJ var4 = this.a;
      kO var2 = (kO)var1.b(this.a.a);
      var4 = this.b;
      kO var3 = this.b.a.b((T)var1);
      var4 = this.b;
      var4 = this.a;
      kO var8 = (kO)var2.d(this.b.a.b(this.a.a));
      return this.a((T)var1, (T)var8, (T)var2, (T)var3);
   }

   @Override
   public final boolean e() {
      return this.c;
   }

   protected abstract uJ<T> a(T var1, T var2, T var3, T var4);
}
