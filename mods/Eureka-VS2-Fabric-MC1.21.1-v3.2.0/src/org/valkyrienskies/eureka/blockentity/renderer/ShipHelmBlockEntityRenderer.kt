package org.valkyrienskies.eureka.blockentity.renderer

import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2586
import net.minecraft.class_2741
import net.minecraft.class_2769
import net.minecraft.class_4587
import net.minecraft.class_4597
import net.minecraft.class_827
import net.minecraft.class_5614.class_5615
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity
import org.valkyrienskies.mod.common.VSGameUtilsKt

public class ShipHelmBlockEntityRenderer(ctx: class_5615) : class_827<ShipHelmBlockEntity> {
   public final val ctx: class_5615

   init {
      this.ctx = ctx
   }

   public open fun render(
      blockEntity: ShipHelmBlockEntity,
      partialTicks: Float,
      matrixStack: class_4587,
      buffer: class_4597,
      combinedLight: Int,
      combinedOverlay: Int
   ) {
      matrixStack.method_22903()
      matrixStack.method_22904(0.5, 0.6, 0.5)
      matrixStack.method_22907(
         Quaternionf(
            AxisAngle4f(
               (float)(
                  (double)(-(blockEntity.method_11010().method_11654(class_2741.field_12481 as class_2769) as class_2350).method_10144()) * Math.PI / 180.0
               ),
               0.0F,
               1.0F,
               0.0F
            )
         )
      )
      val var10000: class_1937 = blockEntity.method_10997()
      val var10: Ship
      if (var10000 != null) {
         val var10001: class_2338 = blockEntity.method_11016()
         var10 = VSGameUtilsKt.getShipManagingPos(var10000, var10001)
      } else {
         var10 = null
      }

      var rot: Double = 0.0
      if (var10 != null) {
         rot = var10.getOmega().y()
      }

      matrixStack.method_22904(0.0, 0.0, 0.19)
      matrixStack.method_22907(Quaternionf(AxisAngle4f((float)(rot / (double)20.0F * (double)(float) Math.PI), 0.0F, 0.0F, 1.0F)))
      WheelModels.INSTANCE.render(matrixStack, blockEntity, buffer, combinedLight, combinedOverlay)
      matrixStack.method_22909()
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun method_3569(blockEntity: class_2586, f: Float, poseStack: class_4587, multiBufferSource: class_4597, i: Int, j: Int) {
      this.render(blockEntity as ShipHelmBlockEntity, f, poseStack, multiBufferSource, i, j)
   }
}
