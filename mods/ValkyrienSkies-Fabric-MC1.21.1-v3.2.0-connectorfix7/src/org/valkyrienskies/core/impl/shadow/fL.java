package org.valkyrienskies.core.impl.shadow;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public final class fL {
   private static SortedSet a = kD.a(new TreeSet());

   private static <E> Set<E> a() {
      return Collections.emptySet();
   }

   private static <E> SortedSet<E> b() {
      return a;
   }

   private fL() {
   }

   public static <T> Set<T> a(Set<T> var0) {
      return var0 == null ? Collections.emptySet() : var0;
   }

   public static boolean a(Collection<?> var0, Collection<?> var1) {
      if (var0 == var1) {
         return true;
      } else {
         return var0 != null && var1 != null && var0.size() == var1.size() ? var0.containsAll(var1) : false;
      }
   }

   public static <T> int a(Collection<T> var0) {
      if (var0 == null) {
         return 0;
      }

      int var1 = 0;
      Iterator var3 = var0.iterator();

      while (var3.hasNext()) {
         Object var2;
         if ((var2 = var3.next()) != null) {
            var1 += var2.hashCode();
         }
      }

      return var1;
   }

   private static <E> Set<E> c() {
      return Collections.newSetFromMap(new IdentityHashMap<>());
   }

   private static <E> Set<E> b(Set<E> var0) {
      return Collections.synchronizedSet(var0);
   }

   private static <E> Set<E> c(Set<? extends E> var0) {
      return kC.a(var0);
   }

   private static <E> Set<E> a(Set<E> var0, fF<? super E> var1) {
      return kw.a(var0, var1);
   }

   private static <E> Set<E> a(Set<E> var0, fQ<? super E, ? extends E> var1) {
      return kz.a(var0, var1);
   }

   private static <E> Set<E> d(Set<E> var0) {
      return kt.a(var0);
   }

   private static <E> SortedSet<E> a(SortedSet<E> var0) {
      return Collections.synchronizedSortedSet(var0);
   }

   private static <E> SortedSet<E> b(SortedSet<E> var0) {
      return kD.a(var0);
   }

   private static <E> SortedSet<E> a(SortedSet<E> var0, fF<? super E> var1) {
      return kx.a(var0, var1);
   }

   private static <E> SortedSet<E> a(SortedSet<E> var0, fQ<? super E, ? extends E> var1) {
      return kA.a(var0, var1);
   }

   private static <E> SortedSet<E> a(NavigableSet<E> var0) {
      return kB.a(var0);
   }

   private static <E> SortedSet<E> a(NavigableSet<E> var0, fF<? super E> var1) {
      return kv.a(var0, var1);
   }

   private static <E> SortedSet<E> a(NavigableSet<E> var0, fQ<? super E, ? extends E> var1) {
      return ky.a(var0, var1);
   }

   private static <E> fL.a<E> a(final Set<? extends E> var0, final Set<? extends E> var1) {
      if (var0 != null && var1 != null) {
         final var var2 = b(var1, var0);
         return new fL.a<E>() {
            @Override
            public final boolean contains(Object o) {
               return var0.contains(o) || var1.contains(o);
            }

            @Override
            public final Iterator<E> a() {
               return fq.a(var0.iterator(), var2.iterator());
            }

            @Override
            public final boolean isEmpty() {
               return var0.isEmpty() && var1.isEmpty();
            }

            @Override
            public final int size() {
               return var0.size() + var2.size();
            }
         };
      } else {
         throw new NullPointerException("Sets must not be null.");
      }
   }

   private static <E> fL.a<E> b(final Set<? extends E> var0, final Set<? extends E> var1) {
      if (var0 != null && var1 != null) {
         final fF var2 = new fF<E>() {
            @Override
            public final boolean a(E var1x) {
               return !var1.contains(var1x);
            }
         };
         return new fL.a<E>() {
            @Override
            public final boolean contains(Object o) {
               return var0.contains(o) && !var1.contains(o);
            }

            @Override
            public final Iterator<E> a() {
               return fq.a(var0.iterator(), var2);
            }
         };
      } else {
         throw new NullPointerException("Sets must not be null.");
      }
   }

   private static <E> fL.a<E> c(final Set<? extends E> var0, final Set<? extends E> var1) {
      if (var0 != null && var1 != null) {
         final fF var2 = new fF<E>() {
            @Override
            public final boolean a(E var1x) {
               return var1.contains(var1x);
            }
         };
         return new fL.a<E>() {
            @Override
            public final boolean contains(Object o) {
               return var0.contains(o) && var1.contains(o);
            }

            @Override
            public final Iterator<E> a() {
               return fq.a(var0.iterator(), var2);
            }
         };
      } else {
         throw new NullPointerException("Sets must not be null.");
      }
   }

   private static <E> fL.a<E> d(final Set<? extends E> var0, final Set<? extends E> var1) {
      if (var0 != null && var1 != null) {
         final var var2 = b(var0, var1);
         final var var3 = b(var1, var0);
         return new fL.a<E>() {
            @Override
            public final boolean contains(Object o) {
               return var0.contains(o) ^ var1.contains(o);
            }

            @Override
            public final Iterator<E> a() {
               return fq.a(var2.iterator(), var3.iterator());
            }

            @Override
            public final boolean isEmpty() {
               return var2.isEmpty() && var3.isEmpty();
            }

            @Override
            public final int size() {
               return var2.size() + var3.size();
            }
         };
      } else {
         throw new NullPointerException("Sets must not be null.");
      }
   }

   public abstract static class a<E> extends AbstractSet<E> {
      @Override
      public Iterator<E> iterator() {
         return fq.a(this.a());
      }

      protected abstract Iterator<E> a();

      @Override
      public int size() {
         return fq.e(this.iterator());
      }

      private <S extends Set<E>> void a(S var1) {
         fd.a(var1, this);
      }

      private Set<E> b() {
         HashSet var1 = new HashSet(this.size());
         HashSet var3 = var1;
         fL.a var2 = this;
         fd.a(var3, var2);
         return var1;
      }
   }
}
