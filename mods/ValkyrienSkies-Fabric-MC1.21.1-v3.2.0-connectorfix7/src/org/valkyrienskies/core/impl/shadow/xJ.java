package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class xJ extends xI {
   private final boolean a;

   private xJ(wK<wR> var1) {
      this(true, var1);
   }

   public xJ(boolean var1, wK<wR> var2) {
      super(var2);
      this.a = var1;
   }

   private wR o() {
      xJ var1 = this;
      if (this.f() == null && var1.g() == null) {
         wK var29;
         if ((var29 = this.j()) == null) {
            throw new pl();
         }

         int var2 = this.d().length;
         tq var3 = this.b();
         double[] var4 = new double[var2];

         for (int var5 = 0; var5 < var2; var5++) {
            var4[var5] = var3.b(var5, var5);
         }

         double[] var31;
         int var30 = (var31 = this.e()).length;
         wR var6 = null;
         boolean var7 = false;

         while (!var7) {
            this.l();
            wR var8 = var6;
            double[] var32 = this.b(var31);
            double[] var9 = this.e(var32);
            tq var10 = this.c(var31);
            var6 = new wR(var31, var32);
            double[] var11 = new double[var30];
            double[][] var12 = new double[var30][var30];

            for (int var13 = 0; var13 < var2; var13++) {
               double[] var14 = var10.f(var13);
               double var18 = var4[var13];
               double var20 = var9[var13];
               double var22 = var18 * var20;

               for (int var15 = 0; var15 < var30; var15++) {
                  var11[var15] += var22 * var14[var15];
               }

               for (int var34 = 0; var34 < var30; var34++) {
                  double[] var16 = var12[var34];
                  double var26 = var18 * var14[var34];

                  for (int var17 = 0; var17 < var30; var17++) {
                     var16[var17] += var26 * var14[var17];
                  }
               }
            }

            if (var8 != null && (var7 = var29.a(this.i(), var8, var6))) {
               this.a(this.d(var9));
               return var6;
            }

            try {
               sD var33 = new sD(var12);
               double[] var35 = (this.a ? new tb(var33).a() : new tn(var33).c()).a(new sA(var11, false)).g();

               for (int var19 = 0; var19 < var30; var19++) {
                  var31[var19] += var35[var19];
               }
            } catch (tA var28) {
               throw new oR(px.UNABLE_TO_SOLVE_SINGULAR_PROBLEM);
            }
         }

         throw new oY();
      } else {
         throw new pb(px.CONSTRAINT);
      }
   }

   private void p() {
      if (this.f() != null || this.g() != null) {
         throw new pb(px.CONSTRAINT);
      }
   }

   // $VF: synthetic method
   @Override
   public final Object c() {
      return this.o();
   }
}
