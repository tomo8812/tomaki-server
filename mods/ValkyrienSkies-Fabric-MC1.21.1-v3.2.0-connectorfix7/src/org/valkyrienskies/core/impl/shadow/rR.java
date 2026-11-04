package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

final class rR<S extends qN, T extends qN> implements rN<S> {
   final qM<S> a;
   qM<S> b;
   rM<S> c;
   double d;

   rR(qM<S> var1) {
      this.a = var1;
      this.b = null;
      this.c = null;
      this.d = Double.POSITIVE_INFINITY;
   }

   @Override
   public final int a(rM<S> var1) {
      return var1.a.c().a(this.a) <= 0.0 ? rN.a.MINUS_SUB_PLUS$672d2271 : rN.a.PLUS_SUB_MINUS$672d2271;
   }

   @Override
   public final void b(rM<S> var1) {
      rV var2;
      double var3;
      if (BY.w(var3 = (var2 = var1.a.c()).a(this.a)) < this.d) {
         qM var5 = var2.b(this.a);
         rM var9 = var1;
         ArrayList var10 = new ArrayList(2);
         rO var13;
         a((var13 = (rO)var9.e).b, var10);
         a(var13.a, var10);
         ArrayList var14 = var10;
         boolean var6 = false;

         for (rY var8 : var14) {
            if (!var6) {
               rY var25 = var8;
               rV var21 = var2;
               qM var18 = var5;
               rU var22 = (rU)var21;
               if (var25.b(var22.d(var18)) != rY.a.OUTSIDE$3ad793e5) {
                  this.b = var5;
                  this.d = BY.w(var3);
                  var6 = true;
               }
            }
         }

         if (!var6) {
            for (rY var19 : var14) {
               rY var26 = var19;
               rV var23 = var2;
               qM var20 = var5;
               rU var24 = (rU)var23;
               double var11;
               rQ var15;
               qM var16;
               if ((var16 = (var15 = var26.a(var24.d(var20))).a == null ? null : var24.c(var15.a)) != null && (var11 = this.a.a(var16)) < this.d) {
                  this.b = var16;
                  this.d = var11;
               }
            }
         }
      }
   }

   @Override
   public final void c(rM<S> var1) {
      if (this.c == null) {
         this.c = var1;
      }
   }

   public final rQ<S> a() {
      rM var1 = this.c;
      this.d = BY.h(this.d, (Boolean)this.c.e ? -1.0 : 1.0);
      return new rQ<>(this.a, this.b, this.d);
   }

   private List<rY<T>> d(rM<S> var1) {
      ArrayList var2 = new ArrayList(2);
      rO var4;
      a((var4 = (rO)var1.e).b, var2);
      a(var4.a, var2);
      return var2;
   }

   private static void a(sb<S> var0, List<rY<T>> var1) {
      rY var3;
      if (var0 != null && (var3 = (var0 = var0).b) != null) {
         var1.add(var3);
      }
   }

   private static boolean a(qM<S> var0, rV<S> var1, rY<T> var2) {
      rU var3 = (rU)var1;
      return var2.b(var3.d(var0)) != rY.a.OUTSIDE$3ad793e5;
   }

   private static qM<S> b(qM<S> var0, rV<S> var1, rY<T> var2) {
      rU var4 = (rU)var1;
      rQ var3;
      return (var3 = var2.a(var4.d(var0))).a == null ? null : var4.c(var3.a);
   }
}
