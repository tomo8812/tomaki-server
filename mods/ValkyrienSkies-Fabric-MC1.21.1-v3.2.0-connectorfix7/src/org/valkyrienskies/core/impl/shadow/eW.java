package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public interface eW<E> extends Collection<E> {
   int a(Object var1);

   @Override
   boolean add(E var1);

   boolean a(E var1, int var2);

   @Override
   boolean remove(Object var1);

   boolean b(Object var1, int var2);

   Set<E> a();

   @Override
   int size();

   @Override
   boolean containsAll(Collection<?> var1);

   @Override
   boolean removeAll(Collection<?> var1);

   @Override
   boolean retainAll(Collection<?> var1);

   @Override
   Iterator<E> iterator();
}
