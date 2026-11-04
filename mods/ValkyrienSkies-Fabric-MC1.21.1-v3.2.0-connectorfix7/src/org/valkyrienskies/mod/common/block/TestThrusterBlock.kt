package org.valkyrienskies.mod.common.block

import com.mojang.serialization.MapCodec
import net.minecraft.class_1657
import net.minecraft.class_1750
import net.minecraft.class_1937
import net.minecraft.class_2248
import net.minecraft.class_2318
import net.minecraft.class_2338
import net.minecraft.class_2343
import net.minecraft.class_2350
import net.minecraft.class_2498
import net.minecraft.class_2586
import net.minecraft.class_2680
import net.minecraft.class_2741
import net.minecraft.class_2769
import net.minecraft.class_3218
import net.minecraft.class_5819
import net.minecraft.class_2689.class_2690
import net.minecraft.class_4970.class_2251
import org.valkyrienskies.mod.common.blockentity.TestThrusterBlockEntity

public object TestThrusterBlock : class_2318(class_2251.method_9637().method_9629(10.0F, 1200.0F).method_9626(class_2498.field_11533)), class_2343 {
   private final val CODEC: MapCodec<TestThrusterBlock>

   fun method_9605(blockPlaceContext: class_1750): class_2680? {
      val var10000: class_2680 = this.method_9564()
         .method_11657(class_2741.field_12484 as class_2769, blockPlaceContext.method_8045().method_49803(blockPlaceContext.method_8037())) as class_2680
      val var10001: class_2769 = class_2318.field_10927 as class_2769
      if (blockPlaceContext.method_8036() != null) {
         val var10002: class_1657 = blockPlaceContext.method_8036()
         if (var10002.method_18276()) {
            var10000.method_11657(var10001, blockPlaceContext.method_7715().method_10153() as java.lang.Comparable) as class_2680
         }
      }

      var10000.method_11657(var10001, blockPlaceContext.method_8042() as java.lang.Comparable) as class_2680
   }

   fun method_9612(blockState: class_2680, level: class_1937, blockPos: class_2338, block: class_2248, blockPos2: class_2338, bl: Boolean) {
      if (!level.field_9236) {
         if (!(blockState.method_11654(class_2741.field_12484 as class_2769) as java.lang.Boolean == level.method_49803(blockPos))) {
            val var10002: Any = blockState.method_28493(class_2741.field_12484 as class_2769)
            level.method_8652(blockPos, var10002 as class_2680, 2)
         }

         if (level is class_3218 && (level as class_3218).method_8321(blockPos) is TestThrusterBlockEntity) {
            val var10000: class_2586 = (level as class_3218).method_8321(blockPos)
            (var10000 as TestThrusterBlockEntity).isActive = blockState.method_11654(class_2741.field_12484 as class_2769)
         }
      }
   }

   fun method_9588(blockState: class_2680, serverLevel: class_3218, blockPos: class_2338, randomSource: class_5819) {
   }

   fun method_9515(builder: class_2690<class_2248, class_2680>) {
      builder.method_11667(arrayOf(class_2741.field_12484)).method_11667(arrayOf(class_2318.field_10927))
   }

   fun method_10123(blockPos: class_2338, blockState: class_2680): class_2586? {
      TestThrusterBlockEntity(blockPos, blockState) as class_2586
   }

   fun method_53969(): MapCodec<TestThrusterBlock> {
      CODEC
   }

   @JvmStatic
   fun {
      val var10000: MapCodec = class_2318.method_54094({ it: class_2251 ->
         INSTANCE
      })
      CODEC = var10000
      INSTANCE.method_9590(
         ((INSTANCE.field_10647.method_11664() as class_2680).method_11657(class_2741.field_12484 as class_2769, false) as class_2680)
            .method_11657(class_2318.field_10927 as class_2769, class_2350.field_11043 as java.lang.Comparable) as class_2680
      )
   }
}
