package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;

public final class Gw<S> implements Gx<S> {
   private final S b;

   public Gw(S var1) {
      this.b = (S)var1;
   }

   @Override
   public final boolean a(S var1) {
      return Intrinsics.areEqual(this.b, var1);
   }

   @Override
   public final String toString() {
      return String.valueOf(this.b);
   }
}
