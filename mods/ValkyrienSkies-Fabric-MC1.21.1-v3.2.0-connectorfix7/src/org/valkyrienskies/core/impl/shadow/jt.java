package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class jt<K, V> extends jd<K, V> implements Serializable, fD<K, V> {
   private static final long c = 2728177751851003750L;
   final List<K> b = new ArrayList<>();

   public static <K, V> jt<K, V> a(Map<K, V> var0) {
      return new jt<>(var0);
   }

   public jt() {
      this(new HashMap<>());
   }

   private jt(Map<K, V> var1) {
      super(var1);
      this.b.addAll(this.i().keySet());
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(super.a);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      super.a = (Map<K, V>)var1.readObject();
   }

   @Override
   public final fE<K, V> e() {
      return new jt.e<>(this);
   }

   @Override
   public final K firstKey() {
      if (this.size() == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return this.b.get(0);
      }
   }

   @Override
   public final K lastKey() {
      if (this.size() == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return this.b.get(this.size() - 1);
      }
   }

   @Override
   public final K c(Object var1) {
      int var2;
      return (var2 = this.b.indexOf(var1)) >= 0 && var2 < this.size() - 1 ? this.b.get(var2 + 1) : null;
   }

   @Override
   public final K d(Object var1) {
      int var2;
      return (var2 = this.b.indexOf(var1)) > 0 ? this.b.get(var2 - 1) : null;
   }

   @Override
   public final V put(K key, V value) {
      if (this.i().containsKey(key)) {
         return this.i().put(key, value);
      }

      value = this.i().put(key, value);
      this.b.add(key);
      return value;
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> map) {
      for (Entry var2 : map.entrySet()) {
         this.put((K)var2.getKey(), (V)var2.getValue());
      }
   }

   private void a(int var1, Map<? extends K, ? extends V> var2) {
      if (var1 >= 0 && var1 <= this.b.size()) {
         Iterator var12 = var2.entrySet().iterator();

         while (var12.hasNext()) {
            Entry var3;
            boolean var4 = (boolean)(var3 = (Entry)var12.next()).getKey();
            var4 = this.containsKey(var4);
            Object var8 = var3.getValue();
            Object var7 = var3.getKey();
            int var6 = var1;
            jt var5 = this;
            if (var6 < 0 || var6 > var5.b.size()) {
               throw new IndexOutOfBoundsException("Index: " + var6 + ", Size: " + var5.b.size());
            }

            Map var9;
            if ((var9 = var5.i()).containsKey(var7)) {
               Object var10 = var9.remove(var7);
               int var11 = var5.b.indexOf(var7);
               var5.b.remove(var11);
               if (var11 < var6) {
                  var6--;
               }

               var5.b.add(var6, (K)var7);
               var9.put(var7, var8);
            } else {
               var5.b.add(var6, (K)var7);
               var9.put(var7, var8);
               Object var10000 = null;
            }

            if (!var4) {
               var1++;
            } else {
               Object var14 = var3.getKey();
               var1 = this.b.indexOf(var14) + 1;
            }
         }
      } else {
         throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + this.b.size());
      }
   }

   @Override
   public final V remove(Object key) {
      Object var2 = null;
      if (this.i().containsKey(key)) {
         var2 = this.i().remove(key);
         this.b.remove(key);
      }

      return (V)var2;
   }

   @Override
   public final void clear() {
      this.i().clear();
      this.b.clear();
   }

   @Override
   public final Set<K> keySet() {
      return new jt.b<>(this);
   }

   private List<K> a() {
      return iY.a(this.b);
   }

   @Override
   public final Collection<V> values() {
      return new jt.f<>(this);
   }

   private List<V> b() {
      return new jt.f<>(this);
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      return new jt.a<>(this, this.b);
   }

   @Override
   public final String toString() {
      if (this.isEmpty()) {
         return "{}";
      }

      StringBuilder var1;
      (var1 = new StringBuilder()).append('{');
      boolean var2 = true;
      Iterator var3 = this.entrySet().iterator();

      while (var3.hasNext()) {
         Entry var4;
         Object var5 = (var4 = (Entry)var3.next()).getKey();
         Object var6 = var4.getValue();
         if (var2) {
            var2 = false;
         } else {
            var1.append(", ");
         }

         var1.append(var5 == this ? "(this Map)" : var5);
         var1.append('=');
         var1.append(var6 == this ? "(this Map)" : var6);
      }

      var1.append('}');
      return var1.toString();
   }

   private K c(int var1) {
      return this.b.get(var1);
   }

   public final V a(int var1) {
      return this.get(this.b.get(var1));
   }

   private int a(Object var1) {
      return this.b.indexOf(var1);
   }

   public final V a(int var1, V var2) {
      Object var3 = this.b.get(var1);
      return this.put((K)var3, (V)var2);
   }

   private V a(int var1, K var2, V var3) {
      if (var1 >= 0 && var1 <= this.b.size()) {
         Map var4;
         if ((var4 = this.i()).containsKey(var2)) {
            Object var5 = var4.remove(var2);
            int var6 = this.b.indexOf(var2);
            this.b.remove(var6);
            if (var6 < var1) {
               var1--;
            }

            this.b.add(var1, (K)var2);
            var4.put(var2, var3);
            return (V)var5;
         } else {
            this.b.add(var1, (K)var2);
            var4.put(var2, var3);
            return null;
         }
      } else {
         throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + this.b.size());
      }
   }

   public final V b(int var1) {
      int var2 = var1;
      return this.remove(this.b.get(var2));
   }

   private List<K> d() {
      return iY.a(this.b);
   }

   // $VF: synthetic method
   @Override
   public final fu c() {
      return this.e();
   }

   static class a<K, V> extends AbstractSet<Entry<K, V>> {
      private final jt<K, V> a;
      private final List<K> b;
      private Set<Entry<K, V>> c;

      public a(jt<K, V> var1, List<K> var2) {
         this.a = var1;
         this.b = var2;
      }

      private Set<Entry<K, V>> a() {
         if (this.c == null) {
            this.c = this.a.i().entrySet();
         }

         return this.c;
      }

      @Override
      public final int size() {
         return this.a.size();
      }

      @Override
      public final boolean isEmpty() {
         return this.a.isEmpty();
      }

      @Override
      public final boolean contains(Object obj) {
         return this.a().contains(obj);
      }

      @Override
      public final boolean containsAll(Collection<?> coll) {
         return this.a().containsAll(coll);
      }

      @Override
      public final boolean remove(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         } else if (this.a().contains(obj)) {
            obj = ((Entry)obj).getKey();
            this.a.remove(obj);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public final void clear() {
         this.a.clear();
      }

      @Override
      public final boolean equals(Object obj) {
         return obj == this ? true : this.a().equals(obj);
      }

      @Override
      public final int hashCode() {
         return this.a().hashCode();
      }

      @Override
      public final String toString() {
         return this.a().toString();
      }

      @Override
      public final Iterator<Entry<K, V>> iterator() {
         return new jt.c<>(this.a, this.b);
      }
   }

   static class b<K> extends AbstractSet<K> {
      private final jt<K, Object> a;

      b(jt<K, ?> var1) {
         this.a = var1;
      }

      @Override
      public final int size() {
         return this.a.size();
      }

      @Override
      public final boolean contains(Object value) {
         return this.a.containsKey(value);
      }

      @Override
      public final void clear() {
         this.a.clear();
      }

      @Override
      public final Iterator<K> iterator() {
         return new hR<Entry<K, Object>, K>(this.a.entrySet().iterator()) {
            @Override
            public final K next() {
               return this.a().next().getKey();
            }
         };
      }
   }

   static class c<K, V> extends hR<K, Entry<K, V>> {
      private final jt<K, V> a;
      private K b = (K)null;

      c(jt<K, V> var1, List<K> var2) {
         super(var2.iterator());
         this.a = var1;
      }

      private Entry<K, V> b() {
         this.b = this.a().next();
         return new jt.d<>(this.a, this.b);
      }

      @Override
      public final void remove() {
         super.remove();
         this.a.i().remove(this.b);
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         jt.c var1 = this;
         this.b = var1.a().next();
         return new jt.d<>(var1.a, var1.b);
      }
   }

   static class d<K, V> extends iF<K, V> {
      private final jt<K, V> a;

      d(jt<K, V> var1, K var2) {
         super((K)var2, null);
         this.a = var1;
      }

      @Override
      public final V getValue() {
         return this.a.get(this.getKey());
      }

      @Override
      public final V setValue(V value) {
         return this.a.i().put(this.getKey(), value);
      }
   }

   static class e<K, V> implements fE<K, V>, fJ<K> {
      private final jt<K, V> a;
      private ListIterator<K> b;
      private K c = (K)null;
      private boolean d = false;

      e(jt<K, V> var1) {
         this.a = var1;
         this.b = var1.b.listIterator();
      }

      @Override
      public final boolean hasNext() {
         return this.b.hasNext();
      }

      @Override
      public final K next() {
         this.c = this.b.next();
         this.d = true;
         return this.c;
      }

      @Override
      public final boolean hasPrevious() {
         return this.b.hasPrevious();
      }

      @Override
      public final K previous() {
         this.c = this.b.previous();
         this.d = true;
         return this.c;
      }

      @Override
      public final void remove() {
         if (!this.d) {
            throw new IllegalStateException("remove() can only be called once after next()");
         }

         this.b.remove();
         this.a.a.remove(this.c);
         this.d = false;
      }

      @Override
      public final K a() {
         if (!this.d) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return this.c;
         }
      }

      @Override
      public final V b() {
         if (!this.d) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return this.a.get(this.c);
         }
      }

      @Override
      public final V a(V var1) {
         if (!this.d) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return this.a.a.put(this.c, (V)var1);
         }
      }

      @Override
      public final void c() {
         this.b = this.a.b.listIterator();
         this.c = null;
         this.d = false;
      }

      @Override
      public final String toString() {
         return this.d ? "Iterator[" + this.a() + "=" + this.b() + "]" : "Iterator[]";
      }
   }

   static class f<V> extends AbstractList<V> {
      private final jt<Object, V> a;

      f(jt<?, V> var1) {
         this.a = var1;
      }

      @Override
      public final int size() {
         return this.a.size();
      }

      @Override
      public final boolean contains(Object value) {
         return this.a.containsValue(value);
      }

      @Override
      public final void clear() {
         this.a.clear();
      }

      @Override
      public final Iterator<V> iterator() {
         return new hR<Entry<Object, V>, V>(this.a.entrySet().iterator()) {
            @Override
            public final V next() {
               return this.a().next().getValue();
            }
         };
      }

      @Override
      public final V get(int index) {
         int var2 = index;
         jt var3 = this.a;
         return this.a.get(var3.b.get(var2));
      }

      @Override
      public final V set(int index, V value) {
         Object var3 = value;
         value = (V)index;
         jt var4 = this.a;
         value = (V)this.a.b.get((int)value);
         return (V)var4.put(value, var3);
      }

      @Override
      public final V remove(int index) {
         int var2 = index;
         jt var3 = this.a;
         var2 = var2;
         return this.a.remove(var3.b.get(var2));
      }
   }
}
