package org.valkyrienskies.core.impl.shadow;

public final class rr extends rL<rc, rv> {
   public rr(rV<rc> var1, rY<rv> var2) {
      super(var1, var2);
   }

   @Override
   public final rL<rc, rv> a(rV<rc> var1, rY<rv> var2) {
      return new rr(var1, var2);
   }

   @Override
   public final sb.a<rc> a(rV<rc> var1) {
      var1 = var1;
      ri var2 = (ri)this.c();
      ri var4 = var2;
      ri var3 = var1;
      rs var5;
      rf var10000;
      if ((var5 = rs.c(var1.e, var4.e)).e() < var3.f) {
         var10000 = null;
      } else {
         ri var7 = new ri(var5, var3.f);
         ri var6 = var4;
         var4 = var3;
         rs var9 = var3.e;
         double var22 = var3.e.i;
         var9 = var4.e;
         double var24 = var4.e.j;
         var9 = var4.e;
         double var26 = var4.e.k;
         double var28 = var4.a;
         var9 = var6.e;
         double var30 = var6.e.i;
         var9 = var6.e;
         double var32 = var6.e.j;
         var9 = var6.e;
         double var34 = var6.e.k;
         double var36 = var6.a;
         var9 = var7.e;
         double var38 = var7.e.i;
         var9 = var7.e;
         double var40 = var7.e.j;
         var9 = var7.e;
         double var42 = var7.e.k;
         double var44 = var7.a;
         double var46 = var32 * var42 - var40 * var34;
         double var48 = var34 * var38 - var42 * var30;
         double var50 = var30 * var40 - var38 * var32;
         double var52;
         rs var79;
         if (BY.w(var52 = var22 * var46 + var24 * var48 + var26 * var50) < 1.0E-10) {
            var79 = null;
         } else {
            double var54 = 1.0 / var52;
            var79 = new rs(
               (-var46 * var28 - (var26 * var40 - var42 * var24) * var36 - (var34 * var24 - var26 * var32) * var44) * var54,
               (-var48 * var28 - (var42 * var22 - var26 * var38) * var36 - (var26 * var30 - var34 * var22) * var44) * var54,
               (-var50 * var28 - (var24 * var38 - var40 * var22) * var36 - (var32 * var22 - var24 * var30) * var44) * var54
            );
         }

         rs var65 = var79;
         var10000 = new rf(var65, var65.g(var5), var3.f);
      }

      rf var60 = var10000;
      double var68 = var2.f;
      if (var60 == null) {
         var4 = var2;
         var3 = var1;
         double var70;
         if ((var70 = var1.a + (var3.a((rV<rc>)var4) ? -var4.a : var4.a)) < -var68) {
            return new sb.a<>(null, this);
         } else {
            return var70 > var68 ? new sb.a<>(this, null) : new sb.a<>(null, null);
         }
      } else {
         rB var69 = var2.e(var60.b(qZ.a));
         rB var8 = var2.e(var60.b(qZ.b));
         if (rs.c(var60.a, var2.e).f(var1.e) < 0.0) {
            rB var57 = var69;
            var69 = var8;
            var8 = var57;
         }

         rA var58 = new rw(var69, var8, var68).d();
         rA var61 = new rw(var8, var69, var68).d();
         rM var66 = this.d().a(false).a(var58);
         rM var62 = this.d().c(var66.b) ? new rM(Boolean.FALSE) : new rM<>(var61, new rM<>(Boolean.FALSE), var66.b, null);
         rM var59 = this.d().c(var66.c) ? new rM(Boolean.FALSE) : new rM<>(var58, new rM<>(Boolean.FALSE), var66.c, null);
         return new sb.a<>(new rr(var2.a(), new ry(var62, var68)), new rr(var2.a(), new ry(var59, var68)));
      }
   }
}
