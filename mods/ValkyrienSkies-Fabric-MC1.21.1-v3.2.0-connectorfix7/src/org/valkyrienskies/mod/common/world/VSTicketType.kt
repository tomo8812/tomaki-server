package org.valkyrienskies.mod.common.world

import java.util.Comparator
import net.minecraft.class_1923
import net.minecraft.class_3230
import org.jetbrains.annotations.NotNull

public object VSTicketType {
   @JvmField
   @NotNull
   public final val SHIP_CHUNK: class_3230<class_1923>

   @JvmField
   @NotNull
   public final val SHIP_ACTIVE_VOXEL: class_3230<class_1923>

   @JvmStatic
   fun {
      var var0: class_3230 = class_3230.method_14291("vs_ship_chunk", Comparator.comparingLong({ p0: Any ->
         (`$tmp0`(p0) as java.lang.Number).longValue()
      }))
      SHIP_CHUNK = var0
      var0 = class_3230.method_14291("vs_ship_active_voxel", Comparator.comparingLong({ p0: Any ->
         (`$tmp0`(p0) as java.lang.Number).longValue()
      }))
      SHIP_ACTIVE_VOXEL = var0
   }
}
