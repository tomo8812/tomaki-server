package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public abstract class hR<I, O> implements Iterator<O> {
   private final Iterator<I> a;

   public hR(Iterator<I> var1) {
      if (var1 == null) {
         throw new NullPointerException("Iterator must not be null");
      }

      this.a = var1;
   }

   protected final Iterator<I> a() {
      return this.a;
   }

   @Override
   public boolean hasNext() {
      return this.a.hasNext();
   }

   @Override
   public void remove() {
      this.a.remove();
   }
}
