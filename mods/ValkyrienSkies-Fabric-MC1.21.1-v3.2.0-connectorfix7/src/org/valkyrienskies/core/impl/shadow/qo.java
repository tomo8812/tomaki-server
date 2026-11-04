package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;

public final class qo implements qA {
   @Override
   public final qm a(qp var1) {
      if (!(var1 instanceof qn)) {
         throw new oV(px.INVALID_BINARY_CHROMOSOME);
      }

      var1 = var1;
      ArrayList var2 = new ArrayList(var1.a);
      int var3 = qx.a().nextInt(var1.b());
      var2.set(var3, var1.a.get(var3) == 0 ? 1 : 0);
      return var1.c();
   }
}
