package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.List;

public final class tT<T extends tL> extends tS<T> {
   public tT(tW var1) {
      super(var1);
   }

   @Override
   public final double a(List<? extends tK<T>> var1) {
      double var2 = 0.0;
      Iterator var8 = var1.iterator();

      while (var8.hasNext()) {
         tK var4;
         if (!(var4 = (tK)var8.next()).b.isEmpty()) {
            tL var5 = a(var4);
            AM var6 = new AM();

            for (tL var7 : var4.b) {
               var6.a(this.a(var7, var5));
            }

            var2 += var6.c();
         }
      }

      return var2;
   }
}
