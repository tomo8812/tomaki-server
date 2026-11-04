package org.valkyrienskies.core.impl.shadow

import org.valkyrienskies.physics_api_krunch.voxel.KrunchLod1BlockRegistry

internal object IK : GZ<KrunchLod1BlockRegistry> {
   @JvmStatic
   public open fun b(p0: Hm): KrunchLod1BlockRegistry {
      return KrunchLod1BlockRegistry(var0)
   }

   @JvmSynthetic
   override final fun a(var1: Hm): GY {
      val var2: Hm
      KrunchLod1BlockRegistry(var2) as GY
   }
}
