package org.valkyrienskies.mod.util

import net.minecraft.class_1936
import net.minecraft.class_1937
import net.minecraft.class_2246
import net.minecraft.class_2338
import net.minecraft.class_2470
import net.minecraft.class_2487
import net.minecraft.class_2586
import net.minecraft.class_2621
import net.minecraft.class_2680
import net.minecraft.class_2818
import net.minecraft.class_3829
import net.minecraft.class_7225.class_7874
import org.valkyrienskies.core.api.ships.ServerShip

public final val AIR: class_2680 = class_2246.field_10124.method_9564()

public fun relocateBlock(
   fromChunk: class_2818,
   from: class_2338,
   toChunk: class_2818,
   to: class_2338,
   doUpdate: Boolean,
   toShip: ServerShip?,
   rotation: class_2470 = class_2470.field_11467
) {
   var state: class_2680 = fromChunk.method_8320(from)
   val entity: class_2586 = fromChunk.method_8321(from)
   val level: class_1937 = toChunk.method_12200()
   val var10000: class_2487
   if (entity != null) {
      val be: class_2487 = entity.method_38242(fromChunk.method_12200().method_30349() as class_7874)
      be.method_10569("x", to.method_10263())
      be.method_10569("y", to.method_10264())
      be.method_10569("z", to.method_10260())
      if (entity is class_3829) {
         entity.method_58690(class_2487(), level.method_30349() as class_7874)
      }

      if (entity is class_2621) {
         (entity as class_2621).method_54867(null, 0L)
      }

      level.method_8544(from)
      var10000 = be
   } else {
      var10000 = null
   }

   state = state.method_26186(rotation)
   fromChunk.method_12010(from, AIR, false)
   toChunk.method_12010(to, state, false)
   if (doUpdate) {
      updateBlock(level, from, to, state)
   }

   if (var10000 != null) {
      val var21: class_2586 = level.method_8321(to)
      var21.method_58690(var10000, toChunk.method_12200().method_30349() as class_7874)
   }
}

public fun updateBlock(level: class_1937, fromPos: class_2338, toPos: class_2338, toState: class_2680) {
   level.method_16109(fromPos, toState, AIR)
   level.method_8413(fromPos, toState, AIR, 107)
   level.method_8408(fromPos, AIR.method_26204())
   AIR.method_26198(level as class_1936, fromPos, 107, 511 - 1)
   AIR.method_26183(level as class_1936, fromPos, 107, 511)
   AIR.method_26198(level as class_1936, fromPos, 107, 511)
   level.method_8398().method_12130().method_15513(fromPos)
   level.method_16109(toPos, AIR, toState)
   level.method_8413(toPos, AIR, toState, 107)
   level.method_8408(toPos, toState.method_26204())
   if (!level.field_9236 && toState.method_26221()) {
      level.method_8455(toPos, toState.method_26204())
   }

   level.method_8398().method_12130().method_15513(toPos)
}

public fun class_1937.relocateBlock(from: class_2338, to: class_2338, doUpdate: Boolean, toShip: ServerShip?, rotation: class_2470) {
   val var10000: class_2818 = `$this$relocateBlock`.method_8500(from)
   val var10002: class_2818 = `$this$relocateBlock`.method_8500(to)
   relocateBlock(var10000, from, var10002, to, doUpdate, toShip, rotation)
}
