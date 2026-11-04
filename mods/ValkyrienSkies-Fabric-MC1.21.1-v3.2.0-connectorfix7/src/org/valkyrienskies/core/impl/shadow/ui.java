package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public final class ui implements Runnable {
   private final ud a;
   private final Iterator<double[]> b;
   private final uj c;

   private ui(ud var1, Iterator<double[]> var2, uj var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   @Override
   public final void run() {
      while (this.b.hasNext()) {
         uj var10000 = this.c;
         ud var10001 = this.a;
         double[] var3 = this.b.next();
         ud var2 = var10001;
         uj var1 = var10000;
         long var4 = var10000.d.incrementAndGet() - 1L;
         double var6 = var1.b.a(var4);
         double var19 = var6;
         double[] var12 = var3;
         ud var11 = var2;
         uj var10 = var1;

         ue var8;
         do {
            var8 = uc.a(var12, (Iterable<ue>)var11, var10.a);
         } while (!var10.a(var8, var12, var19));

         var8 = var8;
         int var21 = var1.c.a(var4);
         lL var5 = new lL(var6, 0.0, var21);
         if (var21 > 0) {
            Collection var22;
            (var22 = new HashSet()).add(var8);
            HashSet var7;
            (var7 = new HashSet()).add(var8);
            int var24 = 1;

            while (true) {
               for (ue var25 : var22 = var2.a(var22, var7)) {
                  var19 = var5.a(var24);
                  var12 = var3;
                  ue var27 = var25;
                  var10 = var1;

                  while (!var10.a(var27, var12, var19)) {
                  }
               }

               var7.addAll(var22);
               if (++var24 > var21) {
                  break;
               }
            }
         }
      }
   }
}
