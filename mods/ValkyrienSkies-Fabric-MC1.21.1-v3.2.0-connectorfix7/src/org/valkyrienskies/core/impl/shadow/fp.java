package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class fp {
   static final <unrepresentable> a = new fj<Object>() {
      @Override
      public final Iterator<Object> iterator() {
         return fq.a();
      }
   };

   private static <E> Iterable<E> a() {
      return a;
   }

   public static <E> Iterable<E> a(Iterable<? extends E> var0, Iterable<? extends E> var1) {
      return a(var0, var1);
   }

   private static <E> Iterable<E> a(Iterable<? extends E> var0, Iterable<? extends E> var1, Iterable<? extends E> var2) {
      return a(var0, var1, var2);
   }

   private static <E> Iterable<E> a(Iterable<? extends E> var0, Iterable<? extends E> var1, Iterable<? extends E> var2, Iterable<? extends E> var3) {
      return a(var0, var1, var2, var3);
   }

   private static <E> Iterable<E> a(final Iterable<? extends E>... var0) {
      b(var0);
      return new fj<E>() {
         @Override
         public final Iterator<E> iterator() {
            return new ii<E>() {
               @Override
               public final Iterator<? extends E> a(int var1) {
                  return var1 > var0.length ? null : var0[var1 - 1].iterator();
               }
            };
         }
      };
   }

   public static <E> Iterable<E> b(final Iterable<? extends E> var0, final Iterable<? extends E> var1) {
      b(var0, var1);
      return new fj<E>() {
         @Override
         public final Iterator<E> iterator() {
            return fq.a(null, var0.iterator(), var1.iterator());
         }
      };
   }

   public static <E> Iterable<E> a(final Comparator<? super E> var0, final Iterable<? extends E> var1, final Iterable<? extends E> var2) {
      b(var1, var2);
      return new fj<E>() {
         @Override
         public final Iterator<E> iterator() {
            return fq.a(var0, var1.iterator(), var2.iterator());
         }
      };
   }

   public static <E> Iterable<E> a(final Iterable<E> var0, final fF<? super E> var1) {
      i(var0);
      if (var1 == null) {
         throw new NullPointerException("Predicate must not be null.");
      } else {
         return new fj<E>() {
            @Override
            public final Iterator<E> iterator() {
               return fq.a(fp.m(var0), var1);
            }
         };
      }
   }

   public static <E> Iterable<E> a(final Iterable<E> var0, final long var1) {
      i(var0);
      if (var1 < 0L) {
         throw new IllegalArgumentException("MaxSize parameter must not be negative.");
      } else {
         return new fj<E>() {
            @Override
            public final Iterator<E> iterator() {
               return fq.a(var0.iterator(), var1);
            }
         };
      }
   }

   public static <E> Iterable<E> a(final Iterable<E> var0) {
      i(var0);
      return new fj<E>() {
         @Override
         public final Iterator<E> iterator() {
            return new ii<E>() {
               @Override
               public final Iterator<? extends E> a(int var1) {
                  return fp.e(var0) ? null : var0.iterator();
               }
            };
         }
      };
   }

   public static <E> Iterable<E> b(final Iterable<E> var0) {
      i(var0);
      return new fj<E>() {
         @Override
         public final Iterator<E> iterator() {
            List var1 = var0 instanceof List ? (List)var0 : fq.c(var0.iterator());
            return new it<>(var1);
         }
      };
   }

   public static <E> Iterable<E> b(final Iterable<E> var0, final long var1) {
      i(var0);
      if (var1 < 0L) {
         throw new IllegalArgumentException("ElementsToSkip parameter must not be negative.");
      } else {
         return new fj<E>() {
            @Override
            public final Iterator<E> iterator() {
               return fq.b(var0.iterator(), var1);
            }
         };
      }
   }

   public static <I, O> Iterable<O> a(final Iterable<I> var0, final fQ<? super I, ? extends O> var1) {
      i(var0);
      if (var1 == null) {
         throw new NullPointerException("Transformer must not be null.");
      } else {
         return new fj<O>() {
            @Override
            public final Iterator<O> iterator() {
               return fq.a(var0.iterator(), var1);
            }
         };
      }
   }

   public static <E> Iterable<E> c(final Iterable<E> var0) {
      i(var0);
      return new fj<E>() {
         @Override
         public final Iterator<E> iterator() {
            return new iy<>(var0.iterator());
         }
      };
   }

   public static <E> Iterable<E> d(Iterable<E> var0) {
      i(var0);
      return var0 instanceof fp.a ? var0 : new fp.a<>(var0);
   }

   public static <E> Iterable<E> c(final Iterable<? extends E> var0, final Iterable<? extends E> var1) {
      i(var0);
      i(var1);
      return new fj<E>() {
         @Override
         public final Iterator<E> iterator() {
            return fq.b(var0.iterator(), var1.iterator());
         }
      };
   }

   public static <E> Iterable<E> a(final Iterable<? extends E> var0, final Iterable<? extends E>... var1) {
      i(var0);
      b(var1);
      return new fj<E>() {
         @Override
         public final Iterator<E> iterator() {
            Iterator[] var1x;
            (var1x = new Iterator[var1.length + 1])[0] = var0.iterator();

            for (int var2 = 0; var2 < var1.length; var2++) {
               var1x[var2 + 1] = var1[var2].iterator();
            }

            return fq.a(var1x);
         }
      };
   }

   private static <E> Iterable<E> k(Iterable<E> var0) {
      return var0 == null ? a : var0;
   }

   public static <E> void a(Iterable<E> var0, fb<? super E> var1) {
      fq.a(m(var0), var1);
   }

   public static <E> E b(Iterable<E> var0, fb<? super E> var1) {
      return fq.b(m(var0), var1);
   }

   public static <E> E b(Iterable<E> var0, fF<? super E> var1) {
      return fq.b(m(var0), var1);
   }

   public static <E> int c(Iterable<E> var0, fF<? super E> var1) {
      return fq.c(m(var0), var1);
   }

   public static <E> boolean d(Iterable<E> var0, fF<? super E> var1) {
      return fq.e(m(var0), var1);
   }

   public static <E> boolean e(Iterable<E> var0, fF<? super E> var1) {
      return fq.d(m(var0), var1);
   }

   public static <E> long f(Iterable<E> var0, fF<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException("Predicate must not be null.");
      } else {
         return f(a(k(var0), var1));
      }
   }

   public static boolean e(Iterable<?> var0) {
      return var0 instanceof Collection ? ((Collection)var0).isEmpty() : fq.d(m(var0));
   }

   public static <E> boolean a(Iterable<E> var0, Object var1) {
      return var0 instanceof Collection ? ((Collection)var0).contains(var1) : fq.a(m(var0), var1);
   }

   private static <E> boolean a(Iterable<? extends E> var0, E var1, fg<? super E> var2) {
      if (var2 == null) {
         throw new NullPointerException("Equator must not be null.");
      } else {
         return e(var0, gX.a(var1, var2));
      }
   }

   public static <E, T extends E> int b(Iterable<E> var0, T var1) {
      if (var0 instanceof Set) {
         return ((Set)var0).contains(var1) ? 1 : 0;
      } else {
         return var0 instanceof eW ? ((eW)var0).a(var1) : f(a(k(var0), gX.b(var1)));
      }
   }

   public static <T> T a(Iterable<T> var0, int var1) {
      fd.a(var1);
      return (T)(var0 instanceof List ? ((List)var0).get(var1) : fq.b(m(var0), var1));
   }

   private static <T> T l(Iterable<T> var0) {
      return a(var0, 0);
   }

   public static int f(Iterable<?> var0) {
      return var0 instanceof Collection ? ((Collection)var0).size() : fq.e(m(var0));
   }

   private static <O> List<List<O>> g(Iterable<? extends O> var0, fF<? super O> var1) {
      if (var1 == null) {
         throw new NullPointerException("Predicate must not be null.");
      }

      Class<ArrayList> var2 = ArrayList.class;
      hk var4 = hk.<ArrayList>a(ArrayList.class, null, null);
      fF[] var3 = new fF[]{var1};
      return a(var0, var4, var3);
   }

   private static <O> List<List<O>> a(Iterable<? extends O> var0, fF<? super O>... var1) {
      Class<ArrayList> var2 = ArrayList.class;
      hk var3 = hk.<ArrayList>a(ArrayList.class, null, null);
      return a(var0, var3, var1);
   }

   private static <O, R extends Collection<O>> List<R> a(Iterable<? extends O> var0, fh<R> var1, fF<? super O>... var2) {
      while (var0 == null) {
         var var3 = a;
         var1 = var1;
         var0 = a;
      }

      if (var2 == null) {
         throw new NullPointerException("Predicates must not be null.");
      }

      fF[] var9 = var2;
      int var4 = var2.length;

      for (int var5 = 0; var5 < var4; var5++) {
         fF var6;
         if ((var6 = var9[var5]) == null) {
            throw new NullPointerException("Predicate must not be null.");
         }
      }

      if (var2.length <= 0) {
         Collection var11;
         fd.a(var11 = (Collection)var1.a(), var0);
         return Collections.singletonList((R)var11);
      }

      int var10;
      var4 = (var10 = var2.length) + 1;
      ArrayList var14 = new ArrayList(var4);

      for (int var15 = 0; var15 < var4; var15++) {
         var14.add(var1.a());
      }

      for (Object var7 : var0) {
         boolean var8 = false;

         for (int var13 = 0; var13 < var10; var13++) {
            if (var2[var13].a(var7)) {
               ((Collection)var14.get(var13)).add(var7);
               var8 = true;
               break;
            }
         }

         if (!var8) {
            ((Collection)var14.get(var10)).add(var7);
         }
      }

      return var14;
   }

   public static <E> List<E> g(Iterable<E> var0) {
      return fq.c(m(var0));
   }

   public static <E> String h(Iterable<E> var0) {
      return fq.f(m(var0));
   }

   private static <E> String b(Iterable<E> var0, fQ<? super E, String> var1) {
      if (var1 == null) {
         throw new NullPointerException("Transformer must not be null.");
      } else {
         return fq.b(m(var0), var1);
      }
   }

   private static <E> String a(Iterable<E> var0, fQ<? super E, String> var1, String var2, String var3, String var4) {
      return fq.a(m(var0), var1, var2, var3, var4);
   }

   static void i(Iterable<?> var0) {
      if (var0 == null) {
         throw new NullPointerException("Iterable must not be null.");
      }
   }

   private static void b(Iterable<?>... var0) {
      if (var0 == null) {
         throw new NullPointerException("Iterables must not be null.");
      }

      Iterable[] var4;
      int var1 = (var4 = var0).length;

      for (int var2 = 0; var2 < var1; var2++) {
         Iterable var3;
         i(var3 = var4[var2]);
      }
   }

   private static <E> Iterator<E> m(Iterable<E> var0) {
      return var0 != null ? var0.iterator() : fq.a();
   }

   static final class a<E> extends fj<E> {
      private final Iterable<E> b;

      public a(Iterable<E> var1) {
         this.b = var1;
      }

      @Override
      public final Iterator<E> iterator() {
         return fq.a(this.b.iterator());
      }
   }
}
