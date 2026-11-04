package org.valkyrienskies.mod.common

import net.minecraft.class_2338
import net.minecraft.class_310
import net.minecraft.class_4587
import net.minecraft.class_638
import org.joml.Matrix4d
import org.joml.Matrix4dc
import org.joml.Matrix4f
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt

public object VSClientGameUtils {
   @JvmStatic
   public fun multiplyWithShipToWorld(poseStack: class_4587, ship: ClientShip) {
      VectorConversionsMCKt.multiply(poseStack, ship.renderTransform.shipToWorld, ship.renderTransform.shipToWorldRotation)
   }

   @JvmStatic
   public fun transformRenderIfInShipyard(poseStack: class_4587, offsetX: Double, offsetY: Double, offsetZ: Double) {
      val var10000: class_638 = class_310.method_1551().field_1687
      val ship: ClientShip = if (var10000 != null) VSGameUtilsKt.getLoadedShipManagingPos(var10000, offsetX, offsetY, offsetZ) else null
      if (ship != null) {
         val renderMatrix: Matrix4d = Matrix4d().mul(ship.renderTransform.shipToWorld).translate(offsetX, offsetY, offsetZ)
         VectorConversionsMCKt.multiply(poseStack, renderMatrix)
      } else {
         poseStack.method_22904(offsetX, offsetY, offsetZ)
      }
   }

   @JvmStatic
   public fun getClientShip(offsetX: Double, offsetY: Double, offsetZ: Double): ClientShip? {
      val var10000: class_638 = class_310.method_1551().field_1687
      return if (var10000 != null) VSGameUtilsKt.getLoadedShipManagingPos(var10000, offsetX, offsetY, offsetZ) else null
   }

   @JvmStatic
   public fun transformRenderWithShip(
      renderTransform: ShipTransform,
      poseStack: class_4587,
      offsetX: Double,
      offsetY: Double,
      offsetZ: Double,
      camX: Double,
      camY: Double,
      camZ: Double
   ) {
      val shipToWorldMatrix: Matrix4dc = renderTransform.shipToWorld
      val renderMatrix: Matrix4d = Matrix4d()
      renderMatrix.translate(-camX, -camY, -camZ)
      renderMatrix.mul(shipToWorldMatrix)
      renderMatrix.translate(offsetX, offsetY, offsetZ)
      VectorConversionsMCKt.multiply(poseStack, renderMatrix, renderTransform.shipToWorldRotation)
   }

   @JvmStatic
   public fun transformRenderWithShip(
      renderTransform: ShipTransform,
      matrix: Matrix4f,
      offsetX: Double,
      offsetY: Double,
      offsetZ: Double,
      camX: Double,
      camY: Double,
      camZ: Double
   ) {
      val shipToWorldMatrix: Matrix4dc = renderTransform.shipToWorld
      val renderMatrix: Matrix4d = Matrix4d()
      renderMatrix.translate(-camX, -camY, -camZ)
      renderMatrix.mul(shipToWorldMatrix)
      renderMatrix.translate(offsetX, offsetY, offsetZ)
      matrix.mul(Matrix4f(renderMatrix))
   }

   @JvmStatic
   public fun transformRenderWithShip(renderTransform: ShipTransform, matrix: class_4587, blockPos: class_2338, camX: Double, camY: Double, camZ: Double) {
      transformRenderWithShip(
         renderTransform, matrix, (double)blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260(), camX, camY, camZ
      )
   }
}
