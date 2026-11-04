package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class gy<E> extends gv<E> {
   private static final long b = -5259182142076705162L;
   protected final fF<? super E> a;

   private static <E> gy.a<E> a(fF<? super E> var0) {
      return new gy.a<>(var0);
   }

   private static <E> gy.a<E> a() {
      return new gy.a<>(hr.a());
   }

   public static <T> gy<T> a(Collection<T> var0, fF<? super T> var1) {
      return new gy(var0, var1);
   }

   public gy(Collection<E> var1, fF<? super E> var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("Predicate must not be null.");
      }

      this.a = var2;

      for (Object var4 : var1) {
         this.b((E)var4);
      }
   }

   public final void b(E var1) {
      if (!this.a.a((E)var1)) {
         throw new IllegalArgumentException("Cannot add Object '" + var1 + "' - Predicate '" + this.a + "' rejected it");
      }
   }

   @Override
   public boolean add(E object) {
      this.b(object);
      return this.f().add(object);
   }

   @Override
   public boolean addAll(Collection<? extends E> coll) {
      for (Object var3 : coll) {
         this.b((E)var3);
      }

      return this.f().addAll(coll);
   }

   public static class a<E> {
      private final fF<? super E> a;
      private final List<E> b = new ArrayList<>();
      private final List<E> c = new ArrayList<>();

      public a(fF<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException("Predicate must not be null");
         }

         this.a = var1;
      }

      private gy.a<E> a(E var1) {
         if (this.a.a((E)var1)) {
            this.b.add((E)var1);
         } else {
            this.c.add((E)var1);
         }

         return this;
      }

      private gy.a<E> a(Collection<? extends E> var1) {
         if (var1 != null) {
            for (Object var2 : var1) {
               Object var3 = var2;
               var2 = this;
               if (this.a.a((E)var3)) {
                  ((gy.a)var2).b.add((E)var3);
               } else {
                  ((gy.a)var2).c.add((E)var3);
               }
            }
         }

         return this;
      }

      private List<E> a() {
         ArrayList var2 = new ArrayList();
         gy.a var1 = this;
         iU var3;
         (var3 = iU.a(var2, var1.a)).addAll(var1.b);
         return var3;
      }

      private List<E> a(List<E> var1) {
         iU var2;
         (var2 = iU.a(var1, this.a)).addAll(this.b);
         return var2;
      }

      private Set<E> b() {
         HashSet var2 = new HashSet();
         gy.a var1 = this;
         kw var3;
         (var3 = kw.a(var2, var1.a)).addAll(var1.b);
         return var3;
      }

      private Set<E> a(Set<E> var1) {
         kw var2;
         (var2 = kw.a(var1, this.a)).addAll(this.b);
         return var2;
      }

      private fy<E> c() {
         jU var2 = new jU();
         gy.a var1 = this;
         jV var3;
         (var3 = jV.a(var2, var1.a)).addAll(var1.b);
         return var3;
      }

      private fy<E> a(fy<E> var1) {
         jV var2;
         (var2 = jV.a(var1, this.a)).addAll(this.b);
         return var2;
      }

      private eW<E> d() {
         ga var2 = new ga();
         gy.a var1 = this;
         gb var3;
         (var3 = gb.a(var2, var1.a)).addAll(var1.b);
         return var3;
      }

      private eW<E> a(eW<E> var1) {
         gb var2;
         (var2 = gb.a(var1, this.a)).addAll(this.b);
         return var2;
      }

      private Queue<E> e() {
         LinkedList var2 = new LinkedList();
         gy.a var1 = this;
         kb var3;
         (var3 = kb.a(var2, var1.a)).addAll(var1.b);
         return var3;
      }

      private Queue<E> a(Queue<E> var1) {
         kb var2;
         (var2 = kb.a(var1, this.a)).addAll(this.b);
         return var2;
      }

      private Collection<E> f() {
         return Collections.unmodifiableCollection(this.c);
      }
   }
}
