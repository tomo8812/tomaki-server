package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public final class iV<E> extends iO<E> {
   private static final long a = 7196982186153478694L;
   private final Set<E> b;

   private static <E> iV<E> a(List<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("List must not be null");
      }

      if (var0.isEmpty()) {
         return new iV<>(var0, new HashSet<>());
      }

      ArrayList var1 = new ArrayList<>(var0);
      var0.clear();
      iV var2;
      (var2 = new iV<>(var0, new HashSet<>())).addAll(var1);
      return var2;
   }

   private iV(List<E> var1, Set<E> var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("Set must not be null");
      }

      this.b = var2;
   }

   private Set<E> a() {
      return kC.a(this.b);
   }

   @Override
   public final boolean add(E object) {
      int var2 = this.size();
      this.add(this.size(), object);
      return var2 != this.size();
   }

   @Override
   public final void add(int index, E object) {
      if (!this.b.contains(object)) {
         super.add(index, object);
         this.b.add(object);
      }
   }

   @Override
   public final boolean addAll(Collection<? extends E> coll) {
      return this.addAll(this.size(), coll);
   }

   @Override
   public final boolean addAll(int index, Collection<? extends E> coll) {
      ArrayList var3 = new ArrayList();

      for (Object var4 : coll) {
         if (this.b.add((E)var4)) {
            var3.add(var4);
         }
      }

      return super.addAll(index, var3);
   }

   @Override
   public final E set(int index, E object) {
      int var3 = this.indexOf(object);
      Object var4 = super.set(index, object);
      if (var3 != -1 && var3 != index) {
         super.remove(var3);
      }

      this.b.remove(var4);
      this.b.add(object);
      return (E)var4;
   }

   @Override
   public final boolean remove(Object object) {
      boolean var2;
      if (var2 = this.b.remove(object)) {
         super.remove(object);
      }

      return var2;
   }

   @Override
   public final E remove(int index) {
      Object var2 = super.remove(index);
      this.b.remove(var2);
      return (E)var2;
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
      boolean var2;
      if (!(var2 = this.b.retainAll(coll))) {
         return false;
      }

      if (this.b.size() == 0) {
         super.clear();
      } else {
         super.retainAll(this.b);
      }

      return var2;
   }

   @Override
   public final void clear() {
      super.clear();
      this.b.clear();
   }

   @Override
   public final boolean contains(Object object) {
      return this.b.contains(object);
   }

   @Override
   public final boolean containsAll(Collection<?> coll) {
      return this.b.containsAll(coll);
   }

   @Override
   public final Iterator<E> iterator() {
      return new iV.a<>(super.iterator(), this.b);
   }

   @Override
   public final ListIterator<E> listIterator() {
      return new iV.b<>(super.listIterator(), this.b);
   }

   @Override
   public final ListIterator<E> listIterator(int index) {
      return new iV.b<>(super.listIterator(index), this.b);
   }

   @Override
   public final List<E> subList(int fromIndex, int toIndex) {
      List var3 = super.subList(fromIndex, toIndex);
      Set var4 = a(this.b, var3);
      return iY.a(new iV<>(var3, var4));
   }

   private static Set<E> a(Set<E> var0, List<E> var1) {
      if (var0.getClass().equals(HashSet.class)) {
         var0 = new HashSet(var1.size());
      } else {
         try {
            var0 = (Set)var0.getClass().newInstance();
         } catch (InstantiationException var2) {
            var0 = new HashSet();
         } catch (IllegalAccessException var3) {
            var0 = new HashSet();
         }
      }

      var0.addAll(var1);
      return var0;
   }

   static class a<E> extends hN<E> {
      private final Set<E> a;
      private E b = (E)null;

      protected a(Iterator<E> var1, Set<E> var2) {
         super(var1);
         this.a = var2;
      }

      @Override
      public final E next() {
         this.b = super.next();
         return this.b;
      }

      @Override
      public final void remove() {
         super.remove();
         this.a.remove(this.b);
         this.b = null;
      }
   }

   static class b<E> extends hO<E> {
      private final Set<E> a;
      private E b = (E)null;

      protected b(ListIterator<E> var1, Set<E> var2) {
         super(var1);
         this.a = var2;
      }

      @Override
      public final E next() {
         this.b = super.next();
         return this.b;
      }

      @Override
      public final E previous() {
         this.b = super.previous();
         return this.b;
      }

      @Override
      public final void remove() {
         super.remove();
         this.a.remove(this.b);
         this.b = null;
      }

      @Override
      public final void add(E object) {
         if (!this.a.contains(object)) {
            super.add(object);
            this.a.add(object);
         }
      }

      @Override
      public final void set(E object) {
         throw new UnsupportedOperationException("ListIterator does not support set");
      }
   }
}
