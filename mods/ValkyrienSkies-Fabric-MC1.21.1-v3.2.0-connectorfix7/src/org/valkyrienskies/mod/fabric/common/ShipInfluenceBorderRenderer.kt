package org.valkyrienskies.mod.fabric.common

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.class_1921
import net.minecraft.class_238
import net.minecraft.class_243
import net.minecraft.class_310
import net.minecraft.class_4587
import net.minecraft.class_4588
import net.minecraft.class_4597
import net.minecraft.class_638
import net.minecraft.class_761
import org.joml.primitives.AABBic
import org.valkyrienskies.mod.client.ShipDebugRender
import org.valkyrienskies.mod.common.VSClientGameUtils
import org.valkyrienskies.mod.common.config.VSClientConfig

public object ShipInfluenceBorderRenderer {
   private const val R: Float = 0.0F
   private const val G: Float = 0.45F
   private const val B: Float = 1.0F
   private const val A: Float = 1.0F

   public fun register() {
      WorldRenderEvents.AFTER_ENTITIES.register({ it: WorldRenderContext ->
         val var10000: ShipInfluenceBorderRenderer = INSTANCE
         var10000.render(it)
      })
   }

   private fun render(context: WorldRenderContext) {
      if (ShipDebugRender.influenceBorder) {
         val mc: class_310 = class_310.method_1551()
         if (mc.field_1687 != null) {
            val world: class_638 = mc.field_1687
            val var10000: class_4587 = context.matrixStack()
            if (var10000 != null) {
               val poseStack: class_4587 = var10000
               val var40: class_4597 = context.consumers()
               if (var40 != null) {
                  val var41: class_4588 = var40.getBuffer(class_1921.method_23594())
                  if (var41 != null) {
                     val consumer: class_4588 = var41
                     val cam: class_243 = mc.field_1773.method_19418().method_19326()
                     val extLeft: Double = VSClientConfig.CLIENT.influenceExtendLeft
                     val extRight: Double = VSClientConfig.CLIENT.influenceExtendRight
                     val extBottom: Double = VSClientConfig.CLIENT.influenceExtendBottom
                     val extTop: Double = VSClientConfig.CLIENT.influenceExtendTop
                     val extBack: Double = VSClientConfig.CLIENT.influenceExtendBack
                     val extFront: Double = VSClientConfig.CLIENT.influenceExtendFront

                     for (ship in shipObjectWorld.loadedShips) {
                        val var42: AABBic = ship.getShipAABB()
                        if (var42 != null) {
                           val minX: Double = var42.minX() - extLeft
                           val maxX: Double = var42.maxX() + extRight
                           val minY: Double = var42.minY() - extBottom
                           val maxY: Double = var42.maxY() + extTop
                           val minZ: Double = var42.minZ() - extBack
                           val maxZ: Double = var42.maxZ() + extFront
                           val cx: Double = (minX + maxX) * 0.5
                           val cy: Double = (minY + maxY) * 0.5
                           val cz: Double = (minZ + maxZ) * 0.5
                           poseStack.method_22903()
                           VSClientGameUtils.transformRenderWithShip(
                              ship.renderTransform, poseStack, cx, cy, cz, cam.field_1352, cam.field_1351, cam.field_1350
                           )
                           class_761.method_22982(
                              poseStack, consumer, class_238(minX - cx, minY - cy, minZ - cz, maxX - cx, maxY - cy, maxZ - cz), 0.0F, 0.45F, 1.0F, 1.0F
                           )
                           poseStack.method_22909()
                        }
                     }

                     return
                  }
               }
            }
         }
      }
   }
}
