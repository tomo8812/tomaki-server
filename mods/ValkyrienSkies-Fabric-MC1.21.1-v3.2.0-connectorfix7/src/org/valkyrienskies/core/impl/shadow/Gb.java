package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Gb {
   public static final Ga a() {
      return Ga.INSTANCE;
   }

   public static final Logger a(String var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      Logger var10000 = LogManager.getLogger(var0);
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      Logger var1 = var10000;
      Intrinsics.checkNotNullParameter(var10000, "");
      return var1;
   }
}
