package org.valkyrienskies.mod.util

import net.minecraft.class_1937
import net.minecraft.class_2338
import org.valkyrienskies.mod.api.BlockEntityPhysicsListener
import org.valkyrienskies.mod.common.ValkyrienSkiesMod

public object PhysicsBlockEntityUtil {
   @JvmStatic
   public fun onLoad(listener: BlockEntityPhysicsListener, pos: class_2338, level: class_1937, reason: String = "") {
      ValkyrienSkiesMod.INSTANCE.addBlockEntityPhysTicker(dimensionId, pos, listener)
   }

   @JvmStatic
   public fun onRemove(pos: class_2338, level: class_1937, reason: String = "") {
      ValkyrienSkiesMod.INSTANCE.removeBlockEntityPhysTicker(pos, dimensionId)
   }
}
