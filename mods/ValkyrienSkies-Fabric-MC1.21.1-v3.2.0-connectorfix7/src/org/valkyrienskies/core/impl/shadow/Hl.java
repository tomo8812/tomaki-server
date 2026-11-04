package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;

public final class Hl extends RuntimeException {
   public Hl(String var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super(var1);
   }
}
