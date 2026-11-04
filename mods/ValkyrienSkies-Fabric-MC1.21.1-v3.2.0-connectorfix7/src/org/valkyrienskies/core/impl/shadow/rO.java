package org.valkyrienskies.core.impl.shadow;

public final class rO<S extends qN> {
   public final sb<S> a;
   public final sb<S> b;
   public final rX<S> c;

   @Deprecated
   private rO(sb<S> var1, sb<S> var2) {
      this(var1, var2, null);
   }

   rO(sb<S> var1, sb<S> var2, rX<S> var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   public final sb<S> a() {
      return this.a;
   }

   public final sb<S> b() {
      return this.b;
   }

   public final rX<S> c() {
      return this.c;
   }
}
