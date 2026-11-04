package org.valkyrienskies.core.impl.shadow;

public final class xM extends xR {
   private static final double a = 0.5 * (3.0 - BY.a(5.0));
   private static final double b = 2.0 * BY.x(1.0);
   private final double c;
   private final double d;

   public xM(double var1, double var3, wK<xS> var5) {
      super(var5);
      if (var1 < b) {
         throw new pn(var1, b, true);
      }

      if (var3 <= 0.0) {
         throw new pk(var3);
      }

      this.c = var1;
      this.d = var3;
   }

   private xM(double var1, double var3) {
      this(var1, var3, null);
   }

   private xS g() {
      boolean var1 = this.b() == xi.MINIMIZE;
      double var2 = this.e();
      double var4 = this.d();
      double var6 = this.f();
      wK var8 = this.j();
      double var9;
      double var11;
      if (var2 < var6) {
         var9 = var2;
         var11 = var6;
      } else {
         var9 = var6;
         var11 = var2;
      }

      double var13 = var4;
      double var15 = var4;
      double var17 = var13;
      double var19 = 0.0;
      double var21 = 0.0;
      double var23 = this.a(var13);
      if (!var1) {
         var23 = -var23;
      }

      double var25 = var23;
      double var27 = var23;
      xS var49 = null;
      xS var3;
      xS var50 = var3 = new xS(var13, var1 ? var23 : -var23);

      while (true) {
         double var32 = 0.5 * (var9 + var11);
         double var34 = this.c * BY.w(var13) + this.d;
         double var36 = var34 * 2.0;
         if (BY.w(var13 - var32) <= var36 - 0.5 * (var11 - var9)) {
            return a(var50, a(var49, var3, var1), var1);
         }

         if (BY.w(var21) > var34) {
            double var43 = (var13 - var17) * (var23 - var25);
            double var41 = (var13 - var15) * (var23 - var27);
            double var39 = (var13 - var15) * var41 - (var13 - var17) * var43;
            if ((var41 = 2.0 * (var41 - var43)) > 0.0) {
               var39 = -var39;
            } else {
               var41 = -var41;
            }

            var43 = var21;
            var21 = var19;
            if (var39 > var41 * (var9 - var13) && var39 < var41 * (var11 - var13) && BY.w(var39) < BY.w(var41 * 0.5 * var43)) {
               var19 = var39 / var41;
               double var45;
               if ((var45 = var13 + var19) - var9 < var36 || var11 - var45 < var36) {
                  if (var13 <= var32) {
                     var19 = var34;
                  } else {
                     var19 = -var34;
                  }
               }
            } else {
               if (var13 < var32) {
                  var21 = var11 - var13;
               } else {
                  var21 = var9 - var13;
               }

               var19 = a * var21;
            }
         } else {
            if (var13 < var32) {
               var21 = var11 - var13;
            } else {
               var21 = var9 - var13;
            }

            var19 = a * var21;
         }

         double var53;
         if (BY.w(var19) < var34) {
            if (var19 >= 0.0) {
               var53 = var13 + var34;
            } else {
               var53 = var13 - var34;
            }
         } else {
            var53 = var13 + var19;
         }

         double var47 = this.a(var53);
         if (!var1) {
            var47 = -var47;
         }

         var49 = var3;
         var3 = new xS(var53, var1 ? var47 : -var47);
         var50 = a(var50, a(var49, var3, var1), var1);
         if (var8 != null && var8.a(this.i(), var49, var3)) {
            return var50;
         }

         if (var47 <= var23) {
            if (var53 < var13) {
               var11 = var13;
            } else {
               var9 = var13;
            }

            var15 = var17;
            var25 = var27;
            var17 = var13;
            var27 = var23;
            var13 = var53;
            var23 = var47;
         } else {
            if (var53 < var13) {
               var9 = var53;
            } else {
               var11 = var53;
            }

            if (var47 <= var27 || Cq.a(var17, var13)) {
               var15 = var17;
               var25 = var27;
               var17 = var53;
               var27 = var47;
            } else if (var47 <= var25 || Cq.a(var15, var13) || Cq.a(var15, var17)) {
               var15 = var53;
               var25 = var47;
            }
         }

         this.l();
      }
   }

   private static xS a(xS var0, xS var1, boolean var2) {
      if (var0 == null) {
         return var1;
      } else if (var1 == null) {
         return var0;
      } else if (var2) {
         return var0.b <= var1.b ? var0 : var1;
      } else {
         return var0.b >= var1.b ? var0 : var1;
      }
   }

   // $VF: synthetic method
   @Override
   public final Object c() {
      xM var1 = this;
      boolean var2 = this.b() == xi.MINIMIZE;
      double var3 = var1.e();
      double var5 = var1.d();
      double var7 = var1.f();
      wK var9 = var1.j();
      double var10;
      double var12;
      if (var3 < var7) {
         var10 = var3;
         var12 = var7;
      } else {
         var10 = var7;
         var12 = var3;
      }

      double var14 = var5;
      double var16 = var5;
      double var18 = var14;
      double var20 = 0.0;
      double var22 = 0.0;
      double var24 = var1.a(var14);
      if (!var2) {
         var24 = -var24;
      }

      double var26 = var24;
      double var28 = var24;
      xS var50 = null;
      xS var4;
      xS var51 = var4 = new xS(var14, var2 ? var24 : -var24);

      while (true) {
         double var33 = 0.5 * (var10 + var12);
         double var35 = var1.c * BY.w(var14) + var1.d;
         double var37 = var35 * 2.0;
         if (BY.w(var14 - var33) <= var37 - 0.5 * (var12 - var10)) {
            return a(var51, a(var50, var4, var2), var2);
         }

         if (BY.w(var22) > var35) {
            double var44 = (var14 - var18) * (var24 - var26);
            double var42 = (var14 - var16) * (var24 - var28);
            double var40 = (var14 - var16) * var42 - (var14 - var18) * var44;
            if ((var42 = 2.0 * (var42 - var44)) > 0.0) {
               var40 = -var40;
            } else {
               var42 = -var42;
            }

            var44 = var22;
            var22 = var20;
            if (var40 > var42 * (var10 - var14) && var40 < var42 * (var12 - var14) && BY.w(var40) < BY.w(var42 * 0.5 * var44)) {
               var20 = var40 / var42;
               double var46;
               if ((var46 = var14 + var20) - var10 < var37 || var12 - var46 < var37) {
                  if (var14 <= var33) {
                     var20 = var35;
                  } else {
                     var20 = -var35;
                  }
               }
            } else {
               if (var14 < var33) {
                  var22 = var12 - var14;
               } else {
                  var22 = var10 - var14;
               }

               var20 = a * var22;
            }
         } else {
            if (var14 < var33) {
               var22 = var12 - var14;
            } else {
               var22 = var10 - var14;
            }

            var20 = a * var22;
         }

         double var54;
         if (BY.w(var20) < var35) {
            if (var20 >= 0.0) {
               var54 = var14 + var35;
            } else {
               var54 = var14 - var35;
            }
         } else {
            var54 = var14 + var20;
         }

         double var48 = var1.a(var54);
         if (!var2) {
            var48 = -var48;
         }

         var50 = var4;
         var4 = new xS(var54, var2 ? var48 : -var48);
         var51 = a(var51, a(var50, var4, var2), var2);
         if (var9 != null && var9.a(var1.i(), var50, var4)) {
            return var51;
         }

         if (var48 <= var24) {
            if (var54 < var14) {
               var12 = var14;
            } else {
               var10 = var14;
            }

            var16 = var18;
            var26 = var28;
            var18 = var14;
            var28 = var24;
            var14 = var54;
            var24 = var48;
         } else {
            if (var54 < var14) {
               var10 = var54;
            } else {
               var12 = var54;
            }

            if (var48 <= var28 || Cq.a(var18, var14)) {
               var16 = var18;
               var26 = var28;
               var18 = var54;
               var28 = var48;
            } else if (var48 <= var26 || Cq.a(var16, var14) || Cq.a(var16, var18)) {
               var16 = var54;
               var26 = var48;
            }
         }

         var1.l();
      }
   }
}
