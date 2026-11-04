package org.valkyrienskies.core.impl.shadow;

import java.util.Set;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;

public final class Gv<S> implements Gx<S> {
   private final Set<S> b;

   public Gv(S... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.b = ArraysKt.toSet(var1);
   }

   @Override
   public final boolean a(S var1) {
      return this.b.contains(var1);
   }

   @Override
   public final String toString() {
      return "oneOf" + this.b;
   }
}
