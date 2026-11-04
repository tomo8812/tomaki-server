package org.valkyrienskies.mod.compat

import net.minecraft.class_1297
import net.minecraft.class_2382
import net.minecraft.class_243
import net.minecraft.class_2350.class_2351

public object CreateCompat {
   private final val contraptionClass: Class<*>?

   @JvmStatic
   public fun isContraption(entity: class_1297): Boolean {
      return contraptionClass != null && contraptionClass.isInstance(entity)
   }

   @JvmStatic
   public fun getCenterOf(pos: class_2382): class_243 {
      if (pos == class_2382.field_11176) {
         return class_243(0.5, 0.5, 0.5)
      } else {
         val var10000: class_243 = class_243.method_24954(pos).method_1031(0.5, 0.5, 0.5)
         return var10000
      }
   }

   @JvmStatic
   public fun rotate(vec: class_243, deg: Double, axis: class_2351): class_243 {
      if (deg == 0.0) {
         return vec
      } else if (vec == class_243.field_1353) {
         return vec
      } else {
         val sin: Double = Math.sin(deg / (double)180.0F * Math.PI)
         val cos: Double = Math.cos(deg / (double)180.0F * Math.PI)
         if (axis === class_2351.field_11048) {
            return class_243(vec.field_1352, vec.field_1351 * cos - vec.field_1350 * sin, vec.field_1350 * cos + vec.field_1351 * sin)
         } else if (axis === class_2351.field_11052) {
            return class_243(vec.field_1352 * cos + vec.field_1350 * sin, vec.field_1351, vec.field_1350 * cos - vec.field_1352 * sin)
         } else {
            return if (axis === class_2351.field_11051)
               class_243(vec.field_1352 * cos - vec.field_1351 * sin, vec.field_1351 * cos + vec.field_1352 * sin, vec.field_1350)
               else
               vec
            }
      }
   }

   @JvmStatic
   public fun lerp(p: Float, from: class_243, to: class_243): class_243 {
      val var10000: class_243 = from.method_1019(to.method_1020(from).method_1021((double)p))
      return var10000
   }

   @JvmStatic
   fun {
      val var0: CreateCompat = INSTANCE

      var `$this$contraptionClass_u24lambda_u240`: Any
      try {
         `$this$contraptionClass_u24lambda_u240` = Result.constructor_impl/* $VF was: constructor-impl */(
            Class.forName("com.simibubi.create.content.contraptions.AbstractContraptionEntity")
         )
      } catch (var3: java.lang.Throwable) {
         `$this$contraptionClass_u24lambda_u240` = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var3))
      }

      contraptionClass = (
         if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$contraptionClass_u24lambda_u240`)) null else `$this$contraptionClass_u24lambda_u240`
      ) as Class<*>
   }
}
