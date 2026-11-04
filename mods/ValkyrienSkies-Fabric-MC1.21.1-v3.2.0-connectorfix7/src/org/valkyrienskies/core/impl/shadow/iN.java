package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public abstract class iN<E> extends gv<E> implements List<E> {
   private static final long a = 4500739654952315623L;

   protected iN() {
   }

   protected iN(List<E> var1) {
      super(var1);
   }

   protected final List<E> c() {
      return (List<E>)super.f();
   }

   @Override
   public boolean equals(Object object) {
      return object == this || ((List)super.f()).equals(object);
   }

   @Override
   public int hashCode() {
      return ((List)super.f()).hashCode();
   }

   @Override
   public void add(int index, E object) {
      ((List)super.f()).add(index, object);
   }

   @Override
   public boolean addAll(int index, Collection<? extends E> coll) {
      return ((List)super.f()).addAll(index, coll);
   }

   @Override
   public E get(int index) {
      return ((List)super.f()).get(index);
   }

   @Override
   public int indexOf(Object object) {
      return ((List)super.f()).indexOf(object);
   }

   @Override
   public int lastIndexOf(Object object) {
      return ((List)super.f()).lastIndexOf(object);
   }

   @Override
   public ListIterator<E> listIterator() {
      return ((List)super.f()).listIterator();
   }

   @Override
   public ListIterator<E> listIterator(int index) {
      return ((List)super.f()).listIterator(index);
   }

   @Override
   public E remove(int index) {
      return ((List)super.f()).remove(index);
   }

   @Override
   public E set(int index, E object) {
      return ((List)super.f()).set(index, object);
   }

   @Override
   public List<E> subList(int fromIndex, int toIndex) {
      return ((List)super.f()).subList(fromIndex, toIndex);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Collection f() {
      return super.f();
   }
}
