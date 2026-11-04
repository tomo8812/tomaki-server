package org.valkyrienskies.core.impl.shadow

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.util.Map.Entry
import java.util.concurrent.locks.Lock
import javax.inject.Inject
import javax.inject.Provider

@dY
public class Cy @Inject  public constructor(p0: Provider<CA>) {
   private final val a: Provider<CA>

   init {
      this.a = var1
   }

   public fun a(): CA {
      val var10000: Any = this.a.get()
      return var10000 as CA
   }

   public fun a(p0: JsonNode): CA {
      if (var1 !is ObjectNode) {
         throw IllegalArgumentException(("AttachmentHolderFactory: Tried to create AttachmentHolder from non-object JsonNode $var1").toString())
      } else {
         val var2: CA
         val var10000: CA = var2 = this.a.get()
         val var3: ObjectNode = var1 as ObjectNode
         val var10: CA = var10000
         val var4: Lock
         (var4 = var10000.b).lock()

         try {
            var10.d.clear()
            var10.c = var3
            var10.e.clear()
            val var19: java.util.Iterator = var3.fields()
            val var12: java.util.Iterator = var19

            while (var12.hasNext()) {
               val var5: Entry
               val var6: java.lang.String = var5.getKey() as java.lang.String
               val var13: JsonNode = var5.getValue() as JsonNode
               val var20: CH = var10.a
               val var14: CE
               if ((var14 = var20.a(var6, var13)) is CE.d) {
                  if (!var10.a(var6, (var14 as CE.d).a)) {
                  }
               } else if (var14 is CE.f) {
                  if (var10.a(var6, (var14 as CE.f).a)) {
                     var10.d.add(var6)
                  }
               } else if (var14 !is CE.c) {
                  if (var14 is CE.a) {
                     var10.f.put(var6, (var14 as CE.a).a)
                  } else if (var14 is CE.b) {
                     var10.f.put((var14 as CE.b).b, (var14 as CE.b).a)
                  } else if (var14 is CE.e) {
                     var10.d.add(var6)
                  }
               }
            }
         } finally {
            var4.unlock()
         }

         return var2
      }
   }
}
