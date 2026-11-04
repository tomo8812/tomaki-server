package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;

public final class aX {
   public final ResolvedType a;
   private final av b;

   protected aX(ResolvedType var1, av var2) {
      this.a = var1;
      this.b = var2;
   }

   public final ResolvedType a() {
      return this.a;
   }

   private av b() {
      return this.b;
   }

   @Override
   public final int hashCode() {
      return this.a.hashCode() + (this.b == null ? 0 : this.b.hashCode());
   }

   @Override
   public final boolean equals(Object other) {
      if (other == this) {
         return true;
      }

      if (!(other instanceof aX)) {
         return false;
      }

      other = (aX)other;
      return this.a.equals(((aX)other).a) && this.b == ((aX)other).b;
   }
}
