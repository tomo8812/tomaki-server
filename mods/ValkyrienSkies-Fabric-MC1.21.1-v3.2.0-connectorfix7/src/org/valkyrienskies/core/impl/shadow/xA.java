package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;

public final class xA extends xp {
   private xu b;

   private xA(wK<wQ> var1) {
      super(var1);
   }

   private xA(double var1, double var3) {
      this(new wU(var1, var3));
   }

   @Override
   public final wQ c(wO... var1) {
      return super.c(var1);
   }

   private wQ b() {
      xA var3 = this;
      if (this.b == null) {
         throw new pl();
      }

      if (var3.f() == null && var3.g() == null) {
         kW var1 = new kW(this) {
            // $VF: synthetic field
            private xA a;

            {
               this.a = var1;
            }

            @Override
            public final double a(double[] var1) {
               return this.a.b(var1);
            }
         };
         boolean var2 = this.n() == xi.MINIMIZE;
         Comparator var9 = new Comparator<wQ>(this, var2) {
            // $VF: synthetic field
            private boolean a;
            // $VF: synthetic field
            private xA b;

            {
               this.b = var1;
               this.a = var2x;
            }

            private int a(wQ var1, wQ var2x) {
               double var3x = (Double)var1.c();
               double var5 = (Double)var2x.c();
               return this.a ? Double.compare(var3x, var5) : Double.compare(var5, var3x);
            }

            // $VF: synthetic method
            @Override
            public final int compare(Object var1, Object var2) {
               wQ var10001 = (wQ)var1;
               wQ var3 = var2;
               var2 = var10001;
               var var10 = this;
               double var6 = (Double)var2.c();
               double var8 = (Double)var3.c();
               return var10.a ? Double.compare(var6, var8) : Double.compare(var8, var6);
            }
         };
         xu var10000 = this.b;
         double[] var4 = this.e();
         xu var10 = var10000;
         if (var10000.c != var4.length) {
            throw new oS(var10.c, var4.length);
         }

         var10.a = new wQ[var10.c + 1];
         var10.a[0] = new wQ(var4, Double.NaN);

         for (int var5 = 0; var5 < var10.c; var5++) {
            double[] var6 = var10.b[var5];
            double[] var7 = new double[var10.c];

            for (int var8 = 0; var8 < var10.c; var8++) {
               var7[var8] = var4[var8] + var6[var8];
            }

            var10.a[var5 + 1] = new wQ(var7, Double.NaN);
         }

         this.b.a(var1, var9);
         wQ[] var11 = null;
         wK var12 = this.j();

         while (true) {
            if (this.i() > 0) {
               boolean var13 = true;
               int var14 = 0;

               while (true) {
                  xu var15 = this.b;
                  if (var14 >= this.b.a.length) {
                     if (var13) {
                        return this.b.a(0);
                     }
                     break;
                  }

                  wQ var16 = var11[var14];
                  var13 = var13 && var12.a(0, var16, this.b.a(var14));
                  var14++;
               }
            }

            var11 = this.b.c();
            this.b.a((kW)var1, (Comparator<wQ>)var9);
            this.l();
         }
      } else {
         throw new pb(px.CONSTRAINT);
      }
   }

   @Override
   public final void b(wO... var1) {
      super.b(var1);
      wO[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var5[var3]) instanceof xu) {
            this.b = (xu)var4;
            return;
         }
      }
   }

   private void d() {
      if (this.b == null) {
         throw new pl();
      } else if (this.f() != null || this.g() != null) {
         throw new pb(px.CONSTRAINT);
      }
   }

   // $VF: synthetic method
   @Override
   public final Object a(wO[] var1) {
      wO[] var2 = var1;
      return super.c(var2);
   }

   // $VF: synthetic method
   @Override
   public final Object c() {
      xA var1 = this;
      xA var4 = this;
      if (this.b == null) {
         throw new pl();
      }

      if (var4.f() == null && var4.g() == null) {
         kW var2 = new kW(var1) {
            // $VF: synthetic field
            private xA a;

            {
               this.a = var1;
            }

            @Override
            public final double a(double[] var1) {
               return this.a.b(var1);
            }
         };
         boolean var3 = var1.n() == xi.MINIMIZE;
         Comparator var10 = new Comparator<wQ>(var1, var3) {
            // $VF: synthetic field
            private boolean a;
            // $VF: synthetic field
            private xA b;

            {
               this.b = var1;
               this.a = var2x;
            }

            private int a(wQ var1, wQ var2x) {
               double var3x = (Double)var1.c();
               double var5 = (Double)var2x.c();
               return this.a ? Double.compare(var3x, var5) : Double.compare(var5, var3x);
            }

            // $VF: synthetic method
            @Override
            public final int compare(Object var1, Object var2) {
               wQ var10001 = (wQ)var1;
               wQ var3 = var2;
               var2 = var10001;
               var var10 = this;
               double var6 = (Double)var2.c();
               double var8 = (Double)var3.c();
               return var10.a ? Double.compare(var6, var8) : Double.compare(var8, var6);
            }
         };
         xu var10000 = var1.b;
         double[] var5 = var1.e();
         xu var11 = var10000;
         if (var10000.c != var5.length) {
            throw new oS(var11.c, var5.length);
         }

         var11.a = new wQ[var11.c + 1];
         var11.a[0] = new wQ(var5, Double.NaN);

         for (int var6 = 0; var6 < var11.c; var6++) {
            double[] var7 = var11.b[var6];
            double[] var8 = new double[var11.c];

            for (int var9 = 0; var9 < var11.c; var9++) {
               var8[var9] = var5[var9] + var7[var9];
            }

            var11.a[var6 + 1] = new wQ(var8, Double.NaN);
         }

         var1.b.a(var2, var10);
         wQ[] var12 = null;
         wK var13 = var1.j();

         while (true) {
            if (var1.i() > 0) {
               boolean var14 = true;
               int var15 = 0;

               while (true) {
                  xu var16 = var1.b;
                  if (var15 >= var1.b.a.length) {
                     if (var14) {
                        return var1.b.a(0);
                     }
                     break;
                  }

                  wQ var17 = var12[var15];
                  var14 = var14 && var13.a(0, var17, var1.b.a(var15));
                  var15++;
               }
            }

            var12 = var1.b.c();
            var1.b.a((kW)var2, (Comparator<wQ>)var10);
            var1.l();
         }
      } else {
         throw new pb(px.CONSTRAINT);
      }
   }
}
