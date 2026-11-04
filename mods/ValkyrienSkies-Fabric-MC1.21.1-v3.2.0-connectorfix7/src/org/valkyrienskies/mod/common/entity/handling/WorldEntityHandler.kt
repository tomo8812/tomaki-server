package org.valkyrienskies.mod.common.entity.handling

import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import net.minecraft.class_1297
import net.minecraft.class_1665
import net.minecraft.class_1668
import net.minecraft.class_1675
import net.minecraft.class_1676
import net.minecraft.class_1937
import net.minecraft.class_243
import net.minecraft.class_4587
import net.minecraft.class_4597
import net.minecraft.class_897
import org.apache.logging.log4j.Logger
import org.joml.Matrix4dc
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.util.VectorConversionsKt
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.util.ClassLogger
import org.valkyrienskies.mod.util.LoggingKt

public object WorldEntityHandler : VSEntityHandler {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties = arrayOf(
      Reflection.property1(
         PropertyReference1Impl(WorldEntityHandler.class, "dbgLogger", "getDbgLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
      )
   );

   private final val dbgLogger: Logger by LoggingKt.logger().provideDelegate_j8jxBIw/* $VF was: provideDelegate-j8jxBIw */(INSTANCE, $$delegatedProperties[0])
      private final get() {
         return ClassLogger.getValue_impl/* $VF was: getValue-impl */(dbgLogger$delegate, this, $$delegatedProperties[0])
      }


   public override fun freshEntityInShipyard(entity: class_1297, ship: Ship) {
      this.moveEntityFromShipyardToWorld(entity, ship)
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
      val offset: class_243 = entityRenderer.method_23169(entity, partialTicks)
      matrixStack.method_22904(x + offset.field_1352, y + offset.field_1351, z + offset.field_1350)
   }

   public override fun positionSetFromVehicle(self: class_1297, vehicle: class_1297, x: Double, y: Double, z: Double) {
      val var10000: class_1937 = self.method_37908()
      val var10001: class_243 = vehicle.method_19538()
      if (VSGameUtilsKt.isBlockInShipyard(var10000, var10001) && VSGameUtilsKt.getShipManaging(vehicle) == null) {
         this.dbgLogger
            .warn(
               "[VS-TD-DBG] positionSetFromVehicle STOP_RIDING side=${if (self.method_37908().field_9236) "client" else "server"} selfClass=${self.getClass()
                  .getSimpleName()} vehicleClass=${vehicle.getClass().getSimpleName()} vehiclePos=(${vehicle.method_23317()},${vehicle.method_23318()},${vehicle.method_23321()}) requestedPos=($x,$y,$z)"
            )
            self.method_5848()
      } else {
         val var9: Vector3d = VSGameUtilsKt.toWorldCoordinates$default(self.method_37908(), x, y, z, null, 8, null)
         self.method_5814(VectorConversionsKt.component1(var9), VectorConversionsKt.component2(var9), VectorConversionsKt.component3(var9))
      }
   }

   public override fun getTeleportPos(self: class_1297, pos: Vector3d): Vector3d {
      return VSGameUtilsKt.toWorldCoordinates(self.method_37908(), pos)
   }

   public override fun applyRenderOnMountedEntity(ship: ClientShip, self: class_1297, passenger: class_1297, partialTicks: Float, matrixStack: class_4587) {
   }

   public fun moveEntityFromShipyardToWorld(entity: class_1297, ship: Ship) {
      this.moveEntityFromShipyardToWorld(entity, ship, entity.method_23317(), entity.method_23318(), entity.method_23321())
   }

   public fun moveEntityFromShipyardToWorld(entity: class_1297, ship: Ship, entityX: Double, entityY: Double, entityZ: Double) {
      val newPos: Vector3d = ship.shipToWorld.transformPosition(Vector3d(entityX, entityY, entityZ))
      entity.method_5814(newPos.x, newPos.y, newPos.z)
      entity.field_6014 = entity.method_23317()
      entity.field_6036 = entity.method_23318()
      entity.field_5969 = entity.method_23321()
      val var10000: Quaterniondc = ship.transform.shipToWorldRotation
      var var10001: class_243 = entity.method_18798()
      val entityVelocity: Vector3d = var10000.transform(VectorConversionsMCKt.toJOML(var10001))
      entity.method_18799(VectorConversionsMCKt.toMinecraft(entityVelocity))
      if (entity is class_1676) {
         VSGameUtilsKt.applyShipVelocity(entity, ship)
      } else {
         (entity as IEntityDraggingInformationProvider).vs$dragImmediately(ship)
      }

      var yaw: Double = 0.0
      var pitch: Double = 0.0
      if (entity is class_1665) {
         val var20: class_243 = (entity as class_1665).method_18798()
         val var16: Vector3d = VectorConversionsMCKt.toJOML(var20)
         yaw = Math.atan2(var16.x, var16.z)
         pitch = Math.atan2(var16.y, Math.sqrt(var16.x * var16.x + var16.z * var16.z))
      } else {
         val var21: Matrix4dc = ship.shipToWorld
         var10001 = entity.method_5720()
         val var17: Vector3d = var21.transformDirection(VectorConversionsMCKt.toJOML(var10001))
         yaw = Math.atan2(-var17.x, var17.z)
         pitch = Math.atan2(-var17.y, Math.sqrt(var17.x * var17.x + var17.z * var17.z))
      }

      entity.method_36456((float)(yaw * (180.0 / Math.PI)))
      entity.method_36457((float)(pitch * (180.0 / Math.PI)))
      entity.field_5982 = entity.method_36454()
      entity.field_6004 = entity.method_36455()
      if (entity is class_1668) {
         class_1675.method_7484(entity, 1.0F)
      }
   }
}
