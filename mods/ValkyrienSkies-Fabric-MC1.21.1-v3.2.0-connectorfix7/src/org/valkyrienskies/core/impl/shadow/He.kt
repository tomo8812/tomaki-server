package org.valkyrienskies.core.impl.shadow

import it.unimi.dsi.fastutil.ints.IntList
import org.joml.Vector3dc
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic

public interface He {
   @JvmStatic
   He.a a = He.a.a;

   fun a(var1: Vector3dc, var2: Double): MutableList<He.c>

   fun h()

   fun <T extends GW> a(var1: T): Hc<T>

   fun a(var1: Int): Boolean

   fun b()

   fun g(): Boolean

   fun a(var1: HM, var2: Boolean): Boolean

   fun a(var1: HM): Boolean

   fun b(var1: Int): Boolean

   fun d(): MutableList<HM>

   fun e(): MutableList<HM>

   fun f(): MutableList<HM>

   fun a(var1: Int, var2: Int)

   fun b(var1: Int, var2: Int)

   fun a(var1: Vector3dc, var2: Vector3dc, var3: Double, vararg var5: Int): Hh

   fun b(var1: Vector3dc, var2: Vector3dc, var3: Double, vararg var5: Int): Hi

   fun a(var1: Int)

   fun c(): MutableList<HW>

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      @JvmSynthetic
      fun `a$default`(var0: He, var1: HM, var2: Boolean, var3: Int, var4: Any): Boolean {
         if (var4 != null) {
            throw UnsupportedOperationException();
         } else {
            if ((var3 and 2) != 0) {
               var2 = false;
            }

            var0.a(var1, var2);
         }
      }
   }

   // $VF: Class flags could not be determined
   internal class a {
      @JvmStatic
      @JvmSynthetic
      He.a a = He.a();
      @JvmStatic
      private AABBic b = AABBi(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

      fun a() {
      }

      @JvmStatic
      fun a(): AABBic {
         b;
      }
   }

   // $VF: Class flags could not be determined
   internal final interface c {
      fun a(): IntList

      fun a(var1: Vector3dc, var2: Double)

      fun b(var1: Vector3dc, var2: Double)

      fun b()
   }
}
