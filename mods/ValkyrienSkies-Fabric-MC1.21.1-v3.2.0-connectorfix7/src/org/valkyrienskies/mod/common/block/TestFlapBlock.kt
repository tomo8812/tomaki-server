package org.valkyrienskies.mod.common.block

import com.mojang.serialization.MapCodec
import net.minecraft.class_1750
import net.minecraft.class_1922
import net.minecraft.class_1937
import net.minecraft.class_2248
import net.minecraft.class_2318
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_2498
import net.minecraft.class_265
import net.minecraft.class_2680
import net.minecraft.class_2769
import net.minecraft.class_3726
import net.minecraft.class_2689.class_2690
import net.minecraft.class_4970.class_2251
import org.valkyrienskies.core.api.ships.Wing
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt

public class TestFlapBlock : class_2318(class_2251.method_9637().method_9629(10.0F, 1200.0F).method_9626(class_2498.field_11533)), WingBlock {
   private final val EAST_AABB: class_265 = class_2318.method_9541(4.0, 0.0, 0.0, 12.0, 16.0, 16.0)
   private final val WEST_AABB: class_265 = class_2318.method_9541(4.0, 0.0, 0.0, 12.0, 16.0, 16.0)
   private final val SOUTH_AABB: class_265 = class_2318.method_9541(0.0, 0.0, 4.0, 16.0, 16.0, 12.0)
   private final val NORTH_AABB: class_265 = class_2318.method_9541(0.0, 0.0, 4.0, 16.0, 16.0, 12.0)
   private final val UP_AABB: class_265 = class_2318.method_9541(0.0, 4.0, 0.0, 16.0, 12.0, 16.0)
   private final val DOWN_AABB: class_265 = class_2318.method_9541(0.0, 4.0, 0.0, 16.0, 12.0, 16.0)

   fun method_9515(builder: class_2690<class_2248, class_2680>) {
      builder.method_11667(arrayOf(class_2318.field_10927))
   }

   fun method_9605(ctx: class_1750): class_2680 {
      val var10000: Any = this.method_9564().method_11657(class_2318.field_10927 as class_2769, ctx.method_7715().method_10153() as java.lang.Comparable)
      var10000 as class_2680
   }

   /** @deprecated */
   @Deprecated(message = "Deprecated in Java")
   fun method_9530(state: class_2680, blockGetter: class_1922, blockPos: class_2338, collisionContext: class_3726): class_265 {
      val var10000: class_2350 = state.method_11654(class_2318.field_10927 as class_2769) as class_2350
      when (if (var10000 == null) -1 else TestFlapBlock.WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
         1 -> {
            val var11: class_265 = this.DOWN_AABB
            var11
         }
         2 -> {
            val var10: class_265 = this.NORTH_AABB
            var10
         }
         3 -> {
            val var9: class_265 = this.SOUTH_AABB
            var9
         }
         4 -> {
            val var8: class_265 = this.WEST_AABB
            var8
         }
         5 -> {
            val var7: class_265 = this.EAST_AABB
            var7
         }
         6 -> {
            val var6: class_265 = this.UP_AABB
            var6
         }
         else -> {
            val var5: class_265 = this.UP_AABB
            var5
         }
      }
   }

   public override fun getWing(level: class_1937?, pos: class_2338?, blockState: class_2680): Wing {
      val var10002: class_2382 = (blockState.method_11654(class_2318.field_10927 as class_2769) as class_2350).method_10163()
      return Wing(VectorConversionsMCKt.toJOMLD(var10002), 150.0, 30.0, null, 0.0)
   }

   fun method_53969(): MapCodec<TestFlapBlock> {
      CODEC
   }

   @JvmStatic
   fun {
      val var10000: MapCodec = class_2318.method_54094({ it: class_2251 ->
         TestFlapBlock()
      })
      CODEC = var10000
   }

   public companion object {
      public final val CODEC: MapCodec<TestFlapBlock>
   }

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class WhenMappings {
      @JvmStatic
      @JvmSynthetic
      public int[] $EnumSwitchMapping$0;

      @JvmStatic
      fun {
         val var0: IntArray = IntArray(class_2350.values().length)

         try {
            var0[class_2350.field_11033.ordinal()] = 1
         } catch (var7: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11043.ordinal()] = 2
         } catch (var6: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11035.ordinal()] = 3
         } catch (var5: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11039.ordinal()] = 4
         } catch (var4: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11034.ordinal()] = 5
         } catch (var3: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11036.ordinal()] = 6
         } catch (var2: NoSuchFieldError) {
         }

         $EnumSwitchMapping$0 = var0
      }
   }
}
