package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;

@Deprecated
public final class yI extends yz<kW> implements yn {
   private yx b;

   @Deprecated
   public yI() {
      this(new yt());
   }

   private yI(ya<yp> var1) {
      super(var1);
   }

   private yI(double var1, double var3) {
      this(new yt(var1, var3));
   }

   @Deprecated
   private void a(yx var1) {
      this.a(var1);
   }

   @Override
   protected final yp a(int var1, kW var2, yf var3, yo... var4) {
      this.a(var4);
      return super.a(var1, var2, var3, var4);
   }

   private void a(yo... var1) {
      yo[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         yo var4;
         if ((var4 = var5[var3]) instanceof yx) {
            this.b = (yx)var4;
         }
      }
   }

   @Override
   protected final yp d() {
      if (this.b == null) {
         throw new pl();
      }

      kW var1 = new kW() {
         @Override
         public final double a(double[] var1) {
            return yI.this.a(var1);
         }
      };
      final boolean var2 = this.e() == yf.MINIMIZE;
      Comparator var9 = new Comparator<yp>() {
         private int a(yp var1, yp var2x) {
            double var3 = (Double)var1.c();
            double var5 = (Double)var2x.c();
            return var2 ? Double.compare(var3, var5) : Double.compare(var5, var3);
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2x) {
            yp var10001 = (yp)var1;
            yp var3 = var2x;
            var2x = var10001;
            var var10 = this;
            double var6 = (Double)var2x.c();
            double var8 = (Double)var3.c();
            return var2 ? Double.compare(var6, var8) : Double.compare(var8, var6);
         }
      };
      yx var10000 = this.b;
      double[] var4 = this.f();
      yx var3 = var10000;
      if (var10000.c != var4.length) {
         throw new oS(var3.c, var4.length);
      }

      var3.a = new yp[var3.c + 1];
      var3.a[0] = new yp(var4, Double.NaN);

      for (int var5 = 0; var5 < var3.c; var5++) {
         double[] var6 = var3.b[var5];
         double[] var7 = new double[var3.c];

         for (int var8 = 0; var8 < var3.c; var8++) {
            var7[var8] = var4[var8] + var6[var8];
         }

         var3.a[var5 + 1] = new yp(var7, Double.NaN);
      }

      this.b.a(var1, var9);
      yp[] var10 = null;
      int var11 = 0;
      ya var12 = this.c();

      while (true) {
         if (var11 > 0) {
            boolean var13 = true;
            int var14 = 0;

            while (true) {
               yx var15 = this.b;
               if (var14 >= this.b.a.length) {
                  if (var13) {
                     return this.b.a(0);
                  }
                  break;
               }

               yp var16 = var10[var14];
               var13 = var13 && var12.a(var11, var16, this.b.a(var14));
               var14++;
            }
         }

         var10 = this.b.c();
         this.b.a((kW)var1, (Comparator<yp>)var9);
         var11++;
      }
   }
}
