package org.valkyrienskies.core.impl.shadow;

public final class sF extends tm {
   private static String a;
   private static String b;
   private boolean c;
   private final double d;

   private sF(int var1, double var2, boolean var4) {
      super(var1);
      this.d = var2;
      this.c = var4;
   }

   private sF(Cf var1, double var2, boolean var4) {
      super(var1);
      this.d = var2;
      this.c = var4;
   }

   private boolean b() {
      return this.c;
   }

   @Override
   public final tu a(tp var1, tp var2, tu var3, tu var4) {
      c(var1, var2, var3, var4);
      Cf var5;
      (var5 = this.a()).c();
      double var6 = this.d * var3.c();
      var var8 = tu.n(var3);
      Cc.a var11 = var5.a;
      var5.a.d(1);
      tu var28;
      var var9 = tu.n(var28 = var4);
      tu var10 = var28.j();
      sA var29 = var1.a(var10);
      tu var27;
      var var12 = tu.n(var27 = var3.b(1.0, -1.0, var29));
      double var15 = var27.c();
      tu var13;
      if (var2 == null) {
         var13 = var27;
      } else {
         var13 = null;
      }

      var11 = var5.a;
      sJ var31 = new sJ(this, var5.a.e, var9, var8, var12, var15);
      var5.a(var31);
      if (var15 <= var6) {
         var5.d(var31);
         return var28;
      }

      double var19 = 0.0;

      do {
         var11 = var5.a;
         var5.a.d(1);
         var11 = var5.a;
         sJ var34 = new sJ(this, var5.a.e, var9, var8, var12, var15);
         var5.c(var34);
         if (var2 != null) {
            var13 = var2.a(var27);
         }

         double var21 = var27.b(var13);
         if (this.c && var21 <= 0.0) {
            pu var24;
            tf var39;
            (var24 = (var39 = new tf()).a).a("operator", var2);
            var24.a("vector", var27);
            throw var39;
         }

         var11 = var5.a;
         if (var5.a.e == 2) {
            var10.a(0, var13);
         } else {
            var10.a(var21 / var19, 1.0, var13);
         }

         sA var36 = var1.a(var10);
         double var23 = var10.b(var36);
         if (this.c && var23 <= 0.0) {
            pu var26;
            tf var40;
            (var26 = (var40 = new tf()).a).a("operator", var1);
            var26.a("vector", var10);
            throw var40;
         }

         double var25 = var21 / var23;
         var28.a(1.0, var25, var10);
         var27.a(1.0, -var25, var36);
         var19 = var21;
         var15 = var27.c();
         var11 = var5.a;
         var38 = new sJ(this, var5.a.e, var9, var8, var12, var15);
         var5.b(var38);
      } while (!(var15 <= var6));

      var5.d(var38);
      return var28;
   }
}
