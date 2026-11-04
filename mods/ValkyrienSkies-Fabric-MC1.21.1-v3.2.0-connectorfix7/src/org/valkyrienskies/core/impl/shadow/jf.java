package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public abstract class jf<K, V> extends iZ<K, V> {
   private jf.h j;
   private jf.h k;
   private boolean l;
   private transient ReferenceQueue<Object> m;

   protected jf() {
   }

   protected jf(jf.h var1, jf.h var2, int var3, float var4, boolean var5) {
      super(var3, var4);
      this.j = var1;
      this.k = var2;
      this.l = var5;
   }

   @Override
   protected final void d() {
      this.m = new ReferenceQueue<>();
   }

   @Override
   public int size() {
      this.e();
      return super.size();
   }

   @Override
   public boolean isEmpty() {
      this.e();
      return super.isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      this.e();
      iZ.c var3;
      return (var3 = this.e(key)) == null ? false : var3.getValue() != null;
   }

   @Override
   public boolean containsValue(Object value) {
      this.e();
      return value == null ? false : super.containsValue(value);
   }

   @Override
   public V get(Object key) {
      this.e();
      iZ.c var3;
      return (V)((var3 = this.e(key)) == null ? null : var3.getValue());
   }

   @Override
   public V put(K key, V value) {
      if (key == null) {
         throw new NullPointerException("null keys not allowed");
      }

      if (value == null) {
         throw new NullPointerException("null values not allowed");
      }

      this.e();
      return super.put(key, value);
   }

   @Override
   public V remove(Object key) {
      if (key == null) {
         return null;
      }

      this.e();
      return super.remove(key);
   }

   @Override
   public void clear() {
      super.clear();

      while (this.m.poll() != null) {
      }
   }

   @Override
   public final fu<K, V> c() {
      return new jf.g<>(this);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      if (super.g == null) {
         super.g = new jf.c<>(this);
      }

      return super.g;
   }

   @Override
   public Set<K> keySet() {
      if (super.h == null) {
         super.h = new jf.e<>(this);
      }

      return super.h;
   }

   @Override
   public Collection<V> values() {
      if (super.i == null) {
         super.i = new jf.i<>(this);
      }

      return super.i;
   }

   private void a() {
      this.e();
   }

   private void b() {
      this.e();
   }

   private void e() {
      for (Reference var1 = this.m.poll(); var1 != null; var1 = this.m.poll()) {
         this.a(var1);
      }
   }

   private void a(Reference<?> var1) {
      int var10000 = var1.hashCode();
      int var3 = super.d.length;
      int var2 = var10000 & var3 - 1;
      iZ.c var5 = null;

      for (iZ.c var4 = super.d[var2]; var4 != null; var4 = var4.a) {
         if (((jf.b)var4).a(var1)) {
            if (var5 == null) {
               super.d[var2] = var4.a;
            } else {
               var5.a = var4.a;
            }

            super.c--;
            return;
         }

         var5 = var4;
      }
   }

   @Override
   protected final iZ.c<K, V> e(Object var1) {
      return var1 == null ? null : super.e(var1);
   }

   protected int c(Object var1, Object var2) {
      return (var1 == null ? 0 : var1.hashCode()) ^ (var2 == null ? 0 : var2.hashCode());
   }

   @Override
   protected boolean a(Object var1, Object var2) {
      var2 = this.j == jf.h.HARD ? var2 : ((Reference)var2).get();
      return var1 == var2 || var1.equals(var2);
   }

   private jf.b<K, V> b(iZ.c<K, V> var1, int var2, K var3, V var4) {
      return new jf.b<>(this, var1, var2, (K)var3, (V)var4);
   }

   @Override
   protected final Iterator<Entry<K, V>> f() {
      return new jf.d<>(this);
   }

   @Override
   protected final Iterator<K> g() {
      return new jf.f<>(this);
   }

   @Override
   protected final Iterator<V> h() {
      return new jf.j<>(this);
   }

   @Override
   protected final void a(ObjectOutputStream var1) {
      var1.writeInt(this.j.value);
      var1.writeInt(this.k.value);
      var1.writeBoolean(this.l);
      var1.writeFloat(super.b);
      var1.writeInt(super.d.length);
      fu var2 = this.c();

      while (var2.hasNext()) {
         var1.writeObject(var2.next());
         var1.writeObject(var2.b());
      }

      var1.writeObject(null);
   }

   @Override
   protected final void a(ObjectInputStream var1) {
      this.j = jf.h.a(var1.readInt());
      this.k = jf.h.a(var1.readInt());
      this.l = var1.readBoolean();
      super.b = var1.readFloat();
      int var2 = var1.readInt();
      this.d();
      super.d = new iZ.c[var2];
      int var10001 = super.d.length;
      float var3 = super.b;
      super.e = (int)(var10001 * var3);

      while ((var4 = var1.readObject()) != null) {
         Object var5 = var1.readObject();
         this.put((K)var4, (V)var5);
      }
   }

   protected final boolean a(jf.h var1) {
      return this.j == var1;
   }

   // $VF: synthetic method
   @Override
   protected final iZ.c a(iZ.c var1, int var2, Object var3, Object var4) {
      Object var5 = var4;
      var4 = var3;
      var3 = var2;
      iZ.c var7 = var1;
      jf var6 = this;
      return new jf.b<>(var6, var7, var3, var4, var5);
   }

   static class a<K, V> {
      private jf<K, V> a;
      private int b;
      private jf.b<K, V> c;
      private jf.b<K, V> d;
      private K e;
      private K f;
      private V g;
      private V h;
      private int i;

      public a(jf<K, V> var1) {
         this.a = var1;
         this.b = var1.size() != 0 ? var1.d.length : 0;
         this.i = var1.f;
      }

      public boolean hasNext() {
         this.a();

         while (this.b()) {
            jf.b var1 = this.c;
            int var2 = this.b;

            while (var1 == null && var2 > 0) {
               var1 = (jf.b)this.a.d[--var2];
            }

            this.c = var1;
            this.b = var2;
            if (var1 == null) {
               this.e = null;
               this.g = null;
               return false;
            }

            this.f = (K)var1.getKey();
            this.h = (V)var1.getValue();
            if (this.b()) {
               this.c = this.c.a();
            }
         }

         return true;
      }

      private void a() {
         if (this.a.f != this.i) {
            throw new ConcurrentModificationException();
         }
      }

      private boolean b() {
         return this.f == null || this.h == null;
      }

      protected final jf.b<K, V> c() {
         this.a();
         if (this.b() && !this.hasNext()) {
            throw new NoSuchElementException();
         }

         this.d = this.c;
         this.c = this.c.a();
         this.e = this.f;
         this.g = this.h;
         this.f = null;
         this.h = null;
         return this.d;
      }

      protected final jf.b<K, V> d() {
         this.a();
         return this.d;
      }

      public void remove() {
         this.a();
         if (this.d == null) {
            throw new IllegalStateException();
         }

         this.a.remove(this.e);
         this.d = null;
         this.e = null;
         this.g = null;
         this.i = this.a.f;
      }
   }

   protected static class b<K, V> extends iZ.c<K, V> {
      private final jf<K, V> e;

      public b(jf<K, V> var1, iZ.c<K, V> var2, int var3, K var4, V var5) {
         super(var2, var3, null, null);
         this.e = var1;
         super.c = this.a(var1.j, var4, var3);
         super.d = this.a(var1.k, var5, var3);
      }

      @Override
      public final K getKey() {
         return (K)(this.e.j == jf.h.HARD ? super.c : ((Reference)super.c).get());
      }

      @Override
      public final V getValue() {
         return (V)(this.e.k == jf.h.HARD ? super.d : ((Reference)super.d).get());
      }

      @Override
      public final V setValue(V obj) {
         Object var2 = this.getValue();
         if (this.e.k != jf.h.HARD) {
            ((Reference)super.d).clear();
         }

         super.d = this.a(this.e.k, obj, super.b);
         return (V)var2;
      }

      @Override
      public final boolean equals(Object obj) {
         if (obj == this) {
            return true;
         }

         if (!(obj instanceof Entry)) {
            return false;
         }

         Entry var3;
         Object var2 = (var3 = (Entry)obj).getKey();
         obj = var3.getValue();
         return var2 != null && obj != null ? this.e.a(var2, super.c) && this.e.b(obj, this.getValue()) : false;
      }

      @Override
      public final int hashCode() {
         return this.e.c(this.getKey(), this.getValue());
      }

      private <T> Object a(jf.h var1, T var2, int var3) {
         if (var1 == jf.h.HARD) {
            return var2;
         } else if (var1 == jf.h.SOFT) {
            return new jf.k<>(var3, var2, this.e.m);
         } else if (var1 == jf.h.WEAK) {
            return new jf.l<>(var3, var2, this.e.m);
         } else {
            throw new Error();
         }
      }

      final boolean a(Reference<?> var1) {
         boolean var2;
         if (var2 = this.e.j != jf.h.HARD && super.c == var1 || this.e.k != jf.h.HARD && super.d == var1) {
            if (this.e.j != jf.h.HARD) {
               ((Reference)super.c).clear();
            }

            if (this.e.k != jf.h.HARD) {
               ((Reference)super.d).clear();
            } else if (this.e.l) {
               super.d = null;
            }
         }

         return var2;
      }

      protected final jf.b<K, V> a() {
         return (jf.b<K, V>)super.a;
      }
   }

   static class c<K, V> extends iZ.a<K, V> {
      protected c(iZ<K, V> var1) {
         super(var1);
      }

      @Override
      public final Object[] toArray() {
         return this.toArray(new Object[this.size()]);
      }

      @Override
      public final <T> T[] toArray(T[] arr) {
         ArrayList var2 = new ArrayList(this.size());

         for (Entry var4 : this) {
            var2.add(new iI(var4));
         }

         return (T[])var2.toArray(arr);
      }
   }

   static class d<K, V> extends jf.a<K, V> implements Iterator<Entry<K, V>> {
      public d(jf<K, V> var1) {
         super(var1);
      }

      private Entry<K, V> a() {
         return this.c();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         return this.c();
      }
   }

   static class e<K> extends iZ.f<K> {
      protected e(iZ<K, ?> var1) {
         super(var1);
      }

      @Override
      public final Object[] toArray() {
         return this.toArray(new Object[this.size()]);
      }

      @Override
      public final <T> T[] toArray(T[] arr) {
         ArrayList var2 = new ArrayList(this.size());

         for (Object var4 : this) {
            var2.add(var4);
         }

         return (T[])var2.toArray(arr);
      }
   }

   static class f<K> extends jf.a<K, Object> implements Iterator<K> {
      f(jf<K, ?> var1) {
         super(var1);
      }

      @Override
      public final K next() {
         return this.c().getKey();
      }
   }

   static class g<K, V> extends jf.a<K, V> implements fu<K, V> {
      protected g(jf<K, V> var1) {
         super(var1);
      }

      @Override
      public final K next() {
         return this.c().getKey();
      }

      @Override
      public final K a() {
         jf.b var1;
         if ((var1 = this.d()) == null) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return (K)var1.getKey();
         }
      }

      @Override
      public final V b() {
         jf.b var1;
         if ((var1 = this.d()) == null) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return (V)var1.getValue();
         }
      }

      @Override
      public final V a(V var1) {
         jf.b var2;
         if ((var2 = this.d()) == null) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return (V)var2.setValue(var1);
         }
      }
   }

   public enum h {
      HARD(0),
      SOFT(1),
      WEAK(2);

      public final int value;

      public static jf.h a(int var0) {
         switch (var0) {
            case 0:
               return HARD;
            case 1:
               return SOFT;
            case 2:
               return WEAK;
            default:
               throw new IllegalArgumentException();
         }
      }

      h(int var3) {
         this.value = var3;
      }
   }

   static class i<V> extends iZ.h<V> {
      protected i(iZ<?, V> var1) {
         super(var1);
      }

      @Override
      public final Object[] toArray() {
         return this.toArray(new Object[this.size()]);
      }

      @Override
      public final <T> T[] toArray(T[] arr) {
         ArrayList var2 = new ArrayList(this.size());

         for (Object var4 : this) {
            var2.add(var4);
         }

         return (T[])var2.toArray(arr);
      }
   }

   static class j<V> extends jf.a<Object, V> implements Iterator<V> {
      j(jf<?, V> var1) {
         super(var1);
      }

      @Override
      public final V next() {
         return this.c().getValue();
      }
   }

   static class k<T> extends SoftReference<T> {
      private final int a;

      public k(int var1, T var2, ReferenceQueue<? super T> var3) {
         super((T)var2, var3);
         this.a = var1;
      }

      @Override
      public final int hashCode() {
         return this.a;
      }
   }

   static class l<T> extends WeakReference<T> {
      private final int a;

      public l(int var1, T var2, ReferenceQueue<? super T> var3) {
         super((T)var2, var3);
         this.a = var1;
      }

      @Override
      public final int hashCode() {
         return this.a;
      }
   }
}
