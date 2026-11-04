package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class jR<E> extends jS<E> {
   private transient Map<E, jR.d> a;
   private transient int b;
   private transient int c;

   protected jR() {
   }

   protected jR(Map<E, jR.d> var1) {
      this.a = var1;
   }

   private Map<E, jR.d> f() {
      return this.a;
   }

   protected final void a(Map<E, jR.d> var1) {
      this.a = var1;
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
      jR.d var2;
      return (var2 = this.a.get(var1)) != null ? var2.a : 0;
   }

   @Override
   public boolean contains(Object object) {
      return this.a.containsKey(object);
   }

   @Override
   public Iterator<E> iterator() {
      return new jR.b<>(this);
   }

   @Override
   public final int b(E var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("Occurrences must not be negative.");
      }

      jR.d var3;
      int var4 = (var3 = this.a.get(var1)) != null ? var3.a : 0;
      if (var2 > 0) {
         this.c++;
         this.b += var2;
         if (var3 == null) {
            this.a.put((E)var1, new jR.d(var2));
         } else {
            var3.a += var2;
         }
      }

      return var4;
   }

   @Override
   public void clear() {
      this.c++;
      this.a.clear();
      this.b = 0;
   }

   @Override
   public final int c(Object var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("Occurrences must not be negative.");
      }

      jR.d var3;
      if ((var3 = this.a.get(var1)) == null) {
         return 0;
      }

      int var4 = var3.a;
      if (var2 > 0) {
         this.c++;
         if (var2 < var3.a) {
            var3.a -= var2;
            this.b -= var2;
         } else {
            this.a.remove(var1);
            this.b = this.b - var3.a;
         }
      }

      return var4;
   }

   @Override
   protected final Iterator<E> e() {
      return new jR.e<>(this.a.keySet().iterator(), this);
   }

   @Override
   protected final int c() {
      return this.a.size();
   }

   @Override
   protected final Iterator<fy.a<E>> d() {
      return new jR.a<>(this.a.entrySet().iterator(), this);
   }

   @Override
   protected final void a(ObjectOutputStream var1) {
      var1.writeInt(this.a.size());

      for (Entry var3 : this.a.entrySet()) {
         var1.writeObject(var3.getKey());
         var1.writeInt(((jR.d)var3.getValue()).a);
      }
   }

   @Override
   protected final void a(ObjectInputStream var1) {
      int var2 = var1.readInt();

      for (int var3 = 0; var3 < var2; var3++) {
         Object var4 = var1.readObject();
         int var5 = var1.readInt();
         this.a.put((E)var4, new jR.d(var5));
         this.b += var5;
      }
   }

   @Override
   public Object[] toArray() {
      Object[] var1 = new Object[this.size()];
      int var2 = 0;
      Iterator var3 = this.a.entrySet().iterator();

      while (var3.hasNext()) {
         Entry var4;
         Object var5 = (var4 = (Entry)var3.next()).getKey();

         for (int var6 = ((jR.d)var4.getValue()).a; var6 > 0; var6--) {
            var1[var2++] = var5;
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
      Iterator var3 = this.a.entrySet().iterator();

      while (var3.hasNext()) {
         Entry var4;
         Object var5 = (var4 = (Entry)var3.next()).getKey();

         for (int var8 = ((jR.d)var4.getValue()).a; var8 > 0; var8--) {
            Object var6 = var5;
            array[var2++] = (T)var6;
         }
      }

      while (var2 < array.length) {
         array[var2++] = null;
      }

      return array;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      if (!(object instanceof fy)) {
         return false;
      }

      if (((fy)(object = (fy)object)).size() != this.size()) {
         return false;
      }

      for (Object var3 : this.a.keySet()) {
         if (((fy)object).a(var3) != this.a(var3)) {
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
         jR.d var5 = (jR.d)var3.getValue();
         var1 += (var4 == null ? 0 : var4.hashCode()) ^ var5.a;
      }

      return var1;
   }

   protected static class a<E> implements Iterator<fy.a<E>> {
      private jR<E> a;
      private Iterator<Entry<E, jR.d>> b;
      private fy.a<E> c = null;
      private boolean d = false;

      protected a(Iterator<Entry<E, jR.d>> var1, jR<E> var2) {
         this.b = var1;
         this.a = var2;
      }

      @Override
      public final boolean hasNext() {
         return this.b.hasNext();
      }

      private fy.a<E> a() {
         this.c = new jR.c<>(this.b.next());
         this.d = true;
         return this.c;
      }

      @Override
      public final void remove() {
         if (!this.d) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         }

         this.b.remove();
         this.c = null;
         this.d = false;
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         jR.a var1 = this;
         this.c = new jR.c<>(var1.b.next());
         var1.d = true;
         return var1.c;
      }
   }

   static class b<E> implements Iterator<E> {
      private final jR<E> a;
      private final Iterator<Entry<E, jR.d>> b;
      private Entry<E, jR.d> c;
      private int d;
      private final int e;
      private boolean f;

      public b(jR<E> var1) {
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

         jR.d var1;
         if ((var1 = this.c.getValue()).a > 1) {
            var1.a--;
         } else {
            this.b.remove();
         }

         this.a.b--;
         this.f = false;
      }
   }

   protected static class c<E> extends jS.a<E> {
      private Entry<E, jR.d> a;

      protected c(Entry<E, jR.d> var1) {
         this.a = var1;
      }

      @Override
      public final E a() {
         return this.a.getKey();
      }

      @Override
      public final int b() {
         return this.a.getValue().a;
      }
   }

   protected static class d {
      protected int a;

      d(int var1) {
         this.a = var1;
      }

      @Override
      public final boolean equals(Object obj) {
         return !(obj instanceof jR.d) ? false : ((jR.d)obj).a == this.a;
      }

      @Override
      public final int hashCode() {
         return this.a;
      }
   }

   protected static class e<E> extends hN<E> {
      private jR<E> a;
      private E b = (E)null;
      private boolean c = false;

      protected e(Iterator<E> var1, jR<E> var2) {
         super(var1);
         this.a = var2;
      }

      @Override
      public final E next() {
         this.b = super.next();
         this.c = true;
         return this.b;
      }

      @Override
      public final void remove() {
         if (!this.c) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         }

         int var1 = this.a.a(this.b);
         super.remove();
         this.a.c(this.b, var1);
         this.b = null;
         this.c = false;
      }
   }
}
