package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class jn<K, V> implements Serializable, Cloneable, fn<K, V> {
   private static final long a = -6701087419741928296L;
   private transient int b;
   private transient int c;
   private transient int d;
   private transient int e;
   private transient K f;
   private transient K g;
   private transient K h;
   private transient V i;
   private transient V j;
   private transient V k;
   private transient iZ<K, V> l;

   public jn() {
   }

   private jn(Map<? extends K, ? extends V> var1) {
      this.putAll(var1);
   }

   @Override
   public final V get(Object key) {
      if (this.l != null) {
         return this.l.get(key);
      }

      if (key == null) {
         switch (this.b) {
            case 3:
               if (this.h == null) {
                  return this.k;
               }
            case 2:
               if (this.g == null) {
                  return this.j;
               }
            case 1:
               if (this.f == null) {
                  return this.i;
               }
         }
      } else if (this.b > 0) {
         int var2 = key.hashCode();
         switch (this.b) {
            case 3:
               if (this.e == var2 && key.equals(this.h)) {
                  return this.k;
               }
            case 2:
               if (this.d == var2 && key.equals(this.g)) {
                  return this.j;
               }
            case 1:
               if (this.c == var2 && key.equals(this.f)) {
                  return this.i;
               }
         }
      }

      return null;
   }

   @Override
   public final int size() {
      return this.l != null ? this.l.size() : this.b;
   }

   @Override
   public final boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public final boolean containsKey(Object key) {
      if (this.l != null) {
         return this.l.containsKey(key);
      }

      if (key == null) {
         switch (this.b) {
            case 3:
               if (this.h == null) {
                  return true;
               }
            case 2:
               if (this.g == null) {
                  return true;
               }
            case 1:
               if (this.f == null) {
                  return true;
               }
         }
      } else if (this.b > 0) {
         int var2 = key.hashCode();
         switch (this.b) {
            case 3:
               if (this.e == var2 && key.equals(this.h)) {
                  return true;
               }
            case 2:
               if (this.d == var2 && key.equals(this.g)) {
                  return true;
               }
            case 1:
               if (this.c == var2 && key.equals(this.f)) {
                  return true;
               }
         }
      }

      return false;
   }

   @Override
   public final boolean containsValue(Object value) {
      if (this.l != null) {
         return this.l.containsValue(value);
      }

      if (value == null) {
         switch (this.b) {
            case 3:
               if (this.k == null) {
                  return true;
               }
            case 2:
               if (this.j == null) {
                  return true;
               }
            case 1:
               if (this.i == null) {
                  return true;
               }
         }
      } else {
         switch (this.b) {
            case 3:
               if (value.equals(this.k)) {
                  return true;
               }
            case 2:
               if (value.equals(this.j)) {
                  return true;
               }
            case 1:
               if (value.equals(this.i)) {
                  return true;
               }
         }
      }

      return false;
   }

   @Override
   public final V put(K key, V value) {
      if (this.l != null) {
         return this.l.put(key, value);
      }

      if (key == null) {
         switch (this.b) {
            case 3:
               if (this.h == null) {
                  Object var8 = this.k;
                  this.k = value;
                  return (V)var8;
               }
            case 2:
               if (this.g == null) {
                  Object var7 = this.j;
                  this.j = value;
                  return (V)var7;
               }
            case 1:
               if (this.f == null) {
                  Object var3 = this.i;
                  this.i = value;
                  return (V)var3;
               }
         }
      } else if (this.b > 0) {
         int var9 = key.hashCode();
         switch (this.b) {
            case 3:
               if (this.e == var9 && key.equals(this.h)) {
                  key = (K)this.k;
                  this.k = value;
                  return (V)key;
               }
            case 2:
               if (this.d == var9 && key.equals(this.g)) {
                  key = (K)this.j;
                  this.j = value;
                  return (V)key;
               }
            case 1:
               if (this.c == var9 && key.equals(this.f)) {
                  key = (K)this.i;
                  this.i = value;
                  return (V)key;
               }
         }
      }

      switch (this.b) {
         case 0:
            this.c = key == null ? 0 : key.hashCode();
            this.f = key;
            this.i = value;
            break;
         case 1:
            this.d = key == null ? 0 : key.hashCode();
            this.g = key;
            this.j = value;
            break;
         case 2:
            this.e = key == null ? 0 : key.hashCode();
            this.h = key;
            this.k = value;
            break;
         default:
            this.a();
            this.l.put(key, value);
            return null;
      }

      this.b++;
      return null;
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> map) {
      int var2;
      if ((var2 = map.size()) != 0) {
         if (this.l != null) {
            this.l.putAll(map);
         } else if (var2 >= 4) {
            this.a();
            this.l.putAll(map);
         } else {
            for (Entry var4 : map.entrySet()) {
               this.put((K)var4.getKey(), (V)var4.getValue());
            }
         }
      }
   }

   private void a() {
      this.l = new jo<>();
      switch (this.b) {
         case 3:
            this.l.put(this.h, this.k);
         case 2:
            this.l.put(this.g, this.j);
         case 1:
            this.l.put(this.f, this.i);
         case 0:
            this.b = 0;
            this.c = this.d = this.e = 0;
            this.f = this.g = this.h = null;
            this.i = this.j = this.k = null;
            return;
         default:
            throw new IllegalStateException("Invalid map index: " + this.b);
      }
   }

   private static iZ<K, V> b() {
      return new jo<>();
   }

   @Override
   public final V remove(Object key) {
      if (this.l != null) {
         return this.l.remove(key);
      }

      if (this.b == 0) {
         return null;
      }

      if (key == null) {
         switch (this.b) {
            case 1:
               if (this.f == null) {
                  Object var13 = this.i;
                  this.c = 0;
                  this.f = null;
                  this.i = null;
                  this.b = 0;
                  return (V)var13;
               }
               break;
            case 2:
               if (this.g == null) {
                  Object var12 = this.j;
                  this.d = 0;
                  this.g = null;
                  this.j = null;
                  this.b = 1;
                  return (V)var12;
               }

               if (this.f == null) {
                  Object var11 = this.i;
                  this.c = this.d;
                  this.f = this.g;
                  this.i = this.j;
                  this.d = 0;
                  this.g = null;
                  this.j = null;
                  this.b = 1;
                  return (V)var11;
               }

               return null;
            case 3:
               if (this.h == null) {
                  Object var10 = this.k;
                  this.e = 0;
                  this.h = null;
                  this.k = null;
                  this.b = 2;
                  return (V)var10;
               }

               if (this.g == null) {
                  Object var9 = this.j;
                  this.d = this.e;
                  this.g = this.h;
                  this.j = this.k;
                  this.e = 0;
                  this.h = null;
                  this.k = null;
                  this.b = 2;
                  return (V)var9;
               }

               if (this.f == null) {
                  Object var2 = this.i;
                  this.c = this.e;
                  this.f = this.h;
                  this.i = this.k;
                  this.e = 0;
                  this.h = null;
                  this.k = null;
                  this.b = 2;
                  return (V)var2;
               }

               return null;
         }
      } else if (this.b > 0) {
         int var14 = key.hashCode();
         switch (this.b) {
            case 1:
               if (this.c == var14 && key.equals(this.f)) {
                  key = this.i;
                  this.c = 0;
                  this.f = null;
                  this.i = null;
                  this.b = 0;
                  return (V)key;
               }
               break;
            case 2:
               if (this.d == var14 && key.equals(this.g)) {
                  key = this.j;
                  this.d = 0;
                  this.g = null;
                  this.j = null;
                  this.b = 1;
                  return (V)key;
               }

               if (this.c == var14 && key.equals(this.f)) {
                  key = this.i;
                  this.c = this.d;
                  this.f = this.g;
                  this.i = this.j;
                  this.d = 0;
                  this.g = null;
                  this.j = null;
                  this.b = 1;
                  return (V)key;
               }

               return null;
            case 3:
               if (this.e == var14 && key.equals(this.h)) {
                  key = this.k;
                  this.e = 0;
                  this.h = null;
                  this.k = null;
                  this.b = 2;
                  return (V)key;
               }

               if (this.d == var14 && key.equals(this.g)) {
                  key = this.j;
                  this.d = this.e;
                  this.g = this.h;
                  this.j = this.k;
                  this.e = 0;
                  this.h = null;
                  this.k = null;
                  this.b = 2;
                  return (V)key;
               }

               if (this.c == var14 && key.equals(this.f)) {
                  key = this.i;
                  this.c = this.e;
                  this.f = this.h;
                  this.i = this.k;
                  this.e = 0;
                  this.h = null;
                  this.k = null;
                  this.b = 2;
                  return (V)key;
               }

               return null;
         }
      }

      return null;
   }

   @Override
   public final void clear() {
      if (this.l != null) {
         this.l.clear();
         this.l = null;
      } else {
         this.b = 0;
         this.c = this.d = this.e = 0;
         this.f = this.g = this.h = null;
         this.i = this.j = this.k = null;
      }
   }

   @Override
   public final fu<K, V> c() {
      if (this.l != null) {
         return this.l.c();
      } else {
         return this.b == 0 ? hY.d() : new jn.e<>(this);
      }
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      return this.l != null ? this.l.entrySet() : new jn.b<>(this);
   }

   @Override
   public final Set<K> keySet() {
      return this.l != null ? this.l.keySet() : new jn.f<>(this);
   }

   @Override
   public final Collection<V> values() {
      return this.l != null ? this.l.values() : new jn.h<>(this);
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeInt(this.size());
      fu var2 = this.c();

      while (var2.hasNext()) {
         var1.writeObject(var2.next());
         var1.writeObject(var2.b());
      }
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      int var2;
      if ((var2 = var1.readInt()) > 3) {
         this.l = new jo<>();
      }

      for (int var3 = var2; var3 > 0; var3--) {
         this.put((K)var1.readObject(), (V)var1.readObject());
      }
   }

   private jn<K, V> d() {
      try {
         jn var1;
         if ((var1 = (jn)super.clone()).l != null) {
            var1.l = var1.l.i();
         }

         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (this.l != null) {
         return this.l.equals(obj);
      }

      if (!(obj instanceof Map)) {
         return false;
      }

      obj = (Map)obj;
      if (this.b != obj.size()) {
         return false;
      }

      if (this.b > 0) {
         switch (this.b) {
            case 3:
               if (!obj.containsKey(this.h)) {
                  return false;
               }

               Object var2 = obj.get(this.h);
               if (this.k == null ? var2 != null : !this.k.equals(var2)) {
                  return false;
               }
            case 2:
               if (!obj.containsKey(this.g)) {
                  return false;
               }

               Object var4 = obj.get(this.g);
               if (this.j == null ? var4 != null : !this.j.equals(var4)) {
                  return false;
               }
            case 1:
               if (!obj.containsKey(this.f)) {
                  return false;
               }

               Object var5 = obj.get(this.f);
               if (this.i == null ? var5 != null : !this.i.equals(var5)) {
                  return false;
               }
         }
      }

      return true;
   }

   @Override
   public final int hashCode() {
      if (this.l != null) {
         return this.l.hashCode();
      }

      int var1 = 0;
      switch (this.b) {
         case 3:
            var1 = 0 + (this.e ^ (this.k == null ? 0 : this.k.hashCode()));
         case 2:
            var1 += this.d ^ (this.j == null ? 0 : this.j.hashCode());
         case 1:
            var1 += this.c ^ (this.i == null ? 0 : this.i.hashCode());
         case 0:
            return var1;
         default:
            throw new IllegalStateException("Invalid map index: " + this.b);
      }
   }

   @Override
   public final String toString() {
      if (this.l != null) {
         return this.l.toString();
      }

      if (this.b == 0) {
         return "{}";
      }

      StringBuilder var1;
      (var1 = new StringBuilder(128)).append('{');
      switch (this.b) {
         case 3:
            var1.append(this.h == this ? "(this Map)" : this.h);
            var1.append('=');
            var1.append(this.k == this ? "(this Map)" : this.k);
            var1.append(',');
         case 2:
            var1.append(this.g == this ? "(this Map)" : this.g);
            var1.append('=');
            var1.append(this.j == this ? "(this Map)" : this.j);
            var1.append(',');
         case 1:
            var1.append(this.f == this ? "(this Map)" : this.f);
            var1.append('=');
            var1.append(this.i == this ? "(this Map)" : this.i);
            var1.append('}');
            return var1.toString();
         default:
            throw new IllegalStateException("Invalid map index: " + this.b);
      }
   }

   // $VF: synthetic method
   @Override
   public final Object clone() {
      return this.d();
   }

   abstract static class a<K, V> {
      private final jn<K, V> a;
      private int b = 0;
      private jn.d<K, V> c = null;

      public a(jn<K, V> var1) {
         this.a = var1;
      }

      public boolean hasNext() {
         return this.b < this.a.b;
      }

      public final Entry<K, V> a() {
         if (!this.hasNext()) {
            throw new NoSuchElementException("No next() entry in the iteration");
         }

         this.c = new jn.d<>(this.a, ++this.b);
         return this.c;
      }

      public void remove() {
         if (this.c == null) {
            throw new IllegalStateException("remove() can only be called once after next()");
         }

         boolean var1 = true;
         jn.d var2 = this.c;
         this.c.a = true;
         this.a.remove(this.c.getKey());
         this.b--;
         this.c = null;
      }
   }

   static class b<K, V> extends AbstractSet<Entry<K, V>> {
      private final jn<K, V> a;

      b(jn<K, V> var1) {
         this.a = var1;
      }

      @Override
      public final int size() {
         return this.a.size();
      }

      @Override
      public final void clear() {
         this.a.clear();
      }

      @Override
      public final boolean remove(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         }

         Entry var3;
         obj = (var3 = (Entry)obj).getKey();
         boolean var2 = this.a.containsKey(obj);
         this.a.remove(obj);
         return var2;
      }

      @Override
      public final Iterator<Entry<K, V>> iterator() {
         if (this.a.l != null) {
            return this.a.l.entrySet().iterator();
         } else {
            return this.a.size() == 0 ? hW.b() : new jn.c<>(this.a);
         }
      }
   }

   static class c<K, V> extends jn.a<K, V> implements Iterator<Entry<K, V>> {
      c(jn<K, V> var1) {
         super(var1);
      }

      private Entry<K, V> b() {
         return this.a();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         return this.a();
      }
   }

   static class d<K, V> implements Entry<K, V> {
      private final jn<K, V> b;
      private final int c;
      volatile boolean a;

      public d(jn<K, V> var1, int var2) {
         this.b = var1;
         this.c = var2;
         this.a = false;
      }

      final void a(boolean var1) {
         this.a = true;
      }

      @Override
      public final K getKey() {
         if (this.a) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         }

         switch (this.c) {
            case 1:
               return this.b.f;
            case 2:
               return this.b.g;
            case 3:
               return this.b.h;
            default:
               throw new IllegalStateException("Invalid map index: " + this.c);
         }
      }

      @Override
      public final V getValue() {
         if (this.a) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         }

         switch (this.c) {
            case 1:
               return this.b.i;
            case 2:
               return this.b.j;
            case 3:
               return this.b.k;
            default:
               throw new IllegalStateException("Invalid map index: " + this.c);
         }
      }

      @Override
      public final V setValue(V value) {
         if (this.a) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         }

         Object var2 = this.getValue();
         switch (this.c) {
            case 1:
               this.b.i = value;
               break;
            case 2:
               this.b.j = value;
               break;
            case 3:
               this.b.k = value;
               break;
            default:
               throw new IllegalStateException("Invalid map index: " + this.c);
         }

         return (V)var2;
      }

      @Override
      public final boolean equals(Object obj) {
         if (this.a) {
            return false;
         }

         if (!(obj instanceof Entry)) {
            return false;
         }

         obj = (Entry)obj;
         Object var2 = this.getKey();
         Object var3 = this.getValue();
         return (var2 == null ? obj.getKey() == null : var2.equals(obj.getKey())) && (var3 == null ? obj.getValue() == null : var3.equals(obj.getValue()));
      }

      @Override
      public final int hashCode() {
         if (this.a) {
            return 0;
         }

         Object var1 = this.getKey();
         Object var2 = this.getValue();
         return (var1 == null ? 0 : var1.hashCode()) ^ (var2 == null ? 0 : var2.hashCode());
      }

      @Override
      public final String toString() {
         return !this.a ? this.getKey() + "=" + this.getValue() : "";
      }
   }

   static class e<K, V> implements fJ<K>, fu<K, V> {
      private final jn<K, V> a;
      private int b = 0;
      private boolean c = false;

      e(jn<K, V> var1) {
         this.a = var1;
      }

      @Override
      public final boolean hasNext() {
         return this.b < this.a.b;
      }

      @Override
      public final K next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException("No next() entry in the iteration");
         }

         this.c = true;
         this.b++;
         return this.a();
      }

      @Override
      public final void remove() {
         if (!this.c) {
            throw new IllegalStateException("remove() can only be called once after next()");
         }

         this.a.remove(this.a());
         this.b--;
         this.c = false;
      }

      @Override
      public final K a() {
         if (!this.c) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         }

         switch (this.b) {
            case 1:
               return this.a.f;
            case 2:
               return this.a.g;
            case 3:
               return this.a.h;
            default:
               throw new IllegalStateException("Invalid map index: " + this.b);
         }
      }

      @Override
      public final V b() {
         if (!this.c) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         }

         switch (this.b) {
            case 1:
               return this.a.i;
            case 2:
               return this.a.j;
            case 3:
               return this.a.k;
            default:
               throw new IllegalStateException("Invalid map index: " + this.b);
         }
      }

      @Override
      public final V a(V var1) {
         if (!this.c) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         }

         Object var2 = this.b();
         switch (this.b) {
            case 1:
               this.a.i = (V)var1;
               break;
            case 2:
               this.a.j = (V)var1;
               break;
            case 3:
               this.a.k = (V)var1;
               break;
            default:
               throw new IllegalStateException("Invalid map index: " + this.b);
         }

         return (V)var2;
      }

      @Override
      public final void c() {
         this.b = 0;
         this.c = false;
      }

      @Override
      public final String toString() {
         return this.c ? "Iterator[" + this.a() + "=" + this.b() + "]" : "Iterator[]";
      }
   }

   static class f<K> extends AbstractSet<K> {
      private final jn<K, ?> a;

      f(jn<K, ?> var1) {
         this.a = var1;
      }

      @Override
      public final int size() {
         return this.a.size();
      }

      @Override
      public final void clear() {
         this.a.clear();
      }

      @Override
      public final boolean contains(Object key) {
         return this.a.containsKey(key);
      }

      @Override
      public final boolean remove(Object key) {
         boolean var2 = this.a.containsKey(key);
         this.a.remove(key);
         return var2;
      }

      @Override
      public final Iterator<K> iterator() {
         if (this.a.l != null) {
            return this.a.l.keySet().iterator();
         } else {
            return this.a.size() == 0 ? hW.b() : new jn.g<>(this.a);
         }
      }
   }

   static class g<K> extends jn.a<K, Object> implements Iterator<K> {
      g(jn<K, ?> var1) {
         super(var1);
      }

      @Override
      public final K next() {
         return this.a().getKey();
      }
   }

   static class h<V> extends AbstractCollection<V> {
      private final jn<?, V> a;

      h(jn<?, V> var1) {
         this.a = var1;
      }

      @Override
      public final int size() {
         return this.a.size();
      }

      @Override
      public final void clear() {
         this.a.clear();
      }

      @Override
      public final boolean contains(Object value) {
         return this.a.containsValue(value);
      }

      @Override
      public final Iterator<V> iterator() {
         if (this.a.l != null) {
            return this.a.l.values().iterator();
         } else {
            return this.a.size() == 0 ? hW.b() : new jn.i<>(this.a);
         }
      }
   }

   static class i<V> extends jn.a<Object, V> implements Iterator<V> {
      i(jn<?, V> var1) {
         super(var1);
      }

      @Override
      public final V next() {
         return this.a().getValue();
      }
   }
}
