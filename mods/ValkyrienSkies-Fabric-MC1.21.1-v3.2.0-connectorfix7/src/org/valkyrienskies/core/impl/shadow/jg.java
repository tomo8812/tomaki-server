package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

public abstract class jg<K, V> extends jd<K, V> implements fo<K, V> {
   protected jg() {
   }

   public jg(SortedMap<K, V> var1) {
      super(var1);
   }

   public SortedMap<K, V> c_() {
      return (SortedMap<K, V>)super.i();
   }

   @Override
   public Comparator<? super K> comparator() {
      return this.c_().comparator();
   }

   @Override
   public K firstKey() {
      return this.c_().firstKey();
   }

   @Override
   public K lastKey() {
      return this.c_().lastKey();
   }

   @Override
   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return this.c_().subMap(fromKey, toKey);
   }

   @Override
   public SortedMap<K, V> headMap(K toKey) {
      return this.c_().headMap(toKey);
   }

   @Override
   public SortedMap<K, V> tailMap(K fromKey) {
      return this.c_().tailMap(fromKey);
   }

   @Override
   public K d(K var1) {
      SortedMap var2;
      return (K)((var2 = this.headMap((K)var1)).isEmpty() ? null : var2.lastKey());
   }

   @Override
   public K c(K var1) {
      Iterator var2;
      (var2 = this.tailMap((K)var1).keySet().iterator()).next();
      return (K)(var2.hasNext() ? var2.next() : null);
   }

   @Override
   public final fE<K, V> e() {
      return new jg.a<>(this.entrySet());
   }

   // $VF: synthetic method
   @Override
   protected Map i() {
      return this.c_();
   }

   // $VF: synthetic method
   @Override
   public final fu c() {
      return this.e();
   }

   protected static class a<K, V> extends jk<K, V> implements fE<K, V> {
      protected a(Set<Entry<K, V>> var1) {
         super(var1);
      }

      @Override
      public final synchronized void c() {
         super.c();
         super.a = new ij<>(super.a);
      }

      @Override
      public final boolean hasPrevious() {
         return ((ListIterator)super.a).hasPrevious();
      }

      @Override
      public final K previous() {
         super.b = ((ListIterator)super.a).previous();
         return this.a();
      }
   }
}
