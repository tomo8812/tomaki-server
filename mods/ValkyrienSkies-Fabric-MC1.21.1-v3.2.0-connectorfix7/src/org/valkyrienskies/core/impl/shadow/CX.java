package org.valkyrienskies.core.impl.shadow;

import java.util.SortedSet;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkUnwatchTask;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTask;

public final class CX {
   public static final CW a(SortedSet<VsiChunkWatchTask> var0, SortedSet<VsiChunkUnwatchTask> var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      return new CW(var0, var1);
   }
}
