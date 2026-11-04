package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class jK<K, V> implements fA<K, V> {
   private transient Collection<V> b;
   private transient jK<K, V>.b c;
   private transient fy<K> d;
   private transient jK<K, V>.a e;
   transient Map<K, Collection<V>> a;

   protected jK() {
   }

   protected jK(Map<K, ? extends Collection<V>> var1) {
      if (var1 == null) {
         throw new NullPointerException("Map must not be null.");
      }

      this.a = var1;
   }

   protected Map<K, ? extends Collection<V>> j() {
      return this.a;
   }

   protected final void b(Map<K, ? extends Collection<V>> var1) {
      this.a = var1;
   }

   protected abstract Collection<V> l();

   @Override
   public final boolean c(Object var1) {
      return this.j().containsKey(var1);
   }

   @Override
   public final boolean d(Object var1) {
      return this.g().contains(var1);
   }

   @Override
   public final boolean a(Object var1, Object var2) {
      Collection var3;
      return (var3 = this.j().get(var1)) != null && var3.contains(var2);
   }

   @Override
   public final Collection<Entry<K, V>> d() {
      return this.c != null ? this.c : (this.c = new jK.b((byte)0));
   }

   @Override
   public Collection<V> e(K var1) {
      return this.g((K)var1);
   }

   Collection<V> g(K var1) {
      return new jK.h(var1);
   }

   @Override
   public Collection<V> f(Object var1) {
      return fd.a((Collection<V>)this.j().remove(var1));
   }

   @Override
   public final boolean c(Object var1, Object var2) {
      Collection var3;
      if ((var3 = this.j().get(var1)) == null) {
         return false;
      }

      var2 = var3.remove(var2);
      if (var3.isEmpty()) {
         this.j().remove(var1);
      }

      return var4;
   }

   @Override
   public final boolean b() {
      return this.j().isEmpty();
   }

   @Override
   public final Set<K> f() {
      return this.j().keySet();
   }

   @Override
   public final int a() {
      int var1 = 0;

      for (Collection var3 : this.j().values()) {
         var1 += var3.size();
      }

      return var1;
   }

   @Override
   public final Collection<V> g() {
      jK.f var1 = this.b;
      return this.b != null ? var1 : (this.b = new jK.f((byte)0));
   }

   @Override
   public final void c() {
      this.j().clear();
   }

   @Override
   public final boolean b(K var1, V var2) {
      Collection var3;
      if ((var3 = this.j().get(var1)) == null) {
         if ((var3 = this.l()).add(var2)) {
            this.a.put((K)var1, var3);
            return true;
         } else {
            return false;
         }
      } else {
         return var3.add(var2);
      }
   }

   @Override
   public final boolean a(Map<? extends K, ? extends V> var1) {
      if (var1 == null) {
         throw new NullPointerException("Map must not be null.");
      }

      boolean var2 = false;

      for (Entry var3 : var1.entrySet()) {
         var2 |= this.b((K)var3.getKey(), (V)var3.getValue());
      }

      return var2;
   }

   @Override
   public final boolean a(fA<? extends K, ? extends V> var1) {
      if (var1 == null) {
         throw new NullPointerException("Map must not be null.");
      }

      boolean var2 = false;

      for (Entry var3 : var1.d()) {
         var2 |= this.b((K)var3.getKey(), (V)var3.getValue());
      }

      return var2;
   }

   @Override
   public final fy<K> e() {
      if (this.d == null) {
         this.d = jX.a(new jK.c((byte)0));
      }

      return this.d;
   }

   @Override
   public final Map<K, Collection<V>> h() {
      return this.e != null ? this.e : (this.e = new jK.a(this.a));
   }

   @Override
   public final boolean a(K var1, Iterable<? extends V> var2) {
      if (var2 == null) {
         throw new NullPointerException("Values must not be null.");
      }

      Iterator var3;
      Collection var4;
      return var2 instanceof Collection
         ? !(var4 = (Collection)var2).isEmpty() && this.e((K)var1).addAll(var4)
         : (var3 = var2.iterator()).hasNext() && fd.a(this.e((K)var1), var3);
   }

   @Override
   public final fu<K, V> i() {
      return this.a() == 0 ? hY.d() : new jK.e();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof fA ? this.h().equals(((fA)obj).h()) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.j().hashCode();
   }

   @Override
   public String toString() {
      return this.j().toString();
   }

   protected final void a(ObjectOutputStream var1) {
      var1.writeInt(this.a.size());

      for (Entry var3 : this.a.entrySet()) {
         var1.writeObject(var3.getKey());
         var1.writeInt(((Collection)var3.getValue()).size());

         for (Object var4 : (Collection)var3.getValue()) {
            var1.writeObject(var4);
         }
      }
   }

   protected final void a(ObjectInputStream var1) {
      int var2 = var1.readInt();

      for (int var3 = 0; var3 < var2; var3++) {
         Collection var4 = (Collection)var1.readObject();
         var4 = this.e((K)var4);
         int var5 = var1.readInt();

         for (int var6 = 0; var6 < var5; var6++) {
            Object var7 = var1.readObject();
            var4.add(var7);
         }
      }
   }

   // $VF: synthetic method
   static Map a(jK var0) {
      return var0.a;
   }

   class a extends AbstractMap<K, Collection<V>> {
      final transient Map<K, Collection<V>> a;

      a(Map<K, Collection<V>> var2) {
         this.a = var2;
      }

      @Override
      public final Set<Entry<K, Collection<V>>> entrySet() {
         return new jK.a.a();
      }

      @Override
      public final boolean containsKey(Object key) {
         return this.a.containsKey(key);
      }

      private Collection<V> a(Object var1) {
         if ((Collection)this.a.get(var1) == null) {
            return null;
         }

         var1 = var1;
         return jK.this.g((K)var1);
      }

      @Override
      public final Set<K> keySet() {
         return jK.this.f();
      }

      @Override
      public final int size() {
         return this.a.size();
      }

      private Collection<V> b(Object var1) {
         if ((var1 = this.a.remove(var1)) == null) {
            return null;
         }

         Collection var2;
         (var2 = jK.this.l()).addAll(var1);
         var1.clear();
         return var2;
      }

      @Override
      public final boolean equals(Object object) {
         return this == object || this.a.equals(object);
      }

      @Override
      public final int hashCode() {
         return this.a.hashCode();
      }

      @Override
      public final String toString() {
         return this.a.toString();
      }

      @Override
      public final void clear() {
         jK.this.c();
      }

      // $VF: synthetic method
      @Override
      public final Object remove(Object var1) {
         Collection var2 = var1;
         var1 = this;
         if ((var2 = this.a.remove(var2)) == null) {
            return null;
         }

         Collection var4;
         (var4 = jK.this.l()).addAll(var2);
         var2.clear();
         return var4;
      }

      // $VF: synthetic method
      @Override
      public final Object get(Object var1) {
         Object var2 = var1;
         var1 = this;
         if ((Collection)this.a.get(var2) == null) {
            return null;
         }

         var2 = var2;
         return jK.this.g((K)var2);
      }

      class a extends AbstractSet<Entry<K, Collection<V>>> {
         @Override
         public final Iterator<Entry<K, Collection<V>>> iterator() {
            return a.this.new b(a.this.a.entrySet().iterator());
         }

         @Override
         public final int size() {
            return a.this.size();
         }

         @Override
         public final void clear() {
            a.this.clear();
         }

         @Override
         public final boolean contains(Object o) {
            return a.this.a.entrySet().contains(o);
         }

         @Override
         public final boolean remove(Object o) {
            if (!this.contains(o)) {
               return false;
            }

            o = (Entry)o;
            jK.this.f(o.getKey());
            return true;
         }
      }

      class b extends hN<Entry<K, Collection<V>>> {
         b(Iterator<Entry<K, Collection<V>>> var2) {
            super(var2);
         }

         private Entry<K, Collection<V>> b() {
            Object var1 = ((Entry)super.next()).getKey();
            return new iL<>((K)var1, jK.this.g((K)var1));
         }

         // $VF: synthetic method
         @Override
         public final Object next() {
            jK.a.b var1 = this;
            Object var2 = ((Entry)super.next()).getKey();
            return new iL<>(var2, jK.this.g((K)var2));
         }
      }
   }

   class b extends AbstractCollection<Entry<K, V>> {
      private b() {
      }

      @Override
      public final Iterator<Entry<K, V>> iterator() {
         return new ii<Entry<K, V>>() {
            private Collection<K> b = new ArrayList<>(jK.this.j().keySet());
            private Iterator<K> c = jK.b.super.b.iterator();

            @Override
            public final Iterator<? extends Entry<K, V>> a(int var1) {
               if (!this.c.hasNext()) {
                  return null;
               }

               final Object var3 = this.c.next();
               fQ var2 = new fQ<V, Entry<K, V>>() {
                  private Entry<K, V> b(V var1) {
                     return jK.this.new d(var3, var1);
                  }

                  // $VF: synthetic method
                  @Override
                  public final Object a(Object var1) {
                     Object var2 = var1;
                     var1 = this;
                     return jK.this.new d(var3, var2);
                  }
               };
               return new ix<>(jK.this.new g(var3), var2);
            }
         };
      }

      @Override
      public final int size() {
         return jK.this.a();
      }

      // $VF: synthetic method
      b(byte var2) {
         this();
      }
   }

   class c extends jS<K> {
      private c() {
      }

      @Override
      public final boolean contains(Object o) {
         return jK.this.j().containsKey(o);
      }

      @Override
      public final boolean isEmpty() {
         return jK.this.j().isEmpty();
      }

      @Override
      public final int size() {
         return jK.this.a();
      }

      @Override
      public final int c() {
         return jK.this.j().size();
      }

      @Override
      public final int a(Object var1) {
         int var2 = 0;
         if ((var1 = jK.this.j().get(var1)) != null) {
            var2 = var1.size();
         }

         return var2;
      }

      @Override
      public final Iterator<fy.a<K>> d() {
         jK.c.a var1 = new jK.c.a((byte)0);
         jK var2 = jK.this;
         return fq.a(jK.this.a.entrySet().iterator(), var1);
      }

      // $VF: synthetic method
      c(byte var2) {
         this();
      }

      final class a implements fQ<Entry<K, Collection<V>>, fy.a<K>> {
         private a() {
         }

         private fy.a<K> a(Entry<K, Collection<V>> var1) {
            return new jS.a<K>(this, var1) {
               // $VF: synthetic field
               private Entry a;
               // $VF: synthetic field
               private jK.c.a b;

               {
                  this.b = var1;
                  this.a = var2x;
               }

               @Override
               public final K a() {
                  return (K)this.a.getKey();
               }

               @Override
               public final int b() {
                  return ((Collection)this.a.getValue()).size();
               }
            };
         }

         // $VF: synthetic method
         @Override
         public final Object a(Object var1) {
            Entry var2 = (Entry)var1;
            jK.c.a var3 = this;
            return new jS.a<K>(var3, var2) {
               // $VF: synthetic field
               private Entry a;
               // $VF: synthetic field
               private jK.c.a b;

               {
                  this.b = var1;
                  this.a = var2x;
               }

               @Override
               public final K a() {
                  return (K)this.a.getKey();
               }

               @Override
               public final int b() {
                  return ((Collection)this.a.getValue()).size();
               }
            };
         }

         // $VF: synthetic method
         a(byte var2) {
            this();
         }
      }
   }

   class d extends iF<K, V> {
      public d(K var2, V var3) {
         super((K)var2, (V)var3);
      }

      @Override
      public final V setValue(V value) {
         throw new UnsupportedOperationException();
      }
   }

   class e implements fu<K, V> {
      private final Iterator<Entry<K, V>> a;
      private Entry<K, V> b = null;

      public e() {
         this.a = jK.this.d().iterator();
      }

      @Override
      public final boolean hasNext() {
         return this.a.hasNext();
      }

      @Override
      public final K next() {
         this.b = this.a.next();
         return this.b.getKey();
      }

      @Override
      public final K a() {
         if (this.b == null) {
            throw new IllegalStateException();
         } else {
            return this.b.getKey();
         }
      }

      @Override
      public final V b() {
         if (this.b == null) {
            throw new IllegalStateException();
         } else {
            return this.b.getValue();
         }
      }

      @Override
      public final void remove() {
         this.a.remove();
      }

      @Override
      public final V a(V var1) {
         if (this.b == null) {
            throw new IllegalStateException();
         } else {
            return this.b.setValue((V)var1);
         }
      }
   }

   class f extends AbstractCollection<V> {
      private f() {
      }

      @Override
      public final Iterator<V> iterator() {
         if var1 = new if();

         for (Object var3 : jK.this.f()) {
            var1.a(jK.this.new g(var3));
         }

         return var1;
      }

      @Override
      public final int size() {
         return jK.this.a();
      }

      @Override
      public final void clear() {
         jK.this.c();
      }

      // $VF: synthetic method
      f(byte var2) {
         this();
      }
   }

   class g implements Iterator<V> {
      private final Object a;
      private final Collection<V> b;
      private final Iterator<V> c;

      public g(Object var2) {
         this.a = var2;
         this.b = (Collection<V>)jK.this.j().get(var2);
         this.c = this.b.iterator();
      }

      @Override
      public final void remove() {
         this.c.remove();
         if (this.b.isEmpty()) {
            jK.this.f(this.a);
         }
      }

      @Override
      public final boolean hasNext() {
         return this.c.hasNext();
      }

      @Override
      public final V next() {
         return this.c.next();
      }
   }

   class h implements Collection<V> {
      protected final Object a;

      public h(K var2) {
         this.a = var2;
      }

      protected Collection<V> a() {
         return (Collection<V>)jK.this.j().get(this.a);
      }

      @Override
      public boolean add(V value) {
         Collection var2;
         if ((var2 = this.a()) == null) {
            var2 = jK.this.l();
            jK var3 = jK.this;
            jK.this.a.put((K)this.a, var2);
         }

         return var2.add(value);
      }

      @Override
      public boolean addAll(Collection<? extends V> other) {
         Collection var2;
         if ((var2 = this.a()) == null) {
            var2 = jK.this.l();
            jK var3 = jK.this;
            jK.this.a.put((K)this.a, var2);
         }

         return var2.addAll(other);
      }

      @Override
      public void clear() {
         Collection var1;
         if ((var1 = this.a()) != null) {
            var1.clear();
            jK.this.f(this.a);
         }
      }

      @Override
      public Iterator<V> iterator() {
         return this.a() == null ? fq.a : jK.this.new g(this.a);
      }

      @Override
      public int size() {
         Collection var1;
         return (var1 = this.a()) == null ? 0 : var1.size();
      }

      @Override
      public boolean contains(Object obj) {
         Collection var2;
         return (var2 = this.a()) == null ? false : var2.contains(obj);
      }

      @Override
      public boolean containsAll(Collection<?> other) {
         Collection var2;
         return (var2 = this.a()) == null ? false : var2.containsAll(other);
      }

      @Override
      public boolean isEmpty() {
         Collection var1;
         return (var1 = this.a()) == null ? true : var1.isEmpty();
      }

      @Override
      public boolean remove(Object item) {
         Collection var2;
         if ((var2 = this.a()) == null) {
            return false;
         }

         item = var2.remove(item);
         if (var2.isEmpty()) {
            jK.this.f(this.a);
         }

         return var3;
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         Collection var2;
         if ((var2 = this.a()) == null) {
            return false;
         }

         boolean var3 = var2.removeAll(c);
         if (var2.isEmpty()) {
            jK.this.f(this.a);
         }

         return var3;
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         Collection var2;
         if ((var2 = this.a()) == null) {
            return false;
         }

         boolean var3 = var2.retainAll(c);
         if (var2.isEmpty()) {
            jK.this.f(this.a);
         }

         return var3;
      }

      @Override
      public Object[] toArray() {
         Collection var1;
         return (var1 = this.a()) == null ? fd.a.toArray() : var1.toArray();
      }

      @Override
      public <T> T[] toArray(T[] a) {
         Collection var2;
         return (T[])((var2 = this.a()) == null ? fd.a.toArray(a) : var2.toArray(a));
      }

      @Override
      public String toString() {
         Collection var1;
         return (var1 = this.a()) == null ? fd.a.toString() : var1.toString();
      }
   }
}
