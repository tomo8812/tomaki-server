package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;

public final class uj implements ug {
   final tW a;
   final uk b;
   final um c;
   final AtomicLong d = new AtomicLong(0L);

   private uj(tW var1, uk var2, um var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   @Override
   public final void a(ud var1, double[] var2) {
      long var3 = this.d.incrementAndGet() - 1L;
      double var5 = this.b.a(var3);
      ue var7 = this.a(var1, var2, var5);
      int var10 = this.c.a(var3);
      lL var4 = new lL(var5, 0.0, var10);
      if (var10 > 0) {
         Collection var11;
         (var11 = new HashSet()).add(var7);
         HashSet var6;
         (var6 = new HashSet()).add(var7);
         int var12 = 1;

         do {
            for (ue var9 : var11 = var1.a(var11, var6)) {
               this.b(var9, var2, var4.a(var12));
            }

            var6.addAll(var11);
         } while (++var12 <= var10);
      }
   }

   private long a() {
      return this.d.get();
   }

   boolean a(ue var1, double[] var2, double var3) {
      double[] var5;
      var2 = a(var5 = var1.c(), var2, var3);
      return var1.a(var5, var2);
   }

   private void b(ue var1, double[] var2, double var3) {
      while (!this.a(var1, var2, var3)) {
      }
   }

   private ue a(ud var1, double[] var2, double var3) {
      ue var5;
      do {
         var5 = uc.a(var2, (Iterable<ue>)var1, this.a);
      } while (!this.a(var5, var2, var3));

      return var5;
   }

   private static double[] a(double[] var0, double[] var1, double var2) {
      sA var4 = new sA(var0, false);
      return new sA(var1, false).a(var4).c(var2).l(var4).g();
   }
}
