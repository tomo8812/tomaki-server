package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public abstract class jJ<K, V> extends jK<K, V> implements ft<K, V> {
   protected jJ() {
   }

   protected jJ(Map<K, ? extends List<V>> var1) {
      super(var1);
   }

   @Override
   protected final Map<K, List<V>> j() {
      return (Map<K, List<V>>)super.j();
   }

   protected abstract List<V> k();

   @Override
   public final List<V> a(K var1) {
      return this.h((K)var1);
   }

   private List<V> h(K var1) {
      return new jJ.b(var1);
   }

   @Override
   public final List<V> b(Object var1) {
      return fs.a((List<V>)super.j().remove(var1));
   }

   // $VF: synthetic method
   @Override
   public final Collection f(Object var1) {
      return this.b(var1);
   }

   // $VF: synthetic method
   @Override
   final Collection g(Object var1) {
      return this.h((K)var1);
   }

   // $VF: synthetic method
   @Override
   public final Collection e(Object var1) {
      Object var2 = var1;
      return this.h((K)var2);
   }

   // $VF: synthetic method
   @Override
   protected Collection l() {
      return this.k();
   }

   class a implements ListIterator<V> {
      private final Object a;
      private List<V> b;
      private ListIterator<V> c;

      public a(K var2) {
         this.a = var2;
         this.b = fs.a(jJ.this.j().get(var2));
         this.c = this.b.listIterator();
      }

      public a(K var2, int var3) {
         this.a = var2;
         this.b = fs.a(jJ.this.j().get(var2));
         this.c = this.b.listIterator(var3);
      }

      @Override
      public final void add(V value) {
         if (jJ.this.j().get(this.a) == null) {
            List var2 = jJ.this.k();
            jJ.this.j().put((K)this.a, var2);
            this.b = var2;
            this.c = var2.listIterator();
         }

         this.c.add(value);
      }

      @Override
      public final boolean hasNext() {
         return this.c.hasNext();
      }

      @Override
      public final boolean hasPrevious() {
         return this.c.hasPrevious();
      }

      @Override
      public final V next() {
         return this.c.next();
      }

      @Override
      public final int nextIndex() {
         return this.c.nextIndex();
      }

      @Override
      public final V previous() {
         return this.c.previous();
      }

      @Override
      public final int previousIndex() {
         return this.c.previousIndex();
      }

      @Override
      public final void remove() {
         this.c.remove();
         if (this.b.isEmpty()) {
            jJ.this.j().remove(this.a);
         }
      }

      @Override
      public final void set(V value) {
         this.c.set(value);
      }
   }

   class b extends jK<K, V>.h implements List<V> {
      public b(K var2) {
         super(var2);
      }

      private List<V> b() {
         return jJ.this.j().get(super.a);
      }

      @Override
      public final void add(int index, V value) {
         List var3;
         if ((var3 = this.b()) == null) {
            var3 = jJ.this.k();
            jJ.this.j().put((K)super.a, var3);
         }

         var3.add(index, value);
      }

      @Override
      public final boolean addAll(int index, Collection<? extends V> c) {
         List var3;
         if ((var3 = this.b()) == null) {
            boolean var4;
            if (var4 = (var3 = jJ.this.k()).addAll(index, c)) {
               jJ.this.j().put((K)super.a, var3);
            }

            return var4;
         } else {
            return var3.addAll(index, c);
         }
      }

      @Override
      public final V get(int index) {
         return fs.<V>a(this.b()).get(index);
      }

      @Override
      public final int indexOf(Object o) {
         return fs.a(this.b()).indexOf(o);
      }

      @Override
      public final int lastIndexOf(Object o) {
         return fs.a(this.b()).lastIndexOf(o);
      }

      @Override
      public final ListIterator<V> listIterator() {
         return jJ.this.new a(super.a);
      }

      @Override
      public final ListIterator<V> listIterator(int index) {
         return jJ.this.new a(super.a, index);
      }

      @Override
      public final V remove(int index) {
         List var2;
         Object var3 = (var2 = fs.a(this.b())).remove(index);
         if (var2.isEmpty()) {
            jJ.this.b(super.a);
         }

         return (V)var3;
      }

      @Override
      public final V set(int index, V value) {
         return fs.<V>a(this.b()).set(index, value);
      }

      @Override
      public final List<V> subList(int fromIndex, int toIndex) {
         return fs.<V>a(this.b()).subList(fromIndex, toIndex);
      }

      @Override
      public final boolean equals(Object other) {
         List var2;
         if ((var2 = this.b()) == null) {
            return Collections.emptyList().equals(other);
         }

         if (!(other instanceof List)) {
            return false;
         }

         other = (List)other;
         List var8 = (List)other;
         other = var2;
         if (var2 == var8) {
            return true;
         }

         if (other != null && var8 != null && other.size() == var8.size()) {
            Iterator var7 = other.iterator();
            Iterator var9 = var8.iterator();

            while (var7.hasNext() && var9.hasNext()) {
               Object var3 = var7.next();
               Object var4 = var9.next();
               if (var3 == null ? var4 != null : !var3.equals(var4)) {
                  return false;
               }
            }

            return !var7.hasNext() && !var9.hasNext();
         } else {
            return false;
         }
      }

      @Override
      public final int hashCode() {
         List var1;
         if ((var1 = this.b()) == null) {
            return 0;
         }

         int var2 = 1;

         for (Object var3 : var1) {
            var2 = var2 * 31 + (var3 == null ? 0 : var3.hashCode());
         }

         return var2;
      }

      // $VF: synthetic method
      @Override
      protected final Collection a() {
         return this.b();
      }
   }
}
