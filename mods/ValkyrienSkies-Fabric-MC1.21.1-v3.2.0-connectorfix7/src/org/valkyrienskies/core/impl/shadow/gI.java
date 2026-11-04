package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Comparator;

public final class gI<E> implements Serializable, Comparator<E> {
   private static final long a = 2858887242028539265L;
   private final Comparator<? super E> b;

   public gI() {
      this(null);
   }

   public gI(Comparator<? super E> var1) {
      this.b = var1 == null ? fe.a : var1;
   }

   @Override
   public final int compare(E obj1, E obj2) {
      return this.b.compare(obj2, obj1);
   }

   @Override
   public final int hashCode() {
      return "ReverseComparator".hashCode() ^ this.b.hashCode();
   }

   @Override
   public final boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object == null) {
         return false;
      } else if (object.getClass().equals(this.getClass())) {
         object = (gI)object;
         return this.b.equals(((gI)object).b);
      } else {
         return false;
      }
   }
}
