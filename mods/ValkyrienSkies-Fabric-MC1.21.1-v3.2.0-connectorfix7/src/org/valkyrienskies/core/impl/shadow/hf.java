package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;

final class hf {
   private hf() {
   }

   static <T> fF<T>[] a(fF<? super T>... var0) {
      return var0 == null ? null : (fF[])var0.clone();
   }

   static <T> fF<T> a(fF<? super T> var0) {
      return var0;
   }

   static void b(fF<?>... var0) {
      if (var0 == null) {
         throw new NullPointerException("The predicate array must not be null");
      }

      for (int var1 = 0; var1 < var0.length; var1++) {
         if (var0[var1] == null) {
            throw new NullPointerException("The predicate array must not contain a null predicate, index " + var1 + " was null");
         }
      }
   }

   static <T> fF<? super T>[] a(Collection<? extends fF<? super T>> var0) {
      if (var0 == null) {
         throw new NullPointerException("The predicate collection must not be null");
      }

      fF[] var1 = new fF[var0.size()];
      int var2 = 0;

      for (fF var3 : var0) {
         var1[var2] = var3;
         if (var1[var2] == null) {
            throw new NullPointerException("The predicate collection must not contain a null predicate, index " + var2 + " was null");
         }

         var2++;
      }

      return var1;
   }

   static <E> fb<E>[] a(fb<? super E>... var0) {
      return var0 == null ? null : (fb[])var0.clone();
   }

   static void b(fb<?>... var0) {
      if (var0 == null) {
         throw new NullPointerException("The closure array must not be null");
      }

      for (int var1 = 0; var1 < var0.length; var1++) {
         if (var0[var1] == null) {
            throw new NullPointerException("The closure array must not contain a null closure, index " + var1 + " was null");
         }
      }
   }

   private static <T> fb<T> a(fb<? super T> var0) {
      return var0;
   }

   static <I, O> fQ<I, O>[] a(fQ<? super I, ? extends O>... var0) {
      return var0 == null ? null : (fQ[])var0.clone();
   }

   static void b(fQ<?, ?>... var0) {
      if (var0 == null) {
         throw new NullPointerException("The transformer array must not be null");
      }

      for (int var1 = 0; var1 < var0.length; var1++) {
         if (var0[var1] == null) {
            throw new NullPointerException("The transformer array must not contain a null transformer, index " + var1 + " was null");
         }
      }
   }

   private static <I, O> fQ<I, O> a(fQ<? super I, ? extends O> var0) {
      return var0;
   }
}
