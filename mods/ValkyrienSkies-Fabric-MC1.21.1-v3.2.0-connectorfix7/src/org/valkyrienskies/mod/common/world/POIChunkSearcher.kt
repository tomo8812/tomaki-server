package org.valkyrienskies.mod.common.world

import kotlin.jvm.internal.Ref.IntRef
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_243
import net.minecraft.class_4156
import org.joml.Vector4i
import org.joml.Vector4ic
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.mod.common.VSGameUtilsKt

public object POIChunkSearcher {
   public fun shipChunkBounds(chunkSet: IShipActiveChunksSet): Vector4ic? {
      if (chunkSet.size == 0) {
         return null
      } else {
         val minChunkX: IntRef = IntRef()
         minChunkX.element = Integer.MAX_VALUE
         val minChunkZ: IntRef = IntRef()
         minChunkZ.element = Integer.MAX_VALUE
         val maxChunkX: IntRef = IntRef()
         maxChunkX.element = Integer.MIN_VALUE
         val maxChunkZ: IntRef = IntRef()
         maxChunkZ.element = Integer.MIN_VALUE
         chunkSet.forEach({ chunkX: Int, chunkZ: Int ->
            `$minChunkX`.element = Math.min(`$minChunkX`.element, chunkX)
            `$minChunkZ`.element = Math.min(`$minChunkZ`.element, chunkZ)
            `$maxChunkX`.element = Math.max(`$maxChunkX`.element, chunkX)
            `$maxChunkZ`.element = Math.max(`$maxChunkZ`.element, chunkZ)
         })
         return if (minChunkX.element != Integer.MAX_VALUE
               && minChunkZ.element != Integer.MAX_VALUE
               && maxChunkX.element != Integer.MIN_VALUE
               && maxChunkZ.element != Integer.MIN_VALUE)
            Vector4i(minChunkX.element, minChunkZ.element, maxChunkX.element, maxChunkZ.element)
            else
            null
         }
   }

   public fun class_4156.getWorldPos(level: class_1937): class_243 {
      val var10001: class_2338 = `$this$getWorldPos`.method_19141()
      return VSGameUtilsKt.toWorldCoordinates(level, var10001)
   }
}
