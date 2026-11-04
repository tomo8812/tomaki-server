package org.valkyrienskies.core.impl.shadow;

public final class pR implements pV {
   private static final double a = 1.0E-11;
   private final pR.a b;

   public pR() {
      this(pR.a.QR);
   }

   private pR(pR.a var1) {
      this.b = var1;
   }

   private pR.a a() {
      return this.b;
   }

   private static pR a(pR.a var0) {
      return new pR(var0);
   }

   @Override
   public final pZ a(pW var1) {
      Cb var2 = var1.d();
      Cb var3 = var1.e();
      wK var4;
      if ((var4 = var1.f()) == null) {
         throw new pl();
      }

      tu var5 = var1.a();
      pW.a var6 = null;

      while (true) {
         var3.c();
         pW.a var7 = var6;
         var2.c();
         tu var8 = (var6 = var1.a(var5)).d();
         tq var9 = var6.c();
         var5 = var6.e();
         if (var7 != null && var4.a(var3.b, var7, var6)) {
            return new pZ(var6, var2.b, var3.b);
         }

         tu var12 = this.b.a(var9, var8);
         var5 = var5.l(var12);
      }
   }

   @Override
   public final String toString() {
      return "GaussNewtonOptimizer{decomposition=" + this.b + '}';
   }

   private static Co<tq, tu> b(tq var0, tu var1) {
      int var2 = var0.f();
      int var3;
      sv var4 = td.a(var3 = var0.g(), var3);
      sA var5 = new sA(var3);

      for (int var6 = 0; var6 < var2; var6++) {
         for (int var7 = 0; var7 < var3; var7++) {
            var5.a(var7, var5.a(var7) + var1.a(var6) * var0.b(var6, var7));
         }

         for (int var10 = 0; var10 < var3; var10++) {
            for (int var8 = var10; var8 < var3; var8++) {
               var4.c(var10, var8, var4.b(var10, var8) + var0.b(var6, var10) * var0.b(var6, var8));
            }
         }
      }

      for (int var9 = 0; var9 < var3; var9++) {
         for (int var11 = 0; var11 < var9; var11++) {
            var4.c(var9, var11, var4.b(var11, var9));
         }
      }

      return new Co<>(var4, var5);
   }

   // $VF: synthetic method
   static Co a(tq var0, tu var1) {
      var1 = var1;
      tq var9 = var0;
      int var2 = var0.f();
      int var3;
      sv var4 = td.a(var3 = var9.g(), var3);
      sA var5 = new sA(var3);

      for (int var6 = 0; var6 < var2; var6++) {
         for (int var7 = 0; var7 < var3; var7++) {
            var5.a(var7, var5.a(var7) + var1.a(var6) * var9.b(var6, var7));
         }

         for (int var12 = 0; var12 < var3; var12++) {
            for (int var8 = var12; var8 < var3; var8++) {
               var4.c(var12, var8, var4.b(var12, var8) + var9.b(var6, var12) * var9.b(var6, var8));
            }
         }
      }

      for (int var11 = 0; var11 < var3; var11++) {
         for (int var13 = 0; var13 < var11; var13++) {
            var4.c(var11, var13, var4.b(var13, var11));
         }
      }

      return new Co<>(var4, var5);
   }

   public enum a {
      LU {
         @Override
         protected final tu a(tq var1, tu var2) {
            try {
               Co var5;
               tq var7 = (tq)(var5 = pR.a(var1, var2)).a;
               tu var6 = (tu)var5.b;
               return new tb(var7, 1.0E-11).a().a(var6);
            } catch (tA var4) {
               throw new oR(px.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, var4);
            }
         }
      },
      QR {
         @Override
         protected final tu a(tq var1, tu var2) {
            try {
               return new tn(var1, 1.0E-11).c().a(var2);
            } catch (tA var3) {
               throw new oR(px.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, var3);
            }
         }
      },
      CHOLESKY {
         @Override
         protected final tu a(tq var1, tu var2) {
            try {
               Co var5;
               tq var7 = (tq)(var5 = pR.a(var1, var2)).a;
               tu var6 = (tu)var5.b;
               return new sE(var7, 1.0E-11, 1.0E-11).a().a(var6);
            } catch (te var4) {
               throw new oR(px.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, var4);
            }
         }
      },
      SVD {
         @Override
         protected final tu a(tq var1, tu var2) {
            return new tC(var1).a().a(var2);
         }
      };

      a() {
      }

      protected abstract tu a(tq var1, tu var2);

      // $VF: synthetic method
      a(byte var3) {
         this();
      }
   }
}
