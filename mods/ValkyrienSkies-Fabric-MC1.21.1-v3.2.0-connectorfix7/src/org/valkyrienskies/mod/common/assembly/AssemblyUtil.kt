package org.valkyrienskies.mod.common.assembly

import net.minecraft.class_1936
import net.minecraft.class_1937
import net.minecraft.class_2246
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2487
import net.minecraft.class_2586
import net.minecraft.class_2680
import net.minecraft.class_2791
import net.minecraft.class_2818
import net.minecraft.class_2826
import net.minecraft.class_3532
import net.minecraft.class_6760
import net.minecraft.class_2350.class_2351
import net.minecraft.class_2350.class_2352
import net.minecraft.class_7225.class_7874
import org.joml.Vector3d
import org.joml.Vector3i

public object AssemblyUtil {
   public fun setBlock(level: class_1937, pos: class_2338, state: class_2680) {
      val var10000: class_2791 = level.method_22350(pos)
      val section: class_2826 = (var10000 as class_2818).method_38259((var10000 as class_2818).method_31602(pos.method_10264()))
      val oldState: class_2680 = level.method_8320(pos)
      section.method_16675(pos.method_10263() and 15, pos.method_10264() and 15, pos.method_10260() and 15, state)
      level.method_8413(pos, oldState, state, 2)
   }

   public fun removeBlock(level: class_1937, pos: class_2338) {
      level.method_8544(pos)
      level.method_22350(pos).method_12010(pos, class_2246.field_10124.method_9564(), false)
   }

   public fun copyBlock(level: class_1937, from: class_2338, to: class_2338) {
      val state: class_2680 = level.method_8320(from)
      val blockentity: class_2586 = level.method_8321(from)
      level.method_22350(to).method_12010(to, state, false)
      if (level.method_8397().method_8674(from, state.method_26204())) {
         level.method_8397().method_39363(class_6760(state.method_26204(), to, 0L, 0L))
      }

      if (state.method_31709() && blockentity != null) {
         val var10000: class_2487 = blockentity.method_38243(level.method_30349() as class_7874)
         level.method_8438(blockentity)
         val newBlockentity: class_2586 = level.method_8321(to)
         if (newBlockentity != null) {
            newBlockentity.method_58690(var10000, level.method_30349() as class_7874)
         }
      }
   }

   public fun updateBlock(level: class_1937, fromPos: class_2338, toPos: class_2338, toState: class_2680) {
      level.method_16109(fromPos, toState, AssemblyUtilKt.access$getAIR$p())
      level.method_8413(fromPos, toState, AssemblyUtilKt.access$getAIR$p(), 11)
      level.method_8408(fromPos, AssemblyUtilKt.access$getAIR$p().method_26204())
      AssemblyUtilKt.access$getAIR$p().method_26198(level as class_1936, fromPos, 11, 511 - 1)
      AssemblyUtilKt.access$getAIR$p().method_26183(level as class_1936, fromPos, 11, 511)
      AssemblyUtilKt.access$getAIR$p().method_26198(level as class_1936, fromPos, 11, 511)
      level.method_8398().method_12130().method_15513(fromPos)
      level.method_16109(toPos, AssemblyUtilKt.access$getAIR$p(), toState)
      level.method_8413(toPos, AssemblyUtilKt.access$getAIR$p(), toState, 11)
      level.method_8408(toPos, toState.method_26204())
      if (!level.field_9236 && toState.method_26221()) {
         level.method_8455(toPos, toState.method_26204())
      }

      level.method_8398().method_12130().method_15513(toPos)
   }

   public fun toBlockPos(x: Double, y: Double, z: Double): class_2338 {
      return class_2338(class_3532.method_15357(x), (int)Math.floor(y), (int)Math.floor(z))
   }

   public fun toBlockPos(vec: Vector3d): class_2338 {
      return this.toBlockPos(vec.x, vec.y, vec.z)
   }

   public fun getVecDirection(v: Vector3i): class_2350 {
      var axis: class_2351 = class_2351.field_11048
      if (v.y() != 0) {
         axis = class_2351.field_11052
      }

      if (v.z() != 0) {
         axis = class_2351.field_11051
      }

      val var10000: class_2350 = class_2350.method_10169(axis, if (v.x + v.y + v.z > 0) class_2352.field_11056 else class_2352.field_11060)
      return var10000
   }

   public fun getMinCorner(pos1: class_2338, pos2: class_2338): class_2338 {
      return class_2338(
         Math.min(pos1.method_10263(), pos2.method_10263()),
         Math.min(pos1.method_10264(), pos2.method_10264()),
         Math.min(pos1.method_10260(), pos2.method_10260())
      )
   }

   public fun getMaxCorner(pos1: class_2338, pos2: class_2338): class_2338 {
      return class_2338(
         Math.max(pos1.method_10263(), pos2.method_10263()),
         Math.max(pos1.method_10264(), pos2.method_10264()),
         Math.max(pos1.method_10260(), pos2.method_10260())
      )
   }

   public fun getMiddle(pos1: class_2338, pos2: class_2338): Vector3i {
      return Vector3i(
         (int)(
            (double)Math.min(pos1.method_10263(), pos2.method_10263())
               + (Math.max(pos1.method_10263(), pos2.method_10263()) - Math.min(pos1.method_10263(), pos2.method_10263()) + 1) / 2
         ),
         (int)(
            (double)Math.min(pos1.method_10264(), pos2.method_10264())
               + (Math.max(pos1.method_10264(), pos2.method_10264()) - Math.min(pos1.method_10264(), pos2.method_10264()) + 1) / 2
         ),
         (int)(
            (double)Math.min(pos1.method_10260(), pos2.method_10260())
               + (Math.max(pos1.method_10260(), pos2.method_10260()) - Math.min(pos1.method_10260(), pos2.method_10260()) + 1) / 2
         )
      )
   }

   public fun getMiddle(pos1: Vector3d, pos2: Vector3d): Vector3d {
      return Vector3d(
         Math.min(pos1.x, pos2.x) + (Math.max(pos1.x, pos2.x) - Math.min(pos1.x, pos2.x)) / 2.0,
         Math.min(pos1.y, pos2.y) + (Math.max(pos1.y, pos2.y) - Math.min(pos1.y, pos2.y)) / 2.0,
         Math.min(pos1.z, pos2.z) + (Math.max(pos1.z, pos2.z) - Math.min(pos1.z, pos2.z)) / 2.0
      )
   }
}
