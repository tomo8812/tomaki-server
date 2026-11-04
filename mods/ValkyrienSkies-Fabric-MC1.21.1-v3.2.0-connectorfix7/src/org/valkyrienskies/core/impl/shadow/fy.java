package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public interface fy<E> extends Collection<E> {
   int a(Object var1);

   int a(E var1, int var2);

   @Override
   boolean add(E var1);

   int b(E var1, int var2);

   @Override
   boolean remove(Object var1);

   int c(Object var1, int var2);

   Set<E> a();

   Set<fy.a<E>> b();

   @Override
   Iterator<E> iterator();

   @Override
   int size();

   @Override
   boolean containsAll(Collection<?> var1);

   @Override
   boolean removeAll(Collection<?> var1);

   @Override
   boolean retainAll(Collection<?> var1);

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();

   interface a<E> {
      E a();

      int b();

      @Override
      boolean equals(Object var1);

      @Override
      int hashCode();
   }
}
