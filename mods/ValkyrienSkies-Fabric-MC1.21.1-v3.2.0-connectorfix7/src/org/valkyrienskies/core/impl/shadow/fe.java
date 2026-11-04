package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Comparator;

public final class fe {
   public static final gE a = gE.a();

   private fe() {
   }

   public static <E extends Comparable<? super E>> Comparator<E> a() {
      return a;
   }

   private static <E> Comparator<E> a(Comparator<E>... var0) {
      gF var1 = new gF();
      Comparator[] var6;
      int var2 = (var6 = var0).length;

      for (int var3 = 0; var3 < var2; var3++) {
         Comparator var4;
         if ((var4 = var6[var3]) == null) {
            throw new NullPointerException("Comparator cannot be null");
         }

         Comparator var5 = var4;
         boolean var7 = false;
         var5 = var5;
         var4 = var1;
         var1.a();
         var4.a.add(var5);
      }

      return var1;
   }

   private static <E> Comparator<E> a(Collection<Comparator<E>> var0) {
      Comparator[] var6 = var0.toArray(new Comparator[var0.size()]);
      gF var1 = new gF();
      Comparator[] var7;
      int var2 = (var7 = var6).length;

      for (int var3 = 0; var3 < var2; var3++) {
         Comparator var4;
         if ((var4 = var7[var3]) == null) {
            throw new NullPointerException("Comparator cannot be null");
         }

         Comparator var5 = var4;
         boolean var8 = false;
         var5 = var5;
         var4 = var1;
         var1.a();
         var4.a.add(var5);
      }

      return var1;
   }

   private static <E> Comparator<E> a(Comparator<E> var0) {
      return new gI<>(var0);
   }

   private static Comparator<Boolean> a(boolean var0) {
      return gD.a(var0);
   }

   private static <E> Comparator<E> b(Comparator<E> var0) {
      if (var0 == null) {
         var0 = a;
      }

      return new gH<>(var0, false);
   }

   private static <E> Comparator<E> c(Comparator<E> var0) {
      if (var0 == null) {
         var0 = a;
      }

      return new gH<>(var0, true);
   }

   private static <I, O> Comparator<I> a(Comparator<O> var0, fQ<? super I, ? extends O> var1) {
      if (var0 == null) {
         var0 = a;
      }

      return new gJ<>(var1, var0);
   }

   private static <E> E a(E var0, E var1, Comparator<E> var2) {
      if (var2 == null) {
         var2 = a;
      }

      return (E)(var2.compare(var0, var1) < 0 ? var0 : var1);
   }

   private static <E> E b(E var0, E var1, Comparator<E> var2) {
      if (var2 == null) {
         var2 = a;
      }

      return (E)(var2.compare(var0, var1) > 0 ? var0 : var1);
   }
}
