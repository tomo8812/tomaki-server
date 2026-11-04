package org.valkyrienskies.core.impl.shadow;

import java.util.Enumeration;
import java.util.Iterator;

public final class ig<E> implements Enumeration<E> {
   private Iterator<? extends E> a;

   public ig() {
   }

   public ig(Iterator<? extends E> var1) {
      this.a = var1;
   }

   @Override
   public final boolean hasMoreElements() {
      return this.a.hasNext();
   }

   @Override
   public final E nextElement() {
      return (E)this.a.next();
   }

   private Iterator<? extends E> a() {
      return this.a;
   }

   private void a(Iterator<? extends E> var1) {
      this.a = var1;
   }
}
