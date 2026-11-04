package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.List;

public final class ro implements qS<rc, rs> {
   @Override
   public final qR<rc, rs> a(List<rs> var1) {
      if (var1.size() <= 0) {
         return new qR<>(rs.a, Double.NEGATIVE_INFINITY);
      } else {
         rs var2 = (rs)var1.get(0);
         if (var1.size() < 2) {
            return new qR<>(var2, 0.0, var2);
         } else {
            rs var3 = (rs)var1.get(1);
            if (var1.size() < 3) {
               rs var10002 = new rs(0.5, var2, 0.5, var3);
               rs var22 = var3;
               return new qR<>(var10002, 0.5 * var2.a((qM<rc>)var22), var2, var3);
            } else {
               rs var4 = (rs)var1.get(2);
               if (var1.size() < 4) {
                  ri var13 = new ri(var2, var3, var4, 1.0E-10 * (var2.d() + var3.d() + var4.d()));
                  ru var10000 = new ru();
                  rB[] var10001 = new rB[3];
                  qM var18 = var2;
                  var10001[0] = var13.e(var18);
                  var18 = var3;
                  var10001[1] = var13.e(var18);
                  var18 = var4;
                  var10001[2] = var13.e(var18);
                  qR var16 = var10000.a(Arrays.asList(var10001));
                  var18 = (qO)var16.a;
                  return new qR<>(var13.f(var18), var16.b, var2, var3, var4);
               } else {
                  rs var12 = (rs)var1.get(3);
                  qd[] var5 = new qd[]{new qd(var2.i), new qd(var3.i), new qd(var4.i), new qd(var12.i)};
                  qd[] var6 = new qd[]{new qd(var2.j), new qd(var3.j), new qd(var4.j), new qd(var12.j)};
                  qd[] var7 = new qd[]{new qd(var2.k), new qd(var3.k), new qd(var4.k), new qd(var12.k)};
                  qd[] var8 = new qd[]{
                     var5[0].d(var5[0]).a(var6[0].d(var6[0])).a(var7[0].d(var7[0])),
                     var5[1].d(var5[1]).a(var6[1].d(var6[1])).a(var7[1].d(var7[1])),
                     var5[2].d(var5[2]).a(var6[2].d(var6[2])).a(var7[2].d(var7[2])),
                     var5[3].d(var5[3]).a(var6[3].d(var6[3])).a(var7[3].d(var7[3]))
                  };
                  qd var9 = a(var5, var6, var7).c(2);
                  qd var10 = a(var8, var6, var7);
                  qd var11 = a(var8, var5, var7);
                  qd var24 = a(var8, var5, var6);
                  var10 = var10.c(var9);
                  var11 = var11.c(var9).f();
                  qd var25 = var24.c(var9);
                  qd var14 = var5[0].e(var10);
                  qd var17 = var6[0].e(var11);
                  qd var23 = var7[0].e(var25);
                  qd var15 = var14.d(var14).a(var17.d(var17)).a(var23.d(var23));
                  return new qR<>(new rs(var10.doubleValue(), var11.doubleValue(), var25.doubleValue()), BY.a(var15.doubleValue()), var2, var3, var4, var12);
               }
            }
         }
      }
   }

   private static qd a(qd[] var0, qd[] var1, qd[] var2) {
      return var1[0]
         .d(var2[1])
         .d(var0[2].e(var0[3]))
         .a(var1[0].d(var2[2]).d(var0[3].e(var0[1])))
         .a(var1[0].d(var2[3]).d(var0[1].e(var0[2])))
         .a(var1[1].d(var2[0]).d(var0[3].e(var0[2])))
         .a(var1[1].d(var2[2]).d(var0[0].e(var0[3])))
         .a(var1[1].d(var2[3]).d(var0[2].e(var0[0])))
         .a(var1[2].d(var2[0]).d(var0[1].e(var0[3])))
         .a(var1[2].d(var2[1]).d(var0[3].e(var0[0])))
         .a(var1[2].d(var2[3]).d(var0[0].e(var0[1])))
         .a(var1[3].d(var2[0]).d(var0[2].e(var0[1])))
         .a(var1[3].d(var2[1]).d(var0[0].e(var0[2])))
         .a(var1[3].d(var2[2]).d(var0[1].e(var0[0])));
   }
}
