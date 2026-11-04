package org.valkyrienskies.eureka.client

import java.util.ArrayList
import java.util.Arrays
import net.minecraft.class_1297
import net.minecraft.class_310
import net.minecraft.class_327
import net.minecraft.class_332
import net.minecraft.class_4587
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.entity.ShipMountingEntity

public object EurekaSpeedHud {
   private const val COLOR: Int = -922746881
   private const val SCALE: Float = 0.66F
   private const val TOP_MARGIN: Float = 4.0F
   private const val SEPARATOR: String = "  |  "
   private final val COMPASS: Array<String>

   public fun render(guiGraphics: class_332) {
      val cfg: EurekaConfig.Client = EurekaConfig.CLIENT
      if (EurekaConfig.CLIENT.displayHud) {
         if (cfg.displaySpeed || cfg.displayAltitude || cfg.displayHeading) {
            val mc: class_310 = class_310.method_1551()
            val parts: class_1297 = if (mc.field_1724 != null) mc.field_1724.method_5854() else null
            val var10000: ShipMountingEntity = parts as? ShipMountingEntity
            if ((parts as? ShipMountingEntity) != null) {
               val var19: Ship = VSGameUtilsKt.getShipManaging(var10000 as class_1297)
               if (var19 != null) {
                  val var12: ArrayList = ArrayList(3)
                  if (cfg.displaySpeed) {
                     val var15: Array<Any> = arrayOf(var19.getVelocity().length())
                     val var10001: java.lang.String = java.lang.String.format("%.1f", Arrays.copyOf(var15, var15.length))
                     var12.add("$var10001m/s")
                  }

                  if (cfg.displayAltitude) {
                     val var17: Array<Any> = arrayOf(var19.getTransform().getPositionInWorld().y())
                     val var20: java.lang.String = java.lang.String.format("%.0f", Arrays.copyOf(var17, var17.length))
                     var12.add("Y $var20")
                  }

                  if (cfg.displayHeading) {
                     var12.add(this.heading(var19))
                  }

                  if (!var12.isEmpty()) {
                     val line: java.lang.String = CollectionsKt.joinToString$default(var12, "  |  ", null, null, 0, null, null, 62, null)
                     val var14: class_327 = mc.field_1772
                     val var18: class_4587 = guiGraphics.method_51448()
                     var18.method_22903()
                     var18.method_22905(0.66F, 0.66F, 1.0F)
                     guiGraphics.method_51433(
                        var14,
                        line,
                        Math.round(((float)guiGraphics.method_51421() / 0.66F - (float)var14.method_1727(line)) / 2.0F),
                        Math.round(6.060606F),
                        -922746881,
                        true
                     )
                     var18.method_22909()
                  }
               }
            }
         }
      }
   }

   private fun heading(ship: Ship): String {
      val fwd: Vector3d = Vector3d(0.0, 0.0, -1.0)
      ship.getTransform().getShipToWorldRotation().transform(fwd)
      val var5: Double = (Math.toDegrees(-Math.atan2(fwd.x(), fwd.z())) % 360.0 + 360.0) % 360.0
      return "${COMPASS[(int)Math.round(var5 / 45.0) % 8]} ${Math.round(var5)}°"
   }
}
