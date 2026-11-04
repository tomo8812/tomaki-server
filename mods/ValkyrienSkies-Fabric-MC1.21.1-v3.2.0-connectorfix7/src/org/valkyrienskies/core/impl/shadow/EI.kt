package org.valkyrienskies.core.impl.shadow

import java.util.LinkedHashMap
import javax.inject.Named
import org.valkyrienskies.core.impl.game.ChunkAllocator
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider
import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.core.internal.ships.VsiMutableQueryableShipData

@dW
public class EI {
   @JvmStatic
   public EI.a a = EI.a(null);
   VsiMutableQueryableShipData<ShipData> b;
   ChunkAllocator c;
   Ek d;
   java.util.Map<java.lang.String, DP> e;

   fun EI(var1: VsiMutableQueryableShipData<ShipData>, var2: ChunkAllocator, var3: Ek) {
      this.b = var1;
      this.c = var2;
      this.d = var3;
      this.e = LinkedHashMap<>();
   }

   @dX
   @JvmName(name = "a")
   fun a(): VsiMutableQueryableShipData<ShipData> {
      this.b;
   }

   @dX
   @Named("primary")
   @JvmName(name = "b")
   fun b(): ChunkAllocator {
      this.c;
   }

   @dX
   @JvmName(name = "c")
   fun c(): Ek {
      this.d;
   }

   @dX
   @Named("mutableDimensionInfo")
   @JvmName(name = "d")
   fun d(): MutableMap<java.lang.String, DP> {
      this.e;
   }

   @dX
   @Named("dimensionInfo")
   @JvmName(name = "e")
   fun e(): MutableMap<java.lang.String, DP> {
      this.e;
   }

   public companion object a {
      @JvmStatic
      public fun a(): EI {
         return EI(Eo<>(null, 1, null), ChunkAllocator.Companion.create(), Ek.a.a());
      }
   }

   // $VF: Class flags could not be determined
   @dW
   internal final interface b {
      @dP
      fun a(): ChunkAllocatorProvider
   }
}
