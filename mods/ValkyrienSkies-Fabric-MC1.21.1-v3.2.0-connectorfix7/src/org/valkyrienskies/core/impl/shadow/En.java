package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.ships.properties.PhysPose;
import org.valkyrienskies.core.impl.game.ships.PhysPoseImpl;

public final class En {
   public static final Hf a(PhysPose var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      return new Hf(var0.getPos(), var0.getRot());
   }

   public static final PhysPoseImpl a(Hf var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      return new PhysPoseImpl(var0.a, var0.b);
   }
}
