package org.valkyrienskies.core.impl.shadow;

public final class hK<E> implements fb<E> {
   private final fF<? super E> a;
   private final fb<? super E> b;
   private final boolean c;

   public static <E> fb<E> a(fF<? super E> var0, fb<? super E> var1, boolean var2) {
      if (var0 == null) {
         throw new NullPointerException("Predicate must not be null");
      } else if (var1 == null) {
         throw new NullPointerException("Closure must not be null");
      } else {
         return new hK<>(var0, var1, var2);
      }
   }

   private hK(fF<? super E> var1, fb<? super E> var2, boolean var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   @Override
   public final void a(E var1) {
      if (this.c) {
         this.b.a((E)var1);
      }

      while (this.a.a((E)var1)) {
         this.b.a((E)var1);
      }
   }

   private fF<? super E> a() {
      return this.a;
   }

   private fb<? super E> b() {
      return this.b;
   }

   private boolean c() {
      return this.c;
   }
}
