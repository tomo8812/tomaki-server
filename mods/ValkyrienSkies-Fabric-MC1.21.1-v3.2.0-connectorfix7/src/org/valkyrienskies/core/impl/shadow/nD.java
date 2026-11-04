package org.valkyrienskies.core.impl.shadow;

public final class nD extends no {
   private static final double d = 1.0E-6;
   private final nD.a e = new nD.a((byte)0);

   public nD() {
      this(1.0E-6);
   }

   private nD(double var1) {
      super(1.0E-6);
   }

   private nD(double var1, double var3) {
      super(var1, var3);
   }

   private nD(double var1, double var3, double var5) {
      super(var1, var3, var5);
   }

   @Override
   public final double j() {
      double var1 = this.d();
      double var3 = this.e();
      double var5 = this.f();
      double var7 = this.i();
      double var19 = var3;
      double var17 = var5;
      nP.b(var1, var17, var19);
      double var9;
      if (BY.w(var9 = this.b(var5)) <= var7) {
         return var5;
      } else {
         double var11;
         if (BY.w(var11 = this.b(var1)) <= var7) {
            return var1;
         } else if (var9 * var11 < 0.0) {
            return this.d(var1, var5);
         } else {
            double var13;
            if (BY.w(var13 = this.b(var3)) <= var7) {
               return var3;
            } else if (var9 * var13 < 0.0) {
               return this.d(var5, var3);
            } else {
               throw new pe(var1, var3, var11, var13);
            }
         }
      }
   }

   @Deprecated
   private double d(double var1, double var3) {
      nQ[] var5 = nT.a(this.a());
      nQ var6 = new nQ(0.5 * (var1 + var3), 0.0);
      nQ var7 = this.e.b(var5, var6);
      if (this.e.a(var1, var3, var7)) {
         return var7.d;
      }

      double var8 = Double.NaN;
      var5 = this.e.a(var5, var6);

      for (int var12 = 0; var12 < var5.length; var12++) {
         if (this.e.a(var1, var3, var5[var12])) {
            nQ var10;
            var8 = (var10 = var5[var12]).d;
            break;
         }
      }

      return var8;
   }

   private nQ[] a(double[] var1, double var2) {
      int var8 = Integer.MAX_VALUE;
      double var6 = var2;
      double[] var10 = var1;
      nD var9 = this;
      this.a(Integer.MAX_VALUE, new ni(var10), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, var6);
      return var9.e.a(nT.a(var10), new nQ(var6, 0.0));
   }

   private nQ[] a(double[] var1, double var2, int var4) {
      this.a(Integer.MAX_VALUE, new ni(var1), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, var2);
      return this.e.a(nT.a(var1), new nQ(var2, 0.0));
   }

   private nQ b(double[] var1, double var2) {
      int var8 = Integer.MAX_VALUE;
      double var6 = var2;
      double[] var10 = var1;
      nD var9 = this;
      this.a(Integer.MAX_VALUE, new ni(var10), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, var6);
      return var9.e.b(nT.a(var10), new nQ(var6, 0.0));
   }

   private nQ b(double[] var1, double var2, int var4) {
      this.a(Integer.MAX_VALUE, new ni(var1), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, var2);
      return this.e.b(nT.a(var1), new nQ(var2, 0.0));
   }

   class a {
      private a() {
      }

      public final boolean a(double var1, double var3, nQ var5) {
         if (nD.a(var1, var5.d, var3)) {
            nD var8 = nD.this;
            double var10000 = nD.super.c * var5.d();
            nD var9 = nD.this;
            double var6 = BY.e(var10000, nD.super.b);
            if (!(BY.w(var5.c) <= var6)) {
               var10000 = var5.d();
               nD var10 = nD.this;
               if (!(var10000 <= nD.super.a)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }

      public final nQ[] a(nQ[] var1, nQ var2) {
         if (var1 == null) {
            throw new pl();
         }

         int var3;
         if ((var3 = var1.length - 1) == 0) {
            throw new pf(px.POLYNOMIAL);
         }

         nQ[] var4 = new nQ[var3 + 1];

         for (int var5 = 0; var5 <= var3; var5++) {
            var4[var5] = var1[var5];
         }

         nQ[] var10 = new nQ[var3];

         for (int var9 = 0; var9 < var3; var9++) {
            nQ[] var6 = new nQ[var3 - var9 + 1];
            System.arraycopy(var4, 0, var6, 0, var6.length);
            var10[var9] = this.b(var6, var2);
            nQ var11 = var4[var3 - var9];

            for (int var8 = var3 - var9 - 1; var8 >= 0; var8--) {
               nQ var7 = var4[var8];
               var4[var8] = var11;
               var11 = var7.a(var11.c(var10[var9]));
            }
         }

         return var10;
      }

      public final nQ b(nQ[] var1, nQ var2) {
         if (var1 == null) {
            throw new pl();
         }

         int var3;
         if ((var3 = var1.length - 1) == 0) {
            throw new pf(px.POLYNOMIAL);
         }

         nD var8 = nD.this;
         double var4 = nD.super.b;
         var8 = nD.this;
         double var6 = nD.super.c;
         var8 = nD.this;
         double var22 = nD.super.a;
         nQ var10 = new nQ(var3, 0.0);
         nQ var11 = new nQ(var3 - 1, 0.0);
         var2 = var2;
         nQ var12 = new nQ(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

         while (true) {
            nQ var13 = var1[var3];
            nQ var14 = nQ.b;
            nQ var15 = nQ.b;

            for (int var17 = var3 - 1; var17 >= 0; var17--) {
               var15 = var14.a(var2.c(var15));
               var14 = var13.a(var2.c(var14));
               var13 = var1[var17].a(var2.c(var13));
            }

            var15 = var15.c(new nQ(2.0, 0.0));
            double var31 = BY.e(var6 * var2.d(), var4);
            if (var2.d(var12).d() <= var31) {
               return var2;
            }

            if (var13.d() <= var22) {
               return var2;
            }

            nQ var28;
            var13 = (var28 = (var12 = var14.b(var13)).c(var12)).d(var15.b(var13));
            var13 = var11.c(var10.c(var13).d(var28)).g();
            var14 = var12.a(var13);
            var12 = var12.d(var13);
            if ((var13 = var14.d() > var12.d() ? var14 : var12).equals(new nQ(0.0, 0.0))) {
               var2 = var2.a(new nQ(var4, var4));
               var12 = new nQ(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            } else {
               var12 = var2;
               var2 = var2.d(var10.b(var13));
            }

            nD.this.k();
         }
      }

      // $VF: synthetic method
      a(byte var2) {
         this();
      }
   }
}
