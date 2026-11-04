package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class fW<E> implements eW<E> {
   private transient Map<E, fW.b> a;
   private int b;
   private transient int c;
   private transient Set<E> d;

   protected fW() {
   }

   protected fW(Map<E, fW.b> var1) {
      this.a = var1;
   }

   protected Map<E, fW.b> e() {
      return this.a;
   }

   @Override
   public int size() {
      return this.b;
   }

   @Override
   public boolean isEmpty() {
      return this.a.isEmpty();
   }

   @Override
   public final int a(Object var1) {
      fW.b var2;
      return (var2 = this.a.get(var1)) != null ? var2.a : 0;
   }

   @Override
   public boolean contains(Object object) {
      return this.a.containsKey(object);
   }

   @Override
   public boolean containsAll(Collection<?> coll) {
      return coll instanceof eW ? this.a((eW<?>)coll) : this.a(new ga<>(coll));
   }

   private boolean a(eW<?> var1) {
      for (Object var3 : var1.a()) {
         if (this.a(var3) < var1.a(var3)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public Iterator<E> iterator() {
      return new fW.a<>(this);
   }

   @Override
   public boolean add(E object) {
      return this.a(object, 1);
   }

   @Override
   public final boolean a(E var1, int var2) {
      this.c++;
      if (var2 > 0) {
         fW.b var3 = this.a.get(var1);
         this.b += var2;
         if (var3 == null) {
            this.a.put((E)var1, new fW.b(var2));
            return true;
         } else {
            var3.a += var2;
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean addAll(Collection<? extends E> coll) {
      boolean var2 = false;
      Iterator var4 = coll.iterator();

      while (var4.hasNext()) {
         boolean var3 = this.add((E)var4.next());
         var2 = var2 || var3;
      }

      return var2;
   }

   @Override
   public void clear() {
      this.c++;
      this.a.clear();
      this.b = 0;
   }

   @Override
   public boolean remove(Object object) {
      fW.b var2;
      if ((var2 = this.a.get(object)) == null) {
         return false;
      }

      this.c++;
      this.a.remove(object);
      this.b = this.b - var2.a;
      return true;
   }

   @Override
   public final boolean b(Object var1, int var2) {
      fW.b var3;
      if ((var3 = this.a.get(var1)) == null) {
         return false;
      }

      if (var2 <= 0) {
         return false;
      }

      this.c++;
      if (var2 < var3.a) {
         var3.a -= var2;
         this.b -= var2;
      } else {
         this.a.remove(var1);
         this.b = this.b - var3.a;
      }

      return true;
   }

   @Override
   public boolean removeAll(Collection<?> coll) {
      boolean var2 = false;
      if (coll != null) {
         Iterator var4 = coll.iterator();

         while (var4.hasNext()) {
            boolean var3 = this.b(var4.next(), 1);
            var2 = var2 || var3;
         }
      }

      return var2;
   }

   @Override
   public boolean retainAll(Collection<?> coll) {
      return coll instanceof eW ? this.b((eW<?>)coll) : this.b(new ga<>(coll));
   }

   private boolean b(eW<?> var1) {
      boolean var2 = false;
      ga var3 = new ga();

      for (Object var5 : this.a()) {
         int var6 = this.a(var5);
         int var7 = var1.a(var5);
         if (var7 > 0 && var7 <= var6) {
            var3.a(var5, var6 - var7);
         } else {
            var3.a(var5, var6);
         }
      }

      if (!var3.isEmpty()) {
         var2 = this.removeAll(var3);
      }

      return var2;
   }

   @Override
   public Object[] toArray() {
      Object[] var1 = new Object[this.size()];
      int var2 = 0;

      for (Object var4 : this.a.keySet()) {
         for (int var5 = this.a(var4); var5 > 0; var5--) {
            var1[var2++] = var4;
         }
      }

      return var1;
   }

   @Override
   public <T> T[] toArray(T[] array) {
      int var2 = this.size();
      if (array.length < var2) {
         array = (T[])Array.newInstance(array.getClass().getComponentType(), var2);
      }

      var2 = 0;

      for (Object var4 : this.a.keySet()) {
         for (int var5 = this.a(var4); var5 > 0; var5--) {
            Object var6 = var4;
            array[var2++] = (T)var6;
         }
      }

      while (var2 < array.length) {
         array[var2++] = null;
      }

      return array;
   }

   @Override
   public final Set<E> a() {
      if (this.d == null) {
         this.d = kC.a(this.a.keySet());
      }

      return this.d;
   }

   protected final void a(ObjectOutputStream var1) {
      var1.writeInt(this.a.size());

      for (Entry var3 : this.a.entrySet()) {
         var1.writeObject(var3.getKey());
         var1.writeInt(((fW.b)var3.getValue()).a);
      }
   }

   protected final void a(Map<E, fW.b> var1, ObjectInputStream var2) {
      this.a = var1;
      int var3 = var2.readInt();

      for (int var4 = 0; var4 < var3; var4++) {
         Object var5 = var2.readObject();
         int var6 = var2.readInt();
         var1.put(var5, new fW.b(var6));
         this.b += var6;
      }
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      if (!(object instanceof eW)) {
         return false;
      }

      if (((eW)(object = (eW)object)).size() != this.size()) {
         return false;
      }

      for (Object var3 : this.a.keySet()) {
         if (((eW)object).a(var3) != this.a(var3)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int hashCode() {
      int var1 = 0;
      Iterator var2 = this.a.entrySet().iterator();

      while (var2.hasNext()) {
         Entry var3;
         Object var4 = (var3 = (Entry)var2.next()).getKey();
         fW.b var5 = (fW.b)var3.getValue();
         var1 += (var4 == null ? 0 : var4.hashCode()) ^ var5.a;
      }

      return var1;
   }

   @Override
   public String toString() {
      if (this.size() == 0) {
         return "[]";
      }

      StringBuilder var1;
      (var1 = new StringBuilder()).append('[');
      Iterator var2 = this.a().iterator();

      while (var2.hasNext()) {
         Object var3 = var2.next();
         int var4 = this.a(var3);
         var1.append(var4);
         var1.append(':');
         var1.append(var3);
         if (var2.hasNext()) {
            var1.append(',');
         }
      }

      var1.append(']');
      return var1.toString();
   }

   static class a<E> implements Iterator<E> {
      private final fW<E> a;
      private final Iterator<Entry<E, fW.b>> b;
      private Entry<E, fW.b> c;
      private int d;
      private final int e;
      private boolean f;

      public a(fW<E> var1) {
         this.a = var1;
         this.b = var1.a.entrySet().iterator();
         this.c = null;
         this.e = var1.c;
         this.f = false;
      }

      @Override
      public final boolean hasNext() {
         return this.d > 0 || this.b.hasNext();
      }

      @Override
      public final E next() {
         if (this.a.c != this.e) {
            throw new ConcurrentModificationException();
         }

         if (this.d == 0) {
            this.c = this.b.next();
            this.d = this.c.getValue().a;
         }

         this.f = true;
         this.d--;
         return this.c.getKey();
      }

      @Override
      public final void remove() {
         if (this.a.c != this.e) {
            throw new ConcurrentModificationException();
         }

         if (!this.f) {
            throw new IllegalStateException();
         }

         fW.b var1;
         if ((var1 = this.c.getValue()).a > 1) {
            var1.a--;
         } else {
            this.b.remove();
         }

         this.a.b--;
         this.f = false;
      }
   }

   protected static class b {
      protected int a;

      b(int var1) {
         this.a = var1;
      }

      @Override
      public final boolean equals(Object obj) {
         return !(obj instanceof fW.b) ? false : ((fW.b)obj).a == this.a;
      }

      @Override
      public final int hashCode() {
         return this.a;
      }
   }
}
