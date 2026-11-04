package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.world.connectivity.DoubleAugmentation;

public final class Eb implements DoubleAugmentation {
   private final String a;

   public Eb(String var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
   }

   @Override
   public final String getKey() {
      return this.a;
   }

   @Override
   public final double combineDouble(double a, double b) {
      return Math.max(a, b);
   }
}
