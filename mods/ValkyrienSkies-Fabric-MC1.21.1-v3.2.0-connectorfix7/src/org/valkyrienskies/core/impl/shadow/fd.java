package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class fd {
   public static final Collection a = Collections.emptyList();

   private fd() {
   }

   private static <T> Collection<T> a() {
      return a;
   }

   public static <T> Collection<T> a(Collection<T> var0) {
      return var0 == null ? a : var0;
   }

   private static <O> Collection<O> b(Iterable<? extends O> var0, Iterable<? extends O> var1) {
      for (Object var2 : var0 = new fd.c<>(var0, var1)) {
         var0.a(var2, var0.a(var2));
      }

      return var0.c;
   }

   public static <O> Collection<O> a(Iterable<? extends O> var0, Iterable<? extends O> var1) {
      for (Object var2 : var0 = new fd.c<>(var0, var1)) {
         var0.a(var2, var0.b(var2));
      }

      return var0.c;
   }

   private static <O> Collection<O> c(Iterable<? extends O> var0, Iterable<? extends O> var1) {
      for (Object var2 : var0 = new fd.c<>(var0, var1)) {
         var0.a(var2, var0.a(var2) - var0.b(var2));
      }

      return var0.c;
   }

   private static <O> Collection<O> d(Iterable<? extends O> var0, Iterable<? extends O> var1) {
      fF var2 = hI.a();
      var2 = var2;
      var1 = var1;
      var0 = var0;
      ArrayList var3 = new ArrayList();
      ga var4 = new ga();

      for (Object var5 : var1) {
         if (var2.a(var5)) {
            var4.add(var5);
         }
      }

      for (Object var11 : var0) {
         if (!var4.b(var11, 1)) {
            var3.add(var11);
         }
      }

      return var3;
   }

   private static <O> Collection<O> a(Iterable<? extends O> var0, Iterable<? extends O> var1, fF<O> var2) {
      ArrayList var3 = new ArrayList();
      ga var4 = new ga();

      for (Object var5 : var1) {
         if (var2.a(var5)) {
            var4.add(var5);
         }
      }

      for (Object var8 : var0) {
         if (!var4.b(var8, 1)) {
            var3.add(var8);
         }
      }

      return var3;
   }

   private static boolean d(Collection<?> var0, Collection<?> var1) {
      if (var1.isEmpty()) {
         return true;
      }

      Iterator var6 = var0.iterator();
      HashSet var2 = new HashSet();

      for (Object var3 : var1) {
         if (!var2.contains(var3)) {
            boolean var4 = false;

            while (var6.hasNext()) {
               Object var5 = var6.next();
               var2.add(var5);
               if (var3 == null ? var5 == null : var3.equals(var5)) {
                  var4 = true;
                  break;
               }
            }

            if (!var4) {
               return false;
            }
         }
      }

      return true;
   }

   private static <T> boolean a(Collection<?> var0, T... var1) {
      if (var0.size() < var1.length) {
         for (Object var3 : var0) {
            int var10000;
            label63: {
               var3 = var3;
               var3 = var3;
               boolean var5 = false;
               var3 = var3;
               Object[] var6 = var1;
               if (var1 != null) {
                  if (var3 == null) {
                     for (int var4 = 0; var4 < var6.length; var4++) {
                        if (var6[var4] == null) {
                           var10000 = var4;
                           break label63;
                        }
                     }
                  } else {
                     for (int var13 = 0; var13 < var6.length; var13++) {
                        if (var3.equals(var6[var13])) {
                           var10000 = var13;
                           break label63;
                        }
                     }
                  }
               }

               var10000 = -1;
            }

            if (var10000 != -1) {
               return true;
            }
         }
      } else {
         Object[] var8 = var1;
         int var12 = var1.length;

         for (int var7 = 0; var7 < var12; var7++) {
            Object var14 = var8[var7];
            if (var0.contains(var14)) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean e(Collection<?> var0, Collection<?> var1) {
      if (var0.size() < var1.size()) {
         for (Object var3 : var0) {
            if (var1.contains(var3)) {
               return true;
            }
         }
      } else {
         for (Object var5 : var1) {
            if (var0.contains(var5)) {
               return true;
            }
         }
      }

      return false;
   }

   public static <O> Map<O, Integer> a(Iterable<? extends O> var0) {
      HashMap var1 = new HashMap();

      for (Object var2 : var0) {
         Integer var3;
         if ((var3 = (Integer)var1.get(var2)) == null) {
            var1.put(var2, 1);
         } else {
            var1.put(var2, var3 + 1);
         }
      }

      return var1;
   }

   public static boolean a(Collection<?> var0, Collection<?> var1) {
      fd.a var4 = new fd.a(var0, var1);

      for (Object var2 : var0) {
         if (var4.c(var2) > var4.d(var2)) {
            return false;
         }
      }

      return true;
   }

   public static boolean b(Collection<?> var0, Collection<?> var1) {
      return var0.size() < var1.size() && a(var0, var1);
   }

   public static boolean c(Collection<?> var0, Collection<?> var1) {
      if (var0.size() != var1.size()) {
         return false;
      }

      fd.a var3;
      if ((var3 = new fd.a(var0, var1)).a.size() != var3.b.size()) {
         return false;
      }

      for (Object var2 : var3.a.keySet()) {
         if (var3.c(var2) != var3.d(var2)) {
            return false;
         }
      }

      return true;
   }

   private static <E> boolean a(Collection<? extends E> var0, Collection<? extends E> var1, final fg<? super E> var2) {
      if (var2 == null) {
         throw new NullPointerException("Equator must not be null.");
      }

      if (var0.size() != var1.size()) {
         return false;
      }

      fQ var3 = new fQ() {
         private fd.b<?> b(Object var1) {
            return new fd.b<>(var2, var1);
         }

         // $VF: synthetic method
         @Override
         public final Object a(Object var1) {
            Object var2x = var1;
            var1 = this;
            return new fd.b<>(var2, var2x);
         }
      };
      return c(a((Iterable<E>)var0, var3), a((Iterable<E>)var1, var3));
   }

   @Deprecated
   private static <O> int a(O var0, Iterable<? super O> var1) {
      if (var1 == null) {
         throw new NullPointerException("coll must not be null.");
      } else {
         return fp.b(var1, var0);
      }
   }

   @Deprecated
   private static <T> T b(Iterable<T> var0, fF<? super T> var1) {
      return var1 != null ? fp.b(var0, var1) : null;
   }

   @Deprecated
   private static <T, C extends fb<? super T>> C a(Iterable<T> var0, C var1) {
      if (var1 != null) {
         fp.a(var0, var1);
      }

      return (C)var1;
   }

   @Deprecated
   private static <T, C extends fb<? super T>> C a(Iterator<T> var0, C var1) {
      if (var1 != null) {
         fq.a(var0, var1);
      }

      return (C)var1;
   }

   @Deprecated
   private static <T, C extends fb<? super T>> T b(Iterable<T> var0, C var1) {
      return var1 != null ? fp.b(var0, var1) : null;
   }

   @Deprecated
   private static <T, C extends fb<? super T>> T b(Iterator<T> var0, C var1) {
      return var1 != null ? fq.b(var0, var1) : null;
   }

   public static <T> boolean a(Iterable<T> var0, fF<? super T> var1) {
      boolean var2 = false;
      if (var0 != null && var1 != null) {
         Iterator var3 = var0.iterator();

         while (var3.hasNext()) {
            if (!var1.a(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }
      }

      return var2;
   }

   private static <T> boolean c(Iterable<T> var0, fF<? super T> var1) {
      return a(var0, var1 == null ? null : hs.a(var1));
   }

   private static <C> void a(Collection<C> var0, fQ<? super C, ? extends C> var1) {
      if (var0 != null && var1 != null) {
         if (var0 instanceof List) {
            List var4;
            ListIterator var3 = (var4 = (List)var0).listIterator();

            while (var3.hasNext()) {
               var3.set(var1.a(var3.next()));
            }

            return;
         }

         Collection var2 = a((Iterable)var0, var1);
         var0.clear();
         var0.addAll(var2);
      }
   }

   @Deprecated
   private static <C> int d(Iterable<C> var0, fF<? super C> var1) {
      return var1 == null ? 0 : (int)fp.f(var0, var1);
   }

   @Deprecated
   private static <C> boolean e(Iterable<C> var0, fF<? super C> var1) {
      return var1 != null && fp.e(var0, var1);
   }

   @Deprecated
   private static <C> boolean f(Iterable<C> var0, fF<? super C> var1) {
      return var1 != null && fp.d(var0, var1);
   }

   private static <O> Collection<O> g(Iterable<? extends O> var0, fF<? super O> var1) {
      ArrayList var2 = var0 instanceof Collection ? new ArrayList(((Collection)var0).size()) : new ArrayList();
      return a(var0, var1, var2);
   }

   public static <O, R extends Collection<? super O>> R a(Iterable<? extends O> var0, fF<? super O> var1, R var2) {
      if (var0 != null && var1 != null) {
         for (Object var3 : var0) {
            if (var1.a(var3)) {
               var2.add(var3);
            }
         }
      }

      return (R)var2;
   }

   private static <O, R extends Collection<? super O>> R a(Iterable<? extends O> var0, fF<? super O> var1, R var2, R var3) {
      if (var0 != null && var1 != null) {
         for (Object var4 : var0) {
            if (var1.a(var4)) {
               var2.add(var4);
            } else {
               var3.add(var4);
            }
         }
      }

      return (R)var2;
   }

   private static <O> Collection<O> h(Iterable<? extends O> var0, fF<? super O> var1) {
      ArrayList var2 = var0 instanceof Collection ? new ArrayList(((Collection)var0).size()) : new ArrayList();
      return b(var0, var1, var2);
   }

   public static <O, R extends Collection<? super O>> R b(Iterable<? extends O> var0, fF<? super O> var1, R var2) {
      if (var0 != null && var1 != null) {
         for (Object var3 : var0) {
            if (!var1.a(var3)) {
               var2.add(var3);
            }
         }
      }

      return (R)var2;
   }

   private static <I, O> Collection<O> a(Iterable<I> var0, fQ<? super I, ? extends O> var1) {
      ArrayList var2 = var0 instanceof Collection ? new ArrayList(((Collection)var0).size()) : new ArrayList();
      return a(var0, var1, var2);
   }

   private static <I, O> Collection<O> a(Iterator<I> var0, fQ<? super I, ? extends O> var1) {
      return a(var0, var1, new ArrayList<>());
   }

   private static <I, O, R extends Collection<? super O>> R a(Iterable<? extends I> var0, fQ<? super I, ? extends O> var1, R var2) {
      return (R)(var0 != null ? a(var0.iterator(), var1, (R)var2) : var2);
   }

   private static <I, O, R extends Collection<? super O>> R a(Iterator<? extends I> var0, fQ<? super I, ? extends O> var1, R var2) {
      if (var0 != null && var1 != null) {
         while (var0.hasNext()) {
            Object var3 = var0.next();
            var3 = var1.a(var3);
            var2.add(var3);
         }
      }

      return (R)var2;
   }

   private static <T> boolean a(Collection<T> var0, T var1) {
      if (var0 == null) {
         throw new NullPointerException("The collection must not be null");
      } else {
         return var1 != null && var0.add(var1);
      }
   }

   public static <C> boolean a(Collection<C> var0, Iterable<? extends C> var1) {
      return var1 instanceof Collection ? var0.addAll((Collection)var1) : a(var0, var1.iterator());
   }

   public static <C> boolean a(Collection<C> var0, Iterator<? extends C> var1) {
      boolean var2 = false;

      while (var1.hasNext()) {
         var2 |= var0.add(var1.next());
      }

      return var2;
   }

   private static <C> boolean a(Collection<C> var0, Enumeration<? extends C> var1) {
      boolean var2 = false;

      while (var1.hasMoreElements()) {
         var2 |= var0.add(var1.nextElement());
      }

      return var2;
   }

   private static <C> boolean b(Collection<C> var0, C... var1) {
      boolean var2 = false;
      Object[] var6;
      int var3 = (var6 = var1).length;

      for (int var4 = 0; var4 < var3; var4++) {
         Object var5 = var6[var4];
         var2 |= var0.add(var5);
      }

      return var2;
   }

   @Deprecated
   private static <T> T a(Iterator<T> var0, int var1) {
      return fq.b(var0, var1);
   }

   static void a(int var0) {
      if (var0 < 0) {
         throw new IndexOutOfBoundsException("Index cannot be negative: " + var0);
      }
   }

   @Deprecated
   private static <T> T a(Iterable<T> var0, int var1) {
      return fp.a(var0, var1);
   }

   private static Object a(Object var0, int var1) {
      if ((var1 = var1) < 0) {
         throw new IndexOutOfBoundsException("Index cannot be negative: " + var1);
      }

      if (var0 instanceof Map) {
         Map var7;
         return fq.b((var7 = (Map)var0).entrySet().iterator(), var1);
      }

      if (var0 instanceof Object[]) {
         return ((Object[])var0)[var1];
      }

      if (var0 instanceof Iterator) {
         Iterator var6;
         return fq.b(var6 = (Iterator)var0, var1);
      }

      if (var0 instanceof Iterable) {
         Iterable var5;
         return fp.a(var5 = (Iterable)var0, var1);
      }

      if (var0 instanceof Enumeration) {
         Enumeration var3;
         Enumeration var10000 = var3 = (Enumeration)var0;
         var1 = var1;
         Enumeration var4 = var10000;
         int var10;
         a(var10 = var1);

         while (var4.hasMoreElements()) {
            if (--var10 == -1) {
               return var4.nextElement();
            }

            var4.nextElement();
         }

         throw new IndexOutOfBoundsException("Entry does not exist: " + var10);
      } else {
         if (var0 == null) {
            throw new IllegalArgumentException("Unsupported object type: null");
         }

         try {
            return Array.get(var0, var1);
         } catch (IllegalArgumentException var2) {
            throw new IllegalArgumentException("Unsupported object type: " + var0.getClass().getName());
         }
      }
   }

   private static <K, V> Entry<K, V> a(Map<K, V> var0, int var1) {
      a(var1);
      Set var10000 = var0.entrySet();
      var1 = var1;
      return fp.a(var10000, var1);
   }

   public static int a(Object var0) {
      if (var0 == null) {
         return 0;
      }

      int var1 = 0;
      if (var0 instanceof Map) {
         var1 = ((Map)var0).size();
      } else if (var0 instanceof Collection) {
         var1 = ((Collection)var0).size();
      } else if (var0 instanceof Iterable) {
         var1 = fp.f((Iterable<?>)var0);
      } else if (var0 instanceof Object[]) {
         var1 = ((Object[])var0).length;
      } else if (var0 instanceof Iterator) {
         var1 = fq.e((Iterator<?>)var0);
      } else if (var0 instanceof Enumeration) {
         Enumeration var3 = (Enumeration)var0;

         while (var3.hasMoreElements()) {
            var1++;
            var3.nextElement();
         }
      } else {
         try {
            var1 = Array.getLength(var0);
         } catch (IllegalArgumentException var2) {
            throw new IllegalArgumentException("Unsupported object type: " + var0.getClass().getName());
         }
      }

      return var1;
   }

   private static boolean b(Object var0) {
      if (var0 == null) {
         return true;
      }

      if (var0 instanceof Collection) {
         return ((Collection)var0).isEmpty();
      }

      if (var0 instanceof Iterable) {
         return fp.e((Iterable<?>)var0);
      }

      if (var0 instanceof Map) {
         return ((Map)var0).isEmpty();
      }

      if (var0 instanceof Object[]) {
         return ((Object[])var0).length == 0;
      }

      if (var0 instanceof Iterator) {
         return !((Iterator)var0).hasNext();
      }

      if (var0 instanceof Enumeration) {
         return !((Enumeration)var0).hasMoreElements();
      }

      try {
         return Array.getLength(var0) == 0;
      } catch (IllegalArgumentException var1) {
         throw new IllegalArgumentException("Unsupported object type: " + var0.getClass().getName());
      }
   }

   private static boolean b(Collection<?> var0) {
      return var0 == null || var0.isEmpty();
   }

   private static boolean c(Collection<?> var0) {
      Collection var1;
      return (var1 = var0) != null && !var1.isEmpty();
   }

   private static void a(Object[] var0) {
      int var1 = 0;

      for (int var2 = var0.length - 1; var2 > var1; var1++) {
         Object var3 = var0[var2];
         var0[var2] = var0[var1];
         var0[var1] = var3;
         var2--;
      }
   }

   private static boolean d(Collection<? extends Object> var0) {
      if (var0 == null) {
         throw new NullPointerException("The collection must not be null");
      }

      if (var0 instanceof eZ) {
         return ((eZ)var0).a();
      }

      try {
         return gB.b(var0).a();
      } catch (IllegalArgumentException var1) {
         return false;
      }
   }

   private static int e(Collection<? extends Object> var0) {
      if (var0 == null) {
         throw new NullPointerException("The collection must not be null");
      }

      if (var0 instanceof eZ) {
         return ((eZ)var0).b();
      }

      try {
         return gB.b(var0).b();
      } catch (IllegalArgumentException var1) {
         return -1;
      }
   }

   private static <O extends Comparable<? super O>> List<O> e(Iterable<? extends O> var0, Iterable<? extends O> var1) {
      return a(var0, var1, fe.a(), true);
   }

   private static <O extends Comparable<? super O>> List<O> a(Iterable<? extends O> var0, Iterable<? extends O> var1, boolean var2) {
      return a(var0, var1, fe.a(), var2);
   }

   private static <O> List<O> a(Iterable<? extends O> var0, Iterable<? extends O> var1, Comparator<? super O> var2) {
      return a(var0, var1, var2, true);
   }

   private static <O> List<O> a(Iterable<? extends O> var0, Iterable<? extends O> var1, Comparator<? super O> var2, boolean var3) {
      if (var0 != null && var1 != null) {
         if (var2 == null) {
            throw new NullPointerException("The comparator must not be null");
         }

         int var4 = var0 instanceof Collection && var1 instanceof Collection ? Math.max(1, ((Collection)var0).size() + var1.size()) : 10;
         hV var5 = new hV(var2, var0.iterator(), var1.iterator());
         if (var3) {
            return fq.a(var5, var4);
         }

         var1 = new ArrayList(var4);
         Object var7 = null;

         while (var5.hasNext()) {
            Object var8 = var5.next();
            if (var7 == null || !var7.equals(var8)) {
               var1.add(var8);
            }

            var7 = var8;
         }

         var1.trimToSize();
         return var1;
      } else {
         throw new NullPointerException("The collections must not be null");
      }
   }

   private static <E> Collection<List<E>> f(Collection<E> var0) {
      ir var2 = new ir(var0);
      ArrayList var1 = new ArrayList();

      while (var2.hasNext()) {
         var1.add(var2.a());
      }

      return var1;
   }

   private static <C> Collection<C> f(Collection<C> var0, Collection<?> var1) {
      var1 = var1;
      var0 = var0;
      ArrayList var2 = new ArrayList(Math.min(var0.size(), var1.size()));

      for (Object var3 : var0) {
         if (var1.contains(var3)) {
            var2.add(var3);
         }
      }

      return var2;
   }

   private static <E> Collection<E> a(Iterable<E> var0, Iterable<? extends E> var1, final fg<? super E> var2) {
      fQ var3 = new fQ<E, fd.b<E>>() {
         private fd.b<E> b(E var1) {
            return new fd.b<>(var2, (E)var1);
         }

         // $VF: synthetic method
         @Override
         public final Object a(Object var1) {
            Object var2x = var1;
            var1 = this;
            return new fd.b<>(var2, var2x);
         }
      };
      var1 = a(var1, var3, new HashSet());
      ArrayList var7 = new ArrayList();

      for (Object var4 : var0) {
         if (var1.contains(new fd.b<>(var2, var4))) {
            var7.add(var4);
         }
      }

      return var7;
   }

   private static <E> Collection<E> g(Collection<E> var0, Collection<?> var1) {
      var1 = var1;
      var0 = var0;
      ArrayList var2 = new ArrayList();

      for (Object var3 : var0) {
         if (!var1.contains(var3)) {
            var2.add(var3);
         }
      }

      return var2;
   }

   private static <E> Collection<E> b(Iterable<E> var0, Iterable<? extends E> var1, final fg<? super E> var2) {
      fQ var3 = new fQ<E, fd.b<E>>() {
         private fd.b<E> b(E var1) {
            return new fd.b<>(var2, (E)var1);
         }

         // $VF: synthetic method
         @Override
         public final Object a(Object var1) {
            Object var2x = var1;
            var1 = this;
            return new fd.b<>(var2, var2x);
         }
      };
      var1 = a(var1, var3, new HashSet());
      ArrayList var7 = new ArrayList();

      for (Object var4 : var0) {
         if (!var1.contains(new fd.b<>(var2, var4))) {
            var7.add(var4);
         }
      }

      return var7;
   }

   @Deprecated
   private static <C> Collection<C> g(Collection<C> var0) {
      return gz.a(var0);
   }

   @Deprecated
   private static <C> Collection<C> h(Collection<? extends C> var0) {
      return gC.b(var0);
   }

   private static <C> Collection<C> a(Collection<C> var0, fF<? super C> var1) {
      return gy.a(var0, var1);
   }

   private static <E> Collection<E> b(Collection<E> var0, fQ<? super E, ? extends E> var1) {
      return gA.a(var0, var1);
   }

   private static <E> E i(Collection<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Collection must not be null.");
      } else if (var0.size() != 1) {
         throw new IllegalArgumentException("Can extract singleton only when collection size == 1");
      } else {
         return (E)var0.iterator().next();
      }
   }

   static class a<O> {
      final Map<O, Integer> a;
      final Map<O, Integer> b;

      public a(Iterable<? extends O> var1, Iterable<? extends O> var2) {
         this.a = fd.a(var1);
         this.b = fd.a(var2);
      }

      public final int a(Object var1) {
         return Math.max(this.c(var1), this.d(var1));
      }

      public final int b(Object var1) {
         return Math.min(this.c(var1), this.d(var1));
      }

      public final int c(Object var1) {
         return a(var1, this.a);
      }

      public final int d(Object var1) {
         return a(var1, this.b);
      }

      private static int a(Object var0, Map<?, Integer> var1) {
         Integer var2;
         return (var2 = (Integer)var1.get(var0)) != null ? var2 : 0;
      }
   }

   static class b<O> {
      private final fg<? super O> a;
      private final O b;

      public b(fg<? super O> var1, O var2) {
         this.a = var1;
         this.b = (O)var2;
      }

      private O a() {
         return this.b;
      }

      @Override
      public final boolean equals(Object obj) {
         if (!(obj instanceof fd.b)) {
            return false;
         }

         obj = (fd.b)obj;
         return this.a.a(this.b, ((fd.b)obj).b);
      }

      @Override
      public final int hashCode() {
         return this.a.a(this.b);
      }
   }

   static class c<O> extends fd.a<O> implements Iterable<O> {
      private final Set<O> d = new HashSet<>();
      final List<O> c;

      public c(Iterable<? extends O> var1, Iterable<? extends O> var2) {
         super(var1, var2);
         fd.a(this.d, var1);
         fd.a(this.d, var2);
         this.c = new ArrayList<>(this.d.size());
      }

      @Override
      public final Iterator<O> iterator() {
         return this.d.iterator();
      }

      public final void a(O var1, int var2) {
         for (int var3 = 0; var3 < var2; var3++) {
            this.c.add((O)var1);
         }
      }

      public final Collection<O> a() {
         return this.c;
      }
   }
}
