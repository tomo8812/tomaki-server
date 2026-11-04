package org.valkyrienskies.mod.common.entity.handling

import com.mojang.logging.LogUtils
import net.minecraft.class_1297
import net.minecraft.class_1665
import net.minecraft.class_1668
import net.minecraft.class_1675
import net.minecraft.class_243
import net.minecraft.class_4587
import net.minecraft.class_4597
import net.minecraft.class_897
import org.joml.Matrix4dc
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt

public abstract class AbstractShipyardEntityHandler : VSEntityHandler {
   public override fun freshEntityInShipyard(entity: class_1297, ship: Ship) {
   }

   public override fun entityRemovedFromShipyard(entity: class_1297, ship: Ship) {
   }

   public override fun <T : class_1297> applyRenderTransform(
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
      val transform: ShipTransform = ship.renderTransform
      val entityPosition: class_243 = entity.method_30950(partialTicks)
      val var10000: Matrix4dc = transform.shipToWorld
      val transformed: Vector3d = var10000.transformPosition(VectorConversionsMCKt.toJOML(entityPosition))
      val camX: Double = x - entityPosition.field_1352
      val camY: Double = y - entityPosition.field_1351
      val camZ: Double = z - entityPosition.field_1350
      val offset: class_243 = entityRenderer.method_23169(entity, partialTicks)
      val scale: Vector3dc = transform.shipToWorldScaling
      matrixStack.method_22904(transformed.x + camX, transformed.y + camY, transformed.z + camZ)
      matrixStack.method_22907(Quaternionf(transform.shipToWorldRotation))
      matrixStack.method_22905((float)scale.x(), (float)scale.y(), (float)scale.z())
      matrixStack.method_22904(offset.field_1352, offset.field_1351, offset.field_1350)
   }

   public override fun positionSetFromVehicle(self: class_1297, vehicle: class_1297, x: Double, y: Double, z: Double) {
      self.method_5814(x, y, z)
   }

   public override fun getTeleportPos(self: class_1297, pos: Vector3d): Vector3d {
      return pos
   }

   public override fun applyRenderOnMountedEntity(ship: ClientShip, self: class_1297, passenger: class_1297, partialTicks: Float, matrixStack: class_4587) {
      matrixStack.method_22907(Quaternionf(ship.renderTransform.shipToWorldRotation))
   }

   public fun moveEntityFromWorldToShipyard(entity: class_1297, ship: Ship) {
      this.moveEntityFromWorldToShipyard(entity, ship, entity.method_23317(), entity.method_23318(), entity.method_23321())
   }

   public fun moveEntityFromWorldToShipyard(entity: class_1297, ship: Ship, entityX: Double, entityY: Double, entityZ: Double) {
      val shipyardPos: Vector3d = ship.worldToShip.transformPosition(entityX, entityY, entityZ, Vector3d())
      var var10000: class_243 = entity.method_19538()
      val var22: Vector3d = VectorConversionsMCKt.toJOML(var10000).sub(ship.transform.positionInWorld)
      val shipPosVelocity: Vector3d = Vector3d(ship.velocity).add(Vector3d(ship.omega).cross(var22)).mul(0.05)
      var10000 = entity.method_18798()
      val var24: Vector3d = VectorConversionsMCKt.toJOML(var10000).sub(shipPosVelocity)
      ship.worldToShip.transformDirection(var24)
      entity.method_5814(shipyardPos.x, shipyardPos.y, shipyardPos.z)
      entity.method_18799(VectorConversionsMCKt.toMinecraft(var24))
      entity.field_6014 = shipyardPos.x
      entity.field_6036 = shipyardPos.y
      entity.field_5969 = shipyardPos.z
      var yaw: Double = 0.0
      var pitch: Double = 0.0
      if (entity is class_1665) {
         var10000 = (entity as class_1665).method_18798()
         val var18: Vector3d = VectorConversionsMCKt.toJOML(var10000)
         yaw = Math.atan2(var18.x, var18.z)
         pitch = Math.atan2(var18.y, Math.sqrt(var18.x * var18.x + var18.z * var18.z))
      } else {
         val var26: Matrix4dc = ship.worldToShip
         val var10001: class_243 = entity.method_5720()
         val var19: Vector3d = var26.transformDirection(VectorConversionsMCKt.toJOML(var10001))
         yaw = Math.atan2(-var19.x, var19.z)
         pitch = Math.atan2(-var19.y, Math.sqrt(var19.x * var19.x + var19.z * var19.z))
      }

      entity.method_36456((float)(yaw * (180.0 / Math.PI)))
      entity.method_36457((float)(pitch * (180.0 / Math.PI)))
      LogUtils.getLogger().info("Yaw {}, Pitch {}", entity.method_36454(), entity.method_36455())
      entity.field_5982 = entity.method_36454()
      entity.field_6004 = entity.method_36455()
      if (entity is class_1668) {
         class_1675.method_7484(entity, 1.0F)
      }
   }
}
