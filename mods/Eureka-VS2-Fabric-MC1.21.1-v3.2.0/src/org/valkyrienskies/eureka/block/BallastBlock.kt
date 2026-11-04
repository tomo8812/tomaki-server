package org.valkyrienskies.eureka.block

import net.minecraft.class_1937
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2498
import net.minecraft.class_2680
import net.minecraft.class_2741
import net.minecraft.class_2769
import net.minecraft.class_3218
import net.minecraft.class_3620
import net.minecraft.class_2689.class_2690
import net.minecraft.class_4970.class_2251

public class BallastBlock : class_2248(
      class_2251.method_9637().method_31710(class_3620.field_16023).method_9626(class_2498.field_11544).method_9629(4.0F, 4.0F).method_29292()
   ) {
   fun method_9515(builder: class_2690<class_2248, class_2680>) {
      super.method_9515(builder)
      builder.method_11667(arrayOf(class_2741.field_12511))
   }

   fun method_9612(state: class_2680, level: class_1937, pos: class_2338, block: class_2248, fromPos: class_2338, isMoving: Boolean) {
      super.method_9612(state, level, pos, block, fromPos, isMoving)
      if ((level as? class_3218) != null) {
         (level as class_3218).method_8652(pos, state.method_11657(class_2741.field_12511 as class_2769, level.method_49804(pos)) as class_2680, 2)
      }
   }
}
