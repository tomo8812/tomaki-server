package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

final class so implements rN<sq> {
   private final double d;
   double a;
   rs b;
   final List<rs> c;

   so(double var1) {
      this.d = var1;
      this.a = 0.0;
      this.b = rs.a;
      this.c = new ArrayList<>();
   }

   @Override
   public final int a(rM<sq> var1) {
      return rN.a.MINUS_SUB_PLUS$672d2271;
   }

   @Override
   public final void b(rM<sq> var1) {
   }

   @Override
   public final void c(rM<sq> var1) {
      if ((Boolean)var1.e) {
         List var17;
         if ((var17 = new sr(var1.a(Boolean.TRUE, Boolean.FALSE, null), this.d).b()).size() != 1) {
            throw new oY();
         }

         st var2 = (st)var17.get(0);
         int var3 = 0;
         double var9 = 0.0;

         for (sm var6 = var2.c; var3 == 0 || var6.a != var2; var6 = var6.b.c) {
            sl var7 = var6.d;
            rs var4 = var6.d.a;
            st var22 = var6.b;
            sm var23 = var6.b.c;
            var7 = var6.b.c.d;
            rs var5 = var6.b.c.d.a;
            st var25 = var6.b;
            sp var26 = var6.b.a;
            rs var27 = var6.b.a.c;
            double var15;
            if ((var15 = BY.b(rs.b(var5, rs.c(var27, var4)), -rs.b(var5, var4))) < 0.0) {
               var15 += Math.PI * 2;
            }

            var9 += var15;
            var3++;
            st var28 = var6.b;
         }

         double var21 = var9 - (var3 - 2) * Math.PI;
         var2 = (st)var17.get(0);
         var3 = 0;
         rs var31 = rs.a;

         for (sm var10 = var2.c; var3 == 0 || var10.a != var2; var10 = var10.b.c) {
            sl var29 = var10.d;
            var31 = new rs(1.0, var31, var10.c, var10.d.a);
            var3++;
            st var30 = var10.b;
         }

         rs var18 = var31.o();
         this.c.add(var18);
         this.a += var21;
         this.b = new rs(1.0, this.b, var21, var18);
      }
   }

   private static double a(st var0) {
      int var1 = 0;
      double var2 = 0.0;

      for (sm var4 = var0.c; var1 == 0 || var4.a != var0; var4 = var4.b.c) {
         sl var7 = var4.d;
         rs var5 = var4.d.a;
         st var10 = var4.b;
         sm var11 = var4.b.c;
         var7 = var4.b.c.d;
         rs var6 = var4.b.c.d.a;
         st var13 = var4.b;
         sp var14 = var4.b.a;
         rs var15 = var4.b.a.c;
         double var8;
         if ((var8 = BY.b(rs.b(var6, rs.c(var15, var5)), -rs.b(var6, var5))) < 0.0) {
            var8 += Math.PI * 2;
         }

         var2 += var8;
         var1++;
         st var16 = var4.b;
      }

      return var2 - (var1 - 2) * Math.PI;
   }

   private static rs b(st var0) {
      int var1 = 0;
      rs var2 = rs.a;

      for (sm var3 = var0.c; var1 == 0 || var3.a != var0; var3 = var3.b.c) {
         sl var4 = var3.d;
         var2 = new rs(1.0, var2, var3.c, var3.d.a);
         var1++;
         st var5 = var3.b;
      }

      return var2.o();
   }

   public final double a() {
      return this.a;
   }

   public final sp b() {
      return this.b.f() == 0.0 ? sp.b : new sp(this.b);
   }

   public final List<rs> c() {
      return this.c;
   }
}
