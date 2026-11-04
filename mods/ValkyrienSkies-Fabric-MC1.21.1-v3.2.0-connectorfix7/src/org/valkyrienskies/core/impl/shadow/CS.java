package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet;
import org.valkyrienskies.core.internal.world.VsiPlayer;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkUnwatchTask;

public final class CS implements Comparable<VsiChunkUnwatchTask>, VsiChunkUnwatchTask {
   private final long a;
   private final String b;
   private final Iterable<VsiPlayer> c;
   private final boolean d;
   private final double e;
   private final ServerShip f;

   public CS(long var1, String var3, Iterable<? extends VsiPlayer> var4, boolean var5, double var6, ServerShip var8) {
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var8, "");
      super();
      this.a = var1;
      this.b = var3;
      this.c = var4;
      this.d = var5;
      this.e = var6;
      this.f = var8;
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
   public final Iterable<VsiPlayer> getPlayersNeedUnwatching() {
      return this.c;
   }

   @Override
   public final boolean getShouldUnload() {
      return this.d;
   }

   @Override
   public final ServerShip getShip() {
      return this.f;
   }

   @Override
   public final int getChunkX() {
      return IShipActiveChunksSet.Companion.longToChunkX(this.getChunkPos());
   }

   @Override
   public final int getChunkZ() {
      return IShipActiveChunksSet.Companion.longToChunkZ(this.getChunkPos());
   }

   private int a(VsiChunkUnwatchTask var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      int var2;
      if ((var2 = Double.compare(this.e, ((CS)var1).e)) == 0) {
         var2 = Intrinsics.compare(this.getChunkPos(), var1.getChunkPos());
      }

      return var2;
   }

   // $VF: synthetic method
   @Override
   public final int compareTo(Object other) {
      VsiChunkUnwatchTask var2 = (VsiChunkUnwatchTask)other;
      other = this;
      Intrinsics.checkNotNullParameter(var2, "");
      int var3;
      if ((var3 = Double.compare(((CS)other).e, ((CS)var2).e)) == 0) {
         var3 = Intrinsics.compare(((CS)other).getChunkPos(), var2.getChunkPos());
      }

      return var3;
   }
}
