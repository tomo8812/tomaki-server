package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public interface bq {
   String a(aX var1, aI var2);

   default void a(Map<aX, String> var1, aI var2) {
      int var5 = 1;

      for (Iterator var4 = var1.entrySet().iterator(); var4.hasNext(); var5++) {
         Entry var3;
         (var3 = (Entry)var4.next()).setValue((String)var3.getValue() + "-" + var5);
      }
   }

   default String a(aX var1, String var2, aI var3) {
      return var2 + "-nullable";
   }
}
