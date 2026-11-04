package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;

public final class rh {
   rs a;
   rs b;
   rs c;

   private rh(rs var1, rs var2) {
      this.a = var1;
      this.b = var2;
      this.c = rs.c(var1, var2);
   }

   private rB[][] a(rj var1) {
      rh.a var2 = new rh.a(var1.a);
      var1.a(true).a(var2);
      ry var30 = var2.a;
      rB[][] var31 = var2.a.b();

      for (int var32 = 0; var32 < var31.length; var32++) {
         rB[] var3;
         int var4 = (var3 = var31[var32]).length;
         int var5 = 0;

         while (var5 < var4) {
            int var8 = var5;
            int var7 = var4;
            rB[] var6 = var3;
            rB var9 = var3[(var8 + var7 - 1) % var7];
            rB var10 = var6[var8];
            rB var33 = var6[(var8 + 1) % var7];
            double var16 = var10.c - var9.c;
            double var18 = var10.d - var9.d;
            double var20 = var33.c - var10.c;
            double var22 = var33.d - var10.d;
            double var24 = var16 * var22 - var20 * var18;
            double var26 = var16 * var20 + var18 * var22;
            double var28 = BY.a((var16 * var16 + var18 * var18) * (var20 * var20 + var22 * var22));
            if (BY.w(var24) <= var28 * 1.0E-6 && var26 >= 0.0) {
               for (int var34 = var5; var34 < var4 - 1; var34++) {
                  var3[var34] = var3[var34 + 1];
               }

               var4--;
            } else {
               var5++;
            }
         }

         if (var4 != var3.length) {
            var31[var32] = new rB[var4];
            System.arraycopy(var3, 0, var31[var32], 0, var4);
         }
      }

      return var31;
   }

   private static boolean a(rB[] var0, int var1, int var2) {
      rB var3 = var0[(var2 + var1 - 1) % var1];
      rB var4 = var0[var2];
      rB var20 = var0[(var2 + 1) % var1];
      double var6 = var4.c - var3.c;
      double var8 = var4.d - var3.d;
      double var10 = var20.c - var4.c;
      double var12 = var20.d - var4.d;
      double var14 = var6 * var12 - var10 * var8;
      double var16 = var6 * var10 + var8 * var12;
      double var18 = BY.a((var6 * var6 + var8 * var8) * (var10 * var10 + var12 * var12));
      return BY.w(var14) <= var18 * 1.0E-6 && var16 >= 0.0;
   }

   // $VF: synthetic method
   static rs a(rh var0) {
      return var0.c;
   }

   // $VF: synthetic method
   static rs b(rh var0) {
      return var0.a;
   }

   // $VF: synthetic method
   static rs c(rh var0) {
      return var0.b;
   }

   class a implements rN<rc> {
      ry a;
      private final double b;

      a(double var2) {
         this.a = new ry(new rM<>(Boolean.FALSE), var2);
         this.b = var2;
      }

      @Override
      public final int a(rM<rc> var1) {
         return rN.a.MINUS_SUB_PLUS$672d2271;
      }

      @Override
      public final void b(rM<rc> var1) {
         rO var3;
         if ((var3 = (rO)var1.e).a != null) {
            this.a(var3.a, false);
         }

         if (var3.b != null) {
            this.a(var3.b, true);
         }
      }

      @Override
      public final void c(rM<rc> var1) {
      }

      private void a(sb<rc> var1, boolean var2) {
         rL var3 = (rL)var1;
         rh var13 = rh.this;
         double var5;
         ri var24;
         if (BY.w(var5 = (var24 = (ri)var1.c()).e.f(rh.this.c)) > 0.001) {
            rB[][] var27 = ((ry)var3.b).b();
            if (var5 < 0.0 ^ var2) {
               rB[][] var25 = new rB[var27.length][];

               for (int var4 = 0; var4 < var27.length; var4++) {
                  rB[] var31;
                  rB[] var6 = new rB[(var31 = var27[var4]).length];
                  if (var31[0] == null) {
                     var6[0] = null;

                     for (int var34 = 1; var34 < var31.length; var34++) {
                        var6[var34] = var31[var31.length - var34];
                     }
                  } else {
                     for (int var7 = 0; var7 < var31.length; var7++) {
                        var6[var7] = var31[var31.length - (var7 + 1)];
                     }
                  }

                  var25[var4] = var6;
               }

               var27 = var25;
            }

            ArrayList var26 = new ArrayList();
            rB[][] var29 = var27;
            int var32 = var27.length;

            for (int var33 = 0; var33 < var32; var33++) {
               boolean var28;
               rB[] var35;
               int var8 = (var28 = (var35 = var29[var33])[0] != null) ? var35.length - 1 : 1;
               rs var9 = var24.f(var35[var8]);
               int var10 = (var8 + 1) % var35.length;
               var13 = rh.this;
               double var10002 = var9.f(rh.this.a);
               var13 = rh.this;
               rB var38 = new rB(var10002, var9.f(rh.this.b));

               while (var10 < var35.length) {
                  rs var11 = var24.f(var35[var10]);
                  var13 = rh.this;
                  var10002 = var11.f(rh.this.a);
                  var13 = rh.this;
                  rB var39 = new rB(var10002, var11.f(rh.this.b));
                  rw var12;
                  sb var44 = (var12 = new rw(var38, var39, this.b)).d();
                  if (var28 || var8 != 1) {
                     double var22 = var12.h() + (Math.PI / 2);
                     rw var36 = new rw(var38, var22, this.b);
                     var44 = var44.a(var36).a;
                  }

                  if (var28 || var10 != var35.length - 1) {
                     double var45 = var12.h() + (Math.PI / 2);
                     rw var37 = new rw(var39, var45, this.b);
                     var44 = var44.a(var37).b;
                  }

                  var26.add(var44);
                  var8 = var10++;
                  var38 = var39;
               }
            }

            ry var30 = new ry(var26, this.b);
            this.a = (ry)new rZ<rv>().a(this.a, var30);
         }
      }

      public final ry a() {
         return this.a;
      }
   }
}
