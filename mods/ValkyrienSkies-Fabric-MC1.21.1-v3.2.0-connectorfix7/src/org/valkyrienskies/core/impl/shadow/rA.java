package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class rA extends rL<rv, qU> {
   private static final double c = 1.0E-10;

   public rA(rV<rv> var1, rY<qU> var2) {
      super(var1, var2);
   }

   private rA(rB var1, rB var2, double var3) {
      super(new rw(var1, var2, 1.0E-10), a(var1, var2, 1.0E-10));
   }

   @Deprecated
   private rA(rB var1, rB var2) {
      this(var1, var2, 1.0E-10);
   }

   private rA(rz var1) {
      rw var2;
      super(var2 = var1.i, a(var1.g, var1.h, var1.i.d));
   }

   private List<rz> f() {
      rw var1 = (rw)this.c();
      List var2 = ((qW)this.d()).b();
      ArrayList var3 = new ArrayList(var2.size());

      for (qV var4 : var2) {
         rB var5 = var1.f(new qZ(var4.a));
         rB var8 = var1.f(new qZ(var4.b));
         var3.add(new rz(var5, var8, var1));
      }

      return var3;
   }

   private rB a(rA var1, boolean var2) {
      rw var3 = (rw)this.c();
      rw var4 = (rw)var1.c();
      rB var5;
      if ((var5 = var3.a(var4)) == null) {
         return null;
      } else {
         int var7 = this.d().b(var3.e(var5));
         int var6 = var1.d().b(var4.e(var5));
         if (var2) {
            return var7 != rY.a.OUTSIDE$3ad793e5 && var6 != rY.a.OUTSIDE$3ad793e5 ? var5 : null;
         } else {
            return var7 == rY.a.INSIDE$3ad793e5 && var6 == rY.a.INSIDE$3ad793e5 ? var5 : null;
         }
      }
   }

   private static qW a(rB var0, rB var1, double var2) {
      rw var4 = new rw(var0, var1, var2);
      return new qW(var4.e(var0).d, var4.e(var1).d, var2);
   }

   @Override
   public final rL<rv, qU> a(rV<rv> var1, rY<qU> var2) {
      return new rA(var1, var2);
   }

   @Override
   public final sb.a<rv> a(rV<rv> var1) {
      rw var2 = (rw)this.c();
      var1 = var1;
      rB var3 = var2.a(var1);
      double var5 = var2.d;
      if (var3 == null) {
         var2 = var2;
         var1 = var1;
         double var16;
         if ((var16 = var1.c + (Ch.a(var1.a, var2.a, var1.b, var2.b) > 0.0 ? -var2.c : var2.c)) < -var5) {
            return new sb.a<>(null, this);
         } else {
            return var16 > var5 ? new sb.a<>(this, null) : new sb.a<>(null, null);
         }
      } else {
         boolean var7 = BY.n(var2.h() - var1.h()) < 0.0;
         qZ var8 = var2.e(var3);
         qY var10 = new qX(var8, !var7, var5).a();
         qY var14 = new qX(var8, var7, var5).a();
         rM var4 = this.d().a(false).a(var14);
         rM var11 = this.d().c(var4.b) ? new rM(Boolean.FALSE) : new rM<>(var10, new rM<>(Boolean.FALSE), var4.b, null);
         rM var15 = this.d().c(var4.c) ? new rM(Boolean.FALSE) : new rM<>(var14, new rM<>(Boolean.FALSE), var4.c, null);
         return new sb.a<>(new rA(var2.a(), new qW(var11, var5)), new rA(var2.a(), new qW(var15, var5)));
      }
   }
}
