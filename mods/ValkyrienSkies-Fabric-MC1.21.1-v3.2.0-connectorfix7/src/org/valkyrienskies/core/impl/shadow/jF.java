package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public final class jF<K, V> extends kq<Entry<K, V>> implements fU {
   private static final long a = 1678353579659253473L;

   public static <K, V> Set<Entry<K, V>> a(Set<Entry<K, V>> var0) {
      return var0 instanceof fU ? var0 : new jF<>(var0);
   }

   private jF(Set<Entry<K, V>> var1) {
      super(var1);
   }

   private static boolean a() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean addAll(Collection<? extends Entry<K, V>> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean remove(Object object) {
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
   public final Iterator<Entry<K, V>> iterator() {
      return new jF.b(this.c().iterator());
   }

   @Override
   public final Object[] toArray() {
      Object[] var1 = this.c().toArray();

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = new jF.a((Entry<K, V>)var1[var2]);
      }

      return var1;
   }

   @Override
   public final <T> T[] toArray(T[] array) {
      Object[] var2 = array;
      if (array.length > 0) {
         var2 = (Object[])Array.newInstance(array.getClass().getComponentType(), 0);
      }

      var2 = this.c().toArray(var2);

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = new jF.a((Entry<K, V>)var2[var3]);
      }

      if (var2.length > array.length) {
         return (T[])var2;
      }

      System.arraycopy(var2, 0, array, 0, var2.length);
      if (array.length > var2.length) {
         array[var2.length] = null;
      }

      return array;
   }

   // $VF: synthetic method
   @Override
   public final boolean add(Object var1) {
      throw new UnsupportedOperationException();
   }

   class a extends iG<K, V> {
      protected a(Entry<K, V> var2) {
         super(var2);
      }

      @Override
      public final V setValue(V obj) {
         throw new UnsupportedOperationException();
      }
   }

   class b extends hN<Entry<K, V>> {
      protected b(Iterator<Entry<K, V>> var2) {
         super(var2);
      }

      private Entry<K, V> b() {
         return jF.this.new a(this.a().next());
      }

      @Override
      public final void remove() {
         throw new UnsupportedOperationException();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         jF.b var1 = this;
         return jF.this.new a(var1.a().next());
      }
   }
}
