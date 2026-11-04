package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public final class iw<E> extends hN<E> {
   private final long a;
   private long b;

   public iw(Iterator<E> var1, long var2) {
      super(var1);
      if (var2 < 0L) {
         throw new IllegalArgumentException("Offset parameter must not be negative.");
      }

      this.a = var2;
      this.b = 0L;
      var1 = this;

      while (var1.b < var1.a && var1.hasNext()) {
         var1.next();
      }
   }

   private void b() {
      while (this.b < this.a && this.hasNext()) {
         this.next();
      }
   }

   @Override
   public final E next() {
      Object var1 = super.next();
      this.b++;
      return (E)var1;
   }

   @Override
   public final void remove() {
      if (this.b <= this.a) {
         throw new IllegalStateException("remove() can not be called before calling next()");
      }

      super.remove();
   }
}
