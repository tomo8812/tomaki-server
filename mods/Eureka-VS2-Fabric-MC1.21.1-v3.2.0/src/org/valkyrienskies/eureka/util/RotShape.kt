package org.valkyrienskies.eureka.util

import net.minecraft.class_265

public interface RotShape {
   public abstract fun rotate90(): RotShape {
   }

   public open fun rotate180(): RotShape {
   }

   public open fun rotate270(): RotShape {
   }

   public abstract fun makeMcShape(): class_265 {
   }

   public open fun build(): class_265 {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun rotate180(`$this`: RotShape): RotShape {
         `$this`.rotate90().rotate90()
      }

      @JvmStatic
      fun rotate270(`$this`: RotShape): RotShape {
         `$this`.rotate180().rotate90()
      }

      @JvmStatic
      fun build(`$this`: RotShape): class_265 {
         val var10000: class_265 = `$this`.makeMcShape().method_1097()
         var10000
      }
   }
}
