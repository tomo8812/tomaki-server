package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class yS extends yP {
   private final boolean c;

   @Deprecated
   public yS() {
      this(true);
   }

   private yS(ya<yq> var1) {
      this(true, var1);
   }

   @Deprecated
   private yS(boolean var1) {
      this(true, new yu());
   }

   private yS(boolean var1, ya<yq> var2) {
      super(var2);
      this.c = var1;
   }

   @Override
   public final yq h() {
      ya var1;
      if ((var1 = this.c()) == null) {
         throw new pl();
      }

      int var2 = this.f().length;
      tq var3 = this.e();
      double[] var4 = new double[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = var3.b(var5, var5);
      }

      double[] var31;
      int var30 = (var31 = this.d()).length;
      yq var6 = null;
      int var7 = 0;
      boolean var8 = false;

      while (!var8) {
         var7++;
         yq var9 = var6;
         double[] var32 = this.a(var31);
         double[] var10 = this.d(var32);
         tq var11 = this.b(var31);
         var6 = new yq(var31, var32);
         double[] var12 = new double[var30];
         double[][] var13 = new double[var30][var30];

         for (int var14 = 0; var14 < var2; var14++) {
            double[] var15 = var11.f(var14);
            double var19 = var4[var14];
            double var21 = var10[var14];
            double var23 = var19 * var21;

            for (int var16 = 0; var16 < var30; var16++) {
               var12[var16] += var23 * var15[var16];
            }

            for (int var34 = 0; var34 < var30; var34++) {
               double[] var17 = var13[var34];
               double var27 = var19 * var15[var34];

               for (int var18 = 0; var18 < var30; var18++) {
                  var17[var18] += var27 * var15[var18];
               }
            }
         }

         try {
            sD var33 = new sD(var13);
            double[] var35 = (this.c ? new tb(var33).a() : new tn(var33).c()).a(new sA(var12, false)).g();

            for (int var20 = 0; var20 < var30; var20++) {
               var31[var20] += var35[var20];
            }
         } catch (tA var29) {
            throw new oR(px.UNABLE_TO_SOLVE_SINGULAR_PROBLEM);
         }

         if (var9 != null && (var8 = var1.a(var7, var9, var6))) {
            super.b = this.c(var10);
            super.a = var6.a();
            return var6;
         }
      }

      throw new oY();
   }
}
