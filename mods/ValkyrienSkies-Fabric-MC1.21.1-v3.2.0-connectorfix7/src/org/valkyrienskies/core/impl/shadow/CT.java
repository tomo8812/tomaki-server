package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.internal.world.VsiPlayer;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkUnwatchTask;

public final class CT {
   public static final VsiChunkUnwatchTask a(long var0, String var2, Iterable<? extends VsiPlayer> var3, boolean var4, double var5, ServerShip var7) {
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var7, "");
      return new CS(var0, var2, var3, var4, var5, var7);
   }
}
