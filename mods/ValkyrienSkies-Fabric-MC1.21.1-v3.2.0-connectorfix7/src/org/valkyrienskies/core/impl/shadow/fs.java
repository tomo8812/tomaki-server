package org.valkyrienskies.core.impl.shadow;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class fs {
   private fs() {
   }

   public static <T> List<T> a(List<T> var0) {
      return var0 == null ? Collections.emptyList() : var0;
   }

   private static <T> List<T> b(List<T> var0, List<T> var1) {
      return var0 == null ? var1 : var0;
   }

   private static <E> List<E> c(List<? extends E> var0, List<? extends E> var1) {
      ArrayList var2 = new ArrayList();
      List var3 = var0;
      List var4 = var1;
      if (var0.size() > var1.size()) {
         var3 = var1;
         var4 = var0;
      }

      HashSet var5 = new HashSet<>(var3);

      for (Object var7 : var4) {
         if (var5.contains(var7)) {
            var2.add(var7);
            var5.remove(var7);
         }
      }

      return var2;
   }

   private static <E> List<E> d(List<E> var0, List<? extends E> var1) {
      ArrayList var2 = new ArrayList();
      ga var5 = new ga<>(var1);

      for (Object var3 : var0) {
         if (!var5.b(var3, 1)) {
            var2.add(var3);
         }
      }

      return var2;
   }

   private static <E> List<E> e(List<? extends E> var0, List<? extends E> var1) {
      List var3 = var1;
      ArrayList var2 = var0;
      ArrayList var4;
      (var4 = new ArrayList(var2.size() + var3.size())).addAll(var2);
      var4.addAll(var3);
      var3 = var1;
      var2 = var0;
      var4 = new ArrayList();
      var0 = var2;
      var1 = var3;
      if (var2.size() > var3.size()) {
         var0 = var3;
         var1 = var2;
      }

      HashSet var12 = new HashSet<>(var0);

      for (Object var9 : var1) {
         if (var12.contains(var9)) {
            var4.add(var9);
            var12.remove(var9);
         }
      }

      ArrayList var16 = var4;
      var2 = var4;
      var4 = new ArrayList();
      ga var7 = new ga<>(var16);

      for (Object var14 : var2) {
         if (!var7.b(var14, 1)) {
            var4.add(var14);
         }
      }

      return var4;
   }

   private static <E> List<E> f(List<? extends E> var0, List<? extends E> var1) {
      ArrayList var2;
      (var2 = new ArrayList(var0.size() + var1.size())).addAll(var0);
      var2.addAll(var1);
      return var2;
   }

   private static <E> List<E> a(Collection<? extends E> var0, fF<? super E> var1) {
      return fd.a(var0, var1, new ArrayList<>(var0.size()));
   }

   private static <E> List<E> b(Collection<? extends E> var0, fF<? super E> var1) {
      return fd.b(var0, var1, new ArrayList<>(var0.size()));
   }

   public static boolean a(Collection<?> var0, Collection<?> var1) {
      if (var0 == var1) {
         return true;
      }

      if (var0 != null && var1 != null && var0.size() == var1.size()) {
         Iterator var4 = var0.iterator();
         Iterator var5 = var1.iterator();

         while (var4.hasNext() && var5.hasNext()) {
            Object var2 = var4.next();
            Object var3 = var5.next();
            if (var2 == null ? var3 != null : !var2.equals(var3)) {
               return false;
            }
         }

         return !var4.hasNext() && !var5.hasNext();
      } else {
         return false;
      }
   }

   public static int a(Collection<?> var0) {
      if (var0 == null) {
         return 0;
      }

      int var1 = 1;

      for (Object var2 : var0) {
         var1 = var1 * 31 + (var2 == null ? 0 : var2.hashCode());
      }

      return var1;
   }

   public static <E> List<E> b(Collection<E> var0, Collection<?> var1) {
      ArrayList var2 = new ArrayList(Math.min(var0.size(), var1.size()));

      for (Object var3 : var0) {
         if (var1.contains(var3)) {
            var2.add(var3);
         }
      }

      return var2;
   }

   public static <E> List<E> c(Collection<E> var0, Collection<?> var1) {
      ArrayList var2 = new ArrayList();

      for (Object var3 : var0) {
         if (!var1.contains(var3)) {
            var2.add(var3);
         }
      }

      return var2;
   }

   private static <E> List<E> b(List<E> var0) {
      return Collections.synchronizedList(var0);
   }

   public static <E> List<E> a(List<? extends E> var0) {
      return iY.a(var0);
   }

   private static <E> List<E> a(List<E> var0, fF<E> var1) {
      return iU.a(var0, var1);
   }

   private static <E> List<E> a(List<E> var0, fQ<? super E, ? extends E> var1) {
      return iW.a(var0, var1);
   }

   private static <E> List<E> a(List<E> var0, fh<? extends E> var1) {
      return iS.a(var0, var1);
   }

   private static <E> List<E> c(List<E> var0) {
      return iQ.a(var0);
   }

   private static <E> int b(List<E> var0, fF<E> var1) {
      if (var0 != null && var1 != null) {
         for (int var2 = 0; var2 < var0.size(); var2++) {
            Object var3 = var0.get(var2);
            if (var1.a(var3)) {
               return var2;
            }
         }
      }

      return -1;
   }

   public static <E> List<E> a(List<E> var0, List<E> var1) {
      gW var2 = gW.a();
      var1 = var1;
      var0 = var0;
      if (var0 == null || var1 == null) {
         throw new NullPointerException("List must not be null");
      }

      if (var2 == null) {
         throw new NullPointerException("Equator must not be null");
      }

      kn var5 = new kn(var0, var1, var2);
      ki var10 = new ki();
      var5.a(0, var5.a.size(), 0, var5.b.size(), var10);
      ki var6 = var10;
      fs.b var9 = new fs.b();
      fs.b var11 = var9;
      Iterator var7 = var6.a.iterator();

      while (var7.hasNext()) {
         ((kh)var7.next()).a(var11);
      }

      return var9.a;
   }

   private static <E> List<E> a(List<E> var0, List<E> var1, fg<? super E> var2) {
      if (var0 == null || var1 == null) {
         throw new NullPointerException("List must not be null");
      }

      if (var2 == null) {
         throw new NullPointerException("Equator must not be null");
      }

      ki var3 = new kn(var0, var1, var2).a();
      fs.b var4 = new fs.b();
      var3.a(var4);
      return var4.a;
   }

   private static String a(CharSequence var0, CharSequence var1) {
      if (var0 != null && var1 != null) {
         List var3 = a(new fs.a(var0), new fs.a(var1));
         var1 = new StringBuilder();

         for (Character var2 : var3) {
            var1.append(var2);
         }

         return var1.toString();
      } else {
         throw new NullPointerException("CharSequence must not be null");
      }
   }

   private static <T> List<List<T>> a(List<T> var0, int var1) {
      if (var0 == null) {
         throw new NullPointerException("List must not be null");
      } else if (var1 <= 0) {
         throw new IllegalArgumentException("Size must be greater than 0");
      } else {
         return new fs.c<>(var0, var1, (byte)0);
      }
   }

   static final class a extends AbstractList<Character> {
      private final CharSequence a;

      public a(CharSequence var1) {
         this.a = var1;
      }

      private Character a(int var1) {
         return this.a.charAt(var1);
      }

      @Override
      public final int size() {
         return this.a.length();
      }

      // $VF: synthetic method
      @Override
      public final Object get(int var1) {
         int var2 = var1;
         return this.a.charAt(var2);
      }
   }

   public static final class b<E> implements kf<E> {
      final ArrayList<E> a = new ArrayList<>();

      @Override
      public final void a(E var1) {
      }

      @Override
      public final void b(E var1) {
      }

      @Override
      public final void c(E var1) {
         this.a.add((E)var1);
      }

      public final List<E> a() {
         return this.a;
      }
   }

   static class c<T> extends AbstractList<List<T>> {
      private final List<T> a;
      private final int b;

      private c(List<T> var1, int var2) {
         this.a = var1;
         this.b = var2;
      }

      private List<T> a(int var1) {
         int var2 = this.size();
         if (var1 < 0) {
            throw new IndexOutOfBoundsException("Index " + var1 + " must not be negative");
         }

         if (var1 >= var2) {
            throw new IndexOutOfBoundsException("Index " + var1 + " must be less than size " + var2);
         }

         int var3;
         var2 = Math.min((var3 = var1 * this.b) + this.b, this.a.size());
         return this.a.subList(var3, var2);
      }

      @Override
      public final int size() {
         return (int)Math.ceil((double)this.a.size() / this.b);
      }

      @Override
      public final boolean isEmpty() {
         return this.a.isEmpty();
      }

      // $VF: synthetic method
      @Override
      public final Object get(int var1) {
         int var2 = var1;
         fs.c var4 = this;
         int var3 = this.size();
         if (var2 < 0) {
            throw new IndexOutOfBoundsException("Index " + var2 + " must not be negative");
         }

         if (var2 >= var3) {
            throw new IndexOutOfBoundsException("Index " + var2 + " must be less than size " + var3);
         }

         int var5;
         var3 = Math.min((var5 = var2 * var4.b) + var4.b, var4.a.size());
         return var4.a.subList(var5, var3);
      }

      // $VF: synthetic method
      c(List var1, int var2, byte var3) {
         this(var1, var2);
      }
   }
}
