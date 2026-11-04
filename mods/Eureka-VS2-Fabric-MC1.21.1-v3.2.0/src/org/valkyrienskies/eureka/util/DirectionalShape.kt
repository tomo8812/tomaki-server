package org.valkyrienskies.eureka.util

import net.minecraft.class_2350
import net.minecraft.class_265

public class DirectionalShape(shape: RotShape) {
   public final val north: class_265
   public final val east: class_265
   public final val south: class_265
   public final val west: class_265

   init {
      this.north = shape.build()
      this.east = shape.rotate90().build()
      this.south = shape.rotate180().build()
      this.west = shape.rotate270().build()
   }

   public operator fun get(direction: class_2350): class_265 {
      var var10000: class_265
      when (DirectionalShape.WhenMappings.$EnumSwitchMapping$0[direction.ordinal()]) {
         1 -> var10000 = this.north
         2 -> var10000 = this.east
         3 -> var10000 = this.south
         4 -> var10000 = this.west
         else -> throw IllegalArgumentException()
      }

      return var10000
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
            var0[class_2350.field_11043.ordinal()] = 1
         } catch (var5: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11034.ordinal()] = 2
         } catch (var4: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11035.ordinal()] = 3
         } catch (var3: NoSuchFieldError) {
         }

         try {
            var0[class_2350.field_11039.ordinal()] = 4
         } catch (var2: NoSuchFieldError) {
         }

         $EnumSwitchMapping$0 = var0
      }
   }
}
