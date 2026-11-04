package org.valkyrienskies.core.impl.shadow

import org.valkyrienskies.physics_api_krunch.voxel.KrunchLod1BlockRegistry
import org.valkyrienskies.physics_api_krunch.voxel.KrunchVoxelChunk16

internal object IL : Il<KrunchVoxelChunk16> {
   @JvmStatic
   public open fun b(p0: GY): KrunchVoxelChunk16 {
      return KrunchVoxelChunk16(var0 as KrunchLod1BlockRegistry)
   }

   @JvmSynthetic
   override final fun a(var1: GY): Ik {
      val var2: GY
      KrunchVoxelChunk16(var2 as KrunchLod1BlockRegistry) as Ik
   }
}
