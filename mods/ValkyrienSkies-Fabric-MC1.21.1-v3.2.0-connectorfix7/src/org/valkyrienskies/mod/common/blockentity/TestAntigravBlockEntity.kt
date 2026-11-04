package org.valkyrienskies.mod.common.blockentity

import net.minecraft.class_2338
import net.minecraft.class_2586
import net.minecraft.class_2680
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.mod.api.BlockEntityPhysicsListener
import org.valkyrienskies.mod.common.ValkyrienSkiesMod

public class TestAntigravBlockEntity(blockPos: class_2338, blockState: class_2680) : class_2586(
         ValkyrienSkiesMod.INSTANCE.TEST_ANTIGRAV_BLOCK_ENTITY_TYPE, blockPos, blockState
      ),
   BlockEntityPhysicsListener {
   public open lateinit var dimension: String

   public override fun physTick(physShip: PhysShip?, physLevel: PhysLevel) {
      if (physShip != null) {
         PhysShip.applyWorldForceToBodyPos$default(
            physShip,
            Vector3d(
               0.0,
               physShip.mass * (physLevel.aerodynamicUtils.getAtmosphereForDimension(physLevel.dimension).getThird() as java.lang.Number).doubleValue(),
               0.0
            ),
            null,
            2,
            null
         )
      }
   }
}
