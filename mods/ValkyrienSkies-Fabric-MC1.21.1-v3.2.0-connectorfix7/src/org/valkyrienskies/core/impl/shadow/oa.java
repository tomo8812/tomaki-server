package org.valkyrienskies.core.impl.shadow;

public final class oa {
   private static final String a = "pow";

   private oa() {
   }

   private static nX[] a(nZ var0, String var1) {
      nX[] var2 = new nX[2];
      boolean var4 = true;
      int var5 = 0;
      int var6 = 0;
      char[] var3 = new char[var1.length()];

      for (int var7 = 0; var7 < var3.length; var7++) {
         var3[var7] = var1.charAt(var7);
         if (var3[var7] >= '1' && var3[var7] <= '9') {
            var4 = false;
         }

         if (var3[var7] == '.') {
            var6 += (400 - var6) % 4;
            var4 = false;
         }

         if (var6 == var0.a / 2 << 2) {
            var5 = var7;
            break;
         }

         if (var3[var7] >= '0' && var3[var7] <= '9' && !var4) {
            var6++;
         }
      }

      var2[0] = var0.a(new String(var3, 0, var5));

      for (int var9 = 0; var9 < var3.length; var9++) {
         var3[var9] = var1.charAt(var9);
         if (var3[var9] >= '0' && var3[var9] <= '9' && var9 < var5) {
            var3[var9] = '0';
         }
      }

      var2[1] = var0.a(new String(var3));
      return var2;
   }

   private static nX[] i(nX var0) {
      nX[] var1 = new nX[2];
      nX var2 = var0.h(var0.f(var0.E() / 2));
      var1[0] = var0.f(var2).g(var2);
      var1[1] = var0.g(var1[0]);
      return var1;
   }

   private static nX[] a(nX[] var0, nX[] var1) {
      nX[] var2;
      (var2 = new nX[2])[1] = var0[0].F();
      var2[0] = var0[0].h(var1[0]);
      if (var2[0].R() != 1 && !var2[0].equals(var2[1])) {
         var2[1] = var0[0].h(var1[1]).f(var0[1].h(var1[0])).f(var0[1].h(var1[1]));
         return var2;
      } else {
         return var2;
      }
   }

   private static nX[] b(nX[] var0, nX[] var1) {
      nX[] var2;
      (var2 = new nX[2])[0] = var0[0].i(var1[0]);
      var2[1] = var0[1].h(var1[0]).g(var0[0].h(var1[1]));
      var2[1] = var2[1].i(var1[0].h(var1[0]).f(var1[0].h(var1[1])));
      return var2;
   }

   private static nX a(nX[] var0, int var1) {
      boolean var2 = false;
      nX[] var3 = new nX[2];
      nX[] var4;
      (var4 = new nX[2])[0] = var0[0].G();
      var4[1] = var0[0].F();
      if (var1 == 0) {
         return var4[0].f(var4[1]);
      }

      if (var1 < 0) {
         var2 = true;
         var1 = -var1;
      }

      label27:
      while (true) {
         var3[0] = new nX(var0[0]);
         var3[1] = new nX(var0[1]);
         byte var5 = 1;

         while (true) {
            byte var6 = var5;
            if ((var5 <<= 1) > var1) {
               var5 = var6;
               var1 -= var5;
               var4 = a(var4, var3);
               if (var1 <= 0) {
                  break label27;
               }
               break;
            }

            var3 = a(var3, var3);
         }
      }

      var4[0] = var4[0].f(var4[1]);
      if (var2) {
         var4[0] = var0[0].G().i(var4[0]);
      }

      return var4[0];
   }

   public static nX a(nX var0, int var1) {
      boolean var2 = false;
      nX var3 = var0.G();
      if (var1 == 0) {
         return var3;
      }

      if (var1 < 0) {
         var2 = true;
         var1 = -var1;
      }

      do {
         nX var4 = new nX(var0);
         byte var6 = 1;

         nX var5;
         byte var7;
         do {
            var5 = new nX(var4);
            var7 = var6;
            var4 = var4.h(var4);
            var6 <<= 1;
         } while (var1 > var6);

         var4 = var5;
         var6 = var7;
         var1 -= var6;
         var3 = var3.h(var4);
      } while (var1 > 0);

      if (var2) {
         var3 = var0.G().i(var3);
      }

      return var0.a(var3);
   }

   public static nX a(nX var0) {
      nX var1 = var0.K();
      nX var2 = var0.g(var1);
      int var8;
      if ((var8 = var1.M()) > 2147483646) {
         return var0.a((byte)1, (byte)1);
      }

      if (var8 < -2147483646) {
         return var0.C();
      }

      var0 = a(var0.e.l(), var8);
      var1 = var2;
      var2 = var2.G();
      nX var3 = var1.G();
      nX var4 = var1.G();
      nX var5 = new nX(var2);

      for (int var6 = 1; var6 < 90; var6++) {
         var3 = var3.h(var1);
         var4 = var4.j(var6);
         if ((var2 = var2.f(var3.h(var4))).equals(var5)) {
            break;
         }

         var5 = new nX(var2);
      }

      var1 = var2;
      return var0.h(var1);
   }

   private static nX j(nX var0) {
      nX var1 = var0.G();
      nX var2 = var0.G();
      nX var3 = var0.G();
      nX var4 = new nX(var1);

      for (int var5 = 1; var5 < 90; var5++) {
         var2 = var2.h(var0);
         var3 = var3.j(var5);
         if ((var1 = var1.f(var2.h(var3))).equals(var4)) {
            break;
         }

         var4 = new nX(var1);
      }

      return var1;
   }

   public static nX b(nX var0) {
      int var4 = 0;
      if (!var0.equals(var0.F()) && !var0.b(var0.F()) && !var0.J()) {
         if (var0.R() == 1) {
            return var0;
         }

         nX var2;
         int var1 = (var2 = new nX(var0)).N();

         for (int var3 = (var2 = var2.i(a(var0.e(10000), var1))).L().M(); var3 > 2; var4++) {
            var3 >>= 1;
         }

         nX[] var13 = i(var2);
         nX[] var14;
         (var14 = new nX[2])[0] = a(var0.H(), var4);
         var13[0] = var13[0].i(var14[0]);
         var13[1] = var13[1].i(var14[0]);

         for (var14[0] = var0.a("1.33333"); var13[0].f(var13[1]).c(var14[0]); var4++) {
            var13[0] = var13[0].j(2);
            var13[1] = var13[1].j(2);
         }

         var14 = var13;
         nX var5;
         nX var6 = (var5 = var13[0].j(4).f(var14[1].j(4))).f(var14[0].a("-0.25")).i(var5.f(var14[0].a("0.25")));
         nX var7 = new nX(var6);
         nX var8 = new nX(var6);
         nX var9 = new nX(var7);
         byte var10 = 1;

         for (int var11 = 0; var11 < 10000; var11++) {
            var8 = var8.h(var6).h(var6);
            var10 += 2;
            var5 = var8.j(var10);
            if ((var7 = var7.f(var5)).equals(var9)) {
               break;
            }

            var9 = new nX(var7);
         }

         nX[] var19 = i(var7.h(var14[0].H()));
         var13[0] = var0.a("" + (var4 + 4 * var1));
         var13[1] = var0.F();
         var14 = a(var0.e.n(), var13);
         var19[0] = var19[0].f(var14[0]);
         var19[1] = var19[1].f(var14[1]);
         var13[0] = var0.a("" + 4 * var1);
         var13[1] = var0.F();
         var14 = a(var0.e.o(), var13);
         var19[0] = var19[0].f(var14[0]);
         var19[1] = var19[1].f(var14[1]);
         return var0.a(var19[0].f(var19[1]));
      } else {
         var0.e.a(1);
         return var0.a(1, "ln", var0, var0.a((byte)1, (byte)3));
      }
   }

   private static nX[] a(nX[] var0) {
      nX var1;
      nX var2 = (var1 = var0[0].j(4).f(var0[1].j(4))).f(var0[0].a("-0.25")).i(var1.f(var0[0].a("0.25")));
      nX var3 = new nX(var2);
      nX var4 = new nX(var2);
      nX var5 = new nX(var3);
      byte var6 = 1;

      for (int var7 = 0; var7 < 10000; var7++) {
         var4 = var4.h(var2).h(var2);
         var6 += 2;
         var1 = var4.j(var6);
         if ((var3 = var3.f(var1)).equals(var5)) {
            break;
         }

         var5 = new nX(var3);
      }

      return i(var3.h(var0[0].H()));
   }

   public static nX a(nX var0, nX var1) {
      nZ var8 = var0.e;
      var8 = var1.e;
      if (var0.e.a != var1.e.a) {
         var0.e.a(1);
         nX var9;
         (var9 = var0.a(var0.F())).d = 3;
         return var0.a(1, "pow", var0, var9);
      }

      nX var2 = var0.F();
      nX var3 = var0.G();
      nX var4 = var0.H();
      boolean var5 = false;
      if (var1.equals(var2)) {
         return var0.a(var3);
      }

      if (var1.equals(var3)) {
         if (var0.J()) {
            var0.e.a(1);
            return var0.a(1, "pow", var0, var0);
         } else {
            return var0;
         }
      } else {
         if (var0.J() || var1.J()) {
            var0.e.a(1);
            return var0.a(1, "pow", var0, var0.a((byte)1, (byte)3));
         }

         if (var0.equals(var2)) {
            if (nX.a(var3, var0).c(var2)) {
               return var1.c(var2) ? var0.a(var2) : var0.a(var0.a((byte)1, (byte)1));
            } else if (var1.R() != 0 || !var1.K().equals(var1) || var1.e(var4).equals(var2)) {
               return var1.c(var2) ? var0.a(var2) : var0.a(var0.a((byte)1, (byte)1));
            } else {
               return var1.c(var2) ? var0.a(var2.P()) : var0.a(var0.a((byte)-1, (byte)1));
            }
         } else {
            if (var0.b(var2)) {
               var0 = var0.P();
               var5 = true;
            }

            if (var0.c(var3) && var1.R() == 1) {
               return var1.c(var2) ? var1 : var0.a(var2);
            }

            if (var0.b(var3) && var1.R() == 1) {
               return var1.c(var2) ? var0.a(var2) : var0.a(nX.a(var1, var3));
            }

            if (var0.equals(var3) && var1.R() == 1) {
               var0.e.a(1);
               return var0.a(1, "pow", var0, var0.a((byte)1, (byte)3));
            }

            if (var0.R() == 1) {
               if (var5) {
                  if (var1.R() != 0 || !var1.K().equals(var1) || var1.e(var4).equals(var2)) {
                     return var1.c(var2) ? var0.a(var0.a((byte)1, (byte)1)) : var0.a(var2);
                  } else {
                     return var1.c(var2) ? var0.a(var0.a((byte)-1, (byte)1)) : var0.a(var2.P());
                  }
               } else {
                  return var1.c(var2) ? var0 : var0.a(var2);
               }
            } else {
               if (var5 && !var1.K().equals(var1)) {
                  var0.e.a(1);
                  return var0.a(1, "pow", var0, var0.a((byte)1, (byte)3));
               }

               if (var1.b(var0.e(100000000)) && var1.c(var0.e(-100000000))) {
                  nX var6;
                  int var11 = (var6 = var1.K()).M();
                  if ((var6 = var1.g(var6)).d(var2)) {
                     nX var13;
                     nX var10000 = var13 = var6.h(b(var0));
                     var8 = var0.e;
                     nX var7 = var10000.i(var0.e.f).K();
                     var8 = var0.e;
                     var6 = var13.g(var7.h(var0.e.f));
                     var3 = a(i(var0), var11).h(a(var4, var7.M())).h(a(var6));
                  } else {
                     var3 = a(i(var0), var11);
                  }
               } else {
                  var3 = a(b(var0).h(var1));
               }

               if (var5 && var1.K().equals(var1) && !var1.e(var4).equals(var2)) {
                  var3 = var3.P();
               }

               return var0.a(var3);
            }
         }
      }
   }

   private static nX b(nX[] var0) {
      nX var1;
      nX var2 = var1 = var0[0].f(var0[1]);
      var1 = var1.h(var1);
      nX var3 = var2;
      nX var6 = var0[0].G();
      nX var4 = new nX(var2);

      for (byte var5 = 3; var5 < 90; var5 += 2) {
         var3 = var3.h(var1).P();
         var6 = var6.j((var5 - 1) * var5);
         if ((var2 = var2.f(var3.h(var6))).equals(var4)) {
            break;
         }

         var4 = new nX(var2);
      }

      return var2;
   }

   private static nX c(nX[] var0) {
      nX var1;
      nX var2 = var1 = var0[0].G();
      nX var3 = var1;
      nX var6;
      nX var7 = (var6 = var0[0].f(var0[1])).h(var6);
      var1 = var1;
      nX var4 = new nX(var3);

      for (byte var5 = 2; var5 < 90; var5 += 2) {
         var2 = var2.h(var7).P();
         var1 = var1.j((var5 - 1) * var5);
         if ((var3 = var3.f(var2.h(var1))).equals(var4)) {
            break;
         }

         var4 = new nX(var3);
      }

      return var3;
   }

   public static nX c(nX var0) {
      nZ var2 = var0.e;
      nX var1 = var0.e.e;
      var2 = var0.e;
      nX var8 = var0.e.b;
      boolean var3 = false;
      nX var4;
      if ((var4 = var0.e(var1.i(2))).b(var8)) {
         var4 = var4.P();
         var3 = true;
      }

      if (var4.c(var1.j(2))) {
         var4 = var1.g(var4);
      }

      if (var4.b(var1.j(4))) {
         var1 = b(i(var4));
      } else {
         nX[] var6 = new nX[2];
         nX[] var9 = var0.e.k();
         var6[0] = var9[0].j(2).g(var4);
         var6[1] = var9[1].j(2);
         var1 = c(var6);
      }

      if (var3) {
         var1 = var1.P();
      }

      return var0.a(var1);
   }

   public static nX d(nX var0) {
      nZ var2 = var0.e;
      nX var1 = var0.e.e;
      var2 = var0.e;
      nX var9 = var0.e.b;
      boolean var3 = false;
      nX var4;
      if ((var4 = var0.e(var1.i(2))).b(var9)) {
         var4 = var4.P();
      }

      if (var4.c(var1.j(2))) {
         var4 = var1.g(var4);
         var3 = true;
      }

      if (var4.b(var1.j(4))) {
         nX[] var5;
         (var5 = new nX[2])[0] = var4;
         var5[1] = var9;
         var1 = c(var5);
      } else {
         nX[] var7 = new nX[2];
         nX[] var10 = var0.e.k();
         var7[0] = var10[0].j(2).g(var4);
         var7[1] = var10[1].j(2);
         var1 = b(var7);
      }

      if (var3) {
         var1 = var1.P();
      }

      return var0.a(var1);
   }

   public static nX e(nX var0) {
      return c(var0).i(d(var0));
   }

   private static nX k(nX var0) {
      nX var1 = new nX(var0);
      nX var2 = new nX(var1);
      nX var3 = new nX(var1);

      for (byte var4 = 3; var4 < 90; var4 += 2) {
         var2 = var2.h(var0).h(var0).P();
         if ((var1 = var1.f(var2.j(var4))).equals(var3)) {
            break;
         }

         var3 = new nX(var1);
      }

      return var1;
   }

   public static nX f(nX var0) {
      nZ var4 = var0.e;
      nX var1 = var0.e.b;
      var4 = var0.e;
      nX var2 = var0.e.c;
      nX[] var3 = var0.e.i();
      nX[] var21 = var0.e.k();
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;
      nX var8 = var3[0].g(var2).f(var3[1]);
      nX var9;
      if ((var9 = new nX(var0)).b(var1)) {
         var6 = true;
         var9 = var9.P();
      }

      if (var9.c(var2)) {
         var5 = true;
         var9 = var2.i(var9);
      }

      if (var9.c(var8)) {
         nX[] var10 = new nX[2];
         var7 = true;
         var10[0] = var3[0].g(var2);
         var10[1] = var3[1];
         nX[] var22;
         (var22 = a(var3 = i(var9), var10))[0] = var22[0].f(var2);
         var3[0] = var3[0].g(var10[0]);
         var3[1] = var3[1].g(var10[1]);
         nX[] var14 = var22;
         nX[] var11 = var3;
         (var3 = new nX[2])[0] = var11[0].i(var14[0]);
         var3[1] = var11[1].h(var14[0]).g(var11[0].h(var14[1]));
         var3[1] = var3[1].i(var14[0].h(var14[0]).f(var14[0].h(var14[1])));
         nX[] var18;
         var9 = (var18 = var3)[0].f(var18[1]);
      }

      var1 = var9;
      var2 = new nX(var1);
      nX var19 = new nX(var2);
      var8 = new nX(var2);

      for (byte var24 = 3; var24 < 90; var24 += 2) {
         var19 = var19.h(var1).h(var1).P();
         if ((var2 = var2.f(var19.j(var24))).equals(var8)) {
            break;
         }

         var8 = new nX(var2);
      }

      var1 = var2;
      if (var7) {
         var1 = var1.f(var21[0].j(8)).f(var21[1].j(8));
      }

      if (var5) {
         var1 = var21[0].j(2).g(var1).f(var21[1].j(2));
      }

      if (var6) {
         var1 = var1.P();
      }

      return var0.a(var1);
   }

   public static nX g(nX var0) {
      return f(var0.i(var0.G().g(var0.h(var0)).Q()));
   }

   public static nX h(nX var0) {
      boolean var2 = false;
      if (var0.b(var0.F())) {
         var2 = true;
      }

      nX var3;
      nX var1 = f((var3 = nX.a(var0, var0.G())).G().g(var3.h(var3)).Q().i(var3));
      if (var2) {
         nZ var4 = var3.e;
         var1 = var3.e.e.g(var1);
      }

      return var3.a(var1);
   }
}
