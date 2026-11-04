package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.util.DoublePair;
import org.valkyrienskies.core.api.world.connectivity.Component;
import org.valkyrienskies.core.api.world.connectivity.DoubleComponentAugmentation;

public final class Ec implements DoubleComponentAugmentation {
   private final String a;

   private Ec(String var1) {
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

   @Override
   public final DoublePair splitDouble(double value, Component component1, Component component2) {
      Intrinsics.checkNotNullParameter(component1, "");
      Intrinsics.checkNotNullParameter(component2, "");
      return new DoublePair(value, value);
   }
}
