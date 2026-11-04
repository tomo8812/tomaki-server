package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

@Deprecated
public final class jv<K, V> extends jd<K, Object> implements Serializable, fw<K, V> {
   private static final long b = -2214159910087182007L;
   private final fh<? extends Collection<V>> c;
   private transient Collection<V> d;

   public static <K, V> jv<K, V> a(Map<K, ? super Collection<V>> var0) {
      return a(var0, ArrayList.class);
   }

   public static <K, V, C extends Collection<V>> jv<K, V> a(Map<K, ? super C> var0, Class<C> var1) {
      return new jv<>(var0, new jv.a<>(var1));
   }

   public static <K, V, C extends Collection<V>> jv<K, V> a(Map<K, ? super C> var0, fh<C> var1) {
      return new jv<>(var0, var1);
   }

   public jv() {
      this(new HashMap<>(), new jv.a<>(ArrayList.class));
   }

   private <C extends Collection<V>> jv(Map<K, ? super C> var1, fh<C> var2) {
      super(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("The factory must not be null");
      }

      this.c = var2;
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
   public final void clear() {
      this.i().clear();
   }

   @Override
   public final boolean a(Object var1, Object var2) {
      Collection var3;
      if ((var3 = this.a(var1)) == null) {
         return false;
      }

      if (!var3.remove(var2)) {
         return false;
      }

      if (var3.isEmpty()) {
         this.remove(var1);
      }

      return true;
   }

   @Override
   public final boolean containsValue(Object value) {
      Set var2;
      if ((var2 = this.i().entrySet()) != null) {
         Iterator var4 = var2.iterator();

         while (var4.hasNext()) {
            if (((Collection)((Entry)var4.next()).getValue()).contains(value)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public final Object put(K key, Object value) {
      boolean var3 = false;
      Collection var4;
      if ((var4 = this.a(key)) == null) {
         (var4 = this.d()).add(value);
         if (var4.size() > 0) {
            this.i().put(key, (V)var4);
            var3 = true;
         }
      } else {
         var3 = var4.add(value);
      }

      return var3 ? value : null;
   }

   @Override
   public final void putAll(Map<? extends K, ?> map) {
      if (map instanceof fw) {
         for (Entry var9 : ((fw)map).entrySet()) {
            Collection var4 = (Collection)var9.getValue();
            Object var3 = var9.getKey();
            jv var10 = this;
            if (var4 != null && var4.size() != 0) {
               boolean var5 = false;
               Collection var6;
               if ((var6 = var10.a(var3)) == null) {
                  var4.size();
                  (var6 = var10.d()).addAll(var4);
                  if (var6.size() > 0) {
                     var10.i().put(var3, var6);
                     var5 = true;
                  }
               } else {
                  var5 = var6.addAll(var4);
               }
            } else {
               boolean var10000 = false;
            }
         }
      } else {
         for (Entry var2 : map.entrySet()) {
            this.put((K)var2.getKey(), (V)var2.getValue());
         }
      }
   }

   @Override
   public final Set<Entry<K, Object>> entrySet() {
      return super.entrySet();
   }

   @Override
   public final Collection<Object> values() {
      jv.b var1 = this.d;
      return this.d != null ? var1 : (this.d = new jv.b((byte)0));
   }

   private boolean b(Object var1, Object var2) {
      Collection var3;
      return (var3 = this.a(var1)) == null ? false : var3.contains(var2);
   }

   public final Collection<V> a(Object var1) {
      return (Collection<V>)this.i().get(var1);
   }

   private int b(Object var1) {
      Collection var2;
      return (var2 = this.a(var1)) == null ? 0 : var2.size();
   }

   private boolean a(K var1, Collection<V> var2) {
      if (var2 != null && var2.size() != 0) {
         boolean var3 = false;
         Collection var4;
         if ((var4 = this.a(var1)) == null) {
            var2.size();
            (var4 = this.d()).addAll(var2);
            if (var4.size() > 0) {
               this.i().put((K)var1, (V)var4);
               var3 = true;
            }
         } else {
            var3 = var4.addAll(var2);
         }

         return var3;
      } else {
         return false;
      }
   }

   private Iterator<V> c(Object var1) {
      return !this.containsKey(var1) ? hW.b() : new jv.c(var1);
   }

   private Iterator<Entry<K, V>> b() {
      final Iterator var1 = new ArrayList<>(this.keySet()).iterator();
      return new ii<Entry<K, V>>() {
         @Override
         public final Iterator<? extends Entry<K, V>> a(int var1x) {
            if (!var1.hasNext()) {
               return null;
            }

            final Object var3 = var1.next();
            fQ var2 = new fQ<V, Entry<K, V>>() {
               private Entry<K, V> b(V var1x) {
                  return new Entry<K, V>(this, var1x) {
                     // $VF: synthetic field
                     private Object a;
                     // $VF: synthetic field
                     private <unrepresentable> b;

                     {
                        this.b = var1x;
                        this.a = var2x;
                     }

                     @Override
                     public final K getKey() {
                        return (K)var3;
                     }

                     @Override
                     public final V getValue() {
                        return (V)this.a;
                     }

                     @Override
                     public final V setValue(V value) {
                        throw new UnsupportedOperationException();
                     }
                  };
               }

               // $VF: synthetic method
               @Override
               public final Object a(Object var1x) {
                  Object var2 = var1x;
                  var1x = this;
                  return new Entry<K, V>(var1x, var2) {
                     // $VF: synthetic field
                     private Object a;
                     // $VF: synthetic field
                     private <unrepresentable> b;

                     {
                        this.b = var1x;
                        this.a = var2x;
                     }

                     @Override
                     public final K getKey() {
                        return (K)var3;
                     }

                     @Override
                     public final V getValue() {
                        return (V)this.a;
                     }

                     @Override
                     public final V setValue(V value) {
                        throw new UnsupportedOperationException();
                     }
                  };
               }
            };
            return new ix<>(jv.this.new c(var3), var2);
         }
      };
   }

   public final int a() {
      int var1 = 0;

      for (Object var3 : this.i().values()) {
         var1 += fd.a(var3);
      }

      return var1;
   }

   private Collection<V> d() {
      return (Collection<V>)this.c.a();
   }

   static class a<T extends Collection<?>> implements Serializable, fh<T> {
      private static final long a = 2986114157496788874L;
      private final Class<T> b;

      public a(Class<T> var1) {
         this.b = var1;
      }

      private T b() {
         try {
            return this.b.newInstance();
         } catch (Exception var2) {
            throw new fk("Cannot instantiate class: " + this.b, var2);
         }
      }

      private void a(ObjectInputStream var1) {
         var1.defaultReadObject();
         if (this.b != null && !Collection.class.isAssignableFrom(this.b)) {
            throw new UnsupportedOperationException();
         }
      }

      // $VF: synthetic method
      @Override
      public final Object a() {
         return this.b();
      }
   }

   class b extends AbstractCollection<V> {
      private b() {
      }

      @Override
      public final Iterator<V> iterator() {
         if var1 = new if();

         for (Object var3 : jv.this.keySet()) {
            var1.a(jv.this.new c(var3));
         }

         return var1;
      }

      @Override
      public final int size() {
         return jv.this.a();
      }

      @Override
      public final void clear() {
         jv.this.clear();
      }

      // $VF: synthetic method
      b(byte var2) {
         this();
      }
   }

   class c implements Iterator<V> {
      private final Object a;
      private final Collection<V> b;
      private final Iterator<V> c;

      public c(Object var2) {
         this.a = var2;
         this.b = jv.this.a(var2);
         this.c = this.b.iterator();
      }

      @Override
      public final void remove() {
         this.c.remove();
         if (this.b.isEmpty()) {
            jv.this.remove(this.a);
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
}
