package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class nm {
   private static final List<qd> a;
   private static final List<qd> b;
   private static final List<qd> c;
   private static final List<qd> d;
   private static final Map<nm.a, List<qd>> e = new HashMap<>();

   private nm() {
   }

   private static ni a(int var0) {
      return a(var0, a, new nm.b() {
         private final qd[] a = new qd[]{qd.c, qd.a, qd.b};

         @Override
         public final qd[] a(int var1) {
            return this.a;
         }
      });
   }

   private static ni b(int var0) {
      return a(var0, b, new nm.b() {
         @Override
         public final qd[] a(int var1) {
            return new qd[]{qd.c, qd.a, new qd(2 * var1)};
         }
      });
   }

   private static ni c(int var0) {
      return a(var0, c, new nm.b() {
         @Override
         public final qd[] a(int var1) {
            int var2 = var1 + 1;
            return new qd[]{new qd(2 * var1 + 1, var2), new qd(-1, var2), new qd(var1, var2)};
         }
      });
   }

   private static ni d(int var0) {
      return a(var0, d, new nm.b() {
         @Override
         public final qd[] a(int var1) {
            int var2 = var1 + 1;
            return new qd[]{qd.c, new qd(var1 + var2, var2), new qd(var1, var2)};
         }
      });
   }

   private static ni a(int var0, final int var1, final int var2) {
      nm.a var3 = new nm.a(var1, var2);
      if (!e.containsKey(var3)) {
         ArrayList var4 = new ArrayList();
         e.put(var3, var4);
         var4.add(qd.b);
         var4.add(new qd(var1 - var2, 2));
         var4.add(new qd(var1 + 2 + var2, 2));
      }

      return a(
         var0,
         e.get(var3),
         new nm.b() {
            @Override
            public final qd[] a(int var1x) {
               var1x++;
               int var2x;
               int var3x;
               int var4 = (var3x = (var2x = var1x + var1 + var2) + var1x) - 1;
               int var5 = var3x - 2;
               var2x = 2 * var1x * var2x * var5;
               return new qd[]{
                  new qd(var4 * (var1 * var1 - var2 * var2), var2x),
                  new qd(var4 * var3x * var5, var2x),
                  new qd(2 * (var1x + var1 - 1) * (var1x + var2 - 1) * var3x, var2x)
               };
            }
         }
      );
   }

   private static double[] a(double[] var0, double var1) {
      int var3;
      double[] var4 = new double[var3 = var0.length];
      int[][] var5 = new int[var3][var3];

      for (int var6 = 0; var6 < var3; var6++) {
         for (int var7 = 0; var7 <= var6; var7++) {
            var5[var6][var7] = (int)BR.a(var6, var7);
         }
      }

      for (int var9 = 0; var9 < var3; var9++) {
         var4[0] += var0[var9] * BY.a(var1, var9);
      }

      int var10 = var3 - 1;

      for (int var11 = 0; var11 < var10; var11++) {
         for (int var8 = var11; var8 < var10; var8++) {
            var4[var11 + 1] = var4[var11 + 1] + var5[var8 + 1][var8 - var11] * var0[var8 + 1] * BY.a(var1, var8 - var11);
         }
      }

      return var4;
   }

   private static ni a(int var0, List<qd> var1, nm.b var2) {
      synchronized (var1) {
         int var4 = (int)BY.y(BY.a((double)(2 * var1.size()))) - 1;
         if (var0 > var4) {
            List var6 = var1;
            nm.b var5 = var2;
            var4 = var4;
            int var15 = var0;
            int var7 = (var4 - 1) * var4 / 2;

            for (int var18 = var4; var18 < var15; var18++) {
               int var8 = var7;
               var7 += var18;
               qd[] var9 = var5.a(var18);
               qd var10 = (qd)var6.get(var7);
               qd var11 = (qd)var6.get(var8);
               var6.add(var10.d(var9[0]).e(var11.d(var9[2])));

               for (int var12 = 1; var12 < var18; var12++) {
                  qd var13 = var10;
                  var10 = (qd)var6.get(var7 + var12);
                  var11 = (qd)var6.get(var8 + var12);
                  var6.add(var10.d(var9[0]).a(var13.d(var9[1])).e(var11.d(var9[2])));
               }

               qd var22 = var10;
               var10 = (qd)var6.get(var7 + var18);
               var6.add(var10.d(var9[0]).a(var22.d(var9[1])));
               var6.add(var10.d(var9[1]));
            }
         }
      }

      int var3 = var0 * (var0 + 1) / 2;
      double[] var19 = new double[var0 + 1];

      for (int var16 = 0; var16 <= var0; var16++) {
         var19[var16] = ((qd)var1.get(var3 + var16)).doubleValue();
      }

      return new ni(var19);
   }

   private static void a(int var0, int var1, nm.b var2, List<qd> var3) {
      int var4 = (var1 - 1) * var1 / 2;

      for (int var11 = var1; var11 < var0; var11++) {
         int var5 = var4;
         var4 += var11;
         qd[] var6 = var2.a(var11);
         qd var7 = (qd)var3.get(var4);
         qd var8 = (qd)var3.get(var5);
         var3.add(var7.d(var6[0]).e(var8.d(var6[2])));

         for (int var9 = 1; var9 < var11; var9++) {
            qd var10 = var7;
            var7 = (qd)var3.get(var4 + var9);
            var8 = (qd)var3.get(var5 + var9);
            var3.add(var7.d(var6[0]).a(var10.d(var6[1])).e(var8.d(var6[2])));
         }

         qd var14 = var7;
         var7 = (qd)var3.get(var4 + var11);
         var3.add(var7.d(var6[0]).a(var14.d(var6[1])));
         var3.add(var7.d(var6[1]));
      }
   }

   static {
      (a = new ArrayList<>()).add(qd.b);
      a.add(qd.c);
      a.add(qd.b);
      (b = new ArrayList<>()).add(qd.b);
      b.add(qd.c);
      b.add(qd.a);
      (c = new ArrayList<>()).add(qd.b);
      c.add(qd.b);
      c.add(qd.d);
      (d = new ArrayList<>()).add(qd.b);
      d.add(qd.c);
      d.add(qd.b);
   }

   static class a {
      private final int a;
      private final int b;

      a(int var1, int var2) {
         this.a = var1;
         this.b = var2;
      }

      @Override
      public final int hashCode() {
         return this.a << 16 ^ this.b;
      }

      @Override
      public final boolean equals(Object key) {
         if (key != null && key instanceof nm.a) {
            key = (nm.a)key;
            return this.a == ((nm.a)key).a && this.b == ((nm.a)key).b;
         } else {
            return false;
         }
      }
   }

   interface b {
      qd[] a(int var1);
   }
}
