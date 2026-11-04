package org.valkyrienskies.core.impl.shadow;

@Deprecated
public class zi extends zf {
   private static final double a = 0.5 * (3.0 - BY.a(5.0));
   private static final double b = 2.0 * BY.x(1.0);
   private final double c;
   private final double d;

   public zi(double var1, double var3, ya<zm> var5) {
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

   private zi(double var1, double var3) {
      this(var1, var3, null);
   }

   @Override
   protected final zm h() {
      boolean var1 = this.d() == yf.MINIMIZE;
      double var2 = this.e();
      double var4 = this.g();
      double var6 = this.f();
      ya var8 = this.c();
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
      zm var50 = null;
      zm var3;
      zm var51 = var3 = new zm(var13, var1 ? var23 : -var23);
      int var5 = 0;

      while (true) {
         double var33 = 0.5 * (var9 + var11);
         double var35 = this.c * BY.w(var13) + this.d;
         double var37 = var35 * 2.0;
         if (BY.w(var13 - var33) <= var37 - 0.5 * (var11 - var9)) {
            return a(var51, a(var50, var3, var1), var1);
         }

         if (BY.w(var21) > var35) {
            double var44 = (var13 - var17) * (var23 - var25);
            double var42 = (var13 - var15) * (var23 - var27);
            double var40 = (var13 - var15) * var42 - (var13 - var17) * var44;
            if ((var42 = 2.0 * (var42 - var44)) > 0.0) {
               var40 = -var40;
            } else {
               var42 = -var42;
            }

            var44 = var21;
            var21 = var19;
            if (var40 > var42 * (var9 - var13) && var40 < var42 * (var11 - var13) && BY.w(var40) < BY.w(var42 * 0.5 * var44)) {
               var19 = var40 / var42;
               double var46;
               if ((var46 = var13 + var19) - var9 < var37 || var11 - var46 < var37) {
                  if (var13 <= var33) {
                     var19 = var35;
                  } else {
                     var19 = -var35;
                  }
               }
            } else {
               if (var13 < var33) {
                  var21 = var11 - var13;
               } else {
                  var21 = var9 - var13;
               }

               var19 = a * var21;
            }
         } else {
            if (var13 < var33) {
               var21 = var11 - var13;
            } else {
               var21 = var9 - var13;
            }

            var19 = a * var21;
         }

         double var54;
         if (BY.w(var19) < var35) {
            if (var19 >= 0.0) {
               var54 = var13 + var35;
            } else {
               var54 = var13 - var35;
            }
         } else {
            var54 = var13 + var19;
         }

         double var48 = this.a(var54);
         if (!var1) {
            var48 = -var48;
         }

         var50 = var3;
         var3 = new zm(var54, var1 ? var48 : -var48);
         var51 = a(var51, a(var50, var3, var1), var1);
         if (var8 != null && var8.a(var5, var50, var3)) {
            return var51;
         }

         if (var48 <= var23) {
            if (var54 < var13) {
               var11 = var13;
            } else {
               var9 = var13;
            }

            var15 = var17;
            var25 = var27;
            var17 = var13;
            var27 = var23;
            var13 = var54;
            var23 = var48;
         } else {
            if (var54 < var13) {
               var9 = var54;
            } else {
               var11 = var54;
            }

            if (var48 <= var27 || Cq.a(var17, var13)) {
               var15 = var17;
               var25 = var27;
               var17 = var54;
               var27 = var48;
            } else if (var48 <= var25 || Cq.a(var15, var13) || Cq.a(var15, var17)) {
               var15 = var54;
               var25 = var48;
            }
         }

         var5++;
      }
   }

   private static zm a(zm var0, zm var1, boolean var2) {
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
}
