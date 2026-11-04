package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet;
import org.valkyrienskies.core.internal.world.VsiPlayer;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTask;

public final class CU implements Comparable<VsiChunkWatchTask>, VsiChunkWatchTask {
   private final long a;
   private final String b;
   private final Iterable<VsiPlayer> c;
   private final double d;
   private final ServerShip e;

   public CU(long var1, String var3, Iterable<? extends VsiPlayer> var4, double var5, ServerShip var7) {
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var7, "");
      super();
      this.a = var1;
      this.b = var3;
      this.c = var4;
      this.d = var5;
      this.e = var7;
   }

   @Override
   public final long getChunkPos() {
      return this.a;
   }

   @Override
   public final String getDimensionId() {
      return this.b;
   }

   @Override
   public final Iterable<VsiPlayer> getPlayersNeedWatching() {
      return this.c;
   }

   @Override
   public final ServerShip getShip() {
      return this.e;
   }

   @Override
   public final int getChunkX() {
      return IShipActiveChunksSet.Companion.longToChunkX(this.getChunkPos());
   }

   @Override
   public final int getChunkZ() {
      return IShipActiveChunksSet.Companion.longToChunkZ(this.getChunkPos());
   }

   private int a(VsiChunkWatchTask var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      int var2;
      if ((var2 = Double.compare(this.d, ((CU)var1).d)) == 0) {
         var2 = Intrinsics.compare(this.getChunkPos(), var1.getChunkPos());
      }

      return var2;
   }

   // $VF: synthetic method
   @Override
   public final int compareTo(Object other) {
      VsiChunkWatchTask var2 = (VsiChunkWatchTask)other;
      other = this;
      Intrinsics.checkNotNullParameter(var2, "");
      int var3;
      if ((var3 = Double.compare(((CU)other).d, ((CU)var2).d)) == 0) {
         var3 = Intrinsics.compare(((CU)other).getChunkPos(), var2.getChunkPos());
      }

      return var3;
   }
}
