package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public class gz<E> implements Serializable, Collection<E> {
   private static final long b = 2412805092710877986L;
   private final Collection<E> c;
   protected final Object a;

   public static <T> gz<T> a(Collection<T> var0) {
      return new gz(var0);
   }

   public gz(Collection<E> var1) {
      if (var1 == null) {
         throw new NullPointerException("Collection must not be null.");
      }

      this.c = var1;
      this.a = this;
   }

   public gz(Collection<E> var1, Object var2) {
      if (var1 == null) {
         throw new NullPointerException("Collection must not be null.");
      }

      if (var2 == null) {
         throw new NullPointerException("Lock must not be null.");
      }

      this.c = var1;
      this.a = var2;
   }

   public Collection<E> e() {
      return this.c;
   }

   @Override
   public boolean add(E object) {
      synchronized (this.a) {
         return this.e().add(object);
      }
   }

   @Override
   public boolean addAll(Collection<? extends E> coll) {
      synchronized (this.a) {
         return this.e().addAll(coll);
      }
   }

   @Override
   public void clear() {
      synchronized (this.a) {
         this.e().clear();
      }
   }

   @Override
   public boolean contains(Object object) {
      synchronized (this.a) {
         return this.e().contains(object);
      }
   }

   @Override
   public boolean containsAll(Collection<?> coll) {
      synchronized (this.a) {
         return this.e().containsAll(coll);
      }
   }

   @Override
   public boolean isEmpty() {
      synchronized (this.a) {
         return this.e().isEmpty();
      }
   }

   @Override
   public Iterator<E> iterator() {
      return this.e().iterator();
   }

   @Override
   public Object[] toArray() {
      synchronized (this.a) {
         return this.e().toArray();
      }
   }

   @Override
   public <T> T[] toArray(T[] object) {
      synchronized (this.a) {
         return (T[])this.e().toArray(object);
      }
   }

   @Override
   public boolean remove(Object object) {
      synchronized (this.a) {
         return this.e().remove(object);
      }
   }

   @Override
   public boolean removeAll(Collection<?> coll) {
      synchronized (this.a) {
         return this.e().removeAll(coll);
      }
   }

   @Override
   public boolean retainAll(Collection<?> coll) {
      synchronized (this.a) {
         return this.e().retainAll(coll);
      }
   }

   @Override
   public int size() {
      synchronized (this.a) {
         return this.e().size();
      }
   }

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean equals(Object object) {
      Object var2 = this.a;
      synchronized (this.a){} // $VF: monitorenter 
      if (object == this) {
         // $VF: monitorexit
         return true;
      }

      try {
         boolean var10000 = object == this || this.e().equals(object);
         // $VF: monitorexit
         return var10000;
      } finally {
         // $VF: monitorexit
      }
   }

   @Override
   public int hashCode() {
      synchronized (this.a) {
         return this.e().hashCode();
      }
   }

   @Override
   public String toString() {
      synchronized (this.a) {
         return this.e().toString();
      }
   }
}
