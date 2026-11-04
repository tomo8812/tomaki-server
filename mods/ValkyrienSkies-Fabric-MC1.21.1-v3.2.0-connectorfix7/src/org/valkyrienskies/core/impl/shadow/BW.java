package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class BW implements Serializable, Cl {
   private static final long a = 4019938025047800455L;

   @Override
   public final double a(Object var1) {
      if (var1 == null) {
         throw new pl(px.OBJECT_TRANSFORMATION);
      }

      if (var1 instanceof Number) {
         return ((Number)var1).doubleValue();
      }

      try {
         return Double.parseDouble(var1.toString());
      } catch (NumberFormatException var2) {
         throw new oV(px.CANNOT_TRANSFORM_TO_DOUBLE, var1.toString());
      }
   }

   @Override
   public final boolean equals(Object other) {
      return this == other ? true : other instanceof BW;
   }

   @Override
   public final int hashCode() {
      return 401993047;
   }
}
