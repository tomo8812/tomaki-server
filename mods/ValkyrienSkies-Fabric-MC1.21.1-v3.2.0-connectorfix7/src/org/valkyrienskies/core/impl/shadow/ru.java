package org.valkyrienskies.core.impl.shadow;

import java.util.List;

public final class ru implements qS<rv, rB> {
   @Override
   public final qR<rv, rB> a(List<rB> var1) {
      if (var1.size() <= 0) {
         return new qR<>(rB.a, Double.NEGATIVE_INFINITY);
      } else {
         rB var2 = (rB)var1.get(0);
         if (var1.size() < 2) {
            return new qR<>(var2, 0.0, var2);
         } else {
            rB var3 = (rB)var1.get(1);
            if (var1.size() < 3) {
               rB var10002 = new rB(0.5, var2, 0.5, var3);
               rB var10 = var3;
               return new qR<>(var10002, 0.5 * var2.a(var10), var2, var3);
            } else {
               rB var9 = (rB)var1.get(2);
               qd[] var4 = new qd[]{new qd(var2.c), new qd(var3.c), new qd(var9.c)};
               qd[] var5 = new qd[]{new qd(var2.d), new qd(var3.d), new qd(var9.d)};
               qd[] var6 = new qd[]{
                  var4[0].d(var4[0]).a(var5[0].d(var5[0])), var4[1].d(var4[1]).a(var5[1].d(var5[1])), var4[2].d(var4[2]).a(var5[2].d(var5[2]))
               };
               qd var7 = a(var4, var5).c(2);
               qd var8 = a(var6, var5);
               qd var14 = a(var6, var4);
               var8 = var8.c(var7);
               qd var15 = var14.c(var7).f();
               qd var11 = var4[0].e(var8);
               qd var13 = var5[0].e(var15);
               qd var12 = var11.d(var11).a(var13.d(var13));
               return new qR<>(new rB(var8.doubleValue(), var15.doubleValue()), BY.a(var12.doubleValue()), var2, var3, var9);
            }
         }
      }
   }

   private static qd a(qd[] var0, qd[] var1) {
      return var1[0].d(var0[2].e(var0[1])).a(var1[1].d(var0[0].e(var0[2]))).a(var1[2].d(var0[1].e(var0[0])));
   }
}
