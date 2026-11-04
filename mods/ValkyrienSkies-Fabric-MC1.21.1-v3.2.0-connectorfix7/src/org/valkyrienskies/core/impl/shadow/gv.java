package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public abstract class gv<E> implements Serializable, Collection<E> {
   private static final long a = 6249888059822088500L;
   private Collection<E> b;

   public gv() {
   }

   public gv(Collection<E> var1) {
      if (var1 == null) {
         throw new NullPointerException("Collection must not be null.");
      }

      this.b = var1;
   }

   public Collection<E> f() {
      return this.b;
   }

   protected final void a(Collection<E> var1) {
      this.b = var1;
   }

   @Override
   public boolean add(E object) {
      return this.f().add(object);
   }

   @Override
   public boolean addAll(Collection<? extends E> coll) {
      return this.f().addAll(coll);
   }

   @Override
   public void clear() {
      this.f().clear();
   }

   @Override
   public boolean contains(Object object) {
      return this.f().contains(object);
   }

   @Override
   public boolean isEmpty() {
      return this.f().isEmpty();
   }

   @Override
   public Iterator<E> iterator() {
      return this.f().iterator();
   }

   @Override
   public boolean remove(Object object) {
      return this.f().remove(object);
   }

   @Override
   public int size() {
      return this.f().size();
   }

   @Override
   public Object[] toArray() {
      return this.f().toArray();
   }

   @Override
   public <T> T[] toArray(T[] object) {
      return (T[])this.f().toArray(object);
   }

   @Override
   public boolean containsAll(Collection<?> coll) {
      return this.f().containsAll(coll);
   }

   @Override
   public boolean removeAll(Collection<?> coll) {
      return this.f().removeAll(coll);
   }

   @Override
   public boolean retainAll(Collection<?> coll) {
      return this.f().retainAll(coll);
   }

   @Override
   public String toString() {
      return this.f().toString();
   }
}
