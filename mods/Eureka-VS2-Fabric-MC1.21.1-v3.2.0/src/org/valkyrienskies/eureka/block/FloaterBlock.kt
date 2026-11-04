package org.valkyrienskies.eureka.block

import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1937
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2382
import net.minecraft.class_2498
import net.minecraft.class_2680
import net.minecraft.class_2741
import net.minecraft.class_2769
import net.minecraft.class_3218
import net.minecraft.class_3620
import net.minecraft.class_2689.class_2690
import net.minecraft.class_4970.class_2251
import org.valkyrienskies.core.api.attachment.AttachmentHolder
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.VSGameUtilsKt

@SourceDebugExtension(["SMAP\nFloaterBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FloaterBlock.kt\norg/valkyrienskies/eureka/block/FloaterBlock\n+ 2 AttachmentHolder.kt\norg/valkyrienskies/core/api/attachment/AttachmentHolderKt\n*L\n1#1,80:1\n66#2:81\n66#2:82\n*S KotlinDebug\n*F\n+ 1 FloaterBlock.kt\norg/valkyrienskies/eureka/block/FloaterBlock\n*L\n60#1:81\n75#1:82\n*E\n"])
public class FloaterBlock : class_2248(
      class_2251.method_9637().method_31710(class_3620.field_15996).method_9626(class_2498.field_11543).method_9629(1.5F, 3.0F).method_29292()
   ) {
   fun method_9515(builder: class_2690<class_2248, class_2680>) {
      super.method_9515(builder)
      builder.method_11667(arrayOf(class_2741.field_12511))
   }

   fun method_9615(state: class_2680, level: class_1937, pos: class_2338, oldState: class_2680, isMoving: Boolean) {
      super.method_9615(state, level, pos, oldState, isMoving)
      if (!level.field_9236) {
         val var10001: java.lang.Comparable = state.method_11654(class_2741.field_12511 as class_2769)
         val floaterPower: Int = 15 - (var10001 as java.lang.Number).intValue()
         val var10000: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level as class_3218, pos as class_2382)
         if (var10000 != null) {
            val var8: EurekaShipControl = EurekaShipControl.Companion.getOrCreate(var10000)
            var8.floaters = var8.floaters + floaterPower
         }
      }
   }

   fun method_9612(state: class_2680, level: class_1937, pos: class_2338, block: class_2248, fromPos: class_2338, isMoving: Boolean) {
      super.method_9612(state, level, pos, block, fromPos, isMoving)
      if ((level as? class_3218) != null) {
         val signal: Int = level.method_49804(pos)
         val currentPower: Int = state.method_11654(class_2741.field_12511 as class_2769) as Int
         val var10000: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level as class_3218, pos as class_2382)
         if (var10000 != null) {
            val var13: EurekaShipControl = (var10000 as AttachmentHolder).getAttachment(EurekaShipControl.class) as EurekaShipControl
            if (var13 != null) {
               var13.floaters = var13.floaters + (currentPower - signal)
            }
         }

         (level as class_3218).method_8652(pos, state.method_11657(class_2741.field_12511 as class_2769, signal) as class_2680, 2)
      }
   }

   fun method_9536(state: class_2680, level: class_1937, pos: class_2338, newState: class_2680, isMoving: Boolean) {
      super.method_9536(state, level, pos, newState, isMoving)
      if (!level.field_9236) {
         val var10001: java.lang.Comparable = state.method_11654(class_2741.field_12511 as class_2769)
         val floaterPower: Int = 15 - (var10001 as java.lang.Number).intValue()
         val var10000: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level as class_3218, pos as class_2382)
         if (var10000 != null) {
            val var11: EurekaShipControl = (var10000 as AttachmentHolder).getAttachment(EurekaShipControl.class) as EurekaShipControl
            if (var11 != null) {
               var11.floaters = var11.floaters - floaterPower
            }
         }
      }
   }
}
