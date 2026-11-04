package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.internal.world.VsiPlayer;

public final class CV {
   public static final CU a(long var0, String var2, Iterable<? extends VsiPlayer> var3, double var4, ServerShip var6) {
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var6, "");
      return new CU(var0, var2, var3, var4, var6);
   }
}
