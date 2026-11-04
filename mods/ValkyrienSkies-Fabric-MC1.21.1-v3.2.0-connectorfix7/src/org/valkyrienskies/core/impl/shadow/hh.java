package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hh<E> implements Serializable, fb<E> {
   private static final long a = 3518477308466486130L;
   private final fF<? super E> b;
   private final fb<? super E> c;
   private final fb<? super E> d;

   public static <E> fb<E> a(fF<? super E> var0, fb<? super E> var1) {
      return a(var0, var1, ho.a());
   }

   public static <E> fb<E> a(fF<? super E> var0, fb<? super E> var1, fb<? super E> var2) {
      if (var0 == null) {
         throw new NullPointerException("Predicate must not be null");
      } else if (var1 != null && var2 != null) {
         return new hh<>(var0, var1, var2);
      } else {
         throw new NullPointerException("Closures must not be null");
      }
   }

   private hh(fF<? super E> var1, fb<? super E> var2) {
      this(var1, var2, ho.a());
   }

   private hh(fF<? super E> var1, fb<? super E> var2, fb<? super E> var3) {
      this.b = var1;
      this.c = var2;
      this.d = var3;
   }

   @Override
   public final void a(E var1) {
      if (this.b.a((E)var1)) {
         this.c.a((E)var1);
      } else {
         this.d.a((E)var1);
      }
   }

   private fF<? super E> a() {
      return this.b;
   }

   private fb<? super E> b() {
      return this.c;
   }

   private fb<? super E> c() {
      return this.d;
   }
}
