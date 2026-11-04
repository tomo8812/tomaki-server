package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public abstract class hN<E> extends hR<E, E> {
   public hN(Iterator<E> var1) {
      super(var1);
   }

   @Override
   public E next() {
      return this.a().next();
   }
}
