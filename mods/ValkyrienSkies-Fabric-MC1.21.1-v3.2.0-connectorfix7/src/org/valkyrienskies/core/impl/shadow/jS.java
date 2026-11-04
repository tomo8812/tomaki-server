package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class jS<E> extends AbstractCollection<E> implements fy<E> {
   private transient Set<E> a;
   private transient Set<fy.a<E>> b;

   @Override
   public int size() {
      int var1 = 0;

      for (fy.a var3 : this.b()) {
         var1 += var3.b();
      }

      return var1;
   }

   @Override
   public int a(Object var1) {
      Iterator var2 = this.b().iterator();

      while (var2.hasNext()) {
         fy.a var3;
         Object var4;
         if ((var4 = (var3 = (fy.a)var2.next()).a()) == var1 || var4 != null && var4.equals(var1)) {
            return var3.b();
         }
      }

      return 0;
   }

   @Override
   public final int a(E var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("Count must not be negative.");
      }

      int var3;
      if ((var3 = this.a(var1)) < var2) {
         this.b((E)var1, var2 - var3);
      } else {
         this.c(var1, var3 - var2);
      }

      return var3;
   }

   @Override
   public boolean contains(Object object) {
      return this.a(object) > 0;
   }

   @Override
   public Iterator<E> iterator() {
      return new jS.c<>(this);
   }

   @Override
   public boolean add(E object) {
      this.b(object, 1);
      return true;
   }

   @Override
   public int b(E var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      Iterator var1 = this.b().iterator();

      while (var1.hasNext()) {
         var1.next();
         var1.remove();
      }
   }

   @Override
   public boolean remove(Object object) {
      return this.c(object, 1) != 0;
   }

   @Override
   public int c(Object var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> coll) {
      boolean var2 = false;

      for (boolean var3 : coll) {
         var3 = this.c(var3, this.a(var3)) != 0;
         var2 = var2 || var3;
      }

      return var2;
   }

   @Override
   public final Set<E> a() {
      if (this.a == null) {
         jS var1 = this;
         this.a = new jS.d<>(var1);
      }

      return this.a;
   }

   private Set<E> f() {
      return new jS.d<>(this);
   }

   protected Iterator<E> e() {
      fQ var1 = new fQ<fy.a<E>, E>() {
         private static E a(fy.a<E> var0) {
            return (E)var0.a();
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public final Object a(Object var1) {
            fy.a var2;
            return (var2 = (fy.a)var1).a();
         }
      };
      return fq.a(this.b().iterator(), var1);
   }

   @Override
   public final Set<fy.a<E>> b() {
      if (this.b == null) {
         jS var1 = this;
         this.b = new jS.b<>(var1);
      }

      return this.b;
   }

   private Set<fy.a<E>> g() {
      return new jS.b<>(this);
   }

   protected abstract int c();

   protected abstract Iterator<fy.a<E>> d();

   protected void a(ObjectOutputStream var1) {
      var1.writeInt(this.b().size());

      for (fy.a var3 : this.b()) {
         var1.writeObject(var3.a());
         var1.writeInt(var3.b());
      }
   }

   protected void a(ObjectInputStream var1) {
      int var2 = var1.readInt();

      for (int var3 = 0; var3 < var2; var3++) {
         Object var4 = var1.readObject();
         int var5 = var1.readInt();
         this.a((E)var4, var5);
      }
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

      for (fy.a var3 : this.b()) {
         if (((fy)object).a(var3.a()) != this.a(var3.a())) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int hashCode() {
      return this.b().hashCode();
   }

   @Override
   public String toString() {
      return this.b().toString();
   }

   public abstract static class a<E> implements fy.a<E> {
      @Override
      public boolean equals(Object object) {
         if (!(object instanceof fy.a)) {
            return false;
         }

         object = (fy.a)object;
         Object var2 = this.a();
         Object var3 = ((fy.a)object).a();
         return this.b() == ((fy.a)object).b() && (var2 == var3 || var2 != null && var2.equals(var3));
      }

      @Override
      public int hashCode() {
         Object var1;
         return ((var1 = this.a()) == null ? 0 : var1.hashCode()) ^ this.b();
      }

      @Override
      public String toString() {
         return String.format("%s:%d", this.a(), this.b());
      }
   }

   protected static class b<E> extends AbstractSet<fy.a<E>> {
      private final jS<E> a;

      protected b(jS<E> var1) {
         this.a = var1;
      }

      @Override
      public final int size() {
         return this.a.c();
      }

      @Override
      public final Iterator<fy.a<E>> iterator() {
         return this.a.d();
      }

      @Override
      public final boolean contains(Object obj) {
         if (!(obj instanceof fy.a)) {
            return false;
         }

         fy.a var3;
         Object var2 = (var3 = (fy.a)obj).a();
         return this.a.a(var2) == var3.b();
      }

      @Override
      public final boolean remove(Object obj) {
         if (!(obj instanceof fy.a)) {
            return false;
         }

         fy.a var4;
         Object var2 = (var4 = (fy.a)obj).a();
         if (this.a.contains(var2)) {
            int var3 = this.a.a(var2);
            if (var4.b() == var3) {
               this.a.c(var2, var3);
               return true;
            }
         }

         return false;
      }
   }

   static class c<E> implements Iterator<E> {
      private final jS<E> a;
      private final Iterator<fy.a<E>> b;
      private fy.a<E> c;
      private int d;
      private boolean e;

      public c(jS<E> var1) {
         this.a = var1;
         this.b = var1.b().iterator();
         this.c = null;
         this.e = false;
      }

      @Override
      public final boolean hasNext() {
         return this.d > 0 || this.b.hasNext();
      }

      @Override
      public final E next() {
         if (this.d == 0) {
            this.c = this.b.next();
            this.d = this.c.b();
         }

         this.e = true;
         this.d--;
         return this.c.a();
      }

      @Override
      public final void remove() {
         if (!this.e) {
            throw new IllegalStateException();
         }

         if (this.c.b() > 1) {
            this.a.remove(this.c.a());
         } else {
            this.b.remove();
         }

         this.e = false;
      }
   }

   protected static class d<E> extends AbstractSet<E> {
      private jS<E> a;

      protected d(jS<E> var1) {
         this.a = var1;
      }

      @Override
      public final Iterator<E> iterator() {
         return this.a.e();
      }

      @Override
      public final boolean contains(Object key) {
         return this.a.contains(key);
      }

      @Override
      public final boolean containsAll(Collection<?> coll) {
         return this.a.containsAll(coll);
      }

      @Override
      public final boolean remove(Object key) {
         return this.a.c(key, this.a.a(key)) != 0;
      }

      @Override
      public final int size() {
         return this.a.c();
      }

      @Override
      public final void clear() {
         this.a.clear();
      }
   }
}
