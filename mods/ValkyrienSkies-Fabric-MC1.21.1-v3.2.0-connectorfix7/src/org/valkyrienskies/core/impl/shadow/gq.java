package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public final class gq<K, V> extends gl<K, V> implements Serializable, fO<K, V> {
   private static final long d = 721969328361809L;
   private final Comparator<? super K> e;
   private final Comparator<? super V> f;

   public gq() {
      super(new TreeMap<>(), new TreeMap<>());
      this.e = null;
      this.f = null;
   }

   private gq(Map<? extends K, ? extends V> var1) {
      super(new TreeMap<>(), new TreeMap<>());
      this.putAll(var1);
      this.e = null;
      this.f = null;
   }

   private gq(Comparator<? super K> var1, Comparator<? super V> var2) {
      super(new TreeMap<>(var1), new TreeMap<>(var2));
      this.e = var1;
      this.f = var2;
   }

   protected gq(Map<K, V> var1, Map<V, K> var2, eY<V, K> var3) {
      super(var1, var2, var3);
      this.e = ((SortedMap)var1).comparator();
      this.f = ((SortedMap)var2).comparator();
   }

   private static gq<V, K> a(Map<V, K> var0, Map<K, V> var1, eY<K, V> var2) {
      return new gq<>(var0, var1, var2);
   }

   @Override
   public final Comparator<? super K> comparator() {
      return ((SortedMap)super.a).comparator();
   }

   @Override
   public final Comparator<? super V> g() {
      return ((SortedMap)super.b).comparator();
   }

   @Override
   public final K firstKey() {
      return ((SortedMap)super.a).firstKey();
   }

   @Override
   public final K lastKey() {
      return ((SortedMap)super.a).lastKey();
   }

   @Override
   public final K c(K var1) {
      if (this.isEmpty()) {
         return null;
      }

      if (super.a instanceof fD) {
         return ((fD)super.a).c((K)var1);
      }

      SortedMap var2;
      Iterator var3;
      (var3 = (var2 = (SortedMap)super.a).tailMap(var1).keySet().iterator()).next();
      return (K)(var3.hasNext() ? var3.next() : null);
   }

   @Override
   public final K d(K var1) {
      if (this.isEmpty()) {
         return null;
      }

      if (super.a instanceof fD) {
         return ((fD)super.a).d((K)var1);
      }

      SortedMap var2;
      SortedMap var3;
      return (K)((var3 = (var2 = (SortedMap)super.a).headMap(var1)).isEmpty() ? null : var3.lastKey());
   }

   @Override
   public final fE<K, V> e() {
      return new gq.a<>(this);
   }

   private fO<V, K> h() {
      return (fO<V, K>)super.a();
   }

   private fB<V, K> i() {
      return (fO)super.a();
   }

   @Override
   public final SortedMap<K, V> headMap(K toKey) {
      toKey = (K)((SortedMap)super.a).headMap(toKey);
      return new gq.b<>(this, toKey);
   }

   @Override
   public final SortedMap<K, V> tailMap(K fromKey) {
      fromKey = (K)((SortedMap)super.a).tailMap(fromKey);
      return new gq.b<>(this, fromKey);
   }

   @Override
   public final SortedMap<K, V> subMap(K fromKey, K toKey) {
      fromKey = (K)((SortedMap)super.a).subMap(fromKey, toKey);
      return new gq.b<>(this, fromKey);
   }

   @Override
   public final fO<V, K> f() {
      return (fO<V, K>)super.a();
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(super.a);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      super.a = new TreeMap<>(this.e);
      super.b = new TreeMap<>(this.f);
      Map var2 = (Map)var1.readObject();
      this.putAll(var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final eY a() {
      return super.a();
   }

   // $VF: synthetic method
   @Override
   public final fu c() {
      return this.e();
   }

   // $VF: synthetic method
   @Override
   protected final gl a(Map var1, Map var2, gl var3) {
      var3 = var3;
      var2 = var2;
      var1 = var1;
      return new gq<>(var1, var2, var3);
   }

   // $VF: synthetic method
   @Override
   public final fB d() {
      return (fO)super.a();
   }

   protected static class a<K, V> implements fE<K, V>, fJ<K> {
      private final gl<K, V> a;
      private ListIterator<Entry<K, V>> b;
      private Entry<K, V> c = null;

      protected a(gl<K, V> var1) {
         this.a = var1;
         this.b = new ArrayList<>(var1.entrySet()).listIterator();
      }

      @Override
      public final boolean hasNext() {
         return this.b.hasNext();
      }

      @Override
      public final K next() {
         this.c = this.b.next();
         return this.c.getKey();
      }

      @Override
      public final boolean hasPrevious() {
         return this.b.hasPrevious();
      }

      @Override
      public final K previous() {
         this.c = this.b.previous();
         return this.c.getKey();
      }

      @Override
      public final void remove() {
         this.b.remove();
         this.a.remove(this.c.getKey());
         this.c = null;
      }

      @Override
      public final K a() {
         if (this.c == null) {
            throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
         } else {
            return this.c.getKey();
         }
      }

      @Override
      public final V b() {
         if (this.c == null) {
            throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
         } else {
            return this.c.getValue();
         }
      }

      @Override
      public final V a(V var1) {
         if (this.c == null) {
            throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
         }

         if (this.a.b.containsKey(var1) && this.a.b.get(var1) != this.c.getKey()) {
            throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
         }

         Object var2 = this.a.put(this.c.getKey(), (V)var1);
         this.c.setValue((V)var1);
         return (V)var2;
      }

      @Override
      public final void c() {
         this.b = new ArrayList<>(this.a.entrySet()).listIterator();
         this.c = null;
      }

      @Override
      public final String toString() {
         return this.c != null ? "MapIterator[" + this.a() + "=" + this.b() + "]" : "MapIterator[]";
      }
   }

   protected static class b<K, V> extends jg<K, V> {
      protected b(gq<K, V> var1, SortedMap<K, V> var2) {
         super(new gq<>(var2, var1.b, var1.c));
      }

      @Override
      public final boolean containsValue(Object value) {
         return ((gq)super.c_()).a.containsValue(value);
      }

      @Override
      public final void clear() {
         Iterator var1 = this.keySet().iterator();

         while (var1.hasNext()) {
            var1.next();
            var1.remove();
         }
      }

      @Override
      public final SortedMap<K, V> headMap(K toKey) {
         return new gq.b<>((gq<K, V>)super.c_(), super.headMap(toKey));
      }

      @Override
      public final SortedMap<K, V> tailMap(K fromKey) {
         return new gq.b<>((gq<K, V>)super.c_(), super.tailMap(fromKey));
      }

      @Override
      public final SortedMap<K, V> subMap(K fromKey, K toKey) {
         return new gq.b<>((gq<K, V>)super.c_(), super.subMap(fromKey, toKey));
      }

      private gq<K, V> b() {
         return (gq<K, V>)super.c_();
      }

      @Override
      public final K d(K var1) {
         return ((gq)super.c_()).d((K)var1);
      }

      @Override
      public final K c(K var1) {
         return ((gq)super.c_()).c((K)var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public final SortedMap c_() {
         return super.c_();
      }

      // $VF: synthetic method
      @Override
      public final Map i() {
         return super.c_();
      }
   }
}
