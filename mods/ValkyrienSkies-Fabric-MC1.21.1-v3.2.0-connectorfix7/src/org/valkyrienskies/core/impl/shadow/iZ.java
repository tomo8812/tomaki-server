package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public class iZ<K, V> extends AbstractMap<K, V> implements fn<K, V> {
   private static String j;
   private static String k;
   private static String l;
   private static String m;
   private static String n;
   private static String o;
   private static int p;
   private static int q;
   private static float r;
   private static int s;
   protected static final Object a = new Object();
   transient float b;
   transient int c;
   transient iZ.c<K, V>[] d;
   transient int e;
   transient int f;
   transient iZ.a<K, V> g;
   transient iZ.f<K> h;
   transient iZ.h<V> i;

   protected iZ() {
   }

   protected iZ(int var1, float var2, int var3) {
      this.b = var2;
      this.d = new iZ.c[var1];
      this.e = var3;
      this.d();
   }

   protected iZ(int var1) {
      this(var1, 0.75F);
   }

   protected iZ(int var1, float var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Initial capacity must be a non negative number");
      }

      if (!(var2 <= 0.0F) && !Float.isNaN(var2)) {
         this.b = var2;
         var1 = b(var1);
         float var3 = var2;
         this.e = (int)(var1 * var3);
         this.d = new iZ.c[var1];
         this.d();
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0");
      }
   }

   protected iZ(Map<? extends K, ? extends V> var1) {
      this(Math.max(2 * var1.size(), 16), 0.75F);
      this.a(var1);
   }

   protected void d() {
   }

   @Override
   public V get(Object key) {
      key = this.a(key);
      int var2 = this.b(key);
      int var4 = this.d.length;

      for (iZ.c var3 = this.d[var2 & var4 - 1]; var3 != null; var3 = var3.a) {
         if (var3.b == var2 && this.a(key, var3.c)) {
            return (V)var3.getValue();
         }
      }

      return null;
   }

   @Override
   public int size() {
      return this.c;
   }

   @Override
   public boolean isEmpty() {
      return this.c == 0;
   }

   @Override
   public boolean containsKey(Object key) {
      key = this.a(key);
      int var2 = this.b(key);
      int var4 = this.d.length;

      for (iZ.c var3 = this.d[var2 & var4 - 1]; var3 != null; var3 = var3.a) {
         if (var3.b == var2 && this.a(key, var3.c)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(Object value) {
      if (value == null) {
         iZ.c[] var2 = this.d;
         int var3 = this.d.length;

         for (int var4 = 0; var4 < var3; var4++) {
            iZ.c var5;
            for (iZ.c var9 = var5 = var2[var4]; var9 != null; var9 = var9.a) {
               if (var9.getValue() == null) {
                  return true;
               }
            }
         }
      } else {
         iZ.c[] var6 = this.d;
         int var7 = this.d.length;

         for (int var8 = 0; var8 < var7; var8++) {
            iZ.c var10;
            for (iZ.c var11 = var10 = var6[var8]; var11 != null; var11 = var11.a) {
               if (this.b(value, var11.getValue())) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Override
   public V put(K key, V value) {
      Object var3 = this.a(key);
      int var4;
      int var10000 = var4 = this.b(var3);
      int var6 = this.d.length;
      int var5 = var10000 & var6 - 1;

      for (iZ.c var8 = this.d[var5]; var8 != null; var8 = var8.a) {
         if (var8.b == var4 && this.a(var3, var8.c)) {
            key = (K)var8.getValue();
            this.a(var8, value);
            return (V)key;
         }
      }

      this.a(var5, var4, key, value);
      return null;
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      this.a(map);
   }

   private void a(Map<? extends K, ? extends V> var1) {
      int var2;
      if ((var2 = var1.size()) != 0) {
         var2 = (int)((this.c + var2) / this.b + 1.0F);
         this.a(b(var2));

         for (Entry var5 : var1.entrySet()) {
            this.put((K)var5.getKey(), (V)var5.getValue());
         }
      }
   }

   @Override
   public V remove(Object key) {
      key = this.a(key);
      int var2;
      int var10000 = var2 = this.b(key);
      int var4 = this.d.length;
      int var3 = var10000 & var4 - 1;
      iZ.c var8 = this.d[var3];
      iZ.c var5 = null;

      while (var8 != null) {
         if (var8.b == var2 && this.a(key, var8.c)) {
            key = var8.getValue();
            this.a(var8, var3, var5);
            return (V)key;
         }

         var5 = var8;
         var8 = var8.a;
      }

      return null;
   }

   @Override
   public void clear() {
      this.f++;
      iZ.c[] var1 = this.d;

      for (int var2 = this.d.length - 1; var2 >= 0; var2--) {
         var1[var2] = null;
      }

      this.c = 0;
   }

   protected Object a(Object var1) {
      return var1 == null ? a : var1;
   }

   protected int b(Object var1) {
      int var2;
      int var3;
      int var4;
      int var5;
      return (var5 = (var4 = (var3 = (var2 = var1.hashCode()) + ~(var2 << 9)) ^ var3 >>> 14) + (var4 << 4)) ^ var5 >>> 10;
   }

   protected boolean a(Object var1, Object var2) {
      return var1 == var2 || var1.equals(var2);
   }

   protected boolean b(Object var1, Object var2) {
      return var1 == var2 || var1.equals(var2);
   }

   protected static int a(int var0, int var1) {
      return var0 & var1 - 1;
   }

   protected iZ.c<K, V> e(Object var1) {
      var1 = this.a(var1);
      int var2 = this.b(var1);
      int var4 = this.d.length;

      for (iZ.c var3 = this.d[var2 & var4 - 1]; var3 != null; var3 = var3.a) {
         if (var3.b == var2 && this.a(var1, var3.c)) {
            return var3;
         }
      }

      return null;
   }

   protected void a(iZ.c<K, V> var1, V var2) {
      var1.setValue(var2);
   }

   protected final void a(iZ.c<K, V> var1, int var2, int var3, K var4, V var5) {
      var1.a = this.d[var2];
      var1.b = var3;
      var1.c = var4;
      var1.d = var5;
   }

   protected void a(int var1, int var2, K var3, V var4) {
      this.f++;
      iZ.c var6 = this.a(this.d[var1], var2, (K)var3, (V)var4);
      this.a(var6, var1);
      this.c++;
      iZ var5 = this;
      if (this.c >= var5.e && (var2 = var5.d.length << 1) <= 1073741824) {
         var5.a(var2);
      }
   }

   protected iZ.c<K, V> a(iZ.c<K, V> var1, int var2, K var3, V var4) {
      return new iZ.c<>(var1, var2, this.a(var3), (V)var4);
   }

   protected void a(iZ.c<K, V> var1, int var2) {
      this.d[var2] = var1;
   }

   protected final void a(iZ.c<K, V> var1, int var2, iZ.c<K, V> var3) {
      this.f++;
      this.b(var1, var2, var3);
      this.c--;
      a(var1);
   }

   protected void b(iZ.c<K, V> var1, int var2, iZ.c<K, V> var3) {
      if (var3 == null) {
         this.d[var2] = var1.a;
      } else {
         var3.a = var1.a;
      }
   }

   private static void a(iZ.c<K, V> var0) {
      var0.a = null;
      var0.c = null;
      var0.d = null;
   }

   private void a() {
      int var1;
      if (this.c >= this.e && (var1 = this.d.length << 1) <= 1073741824) {
         this.a(var1);
      }
   }

   private void a(int var1) {
      int var2 = this.d.length;
      if (var1 > var2) {
         if (this.c == 0) {
            float var12 = this.b;
            this.e = (int)(var1 * var12);
            this.d = new iZ.c[var1];
         } else {
            iZ.c[] var3 = this.d;
            iZ.c[] var4 = new iZ.c[var1];
            this.f++;
            var2--;

            for (; var2 >= 0; var2--) {
               iZ.c var5;
               if ((var5 = var3[var2]) != null) {
                  var3[var2] = null;

                  while (true) {
                     iZ.c var6 = var5.a;
                     int var8 = var1;
                     int var7 = var5.b;
                     var7 = var5.b & var8 - 1;
                     var5.a = var4[var7];
                     var4[var7] = var5;
                     var5 = var6;
                     if (var6 == null) {
                        break;
                     }
                  }
               }
            }

            float var11 = this.b;
            this.e = (int)(var1 * var11);
            this.d = var4;
         }
      }
   }

   private static int b(int var0) {
      int var1 = 1;
      if (var0 > 1073741824) {
         var1 = 1073741824;
      } else {
         while (var1 < var0) {
            var1 <<= 1;
         }

         if (var1 > 1073741824) {
            var1 = 1073741824;
         }
      }

      return var1;
   }

   protected static int a(int var0, float var1) {
      return (int)(var0 * var1);
   }

   private static iZ.c<K, V> b(iZ.c<K, V> var0) {
      return var0.a;
   }

   private static int c(iZ.c<K, V> var0) {
      return var0.b;
   }

   private static K d(iZ.c<K, V> var0) {
      return (K)var0.getKey();
   }

   private static V e(iZ.c<K, V> var0) {
      return (V)var0.getValue();
   }

   @Override
   public fu<K, V> c() {
      return this.c == 0 ? hY.d() : new iZ.e<>(this);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      if (this.g == null) {
         this.g = new iZ.a<>(this);
      }

      return this.g;
   }

   protected Iterator<Entry<K, V>> f() {
      return this.size() == 0 ? hW.b() : new iZ.b<>(this);
   }

   @Override
   public Set<K> keySet() {
      if (this.h == null) {
         this.h = new iZ.f<>(this);
      }

      return this.h;
   }

   protected Iterator<K> g() {
      return this.size() == 0 ? hW.b() : new iZ.g<>(this);
   }

   @Override
   public Collection<V> values() {
      if (this.i == null) {
         this.i = new iZ.h<>(this);
      }

      return this.i;
   }

   protected Iterator<V> h() {
      return this.size() == 0 ? hW.b() : new iZ.i<>(this);
   }

   protected void a(ObjectOutputStream var1) {
      var1.writeFloat(this.b);
      var1.writeInt(this.d.length);
      var1.writeInt(this.c);
      fu var2 = this.c();

      while (var2.hasNext()) {
         var1.writeObject(var2.next());
         var1.writeObject(var2.b());
      }
   }

   protected void a(ObjectInputStream var1) {
      this.b = var1.readFloat();
      int var2 = var1.readInt();
      int var3 = var1.readInt();
      this.d();
      float var5 = this.b;
      this.e = (int)(var2 * var5);
      this.d = new iZ.c[var2];

      for (int var6 = 0; var6 < var3; var6++) {
         Object var4 = var1.readObject();
         Object var7 = var1.readObject();
         this.put((K)var4, (V)var7);
      }
   }

   protected iZ<K, V> i() {
      try {
         iZ var1;
         (var1 = (iZ)super.clone()).d = new iZ.c[this.d.length];
         var1.g = null;
         var1.h = null;
         var1.i = null;
         var1.f = 0;
         var1.c = 0;
         var1.d();
         var1.putAll(this);
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof Map)) {
         return false;
      }

      if ((obj = (Map)obj).size() != this.size()) {
         return false;
      }

      fu var2 = this.c();

      while (true) {
         try {
            if (!var2.hasNext()) {
               return true;
            }

            Object var3 = var2.next();
            Object var4;
            if ((var4 = var2.b()) == null) {
               if (obj.get(var3) != null || !obj.containsKey(var3)) {
                  return false;
               }
            } else if (!var4.equals(obj.get(var3))) {
               return false;
            }
         } catch (ClassCastException var5) {
            return false;
         } catch (NullPointerException var6) {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;
      Iterator var2 = this.f();

      while (var2.hasNext()) {
         var1 += ((Entry)var2.next()).hashCode();
      }

      return var1;
   }

   @Override
   public String toString() {
      if (this.size() == 0) {
         return "{}";
      }

      StringBuilder var1;
      (var1 = new StringBuilder(32 * this.size())).append('{');
      fu var2;
      boolean var3 = (var2 = this.c()).hasNext();

      while (var3) {
         Object var5 = var2.next();
         Object var4 = var2.b();
         var1.append(var5 == this ? "(this Map)" : var5).append('=').append(var4 == this ? "(this Map)" : var4);
         if (var3 = var2.hasNext()) {
            var1.append(',').append(' ');
         }
      }

      var1.append('}');
      return var1.toString();
   }

   // $VF: synthetic method
   @Override
   protected Object clone() {
      return this.i();
   }

   protected static class a<K, V> extends AbstractSet<Entry<K, V>> {
      private final iZ<K, V> a;

      protected a(iZ<K, V> var1) {
         this.a = var1;
      }

      @Override
      public int size() {
         return this.a.size();
      }

      @Override
      public void clear() {
         this.a.clear();
      }

      @Override
      public boolean contains(Object entry) {
         if (entry instanceof Entry) {
            entry = (Entry)entry;
            iZ.c var2;
            return (var2 = this.a.e(entry.getKey())) != null && var2.equals(entry);
         } else {
            return false;
         }
      }

      @Override
      public boolean remove(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         }

         if (!this.contains(obj)) {
            return false;
         }

         obj = (Entry)obj;
         this.a.remove(obj.getKey());
         return true;
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return this.a.f();
      }
   }

   protected static class b<K, V> extends iZ.d<K, V> implements Iterator<Entry<K, V>> {
      protected b(iZ<K, V> var1) {
         super(var1);
      }

      private Entry<K, V> a() {
         return super.c();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         return super.c();
      }
   }

   protected static class c<K, V> implements Entry<K, V>, fr<K, V> {
      protected iZ.c<K, V> a;
      protected int b;
      protected Object c;
      protected Object d;

      protected c(iZ.c<K, V> var1, int var2, Object var3, V var4) {
         this.a = var1;
         this.b = var2;
         this.c = var3;
         this.d = var4;
      }

      @Override
      public K getKey() {
         return (K)(this.c == iZ.a ? null : this.c);
      }

      @Override
      public V getValue() {
         return (V)this.d;
      }

      @Override
      public V setValue(V value) {
         Object var2 = this.d;
         this.d = value;
         return (V)var2;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         }

         if (!(obj instanceof Entry)) {
            return false;
         }

         obj = (Entry)obj;
         return (this.getKey() == null ? obj.getKey() == null : this.getKey().equals(obj.getKey()))
            && (this.getValue() == null ? obj.getValue() == null : this.getValue().equals(obj.getValue()));
      }

      @Override
      public int hashCode() {
         return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
      }

      @Override
      public String toString() {
         return "" + this.getKey() + '=' + this.getValue();
      }
   }

   protected abstract static class d<K, V> {
      private final iZ<K, V> a;
      private int b;
      private iZ.c<K, V> c;
      private iZ.c<K, V> d;
      private int e;

      protected d(iZ<K, V> var1) {
         this.a = var1;
         iZ.c[] var2 = var1.d;
         int var3 = var1.d.length;
         iZ.c var4 = null;

         while (var3 > 0 && var4 == null) {
            var4 = var2[--var3];
         }

         this.d = var4;
         this.b = var3;
         this.e = var1.f;
      }

      public boolean hasNext() {
         return this.d != null;
      }

      protected final iZ.c<K, V> c() {
         if (this.a.f != this.e) {
            throw new ConcurrentModificationException();
         }

         iZ.c var1 = this.d;
         if (this.d == null) {
            throw new NoSuchElementException("No next() entry in the iteration");
         }

         iZ.c[] var2 = this.a.d;
         int var3 = this.b;
         iZ.c var4 = var1.a;

         while (var4 == null && var3 > 0) {
            var4 = var2[--var3];
         }

         this.d = var4;
         this.b = var3;
         this.c = var1;
         return var1;
      }

      protected final iZ.c<K, V> d() {
         return this.c;
      }

      public void remove() {
         if (this.c == null) {
            throw new IllegalStateException("remove() can only be called once after next()");
         }

         if (this.a.f != this.e) {
            throw new ConcurrentModificationException();
         }

         this.a.remove(this.c.getKey());
         this.c = null;
         this.e = this.a.f;
      }

      @Override
      public String toString() {
         return this.c != null ? "Iterator[" + this.c.getKey() + "=" + this.c.getValue() + "]" : "Iterator[]";
      }
   }

   protected static class e<K, V> extends iZ.d<K, V> implements fu<K, V> {
      protected e(iZ<K, V> var1) {
         super(var1);
      }

      @Override
      public final K next() {
         return super.c().getKey();
      }

      @Override
      public final K a() {
         iZ.c var1;
         if ((var1 = this.d()) == null) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return (K)var1.getKey();
         }
      }

      @Override
      public final V b() {
         iZ.c var1;
         if ((var1 = this.d()) == null) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return (V)var1.getValue();
         }
      }

      @Override
      public final V a(V var1) {
         iZ.c var2;
         if ((var2 = this.d()) == null) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return (V)var2.setValue(var1);
         }
      }
   }

   protected static class f<K> extends AbstractSet<K> {
      private final iZ<K, ?> a;

      protected f(iZ<K, ?> var1) {
         this.a = var1;
      }

      @Override
      public int size() {
         return this.a.size();
      }

      @Override
      public void clear() {
         this.a.clear();
      }

      @Override
      public boolean contains(Object key) {
         return this.a.containsKey(key);
      }

      @Override
      public boolean remove(Object key) {
         boolean var2 = this.a.containsKey(key);
         this.a.remove(key);
         return var2;
      }

      @Override
      public Iterator<K> iterator() {
         return this.a.g();
      }
   }

   protected static class g<K> extends iZ.d<K, Object> implements Iterator<K> {
      protected g(iZ<K, ?> var1) {
         super(var1);
      }

      @Override
      public final K next() {
         return super.c().getKey();
      }
   }

   protected static class h<V> extends AbstractCollection<V> {
      private final iZ<?, V> a;

      protected h(iZ<?, V> var1) {
         this.a = var1;
      }

      @Override
      public int size() {
         return this.a.size();
      }

      @Override
      public void clear() {
         this.a.clear();
      }

      @Override
      public boolean contains(Object value) {
         return this.a.containsValue(value);
      }

      @Override
      public Iterator<V> iterator() {
         return this.a.h();
      }
   }

   protected static class i<V> extends iZ.d<Object, V> implements Iterator<V> {
      protected i(iZ<?, V> var1) {
         super(var1);
      }

      @Override
      public final V next() {
         return super.c().getValue();
      }
   }
}
