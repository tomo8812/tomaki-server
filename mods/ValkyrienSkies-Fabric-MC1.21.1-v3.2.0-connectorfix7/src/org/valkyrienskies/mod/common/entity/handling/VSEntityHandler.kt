package org.valkyrienskies.mod.common.entity.handling

import net.minecraft.class_1297
import net.minecraft.class_4587
import net.minecraft.class_4597
import net.minecraft.class_897
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.Ship

public interface VSEntityHandler {
   public abstract fun freshEntityInShipyard(entity: class_1297, ship: Ship) {
   }

   public abstract fun entityRemovedFromShipyard(entity: class_1297, ship: Ship) {
   }

   public abstract fun <T : class_1297> applyRenderTransform(
      ship: ClientShip,
      entity: Any,
      entityRenderer: class_897<Any>,
      x: Double,
      y: Double,
      z: Double,
      rotationYaw: Float,
      partialTicks: Float,
      matrixStack: class_4587,
      buffer: class_4597,
      packedLight: Int
   ) {
   }

   public abstract fun positionSetFromVehicle(self: class_1297, vehicle: class_1297, x: Double, y: Double, z: Double) {
   }

   public abstract fun getTeleportPos(self: class_1297, pos: Vector3d): Vector3d {
   }

   public abstract fun applyRenderOnMountedEntity(ship: ClientShip, self: class_1297, passenger: class_1297, partialTicks: Float, matrixStack: class_4587) {
   }
}
