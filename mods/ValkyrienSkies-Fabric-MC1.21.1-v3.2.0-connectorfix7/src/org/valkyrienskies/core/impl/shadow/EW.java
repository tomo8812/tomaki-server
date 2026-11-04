package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate;

public final class EW implements VsiTerrainUpdate {
   public final Ip a;

   public EW(Ip var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
   }

   public final Ip a() {
      return this.a;
   }

   @Override
   public final int getChunkX() {
      return this.a.a();
   }

   @Override
   public final int getChunkY() {
      return this.a.b();
   }

   @Override
   public final int getChunkZ() {
      return this.a.c();
   }

   private Ip b() {
      return this.a;
   }

   private static EW a(Ip var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      return new EW(var0);
   }

   // $VF: synthetic method
   private static EW a(EW var0, Ip var1, int var2) {
      if ((var2 & 1) != 0) {
         var1 = var0.a;
      }

      Ip var3 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      return new EW(var3);
   }

   @Override
   public final String toString() {
      return "TerrainUpdateImpl(update=" + this.a + ")";
   }

   @Override
   public final int hashCode() {
      return this.a.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof EW var2) ? false : Intrinsics.areEqual(this.a, var2.a);
      }
   }
}
