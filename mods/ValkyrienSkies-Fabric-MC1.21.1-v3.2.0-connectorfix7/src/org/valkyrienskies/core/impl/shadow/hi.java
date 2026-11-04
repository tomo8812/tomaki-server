package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hi<I, O> implements Serializable, fQ<I, O> {
   private static final long a = 8069309411242014252L;
   private final fF<? super I> b;
   private final fQ<? super I, ? extends O> c;
   private final fQ<? super I, ? extends O> d;

   public static <I, O> fQ<I, O> a(fF<? super I> var0, fQ<? super I, ? extends O> var1, fQ<? super I, ? extends O> var2) {
      if (var0 == null) {
         throw new NullPointerException("Predicate must not be null");
      } else if (var1 != null && var2 != null) {
         return new hi<>(var0, var1, var2);
      } else {
         throw new NullPointerException("Transformers must not be null");
      }
   }

   public static <T> fQ<T, T> a(fF<? super T> var0, fQ<? super T, ? extends T> var1) {
      if (var0 == null) {
         throw new NullPointerException("Predicate must not be null");
      } else if (var1 == null) {
         throw new NullPointerException("Transformer must not be null");
      } else {
         return new hi(var0, var1, hp.a());
      }
   }

   private hi(fF<? super I> var1, fQ<? super I, ? extends O> var2, fQ<? super I, ? extends O> var3) {
      this.b = var1;
      this.c = var2;
      this.d = var3;
   }

   @Override
   public final O a(I var1) {
      return (O)(this.b.a((I)var1) ? this.c.a((I)var1) : this.d.a((I)var1));
   }

   private fF<? super I> a() {
      return this.b;
   }

   private fQ<? super I, ? extends O> b() {
      return this.c;
   }

   private fQ<? super I, ? extends O> c() {
      return this.d;
   }
}
