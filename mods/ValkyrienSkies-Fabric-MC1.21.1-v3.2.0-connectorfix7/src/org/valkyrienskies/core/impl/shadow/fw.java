package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;

@Deprecated
public interface fw<K, V> extends fn<K, Object> {
   boolean a(K var1, V var2);

   @Override
   int size();

   @Override
   Object get(Object var1);

   @Override
   boolean containsValue(Object var1);

   @Override
   Object put(K var1, Object var2);

   @Override
   Object remove(Object var1);

   @Override
   Collection<Object> values();
}
