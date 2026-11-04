package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class qH implements qA {
   @Override
   public final qm a(qp var1) {
      if (!(var1 instanceof qG)) {
         throw new oV(px.RANDOMKEY_MUTATION_WRONG_CLASS, var1.getClass().getSimpleName());
      }

      qG var4;
      List var2 = (var4 = (qG)var1).a;
      int var3 = qx.a().nextInt(var2.size());
      new ArrayList<>(var2).set(var3, qx.a().nextDouble());
      return var4.c();
   }
}
