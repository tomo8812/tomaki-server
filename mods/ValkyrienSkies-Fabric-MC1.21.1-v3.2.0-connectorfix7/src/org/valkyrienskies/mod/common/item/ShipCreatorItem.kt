package org.valkyrienskies.mod.common.item

import java.util.function.DoubleSupplier
import net.minecraft.class_1269
import net.minecraft.class_1657
import net.minecraft.class_1792
import net.minecraft.class_1799
import net.minecraft.class_1838
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2382
import net.minecraft.class_2470
import net.minecraft.class_2561
import net.minecraft.class_2680
import net.minecraft.class_3218
import net.minecraft.class_1792.class_1793
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.slf4j.LoggerFactory
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.internal.VsiCore
import org.valkyrienskies.core.internal.ships.VsiServerShip
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.util.RelocationUtilKt

public class ShipCreatorItem(properties: class_1793, scale: DoubleSupplier, minScaling: DoubleSupplier) : class_1792(properties) {
   private final val scale: DoubleSupplier
   private final val minScaling: DoubleSupplier

   init {
      this.scale = scale
      this.minScaling = minScaling
   }

   fun method_7886(stack: class_1799): Boolean {
      true
   }

   fun method_7884(ctx: class_1838): class_1269 {
      val blockState: class_1937 = ctx.method_8045()
      val var10000: class_3218 = blockState as? class_3218
      if ((blockState as? class_3218) == null) {
         val var26: class_1269 = super.method_7884(ctx)
         var26
      } else {
         val blockPos: class_2338 = ctx.method_8037()
         val var21: class_2680 = var10000.method_8320(blockPos)
         if (!var10000.field_9236) {
            val var22: class_1937 = ctx.method_8045()
            val parentShip: Ship = VSGameUtilsKt.getShipManagingPos(var22, blockPos)
            if (!var21.method_26215()) {
               val dimensionId: java.lang.String = dimensionId
               val scale: Double = this.scale.getAsDouble()
               val minScaling: Double = this.minScaling.getAsDouble()
               LoggerFactory.getLogger("VS2").info(" ShipCreatorItem: creating ship at $blockPos (block=$var21, dim=$dimensionId, scale=$scale)")
               val serverShip: ServerShip = shipObjectWorld
                  .createNewShipAtBlock(VectorConversionsMCKt.toJOML(blockPos as class_2382), false, scale, dimensionId)
                  LoggerFactory.getLogger("VS2").info(" ShipCreatorItem: ship created id=${serverShip.getId()}, slug=${serverShip.slug}")
               EntityShipCollisionUtils.markShipAsRecentlySpawned$default(serverShip.getId(), var10000.method_8510(), 0L, 4, null)
               val centerPos: class_2338 = VectorConversionsMCKt.toBlockPos(
                  ChunkClaim.getCenterBlockCoordinates$default(serverShip.getChunkClaim(), yRange, null, 2, null)
               )
               LoggerFactory.getLogger("VS2").info(" ShipCreatorItem: relocating block from $blockPos to $centerPos in shipyard")
               RelocationUtilKt.relocateBlock(var10000 as class_1937, blockPos, centerPos, true, serverShip, class_2470.field_11467)
               LoggerFactory.getLogger("VS2").info(" ShipCreatorItem: block relocated. Ship transform pos=(${serverShip.getTransform().getPosition()})")
               val var23: class_1657 = ctx.method_8036()
               if (var23 != null) {
                  var23.method_43496(class_2561.method_43469("command.valkyrienskies.shipify.success_one", arrayOf(serverShip.slug)) as class_2561)
               }

               if (parentShip != null) {
                  val newShipPosInWorld: Vector3d = parentShip.shipToWorld
                     .transformPosition(VectorConversionsMCKt.toJOMLD(blockPos as class_2382).add(0.5, 0.5, 0.5))
                     val var20: Vector3d = VectorConversionsMCKt.toJOMLD(blockPos as class_2382).add(0.5, 0.5, 0.5)
                  val newShipRotation: Quaterniondc = parentShip.transform.shipToWorldRotation
                  var newShipScaling: Vector3d = parentShip.transform.shipToWorldScaling.mul(scale, Vector3d())
                  if (newShipScaling.x() < minScaling) {
                     newShipScaling = Vector3d(minScaling, minScaling, minScaling)
                  }

                  val var24: VsiCore = vsCore
                  val var10001: Vector3dc = newShipPosInWorld
                  val var10003: Vector3dc = newShipScaling
                  val newTransform: BodyTransform = var24.newBodyTransform(var10001, newShipRotation, var10003, var20)
                  (serverShip as VsiServerShip).unsafeSetTransform(newTransform)
               }
            }
         }

         val var25: class_1269 = super.method_7884(ctx)
         var25
      }
   }
}
