package org.valkyrienskies.core.impl.shadow;

import java.util.Map;
import kotlin.jvm.functions.Function0;

public final class CI {
   private static final <K, V> void a(Map<K, V> var0, K var1, V var2, Function0<String> var3) {
      if (var0.putIfAbsent(var1, var2) != null) {
         Object var4 = var3.invoke()
         throw new IllegalArgumentException(var4.toString())
      }
   }

   // $VF: synthetic method
   private static void a(Map var0, final Object var1, Object var2, Function0 var3, int var4) {
      if ((var4 & 4) != 0) {
         var3 = (Function0)(new Function0<String>() {
            public final String a() {
               return var1 + " was already in the map"
            }

            // $VF: synthetic method
            public final Object invoke() {
               return this.a()
            }
         })
      }

      if (var0.putIfAbsent(var1, var2) != null) {
         Object var5 = var3.invoke()
         throw new IllegalArgumentException(var5.toString())
      }
   }
}
