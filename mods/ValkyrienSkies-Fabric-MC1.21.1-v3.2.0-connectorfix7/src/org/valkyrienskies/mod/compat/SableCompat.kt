package org.valkyrienskies.mod.compat

import dev.ryanhcode.sable.companion.SableCompanion
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2374
import net.minecraft.class_2382
import net.minecraft.class_243
import org.joml.Vector3d
import org.joml.Vector3dc

public object SableCompat {
   public fun sublevelToWorld(level: class_1937?, pos: Vector3dc, dest: Vector3d): Vector3d {
      val var10000: Vector3d = SableCompanion.INSTANCE.projectOutOfSubLevel(level, pos, dest)
      return var10000
   }

   public fun sublevelToWorld(level: class_1937?, pos: class_2374): class_243 {
      val var10000: class_243 = SableCompanion.INSTANCE.projectOutOfSubLevel(level, pos)
      return var10000
   }

   public fun isChunkInSublevel(level: class_1937?, x: Int, y: Int): Boolean {
      return SableCompanion.INSTANCE.isInPlotGrid(level, x, y)
   }

   public fun isBlockInSublevel(level: class_1937?, x: Int, y: Int, z: Int): Boolean {
      return SableCompanion.INSTANCE.isInPlotGrid(level, class_2338(x, y, z) as class_2382)
   }
}
