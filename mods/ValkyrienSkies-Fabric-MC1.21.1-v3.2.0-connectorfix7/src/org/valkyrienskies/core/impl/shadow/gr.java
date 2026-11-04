package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class gr<K extends Comparable<K>, V extends Comparable<V>> implements Serializable, fB<K, V> {
   private static final long a = 721969328361807L;
   private transient gr.h<K, V>[] b;
   private transient int c = 0;
   private transient int d = 0;
   private transient Set<K> e;
   private transient Set<V> f;
   private transient Set<Entry<K, V>> g;
   private transient gr<K, V>.c h = null;

   public gr() {
      this.b = new gr.h[2];
   }

   private gr(Map<? extends K, ? extends V> var1) {
      this();
      this.putAll(var1);
   }

   @Override
   public final int size() {
      return this.c;
   }

   @Override
   public final boolean isEmpty() {
      return this.c == 0;
   }

   @Override
   public final boolean containsKey(Object key) {
      c(key, gr.a.KEY);
      return this.l(key) != null;
   }

   @Override
   public final boolean containsValue(Object value) {
      c(value, gr.a.VALUE);
      return this.m(value) != null;
   }

   public final V e(Object var1) {
      c(var1, gr.a.KEY);
      gr.h var3;
      return (var3 = this.l(var1)) == null ? null : var3.b;
   }

   private V a(K var1, V var2) {
      Comparable var3 = this.e(var1);
      this.b((K)var1, (V)var2);
      return (V)var3;
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> map) {
      for (Entry var2 : map.entrySet()) {
         this.a((K)var2.getKey(), (V)var2.getValue());
      }
   }

   public final V f(Object var1) {
      return this.i(var1);
   }

   @Override
   public final void clear() {
      this.h();
      this.c = 0;
      this.b[gr.a.KEY.ordinal()] = null;
      this.b[gr.a.VALUE.ordinal()] = null;
   }

   public final K g(Object var1) {
      c(var1, gr.a.VALUE);
      gr.h var3;
      return (var3 = this.m(var1)) == null ? null : var3.a;
   }

   public final K h(Object var1) {
      return this.j(var1);
   }

   private K f() {
      if (this.c == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return c(this.b[gr.a.KEY.ordinal()], gr.a.KEY).a;
      }
   }

   private K g() {
      if (this.c == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return d(this.b[gr.a.KEY.ordinal()], gr.a.KEY).a;
      }
   }

   private K a(K var1) {
      c(var1, gr.a.KEY);
      gr.h var3;
      return (var3 = this.a(this.l(var1), gr.a.KEY)) == null ? null : var3.a;
   }

   private K b(K var1) {
      c(var1, gr.a.KEY);
      gr.h var3;
      return (var3 = this.b(this.l(var1), gr.a.KEY)) == null ? null : var3.a;
   }

   @Override
   public final Set<K> keySet() {
      if (this.e == null) {
         this.e = new gr.g(gr.a.KEY);
      }

      return this.e;
   }

   @Override
   public final Set<V> b() {
      if (this.f == null) {
         this.f = new gr.i(gr.a.KEY);
      }

      return this.f;
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      if (this.g == null) {
         this.g = new gr.b();
      }

      return this.g;
   }

   @Override
   public final fE<K, V> e() {
      return this.isEmpty() ? ia.d() : new gr.m(gr.a.KEY);
   }

   @Override
   public final fB<V, K> d() {
      if (this.h == null) {
         this.h = new gr.c();
      }

      return this.h;
   }

   @Override
   public final boolean equals(Object obj) {
      return this.d(obj, gr.a.KEY);
   }

   @Override
   public final int hashCode() {
      return this.a(gr.a.KEY);
   }

   @Override
   public final String toString() {
      return this.b(gr.a.KEY);
   }

   private void b(K var1, V var2) {
      a((Object)var1, (Object)var2);
      this.i(var1);
      this.j(var2);
      gr.h var3;
      if ((var3 = this.b[gr.a.KEY.ordinal()]) == null) {
         gr.h var8 = new gr.h((K)var1, (V)var2);
         this.b[gr.a.KEY.ordinal()] = var8;
         this.b[gr.a.VALUE.ordinal()] = var8;
         this.i();
      } else {
         while (true) {
            Comparable var5 = var3.a;
            int var4;
            if ((var4 = var1.compareTo(var5)) == 0) {
               throw new IllegalArgumentException("Cannot store a duplicate key (\"" + var1 + "\") in this Map");
            }

            if (var4 < 0) {
               if (gr.h.a(var3, gr.a.KEY) == null) {
                  gr.h var6 = new gr.h((K)var1, (V)var2);
                  this.b(var6);
                  gr.h.a(var3, var6, gr.a.KEY);
                  gr.h.b(var6, var3, gr.a.KEY);
                  this.o(var6, gr.a.KEY);
                  this.i();
                  return;
               }

               var3 = gr.h.a(var3, gr.a.KEY);
            } else {
               if (gr.h.b(var3, gr.a.KEY) == null) {
                  gr.h var7 = new gr.h((K)var1, (V)var2);
                  this.b(var7);
                  gr.h.c(var3, var7, gr.a.KEY);
                  gr.h.b(var7, var3, gr.a.KEY);
                  this.o(var7, gr.a.KEY);
                  this.i();
                  return;
               }

               var3 = gr.h.b(var3, gr.a.KEY);
            }
         }
      }
   }

   V i(Object var1) {
      if ((var1 = this.l(var1)) == null) {
         return null;
      }

      this.a(var1);
      return var1.b;
   }

   K j(Object var1) {
      if ((var1 = this.m(var1)) == null) {
         return null;
      }

      this.a(var1);
      return var1.a;
   }

   private <T extends Comparable<T>> gr.h<K, V> b(Object var1, gr.a var2) {
      gr.h var3 = null;
      gr.h var4 = this.b[var2.ordinal()];

      while (var4 != null) {
         Comparable var10000 = (Comparable)var1;
         Comparable var6 = (Comparable)gr.h.c(var4, var2);
         int var5;
         if ((var5 = var10000.compareTo(var6)) == 0) {
            var3 = var4;
            break;
         }

         var4 = var5 < 0 ? gr.h.a(var4, var2) : gr.h.b(var4, var2);
      }

      return var3;
   }

   private gr.h<K, V> l(Object var1) {
      return this.b(var1, gr.a.KEY);
   }

   private gr.h<K, V> m(Object var1) {
      return this.b(var1, gr.a.VALUE);
   }

   private gr.h<K, V> a(gr.h<K, V> var1, gr.a var2) {
      if (var1 == null) {
         var1 = null;
      } else if (gr.h.b(var1, var2) != null) {
         var1 = c(gr.h.b(var1, var2), var2);
      } else {
         gr.h var3 = gr.h.d(var1, var2);

         for (gr.h var5 = var1; var3 != null && var5 == gr.h.b(var3, var2); var3 = gr.h.d(var3, var2)) {
            var5 = var3;
         }

         var1 = var3;
      }

      return var1;
   }

   private gr.h<K, V> b(gr.h<K, V> var1, gr.a var2) {
      if (var1 == null) {
         var1 = null;
      } else if (gr.h.a(var1, var2) != null) {
         var1 = d(gr.h.a(var1, var2), var2);
      } else {
         gr.h var3 = gr.h.d(var1, var2);

         for (gr.h var5 = var1; var3 != null && var5 == gr.h.a(var3, var2); var3 = gr.h.d(var3, var2)) {
            var5 = var3;
         }

         var1 = var3;
      }

      return var1;
   }

   private static <T extends Comparable<T>> int c(T var0, T var1) {
      return var0.compareTo(var1);
   }

   private static gr.h<K, V> c(gr.h<K, V> var0, gr.a var1) {
      if ((var0 = var0) != null) {
         while (gr.h.a(var0, var1) != null) {
            var0 = gr.h.a(var0, var1);
         }
      }

      return var0;
   }

   private static gr.h<K, V> d(gr.h<K, V> var0, gr.a var1) {
      if ((var0 = var0) != null) {
         while (gr.h.b(var0, var1) != null) {
            var0 = gr.h.b(var0, var1);
         }
      }

      return var0;
   }

   private static void a(gr.h<K, V> var0, gr.h<K, V> var1, gr.a var2) {
      if (var1 != null) {
         if (var0 == null) {
            gr.h.e(var1, var2);
            return;
         }

         gr.h.d(var1, var0, var2);
      }
   }

   private static boolean e(gr.h<?, ?> var0, gr.a var1) {
      return var0 != null && gr.h.f(var0, var1);
   }

   private static boolean f(gr.h<?, ?> var0, gr.a var1) {
      return var0 == null || gr.h.g(var0, var1);
   }

   private static void g(gr.h<?, ?> var0, gr.a var1) {
      if (var0 != null) {
         gr.h.h(var0, var1);
      }
   }

   private static void h(gr.h<?, ?> var0, gr.a var1) {
      if (var0 != null) {
         gr.h.e(var0, var1);
      }
   }

   private gr.h<K, V> i(gr.h<K, V> var1, gr.a var2) {
      return j(j(var1, var2), var2);
   }

   private static gr.h<K, V> j(gr.h<K, V> var0, gr.a var1) {
      return var0 == null ? null : gr.h.d(var0, var1);
   }

   private static gr.h<K, V> k(gr.h<K, V> var0, gr.a var1) {
      return var0 == null ? null : gr.h.b(var0, var1);
   }

   private static gr.h<K, V> l(gr.h<K, V> var0, gr.a var1) {
      return var0 == null ? null : gr.h.a(var0, var1);
   }

   private void m(gr.h<K, V> var1, gr.a var2) {
      gr.h var3 = gr.h.b(var1, var2);
      gr.h.c(var1, gr.h.a(var3, var2), var2);
      if (gr.h.a(var3, var2) != null) {
         gr.h.b(gr.h.a(var3, var2), var1, var2);
      }

      gr.h.b(var3, gr.h.d(var1, var2), var2);
      if (gr.h.d(var1, var2) == null) {
         this.b[var2.ordinal()] = var3;
      } else if (gr.h.a(gr.h.d(var1, var2), var2) == var1) {
         gr.h.a(gr.h.d(var1, var2), var3, var2);
      } else {
         gr.h.c(gr.h.d(var1, var2), var3, var2);
      }

      gr.h.a(var3, var1, var2);
      gr.h.b(var1, var3, var2);
   }

   private void n(gr.h<K, V> var1, gr.a var2) {
      gr.h var3 = gr.h.a(var1, var2);
      gr.h.a(var1, gr.h.b(var3, var2), var2);
      if (gr.h.b(var3, var2) != null) {
         gr.h.b(gr.h.b(var3, var2), var1, var2);
      }

      gr.h.b(var3, gr.h.d(var1, var2), var2);
      if (gr.h.d(var1, var2) == null) {
         this.b[var2.ordinal()] = var3;
      } else if (gr.h.b(gr.h.d(var1, var2), var2) == var1) {
         gr.h.c(gr.h.d(var1, var2), var3, var2);
      } else {
         gr.h.a(gr.h.d(var1, var2), var3, var2);
      }

      gr.h.c(var3, var1, var2);
      gr.h.b(var1, var3, var2);
   }

   private void o(gr.h<K, V> var1, gr.a var2) {
      gr.h var4;
      g(var4 = var1, var2);

      while (var4 != null && var4 != this.b[var2.ordinal()] && e(gr.h.d(var4, var2), var2)) {
         if (gr.h.i(var4, var2)) {
            gr.h var3;
            if (e(var3 = k(this.i(var4, var2), var2), var2)) {
               h(j(var4, var2), var2);
               h(var3, var2);
               g(this.i(var4, var2), var2);
               var4 = this.i(var4, var2);
            } else {
               if (gr.h.j(var4, var2)) {
                  var4 = j(var4, var2);
                  this.m(var4, var2);
               }

               h(j(var4, var2), var2);
               g(this.i(var4, var2), var2);
               if (this.i(var4, var2) != null) {
                  this.n(this.i(var4, var2), var2);
               }
            }
         } else {
            gr.h var5;
            if (e(var5 = l(this.i(var4, var2), var2), var2)) {
               h(j(var4, var2), var2);
               h(var5, var2);
               g(this.i(var4, var2), var2);
               var4 = this.i(var4, var2);
            } else {
               if (gr.h.i(var4, var2)) {
                  var4 = j(var4, var2);
                  this.n(var4, var2);
               }

               h(j(var4, var2), var2);
               g(this.i(var4, var2), var2);
               if (this.i(var4, var2) != null) {
                  this.m(this.i(var4, var2), var2);
               }
            }
         }
      }

      h(this.b[var2.ordinal()], var2);
   }

   private void a(gr.h<K, V> var1) {
      gr.a[] var2;
      int var3 = (var2 = gr.a.values()).length;

      for (int var4 = 0; var4 < var3; var4++) {
         gr.a var5 = var2[var4];
         if (gr.h.a(var1, var5) != null && gr.h.b(var1, var5) != null) {
            this.b(this.a(var1, var5), var1, var5);
         }

         gr.h var6;
         if ((var6 = gr.h.a(var1, var5) != null ? gr.h.a(var1, var5) : gr.h.b(var1, var5)) != null) {
            gr.h.b(var6, gr.h.d(var1, var5), var5);
            if (gr.h.d(var1, var5) == null) {
               this.b[var5.ordinal()] = var6;
            } else if (var1 == gr.h.a(gr.h.d(var1, var5), var5)) {
               gr.h.a(gr.h.d(var1, var5), var6, var5);
            } else {
               gr.h.c(gr.h.d(var1, var5), var6, var5);
            }

            gr.h.a(var1, null, var5);
            gr.h.c(var1, null, var5);
            gr.h.b(var1, null, var5);
            if (f(var1, var5)) {
               this.p(var6, var5);
            }
         } else if (gr.h.d(var1, var5) == null) {
            this.b[var5.ordinal()] = null;
         } else {
            if (f(var1, var5)) {
               this.p(var1, var5);
            }

            if (gr.h.d(var1, var5) != null) {
               if (var1 == gr.h.a(gr.h.d(var1, var5), var5)) {
                  gr.h.a(gr.h.d(var1, var5), null, var5);
               } else {
                  gr.h.c(gr.h.d(var1, var5), null, var5);
               }

               gr.h.b(var1, null, var5);
            }
         }
      }

      this.j();
   }

   private void p(gr.h<K, V> var1, gr.a var2) {
      var1 = var1;

      while (var1 != this.b[var2.ordinal()] && f(var1, var2)) {
         if (gr.h.i(var1, var2)) {
            gr.h var5;
            if (e(var5 = k(j(var1, var2), var2), var2)) {
               h(var5, var2);
               g(j(var1, var2), var2);
               this.m(j(var1, var2), var2);
               var5 = k(j(var1, var2), var2);
            }

            if (f(l(var5, var2), var2) && f(k(var5, var2), var2)) {
               g(var5, var2);
               var1 = j(var1, var2);
            } else {
               if (f(k(var5, var2), var2)) {
                  h(l(var5, var2), var2);
                  g(var5, var2);
                  this.n(var5, var2);
                  var5 = k(j(var1, var2), var2);
               }

               a(j(var1, var2), var5, var2);
               h(j(var1, var2), var2);
               h(k(var5, var2), var2);
               this.m(j(var1, var2), var2);
               var1 = this.b[var2.ordinal()];
            }
         } else {
            gr.h var3;
            if (e(var3 = l(j(var1, var2), var2), var2)) {
               h(var3, var2);
               g(j(var1, var2), var2);
               this.n(j(var1, var2), var2);
               var3 = l(j(var1, var2), var2);
            }

            if (f(k(var3, var2), var2) && f(l(var3, var2), var2)) {
               g(var3, var2);
               var1 = j(var1, var2);
            } else {
               if (f(l(var3, var2), var2)) {
                  h(k(var3, var2), var2);
                  g(var3, var2);
                  this.m(var3, var2);
                  var3 = l(j(var1, var2), var2);
               }

               a(j(var1, var2), var3, var2);
               h(j(var1, var2), var2);
               h(l(var3, var2), var2);
               this.n(j(var1, var2), var2);
               var1 = this.b[var2.ordinal()];
            }
         }
      }

      h(var1, var2);
   }

   private void b(gr.h<K, V> var1, gr.h<K, V> var2, gr.a var3) {
      gr.h var4 = gr.h.d(var1, var3);
      gr.h var5 = gr.h.a(var1, var3);
      gr.h var6 = gr.h.b(var1, var3);
      gr.h var7 = gr.h.d(var2, var3);
      gr.h var8 = gr.h.a(var2, var3);
      gr.h var9 = gr.h.b(var2, var3);
      boolean var10 = gr.h.d(var1, var3) != null && var1 == gr.h.a(gr.h.d(var1, var3), var3);
      boolean var11 = gr.h.d(var2, var3) != null && var2 == gr.h.a(gr.h.d(var2, var3), var3);
      if (var1 == var7) {
         gr.h.b(var1, var2, var3);
         if (var11) {
            gr.h.a(var2, var1, var3);
            gr.h.c(var2, var6, var3);
         } else {
            gr.h.c(var2, var1, var3);
            gr.h.a(var2, var5, var3);
         }
      } else {
         gr.h.b(var1, var7, var3);
         if (var7 != null) {
            if (var11) {
               gr.h.a(var7, var1, var3);
            } else {
               gr.h.c(var7, var1, var3);
            }
         }

         gr.h.a(var2, var5, var3);
         gr.h.c(var2, var6, var3);
      }

      if (var2 == var4) {
         gr.h.b(var2, var1, var3);
         if (var10) {
            gr.h.a(var1, var2, var3);
            gr.h.c(var1, var9, var3);
         } else {
            gr.h.c(var1, var2, var3);
            gr.h.a(var1, var8, var3);
         }
      } else {
         gr.h.b(var2, var4, var3);
         if (var4 != null) {
            if (var10) {
               gr.h.a(var4, var2, var3);
            } else {
               gr.h.c(var4, var2, var3);
            }
         }

         gr.h.a(var1, var8, var3);
         gr.h.c(var1, var9, var3);
      }

      if (gr.h.a(var1, var3) != null) {
         gr.h.b(gr.h.a(var1, var3), var1, var3);
      }

      if (gr.h.b(var1, var3) != null) {
         gr.h.b(gr.h.b(var1, var3), var1, var3);
      }

      if (gr.h.a(var2, var3) != null) {
         gr.h.b(gr.h.a(var2, var3), var2, var3);
      }

      if (gr.h.b(var2, var3) != null) {
         gr.h.b(gr.h.b(var2, var3), var2, var3);
      }

      gr.h.e(var1, var2, var3);
      if (this.b[var3.ordinal()] == var1) {
         this.b[var3.ordinal()] = var2;
      } else {
         if (this.b[var3.ordinal()] == var2) {
            this.b[var3.ordinal()] = var1;
         }
      }
   }

   private static void c(Object var0, gr.a var1) {
      if (var0 == null) {
         throw new NullPointerException(var1 + " cannot be null");
      }

      if (!(var0 instanceof Comparable)) {
         throw new ClassCastException(var1 + " must be Comparable");
      }
   }

   private static void n(Object var0) {
      c(var0, gr.a.KEY);
   }

   private static void o(Object var0) {
      c(var0, gr.a.VALUE);
   }

   private static void a(Object var0, Object var1) {
      c(var0, gr.a.KEY);
      c(var1, gr.a.VALUE);
   }

   private void h() {
      this.d++;
   }

   private void i() {
      this.h();
      this.c++;
   }

   private void j() {
      this.h();
      this.c--;
   }

   private void b(gr.h<K, V> var1) {
      gr.h var2 = this.b[gr.a.VALUE.ordinal()];

      while (true) {
         Comparable var4 = var2.b;
         Comparable var3 = var1.b;
         int var5;
         if ((var5 = var1.b.compareTo((V)var4)) == 0) {
            throw new IllegalArgumentException("Cannot store a duplicate value (\"" + gr.h.c(var1, gr.a.VALUE) + "\") in this Map");
         }

         if (var5 < 0) {
            if (gr.h.a(var2, gr.a.VALUE) == null) {
               gr.h.a(var2, var1, gr.a.VALUE);
               gr.h.b(var1, var2, gr.a.VALUE);
               this.o(var1, gr.a.VALUE);
               return;
            }

            var2 = gr.h.a(var2, gr.a.VALUE);
         } else {
            if (gr.h.b(var2, gr.a.VALUE) == null) {
               gr.h.c(var2, var1, gr.a.VALUE);
               gr.h.b(var1, var2, gr.a.VALUE);
               this.o(var1, gr.a.VALUE);
               return;
            }

            var2 = gr.h.b(var2, gr.a.VALUE);
         }
      }
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   private boolean d(Object var1, gr.a var2) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof Map)) {
         return false;
      }

      if ((var1 = var1).size() != this.size()) {
         return false;
      }

      if (this.c > 0) {
         try {
            var10 = this.c(var2);
         } catch (ClassCastException var5) {
            return false;
         } catch (NullPointerException var6) {
            return false;
         }

         while (true) {
            try {
               if (!var10.hasNext()) {
                  break;
               }

               Object var3 = var10.next();
               if (!var10.b().equals(var1.get(var3))) {
                  return false;
               }
            } catch (ClassCastException var7) {
               return false;
            } catch (NullPointerException var8) {
               return false;
            }
         }
      }

      return true;
   }

   private int a(gr.a var1) {
      int var2 = 0;
      if (this.c > 0) {
         fu var5 = this.c(var1);

         while (var5.hasNext()) {
            Object var3 = var5.next();
            Object var4 = var5.b();
            var2 += var3.hashCode() ^ var4.hashCode();
         }
      }

      return var2;
   }

   private String b(gr.a var1) {
      if (this.c == 0) {
         return "{}";
      }

      StringBuilder var2;
      (var2 = new StringBuilder(this.c << 5)).append('{');
      fu var5;
      boolean var3 = (var5 = this.c(var1)).hasNext();

      while (var3) {
         Object var6 = var5.next();
         Object var4 = var5.b();
         var2.append(var6 == this ? "(this Map)" : var6).append('=').append(var4 == this ? "(this Map)" : var4);
         if (var3 = var5.hasNext()) {
            var2.append(", ");
         }
      }

      var2.append('}');
      return var2.toString();
   }

   private fu<?, ?> c(gr.a var1) {
      switch (var1) {
         case KEY:
            return new gr.m(gr.a.KEY);
         case VALUE:
            return new gr.f(gr.a.VALUE);
         default:
            throw new IllegalArgumentException();
      }
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.b = new gr.h[2];
      int var2 = var1.readInt();

      for (int var3 = 0; var3 < var2; var3++) {
         Comparable var4 = (Comparable)var1.readObject();
         Comparable var5 = (Comparable)var1.readObject();
         this.a((K)var4, (V)var5);
      }
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeInt(this.size());

      for (Entry var3 : this.entrySet()) {
         var1.writeObject(var3.getKey());
         var1.writeObject(var3.getValue());
      }
   }

   // $VF: synthetic method
   @Override
   public final eY a() {
      return this.d();
   }

   // $VF: synthetic method
   @Override
   public final Object b(Object var1) {
      Object var2 = var1;
      return this.j(var2);
   }

   // $VF: synthetic method
   @Override
   public final Object a(Object var1) {
      return this.g(var1);
   }

   // $VF: synthetic method
   @Override
   public final Object put(Object var1, Object var2) {
      return this.a((K)var1, (V)var2);
   }

   // $VF: synthetic method
   @Override
   public final Collection values() {
      return this.b();
   }

   // $VF: synthetic method
   @Override
   public final Object remove(Object var1) {
      Object var2 = var1;
      return this.i(var2);
   }

   // $VF: synthetic method
   @Override
   public final Object get(Object var1) {
      return this.e(var1);
   }

   // $VF: synthetic method
   @Override
   public final fu c() {
      return this.e();
   }

   // $VF: synthetic method
   @Override
   public final Object d(Object var1) {
      Comparable var2 = (Comparable)var1;
      gr var4 = this;
      c(var2, gr.a.KEY);
      gr.h var5;
      return (var5 = var4.b(var4.l(var2), gr.a.KEY)) == null ? null : var5.a;
   }

   // $VF: synthetic method
   @Override
   public final Object c(Object var1) {
      Comparable var2 = (Comparable)var1;
      gr var4 = this;
      c(var2, gr.a.KEY);
      gr.h var5;
      return (var5 = var4.a(var4.l(var2), gr.a.KEY)) == null ? null : var5.a;
   }

   // $VF: synthetic method
   @Override
   public final Object lastKey() {
      gr var1 = this;
      if (this.c == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return d(var1.b[gr.a.KEY.ordinal()], gr.a.KEY).a;
      }
   }

   // $VF: synthetic method
   @Override
   public final Object firstKey() {
      gr var1 = this;
      if (this.c == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return c(var1.b[gr.a.KEY.ordinal()], gr.a.KEY).a;
      }
   }

   // $VF: synthetic method
   static gr.h a(gr var0, gr.h var1, gr.a var2) {
      return c(var1, var2);
   }

   // $VF: synthetic method
   static gr.h d(gr var0, gr.h var1, gr.a var2) {
      return d(var1, var2);
   }

   // $VF: synthetic method
   static void k(Object var0) {
      c(var0, gr.a.KEY);
   }

   enum a {
      KEY("key"),
      VALUE("value");

      private final String description;

      a(String var3) {
         this.description = var3;
      }

      @Override
      public final String toString() {
         return this.description;
      }
   }

   class b extends gr<K, V>.j<Entry<K, V>> {
      b() {
         super(gr.a.KEY);
      }

      @Override
      public final boolean contains(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         }

         Entry var3;
         Object var2 = (var3 = (Entry)obj).getValue();
         return (obj = gr.this.l(var3.getKey())) != null && ((gr.h)obj).b.equals(var2);
      }

      @Override
      public final boolean remove(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         } else {
            Entry var4;
            Object var2 = (var4 = (Entry)obj).getValue();
            if ((obj = gr.this.l(var4.getKey())) != null && ((gr.h)obj).b.equals(var2)) {
               gr.this.a((gr.h<K, V>)obj);
               return true;
            } else {
               return false;
            }
         }
      }

      @Override
      public final Iterator<Entry<K, V>> iterator() {
         return gr.this.new l();
      }
   }

   class c implements fB<V, K> {
      private Set<V> a;
      private Set<K> b;
      private Set<Entry<V, K>> c;

      @Override
      public final int size() {
         return gr.this.size();
      }

      @Override
      public final boolean isEmpty() {
         return gr.this.isEmpty();
      }

      private K e(Object var1) {
         return gr.this.g(var1);
      }

      private V f(Object var1) {
         return gr.this.e(var1);
      }

      @Override
      public final boolean containsKey(Object key) {
         return gr.this.containsValue(key);
      }

      @Override
      public final boolean containsValue(Object value) {
         return gr.this.containsKey(value);
      }

      private V f() {
         if (gr.this.c == 0) {
            throw new NoSuchElementException("Map is empty");
         } else {
            return gr.a(gr.this, gr.this.b[gr.a.VALUE.ordinal()], gr.a.VALUE).b;
         }
      }

      private V g() {
         if (gr.this.c == 0) {
            throw new NoSuchElementException("Map is empty");
         } else {
            return gr.d(gr.this, gr.this.b[gr.a.VALUE.ordinal()], gr.a.VALUE).b;
         }
      }

      private V a(V var1) {
         gr.k(var1);
         gr.h var2;
         return (var2 = gr.this.a(gr.this.b(var1, gr.a.VALUE), gr.a.VALUE)) == null ? null : var2.b;
      }

      private V b(V var1) {
         gr.k(var1);
         gr.h var2;
         return (var2 = gr.this.b(gr.this.b(var1, gr.a.VALUE), gr.a.VALUE)) == null ? null : var2.b;
      }

      private K a(V var1, K var2) {
         Comparable var3 = this.e(var1);
         gr.this.b((K)var2, (V)var1);
         return (K)var3;
      }

      @Override
      public final void putAll(Map<? extends V, ? extends K> map) {
         for (Entry var2 : map.entrySet()) {
            this.a((V)var2.getKey(), (K)var2.getValue());
         }
      }

      private K g(Object var1) {
         Object var2 = var1;
         var1 = gr.this;
         return gr.this.j(var2);
      }

      private V h(Object var1) {
         Object var2 = var1;
         var1 = gr.this;
         return gr.this.i(var2);
      }

      @Override
      public final void clear() {
         gr.this.clear();
      }

      @Override
      public final Set<V> keySet() {
         if (this.a == null) {
            this.a = gr.this.new i(gr.a.VALUE);
         }

         return this.a;
      }

      @Override
      public final Set<K> b() {
         if (this.b == null) {
            this.b = gr.this.new g(gr.a.VALUE);
         }

         return this.b;
      }

      @Override
      public final Set<Entry<V, K>> entrySet() {
         if (this.c == null) {
            this.c = gr.this.new d();
         }

         return this.c;
      }

      @Override
      public final fE<V, K> e() {
         return this.isEmpty() ? ia.d() : gr.this.new f(gr.a.VALUE);
      }

      @Override
      public final fB<K, V> d() {
         return gr.this;
      }

      @Override
      public final boolean equals(Object obj) {
         return gr.this.d(obj, gr.a.VALUE);
      }

      @Override
      public final int hashCode() {
         return gr.this.a(gr.a.VALUE);
      }

      @Override
      public final String toString() {
         return gr.this.b(gr.a.VALUE);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public final eY a() {
         return gr.this;
      }

      // $VF: synthetic method
      @Override
      public final Object b(Object var1) {
         Object var2 = var1;
         var2 = var2;
         var1 = gr.this;
         return gr.this.i(var2);
      }

      // $VF: synthetic method
      @Override
      public final Object a(Object var1) {
         Object var2 = var1;
         return gr.this.e(var2);
      }

      // $VF: synthetic method
      @Override
      public final Object put(Object var1, Object var2) {
         return this.a((V)var1, (K)var2);
      }

      // $VF: synthetic method
      @Override
      public final Collection values() {
         return this.b();
      }

      // $VF: synthetic method
      @Override
      public final Object remove(Object var1) {
         Object var2 = var1;
         var2 = var2;
         var1 = gr.this;
         return gr.this.j(var2);
      }

      // $VF: synthetic method
      @Override
      public final Object get(Object var1) {
         return this.e(var1);
      }

      // $VF: synthetic method
      @Override
      public final fu c() {
         return this.e();
      }

      // $VF: synthetic method
      @Override
      public final Object d(Object var1) {
         Comparable var2 = (Comparable)var1;
         gr.c var3 = this;
         gr.k(var2);
         gr.h var4;
         return (var4 = gr.this.b(gr.this.b(var2, gr.a.VALUE), gr.a.VALUE)) == null ? null : var4.b;
      }

      // $VF: synthetic method
      @Override
      public final Object c(Object var1) {
         Comparable var2 = (Comparable)var1;
         gr.c var3 = this;
         gr.k(var2);
         gr.h var4;
         return (var4 = gr.this.a(gr.this.b(var2, gr.a.VALUE), gr.a.VALUE)) == null ? null : var4.b;
      }

      // $VF: synthetic method
      @Override
      public final Object lastKey() {
         gr.c var1 = this;
         if (gr.this.c == 0) {
            throw new NoSuchElementException("Map is empty");
         } else {
            return gr.d(gr.this, gr.this.b[gr.a.VALUE.ordinal()], gr.a.VALUE).b;
         }
      }

      // $VF: synthetic method
      @Override
      public final Object firstKey() {
         gr.c var1 = this;
         if (gr.this.c == 0) {
            throw new NoSuchElementException("Map is empty");
         } else {
            return gr.a(gr.this, gr.this.b[gr.a.VALUE.ordinal()], gr.a.VALUE).b;
         }
      }
   }

   class d extends gr<K, V>.j<Entry<V, K>> {
      d() {
         super(gr.a.VALUE);
      }

      @Override
      public final boolean contains(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         }

         Entry var3;
         Object var2 = (var3 = (Entry)obj).getValue();
         return (obj = gr.this.m(var3.getKey())) != null && ((gr.h)obj).a.equals(var2);
      }

      @Override
      public final boolean remove(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         } else {
            Entry var4;
            Object var2 = (var4 = (Entry)obj).getValue();
            if ((obj = gr.this.m(var4.getKey())) != null && ((gr.h)obj).a.equals(var2)) {
               gr.this.a((gr.h<K, V>)obj);
               return true;
            } else {
               return false;
            }
         }
      }

      @Override
      public final Iterator<Entry<V, K>> iterator() {
         return gr.this.new e();
      }
   }

   class e extends gr<K, V>.k implements fC<Entry<V, K>> {
      e() {
         super(gr.a.VALUE);
      }

      private Entry<V, K> a() {
         return a(this.c());
      }

      private Entry<V, K> b() {
         return a(this.d());
      }

      private static Entry<V, K> a(gr.h<K, V> var0) {
         return new iL<>(var0.b, var0.a);
      }

      // $VF: synthetic method
      @Override
      public final Object previous() {
         return a(this.d());
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         return a(this.c());
      }
   }

   class f extends gr<K, V>.k implements fE<V, K> {
      public f(gr.a var2) {
         super(var2);
      }

      private V e() {
         if (super.a == null) {
            throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
         }

         gr.h var1 = super.a;
         return super.a.b;
      }

      private K f() {
         if (super.a == null) {
            throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
         }

         gr.h var1 = super.a;
         return super.a.a;
      }

      private static K g() {
         throw new UnsupportedOperationException();
      }

      private V h() {
         return this.c().b;
      }

      private V i() {
         return this.d().b;
      }

      // $VF: synthetic method
      @Override
      public final Object previous() {
         return this.d().b;
      }

      // $VF: synthetic method
      @Override
      public final Object a(Object var1) {
         throw new UnsupportedOperationException();
      }

      // $VF: synthetic method
      @Override
      public final Object b() {
         gr.f var1 = this;
         if (super.a == null) {
            throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
         }

         gr.h var2;
         return (var2 = var1.a).a;
      }

      // $VF: synthetic method
      @Override
      public final Object a() {
         gr.f var1 = this;
         if (super.a == null) {
            throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
         }

         gr.h var2;
         return (var2 = var1.a).b;
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         return this.c().b;
      }
   }

   class g extends gr<K, V>.j<K> {
      public g(gr.a var2) {
         super(var2);
      }

      @Override
      public final Iterator<K> iterator() {
         return gr.this.new m(super.a);
      }

      @Override
      public final boolean contains(Object obj) {
         gr.c(obj, gr.a.KEY);
         return gr.this.l(obj) != null;
      }

      @Override
      public final boolean remove(Object o) {
         return gr.this.i(o) != null;
      }
   }

   static class h<K extends Comparable<K>, V extends Comparable<V>> implements Entry<K, V>, fr<K, V> {
      final K a;
      final V b;
      private final gr.h<K, V>[] c;
      private final gr.h<K, V>[] d;
      private final gr.h<K, V>[] e;
      private final boolean[] f;
      private int g;
      private boolean h;

      h(K var1, V var2) {
         this.a = (K)var1;
         this.b = (V)var2;
         this.c = new gr.h[2];
         this.d = new gr.h[2];
         this.e = new gr.h[2];
         this.f = new boolean[]{true, true};
         this.h = false;
      }

      private Object a(gr.a var1) {
         switch (var1) {
            case KEY:
               return this.a;
            case VALUE:
               return this.b;
            default:
               throw new IllegalArgumentException();
         }
      }

      private void k(gr.h<K, V> var1, gr.a var2) {
         this.c[var2.ordinal()] = var1;
      }

      private gr.h<K, V> b(gr.a var1) {
         return this.c[var1.ordinal()];
      }

      private void l(gr.h<K, V> var1, gr.a var2) {
         this.d[var2.ordinal()] = var1;
      }

      private gr.h<K, V> c(gr.a var1) {
         return this.d[var1.ordinal()];
      }

      private void m(gr.h<K, V> var1, gr.a var2) {
         this.e[var2.ordinal()] = var1;
      }

      private gr.h<K, V> d(gr.a var1) {
         return this.e[var1.ordinal()];
      }

      private void n(gr.h<K, V> var1, gr.a var2) {
         this.f[var2.ordinal()] ^= var1.f[var2.ordinal()];
         var1.f[var2.ordinal()] ^= this.f[var2.ordinal()];
         this.f[var2.ordinal()] ^= var1.f[var2.ordinal()];
      }

      private boolean e(gr.a var1) {
         return this.f[var1.ordinal()];
      }

      private boolean f(gr.a var1) {
         return !this.f[var1.ordinal()];
      }

      private void g(gr.a var1) {
         this.f[var1.ordinal()] = true;
      }

      private void h(gr.a var1) {
         this.f[var1.ordinal()] = false;
      }

      private void o(gr.h<K, V> var1, gr.a var2) {
         this.f[var2.ordinal()] = var1.f[var2.ordinal()];
      }

      private boolean i(gr.a var1) {
         return this.e[var1.ordinal()] != null && this.e[var1.ordinal()].c[var1.ordinal()] == this;
      }

      private boolean j(gr.a var1) {
         return this.e[var1.ordinal()] != null && this.e[var1.ordinal()].d[var1.ordinal()] == this;
      }

      public final K a() {
         return this.a;
      }

      public final V b() {
         return this.b;
      }

      private static V c() {
         throw new UnsupportedOperationException("Map.Entry.setValue is not supported");
      }

      @Override
      public final boolean equals(Object obj) {
         if (obj == this) {
            return true;
         }

         if (!(obj instanceof Entry)) {
            return false;
         }

         obj = (Entry)obj;
         return this.a.equals(obj.getKey()) && this.b.equals(obj.getValue());
      }

      @Override
      public final int hashCode() {
         if (!this.h) {
            this.g = this.a.hashCode() ^ this.b.hashCode();
            this.h = true;
         }

         return this.g;
      }

      // $VF: synthetic method
      @Override
      public final Object setValue(Object var1) {
         throw new UnsupportedOperationException("Map.Entry.setValue is not supported");
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public final Object getValue() {
         return this.b;
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public final Object getKey() {
         return this.a;
      }

      // $VF: synthetic method
      static gr.h a(gr.h var0, gr.a var1) {
         var1 = var1;
         return var0.c[var1.ordinal()];
      }

      // $VF: synthetic method
      static void a(gr.h var0, gr.h var1, gr.a var2) {
         var2 = var2;
         var1 = var1;
         var0.c[var2.ordinal()] = var1;
      }

      // $VF: synthetic method
      static void b(gr.h var0, gr.h var1, gr.a var2) {
         var2 = var2;
         var1 = var1;
         var0.e[var2.ordinal()] = var1;
      }

      // $VF: synthetic method
      static gr.h b(gr.h var0, gr.a var1) {
         var1 = var1;
         return var0.d[var1.ordinal()];
      }

      // $VF: synthetic method
      static void c(gr.h var0, gr.h var1, gr.a var2) {
         var2 = var2;
         var1 = var1;
         var0.d[var2.ordinal()] = var1;
      }

      // $VF: synthetic method
      static Object c(gr.h var0, gr.a var1) {
         var1 = var1;
         var0 = var0;
         switch (var1) {
            case KEY:
               return var0.a;
            case VALUE:
               return var0.b;
            default:
               throw new IllegalArgumentException();
         }
      }

      // $VF: synthetic method
      static gr.h d(gr.h var0, gr.a var1) {
         var1 = var1;
         return var0.e[var1.ordinal()];
      }

      // $VF: synthetic method
      static void e(gr.h var0, gr.a var1) {
         var1 = var1;
         var0.f[var1.ordinal()] = true;
      }

      // $VF: synthetic method
      static void d(gr.h var0, gr.h var1, gr.a var2) {
         var2 = var2;
         var1 = var1;
         var0.f[var2.ordinal()] = var1.f[var2.ordinal()];
      }

      // $VF: synthetic method
      static boolean f(gr.h var0, gr.a var1) {
         var1 = var1;
         return !var0.f[var1.ordinal()];
      }

      // $VF: synthetic method
      static boolean g(gr.h var0, gr.a var1) {
         var1 = var1;
         return var0.f[var1.ordinal()];
      }

      // $VF: synthetic method
      static void h(gr.h var0, gr.a var1) {
         var1 = var1;
         var0.f[var1.ordinal()] = false;
      }

      // $VF: synthetic method
      static boolean i(gr.h var0, gr.a var1) {
         var1 = var1;
         gr.h var2 = var0;
         return var0.e[var1.ordinal()] != null && var2.e[var1.ordinal()].c[var1.ordinal()] == var2;
      }

      // $VF: synthetic method
      static boolean j(gr.h var0, gr.a var1) {
         var1 = var1;
         gr.h var2 = var0;
         return var0.e[var1.ordinal()] != null && var2.e[var1.ordinal()].d[var1.ordinal()] == var2;
      }

      // $VF: synthetic method
      static void e(gr.h var0, gr.h var1, gr.a var2) {
         var2 = var2;
         var1 = var1;
         gr.h var3 = var0;
         var0.f[var2.ordinal()] ^= var1.f[var2.ordinal()];
         var1.f[var2.ordinal()] ^= var3.f[var2.ordinal()];
         var3.f[var2.ordinal()] ^= var1.f[var2.ordinal()];
      }
   }

   class i extends gr<K, V>.j<V> {
      public i(gr.a var2) {
         super(var2);
      }

      @Override
      public final Iterator<V> iterator() {
         return gr.this.new f(super.a);
      }

      @Override
      public final boolean contains(Object obj) {
         gr.c(obj, gr.a.VALUE);
         return gr.this.m(obj) != null;
      }

      @Override
      public final boolean remove(Object o) {
         return gr.this.j(o) != null;
      }
   }

   abstract class j<E> extends AbstractSet<E> {
      final gr.a a;

      j(gr.a var2) {
         this.a = var2;
      }

      @Override
      public int size() {
         return gr.this.size();
      }

      @Override
      public void clear() {
         gr.this.clear();
      }
   }

   abstract class k {
      private final gr.a b;
      gr.h<K, V> a;
      private gr.h<K, V> c;
      private gr.h<K, V> d;
      private int e;

      k(gr.a var2) {
         this.b = var2;
         this.e = gr.this.d;
         this.c = gr.a(gr.this, gr.this.b[var2.ordinal()], var2);
         this.a = null;
         this.d = null;
      }

      public final boolean hasNext() {
         return this.c != null;
      }

      protected final gr.h<K, V> c() {
         if (this.c == null) {
            throw new NoSuchElementException();
         }

         if (gr.this.d != this.e) {
            throw new ConcurrentModificationException();
         }

         this.a = this.c;
         this.d = this.c;
         this.c = gr.this.a(this.c, this.b);
         return this.a;
      }

      public boolean hasPrevious() {
         return this.d != null;
      }

      protected final gr.h<K, V> d() {
         if (this.d == null) {
            throw new NoSuchElementException();
         }

         if (gr.this.d != this.e) {
            throw new ConcurrentModificationException();
         }

         this.c = this.a;
         if (this.c == null) {
            this.c = gr.this.a(this.d, this.b);
         }

         this.a = this.d;
         this.d = gr.this.b(this.d, this.b);
         return this.a;
      }

      public final void remove() {
         if (this.a == null) {
            throw new IllegalStateException();
         }

         if (gr.this.d != this.e) {
            throw new ConcurrentModificationException();
         }

         gr.this.a(this.a);
         this.e++;
         this.a = null;
         if (this.c == null) {
            this.d = gr.d(gr.this, gr.this.b[this.b.ordinal()], this.b);
         } else {
            this.d = gr.this.b(this.c, this.b);
         }
      }
   }

   class l extends gr<K, V>.k implements fC<Entry<K, V>> {
      l() {
         super(gr.a.KEY);
      }

      private Entry<K, V> a() {
         return this.c();
      }

      private Entry<K, V> b() {
         return this.d();
      }

      // $VF: synthetic method
      @Override
      public final Object previous() {
         return this.d();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         return this.c();
      }
   }

   class m extends gr<K, V>.k implements fE<K, V> {
      m(gr.a var2) {
         super(var2);
      }

      private K e() {
         if (super.a == null) {
            throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
         }

         gr.h var1 = super.a;
         return super.a.a;
      }

      private V f() {
         if (super.a == null) {
            throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
         }

         gr.h var1 = super.a;
         return super.a.b;
      }

      private static V g() {
         throw new UnsupportedOperationException();
      }

      private K h() {
         return this.c().a;
      }

      private K i() {
         return this.d().a;
      }

      // $VF: synthetic method
      @Override
      public final Object previous() {
         return this.d().a;
      }

      // $VF: synthetic method
      @Override
      public final Object a(Object var1) {
         throw new UnsupportedOperationException();
      }

      // $VF: synthetic method
      @Override
      public final Object b() {
         gr.m var1 = this;
         if (super.a == null) {
            throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
         }

         gr.h var2;
         return (var2 = var1.a).b;
      }

      // $VF: synthetic method
      @Override
      public final Object a() {
         gr.m var1 = this;
         if (super.a == null) {
            throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
         }

         gr.h var2;
         return (var2 = var1.a).a;
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         return this.c().a;
      }
   }
}
