package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Comparator;

public final class gH<E> implements Serializable, Comparator<E> {
   private static final long a = -5820772575483504339L;
   private final Comparator<? super E> b;
   private final boolean c;

   public gH() {
      this(fe.a, true);
   }

   private gH(Comparator<? super E> var1) {
      this(var1, true);
   }

   private gH(boolean var1) {
      this(fe.a, var1);
   }

   public gH(Comparator<? super E> var1, boolean var2) {
      this.b = var1;
      this.c = var2;
      if (var1 == null) {
         throw new NullPointerException("null nonNullComparator");
      }
   }

   @Override
   public final int compare(E o1, E o2) {
      if (o1 == o2) {
         return 0;
      } else if (o1 == null) {
         return this.c ? 1 : -1;
      } else if (o2 == null) {
         return this.c ? -1 : 1;
      } else {
         return this.b.compare(o1, o2);
      }
   }

   @Override
   public final int hashCode() {
      return (this.c ? -1 : 1) * this.b.hashCode();
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }

      if (obj == this) {
         return true;
      }

      if (!obj.getClass().equals(this.getClass())) {
         return false;
      }

      obj = (gH)obj;
      return this.c == ((gH)obj).c && this.b.equals(((gH)obj).b);
   }
}
