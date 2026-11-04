package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public final class kt<E> extends kp<E> {
   private static final long a = -228664372470420141L;
   private final List<E> b;

   private static <E> kt<E> a(Set<E> var0, List<E> var1) {
      if (var0 == null) {
         throw new NullPointerException("Set must not be null");
      } else if (var1 == null) {
         throw new NullPointerException("List must not be null");
      } else if (var0.size() <= 0 && var1.size() <= 0) {
         return new kt<>(var0, var1);
      } else {
         throw new IllegalArgumentException("Set and List must be empty");
      }
   }

   public static <E> kt<E> a(Set<E> var0) {
      return new kt<>(var0);
   }

   private static <E> kt<E> a(List<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("List must not be null");
      }

      fd.a(var0, new hJ<>());
      HashSet var1 = new HashSet<>(var0);
      return new kt<>(var1, var0);
   }

   public kt() {
      super(new HashSet<>());
      this.b = new ArrayList<>();
   }

   private kt(Set<E> var1) {
      super(var1);
      this.b = new ArrayList<>(var1);
   }

   private kt(Set<E> var1, List<E> var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("List must not be null");
      }

      this.b = var2;
   }

   private List<E> a() {
      return iY.a(this.b);
   }

   @Override
   public final void clear() {
      this.c().clear();
      this.b.clear();
   }

   private fC<E> b() {
      return new kt.a<>(this.b.listIterator(), this.c(), (byte)0);
   }

   @Override
   public final boolean add(E object) {
      if (this.c().add(object)) {
         this.b.add(object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean addAll(Collection<? extends E> coll) {
      boolean var2 = false;

      for (Object var3 : coll) {
         var2 |= this.add((E)var3);
      }

      return var2;
   }

   @Override
   public final boolean remove(Object object) {
      boolean var2;
      if (var2 = this.c().remove(object)) {
         this.b.remove(object);
      }

      return var2;
   }

   @Override
   public final boolean removeAll(Collection<?> coll) {
      boolean var2 = false;

      for (Object var3 : coll) {
         var2 |= this.remove(var3);
      }

      return var2;
   }

   @Override
   public final boolean retainAll(Collection<?> coll) {
      boolean var3;
      if (!(var3 = this.c().retainAll(coll))) {
         return false;
      }

      if (this.c().size() == 0) {
         this.b.clear();
      } else {
         Iterator var2 = this.b.iterator();

         while (var2.hasNext()) {
            if (!this.c().contains(var2.next())) {
               var2.remove();
            }
         }
      }

      return var3;
   }

   @Override
   public final Object[] toArray() {
      return this.b.toArray();
   }

   @Override
   public final <T> T[] toArray(T[] a) {
      return (T[])this.b.toArray(a);
   }

   private E a(int var1) {
      return this.b.get(var1);
   }

   private int a(Object var1) {
      return this.b.indexOf(var1);
   }

   private void a(int var1, E var2) {
      if (!this.contains(var2)) {
         this.c().add((E)var2);
         this.b.add(var1, (E)var2);
      }
   }

   private boolean a(int var1, Collection<? extends E> var2) {
      boolean var3 = false;
      ArrayList var4 = new ArrayList();

      for (Object var5 : var2) {
         if (!this.contains(var5)) {
            this.c().add((E)var5);
            var4.add(var5);
            var3 = true;
         }
      }

      if (var3) {
         this.b.addAll(var1, var4);
      }

      return var3;
   }

   private E b(int var1) {
      Object var2 = this.b.remove(var1);
      this.remove(var2);
      return (E)var2;
   }

   @Override
   public final String toString() {
      return this.b.toString();
   }

   // $VF: synthetic method
   @Override
   public final Iterator iterator() {
      kt var1 = this;
      return new kt.a(var1.b.listIterator(), var1.c(), (byte)0);
   }

   static class a<E> extends hN<E> implements fC<E> {
      private final Collection<E> a;
      private E b;

      private a(ListIterator<E> var1, Collection<E> var2) {
         super(var1);
         this.a = var2;
      }

      @Override
      public final E next() {
         this.b = this.a().next();
         return this.b;
      }

      @Override
      public final void remove() {
         this.a.remove(this.b);
         this.a().remove();
         this.b = null;
      }

      @Override
      public final boolean hasPrevious() {
         return ((ListIterator)this.a()).hasPrevious();
      }

      @Override
      public final E previous() {
         this.b = ((ListIterator)this.a()).previous();
         return this.b;
      }

      // $VF: synthetic method
      a(ListIterator var1, Collection var2, byte var3) {
         this(var1, var2);
      }
   }
}
