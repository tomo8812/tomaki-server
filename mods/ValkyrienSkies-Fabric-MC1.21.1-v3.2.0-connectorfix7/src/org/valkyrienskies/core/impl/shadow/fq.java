package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class fq {
   public static final fJ a = hW.a;
   private static fK b;
   private static fC c;
   private static fu d;
   private static fE e;
   private static final String f = "[";
   private static final String g = "]";
   private static final String h = ", ";

   private fq() {
   }

   public static <E> fJ<E> a() {
      return hW.a();
   }

   private static <E> fK<E> b() {
      return hX.a();
   }

   private static <E> fC<E> c() {
      return hZ.a();
   }

   private static <K, V> fu<K, V> d() {
      return hY.d();
   }

   private static <K, V> fE<K, V> e() {
      return ia.d();
   }

   private static <E> fJ<E> a(E var0) {
      return new iu<>((E)var0);
   }

   private static <E> ListIterator<E> b(E var0) {
      return new iv<>((E)var0);
   }

   private static <E> fJ<E> a(E... var0) {
      return new in<>((E[])var0);
   }

   private static <E> fJ<E> c(Object var0) {
      return new hS<>(var0);
   }

   private static <E> fJ<E> a(E[] var0, int var1) {
      return new in<>((E[])var0, var1);
   }

   private static <E> fJ<E> a(Object var0, int var1) {
      return new hS<>(var0, var1);
   }

   private static <E> fJ<E> a(E[] var0, int var1, int var2) {
      return new in<>((E[])var0, var1, var2);
   }

   private static <E> fJ<E> a(Object var0, int var1, int var2) {
      return new hS<>(var0, var1, var2);
   }

   private static <E> fK<E> b(E... var0) {
      return new io<>((E[])var0);
   }

   private static <E> fK<E> d(Object var0) {
      return new hT<>(var0);
   }

   private static <E> fK<E> b(E[] var0, int var1) {
      return new io<>((E[])var0, var1);
   }

   private static <E> fK<E> b(Object var0, int var1) {
      return new hT<>(var0, var1);
   }

   private static <E> fK<E> b(E[] var0, int var1, int var2) {
      return new io<>((E[])var0, var1, var2);
   }

   private static <E> fK<E> b(Object var0, int var1, int var2) {
      return new hT<>(var0, var1, var2);
   }

   public static <E> hU<E> a(Iterator<? extends E> var0, long var1) {
      long var6 = var1;
      long var4 = 0L;
      var0 = var0;
      return new hU<>(var0, 0L, var6);
   }

   private static <E> hU<E> a(Iterator<? extends E> var0, long var1, long var3) {
      return new hU<>(var0, 0L, var3);
   }

   public static <E> Iterator<E> a(Iterator<E> var0) {
      return iz.a(var0);
   }

   private static <E> ListIterator<E> a(ListIterator<E> var0) {
      return iA.a(var0);
   }

   private static <K, V> fu<K, V> a(fu<K, V> var0) {
      return iB.a(var0);
   }

   public static <E> Iterator<E> a(Iterator<? extends E> var0, Iterator<? extends E> var1) {
      return new if<>(var0, var1);
   }

   private static <E> Iterator<E> b(Iterator<? extends E>... var0) {
      return new if<>(var0);
   }

   private static <E> Iterator<E> a(Collection<Iterator<? extends E>> var0) {
      return new if<>(var0);
   }

   public static <E> Iterator<E> a(Comparator<? super E> var0, Iterator<? extends E> var1, Iterator<? extends E> var2) {
      var0 = var0 == null ? fe.a : var0;
      return new hV<>(var0, var1, var2);
   }

   private static <E> Iterator<E> a(Comparator<? super E> var0, Iterator<? extends E>... var1) {
      var0 = var0 == null ? fe.a : var0;
      return new hV<>(var0, var1);
   }

   private static <E> Iterator<E> a(Comparator<? super E> var0, Collection<Iterator<? extends E>> var1) {
      var0 = var0 == null ? fe.a : var0;
      return new hV<>(var0, var1);
   }

   private static <E> Iterator<E> a(E var0, fQ<? super E, ? extends E> var1) {
      return new ip<>((E)var0, var1);
   }

   public static <I, O> Iterator<O> a(Iterator<? extends I> var0, fQ<? super I, ? extends O> var1) {
      if (var0 == null) {
         throw new NullPointerException("Iterator must not be null");
      } else if (var1 == null) {
         throw new NullPointerException("Transformer must not be null");
      } else {
         return new ix<>(var0, var1);
      }
   }

   public static <E> Iterator<E> a(Iterator<? extends E> var0, fF<? super E> var1) {
      if (var0 == null) {
         throw new NullPointerException("Iterator must not be null");
      } else if (var1 == null) {
         throw new NullPointerException("Predicate must not be null");
      } else {
         return new id<>(var0, var1);
      }
   }

   private static <E> ListIterator<E> a(ListIterator<? extends E> var0, fF<? super E> var1) {
      if (var0 == null) {
         throw new NullPointerException("ListIterator must not be null");
      } else if (var1 == null) {
         throw new NullPointerException("Predicate must not be null");
      } else {
         return new ie<>(var0, var1);
      }
   }

   private static <E> fJ<E> b(Collection<? extends E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Collection must not be null");
      } else {
         return new ik<>(var0);
      }
   }

   private static <E> fK<E> a(List<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("List must not be null");
      } else {
         return new il<>(var0);
      }
   }

   private static im a(NodeList var0) {
      if (var0 == null) {
         throw new NullPointerException("NodeList must not be null");
      } else {
         return new im(var0);
      }
   }

   private static im a(Node var0) {
      if (var0 == null) {
         throw new NullPointerException("Node must not be null");
      } else {
         return new im(var0);
      }
   }

   private static <E> Iterator<E> g(Iterator<? extends E> var0) {
      return iq.a(var0);
   }

   private static <E> Iterator<E> h(Iterator<? extends E> var0) {
      return is.a(var0);
   }

   public static <E> iw<E> b(Iterator<E> var0, long var1) {
      return new iw<>(var0, var1);
   }

   public static <E> iD<E> b(Iterator<? extends E> var0, Iterator<? extends E> var1) {
      return new iD<>(var0, var1);
   }

   private static <E> iD<E> a(Iterator<? extends E> var0, Iterator<? extends E> var1, Iterator<? extends E> var2) {
      return new iD<>(var0, var1, var2);
   }

   public static <E> iD<E> a(Iterator<? extends E>... var0) {
      return new iD<>(var0);
   }

   private static <E> Iterator<E> a(Enumeration<? extends E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Enumeration must not be null");
      } else {
         return new ic<>(var0);
      }
   }

   private static <E> Iterator<E> a(Enumeration<? extends E> var0, Collection<? super E> var1) {
      if (var0 == null) {
         throw new NullPointerException("Enumeration must not be null");
      } else if (var1 == null) {
         throw new NullPointerException("Collection must not be null");
      } else {
         return new ic<>(var0, var1);
      }
   }

   public static <E> Enumeration<E> b(Iterator<? extends E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Iterator must not be null");
      } else {
         return new ig<>(var0);
      }
   }

   public static <E> Iterable<E> a(Iterator<? extends E> var0) {
      return new ih<>(var0, false);
   }

   private static <E> Iterable<E> i(Iterator<? extends E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Iterator must not be null");
      } else {
         return new ih<>(var0, true);
      }
   }

   private static <E> ListIterator<E> j(Iterator<? extends E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Iterator must not be null");
      } else {
         return new ij<>(var0);
      }
   }

   private static Object[] k(Iterator<?> var0) {
      if (var0 == null) {
         throw new NullPointerException("Iterator must not be null");
      } else {
         return a(var0, 100).toArray();
      }
   }

   public static <E> E[] a(Iterator<? extends E> var0, Class<E> var1) {
      if (var0 == null) {
         throw new NullPointerException("Iterator must not be null");
      }

      if (var1 == null) {
         throw new NullPointerException("Array class must not be null");
      }

      List var2 = a(var0, 100);
      Object[] var3 = (Object[])Array.newInstance(var1, var2.size());
      return (E[])var2.toArray(var3);
   }

   public static <E> List<E> c(Iterator<? extends E> var0) {
      return a(var0, 10);
   }

   public static <E> List<E> a(Iterator<? extends E> var0, int var1) {
      if (var0 == null) {
         throw new NullPointerException("Iterator must not be null");
      }

      if (var1 <= 0) {
         throw new IllegalArgumentException("Estimated size must be greater than 0");
      }

      ArrayList var2 = new ArrayList(var1);

      while (var0.hasNext()) {
         var2.add(var0.next());
      }

      return var2;
   }

   private static Iterator<?> e(Object var0) {
      if (var0 == null) {
         return hW.a();
      }

      if (var0 instanceof Iterator) {
         return (Iterator<?>)var0;
      }

      if (var0 instanceof Iterable) {
         return ((Iterable)var0).iterator();
      }

      if (var0 instanceof Object[]) {
         return new in((Object[])var0);
      }

      if (var0 instanceof Enumeration) {
         return new ic((Enumeration)var0);
      }

      if (var0 instanceof Map) {
         return ((Map)var0).values().iterator();
      }

      if (var0 instanceof NodeList) {
         return new im((NodeList)var0);
      }

      if (var0 instanceof Node) {
         return new im((Node)var0);
      }

      if (var0 instanceof Dictionary) {
         return new ic(((Dictionary)var0).elements());
      }

      if (var0.getClass().isArray()) {
         return new hS(var0);
      }

      try {
         Method var1 = var0.getClass().getMethod("iterator", null);
         Iterator var7;
         if (Iterator.class.isAssignableFrom(var1.getReturnType()) && (var7 = (Iterator)var1.invoke(var0, null)) != null) {
            return var7;
         }
      } catch (RuntimeException var2) {
      } catch (NoSuchMethodException var3) {
      } catch (IllegalAccessException var4) {
      } catch (InvocationTargetException var5) {
      }

      var0 = var0;
      return new iu<>(var0);
   }

   public static <E> void a(Iterator<E> var0, fb<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException("Closure must not be null");
      }

      if (var0 != null) {
         while (var0.hasNext()) {
            Object var2 = var0.next();
            var1.a(var2);
         }
      }
   }

   public static <E> E b(Iterator<E> var0, fb<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException("Closure must not be null.");
      }

      if (var0 != null) {
         while (var0.hasNext()) {
            Object var2 = var0.next();
            if (!var0.hasNext()) {
               return (E)var2;
            }

            var1.a(var2);
         }
      }

      return null;
   }

   public static <E> E b(Iterator<E> var0, fF<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException("Predicate must not be null");
      }

      if (var0 != null) {
         while (var0.hasNext()) {
            Object var2 = var0.next();
            if (var1.a(var2)) {
               return (E)var2;
            }
         }
      }

      return null;
   }

   public static <E> int c(Iterator<E> var0, fF<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException("Predicate must not be null");
      }

      if (var0 != null) {
         for (int var2 = 0; var0.hasNext(); var2++) {
            Object var3 = var0.next();
            if (var1.a(var3)) {
               return var2;
            }
         }
      }

      return -1;
   }

   public static <E> boolean d(Iterator<E> var0, fF<? super E> var1) {
      return c(var0, var1) != -1;
   }

   public static <E> boolean e(Iterator<E> var0, fF<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException("Predicate must not be null");
      }

      if (var0 != null) {
         while (var0.hasNext()) {
            Object var2 = var0.next();
            if (!var1.a(var2)) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean d(Iterator<?> var0) {
      return var0 == null || !var0.hasNext();
   }

   public static <E> boolean a(Iterator<E> var0, Object var1) {
      return d(var0, gX.b(var1));
   }

   public static <E> E b(Iterator<E> var0, int var1) {
      int var2;
      fd.a(var2 = var1);

      while (var0.hasNext()) {
         if (--var2 == -1) {
            return (E)var0.next();
         }

         var0.next();
      }

      throw new IndexOutOfBoundsException("Entry does not exist: " + var2);
   }

   private static <E> E l(Iterator<E> var0) {
      return b(var0, 0);
   }

   public static int e(Iterator<?> var0) {
      int var1 = 0;
      if (var0 != null) {
         while (var0.hasNext()) {
            var0.next();
            var1++;
         }
      }

      return var1;
   }

   public static <E> String f(Iterator<E> var0) {
      return a(var0, hC.a(), ", ", "[", "]");
   }

   public static <E> String b(Iterator<E> var0, fQ<? super E, String> var1) {
      return a(var0, var1, ", ", "[", "]");
   }

   public static <E> String a(Iterator<E> var0, fQ<? super E, String> var1, String var2, String var3, String var4) {
      if (var1 == null) {
         throw new NullPointerException("transformer may not be null");
      }

      if (var2 == null) {
         throw new NullPointerException("delimiter may not be null");
      }

      if (var3 == null) {
         throw new NullPointerException("prefix may not be null");
      }

      if (var4 == null) {
         throw new NullPointerException("suffix may not be null");
      }

      StringBuilder var5 = new StringBuilder(var3);
      if (var0 != null) {
         while (var0.hasNext()) {
            Object var6 = var0.next();
            var5.append((String)var1.a(var6));
            var5.append(var2);
         }

         if (var5.length() > var3.length()) {
            var5.setLength(var5.length() - var2.length());
         }
      }

      var5.append(var4);
      return var5.toString();
   }
}
