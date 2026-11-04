package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public final class js<K, V> extends jc<K, V> implements Serializable, Cloneable {
   private static final long k = 9077234323521161066L;

   public js() {
      super(16, 0.75F, 12);
   }

   public js(int var1) {
      super(var1);
   }

   private js(int var1, float var2) {
      super(var1, var2);
   }

   private js(Map<? extends K, ? extends V> var1) {
      super(var1);
   }

   private js<K, V> a() {
      return (js<K, V>)super.i();
   }

   private void b(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      this.a(var1);
   }

   private void b(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a(var1);
   }

   public final K b(int var1) {
      return this.a(var1).getKey();
   }

   private V c(int var1) {
      return this.a(var1).getValue();
   }

   public final int g(Object var1) {
      var1 = this.a(var1);
      int var2 = 0;

      for (jc.c var3 = super.j.f; var3 != super.j; var2++) {
         if (this.a((Object)var1, var3.c)) {
            return var2;
         }

         var3 = var3.f;
      }

      return -1;
   }

   private V d(int var1) {
      return this.remove(this.b(var1));
   }

   private List<K> b() {
      return new js.a<>(this);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final iZ i() {
      return super.i();
   }

   // $VF: synthetic method
   @Override
   public final Object clone() {
      return super.i();
   }

   static class a<K> extends AbstractList<K> {
      private final js<K, ?> a;

      a(js<K, ?> var1) {
         this.a = var1;
      }

      @Override
      public final int size() {
         return this.a.size();
      }

      @Override
      public final K get(int index) {
         return this.a.b(index);
      }

      @Override
      public final boolean contains(Object obj) {
         return this.a.containsKey(obj);
      }

      @Override
      public final int indexOf(Object obj) {
         return this.a.g(obj);
      }

      @Override
      public final int lastIndexOf(Object obj) {
         return this.a.g(obj);
      }

      @Override
      public final boolean containsAll(Collection<?> coll) {
         return this.a.keySet().containsAll(coll);
      }

      @Override
      public final K remove(int index) {
         throw new UnsupportedOperationException();
      }

      @Override
      public final boolean remove(Object obj) {
         throw new UnsupportedOperationException();
      }

      @Override
      public final boolean removeAll(Collection<?> coll) {
         throw new UnsupportedOperationException();
      }

      @Override
      public final boolean retainAll(Collection<?> coll) {
         throw new UnsupportedOperationException();
      }

      @Override
      public final void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public final Object[] toArray() {
         return this.a.keySet().toArray();
      }

      @Override
      public final <T> T[] toArray(T[] array) {
         return (T[])this.a.keySet().toArray(array);
      }

      @Override
      public final Iterator<K> iterator() {
         return iz.a(this.a.keySet().iterator());
      }

      @Override
      public final ListIterator<K> listIterator() {
         return iA.a(super.listIterator());
      }

      @Override
      public final ListIterator<K> listIterator(int fromIndex) {
         return iA.a(super.listIterator(fromIndex));
      }

      @Override
      public final List<K> subList(int fromIndexInclusive, int toIndexExclusive) {
         return iY.a(super.subList(fromIndexInclusive, toIndexExclusive));
      }
   }
}
