package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public interface fu<K, V> extends Iterator<K> {
   @Override
   boolean hasNext();

   @Override
   K next();

   K a();

   V b();

   @Override
   void remove();

   V a(V var1);
}
