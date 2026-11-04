package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType;
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate;

public final class EV implements VsiTerrainUpdate.Builder {
   private final Ir a;
   private boolean b;

   public EV(int var1, int var2, int var3) {
      this.a = new Ir(var1, var2, var3);
   }

   @Override
   public final void addBlock(int x, int y, int z, VsiBlockType block) {
      Intrinsics.checkNotNullParameter(block, "");
      DM var5;
      this.a.a(x, y, z, (var5 = (DM)block).a);
   }

   @Override
   public final VsiTerrainUpdate build() {
      if (this.b) {
         String var1 = "This builder has already been used!";
         throw new IllegalArgumentException(var1.toString());
      } else {
         this.b = true;
         return new EW(this.a);
      }
   }
}
