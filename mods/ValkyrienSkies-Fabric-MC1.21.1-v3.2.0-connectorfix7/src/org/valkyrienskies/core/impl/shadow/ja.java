package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

abstract class ja<K, V> extends jd<K, V> {
   protected ja() {
   }

   protected ja(Map<K, V> var1) {
      super(var1);
   }

   protected abstract V a(V var1);

   protected boolean a() {
      return true;
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.a() ? new ja.a(super.a.entrySet(), this) : super.a.entrySet();
   }

   class a extends kq<Entry<K, V>> {
      private static final long a = 4354731610923110264L;
      private final ja<K, V> b;

      protected a(Set<Entry<K, V>> var2, ja<K, V> var3) {
         super(var2);
         this.b = var3;
      }

      @Override
      public final Iterator<Entry<K, V>> iterator() {
         return ja.this.new b(this.c().iterator(), this.b);
      }

      @Override
      public final Object[] toArray() {
         Object[] var1 = this.c().toArray();

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = ja.this.new c((Entry<K, V>)var1[var2], this.b);
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
            var2[var3] = ja.this.new c((Entry<K, V>)var2[var3], this.b);
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
   }

   class b extends hN<Entry<K, V>> {
      private final ja<K, V> a;

      protected b(Iterator<Entry<K, V>> var2, ja<K, V> var3) {
         super(var2);
         this.a = var3;
      }

      private Entry<K, V> b() {
         Entry var1 = this.a().next();
         return ja.this.new c(var1, this.a);
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         ja.b var1 = this;
         Entry var2 = this.a().next();
         return ja.this.new c(var2, var1.a);
      }
   }

   class c extends iG<K, V> {
      private final ja<K, V> a;

      protected c(Entry<K, V> var2, ja<K, V> var3) {
         super(var2);
         this.a = var3;
      }

      @Override
      public final V setValue(V value) {
         value = this.a.a(value);
         return (V)this.a().setValue(value);
      }
   }
}
