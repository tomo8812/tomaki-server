package org.valkyrienskies.mod.util

import net.minecraft.class_238
import org.joml.Vector3d
import org.joml.Vector3dc

public final val DEFAULT_WORLD_GRAVITY: Vector3dc = Vector3d(0.0, -10.0, 0.0) as Vector3dc

public fun class_238.scale(scale: Double): class_238 {
   val sizeX: Double = `$this$scale`.method_17939() * scale / 2
   val sizeY: Double = `$this$scale`.method_17940() * scale / 2
   val sizeZ: Double = `$this$scale`.method_17941() * scale / 2
   return class_238(
      `$this$scale`.method_1005().field_1352 - sizeX,
      `$this$scale`.method_1005().field_1351 - sizeY,
      `$this$scale`.method_1005().field_1350 - sizeZ,
      `$this$scale`.method_1005().field_1352 + sizeX,
      `$this$scale`.method_1005().field_1351 + sizeY,
      `$this$scale`.method_1005().field_1350 + sizeZ
   )
}
