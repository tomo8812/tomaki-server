package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

public final class jY extends Properties {
   private static final long a = 1L;

   @Override
   public final synchronized Enumeration<Object> keys() {
      Set var1 = this.keySet();
      ArrayList var2 = new ArrayList(var1.size());

      for (Object var3 : var1) {
         var2.add(var3.toString());
      }

      Collections.sort(var2);
      return new ig<>(var2.iterator());
   }
}
