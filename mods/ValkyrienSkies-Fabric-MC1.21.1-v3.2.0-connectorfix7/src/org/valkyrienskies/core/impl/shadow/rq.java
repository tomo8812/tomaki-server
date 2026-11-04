package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class rq {
   private static final double a = 1.0E-10;
   private final rf b;
   private final qW c;

   public rq(rf var1, qW var2) {
      this.b = var1;
      this.c = var2;
   }

   private rq(rs var1, rs var2, double var3) {
      this(new rf(var1, var2, 1.0E-10), a(var1, var2, 1.0E-10));
   }

   private rq(rs var1, rs var2) {
      this(var1, var2, 1.0E-10);
   }

   private rq(rn var1) {
      rf var2;
      this(var2 = var1.c, a(var1.a, var1.b, var1.c.c));
   }

   private List<rn> a() {
      List var1 = this.c.b();
      ArrayList var2 = new ArrayList(var1.size());

      for (qV var3 : var1) {
         rs var4 = this.b.b(new qZ(var3.a));
         rs var7 = this.b.b(new qZ(var3.b));
         var2.add(new rn(var4, var7, this.b));
      }

      return var2;
   }

   private rs a(rq var1, boolean var2) {
      rf var4 = var1.b;
      rf var3 = this.b;
      rf var5 = var4;
      var3 = this.b;
      double var11 = this.b.a.f(var5.a);
      double var13;
      rs var10000;
      if ((var13 = 1.0 - var11 * var11) < Cq.a) {
         var10000 = var3.b;
      } else {
         rs var6;
         double var16 = (var6 = var5.b.h(var3.b)).f(var3.a);
         double var18 = var6.f(var5.a);
         var10000 = new rs(1.0, var3.b, (var16 - var18 * var11) / var13, var3.a);
      }

      rs var22 = var10000;
      rs var23;
      if ((var23 = var4.b(var22) ? var22 : null) == null) {
         return null;
      } else {
         int var24 = this.c.b(this.b.a((qM<rc>)var23));
         int var20 = var1.c.b(var1.b.a((qM<rc>)var23));
         if (var2) {
            return var24 != rY.a.OUTSIDE$3ad793e5 && var20 != rY.a.OUTSIDE$3ad793e5 ? var23 : null;
         } else {
            return var24 == rY.a.INSIDE$3ad793e5 && var20 == rY.a.INSIDE$3ad793e5 ? var23 : null;
         }
      }
   }

   private static qW a(rs var0, rs var1, double var2) {
      rf var4 = new rf(var0, var1, var2);
      return new qW(var4.a((qM<rc>)var0).d, var4.a((qM<rc>)var1).d, var2);
   }
}
