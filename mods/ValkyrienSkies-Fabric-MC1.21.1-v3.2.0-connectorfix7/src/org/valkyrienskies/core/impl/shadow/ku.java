package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class ku<E, V> implements Serializable, Set<E> {
   private static final long a = 6723912213766056587L;
   private final Map<E, ? super V> b;
   private final V c;

   private static <E, V> ku<E, V> a(Map<E, ? super V> var0) {
      Object var1 = null;
      var0 = var0;
      return new ku<>(var0, null);
   }

   private static <E, V> ku<E, V> a(Map<E, ? super V> var0, V var1) {
      return new ku<>(var0, null);
   }

   private ku(Map<E, ? super V> var1, V var2) {
      if (var1 == null) {
         throw new NullPointerException("The map must not be null");
      }

      this.b = var1;
      this.c = (V)var2;
   }

   @Override
   public final int size() {
      return this.b.size();
   }

   @Override
   public final boolean isEmpty() {
      return this.b.isEmpty();
   }

   @Override
   public final Iterator<E> iterator() {
      return this.b.keySet().iterator();
   }

   @Override
   public final boolean contains(Object obj) {
      return this.b.containsKey(obj);
   }

   @Override
   public final boolean containsAll(Collection<?> coll) {
      return this.b.keySet().containsAll(coll);
   }

   @Override
   public final boolean add(E obj) {
      int var2 = this.b.size();
      this.b.put(obj, this.c);
      return this.b.size() != var2;
   }

   @Override
   public final boolean addAll(Collection<? extends E> coll) {
      int var2 = this.b.size();

      for (Object var3 : coll) {
         this.b.put((E)var3, this.c);
      }

      return this.b.size() != var2;
   }

   @Override
   public final boolean remove(Object obj) {
      int var2 = this.b.size();
      this.b.remove(obj);
      return this.b.size() != var2;
   }

   @Override
   public final boolean removeAll(Collection<?> coll) {
      return this.b.keySet().removeAll(coll);
   }

   @Override
   public final boolean retainAll(Collection<?> coll) {
      return this.b.keySet().retainAll(coll);
   }

   @Override
   public final void clear() {
      this.b.clear();
   }

   @Override
   public final Object[] toArray() {
      return this.b.keySet().toArray();
   }

   @Override
   public final <T> T[] toArray(T[] array) {
      return (T[])this.b.keySet().toArray(array);
   }

   @Override
   public final boolean equals(Object obj) {
      return this.b.keySet().equals(obj);
   }

   @Override
   public final int hashCode() {
      return this.b.keySet().hashCode();
   }
}
