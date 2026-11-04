package org.valkyrienskies.core.impl.shadow

public object Is {
   @JvmStatic
   fun a(var0: Ip, var1: (Int?, Int?, Int?, Int?) -> Unit) {
      if (var0 is In) {
         val var12: In = var0 as In
         var var17: Int = 0

         while (var17 < 16) {
            val var19: Int = var17++
            var var20: Int = 0

            while (var20 < 16) {
               val var14: Int = var20++
               var var22: Int = 0

               while (var22 < 16) {
                  val var15: Int = var22++
                  var1(var15, var19, var14, var12.a(var15, var19, var14))
               }
            }
         }
      } else {
         if (var0 is Ir) {
            val var11: Ir = var0 as Ir
            var var13: Int = 0
            val var3: Int = var11.e.size()

            while (var13 < var3) {
               val var4: Int = var13++
               val var5: Short = var11.e.getShort(var4)
               var1(var5 shr 8 and 15, var5 shr 4 and 15, var5 and 15, var11.f.getInt(var4))
            }
         }
      }
   }
}
