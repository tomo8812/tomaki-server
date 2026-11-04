package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.util.DoublePair;
import org.valkyrienskies.core.api.world.connectivity.Component;
import org.valkyrienskies.core.api.world.connectivity.DoubleComponentAugmentation;

public final class Ee implements DoubleComponentAugmentation {
   private final String a;

   public Ee(String var1) {
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
      return a + b;
   }

   @Override
   public final DoublePair splitDouble(double value, Component component1, Component component2) {
      Intrinsics.checkNotNullParameter(component1, "");
      Intrinsics.checkNotNullParameter(component2, "");
      long var5 = component1.getSize();
      long var7 = component2.getSize();
      long var9;
      return (var9 = var5 + var7) == 0L ? new DoublePair(0.0, 0.0) : new DoublePair(value * ((double)var5 / var9), value * ((double)var7 / var9));
   }
}
