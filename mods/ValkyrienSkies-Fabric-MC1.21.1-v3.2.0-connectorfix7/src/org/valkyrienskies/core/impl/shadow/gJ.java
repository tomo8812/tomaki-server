package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Comparator;

public final class gJ<I, O> implements Serializable, Comparator<I> {
   private static final long a = 3456940356043606220L;
   private final Comparator<O> b;
   private final fQ<? super I, ? extends O> c;

   private gJ(fQ<? super I, ? extends O> var1) {
      this(var1, fe.a);
   }

   public gJ(fQ<? super I, ? extends O> var1, Comparator<O> var2) {
      this.b = var2;
      this.c = var1;
   }

   @Override
   public final int compare(I obj1, I obj2) {
      obj1 = (I)this.c.a(obj1);
      obj2 = (I)this.c.a(obj2);
      return this.b.compare((O)obj1, (O)obj2);
   }

   @Override
   public final int hashCode() {
      int var1;
      return (var1 = 629 + (this.b == null ? 0 : this.b.hashCode())) * 37 + (this.c == null ? 0 : this.c.hashCode());
   }

   @Override
   public final boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (object == null) {
         return false;
      }

      if (!object.getClass().equals(this.getClass())) {
         return false;
      }

      object = (gJ)object;
      return (this.b == null ? ((gJ)object).b == null : this.b.equals(((gJ)object).b))
         && (this.c == null ? ((gJ)object).c == null : this.c.equals(((gJ)object).c));
   }
}
